#include <Servo.h>

#define trigPin 8
#define echoPin 9
#define greenLED 3
#define blueLED 4
#define redLED 5
#define buzzer 6

Servo turretServo;
int angle = 0;
int direction = 1;  
bool targetLocked = false;

void setup() {
pinMode(trigPin, OUTPUT);
pinMode(echoPin, INPUT);
pinMode(greenLED, OUTPUT);
pinMode(blueLED, OUTPUT);
pinMode(redLED, OUTPUT);
pinMode(buzzer, OUTPUT);

turretServo.attach(10); // Servo control on pin 10
Serial.begin(9600);
}

long getDistance() {
digitalWrite(trigPin, LOW);
delayMicroseconds(2);

digitalWrite(trigPin, HIGH);
delayMicroseconds(10);
digitalWrite(trigPin, LOW);

long duration = pulseIn(echoPin, HIGH, 30000); 
long distance = duration * 0.034 / 2;
return distance;
}

void loop() {
if (!targetLocked) {
digitalWrite(greenLED, HIGH);

// Move the servo in current direction
turretServo.write(angle);
delay(50); 

long distance = getDistance();
Serial.print("Angle: ");
Serial.print(angle);
Serial.print("° | Distance: ");
Serial.print(distance);
Serial.println(" cm");

if (distance > 0 && distance <= 15) {
// Target detected
targetLocked = true;
digitalWrite(greenLED, LOW);
digitalWrite(blueLED, HIGH);
Serial.println("Target Locked!");

delay(3000); // Simulate aiming

digitalWrite(blueLED, LOW);
digitalWrite(redLED, HIGH);
digitalWrite(buzzer, HIGH);
Serial.println("Missile Launched!");

delay(1000);
digitalWrite(buzzer, LOW);
delay(1000);
digitalWrite(redLED, LOW);
} else {
Serial.println("No target found.");
}

// Sweep angle update
angle += direction;
if (angle >= 180 || angle <= 0) {
direction *= -1;  // Change direction
}
}

// Reset when object is gone
if (targetLocked) {
long distance = getDistance();
if (distance > 15 || distance == 0) {
targetLocked = false;
Serial.println("Target Lost. Resetting...");
}
}
}
#include <Servo.h>

#define trigPin 8
#define echoPin 9
#define greenLED 3
#define blueLED 4
#define redLED 5
#define buzzer 6

Servo turretServo;
int angle = 0;
bool targetLocked = false;

void setup() {
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(greenLED, OUTPUT);
  pinMode(blueLED, OUTPUT);
  pinMode(redLED, OUTPUT);
  pinMode(buzzer, OUTPUT);
  
  turretServo.attach(10); 
  Serial.begin(9600);
}

long getDistance() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  long duration = pulseIn(echoPin, HIGH);
  long distance = duration * 0.034 / 2;
  return distance;
}

void loop() {
  if (!targetLocked) {
    digitalWrite(greenLED, HIGH);
    for (angle = 0; angle <= 180; angle++) {
      turretServo.write(angle);
      delay(30); 
      
      long distance = getDistance();
      Serial.print("Angle: ");
      Serial.print(angle);
      Serial.print(" | Distance: ");
      Serial.println(distance);

      if (distance > 0 && distance <= 15) {
        targetLocked = true;
        digitalWrite(greenLED, LOW);
        digitalWrite(blueLED, HIGH);
        Serial.println("Target Locked!");
        delay(3000);
        digitalWrite(blueLED, LOW);
        digitalWrite(redLED, HIGH);
        digitalWrite(buzzer, HIGH);
        Serial.println("Missile Launched!");
        delay(1000);
        digitalWrite(buzzer, LOW);
        delay(1000);
        digitalWrite(redLED, LOW);
        break;
      }
    }
    digitalWrite(greenLED, LOW);
  }

  // Wait for object to move away
  if (targetLocked) {
    long distance = getDistance();
    if (distance > 15) {
      targetLocked = false;
      Serial.println("Target Lost. Resetting...");
    }
  }
}
