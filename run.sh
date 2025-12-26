#!/bin/bash
# JPMorgan Transaction Service - Helper Script

set -e

function show_help() {
    echo "JPMorgan Transaction Service - Helper Script"
    echo ""
    echo "Usage: ./run.sh [command]"
    echo ""
    echo "Commands:"
    echo "  build          - Build the project with Maven"
    echo "  test           - Run all tests"
    echo "  run            - Run the application locally"
    echo "  docker-build   - Build Docker image"
    echo "  docker-up      - Start all services with docker-compose"
    echo "  docker-down    - Stop all services"
    echo "  clean          - Clean build artifacts"
    echo "  h2-console     - Open H2 console (app must be running)"
    echo "  swagger        - Open Swagger UI (app must be running)"
    echo "  kafka-produce  - Send a test message to Kafka"
    echo "  help           - Show this help message"
}

function build() {
    echo "🔨 Building project with Maven..."
    mvn clean install
}

function test() {
    echo "🧪 Running tests..."
    mvn test
}

function run() {
    echo "🚀 Starting application..."
    mvn spring-boot:run
}

function docker_build() {
    echo "🐳 Building Docker image..."
    docker build -t transaction-service:latest .
}

function docker_up() {
    echo "🐳 Starting services with docker-compose..."
    docker-compose up -d
    echo ""
    echo "✅ Services started!"
    echo "   - Transaction Service: http://localhost:8080"
    echo "   - H2 Console: http://localhost:8080/h2-console"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - Kafka: localhost:9092"
}

function docker_down() {
    echo "🛑 Stopping services..."
    docker-compose down
}

function clean() {
    echo "🧹 Cleaning build artifacts..."
    mvn clean
}

function h2_console() {
    echo "🗄️  Opening H2 Console..."
    xdg-open http://localhost:8080/h2-console 2>/dev/null || open http://localhost:8080/h2-console 2>/dev/null || echo "Please open http://localhost:8080/h2-console in your browser"
}

function swagger_ui() {
    echo "📚 Opening Swagger UI..."
    xdg-open http://localhost:8080/swagger-ui.html 2>/dev/null || open http://localhost:8080/swagger-ui.html 2>/dev/null || echo "Please open http://localhost:8080/swagger-ui.html in your browser"
}

function kafka_produce() {
    echo "📨 Sending test message to Kafka..."
    echo 'Make sure docker-compose is running!'
    docker exec -it kafka kafka-console-producer --bootstrap-server localhost:9092 --topic transaction-topic << EOF
{"transactionId":"test-001","userId":1,"type":"CREDIT","amount":150.00,"description":"Test transaction"}
EOF
    echo "✅ Message sent!"
}

case "${1}" in
    build)
        build
        ;;
    test)
        test
        ;;
    run)
        run
        ;;
    docker-build)
        docker_build
        ;;
    docker-up)
        docker_up
        ;;
    docker-down)
        docker_down
        ;;
    clean)
        clean
        ;;
    h2-console)
        h2_console
        ;;
    swagger)
        swagger_ui
        ;;
    kafka-produce)
        kafka_produce
        ;;
    help|--help|-h|"")
        show_help
        ;;
    *)
        echo "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac