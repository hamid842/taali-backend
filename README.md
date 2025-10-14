# Taali Backend - School Management System

A modern, full-featured school management backend built with **Quarkus**, **Kotlin**, and **PostgreSQL**. Features JWT authentication, multi-language support, and complete user management.

## 📋 Table of Contents

- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Detailed Setup](#-detailed-setup)
- [API Usage](#-api-usage)
- [Development](#-development)
- [Troubleshooting](#-troubleshooting)
- [Project Structure](#-project-structure)

## 🛠 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software
- **Java 21** or later
- **Maven 3.8** or later
- **Docker** and **Docker Compose**
- **Git**

### Verify Installation
Open your terminal/command prompt and run:

```bash
# Check Java version
java -version

# Check Maven
mvn -version

# Check Docker
docker --version

# Check Docker Compose
docker-compose --version
```

If any commands fail, install the missing software first.

## 🚀 Quick Start
Follow these steps to get the project running:

1. **Clone the Repository**
```bash
git clone https://github.com/hamid842/taali-backend.git
cd taali-backend
```

2. **Generate JWT Security Keys**
```bash
# Generate private key
openssl genrsa -out privateKey.pem 2048

# Generate public key from private key
openssl rsa -in privateKey.pem -pubout -out src/main/resources/publicKey.pem
```

3. **Start Database Services**
```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# Verify containers are running
docker-compose ps
```

4. **Run the Application**
```bash
# Start in development mode
./mvnw quarkus:dev
```
or if you installed quarkus-cli :
```bash
quarkus dev
```

5. **Verify Installation**
- Open http://localhost:8080/q/health → Should show `{"status":"UP"}`
- Open http://localhost:8080/q/dev/ → Quarkus Development UI
- Open http://localhost:8081 → pgAdmin (admin@tcall.com / admin)

🎉 Your backend is now running!

## 📖 Detailed Setup
### Step 1: Project Setup
```bash
git clone <repository-url>
cd tcall-backend

# View project structure
ls -la
```

You should see:
- `docker-compose.yml` - Database configuration
- `pom.xml` - Maven dependencies
- `scripts/` - Utility scripts
- `src/` - Source code

### Step 2: JWT Key Generation
#### Why We Need This
JWT tokens require RSA key pairs for signing and verification.

#### Using the Script (Recommended)
```bash
chmod +x scripts/generate-keys.sh
./scripts/generate-keys.sh
```

#### Manual Generation
```bash
# Generate private key (keep this secure!)
openssl genrsa -out privateKey.pem 2048

# Generate public key from private key
openssl rsa -in privateKey.pem -pubout -out src/main/resources/publicKey.pem
```

🔐 **Security Notice:**
- `privateKey.pem` → **NEVER commit to git**
- `publicKey.pem` → Safe to commit

### Step 3: Database Setup
```bash
docker-compose up -d
docker-compose ps
```

#### Expected Output
```
Name                    Command              State           Ports
--------------------------------------------------------------------
tcall-postgres   docker-entrypoint.sh postgres    Up      0.0.0.0:5432->5432/tcp
tcall-pgadmin    /entrypoint.sh                   Up      0.0.0.0:8081->80/tcp
```

#### Database Information
| Item | Value |
|------|--------|
| PostgreSQL | localhost:5432 |
| Database | tcall_db |
| Username | postgres |
| Password | password |

#### Access pgAdmin
Open http://localhost:8081  
Login → admin@tcall.com / admin

Add a new server:
```
Name: TCALL Database
Host: postgres (or localhost)
Port: 5432
Username: postgres
Password: password
```

### Step 4: Application Startup
```bash
./mvnw quarkus:dev
```

#### Verify Application Health
```bash
curl http://localhost:8080/q/health
```

Expected: `{"status":"UP","checks":[...]}`

## 🎯 API Usage
### Authentication Endpoints

#### 1. Register a New User
```bash
curl -X POST http://localhost:8080/auth/register   -H "Content-Type: application/json"   -d '{
    "email": "teacher@school.com",
    "password": "teacher123",
    "firstName": "John",
    "lastName": "Smith",
    "phone": "+1234567890",
    "role": "TEACHER"
  }'
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login   -H "Content-Type: application/json"   -d '{
    "email": "teacher@school.com",
    "password": "teacher123"
  }'
```

#### 3. Refresh Token
```bash
curl -X POST http://localhost:8080/auth/refresh   -H "Authorization: Bearer <your-refresh-token>"
```

#### 4. Logout
```bash
curl -X POST http://localhost:8080/auth/logout   -H "Authorization: Bearer <your-refresh-token>"
```

### User Roles
- **STUDENT** - Basic access
- **TEACHER** - Enhanced permissions
- **ADMIN** - Full system access
- **PARENT** - Parental access

### Multi-language Support
Set language via header:
```bash
# English (default)
-H "Accept-Language: en"

# Farsi (Persian)
-H "Accept-Language: fa"
```

Example with Farsi:
```bash
curl -X POST http://localhost:8080/auth/register   -H "Content-Type: application/json"   -H "Accept-Language: fa"   -d '{
    "email": "user@school.com",
    "password": "password123",
    "firstName": "علی",
    "lastName": "محمدی",
    "phone": "+989123456789",
    "role": "STUDENT"
  }'
```

## 💻 Development
```bash
# 1. Ensure database is running
docker-compose up -d

# 2. Start application with live reload
./mvnw quarkus:dev

# 3. Test APIs immediately
```

### Useful Development URLs
- http://localhost:8080 → Application
- http://localhost:8080/q/health → Health check
- http://localhost:8080/q/dev/ → Dev UI
- http://localhost:8080/q/swagger-ui/ → Swagger UI
- http://localhost:8081 → Database UI

### Development Commands
```bash
./mvnw test
./mvnw clean compile -DskipTests
./mvnw clean package
./mvnw dependency:tree
```

## 🐛 Troubleshooting
### Common Issues & Solutions
#### 1. Port Already in Use
```bash
lsof -i :8080
kill -9 <PID>
./mvnw quarkus:dev -Dquarkus.http.port=8082
```

#### 2. Database Connection Failed
```bash
docker-compose ps
docker-compose up -d postgres
docker-compose logs postgres
```

#### 3. JWT Public Key Not Found
```bash
./scripts/generate-keys.sh
openssl genrsa -out privateKey.pem 2048
openssl rsa -in privateKey.pem -pubout -out src/main/resources/publicKey.pem
```

#### 4. Maven Dependencies Failing
```bash
./mvnw clean compile
./mvnw clean compile -U
./mvnw dependency:purge-local-repository
```

#### 5. Docker Permission Issues (Linux)
```bash
sudo usermod -aG docker $USER
newgrp docker
```

## 📁 Project Structure
```text
tcall-backend/
├── src/main/kotlin/com/tcall/
│   ├── entity/              # Database entities
│   │   ├── User.kt
│   │   └── RefreshToken.kt
│   ├── dto/
│   │   ├── AuthRequest.kt
│   │   ├── AuthResponse.kt
│   │   └── ApiResponse.kt
│   ├── service/
│   │   ├── AuthService.kt
│   │   ├── JwtService.kt
│   │   ├── PasswordService.kt
│   │   └── LocalizationService.kt
│   └── resource/
│       └── AuthResource.kt
├── src/main/resources/
│   ├── application.properties
│   ├── publicKey.pem
│   ├── messages_en.properties
│   └── messages_fa.properties
├── docker-compose.yml
├── init-scripts/
│   └── 01-init.sql
├── scripts/
│   └── generate-keys.sh
└── pom.xml
```

## 🎊 What's Included
✅ JWT Authentication  
✅ PostgreSQL Database  
✅ User Management  
✅ Multi-language Support  
✅ Live Reload  
✅ Password Security  
✅ Input Validation  
✅ Swagger UI

## ❓ Getting Help
```bash
docker-compose ps
curl http://localhost:8080/q/health
docker-compose logs postgres
```

## 🚀 Next Steps
✅ Backend API → http://localhost:8080  
✅ Database → localhost:5432  
✅ Ready to connect with your React frontend!

Happy coding! 🎉