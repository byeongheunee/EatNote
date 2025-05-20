# -*-coding:utf-8-*-
import argparse
import subprocess
from models import *  # set ONNX_EXPORT in models.py
from utils.datasets import *
from utils.utils import *
from xml.etree.ElementTree import Element, SubElement, ElementTree
import numpy as np
import platform as pf
import psutil
import PIL
import pandas as pd
import seaborn as sns
import os

def indent(elem, level=0):  #
    i = "\n" + level * "  "
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = i + "  "
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
        for elem in elem:
            indent(elem, level + 1)
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i
    
def detect(save_img=True):
    imgsz = (320, 192) if ONNX_EXPORT else opt.img_size  # (320, 192) or (416, 256) or (608, 352) for (height, width)
    out, source, weights, half, view_img, save_txt, save_xml = opt.output, opt.source, opt.weights, opt.half, opt.view_img, opt.save_txt, opt.save_xml
    webcam = source == '0' or source.startswith('rtsp') or source.startswith('http') or source.endswith('.txt')

    # Initialize
    device = torch_utils.select_device(device='cpu' if ONNX_EXPORT else opt.device)
    if os.path.exists(out):
        shutil.rmtree(out)  # delete output folder
    os.makedirs(out)  # make new output folder

    # Initialize model
    model = Darknet(opt.cfg, imgsz)

    # Load weights
    attempt_download(weights)
    if weights.endswith('.pt'):  # pytorch format
        model.load_state_dict(torch.load(weights, map_location=device, weights_only=False)['model'], strict=False)
    else:  # darknet format
        load_darknet_weights(model, weights)

    # Second-stage classifier
    classify = False
    if classify:
        modelc = torch_utils.load_classifier(name='resnet101', n=2)  # initialize
        modelc.load_state_dict(torch.load('weights/resnet101.pt', map_location=device)['model'],
                               strict=False)  # load weights
        modelc.to(device).eval()

    # Eval mode
    model.to(device).eval()

    # Fuse Conv2d + BatchNorm2d layers
    # model.fuse()

    # Export mode
    if ONNX_EXPORT:
        model.fuse()
        img = torch.zeros((1, 3) + imgsz)  # (1, 3, 320, 192)
        f = opt.weights.replace(opt.weights.split('.')[-1], 'onnx')  # *.onnx filename
        torch.onnx.export(model, img, f, verbose=False, opset_version=11,
                          input_names=['images'], output_names=['classes', 'boxes'])

        # Validate exported model
        import onnx
        model = onnx.load(f)  # Load the ONNX model
        onnx.checker.check_model(model)  # Check that the IR is well formed
        print(onnx.helper.printable_graph(model.graph))  # Print a human readable representation of the graph
        return

    # Half precision
    half = half and device.type != 'cpu'  # half precision only supported on CUDA
    if half:
        model.half()

    # Set Dataloader
    vid_path, vid_writer = None, None
    if webcam:
        view_img = True
        torch.backends.cudnn.benchmark = True  # set True to speed up constant image size inference
        dataset = LoadStreams(source, img_size=imgsz)
    else:
        save_img = True
        dataset = LoadImages(source, img_size=imgsz)

    # Get names and colors
    names = load_classes(opt.names)
    colors = [[random.randint(0, 255) for _ in range(3)] for _ in range(len(names))]
    rslt = []
    nT = 0
    nF = 0
    nN = 0
    nND = 0
    # Run inference
    t0 = time.time()
    img = torch.zeros((1, 3, imgsz, imgsz), device=device)  # init img
    _ = model(img.half() if half else img.float()) if device.type != 'cpu' else None  # run once
    detected_food_codes = set()
    for path, img, im0s, vid_cap in dataset:
        img = torch.from_numpy(img).to(device)
        img = img.half() if half else img.float()  # uint8 to fp16/32
        img /= 255.0  # 0 - 255 to 0.0 - 1.0
        if img.ndimension() == 3:
            img = img.unsqueeze(0)

        # Inference
        t1 = torch_utils.time_synchronized()
        pred = model(img, augment=opt.augment)[0]
        t2 = torch_utils.time_synchronized()

        # to float
        if half:
            pred = pred.float()

        # Apply NMS
        pred = non_max_suppression(pred, opt.conf_thres, opt.iou_thres,
                                   multi_label=False, classes=opt.classes, agnostic=opt.agnostic_nms)

        # Apply Classifier
        if classify:
            pred = apply_classifier(pred, modelc, img, im0s)

        # Process detections
        for i, det in enumerate(pred):  # detections for image i
            if webcam:  # batch_size >= 1
                p, s, im0 = path[i], '%g: ' % i, im0s[i].copy()
            else:
                p, s, im0 = path, '', im0s

            save_path = str(Path(out) / Path(p).name)
            #s += '%gx%g ' % img.shape[2:]  # print string
            gn = torch.tensor(im0.shape)[[1, 0, 1, 0]]  #  normalization gain whwh

            root = Element('annotation')
            SubElement(root, 'folder').text = str(Path(out))
            SubElement(root, 'filename').text = str(Path(p))
            SubElement(root, 'path').text = save_path

            if det is not None and len(det):
                # Rescale boxes from imgsz to im0 size
                det[:, :4] = scale_coords(img.shape[2:], det[:, :4], im0.shape).round()
                count = 0

                # Print results
                for c in det[:, -1].unique():
                    n = (det[:, -1] == c).sum()  # detections per class
                    s += '%g %s, ' % (n, names[int(c)])  # add to string
                total = []
                object_names = []

                # Write results
                for idx, (*xyxy, conf, cls) in enumerate(reversed(det)):
                    food_code = names[int(cls)]
                    if food_code == "00000000": 
                        continue
                    detected_food_codes.add(food_code)
                    label = '%s %.2f' % (names[int(cls)], conf)

    
                    if save_txt:
                        xywh = (xyxy2xywh(torch.tensor(xyxy).view(1, 4)) / gn).view(-1).tolist()
                        with open(save_path[:save_path.rfind('.')] + '.txt', 'a') as file:
                            file.write(('%g ' * 5 + '\n') % (cls, *xywh))
                    #if save_xml:
                    semi = []
                    for nums in range(4):  ## total??좌표 ?�??
                        str_x = str(xyxy[nums]).split('(')
                        str_x = str_x[1].split('.')
                        semi.append(str_x[0])
                    total.append(semi)
                    object_names.append(names[int(cls)])
                    count = count + 1
                  
                   
                    if save_img or view_img:  # Add bbox to image
                        plot_one_box(xyxy, im0, label=label, color=colors[int(cls)])
                        
                        # 객체 잘라서 저장 (crop)
                        x1, y1, x2, y2 = map(int, xyxy)  # 좌표 정수화
                        crop_img = im0[y1:y2, x1:x2]  # 객체 크롭
                        os.makedirs('./image/test_crops', exist_ok=True)

                        food_code = names[int(cls)]  # ← 클래스 이름을 foodCode로 가져오기
                        crop_save_path = os.path.join('./image/test_crops', f'{Path(p).stem}_{idx}_{food_code}.jpg')
                        cv2.imwrite(crop_save_path, crop_img)

                    for i in range(count):  ##리스트 두 개 xml파일에 저장
                        object_xml = SubElement(root, 'object')
                        SubElement(object_xml, 'name').text = object_names[i]
                        bndbox = SubElement(object_xml, 'bndbox')
                        SubElement(bndbox, 'xmin').text = str(total[i][0])
                        SubElement(bndbox, 'ymin').text = str(total[i][1])
                        SubElement(bndbox, 'xmax').text = str(total[i][2])
                        SubElement(bndbox, 'ymax').text = str(total[i][3])
                        
                if save_xml:
                    indent(root)
                    tree = ElementTree(root)
                    tree.write(save_path[:save_path.rfind('.')] + '.xml', encoding='utf-8',
                               xml_declaration=True)  ##아웃풋 폴더에 jpg와 xml 생성

            # Print time (inference + NMS)
            print('%s (%.3fs)' % (s, t2 - t1))

            # Stream results
            if view_img:
                cv2.imshow(p, im0)
                if cv2.waitKey(1) == ord('q'):  # q to quit
                    raise StopIteration

    if save_txt or save_img:
        print('Results saved to %s' % os.getcwd() + os.sep + out)
        if platform == 'darwin':  # MacOS
            os.system('open ' + save_path)

    print('Done. (%.3fs)' % (time.time() - t0))
    with open('./classificaion_result.txt','w') as f:
        rslt = [r + '\n' for r in rslt]
        f.writelines(rslt)
    return detected_food_codes

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--cfg', type=str, default='cfg/yolov3-spp.cfg', help='*.cfg path')
    parser.add_argument('--names', type=str, default='data/food.names', help='*.names path')
    parser.add_argument('--weights', type=str, default='weights/best.pt', help='weights path')
    parser.add_argument('--source', type=str, default='data/samples', help='source')  # input file/folder, 0 for webcam
    parser.add_argument('--output', type=str, default='output', help='output folder')  # output folder
    parser.add_argument('--img-size', type=int, default=320, help='inference size (pixels)')
    parser.add_argument('--conf-thres', type=float, default=0.3, help='object confidence threshold')
    parser.add_argument('--iou-thres', type=float, default=0.5, help='IOU threshold for NMS')
    parser.add_argument('--fourcc', type=str, default='mp4v', help='output video codec (verify ffmpeg support)')
    parser.add_argument('--half', action='store_true', help='half precision FP16 inference')
    parser.add_argument('--device', default='', help='device id (i.e. 0 or 0,1) or cpu')
    parser.add_argument('--view-img', action='store_true', help='display results')
    parser.add_argument('--save-txt', action='store_true', help='save results to *.txt')
    parser.add_argument('--classes', nargs='+', type=int, help='filter by class')
    parser.add_argument('--agnostic-nms', action='store_true', help='class-agnostic NMS')
    parser.add_argument('--augment', action='store_true', help='augmented inference')
    parser.add_argument('--save-xml', action='store_true', help='save results to *.xml')
    opt = parser.parse_args()
    opt.cfg = check_file(opt.cfg)  # check file
    opt.names = check_file(opt.names)  # check file

    with torch.no_grad():
        print('Session START :', time.strftime('%Y-%m-%d %Z %H:%M:%S', time.localtime(time.time())))
        print('command : python3 detect_1231.py --cfg {0} --names {1} --weights {2}'.format(opt.cfg, opt.names, opt.weights))
        print('===============================================================')
        #print(d.isoformat())
        detected_food_codes = detect()
        def printOsInfo():
            #print('GPU                  :\t', torch.cuda.get_device_name(0)) 
            print('OS                   :\t', pf.system())
            #  print('OS Version           :\t', platform.version())

        if __name__ == '__main__':
            printOsInfo()

        def printSystemInfor():
            print('Process information  :\t', pf.processor())
            print('Process Architecture :\t', pf.machine())
            print('RAM Size             :\t',str(round(psutil.virtual_memory().total / (1024.0 **3)))+"(GB)")
            print('===============================================================')
          
        if __name__ == '__main__':
            printSystemInfor()  

        print('Pytorch')
        print('torch ' + torch.__version__)
        print('numpy ' + np.__version__)
        print('torchvision ' + torch.__version__)
        print('matplotlib ' + matplotlib.__version__)
        print('pillow ' + PIL.__version__)
        print('pandas ' + pd.__version__)
        print('seaborn ' + sns.__version__)   
        print('psutil ' + psutil.__version__) 
        print('===============================================================')
        print("== YOLO 탐지 완료 ==")
        if detected_food_codes:
            print("감지된 음식 코드들:")
            for code in detected_food_codes:
                print(code)
        else:
            print("감지된 음식 없음.")

        # # XML 결과가 생성된 후 ResNet 양 추정 실행
        # print("== ResNet 양 추정 시작 ==")
        # subprocess.run(["python3", "resnet_test_t.py"], check=True)
