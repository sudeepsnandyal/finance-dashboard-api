# Finance Dashboard Backend

A comprehensive REST API backend for a finance dashboard system built with Spring Boot, Spring Security, JWT authentication, and MySQL.

## Features

### Core Functionality

1. **User and Role Management**
   - Create, read, update, and delete users
   - Role-based access control (Viewer, Analyst, Admin)
   - User status management (Active/Inactive)

2. **Financial Records Management**
   - Full CRUD operations for financial records
   - Support for income and expense types
   - Category-based organization
   - Date-wise filtering
   - Cursor-based pagination for efficient data fetching
   - Soft delete functionality

3. **Dashboard Summary APIs**
   - Total income and expenses
   - Net balance calculation
   - Category-wise totals (for expense analysis)
   - Monthly trends (12-month history)
   - Recent activity feed

4. **Access Control**
   - Role-based permissions using Spring Security
   - JWT-based authentication
   - Method-level security with `@PreAuthorize`
   - Custom access denied and authentication handlers

5. **Validation & Error Handling**
   - Comprehensive request validation
   - Standardized API responses
   - Proper HTTP status codes
   - Detailed error messages

### Technical Implementation

- **Framework**: Spring Boot 3.1.5
- **Security**: Spring Security with JWT
- **Database**: MySQL 8.x
- **Performance**: Cursor-based pagination, batch queries
- **Data**: Hibernate ORM with JPA
- **API**: RESTful JSON endpoints
- **Validation**: Jakarta Validation (Bean Validation)

## Project Structure

```
src/main/java/com/finance/dashboard/
├── config/
│   ├── AppConfig.java
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── handler/
│       ├── CustomAuthenticationEntryPoint.java
│       └── CustomAccessDeniedHandler.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── FinancialRecordController.java
│   └── DashboardController.java
├── service/
│   ├── CustomUserDetailsService.java
│   ├── UserService.java
│   ├── FinancialRecordService.java
│   ├── DashboardService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── FinancialRecordServiceImpl.java
│       └── DashboardServiceImpl.java
├── repository/
│   ├── UserRepository.java
│   └── FinancialRecordRepository.java
├── model/
│   ├── User.java
│   ├── FinancialRecord.java
│   ├── enums/
│   │   ├── RoleType.java
│   │   ├── UserStatus.java
│   │   └── RecordType.java
│   └── dto/
│       ├── AuthRequest.java
│       ├── AuthResponse.java
│       ├── UserDTO.java
│       ├── FinancialRecordDTO.java
│       ├── DashboardSummaryDTO.java
│       ├── MonthlyTrendDTO.java
│       ├── RecentActivityDTO.java
│       ├── PageResponse.java
│       └── ApiResponse.java
├── util/
│   └── JwtUtil.java
└── exception/
    ├── ResourceNotFoundException.java
    ├── ResourceAlreadyExistsException.java
    ├── AccessDeniedException.java
    └── GlobalExceptionHandler.java
```

## Prerequisites

- **Java 17+** - Required for Spring Boot 3.x
- **Maven 3.6+** - For dependency management
- **MySQL 8.0+** - Database server
- **IDE** (IntelliJ IDEA, Eclipse, VS Code) - Optional but recommended

## Database Setup

1. Install MySQL 8.0 or higher
2. Create a database named `finance_dashboard`:

```sql
CREATE DATABASE finance_dashboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Create a user and grant privileges (optional - you can use root):

```sql
CREATE USER 'finance_user'@'localhost' IDENTIFIED BY 'Finance@123';
GRANT ALL PRIVILEGES ON finance_dashboard.* TO 'finance_user'@'localhost';
FLUSH PRIVILEGES;
```

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/finance_dashboard?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root  # Change to finance_user if you created custom user
spring.datasource.password=root  # Change password accordingly

# JWT Secret (CHANGE THIS!)
app.jwt.secret=yourSuperSecretKeyThatIsAtLeast256BitsLong1234567890!@#$%^&*()_+=
```

**Important**: Change the `app.jwt.secret` to a secure random string in production!

## Running the Application

### Option 1: Using Maven (Command Line)

```bash
# Navigate to the project directory
cd finance-dashboard

# Run the application
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### Option 2: Using IDE

1. Import as Maven project in your IDE
2. Run `FinanceDashboardApplication.java` as Java Application
3. Server starts on `http://localhost:8080`

### Option 3: Build JAR and Run

```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/finance-dashboard-1.0.0.jar
```

## Default Users

On first startup, the application creates three default users:

| Username | Password  | Role   | Description                |
|----------|-----------|--------|----------------------------|
| admin    | admin123  | Admin  | Full system access         |
| analyst  | analyst123| Analyst| Read records + analytics   |
| viewer   | viewer123 | Viewer | Read-only access           |

**Security Note**: Change these passwords in production!

## API Documentation

All APIs are prefixed with `/api/`

### Authentication Endpoints

#### POST `/api/auth/register`
Register a new user account.

**Request Body:**
```json
{
  "username": "john.doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john.doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "status": "ACTIVE",
    "roles": ["ROLE_VIEWER"],
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

#### POST `/api/auth/login`
Authenticate and get JWT token.

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "email": "admin@financedashboard.com",
    "roles": ["ROLE_ADMIN"],
    "expiresAt": "2024-01-02T00:00:00"
  }
}
```

#### GET `/api/auth/validate`
Validate the current JWT token. Requires `Authorization: Bearer <token>` header.

**Response:**
```json
{
  "success": true,
  "message": "Token is valid",
  "data": true
}
```

---

### User Management Endpoints

All user endpoints require authentication. Update/delete only allowed for admins or self.

#### GET `/api/users/{id}`
Get user by ID.

#### GET `/api/users/username/{username}`
Get user by username.

#### GET `/api/users`
Get all users (Admin only).

#### PUT `/api/users/{id}`
Update user details (self or admin).

#### DELETE `/api/users/{id}`
Delete user (self or admin).

#### POST `/api/users/{id}/activate`
Activate user (Admin only).

#### POST `/api/users/{id}/deactivate`
Deactivate user (Admin only).

#### POST `/api/users/{id}/roles`
Assign roles to user (Admin only). Body: `["ROLE_ADMIN", "ROLE_ANALYST"]`.

---

### Financial Records Endpoints

All records endpoints require authentication with roles `VIEWER`, `ANALYST`, or `ADMIN` for reading, and `ANALYST` or `ADMIN` for write operations.

#### Create Record
**POST `/api/records`**

```json
{
  "type": "INCOME",
  "amount": 1500.00,
  "category": "Salary",
  "recordDate": "2024-01-15",
  "notes": "Monthly salary"
}
```

#### Get Record
**GET `/api/records/{id}`**

#### Get All Records (Paginated)
**GET `/api/records?cursor={id}&limit=20`**

Cursor-based pagination:
- Pass `cursor` parameter as the last record ID from previous response
- Omit cursor for first page
- Response includes `content` array and next `cursor` in last item's ID

#### Get Records by Type
**GET `/api/records/type/INCOME?cursor=&limit=20`**

#### Get Records by Category
**GET `/api/records/category/Salary?cursor=&limit=20`**

#### Get Records by Date Range
**GET `/api/records/date-range?startDate=2024-01-01&endDate=2024-01-31&cursor=&limit=20`**

#### Update Record
**PUT `/api/records/{id}`**

```json
{
  "type": "EXPENSE",
  "amount": 299.99,
  "category": "Electronics",
  "recordDate": "2024-01-20",
  "notes": "New headphones"
}
```

#### Delete Record (Permanent)
**DELETE `/api/records/{id}`**

#### Soft Delete Record
**POST `/api/records/{id}/soft-delete`**

Marks record as deleted but keeps in database.

---

### Dashboard Summary Endpoints

All dashboard endpoints require authentication with roles `VIEWER`, `ANALYST`, or `ADMIN`.

#### Get Complete Summary
**GET `/api/dashboard/summary?startDate=&endDate=`**

Response includes:
```json
{
  "totalIncome": 5000.00,
  "totalExpense": 2500.00,
  "netBalance": 2500.00,
  "categoryWiseTotals": {
    "Food": 800.00,
    "Transport": 300.00
  },
  "monthlyTrends": [...],
  "recentActivity": [...]
}
```

#### Get Total Income
**GET `/api/dashboard/income/total?startDate=&endDate=`**

#### Get Total Expense
**GET `/api/dashboard/expense/total?startDate=&endDate=`**

#### Get Category-wise Totals (Expense)
**GET `/api/dashboard/categories?type=EXPENSE`**

#### Get Monthly Trends (12 months)
**GET `/api/dashboard/trends?startDate=`**

Returns array of:
```json
{
  "month": "2024-01",
  "income": 5000.00,
  "expense": 2500.00,
  "net": 2500.00
}
```

#### Get Recent Activity
**GET `/api/dashboard/recent-activity?limit=10`**

---

## Using the API

### Step 1: Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin"
  }
}
```

### Step 2: Use Token in Subsequent Requests

Include the token in the Authorization header:

```bash
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Role-Based Access Control

| Endpoint                    | Viewer | Analyst | Admin |
|-----------------------------|--------|---------|-------|
| `/api/auth/*`               | Yes    | Yes     | Yes   |
| `/api/users/*`              | No     | No      | Yes   |
| `/api/users/{id}` (own)     | Yes    | Yes     | Yes   |
| `/api/records` (read)       | Yes    | Yes     | Yes   |
| `/api/records` (write)      | No     | Yes     | Yes   |
| `/api/dashboard/*`          | Yes    | Yes     | Yes   |

---

## Database Schema

The application uses Hibernate's `ddl-auto=update` to automatically create/update schema.

### Tables

**users**
- id (PK)
- username (unique)
- email (unique)
- password (bcrypt hashed)
- first_name
- last_name
- status (ACTIVE/INACTIVE)
- created_at
- updated_at

**user_roles** (join table for many-to-many)
- user_id (FK)
- role (ENUM: ROLE_VIEWER, ROLE_ANALYST, ROLE_ADMIN)

**financial_records**
- id (PK)
- user_id (FK)
- type (INCOME/EXPENSE)
- amount (DECIMAL)
- category (VARCHAR)
- record_date (DATE)
- notes (TEXT)
- deleted (BOOLEAN)
- created_at
- updated_at

---

## Testing the API

You can test the API using:

### Postman / Insomnia
Import the collection from the `/postman` folder (if available) or create manually.

### cURL Examples

**Login and get token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Create a financial record:**
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "INCOME",
    "amount": 5000.00,
    "category": "Freelance",
    "recordDate": "2024-01-10",
    "notes": "Project payment"
  }'
```

**Get dashboard summary:**
```bash
curl -X GET "http://localhost:8080/api/dashboard/summary?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Error Handling

Standardized error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-01T00:00:00"
}
```

Validation errors return field-level details:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "amount": "Amount must be greater than 0",
    "category": "Category is required"
  }
}
```

---

## Customization

### Adding New Record Categories
Categories are free-text in the current design. To enforce a list:
1. Create an enum for categories
2. Update `FinancialRecordDTO.category` to use the enum
3. Add validation annotation

### Modifying Roles
Edit `RoleType` enum in `model.enums.RoleType` to add/remove roles.

### Changing Pagination Size
Default is 20 records. Modify in controllers:
```java
@RequestParam(value = "limit", defaultValue = "20") int limit
```

### Adjusting JWT Expiration
Edit `application.properties`:
```properties
app.jwt.expiration-ms=172800000  # 2 days in milliseconds
```

---

## Deployment

### Production Considerations

1. **Change JWT Secret**: Use a strong random secret (min 256 bits)
2. **Use Strong Passwords**: Change default user passwords
3. **Enable SSL**: Configure HTTPS in production
4. **Database**: Use connection pool (HikariCP is default), enable pooling
5. **Monitoring**: Add Actuator endpoints for health checks
6. **Logging**: Configure proper logging levels and log rotation
7. **CORS**: Configure allowed origins in SecurityConfig

### Docker (Optional)

Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/finance-dashboard-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
docker build -t finance-dashboard .
docker run -p 8080:8080 finance-dashboard
```

---

## Assumptions & Tradeoffs

1. **Simple Role Model**: Three fixed roles (Viewer/Analyst/Admin). For complex hierarchical roles, RBAC with permissions table would be better.
2. **Soft Delete**: Records are soft-deleted by default; hard delete via DELETE endpoint. This allows recovery.
3. **Cursor Pagination**: Uses ID-based cursor pagination for performance with large datasets.
4. **No Search**: Full-text search not implemented (can be added with database-specific features).
5. **JWT Stateless**: No token blacklist; token expires in 24 hours. For immediate revocation, need token store.
6. **Self-Service**: Users can update their own profiles but only admins can modify roles or other users.
7. **Category-wise totals**: Only for expenses by default (more useful for budgeting).

---

## Future Enhancements

- [ ] Email verification for registration
- [ ] Password reset functionality
- [ ] File upload for receipt attachments
- [ ] Export reports to CSV/PDF
- [ ] Recurring transactions
- [ ] Budget management and alerts
- [ ] Multi-currency support
- [ ] Unit and integration tests
- [ ] API documentation with Swagger/OpenAPI
- [ ] Rate limiting
- [ ] Audit logging
- [ ] Caching layer (Redis) for dashboard queries

---

## Troubleshooting

### Database Connection Error
- Verify MySQL is running
- Check credentials in `application.properties`
- Ensure database exists

### Token Invalid/Expired
- Tokens are valid for 24 hours
- After server restart, new JWT secret may be generated if not configured
- Use `/api/auth/validate` to test token

### 403 Forbidden
- Verify user role has proper permissions
- Check `@PreAuthorize` annotations

### 404 on Records
- Ensure you are accessing records belonging to your user (unless admin)
- Admin can view any user's records

---

## License

This project is for assessment purposes.

---

## Contact

For questions or issues, please refer to the project documentation or create an issue in the repository.
