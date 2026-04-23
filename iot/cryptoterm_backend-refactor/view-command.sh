#!/bin/bash

# View detailed information about a specific command
# Usage: ./view-command.sh <cmd_id>

set -e

# Configuration
MONGO_HOST="${MONGO_HOST:-localhost}"
MONGO_PORT="${MONGO_PORT:-27017}"
MONGO_USER="${MONGO_USER:-cryptoterm}"
MONGO_PASS="${MONGO_PASS:-cryptoterm}"
MONGO_DB="${MONGO_DB:-cryptoterm}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

CMD_ID="${1:-}"

if [ -z "$CMD_ID" ]; then
    echo -e "${RED}ERROR: Command ID is required${NC}"
    echo "Usage: $0 <cmd_id>"
    echo "Example: $0 cmd_abc123"
    exit 1
fi

echo -e "${YELLOW}=== Command Details: $CMD_ID ===${NC}\n"

# Connect to MongoDB and get command details
docker exec -it cryptoterm_mongodb mongosh \
    --host "$MONGO_HOST" \
    --port "$MONGO_PORT" \
    --username "$MONGO_USER" \
    --password "$MONGO_PASS" \
    --authenticationDatabase admin \
    "$MONGO_DB" \
    --eval "
        var cmd = db.asic_commands.findOne({_id: \"$CMD_ID\"});
        if (!cmd) {
            print('ERROR: Command not found');
        } else {
            printjson(cmd);
        }
    " | python3 -m json.tool 2>/dev/null || docker exec -it cryptoterm_mongodb mongosh \
    --host "$MONGO_HOST" \
    --port "$MONGO_PORT" \
    --username "$MONGO_USER" \
    --password "$MONGO_PASS" \
    --authenticationDatabase admin \
    "$MONGO_DB" \
    --eval "
        var cmd = db.asic_commands.findOne({_id: \"$CMD_ID\"});
        if (!cmd) {
            print('ERROR: Command not found');
        } else {
            printjson(cmd);
        }
    "

echo -e "\n${GREEN}=== Done ===${NC}"
