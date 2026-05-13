# Fundoo Notes

## 📌 Overview
Fundoo Notes is a full-stack note-taking application that allows users to create, update, organize, and manage notes efficiently. The application provides secure authentication, note management features, and RESTful APIs for seamless interaction between frontend and backend services.

---

## 🚀 Features

- User Registration & Login
- JWT-Based Authentication
- Create, Update, Delete Notes
- Archive Notes
- Trash Notes
- Pin Important Notes
- Color Notes
- Reminder Support
- Label Management
- Search Functionality
- REST API Integration
- Database Persistence

---

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring MVC
- Spring Security
- Hibernate / JPA
- Maven

### Database
- MySQL

### Tools & Platforms
- Postman
- Git & GitHub
- IntelliJ IDEA
- Docker

---

## 📂 Project Structure

```bash
Fundoo-Notes/
│── src/
│── controller/
│── service/
│── repository/
│── dto/
│── model/
│── exception/
│── config/
│── resources/
│── pom.xml
│── README.md
```

---

## ⚙️ Installation & Setup

### Clone the Repository

```bash
git clone git@github.com:Wave-Shot/Fundoo-Notes.git
```

### Navigate to Project Folder

```bash
cd Fundoo-Notes
```

### Configure Database

Update the `application.properties` file:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fundoonotes
spring.datasource.username=root
spring.datasource.password=your_password
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

---

## 🔗 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /register | Register User |
| POST | /login | User Login |
| POST | /notes | Create Note |
| GET | /notes | Get All Notes |
| PUT | /notes/{id} | Update Note |
| DELETE | /notes/{id} | Delete Note |

---

## 🧪 Testing

Use Postman or Swagger to test APIs.

```bash
mvn test
```

---

## 📸 Future Enhancements

- Email Notifications
- Collaborator Support
- Cloud Deployment
- Mobile Application
- Real-Time Sync

---

## 👨‍💻 Author

### Srihari V

- GitHub: https://github.com/Wave-Shot

---

## 📄 License

This project is developed for learning and educational purposes.
