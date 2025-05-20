from __future__ import print_function, division
import torch.serialization
import torchvision.models.resnet
import torch.nn.modules
import os
import glob
import json

print("🟡 [DEBUG] ResNet 시작 전 crop 파일 확인...")
image_files = glob.glob("image/test_crops/*.jpg")
print("🟢 현재 crop 이미지 수:", len(image_files))
for path in image_files:
    print("📦", path)


torch.serialization.add_safe_globals([
    # ResNet 관련 클래스
    torchvision.models.resnet.ResNet,
    torchvision.models.resnet.BasicBlock,

    # Torch layer types
    torch.nn.modules.container.Sequential,
    torch.nn.modules.conv.Conv2d,
    torch.nn.modules.linear.Linear,
    torch.nn.modules.batchnorm.BatchNorm2d,
    torch.nn.modules.activation.ReLU,
    torch.nn.modules.activation.Softmax,
    torch.nn.modules.activation.LogSoftmax,
    torch.nn.modules.dropout.Dropout,
    torch.nn.modules.pooling.AdaptiveAvgPool2d,
    torch.nn.modules.pooling.MaxPool2d,
    torch.nn.modules.pooling.AvgPool2d,
    torch.nn.modules.flatten.Flatten,
    torch.nn.modules.module.Module,

    # 일반적인 타입
    torch.Tensor,
])



import os
import glob
import platform, psutil
import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib
import matplotlib.pyplot as plt
from PIL import Image
from datetime import date
import datetime, time
import torch
import torch.nn.functional as F
from torch.autograd import Variable
from torchvision import transforms
from sklearn.metrics import confusion_matrix, classification_report

print(os.getcwd())
print('Session START :', time.strftime('%Y-%m-%d %Z %H:%M:%S', time.localtime(time.time())))
print('===============================================================')

def printOsInfo():
    print('OS                   :\t', platform.system())

def printSystemInfor():
    print('Process information  :\t', platform.processor())
    print('Process Architecture :\t', platform.machine())
    print('RAM Size             :\t', str(round(psutil.virtual_memory().total / (1024.0 **3))) + "(GB)")
    print('===============================================================')

printOsInfo()
printSystemInfor()
print('Pytorch')
print('torch ' + torch.__version__)
print('numpy ' + np.__version__)
print('torchvision ' + torch.__version__)
print('matplotlib ' + matplotlib.__version__)
print('pillow ' + Image.__version__)
print('pandas ' + pd.__version__)
print('seaborn ' + sns.__version__)
print('psutil ' + psutil.__version__)
print('===============================================================')

image_folder = './image/test_crops'
image_files = glob.glob(os.path.join(image_folder, '*.jpg'))

# 모델 로딩 함수
def load_checkpoint(filepath, map_location='cpu'):
    checkpoint = torch.load(filepath, map_location=map_location)
    model = checkpoint['model_ft']
    model.load_state_dict(checkpoint['state_dict'], strict=False)
    model.class_to_idx = checkpoint['class_to_idx']
    for param in model.parameters():
        param.requires_grad = False
    return model, checkpoint['class_to_idx']

model, class_to_idx = load_checkpoint("./weights/resnet_quantity.pth")
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")

# 이미지 전처리 함수
def process_image(image):
    preprocess = transforms.Compose([
        transforms.Resize(256),
        transforms.CenterCrop(224),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406],
                             std=[0.229, 0.224, 0.225])
    ])
    return preprocess(image).unsqueeze(0)

# 예측 함수
def predict_image(image_path, model):
    image = Image.open(image_path).convert("RGB")
    img_tensor = process_image(image).to(device)
    model.eval()
    with torch.no_grad():
        outputs = model(img_tensor)
        probabilities = F.softmax(outputs, dim=1)
        top_prob, top_class = probabilities.topk(1, dim=1)
    return top_class.item(), top_prob.item()

# 클래스 레이블 매핑
idx_to_class = {v: k for k, v in class_to_idx.items()}

# 결과 저장
results = []
for img_path in image_files:
    pred_class, prob = predict_image(img_path, model.to(device))
    results.append({
        'filename': os.path.basename(img_path),
        'predicted_class': idx_to_class[pred_class],
        'confidence': round(prob, 4)
    })

# 출력
print("===============================================================")
print("양 추정 결과")
for r in results:
    print(f"{r['filename']} -> {r['predicted_class']} ({r['confidence']})")

# 분석 완료 후 crop 이미지 삭제
crop_folder = "./image/test_crops"
crop_images = glob.glob(os.path.join(crop_folder, "*.jpg"))

for img_path in crop_images:
    try:
        os.remove(img_path)
    except Exception as e:
        print(f"❌ {img_path} 삭제 실패: {e}")

print("✅ 분석 완료 후 crop 이미지 삭제됨.")

json_results = []
for r in results:
    food_code = r['filename'].split('_')[-1].split('.')[0]
    json_results.append({
        "foodCode": food_code,
        "quantity": r['predicted_class'],
        "confidence": r['confidence']
    })

# 저장
with open("results.json", "w") as f:
    json.dump(json_results, f)
