IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'scl_dev')
BEGIN
    CREATE DATABASE scl_dev;
END
GO
