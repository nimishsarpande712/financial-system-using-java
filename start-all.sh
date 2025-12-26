#!/bin/bash
# Production Startup Script for JPMorgan Transaction Service

set -e

echo "🚀 Starting JPMorgan Transaction Service..."
echo "============================================"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check Java
echo -e "${BLUE}Checking Java...${NC}"
if ! command -v java &> /dev/null; then
    echo "Java not found! Installing..."
    apt-get update && apt-get install -y openjdk-17-jdk
fi
java -version

# Check Maven
echo -e "${BLUE}Checking Maven...${NC}"
if ! command -v mvn &> /dev/null; then
    echo "Maven not found! Installing..."
    apt-get install -y maven
fi
mvn -version

# Build Backend
echo -e "${BLUE}Building backend...${NC}"
cd /app
mvn clean package -DskipTests
echo -e "${GREEN}✓ Backend built successfully${NC}"

# Install Frontend Dependencies
echo -e "${BLUE}Installing frontend dependencies...${NC}"
cd /app/frontend
if [ ! -d "node_modules" ]; then
    yarn install
fi
echo -e "${GREEN}✓ Frontend dependencies installed${NC}"

# Start Backend
echo -e "${BLUE}Starting backend on port 8081...${NC}"
cd /app
nohup mvn spring-boot:run -Dspring-boot.run.fork=false > /var/log/backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"

# Wait for backend to be ready
echo "Waiting for backend to start..."
for i in {1..30}; do
    if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Backend is ready!${NC}"
        break
    fi
    sleep 2
    echo -n "."
done

# Start Frontend
echo -e "${BLUE}Starting frontend on port 3000...${NC}"
cd /app/frontend
supervisorctl restart frontend || (nohup yarn start > /var/log/frontend.log 2>&1 &)
echo -e "${GREEN}✓ Frontend started${NC}"

echo ""
echo "============================================"
echo -e "${GREEN}🎉 All services started successfully!${NC}"
echo ""
echo "📊 Service Status:"
echo "  Backend:  http://localhost:8081"
echo "  Frontend: http://localhost:3000"
echo "  Health:   http://localhost:8081/actuator/health"
echo "  Swagger:  http://localhost:8081/swagger-ui.html"
echo "  H2 Console: http://localhost:8081/h2-console"
echo ""
echo "📝 Logs:"
echo "  Backend:  tail -f /var/log/backend.log"
echo "  Frontend: tail -f /var/log/supervisor/frontend.out.log"
echo ""
echo "✅ Ready to accept requests!"
