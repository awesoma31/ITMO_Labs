#!/bin/bash

# Rebuild and restart backend container
# Usage: ./rebuild-backend.sh

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Rebuilding Backend Container ===${NC}\n"

echo -e "${BLUE}1. Stopping backend container...${NC}"
docker-compose stop backend

echo -e "${BLUE}2. Removing old backend container...${NC}"
docker-compose rm -f backend

echo -e "${BLUE}3. Building new backend image (this may take a few minutes)...${NC}"
docker-compose build --no-cache backend

echo -e "${BLUE}4. Starting backend container...${NC}"
docker-compose up -d backend

echo -e "\n${YELLOW}Waiting for backend to start (30 seconds)...${NC}"
sleep 30

echo -e "\n${BLUE}5. Checking backend logs...${NC}"
docker logs cryptoterm_backend --tail 20

echo -e "\n${GREEN}=== Backend Rebuild Complete ===${NC}"
echo -e "\nRun health check:"
echo -e "  ${BLUE}./check-backend.sh${NC}"
