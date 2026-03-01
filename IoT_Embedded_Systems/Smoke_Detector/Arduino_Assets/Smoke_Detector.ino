#define MQ5_PIN A0
#define GREEN_LED 6
#define RED_LED 7
#define BUZZER 8

int gasThreshold = 300; // Adjust this threshold based on sensor calibration

void setup() {
    Serial.begin(9600);
    pinMode(GREEN_LED, OUTPUT);
    pinMode(RED_LED, OUTPUT);
    pinMode(BUZZER, OUTPUT);
    digitalWrite(GREEN_LED, HIGH); // Green LED on by default (safe state)
    digitalWrite(RED_LED, LOW);
    digitalWrite(BUZZER, LOW);
}

void loop() {
    int gasValue = analogRead(MQ5_PIN);
    Serial.print("Gas Level: ");
    Serial.println(gasValue);

    if (gasValue > gasThreshold) {
        // Gas detected - Alert mode
        digitalWrite(RED_LED, HIGH);
        digitalWrite(GREEN_LED, LOW);
        digitalWrite(BUZZER, HIGH);
        Serial.println("⚠️ Gas Detected! Take Action.");
    } else {
        // No gas detected - Safe mode
        digitalWrite(GREEN_LED, HIGH);
        digitalWrite(RED_LED, LOW);
        digitalWrite(BUZZER, LOW);
        Serial.println("✅ No Gas Detected.");
    }

    delay(1000); // Wait for a second before next reading
}
