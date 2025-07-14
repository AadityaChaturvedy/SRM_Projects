import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StatisticsTableFactory {
    public static TableView<EmotionEntry> makeStatisticsTable() {
        TableView<EmotionEntry> table = new TableView<>();

        TableColumn<EmotionEntry, String> emotionCol = new TableColumn<>("Emotion");
        emotionCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmotion()));
        emotionCol.setPrefWidth(90);

        TableColumn<EmotionEntry, String> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCount()));
        countCol.setPrefWidth(65);

        TableColumn<EmotionEntry, String> avgConfCol = new TableColumn<>("Avg Confidence");
        avgConfCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAvgConfidence()));
        avgConfCol.setPrefWidth(120);

        TableColumn<EmotionEntry, String> lastDetCol = new TableColumn<>("Last Detected");
        lastDetCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLastDetected()));
        lastDetCol.setPrefWidth(95);

        table.getColumns().addAll(emotionCol, countCol, avgConfCol, lastDetCol);
        return table;
    }
}
