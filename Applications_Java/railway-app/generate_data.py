import random
import math
from datetime import date, timedelta

# Expanded list of cities with mock coordinates (latitude, longitude) for distance calculation
cities = {
    "Delhi": (28.6139, 77.2090), "Mumbai": (19.0760, 72.8777), "Kolkata": (22.5726, 88.3639),
    "Chennai": (13.0827, 80.2707), "Bangalore": (12.9716, 77.5946), "Hyderabad": (17.3850, 78.4867),
    "Ahmedabad": (23.0225, 72.5714), "Pune": (18.5204, 73.8567), "Surat": (21.1702, 72.8311),
    "Jaipur": (26.9124, 75.7873), "Lucknow": (26.8467, 80.9462), "Kanpur": (26.4499, 80.3319),
    "Nagpur": (21.1458, 79.0882), "Indore": (22.7196, 75.8577), "Thane": (19.2183, 72.9781),
    "Bhopal": (23.2599, 77.4126), "Visakhapatnam": (17.6868, 83.2185), "Patna": (25.5941, 85.1376),
    "Vadodara": (22.3072, 73.1812), "Ghaziabad": (28.6692, 77.4538), "Ludhiana": (30.9010, 75.8573),
    "Agra": (27.1767, 78.0081), "Nashik": (19.9975, 73.7898), "Faridabad": (28.4089, 77.3178),
    "Meerut": (28.9845, 77.7064), "Rajkot": (22.3039, 70.8022), "Varanasi": (25.3176, 82.9739),
    "Srinagar": (34.0837, 74.7973), "Aurangabad": (19.8762, 75.3433), "Dhanbad": (23.7957, 86.4304),
    "Amritsar": (31.6340, 74.8723), "Allahabad": (25.4358, 81.8463), "Ranchi": (23.3441, 85.3096),
    "Howrah": (22.5958, 88.3298), "Coimbatore": (11.0168, 76.9558), "Jabalpur": (23.1815, 79.9864),
    "Gwalior": (26.2183, 78.1828), "Vijayawada": (16.5062, 80.6480), "Jodhpur": (26.2389, 73.0243),
    "Madurai": (9.9252, 78.1198), "Raipur": (21.2514, 81.6296), "Kota": (25.2138, 75.8648)
}

train_names = [
    "Rajdhani Express", "Shatabdi Express", "Duronto Express", "Tejas Express",
    "Garib Rath", "Jan Shatabdi", "Sampark Kranti", "Humsafar Express",
    "Deccan Queen", "Flying Ranee", "Golden Chariot", "Himalayan Queen",
    "Island Express", "Malabar Express", "Nilgiri Express", "Coromandel Express"
]

def calculate_distance(city1, city2):
    lat1, lon1 = cities[city1]
    lat2, lon2 = cities[city2]
    # Simple Euclidean distance for approximation
    return math.sqrt((lat1 - lat2)**2 + (lon1 - lon2)**2)

def calculate_price(distance):
    # Price based on distance, with some randomness
    base_fare = 500
    per_degree_fare = 150 # Approx fare per degree of lat/lon difference
    return round(base_fare + distance * per_degree_fare + random.uniform(-100, 100), 2)

start_date = date(2025, 10, 9)
num_days = 60

# Define specific routes
defined_routes = [
    ("Delhi", "Mumbai"),
    ("Kolkata", "Chennai"),
    ("Bangalore", "Hyderabad"),
    ("Pune", "Ahmedabad"),
    ("Jaipur", "Surat")
]

sql_statements = [
    "DROP TABLE IF EXISTS trains;",
    "CREATE TABLE trains (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, source TEXT, destination TEXT, via TEXT, date TEXT, departure_time TEXT, seats INTEGER, price REAL);",
    "INSERT INTO trains (name, source, destination, via, date, departure_time, seats, price) VALUES"
]

records = []
city_list = list(cities.keys())

for i in range(num_days):
    current_date = start_date + timedelta(days=i)
    date_str = current_date.strftime("%Y-%m-%d")
    
    for source, destination in defined_routes:
        # Generate 2 trains for each defined route
        for _ in range(2):
            # Find a 'via' city that is not the source or destination
            while True:
                via = random.choice(city_list)
                if via != source and via != destination:
                    break
            
            name = random.choice(train_names)
            seats = random.randint(50, 250)
            hour = random.randint(0, 23)
            minute = random.choice([0, 15, 30, 45])
            departure_time = f"{hour:02d}:{minute:02d}"
            
            # Calculate price based on total distance (source -> via -> destination)
            distance = calculate_distance(source, via) + calculate_distance(via, destination)
            price = calculate_price(distance)
            
            records.append(f"('{name}', '{source}', '{destination}', '{via}', '{date_str}', '{departure_time}', {seats}, {price})")

sql_statements.append(",\n".join(records) + ";")

with open("/Users/aadityachaturvedy/Downloads/railway-app/spring-backend/src/main/resources/populate_trains.sql", "w") as f:
    f.write("\n".join(sql_statements))

print("Generated populate_trains.sql with names, timings, prices, and via stations for specific routes.")
