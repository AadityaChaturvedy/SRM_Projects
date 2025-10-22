#include <SPI.h>
#include <MFRC522.h>

#define SS_PIN 10
#define RST_PIN 9
MFRC522 rfid(SS_PIN, RST_PIN);

#define RED_LED 6   // Red LED for Access Denied
#define GREEN_LED 7 // Green LED for Access Granted

struct Student {
    byte uid[4];  
    const char* name;
    const char* regNo;
    const char* section;
    const char* access;
};

// Student Data
Student students[] = {
    {{0x23, 0xFD, 0x10, 0x27}, "Student-1", "306", "CSE-E1", "Access Granted"},
    {{0xE1, 0x1D, 0xA7, 0x7B}, "Student-2", "407", "DSBS-A", "Access Denied"},
};

void setup() {
    Serial.begin(9600);
    SPI.begin();
    rfid.PCD_Init();

    pinMode(RED_LED, OUTPUT);
    pinMode(GREEN_LED, OUTPUT);

    digitalWrite(RED_LED, LOW);
    digitalWrite(GREEN_LED, LOW);

    // Table Header
    Serial.println("\n--------------------------------------------");
    Serial.println("|   Name   | Reg No | Section  |   Status   |");
    Serial.println("--------------------------------------------");
}

void loop() {
    if (!rfid.PICC_IsNewCardPresent() || !rfid.PICC_ReadCardSerial()) {
        return;
    }

    bool found = false;
    for (Student student : students) {
        if (memcmp(student.uid, rfid.uid.uidByte, 4) == 0) {
            
            // Print in Table Format
            Serial.print("| ");
            Serial.print(student.name);
            Serial.print(" | ");
            Serial.print(student.regNo);
            Serial.print("  | ");
            Serial.print(student.section);
            Serial.print(" | ");
            Serial.print(student.access);
            Serial.println(" |");

            // Excel Data Streamer Output
            Serial.print(student.name); Serial.print(",");
            Serial.print(student.regNo); Serial.print(",");
            Serial.print(student.section); Serial.print(",");
            Serial.println(student.access);

            if (strcmp(student.access, "Access Granted") == 0) {
                digitalWrite(GREEN_LED, HIGH);
                digitalWrite(RED_LED, LOW);
            } else {
                digitalWrite(RED_LED, HIGH);
                digitalWrite(GREEN_LED, LOW);
            }

            found = true;
            break;
        }
    }

    if (!found) {
        Serial.println("| Unknown  | ----   | ----     | Access Denied |");
        Serial.println("Unknown,----,----,Access Denied");  // Excel CSV Output

        digitalWrite(RED_LED, HIGH);
        digitalWrite(GREEN_LED, LOW);
    }

    delay(1000);
    digitalWrite(GREEN_LED, LOW);
    digitalWrite(RED_LED, LOW);

    rfid.PICC_HaltA();
    rfid.PCD_StopCrypto1();
}

