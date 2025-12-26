# JPMorgan Transaction Service - Quick Start Guide

## Prerequisites
- Java 17+
- Maven 3.6+
- Docker (optional)

## Quick Start

### 1. Build the Project
```bash
mvn clean install
```

### 2. Run Tests
```bash
mvn test
```

### 3. Run Locally (Without Kafka)
```bash
mvn spring-boot:run
```

Access the application:
- API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
- Swagger UI: http://localhost:8080/swagger-ui.html

### 4. Run with Docker (Full Stack)
```bash
docker-compose up -d
```

This starts:
- Zookeeper
- Kafka
- Transaction Service

## Testing the Application

### Check User Balances
```bash
curl http://localhost:8080/api/users
```

### Get Specific User Balance
```bash
curl http://localhost:8080/api/users/1/balance
```

### View All Transactions
```bash
curl http://localhost:8080/api/transactions
```

### Send Test Transaction (Kafka)
```bash
docker exec -it kafka kafka-console-producer --bootstrap-server localhost:9092 --topic transaction-topic

# Paste this JSON:
{"transactionId":"txn-001","userId":1,"type":"CREDIT","amount":150.00,"description":"Test deposit"}
```

## Health Check
```bash
curl http://localhost:8080/actuator/health
```

## Shutdown
```bash
docker-compose down
```

## Troubleshooting

### View Logs
```bash
docker logs transaction-service
```

### Access H2 Database
- URL: jdbc:h2:mem:transactiondb
- Username: sa
- Password: (leave empty)

### Kafka Issues
```bash
# Check Kafka is running
docker ps | grep kafka

# View Kafka logs
docker logs kafka
```

For more details, see the main README.md