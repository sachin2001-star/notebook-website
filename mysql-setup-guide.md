# MySQL Database Setup Guide

## Prerequisites
- MySQL Server installed and running
- MySQL root access or a user with database creation privileges

## Database Configuration

### 1. MySQL Server Setup
Make sure your MySQL server is running on port 3306.

### 2. Application Configuration
The application is configured to use these MySQL settings:
- **Host:** localhost
- **Port:** 3306
- **Database:** notebook_business (will be created automatically)
- **Username:** root
- **Password:** (your MySQL root password)

### 3. Update application.properties (if needed)
If your MySQL setup is different, update these lines in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/notebook_business?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_ROOT_PASSWORD
```

### 4. Manual Database Creation (Optional)
If you prefer to create the database manually:

```sql
CREATE DATABASE notebook_business;
USE notebook_business;
```

The tables will be created automatically by Hibernate when the application starts.

## Testing the Setup

### 1. Test Database Connection
Visit: http://localhost:8081/test-db

Expected result: "Database test successful! Test submission saved with ID: X. Total submissions: Y"

### 2. Check Database Tables
After running the application, you should see these tables in your MySQL database:
- `contact_submissions` - stores contact form submissions
- `visitor_logs` - stores visitor analytics data

### 3. Verify Data Storage
Submit a contact form and then check the database:

```sql
SELECT * FROM notebook_business.contact_submissions;
```

## Troubleshooting

### Common Issues:

1. **Connection Refused**
   - Make sure MySQL server is running
   - Check if port 3306 is accessible

2. **Access Denied**
   - Verify username and password in application.properties
   - Make sure the user has proper privileges

3. **Database Not Found**
   - The application will create the database automatically
   - If it fails, create it manually: `CREATE DATABASE notebook_business;`

4. **Table Creation Issues**
   - Make sure the user has CREATE TABLE privileges
   - Check MySQL logs for specific error messages

### MySQL Commands for Verification:

```sql
-- Check if database exists
SHOW DATABASES;

-- Use the database
USE notebook_business;

-- Check tables
SHOW TABLES;

-- Check contact submissions
SELECT * FROM contact_submissions;

-- Check visitor logs
SELECT * FROM visitor_logs;
```

## Application Features Now Working with MySQL:

✅ **Contact Form Submissions** - Saved to MySQL database  
✅ **Admin Panel** - Displays data from MySQL  
✅ **File Uploads** - Stored as LONGBLOB in MySQL  
✅ **Visitor Analytics** - Stored in MySQL  
✅ **Error Pages** - Fixed and working  
✅ **Admin Login** - Working with admin/admin123  

## Next Steps:

1. Open `test-all-functionality.html` in your browser
2. Follow the testing steps to verify everything works
3. Submit a contact form to test data persistence
4. Login to admin panel to view saved data
5. Check MySQL database to confirm data is being saved

Your application is now fully configured to use MySQL database! 