// Pin Definitions
const int rainSensorPin = A0;   // Analog pin connected to Rain Sensor
const int redLedPin = 7;
const int greenLedPin = 6;
const int buzzerPin = 8;

// Threshold value to detect rain (adjust if needed)
const int rainThreshold = 500;  // Lower value means more rain

void setup() {
  pinMode(redLedPin, OUTPUT);
  pinMode(greenLedPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);

  Serial.begin(9600);  // For monitoring sensor values
}

void loop() {
  int rainValue = analogRead(rainSensorPin);

  Serial.print("Rain Sensor Value: ");
  Serial.println(rainValue);

  if (rainValue < rainThreshold) {
    // Rain detected
    digitalWrite(redLedPin, HIGH);
    digitalWrite(greenLedPin, LOW);
    digitalWrite(buzzerPin, HIGH);
  } else {
    // No rain
    digitalWrite(redLedPin, LOW);
    digitalWrite(greenLedPin, HIGH);
    digitalWrite(buzzerPin, LOW);
  }

  delay(500); // Read every half second
}