#!/bin/bash

# Resend all PENDING commands for a device
# Usage: ./resend-all-pending.sh <device_id>

set -e

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8080}"
AUTH_TOKEN="${AUTH_TOKEN:-your-jwt-token}"
MONGO_HOST="${MONGO_HOST:-localhost}"
MONGO_PORT="${MONGO_PORT:-27017}"
MONGO_USER="${MONGO_USER:-cryptoterm}"
MONGO_PASS="${MONGO_PASS:-cryptoterm}"
MONGO_DB="${MONGO_DB:-cryptoterm}"
DEVICE_ID="${1:-}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Resend All PENDING Commands ===${NC}\n"

# Check if device ID is provided
if [ -z "$DEVICE_ID" ]; then
    echo -e "${RED}ERROR: Device ID is required${NC}"
    echo "Usage: $0 <device_id>"
    echo "Example: $0 58a8c94f-114d-4ed0-a8eb-8569ec2838bc"
    exit 1
fi

# Check if token is set
if [ "$AUTH_TOKEN" = "your-jwt-token" ]; then
    echo -e "${RED}ERROR: Please set AUTH_TOKEN environment variable${NC}"
    echo "Example: export AUTH_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    exit 1
fi

echo -e "${BLUE}Finding PENDING commands for device: ${DEVICE_ID}${NC}\n"

# Get all PENDING command IDs from MongoDB
CMD_IDS=$(docker exec cryptoterm_mongodb mongosh \
    --quiet \
    --host "$MONGO_HOST" \
    --port "$MONGO_PORT" \
    --username "$MONGO_USER" \
    --password "$MONGO_PASS" \
    --authenticationDatabase admin \
    "$MONGO_DB" \
    --eval "db.asic_commands.find({device_id: \"$DEVICE_ID\", status: \"PENDING\"}, {_id: 1}).toArray().map(c => c._id).join(' ')")

if [ -z "$CMD_IDS" ] || [ "$CMD_IDS" = "null" ]; then
    echo -e "${YELLOW}No PENDING commands found for device${NC}"
    exit 0
fi

echo -e "${GREEN}Found commands: $CMD_IDS${NC}\n"

SUCCESS_COUNT=0
FAIL_COUNT=0

# Resend each command
for CMD_ID in $CMD_IDS; do
    echo -e "${BLUE}Resending: $CMD_ID${NC}"
    
    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/commands/$CMD_ID/resend" \
        -H "Authorization: Bearer $AUTH_TOKEN")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    
    if [ "$HTTP_CODE" -eq 200 ]; then
        echo -e "${GREEN}✓ Success${NC}\n"
        ((SUCCESS_COUNT++))
    else
        echo -e "${RED}✗ Failed (HTTP $HTTP_CODE)${NC}\n"
        ((FAIL_COUNT++))
    fi
    
    sleep 0.5  # Small delay between requests
done

# Summary
echo -e "${YELLOW}=== Summary ===${NC}"
echo -e "${GREEN}✓ Success: $SUCCESS_COUNT${NC}"
if [ $FAIL_COUNT -gt 0 ]; then
    echo -e "${RED}✗ Failed: $FAIL_COUNT${NC}"
fi

echo -e "\n${GREEN}=== Done ===${NC}"
