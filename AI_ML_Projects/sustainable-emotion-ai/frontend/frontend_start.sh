set PATH_TO_FX=%cd%\javafx-sdk-17.0.16\lib

javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp ".;lib/*" *.java

java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp ".;lib/*" EmotionRecognitionApp