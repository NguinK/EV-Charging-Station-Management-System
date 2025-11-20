-- Migration to remove Vehicle entity and move battery/vehicle info to users table
-- This script consolidates vehicle and battery information directly into the EVDriver (users) table

-- Step 1: Add new columns to users table for battery and vehicle information
ALTER TABLE users ADD COLUMN battery_capacity_kwh FLOAT NULL;
ALTER TABLE users ADD COLUMN manufacturer VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN model VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN license_plate VARCHAR(50) NULL;
ALTER TABLE users ADD COLUMN connector_type VARCHAR(20) NULL;

-- Step 2: Add column comments for documentation
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Total battery capacity of the vehicle in kilowatt-hours (kWh), NOT a percentage', 
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'users',
    @level2type = N'COLUMN', @level2name = N'battery_capacity_kwh';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Vehicle manufacturer (e.g., Tesla, VinFast, BYD)', 
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'users',
    @level2type = N'COLUMN', @level2name = N'manufacturer';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Vehicle model (e.g., Model 3, VF8)', 
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'users',
    @level2type = N'COLUMN', @level2name = N'model';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Vehicle license plate number', 
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'users',
    @level2type = N'COLUMN', @level2name = N'license_plate';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Type of charging connector (CCS, CHADEMO, AC)', 
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'users',
    @level2type = N'COLUMN', @level2name = N'connector_type';

-- Step 3: Migrate data from vehicles table to users table (if vehicles table exists and has data)
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'vehicles')
BEGIN
    -- Update users with vehicle data where there's a matching account_id
    UPDATE u
    SET 
        u.battery_capacity_kwh = v.batteryCapacity,
        u.manufacturer = v.manufacturer,
        u.model = v.model,
        u.license_plate = v.licensePlate,
        u.connector_type = CAST(v.connectorType AS VARCHAR(20))
    FROM users u
    INNER JOIN vehicles v ON u.account_id = v.account_id;
END;

-- Step 4: Drop foreign key constraint from charging_sessions table (if it exists)
IF EXISTS (
    SELECT * FROM sys.foreign_keys 
    WHERE name = 'FK_charging_sessions_vehicle' 
    OR parent_object_id = OBJECT_ID('charging_sessions') 
    AND referenced_object_id = OBJECT_ID('vehicles')
)
BEGIN
    DECLARE @constraintName NVARCHAR(200);
    SELECT @constraintName = name 
    FROM sys.foreign_keys 
    WHERE parent_object_id = OBJECT_ID('charging_sessions') 
    AND referenced_object_id = OBJECT_ID('vehicles');
    
    IF @constraintName IS NOT NULL
    BEGIN
        DECLARE @sql NVARCHAR(MAX) = 'ALTER TABLE charging_sessions DROP CONSTRAINT ' + @constraintName;
        EXEC sp_executesql @sql;
    END;
END;

-- Step 5: Drop vehicle_id column from charging_sessions table (if it exists)
IF EXISTS (
    SELECT * FROM sys.columns 
    WHERE object_id = OBJECT_ID('charging_sessions') 
    AND name = 'vehicle_id'
)
BEGIN
    ALTER TABLE charging_sessions DROP COLUMN vehicle_id;
END;

-- Step 6: Drop the vehicles table (if it exists)
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'vehicles')
BEGIN
    DROP TABLE vehicles;
END;

-- Step 7: Add check constraints for data validation
IF NOT EXISTS (
    SELECT * FROM sys.check_constraints 
    WHERE name = 'CHK_battery_capacity_positive' 
    AND parent_object_id = OBJECT_ID('users')
)
BEGIN
    ALTER TABLE users ADD CONSTRAINT CHK_battery_capacity_positive 
    CHECK (battery_capacity_kwh IS NULL OR battery_capacity_kwh > 0);
END;

-- Step 8: Add check constraints for SoC fields in charging_sessions (if not already present)
IF NOT EXISTS (
    SELECT * FROM sys.check_constraints 
    WHERE name = 'CHK_start_soc_range' 
    AND parent_object_id = OBJECT_ID('charging_sessions')
)
BEGIN
    ALTER TABLE charging_sessions ADD CONSTRAINT CHK_start_soc_range 
    CHECK (start_soc IS NULL OR (start_soc >= 0 AND start_soc <= 100));
END;

IF NOT EXISTS (
    SELECT * FROM sys.check_constraints 
    WHERE name = 'CHK_end_soc_range' 
    AND parent_object_id = OBJECT_ID('charging_sessions')
)
BEGIN
    ALTER TABLE charging_sessions ADD CONSTRAINT CHK_end_soc_range 
    CHECK (end_soc IS NULL OR (end_soc >= 0 AND end_soc <= 100));
END;

-- Step 9: Add column comments for SoC fields in charging_sessions
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Battery state of charge in percentage (0-100) at the start of charging, NOT kWh', 
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'charging_sessions',
    @level2type = N'COLUMN', @level2name = N'start_soc';

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Battery state of charge in percentage (0-100) at the end of charging, NOT kWh', 
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'charging_sessions',
    @level2type = N'COLUMN', @level2name = N'end_soc';
