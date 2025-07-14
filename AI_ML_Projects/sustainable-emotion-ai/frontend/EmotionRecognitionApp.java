import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmotionRecognitionApp extends Application {
    private ImageView cameraView;
    private Label emotionLabel, statsLabel, statusLabel, energyVal, cpuLabel, memLabel, latencyLabel;
    private ProgressBar fpsBar, energyBar;
    private TableView<EmotionEntry> statisticsTable;
    private EmotionAPIClient apiClient;
    private VideoCapture camera;
    private boolean running;

    // For statistics summary
    private final Map<String, Integer> emotionCount = new HashMap<>();
    private final Map<String, Double> emotionSumConfidence = new HashMap<>();
    private final Map<String, String> emotionTime = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        header.setPadding(new Insets(6, 12, 6, 16));
        header.setBackground(new Background(new BackgroundFill(Color.web("#194463"), CornerRadii.EMPTY, Insets.EMPTY)));
        Label title = new Label("Sustainable AI Emotion Recognition");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        Region headSpacer = new Region();
        HBox.setHgrow(headSpacer, Priority.ALWAYS);
        statusLabel = new Label("Camera Idle");
        statusLabel.setTextFill(Color.LIMEGREEN);
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        statusLabel.setPadding(new Insets(0,18,0,0));
        header.getChildren().addAll(title, headSpacer, statusLabel);

        // Webcam
        cameraView = new ImageView();
        cameraView.setFitWidth(600);
        cameraView.setFitHeight(410);
        cameraView.setPreserveRatio(true);
        VBox webcamCard = new VBox(8, new Label("Live Camera Feed"), cameraView);
        webcamCard.setStyle("-fx-background-color:#fff;-fx-background-radius:8;");
        webcamCard.setPrefWidth(640);
        webcamCard.setPadding(new Insets(14,12,8,12));
        VBox.setVgrow(cameraView, Priority.ALWAYS);

        // FPS Bar
        fpsBar = new ProgressBar(0);
        fpsBar.setPrefWidth(104);
        statsLabel = new Label("FPS: 0");
        statsLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL,12));
        HBox fpsArea = new HBox(8, statsLabel, fpsBar);
        fpsArea.setAlignment(Pos.CENTER_LEFT);

        // Start/Stop row
        Button startBtn = new Button("▶ Start Camera");
        Button stopBtn = new Button("■ Stop Camera");
        startBtn.setStyle("-fx-background-color:#2ecc71; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius: 5;");
        stopBtn.setStyle("-fx-background-color:#e74c3c; -fx-text-fill:white; -fx-font-weight: bold; -fx-background-radius: 5;");
        startBtn.setPrefWidth(140); stopBtn.setPrefWidth(140);
        startBtn.setDisable(false); stopBtn.setDisable(true);
        startBtn.setOnAction(e -> { if (!running) { running=true; startBtn.setDisable(true); stopBtn.setDisable(false); statusLabel.setText("Camera Active"); startCamera();}});
        stopBtn.setOnAction(e -> { if (running) { running=false; stopBtn.setDisable(true); startBtn.setDisable(false); statusLabel.setText("Camera Idle"); stopCamera();}});
        HBox btnRow = new HBox(14, startBtn, stopBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        // Current Emotion
        Label currEmotion = new Label("Current Emotion");
        currEmotion.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        emotionLabel = new Label("NEUTRAL");
        emotionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        emotionLabel.setTextFill(Color.web("#888"));
        VBox currEmotionBox = new VBox(4, currEmotion, emotionLabel);
        currEmotionBox.setAlignment(Pos.CENTER_LEFT);
        currEmotionBox.setStyle("-fx-background-color:#EDF2F7; -fx-background-radius:7;");
        currEmotionBox.setPadding(new Insets(10,8,10,8));
        currEmotionBox.setPrefWidth(300);

        VBox leftCol = new VBox(webcamCard, fpsArea, btnRow, currEmotionBox);
        leftCol.setSpacing(10);
        leftCol.setAlignment(Pos.TOP_LEFT);
        leftCol.setPadding(new Insets(14,8,0,10));
        leftCol.setPrefWidth(685);

        // Stats Table
        statisticsTable = StatisticsTableFactory.makeStatisticsTable();
        statisticsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        statisticsTable.setPrefHeight(270);
        Label statHead = new Label("Emotion Statistics");
        statHead.setFont(Font.font("Segoe UI",FontWeight.SEMI_BOLD,15));
        Button clearBtn = new Button("Clear Statistics");
        clearBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:4;");
        clearBtn.setOnAction(e -> { emotionCount.clear(); emotionSumConfidence.clear(); emotionTime.clear(); statisticsTable.getItems().clear(); });
        VBox statBox = new VBox(statHead, statisticsTable, clearBtn);
        statBox.setAlignment(Pos.TOP_LEFT);
        statBox.setStyle("-fx-background-color:#fff;-fx-background-radius:8;");
        statBox.setPadding(new Insets(10,10,10,10));

        // Performance metrics summary - bottom right card
        cpuLabel = new Label("CPU Usage: 0%");
        memLabel = new Label("Memory Usage: 0 MB");
        latencyLabel = new Label("Processing Latency: 0 ms");

        VBox perfBox = new VBox(3, new Label("Performance Metrics"), cpuLabel, memLabel, latencyLabel);
        perfBox.setAlignment(Pos.TOP_LEFT);
        perfBox.setStyle("-fx-background-color:#f2f7fa;-fx-background-radius:5;");
        perfBox.setPadding(new Insets(8,8,8,8));
        perfBox.setPrefWidth(270);

        VBox rightCol = new VBox(statBox, perfBox);
        rightCol.setSpacing(12);
        rightCol.setAlignment(Pos.TOP_CENTER);
        rightCol.setPadding(new Insets(24,14,6,8));
        rightCol.setPrefWidth(390);

        HBox mainRow = new HBox(leftCol, rightCol);
        mainRow.setSpacing(10);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        mainRow.setPadding(new Insets(0,0,0,0));
        VBox.setVgrow(mainRow, Priority.ALWAYS);

        // Footer with responsive resizing
        energyBar = new ProgressBar(0);
        energyBar.setPrefWidth(200);
        energyVal = new Label("0W");
        energyVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        energyVal.setTextFill(Color.WHITE);

        Label sust = new Label("\u267B Sustainable AI Computing");
        sust.setFont(Font.font("Segoe UI",FontWeight.BOLD,12));
        sust.setTextFill(Color.web("#16a34a"));
        Label energyLabel = new Label("Energy Consumption:");
        energyLabel.setFont(Font.font("System", FontWeight.BOLD, 12)); // Adjust size as needed
        energyLabel.setTextFill(Color.WHITE);

        HBox bottomBar = new HBox(12, energyLabel, energyBar, energyVal, new Region(), sust);
        HBox.setHgrow(bottomBar.getChildren().get(3), Priority.ALWAYS);
        bottomBar.setPadding(new Insets(4,18,3,15));
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setStyle("-fx-background-color:#1c4661;");
        bottomBar.setMaxWidth(Double.MAX_VALUE);

        VBox appFull = new VBox(header, mainRow, bottomBar);
        appFull.setStyle("-fx-background-color:#f6fafc;");
        VBox.setVgrow(mainRow, Priority.ALWAYS);

        Scene scene = new Scene(appFull, 1140, 715);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sustainable AI Emotion Recognition");
        primaryStage.show();
        apiClient = new EmotionAPIClient("http://localhost:8000/api");
    }

    private void startCamera() {
        camera = new VideoCapture(0);
        running = true;
        new Thread(() -> {
            Mat frame = new Mat();
            long lastTime  = System.currentTimeMillis();
            int frameCount = 0;
            while (running) {
                if (camera.read(frame)) {
                    processFrame(frame);
                    frameCount++;
                    long now = System.currentTimeMillis();
                    if (now - lastTime > 1000) {
                        int fps = frameCount;
                        Platform.runLater(() -> statsLabel.setText("FPS: " + fps));
                        frameCount = 0;
                        lastTime = now;
                    }
                }
            }
            camera.release();
        }).start();
    }

    private void stopCamera() {
        running = false;
        if (camera != null && camera.isOpened()) camera.release();
    }

    private void processFrame(Mat frame) {
        try {
            MatOfByte mob = new MatOfByte();
            Imgcodecs.imencode(".jpg", frame, mob);
            byte[] bytes = mob.toArray();

            EmotionResult result = apiClient.detectEmotion(bytes);

            if (result != null && result.faces != null && result.faces.size() > 0) {
                // Update stats for all faces
                for (FaceDetection face : result.faces) {
                    String emotion = face.getEmotion();
                    double conf = face.getConfidence();
                    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    emotionCount.put(emotion, emotionCount.getOrDefault(emotion, 0) + 1);
                    emotionSumConfidence.put(emotion, emotionSumConfidence.getOrDefault(emotion, 0.0) + conf);
                    emotionTime.put(emotion, time);

                    BoundingBox box = face.getBoundingBox();
                    if (box != null) {
                        Scalar color = getColorForEmotion(emotion);
                        Imgproc.rectangle(frame,
                                new Point(box.getX(), box.getY()),
                                new Point(box.getX() + box.getWidth(), box.getY() + box.getHeight()),
                                color, 3);

                        String label = emotion.toUpperCase() + String.format(" (%.0f%%)", conf * 100);
                        Imgproc.putText(frame, label,
                                new Point(box.getX(), Math.max(box.getY() - 10, 0)),
                                Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, color, 2);
                    }
                }
                FaceDetection strongest = result.faces.get(0);
                Platform.runLater(() -> emotionLabel.setText(strongest.getEmotion().toUpperCase()));
            } else {
                Platform.runLater(() -> emotionLabel.setText("NO FACE"));
            }

            // Update statistics table
            Platform.runLater(() -> {
                statisticsTable.getItems().clear();
                for (String emotion : emotionCount.keySet()) {
                    int count = emotionCount.getOrDefault(emotion, 0);
                    double avgConf = (count == 0) ? 0.0 : (emotionSumConfidence.get(emotion)/count)*100.0;
                    String lastDet = emotionTime.getOrDefault(emotion, "--");
                    statisticsTable.getItems().add(new EmotionEntry(
                        emotion,
                        String.valueOf(count),
                        String.format("%.2f%%", avgConf),
                        lastDet
                    ));
                }
            });

            // Update performance metrics and power/energy
            if (result != null) {
                Platform.runLater(() -> {
                    cpuLabel.setText("CPU Usage: " + Math.round(result.cpu_usage) + "%");
                    memLabel.setText("Memory Usage: " + Math.round(result.memory_usage) + " MB");
                    latencyLabel.setText("Processing Latency: " + result.processing_time_ms + " ms");
                    double maxEnergy = 20.0; // arbitrary upper bar (can calibrate more)
                    double percent = Math.min(1.0, result.energy_consumption/maxEnergy);
                    energyBar.setProgress(percent);
                    energyVal.setText(String.format("%.2fW", result.energy_consumption));
                });
            }

            Image fxImage = matToImage(frame);
            Platform.runLater(() -> cameraView.setImage(fxImage));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Scalar getColorForEmotion(String emotion) {
        switch (emotion.toLowerCase()) {
            case "happy": return new Scalar(40, 220, 80);
            case "sad": return new Scalar(60, 100, 255);
            case "angry": return new Scalar(50, 50, 220);
            case "surprised": return new Scalar(255, 205, 70);
            case "disgust": return new Scalar(120, 255, 140);
            case "neutral": return new Scalar(120, 120, 120);
            case "fear": return new Scalar(130, 40, 210);
            default: return new Scalar(180, 180, 180);
        }
    }

    private Image matToImage(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadLocally();
        launch(args);
    }
}

