#!/bin/bash

# Migration script to add enabled column to users table
# This script will connect to your PostgreSQL database and run the migration

echo "=========================================="
echo "Hotel Reservation System - Database Migration"
echo "Adding 'enabled' column to users table"
echo "=========================================="
echo ""

# Database configuration from application.yaml
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="hotelManagement"
DB_USER="postgres"

# Run the migration
echo "Connecting to database: $DB_NAME"
echo "User: $DB_USER"
echo ""

# Execute the migration SQL file
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f migration.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ Migration completed successfully!"
    echo "=========================================="
    echo ""
    echo "The 'enabled' column has been added to the users table."
    echo "All existing users have been set to enabled=true."
    echo ""
    echo "You can now restart your application."
else
    echo ""
    echo "=========================================="
    echo "❌ Migration failed!"
    echo "=========================================="
    echo ""
    echo "Please check the error messages above and try again."
fi
