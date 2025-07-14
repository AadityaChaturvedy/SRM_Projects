public class FaceDetection {
    public BoundingBox bounding_box;
    public String emotion;
    public double confidence;
    public EmotionDistribution emotion_distribution;

    public BoundingBox getBoundingBox() { return bounding_box; }
    public String getEmotion() { return emotion; }
    public double getConfidence() { return confidence; }
}
