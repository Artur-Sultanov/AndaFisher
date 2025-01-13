# Anda Fisher Application

## 📖 Project Overview
The **Anda Fisher** application is designed to manage beach data with features for creating, updating, deleting, and viewing beach information. Additionally, it supports uploading images for each beach and managing associated fish species.

## 🚀 Features
- **CRUD Operations** for beaches (Create, Read, Update, Delete)
- **File Upload**: Upload images related to beaches
- **Data Transfer Objects (DTO)** for clear API responses
- **Entity Relationship Management**: Link beaches with fish species

## 🏗️ Project Structure

```
anda-fisher
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.anda_fisher
│   │   │       ├── Controller
│   │   │       │   └── BeachController.java
│   │   │       ├── Model
│   │   │       │   ├── Beach.java
│   │   │       │   ├── Fish.java
│   │   │       │   ├── BeachFish.java
│   │   │       │   └── WaterType.java
│   │   │       ├── Service
│   │   │       │   ├── BeachService.java
│   │   │       │   ├── FishService.java
│   │   │       │   └── FileStorageService.java
│   │   │       └── dto
│   │   │           └── BeachDTO.java
│   └── resources
├── uploads/images
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

## 🛠️ Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-repo/anda-fisher.git
   cd anda-fisher
   ```

2. **Configure Database**
   Update `application.properties` with your PostgreSQL credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/anda_fisher
   spring.datasource.username=your_username
   spring.datasource.password=your_password
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

### 🖼️ Image Upload

- **Upload beach image**  
  `POST /api/beaches/{id}/uploadImage`
  - **Form Data:**  
    Key: `file` → Upload an image file (`jpg`, `jpeg`, `png`, `gif`)

## ⚠️ Error Handling

- **404 Not Found:** If the beach does not exist.
- **400 Bad Request:** Invalid file type during upload.
- **500 Internal Server Error:** Server-side errors.

## 📌 Future Improvements
- User authentication & authorization
- Advanced filtering and search capabilities
- Cloud storage for uploaded images

## 📝 License
MIT License. See `LICENSE` file for details.

---

**Developed by Anda Fisher Team** 🌊🐟

