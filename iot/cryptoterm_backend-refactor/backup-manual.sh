#!/bin/bash
set -e

echo "Starting manual backup at $(date)"

# Run the backup script
./backup.sh

echo "Manual backup completed at $(date)"
