#!/bin/bash

# Force rebuild backend container from scratch (no cache)
# Usage: ./force-rebuild-backend.sh

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Force Rebuilding Backend (No Cache) ===${NC}\n"

echo -e "${BLUE}1. Pulling latest changes from git...${NC}"
git pull origin refactor

echo -e "${BLUE}2. Stopping all containers...${NC}"
docker-compose stop

echo -e "${BLUE}3. Removing backend container and image...${NC}"
docker-compose rm -f backend
docker rmi cryptoterm_backend-backend 2>/dev/null || echo "Image already removed"
docker rmi $(docker images -q cryptoterm_backend_backend) 2>/dev/null || echo "No dangling images"

echo -e "${BLUE}4. Pruning Docker build cache...${NC}"
docker builder prune -f

echo -e "${BLUE}5. Building backend from scratch (this will take 3-5 minutes)...${NC}"
docker-compose build --no-cache --pull backend

echo -e "${BLUE}6. Starting all containers...${NC}"
docker-compose up -d

echo -e "\n${YELLOW}Waiting for backend to start (45 seconds)...${NC}"
for i in {45..1}; do
    echo -ne "\r${BLUE}Waiting ${i} seconds...${NC}"
    sleep 1
done
echo -e "\n"

echo -e "${BLUE}7. Checking backend status...${NC}"
docker-compose ps backend

echo -e "\n${BLUE}8. Checking backend logs...${NC}"
docker logs cryptoterm_backend --tail 30

echo -e "\n${GREEN}=== Backend Force Rebuild Complete ===${NC}"
echo -e "\nRun health check:"
echo -e "  ${BLUE}./check-backend.sh${NC}"
echo -e "\nIf still 403, check logs:"
echo -e "  ${BLUE}docker logs cryptoterm_backend | grep -i security${NC}"
