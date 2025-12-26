# Production Deployment Guide

## ✅ Services Running

- **Backend API**: Port 8081 (Spring Boot)
- **Frontend UI**: Port 3000 (React)
- **Database**: H2 in-memory

## 🚀 Quick Start

```bash
# Start all services
./start-all.sh

# Or individually:
# Backend
cd /app && mvn spring-boot:run

# Frontend
cd /app/frontend && yarn start
```

## 📊 Health Checks

```bash
# Backend health
curl http://localhost:8081/actuator/health

# Frontend
curl http://localhost:3000

# API test
curl http://localhost:8081/api/users
```

## 🔗 Access Points

- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8081
- **Swagger Docs**: http://localhost:8081/swagger-ui.html
- **H2 Console**: http://localhost:8081/h2-console
- **Health Check**: http://localhost:8081/actuator/health
- **Metrics**: http://localhost:8081/actuator/metrics

## 📡 API Endpoints

### Users
- `GET /api/users` - List all users
- `GET /api/users/{userId}/balance` - Get user balance

### Transactions
- `GET /api/transactions` - List all transactions
- `GET /api/transactions/user/{userId}` - Get user transactions

## 🎯 Test Data

The application comes with 3 test users:

1. **john_doe** - $1,000.00
2. **jane_smith** - $2,500.00
3. **bob_wilson** - $500.00

## 🐳 Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# This starts:
# - Zookeeper (port 2181)
# - Kafka (port 9092)
# - Transaction Service (port 8080)
```

## ☸️ Kubernetes Deployment

```bash
# Deploy to Kubernetes
kubectl apply -f k8s-deployment.yml

# Check status
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/transaction-service
```

## 🔧 Configuration

### Environment Variables

#### Backend (Spring Boot)
- `SPRING_PROFILE` - Profile (dev/prod)
- `SERVER_PORT` - Server port (default: 8081)
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka servers
- `INCENTIVE_API_URL` - External API URL
- `INCENTIVE_API_ENABLED` - Enable external API (true/false)

#### Frontend (React)
- `PORT` - Frontend port (default: 3000)
- `REACT_APP_BACKEND_URL` - Backend API URL

### Profiles

#### Development (default)
- H2 in-memory database
- Debug logging
- Hot reload enabled

#### Production
- H2 file-based database
- Info level logging
- Optimized builds

## 📝 Logs

```bash
# Backend logs
tail -f /var/log/backend.log

# Frontend logs
tail -f /var/log/supervisor/frontend.out.log

# All supervisor logs
supervisorctl tail -f frontend stdout
```

## 🛠️ Troubleshooting

### Backend not starting
```bash
# Check Java version
java -version  # Should be 17+

# Check Maven
mvn -version

# View errors
tail -50 /var/log/backend.log
```

### Frontend not loading
```bash
# Check Node version
node -v  # Should be 14+

# Reinstall dependencies
cd /app/frontend
rm -rf node_modules
yarn install

# Check logs
tail -50 /var/log/supervisor/frontend.out.log
```

### Port conflicts
```bash
# Check what's using the port
lsof -i :8081
lsof -i :3000

# Kill process if needed
kill -9 <PID>
```

### Database issues
```bash
# Access H2 console
# URL: http://localhost:8081/h2-console
# JDBC URL: jdbc:h2:mem:transactiondb
# Username: sa
# Password: (leave empty)
```

## 🔄 Restart Services

```bash
# Restart all
supervisorctl restart all

# Restart individually
supervisorctl restart backend
supervisorctl restart frontend

# Check status
supervisorctl status
```

## 🧪 Testing

```bash
# Run backend tests
cd /app
mvn test

# Run specific test
mvn test -Dtest=TransactionServiceTest

# Check test coverage
mvn verify
```

## 📦 Building for Production

```bash
# Build backend JAR
cd /app
mvn clean package

# Build frontend static files
cd /app/frontend
yarn build

# The build output is in:
# - Backend: /app/target/transaction-service-1.0.0.jar
# - Frontend: /app/frontend/build/
```

## 🌐 Cloud Deployment

### AWS (ECS/EKS)
1. Build Docker image: `docker build -t transaction-service .`
2. Push to ECR: `docker push <ecr-url>/transaction-service:latest`
3. Update task definition with new image
4. Deploy to ECS/EKS

### Azure (App Service/AKS)
1. Build image: `docker build -t transaction-service .`
2. Push to ACR: `docker push <acr-name>.azurecr.io/transaction-service:latest`
3. Deploy to App Service or AKS

### GCP (Cloud Run/GKE)
1. Build image: `docker build -t transaction-service .`
2. Push to GCR: `docker push gcr.io/<project-id>/transaction-service:latest`
3. Deploy to Cloud Run or GKE

## 🔒 Security Checklist

- [ ] Change default H2 credentials in production
- [ ] Use PostgreSQL/MySQL for production database
- [ ] Enable HTTPS/TLS
- [ ] Configure proper CORS origins
- [ ] Set up authentication/authorization
- [ ] Use environment variables for secrets
- [ ] Enable rate limiting
- [ ] Set up monitoring and alerts

## 📊 Monitoring

### Metrics Available

- `http.server.requests` - HTTP request metrics
- `jvm.memory.used` - JVM memory usage
- `process.cpu.usage` - CPU usage
- `kafka.consumer.fetch.manager.records.consumed` - Kafka consumption

### Health Endpoints

- `/actuator/health` - Overall health
- `/actuator/health/liveness` - Liveness probe
- `/actuator/health/readiness` - Readiness probe

## 🎯 Performance Tuning

### Backend
- Increase JVM heap size: `-Xmx2g -Xms512m`
- Configure Kafka consumer threads
- Enable database connection pooling
- Add caching layer (Redis)

### Frontend
- Use production build: `yarn build`
- Enable gzip compression
- Use CDN for static assets
- Implement code splitting

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [Spring Kafka Documentation](https://spring.io/projects/spring-kafka)
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)

## 🤝 Support

For issues or questions:
- Check logs first
- Review troubleshooting section
- Check health endpoints
- Review configuration

---

**Last Updated**: December 2025
**Version**: 1.0.0
