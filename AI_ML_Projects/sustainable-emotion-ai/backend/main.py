import cv2
import numpy as np
from fastapi import FastAPI, File, UploadFile
from pydantic import BaseModel
from typing import List
import psutil
import time
from deepface import DeepFace

app = FastAPI()

class BoundingBox(BaseModel):
    x: int
    y: int
    width: int
    height: int

class EmotionDistribution(BaseModel):
    angry: float
    disgust: float
    fear: float
    happy: float
    sad: float
    surprise: float
    neutral: float

class FaceDetection(BaseModel):
    bounding_box: BoundingBox
    emotion: str
    confidence: float
    emotion_distribution: EmotionDistribution

class EmotionResult(BaseModel):
    faces: List[FaceDetection]
    processing_time_ms: int
    cpu_usage: float
    memory_usage: float
    energy_consumption: float

def get_energy_consumption():
    return psutil.cpu_percent(interval=0.1) * 0.035

@app.post("/api/detect-emotion", response_model=EmotionResult)
async def detect_emotion(image: UploadFile = File(...)):
    start_time = time.time()
    image_bytes = await image.read()
    arr = np.frombuffer(image_bytes, np.uint8)
    img = cv2.imdecode(arr, cv2.IMREAD_COLOR)

    detections = DeepFace.analyze(img, actions=['emotion'], enforce_detection=False)
    if isinstance(detections, dict): detections = [detections]

    faces = []
    for det in detections:
        region = det.get('region', det.get('facial_area'))
        if region:
            dist = EmotionDistribution(
                angry=det['emotion']['angry']/100.0,
                disgust=det['emotion']['disgust']/100.0,
                fear=det['emotion']['fear']/100.0,
                happy=det['emotion']['happy']/100.0,
                sad=det['emotion']['sad']/100.0,
                surprise=det['emotion']['surprise']/100.0,
                neutral=det['emotion']['neutral']/100.0
            )
            faces.append(FaceDetection(
                bounding_box=BoundingBox(
                    x=region['x'], y=region['y'],
                    width=region['w'], height=region['h']
                ),
                emotion=det['dominant_emotion'],
                confidence=det['emotion'][det['dominant_emotion']]/100.0,
                emotion_distribution=dist
            ))

    proc_time = int((time.time() - start_time)*1000)
    cpu = psutil.cpu_percent(interval=0.05)
    mem = psutil.virtual_memory().percent
    energy = get_energy_consumption()

    return EmotionResult(
        faces=faces,
        processing_time_ms=proc_time,
        cpu_usage=cpu,
        memory_usage=mem,
        energy_consumption=energy
    )
