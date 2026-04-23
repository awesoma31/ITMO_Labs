#!/bin/bash
set -e

# Configuration
BACKUP_DIR="${BACKUP_DIR:-/var/backups/cryptoterm}"
RETENTION_DAYS="${RETENTION_DAYS:-7}"
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")

# Database connection details
POSTGRES_HOST="${DB_HOST:-timescaledb}"
POSTGRES_PORT="${DB_PORT:-5432}"
POSTGRES_DB="${DB_NAME:-cryptoterm}"
POSTGRES_USER="${DB_USER:-cryptoterm}"
POSTGRES_PASSWORD="${DB_PASSWORD:-cryptoterm}"

MONGO_HOST="${MONGODB_HOST:-mongodb}"
MONGO_PORT="${MONGODB_PORT:-27017}"
MONGO_DB="${MONGODB_DATABASE:-cryptoterm}"
MONGO_USER="${MONGODB_USERNAME:-cryptoterm}"
MONGO_PASSWORD="${MONGODB_PASSWORD:-cryptoterm}"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

echo "=================================================="
echo "Starting backup process at $(date)"
echo "=================================================="

# Backup PostgreSQL
echo "Backing up PostgreSQL database: $POSTGRES_DB"
POSTGRES_BACKUP_FILE="$BACKUP_DIR/postgres_${POSTGRES_DB}_${TIMESTAMP}.dump"

PGPASSWORD="$POSTGRES_PASSWORD" pg_dump \
    -h "$POSTGRES_HOST" \
    -p "$POSTGRES_PORT" \
    -U "$POSTGRES_USER" \
    -F c \
    -b \
    -v \
    -f "$POSTGRES_BACKUP_FILE" \
    "$POSTGRES_DB"

if [ $? -eq 0 ]; then
    BACKUP_SIZE=$(du -h "$POSTGRES_BACKUP_FILE" | cut -f1)
    echo "✓ PostgreSQL backup completed successfully: $POSTGRES_BACKUP_FILE ($BACKUP_SIZE)"
else
    echo "✗ PostgreSQL backup failed"
    exit 1
fi

# Backup MongoDB
echo "Backing up MongoDB database: $MONGO_DB"
MONGO_BACKUP_DIR="$BACKUP_DIR/mongodb_${MONGO_DB}_${TIMESTAMP}"

mongodump \
    --host="$MONGO_HOST" \
    --port="$MONGO_PORT" \
    --username="$MONGO_USER" \
    --password="$MONGO_PASSWORD" \
    --authenticationDatabase=admin \
    --db="$MONGO_DB" \
    --out="$MONGO_BACKUP_DIR" \
    --gzip

if [ $? -eq 0 ]; then
    BACKUP_SIZE=$(du -sh "$MONGO_BACKUP_DIR" | cut -f1)
    echo "✓ MongoDB backup completed successfully: $MONGO_BACKUP_DIR ($BACKUP_SIZE)"
else
    echo "✗ MongoDB backup failed"
    exit 1
fi

# Cleanup old backups
echo ""
echo "Cleaning up backups older than $RETENTION_DAYS days..."

# Remove old PostgreSQL backups
DELETED_COUNT=0
while IFS= read -r -d '' file; do
    rm -f "$file"
    echo "  Deleted: $file"
    ((DELETED_COUNT++))
done < <(find "$BACKUP_DIR" -name "postgres_*.dump" -type f -mtime +$RETENTION_DAYS -print0 2>/dev/null || true)

# Remove old MongoDB backup directories
while IFS= read -r -d '' dir; do
    rm -rf "$dir"
    echo "  Deleted: $dir"
    ((DELETED_COUNT++))
done < <(find "$BACKUP_DIR" -name "mongodb_*" -type d -mtime +$RETENTION_DAYS -print0 2>/dev/null || true)

if [ $DELETED_COUNT -eq 0 ]; then
    echo "  No old backups to clean up"
else
    echo "  Cleaned up $DELETED_COUNT old backup(s)"
fi

# Summary
echo "=================================================="
echo "Backup process completed successfully at $(date)"
echo "=================================================="
echo ""
echo "Current backups in $BACKUP_DIR:"
echo ""
echo "PostgreSQL backups:"
find "$BACKUP_DIR" -name "postgres_*.dump" -type f -exec ls -lh {} \; 2>/dev/null | awk '{print "  " $9 " (" $5 ")"}' || echo "  No backups found"
echo ""
echo "MongoDB backups:"
find "$BACKUP_DIR" -name "mongodb_*" -type d -exec du -sh {} \; 2>/dev/null | awk '{print "  " $2 " (" $1 ")"}' || echo "  No backups found"
echo ""
