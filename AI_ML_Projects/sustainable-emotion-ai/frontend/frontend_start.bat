REM Set the JavaFX path
set PATH_TO_FX=%cd%\javafx-sdk-17.0.16\lib

REM Compile Java files
javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp ".;lib/*" *.java

REM Run the application
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp ".;lib/*" EmotionRecognitionApp
