# Database Migration Guide

## Issue
After adding the security fix for the `enabled` column in the User model, the database needs to be updated to include this new column.

**Error:**
```
ERROR: column u1_0.enabled does not exist
```

## Solution

You have **3 options** to fix this:

---

## Option 1: Run the Automated Migration Script (Recommended)

### Step 1: Run the migration script

```bash
cd /Users/raminsafarov/IdeaProjects/HotelReservation
DB_PASSWORD=52020278522 ./run-migration.sh
```

### Step 2: Restart your application

That's it! The script will automatically add the `enabled` column to your users table.

---

## Option 2: Run SQL Manually via psql

### Step 1: Connect to your database

```bash
psql -h localhost -p 5432 -U postgres -d hotelManagement
```

### Step 2: Run the migration SQL

```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;
UPDATE users SET enabled = true WHERE enabled IS NULL;
```

### Step 3: Verify the column was added

```sql
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name = 'enabled';
```

You should see:
```
 column_name | data_type | is_nullable | column_default 
-------------+-----------+-------------+----------------
 enabled     | boolean   | NO          | true
```

### Step 4: Exit and restart your application

```sql
\q
```

---

## Option 3: Use a Database Tool (pgAdmin, DBeaver, etc.)

### Step 1: Open your database tool and connect to:
- Host: localhost
- Port: 5432
- Database: hotelManagement
- User: postgres
- Password: 52020278522

### Step 2: Run this SQL:

```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;
UPDATE users SET enabled = true WHERE enabled IS NULL;
```

### Step 3: Restart your application

---

## Verification

After running the migration, verify it worked by:

1. Starting your Spring Boot application
2. Trying to login - you should NOT see the "column enabled does not exist" error
3. Check the application logs - no database errors

---

## What This Migration Does

1. **Adds the `enabled` column** to the `users` table
   - Type: BOOLEAN
   - Default: true
   - NOT NULL

2. **Sets all existing users to enabled = true**
   - Ensures backward compatibility
   - No users are locked out

3. **Allows future account management**
   - Admins can now enable/disable user accounts
   - Provides better security control

---

## Rollback (If Needed)

If you need to rollback this migration:

```sql
ALTER TABLE users DROP COLUMN IF EXISTS enabled;
```

Then revert the User.java file to remove the enabled field.

---

## Important Notes

- ✅ All existing users will remain enabled (default: true)
- ✅ No data is lost during migration
- ✅ The migration is idempotent (safe to run multiple times)
- ✅ Uses `IF NOT EXISTS` to prevent errors if column already exists

---

## Troubleshooting

### Error: "permission denied"
```bash
# Make sure the script is executable
chmod +x run-migration.sh
```

### Error: "psql: command not found"
You need to install PostgreSQL client tools or use Option 2/3 instead.

### Error: "password authentication failed"
Check your database password in application.yaml or use:
```bash
DB_PASSWORD=your_actual_password ./run-migration.sh
```

### Error: "database does not exist"
Make sure your PostgreSQL server is running and the database exists:
```bash
psql -h localhost -p 5432 -U postgres -l
```

---

## After Migration

Once the migration is complete and your application starts successfully:

1. ✅ Login should work normally
2. ✅ All security features will be active
3. ✅ You can now enable/disable user accounts (future feature)

Your security system is now fully operational! 🎉
