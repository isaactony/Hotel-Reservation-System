# Hotel Reservation Management System

A comprehensive hotel reservation management system built with Java Spring Boot backend and JavaFX frontend.

## Features

### Admin Features
- **User Management**: Register and manage admin accounts
- **Room Management**: Add, edit, and manage hotel rooms
- **Reservation Management**: View all reservations and update their status
- **Visitor Management**: View visitor information and booking history
- **Dashboard**: Comprehensive overview of hotel operations

### Visitor Features
- **Account Registration**: Create visitor accounts with personal information
- **Room Search**: Search for available rooms by date range and guest count
- **Room Booking**: Make reservations with special requests
- **Reservation Management**: View and cancel existing reservations
- **Availability Check**: Real-time room availability checking

### System Features
- **Authentication & Authorization**: Secure login system with role-based access
- **RESTful API**: Complete REST API for all operations
- **Database Integration**: H2 in-memory database with JPA/Hibernate
- **Data Validation**: Comprehensive input validation
- **Error Handling**: Proper error handling and user feedback
- **Sample Data**: Pre-populated with sample rooms, users, and reservations

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Frontend**: JavaFX 17
- **Database**: H2 Database (in-memory)
- **Build Tool**: Maven
- **Architecture**: RESTful API with MVC pattern

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 17 (included in dependencies)

## Installation & Setup

1. **Clone or download the project**
   ```bash
   cd "Hotel Reservation System"
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

## Quick Start (Recommended)

### 🚀 One-Click Startup Scripts

The easiest way to run the application is using the provided startup scripts:

#### For Mac/Linux:
```bash
./start.sh
```

#### For Windows:
```cmd
start.bat
```

These scripts will:
- ✅ Check system requirements (Java 17+, Maven 3.6+)
- ✅ Compile the project automatically
- ✅ Start the Spring Boot backend on port 8081
- ✅ Wait for backend to be ready
- ✅ Launch the JavaFX frontend
- ✅ Display sample login credentials
- ✅ Handle cleanup when closing

## Manual Setup (Alternative)

If you prefer to run components separately:

3. **Run the Spring Boot backend**
   ```bash
   mvn spring-boot:run
   ```
   The backend will start on `http://127.0.0.1:8081`

4. **Run the JavaFX application** (in a new terminal)
   ```bash
   mvn javafx:run
   ```
   Or run the main class directly:
   ```bash
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.hotel.javafx.HotelReservationLauncher
   ```

## Usage

### Starting the Application

#### Option 1: Quick Start (Recommended)
Use the startup scripts for the easiest experience:

**Mac/Linux:**
```bash
./start.sh
```

**Windows:**
```cmd
start.bat
```

#### Option 2: Manual Start
1. **Start the Spring Boot server** first (it will initialize sample data automatically)
2. **Launch the JavaFX application** using the launcher
3. **Choose your role** (Admin or Visitor)

### Sample Accounts

#### Admin Accounts
- Username: `admin1`, Password: `password123`
- Username: `admin2`, Password: `password123`

#### Visitor Accounts
- Username: `visitor1`, Password: `password123`
- Username: `visitor2`, Password: `password123`
- Username: `visitor3`, Password: `password123`

### API Endpoints

#### Authentication
- `POST /api/auth/register/admin` - Register new admin
- `POST /api/auth/register/visitor` - Register new visitor
- `POST /api/auth/login` - User login

#### Rooms
- `GET /api/rooms` - Get all rooms
- `GET /api/rooms/available` - Get available rooms
- `GET /api/rooms/available/dates` - Get rooms available for specific dates
- `GET /api/rooms/type/{type}` - Get rooms by type
- `GET /api/rooms/price-range` - Get rooms by price range
- `POST /api/rooms` - Create new room
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room

#### Reservations
- `GET /api/reservations` - Get all reservations (admin)
- `GET /api/reservations/visitor/{id}` - Get visitor's reservations
- `GET /api/reservations/active/visitor/{id}` - Get visitor's active reservations
- `POST /api/reservations` - Create new reservation
- `PUT /api/reservations/{id}/status` - Update reservation status
- `PUT /api/reservations/{id}/cancel` - Cancel reservation

## Database Access

The application uses H2 in-memory database. You can access the H2 console at:
- URL: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:hoteldb`
- Username: `sa`
- Password: `password`

## Project Structure

```
src/
├── main/
│   ├── java/com/hotel/
│   │   ├── entity/          # JPA entities
│   │   ├── repository/       # Data repositories
│   │   ├── service/          # Business logic
│   │   ├── controller/       # REST controllers
│   │   ├── security/         # Security configuration
│   │   ├── dto/             # Data transfer objects
│   │   └── javafx/          # JavaFX applications
│   │       ├── admin/        # Admin dashboard
│   │       ├── visitor/      # Visitor dashboard
│   │       └── HotelReservationLauncher.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Sample Data

The system automatically creates sample data including:
- 2 Admin accounts
- 3 Visitor accounts
- 12 Rooms (Standard, Deluxe, Suite, Family, Business)
- Various room types with different amenities and pricing

## Features in Detail

### Room Types
- **Standard**: Basic rooms with essential amenities ($120/night)
- **Deluxe**: Premium rooms with additional features ($180/night)
- **Suite**: Luxury suites with separate living areas ($300/night)
- **Family**: Large rooms for families ($220/night)
- **Business**: Rooms designed for business travelers ($200/night)

### Reservation Statuses
- **PENDING**: Newly created reservation awaiting confirmation
- **CONFIRMED**: Confirmed reservation
- **CHECKED_IN**: Guest has checked in
- **CHECKED_OUT**: Guest has checked out
- **CANCELLED**: Cancelled reservation
- **NO_SHOW**: Guest didn't show up

## Troubleshooting

### Common Issues

1. **JavaFX Module Path Error**
   - Ensure JavaFX is properly installed and module path is set
   - Use the Maven JavaFX plugin: `mvn javafx:run`

2. **Port Already in Use**
   - Change the port in `application.properties` if 8081 is occupied
   - Update the base URL in JavaFX applications accordingly

3. **Database Connection Issues**
   - Ensure H2 database is properly configured
   - Check application.properties for correct database settings

### Development Notes

- The system uses in-memory H2 database, so data is lost when the application restarts
- For production use, configure a persistent database (MySQL, PostgreSQL, etc.)
- Authentication is basic HTTP authentication - consider implementing JWT for production
- The JavaFX applications assume the backend is running on localhost:8081

## Future Enhancements

- Payment integration
- Email notifications
- Advanced reporting and analytics
- Mobile application
- Multi-language support
- Advanced search filters
- Room images and galleries
- Guest reviews and ratings

## License

This project is for educational purposes. Feel free to use and modify as needed.
