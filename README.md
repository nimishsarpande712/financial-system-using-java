# JPMorgan Chase Transaction Service

A production-ready Spring Boot microservice for processing high-volume financial transactions with Kafka integration, developed as part of the JPMorgan Chase Software Engineering Job Simulation on Forage (December 2025).

## 🎯 Features

- **Kafka Integration**: Consumes and deserializes high-volume transaction messages from configurable Kafka topics
- **Transaction Processing**: Validates transactions, processes credits/debits, and manages user balances
- **Database Persistence**: Spring Data JPA with H2 SQL database for reliable data storage
- **External API Integration**: Connects to REST Incentive API using RestTemplate for reward calculations
- **Balance Management**: Tracks and updates user balances with optimistic locking for concurrency
- **REST API**: Query user balances and transaction history via JSON endpoints
- **Comprehensive Testing**: Maven test suites with embedded Kafka framework
- **Production-Ready**: Docker support, health checks, monitoring, and cloud deployment configurations

## 🏗️ Architecture

```
Transaction Microservice
├── Kafka Consumer Layer
│   └── Consumes transaction messages from Kafka topics
├── Service Layer
│   ├── Transaction Processing & Validation
│   ├── User Balance Management
│   └── External Incentive API Integration
├── Data Access Layer
│   ├── Spring Data JPA Repositories
│   └── H2 Database
└── REST API Layer
    └── User balance and transaction queries
```

## 🛠️ Technology Stack

- **Java**: 17 (LTS)
- **Spring Boot**: 3.2.1
- **Spring Kafka**: Message consumption and processing
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory and file-based SQL database
- **Maven**: Build and dependency management
- **Lombok**: Code generation
- **JUnit 5 & Mockito**: Testing framework
- **SpringDoc OpenAPI**: API documentation
- **Docker & Kubernetes**: Containerization and orchestration

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** (optional, for containerized deployment)
- **Kafka** (optional, embedded Kafka used for testing)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd transaction-service
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run Tests

```bash
mvn test
```

### 4. Run Locally

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:transactiondb`
- Username: `sa`
- Password: (leave empty)

### 6. Access API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/api-docs`

## 🐳 Docker Deployment

### Build Docker Image

```bash
docker build -t transaction-service:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

This starts:
- Zookeeper (port 2181)
- Kafka (port 9092)
- Transaction Service (port 8080)

### Stop Services

```bash
docker-compose down
```

## ☸️ Kubernetes Deployment

### Apply Kubernetes Manifests

```bash
kubectl apply -f k8s-deployment.yml
```

### Check Deployment Status

```bash
kubectl get pods
kubectl get services
```

### View Logs

```bash
kubectl logs -f deployment/transaction-service
```

## 📡 API Endpoints

### User Management

#### Get User Balance
```http
GET /api/users/{userId}/balance
```

**Response:**
```json
{
  "userId": 1,
  "username": "john_doe",
  "email": "john.doe@example.com",
  "balance": 1151.50
}
```

#### Get All Users
```http
GET /api/users
```

### Transaction Management

#### Get All Transactions
```http
GET /api/transactions
```

#### Get User Transactions
```http
GET /api/transactions/user/{userId}
```

**Response:**
```json
[
  {
    "id": 1,
    "transactionId": "txn-001",
    "userId": 1,
    "type": "CREDIT",
    "amount": 150.00,
    "description": "Deposit",
    "incentiveApplied": true,
    "incentiveAmount": 1.50,
    "status": "COMPLETED",
    "timestamp": "2025-01-15T10:30:00"
  }
]
```

## 📨 Kafka Message Format

The service consumes transaction messages from Kafka in the following JSON format:

```json
{
  "transactionId": "txn-123456",
  "userId": 1,
  "type": "CREDIT",
  "amount": 150.00,
  "description": "Purchase at Store X"
}
```

### Kafka Configuration

- **Topic**: `transaction-topic` (configurable via `kafka.topic.transactions`)
- **Consumer Group**: `transaction-consumer-group`
- **Bootstrap Servers**: `localhost:9092` (configurable)

### Testing Kafka Producer

You can send test messages using the Kafka console producer:

```bash
docker exec -it kafka kafka-console-producer --bootstrap-server localhost:9092 --topic transaction-topic

# Then paste JSON messages
{"transactionId":"txn-001","userId":1,"type":"CREDIT","amount":150.00,"description":"Test deposit"}
```

## 🎁 Incentive API Integration

The service integrates with an external Incentive API to calculate rewards:

**Endpoint**: `POST /api/incentives/calculate`

**Request:**
```json
{
  "userId": 1,
  "transactionAmount": 150.00,
  "transactionType": "CREDIT"
}
```

**Response:**
```json
{
  "incentiveAmount": 1.50,
  "incentiveType": "PERCENTAGE",
  "applied": true
}
```

### Fallback Logic

When the external API is unavailable or disabled, the service uses default incentive calculation:
- **1% incentive** for CREDIT transactions above $100
- **No incentive** for DEBIT transactions or amounts below $100

## 🔧 Configuration

### Application Profiles

- **dev**: Development profile with H2 in-memory database and debug logging
- **prod**: Production profile with file-based H2 and optimized logging

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILE` | Active Spring profile | `dev` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker addresses | `localhost:9092` |
| `INCENTIVE_API_URL` | External incentive API endpoint | `http://localhost:8081/api/incentives/calculate` |
| `INCENTIVE_API_ENABLED` | Enable/disable external API | `true` |
| `SERVER_PORT` | Application port | `8080` |

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=TransactionServiceTest
```

### Test Coverage

The project includes:
- **Unit Tests**: Service layer and business logic
- **Integration Tests**: Kafka consumer with embedded Kafka
- **Controller Tests**: REST API endpoints with MockMvc
- **Repository Tests**: Database operations

## 📊 Monitoring & Health Checks

### Health Endpoint

```bash
curl http://localhost:8080/actuator/health
```

### Metrics Endpoint

```bash
curl http://localhost:8080/actuator/metrics
```

### Available Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

## 🚀 Cloud Deployment

### AWS Deployment (ECS/EKS)

1. Build and push Docker image to ECR
2. Create ECS task definition or EKS deployment
3. Configure environment variables
4. Set up Application Load Balancer
5. Deploy and monitor

### Azure Deployment (App Service/AKS)

1. Build Docker image
2. Push to Azure Container Registry
3. Create App Service or AKS cluster
4. Deploy application
5. Configure monitoring with Application Insights

### GCP Deployment (Cloud Run/GKE)

1. Build and push to Google Container Registry
2. Deploy to Cloud Run or GKE
3. Configure Cloud SQL if needed
4. Set up monitoring with Cloud Logging

## 🔒 Security Considerations

- **Input Validation**: All transaction data is validated
- **Optimistic Locking**: Prevents race conditions in balance updates
- **Error Handling**: Comprehensive exception handling with proper HTTP status codes
- **API Security**: Ready for authentication/authorization integration
- **Secrets Management**: Use environment variables for sensitive configuration

## 📈 Performance Optimizations

- **Kafka Consumer Concurrency**: Configurable parallel processing (default: 3)
- **Database Indexing**: Optimized queries with proper indexes
- **Connection Pooling**: Efficient database connection management
- **Caching**: Circuit breaker pattern for external API calls

## 🐛 Troubleshooting

### Kafka Connection Issues

```bash
# Check if Kafka is running
docker ps | grep kafka

# View Kafka logs
docker logs kafka
```

### Database Issues

```bash
# Access H2 console to inspect data
http://localhost:8080/h2-console
```

### Application Logs

```bash
# View application logs
docker logs transaction-service

# Or in Kubernetes
kubectl logs -f deployment/transaction-service
```

## 📝 Project Structure

```
transaction-service/
├── src/
│   ├── main/
│   │   ├── java/com/jpmorgan/transaction/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── model/            # JPA entities
│   │   │   ├── dto/              # Data transfer objects
│   │   │   ├── kafka/            # Kafka consumers
│   │   │   └── exception/        # Exception handling
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql          # Initial data
│   └── test/                     # Test classes
├── pom.xml                       # Maven configuration
├── Dockerfile                    # Docker build file
├── docker-compose.yml           # Local development setup
├── k8s-deployment.yml           # Kubernetes manifests
└── README.md                    # This file
```

## 🤝 Contributing

This project was developed as part of the JPMorgan Chase Software Engineering Job Simulation. Feel free to fork and enhance!

## 📄 License

This project is for educational purposes as part of the Forage job simulation program.

## 🙏 Acknowledgments

- **JPMorgan Chase** for the Software Engineering Job Simulation
- **Forage** for providing the learning platform
- **Spring Framework** for the excellent ecosystem

## 📧 Contact

For questions or feedback about this project, please reach out through GitHub issues.

---

**Built with ❤️ as part of JPMorgan Chase Software Engineering Job Simulation - December 2025**
