# Quick Fix - Add Enabled Column

## The Problem
Your application can't login because the database is missing the `enabled` column.

---

## Solution (Choose ONE method)

### Method 1: Using psql Command Line

**Step 1:** Open a terminal and run:

```bash
psql -h localhost -p 5432 -U postgres -d hotelManagement
```

**Step 2:** When prompted, enter your PostgreSQL password

**Step 3:** Copy and paste this SQL:

```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;
UPDATE users SET enabled = true WHERE enabled IS NULL;
\q
```

**Step 4:** Restart your Spring Boot application

---

### Method 2: Using pgAdmin (If you have it installed)

1. Open pgAdmin
2. Connect to your `hotelManagement` database
3. Click on **Tools** → **Query Tool**
4. Paste this SQL:

```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;
UPDATE users SET enabled = true WHERE enabled IS NULL;
```

5. Click the **Execute** button (▶️ or F5)
6. Restart your Spring Boot application

---

### Method 3: Using DBeaver (If you have it installed)

1. Open DBeaver
2. Connect to your `hotelManagement` database
3. Click **SQL Editor** → **New SQL Script**
4. Paste this SQL:

```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;
UPDATE users SET enabled = true WHERE enabled IS NULL;
```

5. Press Ctrl+Enter (or Cmd+Enter on Mac) to execute
6. Restart your Spring Boot application

---

### Method 4: One-Line Command (If your password is 52020278522)

```bash
PGPASSWORD=52020278522 psql -h localhost -p 5432 -U postgres -d hotelManagement -c "ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;"
```

If your password is different, replace `52020278522` with your actual PostgreSQL password.

---

## How to Find Your PostgreSQL Password

Check one of these locations:

1. **application.yaml** - Look at line 14:
   ```yaml
   password: ${DB_PASSWORD:52020278522}
   ```
   The password after the colon (52020278522) is the default.

2. **Environment variable** - Check if `DB_PASSWORD` is set:
   ```bash
   echo $DB_PASSWORD
   ```

3. **PostgreSQL config** - If you set a password during PostgreSQL installation

---

## Verify It Worked

After running the SQL, check that the column was added:

```sql
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'users' AND column_name = 'enabled';
```

You should see:
```
 column_name | data_type 
-------------+-----------
 enabled     | boolean
```

---

## Then Restart Your Application

After the migration is complete, restart your Spring Boot application. The login error should be gone!

---

## Still Having Issues?

If you're still getting errors, please share:
1. Your PostgreSQL password (or confirm it's 52020278522)
2. Which method you tried
3. Any error messages you received

I'll help you get it working!
