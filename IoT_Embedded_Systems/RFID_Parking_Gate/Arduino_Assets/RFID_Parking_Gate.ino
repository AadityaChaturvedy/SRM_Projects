#include <SPI.h>
#include <MFRC522.h>
#include <Servo.h>

#define SS_PIN 10
#define RST_PIN 9
#define SERVO_PIN 6
#define GREEN_LED 7
#define RED_LED 8

MFRC522 rfid(SS_PIN, RST_PIN);
Servo doorServo;

// Permitted and Denied Card UIDs
byte permittedUID[] = {0x13, 0xD3, 0x09, 0x27};
byte deniedUID[] = {0x11, 0x16, 0xF5, 0x7B};

void setup() {
    Serial.begin(9600);
    SPI.begin();
    rfid.PCD_Init();
    doorServo.attach(SERVO_PIN);
    pinMode(GREEN_LED, OUTPUT);
    pinMode(RED_LED, OUTPUT);
    digitalWrite(GREEN_LED, LOW);
    digitalWrite(RED_LED, LOW);
    doorServo.write(0); // Initial position
    Serial.println("Scan RFID Card...");
}

void loop() {
    if (!rfid.PICC_IsNewCardPresent() || !rfid.PICC_ReadCardSerial()) {
        return;
    }

    Serial.print("Card UID: ");
    bool isPermitted = true;

    for (byte i = 0; i < rfid.uid.size; i++) {
        Serial.print(rfid.uid.uidByte[i] < 0x10 ? "0" : "");
        Serial.print(rfid.uid.uidByte[i], HEX);
        Serial.print(" ");

        if (rfid.uid.uidByte[i] != permittedUID[i]) {
            isPermitted = false;
        }
    }
    Serial.println();

    if (isPermitted) {
        Serial.println("Access Granted!");
        digitalWrite(GREEN_LED, HIGH);
        digitalWrite(RED_LED, LOW);
        doorServo.write(90);  // Open Door
        delay(5000); // Keep it open for 5 seconds
        doorServo.write(0);   // Close Door
        digitalWrite(GREEN_LED, LOW);
    } else {
        Serial.println("Access Denied!");
        digitalWrite(RED_LED, HIGH);
        digitalWrite(GREEN_LED, LOW);
        delay(2000);
        digitalWrite(RED_LED, LOW);
    }

    rfid.PICC_HaltA();
    rfid.PCD_StopCrypto1();
}
