from flask import Flask, request, jsonify
import subprocess
import os
import uuid
import json
import sys

app = Flask(__name__)
UPLOAD_DIR = os.path.join(os.path.dirname(__file__), "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)
PYTHON_CMD = "python3" if sys.platform != "win32" else "python"

def run_yolo_and_resnet(image_path):
    try:
        subprocess.run([
            PYTHON_CMD, "detect_eatNote.py",
            "--source", image_path,
            "--save-xml"
        ], check=True)

        subprocess.run([
            PYTHON_CMD, "resnet_test_t.py"
        ], check=True)
    except subprocess.CalledProcessError as e:
        raise RuntimeError("YOLO 또는 ResNet 실행 중 오류 발생") from e

@app.route("/analyze", methods=["POST"])
def analyze():
    try:
        file = request.files['file']  # ✅ multipart/form-data로 전송되는 이미지 파일
        filename = str(uuid.uuid4()) + "_" + file.filename
        image_path = os.path.join(UPLOAD_DIR, filename)
        file.save(image_path)

        print("[✅ 저장된 이미지 경로]", image_path)

        run_yolo_and_resnet(image_path)

        with open("results.json") as f:
            results = json.load(f)

        return jsonify({
            "status": "success",
            "results": results
        })

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

if __name__ == "__main__":
    app.run(debug=True, port=5000)
