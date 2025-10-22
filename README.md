# ExpenseMate - Smart Expense & Budget Tracker with Gamified Progress System

A comprehensive Spring Boot web application that helps users manage, analyze, and improve their spending habits through gamification and intelligent insights.

## 🚀 Features

### Core Functionality
- **Secure Authentication**: Role-based access control (User/Admin) with Spring Security
- **Expense Management**: Add, edit, delete expenses with categorization (Food, Travel, Rent, etc.)
- **Budget Tracking**: Set monthly budgets with automatic email alerts
- **Interactive Dashboards**: Visualize spending patterns with Chart.js

### 🎮 Gamified Progress System (Unique Feature)
- **Badge System**: Earn achievements based on spending behavior
  - 🏆 **Budget Hero**: Spend less than 80% of monthly budget
  - 💰 **Consistent Saver**: Stay within budget for 3 consecutive months
  - 📊 **Spending Streak Maintainer**: Log expenses daily for 7+ days
- **Dynamic Badge Display**: Real-time badge updates on dashboard
- **Achievement Collection**: Comprehensive badge tracking system

### 📊 Dashboard & Analytics
- Monthly expense summary (pie charts)
- Spending trend analysis (line charts)
- Budget progress indicators
- Category-wise spending breakdown

### 🔔 Smart Notifications
- Email alerts at 80% and 100% budget usage
- Automated monthly budget resets
- Badge achievement notifications

### 📈 Additional Features
- PDF/CSV report generation
- Advanced expense filtering
- Scheduled background tasks
- Admin monitoring dashboard

## 🛠️ Tech Stack

- **Backend**: Spring Boot, Spring Security, Spring Data JPA, Spring Scheduler
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js
- **Database**: MySQL
- **Email**: JavaMailSender
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17+
- MySQL 8.0+
- Maven 3.6+
- SMTP server configuration for email alerts

## ⚙️ Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/Kusum-k/ExpenseMate.git
   cd ExpenseMate
   ```

2. **Configure Database**
   ```sql
   CREATE DATABASE expensemate;
   ```

3. **Update application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/expensemate
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   spring.mail.host=smtp.gmail.com
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - URL: http://localhost:8080
   - Default Admin: admin@expensemate.com / admin123
   - Default User: user@expensemate.com / user123

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/expensemate/
│   │   ├── config/          # Security & Email configuration
│   │   ├── controller/      # REST controllers
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business logic
│   │   ├── scheduler/       # Background tasks
│   │   └── ExpenseMateApplication.java
│   └── resources/
│       ├── templates/       # Thymeleaf templates
│       ├── static/          # CSS, JS, images
│       └── application.properties
└── test/                    # Unit tests
```

## 🎯 Key Learning Outcomes

- Secure CRUD operations with authentication
- Automated background tasks using schedulers
- Email integration and alerting systems
- Dynamic data visualization
- Gamification logic design
- Role-based access control

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions, please open an issue in the GitHub repository.

---

**ExpenseMate** - Transform your spending habits through smart tracking and gamified progress! 🎮💰