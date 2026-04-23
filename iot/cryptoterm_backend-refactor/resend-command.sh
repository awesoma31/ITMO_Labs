#!/bin/bash

# Resend a command to the device
# Usage: ./resend-command.sh <cmd_id>

set -e

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8080}"
AUTH_TOKEN="${AUTH_TOKEN:-your-jwt-token}"
CMD_ID="${1:-}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Resend Command ===${NC}\n"

# Check if command ID is provided
if [ -z "$CMD_ID" ]; then
    echo -e "${RED}ERROR: Command ID is required${NC}"
    echo "Usage: $0 <cmd_id>"
    echo "Example: $0 cmd_abc123"
    exit 1
fi

# Check if token is set
if [ "$AUTH_TOKEN" = "your-jwt-token" ]; then
    echo -e "${RED}ERROR: Please set AUTH_TOKEN environment variable${NC}"
    echo "Example: export AUTH_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    exit 1
fi

echo -e "${BLUE}Resending command: ${CMD_ID}${NC}\n"

# Resend command via API
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/commands/$CMD_ID/resend" \
    -H "Authorization: Bearer $AUTH_TOKEN")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo -e "${GREEN}✓ Command resent successfully${NC}\n"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}✗ Failed to resend command (HTTP $HTTP_CODE)${NC}"
    echo "$BODY"
    exit 1
fi

echo -e "\n${GREEN}=== Done ===${NC}"
