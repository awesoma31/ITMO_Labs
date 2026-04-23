#!/bin/bash

# Check backend availability
# Usage: ./check-backend.sh [base_url]

# Configuration
BASE_URL="${1:-${BASE_URL:-http://localhost:8080}}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Backend Health Check ===${NC}\n"
echo -e "${BLUE}Testing URL: ${BASE_URL}${NC}\n"

# Check if Docker containers are running
echo -e "${YELLOW}1. Checking Docker containers...${NC}"
if command -v docker &> /dev/null; then
    CONTAINERS=$(docker ps --filter "name=cryptoterm" --format "table {{.Names}}\t{{.Status}}" 2>/dev/null || echo "")
    if [ -n "$CONTAINERS" ]; then
        echo "$CONTAINERS"
    else
        echo -e "${RED}✗ No cryptoterm containers found${NC}"
        echo -e "Run: ${BLUE}sudo docker-compose ps${NC} to see container status"
    fi
else
    echo -e "${YELLOW}⚠ Docker command not available${NC}"
fi
echo ""

# Check backend container logs
echo -e "${YELLOW}2. Checking backend container...${NC}"
BACKEND_STATUS=$(docker ps --filter "name=cryptoterm_backend" --format "{{.Status}}" 2>/dev/null || echo "not found")
echo -e "Backend status: ${BACKEND_STATUS}"

if [ "$BACKEND_STATUS" != "not found" ]; then
    echo -e "\nLast 5 log lines:"
    docker logs cryptoterm_backend --tail 5 2>&1 | tail -5
fi
echo ""

# Test actuator endpoint
echo -e "${YELLOW}3. Testing actuator/health endpoint...${NC}"
HEALTH_RESPONSE=$(curl -s --max-time 5 --connect-timeout 2 "${BASE_URL}/actuator/health" 2>&1)
HEALTH_EXIT=$?

if [ $HEALTH_EXIT -eq 0 ]; then
    echo -e "${GREEN}✓ Health endpoint responded${NC}"
    echo "Response: $HEALTH_RESPONSE"
else
    echo -e "${RED}✗ Failed to connect to health endpoint${NC}"
    echo "Error: $HEALTH_RESPONSE"
fi
echo ""

# Test API endpoint
echo -e "${YELLOW}4. Testing API endpoint (GET /api/v1/commands/device/test)...${NC}"
API_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --max-time 5 --connect-timeout 2 "${BASE_URL}/api/v1/commands/device/test" 2>&1)
API_EXIT=$?

if [ $API_EXIT -eq 0 ]; then
    HTTP_CODE=$(echo "$API_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    BODY=$(echo "$API_RESPONSE" | sed '/HTTP_CODE:/d')
    
    if [ -n "$HTTP_CODE" ]; then
        echo -e "HTTP Code: ${BLUE}${HTTP_CODE}${NC}"
        if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "403" ]; then
            echo -e "${GREEN}✓ API endpoint is responding${NC}"
        else
            echo -e "${YELLOW}⚠ API returned unexpected code${NC}"
        fi
    fi
else
    echo -e "${RED}✗ Failed to connect to API endpoint${NC}"
    echo "Error: $API_RESPONSE"
fi
echo ""

# Test creating a dummy command
echo -e "${YELLOW}5. Testing command creation endpoint (POST /api/v1/commands)...${NC}"
TEST_PAYLOAD='{"deviceId":"test","asic":{"ip":"192.168.1.1","port":80,"scheme":"http","firmware":"anthill"},"steps":[{"id":"test_step","request":{"method":"GET","path":"/api/v1/status","headers":{},"body":null,"timeoutMs":5000},"extract":null}],"policy":{"maxRetries":0,"retryDelayMs":1000}}'

CREATE_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --max-time 10 --connect-timeout 5 -X POST "${BASE_URL}/api/v1/commands" \
    -H "Content-Type: application/json" \
    -d "$TEST_PAYLOAD" 2>&1)
CREATE_EXIT=$?

if [ $CREATE_EXIT -eq 0 ]; then
    HTTP_CODE=$(echo "$CREATE_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    BODY=$(echo "$CREATE_RESPONSE" | sed '/HTTP_CODE:/d')
    
    if [ -n "$HTTP_CODE" ]; then
        echo -e "HTTP Code: ${BLUE}${HTTP_CODE}${NC}"
        if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
            echo -e "${GREEN}✓ Command creation is working!${NC}"
            echo "Response preview: ${BODY:0:100}..."
        elif [ "$HTTP_CODE" = "400" ]; then
            echo -e "${YELLOW}⚠ Got 400 (validation error) - endpoint is working but payload might be invalid${NC}"
            echo "Response: $BODY"
        else
            echo -e "${RED}✗ Unexpected response code${NC}"
            echo "Response: $BODY"
        fi
    fi
else
    echo -e "${RED}✗ Failed to connect to command creation endpoint${NC}"
    echo "Error: $CREATE_RESPONSE"
    echo -e "\n${YELLOW}Possible issues:${NC}"
    echo "  1. Backend is not running"
    echo "  2. Backend is not accessible at ${BASE_URL}"
    echo "  3. Firewall is blocking the connection"
    echo "  4. Wrong BASE_URL (try with external IP or domain name)"
fi
echo ""

# Check MongoDB
echo -e "${YELLOW}6. Checking MongoDB connection...${NC}"
MONGO_STATUS=$(docker ps --filter "name=cryptoterm_mongodb" --format "{{.Status}}" 2>/dev/null || echo "not found")
echo -e "MongoDB status: ${MONGO_STATUS}"

if [ "$MONGO_STATUS" != "not found" ]; then
    MONGO_TEST=$(docker exec cryptoterm_mongodb mongosh --quiet --eval "db.adminCommand('ping')" 2>&1 || echo "failed")
    if [[ "$MONGO_TEST" == *"ok"* ]]; then
        echo -e "${GREEN}✓ MongoDB is responding${NC}"
    else
        echo -e "${RED}✗ MongoDB is not responding${NC}"
    fi
fi
echo ""

# Summary
echo -e "${YELLOW}=== Summary ===${NC}"
if [ $CREATE_EXIT -eq 0 ] && [ "$HTTP_CODE" = "201" -o "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ Backend is healthy and ready to accept commands${NC}"
    echo -e "\nYou can now run:"
    echo -e "  ${BLUE}BASE_URL=${BASE_URL} ASIC_IP=192.168.3.20 ./create-antminer-s19-profiles.sh <device_id>${NC}"
else
    echo -e "${RED}✗ Backend is not ready or not accessible${NC}"
    echo -e "\n${YELLOW}Troubleshooting steps:${NC}"
    echo "  1. Check if containers are running: ${BLUE}sudo docker-compose ps${NC}"
    echo "  2. Start containers if needed: ${BLUE}sudo docker-compose up -d${NC}"
    echo "  3. Check backend logs: ${BLUE}sudo docker logs cryptoterm_backend -f${NC}"
    echo "  4. Try different BASE_URL (external IP): ${BLUE}BASE_URL=http://YOUR_IP:8080 ./check-backend.sh${NC}"
    echo "  5. Check if port 8080 is accessible: ${BLUE}curl -v ${BASE_URL}/actuator/health${NC}"
fi
echo ""
