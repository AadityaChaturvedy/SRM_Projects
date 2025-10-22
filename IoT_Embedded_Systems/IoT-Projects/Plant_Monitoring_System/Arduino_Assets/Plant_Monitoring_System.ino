#include <DHT.h>

#define DHTPIN 2        
#define DHTTYPE DHT11   

#define LDR_PIN 3       
#define SOIL_SENSOR 4   
#define GREEN_LED 7     
#define RED_LED 8       

DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(9600);
  dht.begin();
  pinMode(LDR_PIN, INPUT);
  pinMode(SOIL_SENSOR, INPUT);  
  pinMode(GREEN_LED, OUTPUT);
  pinMode(RED_LED, OUTPUT);

  Serial.println("------------------------------------------------------");
  Serial.println("| Temp (°C) | Humidity (%) | Light  | Moisture | Pump |");
  Serial.println("------------------------------------------------------");
}

void loop() {
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  int sunlight = digitalRead(LDR_PIN);
  int soilMoisture = digitalRead(SOIL_SENSOR);  // Read Digital Output (HIGH/LOW)

  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("ERROR: Sensor Read Failed");
    return;
  }

  String sunlightStatus = (sunlight == LOW) ? "Bright" : "Dim";
  String moistureStatus = (soilMoisture == LOW) ? "Wet" : "Dry"; 
  String pumpStatus;

  if (soilMoisture == HIGH) {  // Dry soil
    digitalWrite(GREEN_LED, HIGH);
    digitalWrite(RED_LED, LOW);
    pumpStatus = "ON";
  } else {  // Wet soil
    digitalWrite(GREEN_LED, LOW);
    digitalWrite(RED_LED, HIGH);
    pumpStatus = "OFF";
  }

  Serial.print("| ");
  Serial.print(temperature);
  Serial.print("°C  | ");
  Serial.print(humidity);
  Serial.print("%     | ");
  Serial.print(sunlightStatus);
  Serial.print(" | ");
  Serial.print(moistureStatus);
  Serial.print("    | ");
  Serial.print(pumpStatus);
  Serial.println("  |");

  delay(2000);
}