#!/bin/bash

# Create ASIC commands for Antminer S19 Pro Hydro power profiles
# Usage: ./create-antminer-s19-profiles.sh <device_id>

set -e

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8080}"
AUTH_TOKEN="${AUTH_TOKEN:-your-jwt-token}"
DEVICE_ID="${1:-}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Create Antminer S19 Pro Hydro Commands ===${NC}\n"

# Check if device ID is provided
if [ -z "$DEVICE_ID" ]; then
    echo -e "${RED}ERROR: Device ID is required${NC}"
    echo "Usage: $0 <device_id>"
    echo "Example: $0 rpi-001"
    exit 1
fi

# Check if token is set (optional, only needed for listing commands)
if [ "$AUTH_TOKEN" = "your-jwt-token" ]; then
    echo -e "${YELLOW}⚠ AUTH_TOKEN not set - will skip listing commands at the end${NC}"
    AUTH_TOKEN=""
fi

echo -e "${BLUE}Target device: ${DEVICE_ID}${NC}\n"

# Power profiles: watts_hashrate
# Format: "watts hashrate description"
PROFILES=(
    "3495 132 3495W_132TH"
    "3635 136 3635W_136TH"
    "3865 143 3865W_143TH"
    "4200 149 4200W_149TH"
    "4250 154 4250W_154TH"
    "4400 160 4400W_160TH"
    "4650 166 4650W_166TH"
    "4855 171 4855W_171TH"
    "5150 177 5150W_177TH"
    "5415 183 5415W_183TH"
    "5560 188 5560W_188TH"
    "5725 193 5725W_193TH"
    "6000 199 6000W_199TH"
    "6400 205 6400W_205TH"
    "6700 210 6700W_210TH"
    "6850 215 6850W_215TH"
    "7000 220 7000W_220TH"
    "7150 225 7150W_225TH"
    "7250 230 7250W_230TH"
    "7400 235 7400W_235TH"
    "7500 240 7500W_240TH"
    "7600 245 7600W_245TH"
    "7700 250 7700W_250TH"
)

SUCCESS_COUNT=0
FAIL_COUNT=0

# Function to create a command for a specific power profile
create_profile_command() {
    local watts=$1
    local hashrate=$2
    local name=$3
    local asic_ip="${ASIC_IP:-192.168.1.100}"
    local asic_port="${ASIC_PORT:-80}"
    local asic_scheme="${ASIC_SCHEME:-http}"
    
    echo -e "${BLUE}Creating command: ${name} (${watts}W, ${hashrate}TH/s)${NC}"
    
    # Create JSON payload for the command (matching CreateAsicCommandRequest DTO structure)
    JSON_PAYLOAD=$(cat <<EOF
{
  "deviceId": "${DEVICE_ID}",
  "asic": {
    "ip": "${asic_ip}",
    "port": ${asic_port},
    "scheme": "${asic_scheme}",
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
      "id": "set_profile_${watts}",
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
              "preset": "${watts}"
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
    
    # Build curl command with optional authorization header and timeout
    if [ -n "$AUTH_TOKEN" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" --max-time 30 --connect-timeout 10 -X POST "$BASE_URL/api/v1/commands" \
          -H "Authorization: Bearer $AUTH_TOKEN" \
          -H "Content-Type: application/json" \
          -d "$JSON_PAYLOAD" 2>&1)
    else
        RESPONSE=$(curl -s -w "\n%{http_code}" --max-time 30 --connect-timeout 10 -X POST "$BASE_URL/api/v1/commands" \
          -H "Content-Type: application/json" \
          -d "$JSON_PAYLOAD" 2>&1)
    fi
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    # Check if HTTP_CODE is a valid number
    if [[ "$HTTP_CODE" =~ ^[0-9]+$ ]]; then
        if [ "$HTTP_CODE" -eq 201 ] || [ "$HTTP_CODE" -eq 200 ]; then
            CMD_ID=$(echo "$BODY" | python3 -c "import sys, json; print(json.load(sys.stdin).get('cmdId', 'N/A'))" 2>/dev/null || echo "N/A")
            echo -e "${GREEN}✓ Success (Command ID: ${CMD_ID})${NC}"
            ((SUCCESS_COUNT++))
        else
            echo -e "${RED}✗ Failed (HTTP $HTTP_CODE)${NC}"
            echo "$BODY"
            ((FAIL_COUNT++))
        fi
    else
        echo -e "${RED}✗ Failed (Connection error or timeout)${NC}"
        echo "Response: $RESPONSE"
        ((FAIL_COUNT++))
    fi
    echo ""
}

# Create all profiles
echo -e "${YELLOW}Creating ${#PROFILES[@]} commands for power profiles...${NC}\n"

for profile in "${PROFILES[@]}"; do
    read -r watts hashrate name <<< "$profile"
    create_profile_command "$watts" "$hashrate" "$name"
    sleep 0.5  # Small delay to avoid overwhelming the server
done

# Summary
echo -e "${YELLOW}=== Summary ===${NC}"
echo -e "${GREEN}✓ Success: $SUCCESS_COUNT${NC}"
if [ $FAIL_COUNT -gt 0 ]; then
    echo -e "${RED}✗ Failed: $FAIL_COUNT${NC}"
fi
echo ""

# List all created commands for the device (only if AUTH_TOKEN is set)
if [ -n "$AUTH_TOKEN" ]; then
    echo -e "${YELLOW}=== Listing all commands for device ${DEVICE_ID} ===${NC}"
    curl -s -X GET "$BASE_URL/api/v1/commands/device/$DEVICE_ID" \
      -H "Authorization: Bearer $AUTH_TOKEN" | python3 -m json.tool || echo "Install python3 for formatted output"
else
    echo -e "${YELLOW}=== Skipping command listing (no AUTH_TOKEN) ===${NC}"
fi

echo -e "\n${GREEN}=== Completed ===${NC}"
echo -e "\nUsage:"
echo -e "  Run script: ./create-antminer-s19-profiles.sh <device_id>"
echo -e "\nOptional environment variables:"
echo -e "  - AUTH_TOKEN: JWT token for listing commands (optional, default: none)"
echo -e "  - ASIC_IP: ASIC miner IP address (default: 192.168.1.100)"
echo -e "  - ASIC_PORT: ASIC miner port (default: 80)"
echo -e "  - ASIC_SCHEME: http or https (default: http)"
echo -e "  - BASE_URL: Backend API URL (default: http://localhost:8080)"
echo -e "\nExamples:"
echo -e "  ./create-antminer-s19-profiles.sh rpi-001"
echo -e "  ASIC_IP=192.168.1.50 ./create-antminer-s19-profiles.sh rpi-001"
echo -e "  AUTH_TOKEN=your-token ./create-antminer-s19-profiles.sh rpi-001"
echo -e "\nList commands (requires AUTH_TOKEN):"
echo -e "  curl -H \"Authorization: Bearer \$AUTH_TOKEN\" $BASE_URL/api/v1/commands/device/<device_id>"
