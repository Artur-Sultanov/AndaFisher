# 🌊 **Anda Fisher Application**

## 📖 Project Overview
The **Anda Fisher** application is a platform for managing beaches, tracking fish species, and providing real-time weather updates. It allows users to interact with beach data, upload media, and includes advanced features for user authentication and role-based access.

## 🚀 Features
- **CRUD Operations** for beaches and fish (Create, Read, Update, Delete)
- **User Authentication & Authorization**: Secure login and registration with JWT
- **Role Management**: Admin and User roles with fine-grained access control
- **File Upload**: Upload images related to beaches
- **Real-time Weather Integration**: Weather data from OpenWeatherMap API
- **DTOs and Mappers**: Clean and secure API responses
- **Beach Filtering:** Search beaches by water type, location, or name
- **Approval Workflow:** Admin approval required for beaches and fish
- **Entity Relationship Management**: Link beaches with fish species
- **Advanced Error Handling**: User-friendly error messages with proper HTTP statuses
- **Static Resource Hosting:** Serve uploaded images from a dedicated endpoint

## 🏗️ Project Structure

```
anda-fisher
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.anda_fisher
│   │   │       ├── Config
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── SwaggerConfig.java
│   │   │       │   └── WebConfig.java
│   │   │       ├── Controller
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── BeachController.java
│   │   │       │   ├── FishController.java
│   │   │       │   └── AdminController.java
│   │   │       ├── DTO
│   │   │       │   ├── BeachDTO.java
│   │   │       │   ├── FishDTO.java
│   │   │       │   ├── WeatherDTO.java
│   │   │       │   └── UserDTO.java
│   │   │       ├── Exception
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── ConflictException.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   └── ValidationException.java
│   │   │       ├── Filter
│   │   │       │   └── BeachFilter.java
│   │   │       ├── Mapper
│   │   │       │   ├── BeachMapper.java
│   │   │       │   ├── FishMapper.java
│   │   │       │   └── UserMapper.java
│   │   │       ├── Model
│   │   │       │   ├── Beach.java
│   │   │       │   ├── Fish.java
│   │   │       │   ├── BeachFish.java
│   │   │       │   ├── WaterType.java
│   │   │       │   └── User.java
│   │   │       ├── Repository
│   │   │       │   ├── BeachRepository.java
│   │   │       │   ├── FishRepository.java
│   │   │       │   └── UserRepository.java
│   │   │       ├── Security
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtService.java
│   │   │       ├── Service
│   │   │       │   ├── BeachService.java
│   │   │       │   ├── FishService.java
│   │   │       │   ├── FileStorageService.java
│   │   │       │   ├── UserService.java
│   │   │       │   ├── EmailService.java
│   │   │       │   └── WeatherService.java
│   │   │       └── Specification
│   │   │           └── BeachSpecifications.java
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
- **Swagger** (OpenAPI 3.0) for API Documentation

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
    "imagePath": "",
    "description": "Beautiful beach",
    "approved": false
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

### 🔒 User Authentication

- **Register User**  
  `POST /auth/register`  
  ```json
  {
    "username": "john_doe",
    "password": "securePassword",
    "email": "john@example.com",
    "phoneNumber": "+1234567890"
  }
  ```

- **Login User**  
  `POST /auth/login`  
  ```json
  {
    "username": "john_doe",
    "password": "securePassword"
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

### 🛡️ Admin Management

- **Approve Beach**  
  `PUT /admin/beaches/{id}/approve`

- **Delete Beach**  
  `DELETE /admin/beaches/{id}`

- **Approve Fish**  
  `PUT /admin/fish/{id}/approve`

- **Delete Fish**  
  `DELETE /admin/fish/{id}`

- **Get All Users**  
  `GET /admin/users`

- **Delete User**  
  `DELETE /admin/users/{id}`

- **Update User Role**  
  `PUT /admin/users/{id}/role`
  ```json
  {
    "role": "ADMIN"
  }
  ```

---

## 📌 Future Improvements

- 🔄 Caching Weather Data to reduce API load
- ⭐ Reviews and Ratings for beaches
- 📱 Social Features: Chat and travel companion search
- ❗ Implement Soft Delete with Media Archive for safer data removal and recovery
- 🗑 Auto-cleaning Archive: Auto-delete old media files after 30 days
- 🖥️ Dashboard: Advanced analytics for admin users
- 🧾 Add Detailed Logging for API Requests
- 📜 API Usage Analytics for Admins
- 🌍 Multilingual Support for Global Users

## 📝 License

This project is licensed under the [MIT License](LICENSE).
---

**Developed by Anda Fisher Team** 🌊🐟
