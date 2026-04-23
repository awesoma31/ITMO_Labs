#!/bin/bash

# List ASIC commands from MongoDB
# Usage: ./list-commands.sh [device_id] [status]

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
CYAN='\033[0;36m'
NC='\033[0m' # No Color

DEVICE_ID="${1:-}"
STATUS="${2:-}"

echo -e "${YELLOW}=== ASIC Commands in MongoDB ===${NC}\n"

# Build MongoDB query
if [ -n "$DEVICE_ID" ] && [ -n "$STATUS" ]; then
    QUERY="{device_id: \"$DEVICE_ID\", status: \"$STATUS\"}"
    echo -e "${BLUE}Filtering by device: $DEVICE_ID, status: $STATUS${NC}\n"
elif [ -n "$DEVICE_ID" ]; then
    QUERY="{device_id: \"$DEVICE_ID\"}"
    echo -e "${BLUE}Filtering by device: $DEVICE_ID${NC}\n"
elif [ -n "$STATUS" ]; then
    QUERY="{status: \"$STATUS\"}"
    echo -e "${BLUE}Filtering by status: $STATUS${NC}\n"
else
    QUERY="{}"
    echo -e "${BLUE}Showing all commands${NC}\n"
fi

# Connect to MongoDB and query commands
docker exec -it cryptoterm_mongodb mongosh \
    --host "$MONGO_HOST" \
    --port "$MONGO_PORT" \
    --username "$MONGO_USER" \
    --password "$MONGO_PASS" \
    --authenticationDatabase admin \
    "$MONGO_DB" \
    --eval "
        db.asic_commands.find($QUERY).sort({created_at: -1}).forEach(function(cmd) {
            print('');
            print('═══════════════════════════════════════════════════════');
            print('Command ID: ' + cmd._id);
            print('Device ID: ' + cmd.device_id);
            print('Status: ' + cmd.status);
            print('Created: ' + cmd.created_at);
            if (cmd.executed_at) {
                print('Executed: ' + cmd.executed_at);
            }
            print('ASIC: ' + cmd.asic.scheme + '://' + cmd.asic.ip + ':' + cmd.asic.port + ' (fw: ' + cmd.asic.firmware + ')');
            print('Steps: ' + cmd.steps.length);
            cmd.steps.forEach(function(step, idx) {
                print('  ' + (idx+1) + '. ' + step.id + ' - ' + step.request.method + ' ' + step.request.path);
            });
            if (cmd.result) {
                print('Result: ' + cmd.result.status);
                if (cmd.result.failedStep) {
                    print('Failed Step: ' + cmd.result.failedStep);
                }
            }
        });
        print('');
        print('═══════════════════════════════════════════════════════');
        var count = db.asic_commands.countDocuments($QUERY);
        print('Total commands: ' + count);
    "

echo -e "\n${GREEN}=== Done ===${NC}"
echo -e "\nUsage:"
echo -e "  ./list-commands.sh                           # List all commands"
echo -e "  ./list-commands.sh <device_id>               # List commands for device"
echo -e "  ./list-commands.sh <device_id> <status>      # List commands for device with status"
echo -e "\nAvailable statuses: PENDING, SENT, EXECUTING, SUCCESS, FAILED, CANCELLED"
