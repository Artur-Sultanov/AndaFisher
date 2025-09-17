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

2. **Configure Environment Variables**
   The application reads all sensitive settings (database, JWT, SMTP, weather API) from environment variables.
   Sample placeholders such as `change_me` and `your_app_password` are used in the property files so no real secrets live in the repo—make sure to replace them locally.
   For local development you can either export the values manually or supply them through a `.env` file.
   For containerised runs, copy the example file and adjust the values:
   ```bash
   cp .env.example .env
   # edit .env
   ```

3. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   **Or if Maven Wrapper is missing:**
   ```bash
   mvn -N io.takari:maven:wrapper
   ./mvnw spring-boot:run
   ```

   The application will start on **http://localhost:8081**.

## 🐳 Running with Docker Compose

1. **Prepare the Environment File**
   Copy `.env.example` to `.env` and fill in the required values:
   - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` – PostgreSQL connection details used by both the application and the bundled database container.
   - `WEATHER_API_KEY` – OpenWeatherMap API key for fetching weather data.
   - `JWT_SECRET`, `JWT_EXPIRATION_MS` – parameters for JWT token generation.
   - `SPRING_MAIL_*` variables – SMTP settings for sending emails.
   - `SPRINGDOC_SWAGGER_UI_ENABLED` – toggle Swagger UI in production (usually `false`).

2. **Launch the Stack**
   ```bash
   docker compose up -d
   ```

3. **Access the Services**
   - Backend API: http://localhost:8081
   - PostgreSQL: localhost:5432

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
    "latitude": 36.7453,
    "longitude": -3.5174,
    "waterType": "SALTHWATER",
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
- ✅ *Success:* "Image uploaded successfully"
- ❌ *Error:* "Invalid file format or upload failed"
- ⚠️ *Error:* "File size exceeds 5MB limit"

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

