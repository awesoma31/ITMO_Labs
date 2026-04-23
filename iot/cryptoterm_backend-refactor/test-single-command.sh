#!/bin/bash

# Test creating a single ASIC command
# Usage: ./test-single-command.sh <device_id>

set -e

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8080}"
DEVICE_ID="${1:-}"
ASIC_IP="${ASIC_IP:-192.168.1.100}"
ASIC_PORT="${ASIC_PORT:-80}"
ASIC_SCHEME="${ASIC_SCHEME:-http}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Test Single ASIC Command ===${NC}\n"

# Check if device ID is provided
if [ -z "$DEVICE_ID" ]; then
    echo -e "${RED}ERROR: Device ID is required${NC}"
    echo "Usage: $0 <device_id>"
    echo "Example: $0 58a8c94f-114d-4ed0-a8eb-8569ec2838bc"
    exit 1
fi

echo -e "${BLUE}Device ID: ${DEVICE_ID}${NC}"
echo -e "${BLUE}ASIC: ${ASIC_SCHEME}://${ASIC_IP}:${ASIC_PORT}${NC}"
echo -e "${BLUE}Backend: ${BASE_URL}${NC}\n"

# Create test command
WATTS=3495
HASHRATE=132
NAME="TEST_3495W_132TH"

echo -e "${YELLOW}Creating test command: ${NAME}${NC}\n"

JSON_PAYLOAD=$(cat <<EOF
{
  "deviceId": "${DEVICE_ID}",
  "asic": {
    "ip": "${ASIC_IP}",
    "port": ${ASIC_PORT},
    "scheme": "${ASIC_SCHEME}",
    "firmware": "anthill"
  },
  "steps": [
    {
      "id": "unlock",
      "request": {
        "method": "POST",
        "path": "/api/v1/unlock",
        "headers": {
          "Content-Type": "application/json"
        },
        "body": {
          "pw": "admin"
        },
        "timeoutMs": 10000
      },
      "extract": {
        "token": "\$.token"
      }
    },
    {
      "id": "set_profile_${WATTS}",
      "request": {
        "method": "POST",
        "path": "/api/v1/settings",
        "headers": {
          "Content-Type": "application/json",
          "Authorization": "Bearer \${token}"
        },
        "body": {
          "miner": {
            "overclock": {
              "modded_psu": false,
              "preset": "${WATTS}"
            }
          }
        },
        "timeoutMs": 15000
      }
    },
    {
      "id": "restart_mining",
      "request": {
        "method": "POST",
        "path": "/api/v1/mining/restart",
        "headers": {
          "Content-Type": "application/json",
          "Authorization": "Bearer \${token}"
        },
        "body": {},
        "timeoutMs": 10000
      }
    }
  ],
  "policy": {
    "maxRetries": 2,
    "retryDelayMs": 2000
  }
}
EOF
)

echo -e "${BLUE}Sending request to ${BASE_URL}/api/v1/commands${NC}\n"

# Send request with verbose output
RESPONSE=$(curl -v --max-time 30 --connect-timeout 10 -X POST "$BASE_URL/api/v1/commands" \
    -H "Content-Type: application/json" \
    -d "$JSON_PAYLOAD" 2>&1)

echo -e "\n${YELLOW}=== Response ===${NC}"
echo "$RESPONSE"

# Try to extract HTTP code
HTTP_CODE=$(echo "$RESPONSE" | grep "< HTTP" | awk '{print $3}' | tail -n1)

echo -e "\n${YELLOW}=== Analysis ===${NC}"
if [ -n "$HTTP_CODE" ]; then
    echo -e "HTTP Code: ${BLUE}${HTTP_CODE}${NC}"
    
    if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✓ Success - Command created${NC}"
        
        # Try to extract command ID
        CMD_ID=$(echo "$RESPONSE" | grep -o '"cmdId":"[^"]*"' | cut -d'"' -f4)
        if [ -n "$CMD_ID" ]; then
            echo -e "Command ID: ${GREEN}${CMD_ID}${NC}"
            echo -e "\nTo view command:"
            echo -e "  ./view-command.sh ${CMD_ID}"
        fi
    else
        echo -e "${RED}✗ Failed - HTTP ${HTTP_CODE}${NC}"
    fi
else
    echo -e "${RED}✗ Failed - Could not determine HTTP code${NC}"
    echo "This might indicate a connection error or timeout"
fi

echo -e "\n${GREEN}=== Test Completed ===${NC}"
