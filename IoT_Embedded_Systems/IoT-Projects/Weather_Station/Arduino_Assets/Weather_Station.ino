#include <DHT.h>

#define DHTPIN 2       
#define DHTTYPE DHT11  
#define LDR_PIN 3      

DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(9600);
  dht.begin();
  pinMode(LDR_PIN, INPUT); // Set LDR pin as input
  Serial.println("READY"); 
}

void loop() {
  // Reading temperature and humidity
  float temperature = dht.readTemperature(); // Read temperature in °C
  float humidity = dht.readHumidity();       // Read humidity in %

  // Reading LDR digital output
  int sunlight = digitalRead(LDR_PIN);


  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("ERROR");
    return;
  }

  
  String sunlightStatus = (sunlight == LOW) ? "Bright" : "Dim";

  // Send data to Serial Monitor and Excel in CSV format
  Serial.print(temperature);
  Serial.print("C,");
  Serial.print(humidity);
  Serial.print("%,");
  Serial.println(sunlightStatus);

  delay(2000); 
}
