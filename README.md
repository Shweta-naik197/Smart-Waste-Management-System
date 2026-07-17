# ♻️ Smart Waste Management System

A personal Java Full Stack web application developed to simplify waste complaint management between citizens, municipal administrators, and sanitation workers. The system enables citizens to report garbage issues with images and location details while allowing administrators to assign tasks and workers to update complaint statuses efficiently.

---

## 🌟 Project Overview

Managing waste collection manually often leads to delays and poor communication. This application provides a centralized platform where:

- Citizens can report waste issues.
- Municipal administrators can monitor and assign complaints.
- Workers can update the status of assigned tasks.

The system aims to improve transparency, accountability, and efficiency in municipal waste management.

---

## ✨ Features

### 👤 Citizen Module
- User Registration & Login
- Raise Waste Complaints
- Upload Garbage Images
- Add Area & Location Details
- Track Complaint Status
- View Complaint History
- Manage Profile

### 👨‍💼 Admin Module
- Secure Authentication
- Dashboard Overview
- Manage Citizens
- View All Complaints
- Assign Complaints to Workers
- Monitor Complaint Progress

### 👷 Worker Module
- Secure Login
- View Assigned Complaints
- Update Complaint Status
- Mark Complaints as Completed

---

## 🛠️ Technology Stack

### Frontend
- HTML5
- CSS3
- JavaScript
- Thymeleaf

### Backend
- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA (Hibernate)

### Database
- MySQL

### Build Tool
- Maven

### Version Control
- Git
- GitHub

### IDE
- Visual Studio Code

---

## 📁 Project Structure

```
Smart-Waste-Management-System
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   ├── templates/
│   │   │   └── application.properties
│
├── uploads/
├── schema.sql
├── pom.xml
└── README.md
```

---

## 🚀 Getting Started

### Clone the Repository

```bash
git clone https://github.com/Shweta-naik197/Smart-Waste-Management-System.git
```

### Navigate to the Project

```bash
cd Smart-Waste-Management-System
```

### Create the Database

```sql
CREATE DATABASE smart_waste_management;
```

### Configure Database Credentials

Update the following file:

```
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_waste_management
spring.datasource.username=root
spring.datasource.password=your_password
```

### Run the Application

```bash
mvn spring-boot:run
```

Or run the `SmartWasteApplication.java` file directly from your IDE.

---

## 📸 Screenshots

Add screenshots of:

- Home Page
- Login Page
- Registration Page
- Citizen Dashboard
- Raise Complaint Page
- Admin Dashboard
- Worker Dashboard

---

## 🚀 Future Enhancements

- Google Maps Integration
- GPS-Based Location Detection
- Email & SMS Notifications
- AI-Based Waste Classification
- Mobile Application
- Analytics Dashboard
- Real-Time Complaint Tracking

---

## 📚 Key Concepts Learned

- Java Full Stack Development
- Spring Boot Framework
- Spring Security Authentication
- MVC Architecture
- CRUD Operations
- Role-Based Access Control
- File Upload Handling
- MySQL Database Integration
- Thymeleaf Templating
- Git & GitHub Version Control

---

## 👩‍💻 Developed By

**Shweta Naik**

Information Science & Engineering Student  
Canara Engineering College

GitHub: https://github.com/Shweta-naik197

