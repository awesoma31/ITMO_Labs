#!/bin/bash

# ASIC Command Templates - Test Script
# Usage: ./test-asic-templates.sh

set -e

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_TOKEN="${ADMIN_TOKEN:-your-admin-jwt-token}"
USER_TOKEN="${USER_TOKEN:-your-user-jwt-token}"
MINER_ID="${MINER_ID:-your-miner-uuid}"
MINER_IP="${MINER_IP:-192.168.1.100}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== ASIC Command Templates Test Script ===${NC}\n"

# Check if tokens are set
if [ "$ADMIN_TOKEN" = "your-admin-jwt-token" ]; then
    echo -e "${RED}ERROR: Please set ADMIN_TOKEN environment variable${NC}"
    echo "Example: export ADMIN_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    exit 1
fi

# Test 1: Create template (Admin)
echo -e "${GREEN}Test 1: Create template '1780_watts' (Admin)${NC}"
curl -X POST "$BASE_URL/api/admin/asic-command-templates" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "1780_watts",
    "description": "Set Antminer S19 Pro Hydro to 1780W power profile",
    "minerModel": "Antminer S19 Pro Hydro",
    "minerVendor": "Bitmain",
    "firmware": "anthill",
    "steps": [
      {
        "id": "unlock",
        "request": {
          "method": "POST",
          "path": "/api/v1/unlock",
          "headers": {"Content-Type": "application/json"},
          "body": {"pw": "admin"},
          "timeoutMs": 10000
        },
        "extract": {"token": "$.token"}
      },
      {
        "id": "set_profile",
        "request": {
          "method": "POST",
          "path": "/api/v1/settings",
          "headers": {
            "Content-Type": "application/json",
            "Authorization": "Bearer ${token}"
          },
          "body": {
            "miner": {
              "overclock": {
                "modded_psu": false,
                "preset": "1780"
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
            "Authorization": "Bearer ${token}"
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
  }'
echo -e "\n"

# Test 2: List all templates (Admin)
echo -e "${GREEN}Test 2: List all templates (Admin)${NC}"
curl -X GET "$BASE_URL/api/admin/asic-command-templates" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
echo -e "\n"

# Test 3: Get specific template (Admin)
echo -e "${GREEN}Test 3: Get template '1780_watts' (Admin)${NC}"
curl -X GET "$BASE_URL/api/admin/asic-command-templates/1780_watts" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
echo -e "\n"

# Test 4: List templates (User)
echo -e "${GREEN}Test 4: List available templates (User)${NC}"
curl -X GET "$BASE_URL/api/v1/asic-command-templates" \
  -H "Authorization: Bearer $USER_TOKEN"
echo -e "\n"

# Test 5: Get templates for model (User)
echo -e "${GREEN}Test 5: Get templates for 'Antminer S19 Pro Hydro' (User)${NC}"
curl -X GET "$BASE_URL/api/v1/asic-command-templates/model/Antminer%20S19%20Pro%20Hydro" \
  -H "Authorization: Bearer $USER_TOKEN"
echo -e "\n"

# Test 6: Execute template (User) - requires valid MINER_ID
if [ "$MINER_ID" != "your-miner-uuid" ]; then
    echo -e "${GREEN}Test 6: Execute template on miner (User)${NC}"
    RESPONSE=$(curl -X POST "$BASE_URL/api/v1/miners/$MINER_ID/execute-template/1780_watts" \
      -H "Authorization: Bearer $USER_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{\"minerIp\": \"$MINER_IP\"}" \
      -s)
    
    echo "$RESPONSE"
    
    # Extract command ID from response
    CMD_ID=$(echo "$RESPONSE" | grep -o '"cmdId":"[^"]*' | cut -d'"' -f4)
    
    if [ -n "$CMD_ID" ]; then
        echo -e "\n${GREEN}Command ID: $CMD_ID${NC}"
        
        # Test 7: Check command status
        echo -e "${GREEN}Test 7: Check command status${NC}"
        sleep 2
        curl -X GET "$BASE_URL/api/v1/commands/$CMD_ID" \
          -H "Authorization: Bearer $USER_TOKEN"
        echo -e "\n"
    fi
else
    echo -e "${YELLOW}Test 6 skipped: Set MINER_ID environment variable to test execution${NC}"
    echo "Example: export MINER_ID=550e8400-e29b-41d4-a716-446655440000"
fi

echo -e "\n${GREEN}=== Tests completed ===${NC}"
echo -e "\nTo run with custom parameters:"
echo -e "  export BASE_URL=http://localhost:8080"
echo -e "  export ADMIN_TOKEN=your-admin-jwt"
echo -e "  export USER_TOKEN=your-user-jwt"
echo -e "  export MINER_ID=your-miner-uuid"
echo -e "  export MINER_IP=192.168.1.100"
echo -e "  ./test-asic-templates.sh"
