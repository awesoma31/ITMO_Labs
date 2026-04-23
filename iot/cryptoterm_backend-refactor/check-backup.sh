l#!/bin/bash

echo "=== Checking Backup System Status ==="
echo ""

echo "1. Checking if backup container is running..."
docker ps --filter "name=cryptoterm_backup" --format "table {{.Names}}\t{{.Status}}\t{{.Image}}"
echo ""

echo "2. Checking backup container logs (last 30 lines)..."
docker logs cryptoterm_backup --tail 30 2>&1 || echo "Container not running or not found"
echo ""

echo "3. Checking TimescaleDB logs for 'postgres' user errors (last 20 lines)..."
docker logs cryptoterm_timescaledb --tail 20 2>&1 | grep -i "user \"postgres\"" || echo "No 'postgres' user errors found in recent logs"
echo ""

echo "4. Listing backup files..."
docker exec cryptoterm_backup ls -lh /var/backups/cryptoterm/ 2>/dev/null || echo "Cannot access backup directory or container not running"
echo ""

echo "5. Testing database connections from backup container..."
echo "   Testing PostgreSQL connection..."
docker exec cryptoterm_backup sh -c 'export PGPASSWORD=$DB_PASSWORD; pg_isready -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME' 2>&1 || echo "PostgreSQL connection test failed"
echo ""

echo "   Testing MongoDB connection..."
docker exec cryptoterm_backup mongosh --host $MONGODB_HOST --port $MONGODB_PORT --username $MONGODB_USERNAME --password $MONGODB_PASSWORD --authenticationDatabase admin --eval "db.adminCommand('ping')" 2>&1 || echo "MongoDB connection test failed"
echo ""

echo "=== Check Complete ==="
