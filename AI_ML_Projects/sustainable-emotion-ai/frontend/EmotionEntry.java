import javafx.beans.property.SimpleStringProperty;

public class EmotionEntry {
    private final SimpleStringProperty emotion;
    private final SimpleStringProperty count;
    private final SimpleStringProperty avgConfidence;
    private final SimpleStringProperty lastDetected;

    public EmotionEntry(String emotion, String count, String avgConfidence, String lastDetected) {
        this.emotion = new SimpleStringProperty(emotion);
        this.count = new SimpleStringProperty(count);
        this.avgConfidence = new SimpleStringProperty(avgConfidence);
        this.lastDetected = new SimpleStringProperty(lastDetected);
    }

    public String getEmotion() { return emotion.get(); }
    public String getCount() { return count.get(); }
    public String getAvgConfidence() { return avgConfidence.get(); }
    public String getLastDetected() { return lastDetected.get(); }
}
