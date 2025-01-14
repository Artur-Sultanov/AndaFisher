# 🌊 **Anda Fisher Application**

## 📖 Project Overview
The **Anda Fisher** application is a platform for managing beaches, tracking fish species, and providing real-time weather updates. It allows users to interact with beach data, upload media, and soon will include social features like reviews, ratings, and travel companions.

## 🚀 Features
- **CRUD Operations** for beaches and fish (Create, Read, Update, Delete)
- **File Upload**: Upload images related to beaches
- **Real-time Weather Integration**: Weather data from OpenWeatherMap API
- **DTOs and Mappers**: Clean and secure API responses
- **Beach Filtering:** Search beaches by water type, location, or name.
- **Entity Relationship Management**: Link beaches with fish species
- **Advanced Error Handling**: User-friendly error messages with proper HTTP statuses

## 🏗️ Project Structure

```
anda-fisher
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.anda_fisher
│   │   │       ├── Controller
│   │   │       │   ├── BeachController.java
│   │   │       │   └── FishController.java
│   │   │       ├── Model
│   │   │       │   ├── Beach.java
│   │   │       │   ├── Fish.java
│   │   │       │   ├── BeachFish.java
│   │   │       │   └── WaterType.java
│   │   │       ├── Service
│   │   │       │   ├── BeachService.java
│   │   │       │   ├── FishService.java
│   │   │       │   ├── FileStorageService.java
│   │   │       │   └── WeatherService.java
│   │   │       ├── Repository
│   │   │       │   ├── BeachRepository.java
│   │   │       │   └── FishRepository.java
│   │   │       ├── Security
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── DTO
│   │   │       │   ├── BeachDTO.java
│   │   │       │   ├── FishDTO.java
│   │   │       │   └── WeatherDTO.java
│   │   │       ├── Mapper
│   │   │       │   ├── FishMapper.java
│   │   │       │   └── BeachMapper.java
│   │   │       ├── Filter
│   │   │       │   └── BeachFilter.java
│   │   │       └── Config
│   │   │           └── WebConfig.java
│   └── resources
├── uploads/images
├── uploads/archive
├── pom.xml
└── README.md
```

## ⚙️ Technologies Used
- **Java 17**
- **Spring Boot 3.3.0**
- **Hibernate/JPA**
- **PostgreSQL**
- **Lombok**
- **Maven**
- **OpenWeatherMap API**

---

## 🛠️ Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Artur-Sultanov/AndaFisher.git
   cd anda-fisher
   ```

2. **Configure Database**
   Update `application.properties` with your PostgreSQL credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/app
   spring.datasource.username=postgres
   spring.datasource.password=12345Aa@
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

   The application will start on **http://localhost:8081**.

## 📡 API Endpoints

### 🌊 Beach Management

- **Get all beaches**  
  `GET /api/beaches`

- **Get beach by ID**  
  `GET /api/beaches/{id}`

- **Create new beach**  
  `POST /api/beaches`
  ```json
  {
    "name": "Playa Granada",
    "location": "Granada",
    "latitude": 36.7783,
    "longitude": -119.4179,
    "waterType": "FRESHWATER",
    "imagePath": ""
  }
  ```

- **Update beach**  
  `PUT /api/beaches/{id}`

- **Delete beach**  
  `DELETE /api/beaches/{id}`  
  **Response:**
  ```json
  {
    "message": "Beach with id {id} has been successfully deleted."
  }
  ```
  
### 🌦️ Weather Integration

- **Get weather for a beach** 
  `GET /api/beaches/{id}/weather`
   **Example Response:**

  ```json
  {
    "location": "Granada",
    "description": "clear sky",
    "temperature": 22.5,
    "feelsLike": 20.0,
    "windSpeed": 2.5,
    "humidity": 65,
    "iconUrl": "http://openweathermap.org/img/wn/01d@2x.png"
  }
  ```


### 🖼️ **Image Upload**

- **Endpoint:** `POST /api/beaches/{id}/uploadImage`  
- **Form Data:**  
  - **Key:** `file` → *(Select image: `jpg`, `jpeg`, `png`, `gif`, max size: 5MB)*  

**Response:**  
- ✅ *Success:* `"Image uploaded successfully"`  
- ❌ *Error:* `"Invalid file format or upload failed"`  
- ⚠️ *Error:* `"File size exceeds 5MB limit"`
  


### ⚙️ Static Resource Configuration

- **Configured access to uploaded images using `WebConfig.java`:**
  ```java
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/uploads/**")
              .addResourceLocations("file:uploads/");
  }
  ```

---


## ⚠️ Error Handling

- **404 Not Found:** If the beach does not exist (handled in service layer). 
- **400 Bad Request:** Invalid file type during upload (validated in controller).
- **500 Internal Server Error:** Server-side errors (handled via global exception handling).

## 📌 Future Improvements

- 🔒 User authentication & authorization (JWT)
- 🔄 Caching Weather Data to reduce API load
- ⭐ Reviews and Ratings for beaches
- 📱 Social Features: Chat and travel companion search
- ❗ Implement Soft Delete with Media Archive for safer data removal and recovery.
- 🗑 Auto-cleaning Archive: Auto-delete old media files after 30 days
  

## 📝 License

This project is licensed under the [MIT License](LICENSE).
---

**Developed by Anda Fisher Team** 🌊🐟
