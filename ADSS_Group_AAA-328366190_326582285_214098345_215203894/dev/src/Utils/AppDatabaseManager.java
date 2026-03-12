package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class AppDatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:MainApp.db";
    private static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");

                // site table: address is primary key
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS site (
                      address      TEXT PRIMARY KEY,
                      name         TEXT NOT NULL,
                      contact_name TEXT NOT NULL,
                      phone        TEXT NOT NULL,
                      zone         TEXT NOT NULL DEFAULT 'CENTER'
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee (
                      id               INTEGER PRIMARY KEY,
                      name             TEXT NOT NULL,
                      bank_account     TEXT NOT NULL,
                      salary           REAL NOT NULL,
                      employment_terms TEXT NOT NULL,
                      start_date       TEXT NOT NULL,
                      is_active        INTEGER NOT NULL CHECK(is_active IN (0,1)),
                      site_address        TEXT NOT NULL,
                      FOREIGN KEY(site_address) REFERENCES site(address)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS role (
                      id   INTEGER PRIMARY KEY,
                      name TEXT NOT NULL UNIQUE
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee_role (
                      employee_id INTEGER,
                      role_id     INTEGER,
                      PRIMARY KEY (employee_id, role_id),
                      FOREIGN KEY(employee_id) REFERENCES employee(id),
                      FOREIGN KEY(role_id) REFERENCES role(id)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift (
                      id             INTEGER PRIMARY KEY,
                      date           TEXT NOT NULL,
                      shift_type     TEXT NOT NULL,
                      shift_manager  INTEGER NOT NULL,
                      site_address      TEXT NOT NULL,
                      FOREIGN KEY(site_address) REFERENCES site(address),
                      FOREIGN KEY(shift_manager) REFERENCES employee(id)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_required_role (
                      shift_id INTEGER,
                      role_id  INTEGER,
                      PRIMARY KEY (shift_id, role_id),
                      FOREIGN KEY(shift_id) REFERENCES shift(id),
                      FOREIGN KEY(role_id)  REFERENCES role(id)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_assignment (
                      shift_id    INTEGER,
                      employee_id INTEGER,
                      PRIMARY KEY (shift_id, employee_id),
                      FOREIGN KEY(shift_id) REFERENCES shift(id),
                      FOREIGN KEY(employee_id) REFERENCES employee(id)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee_availability (
                      employee_id INTEGER,
                      date        TEXT,
                      shift_type  TEXT,
                      PRIMARY KEY (employee_id, date, shift_type),
                      FOREIGN KEY(employee_id) REFERENCES employee(id)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Trucks (
                      licensePlate    TEXT PRIMARY KEY,
                      netWeight       DOUBLE NOT NULL,
                      maxWeight       DOUBLE NOT NULL,
                      requiredLicense INTEGER NOT NULL
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Truck_TimeIntervals (
                      truck_license TEXT NOT NULL,
                      datetime1     TEXT NOT NULL,
                      datetime2     TEXT NOT NULL,
                      PRIMARY KEY (truck_license, datetime1, datetime2),
                      FOREIGN KEY (truck_license) REFERENCES Trucks(licensePlate)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Driver_License (
                      employee_id INTEGER NOT NULL,
                      license     INTEGER NOT NULL,
                      PRIMARY KEY (employee_id, license),
                      FOREIGN KEY (employee_id) REFERENCES employee(id)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Transportations (
                      id                  INTEGER PRIMARY KEY,
                      driver_id           INTEGER  NULL,
                      truck_licensePlate  TEXT  NULL,
                      source_address      TEXT NOT NULL,
                      destination_address TEXT NOT NULL,
                      source_time         TEXT NOT NULL,
                      destination_time    TEXT NOT NULL,
                      comment             TEXT,
                      FOREIGN KEY (driver_id) REFERENCES employee(id),
                      FOREIGN KEY (truck_licensePlate) REFERENCES Trucks(licensePlate),
                      FOREIGN KEY (source_address) REFERENCES site(address),
                      FOREIGN KEY (destination_address) REFERENCES site(address)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Transportation_Stops (
                      transportation_id INTEGER NOT NULL,
                      address           TEXT NOT NULL,
                      arrival_time      TEXT NOT NULL,
                      PRIMARY KEY (transportation_id, address),
                      FOREIGN KEY (transportation_id) REFERENCES Transportations(id),
                      FOREIGN KEY (address) REFERENCES site(address)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Item_At_Stop (
                      transportation_id INTEGER NOT NULL,
                      stop_address      TEXT NOT NULL,
                      item_name         TEXT NOT NULL,
                      pickup            BOOLEAN NOT NULL,
                      item_weight       DOUBLE NOT NULL,
                      PRIMARY KEY (transportation_id, stop_address, item_name, pickup),
                      FOREIGN KEY (transportation_id) REFERENCES Transportations(id),
                      FOREIGN KEY (stop_address) REFERENCES site(address)
                    );
                """);
            }

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void insertInitialData() {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");

            // Roles
            st.executeUpdate("""
            INSERT OR IGNORE INTO role (id, name)
            VALUES (1, 'Driver'), (2, 'Cashier'), (3, 'Manager');
        """);

            // Sites
            st.executeUpdate("""
            INSERT OR IGNORE INTO site (address, name, contact_name, phone, zone)
            VALUES
              ('123 Center St', 'Main WH', 'Alice', '050-1111111', 'CENTER'),
              ('456 North Ave', 'Secondary WH', 'Bob', '050-2222222', 'NORTH'),
              ('789 South Blvd', 'Downtown WH', 'Charlie', '050-3333333', 'SOUTH');
        """);

            // Employees
            st.executeUpdate("""
            INSERT OR IGNORE INTO employee (id, name, bank_account, salary, employment_terms, start_date, is_active, site_address)
            VALUES
              (1, 'John Driver', '1111', 5000, 'Full-Time', '2022-01-01', 1, '123 Center St'),
              (2, 'Jane Cashier', '2222', 4000, 'Part-Time', '2023-02-01', 1, '123 Center St'),
              (3, 'Mark Manager', '3333', 6000, 'Full-Time', '2020-05-01', 1, '456 North Ave'),
              (4, 'Lisa Driver', '4444', 5200, 'Full-Time', '2021-03-15', 1, '789 South Blvd'),
              (5, 'Eli TestDriver', '5555', 5100, 'Full-Time', '2022-06-01', 1, '123 Center St');
        """);

            // Employee Roles
            st.executeUpdate("""
            INSERT OR IGNORE INTO employee_role (employee_id, role_id)
            VALUES (1, 1), (2, 2), (3, 3), (4, 1), (5, 1);
        """);

            // Driver Licenses
            st.executeUpdate("""
            INSERT OR IGNORE INTO Driver_License (employee_id, license)
            VALUES (1, 3), (4, 3), (5, 3);
        """);

            // Trucks
            st.executeUpdate("""
            INSERT OR IGNORE INTO Trucks (licensePlate, netWeight, maxWeight, requiredLicense)
            VALUES ('ABC-123', 5000, 15000, 3), 
                   ('XYZ-789', 4000, 12000, 3), 
                   ('DEF-456', 4500, 14000, 3);
        """);

            // Truck Time Intervals
            st.executeUpdate("""
            INSERT OR IGNORE INTO Truck_TimeIntervals (truck_license, datetime1, datetime2)
            VALUES 
              ('ABC-123', '2025-06-03T08:00', '2025-06-03T18:00'),
              ('XYZ-789', '2025-06-04T09:00', '2025-06-04T17:00'),
              ('DEF-456', '2025-06-10T08:00:00', '2025-06-10T14:00:00'),
              ('DEF-456', '2025-06-11T08:00:00', '2025-06-11T14:00:00');
        """);

            // Shifts (match source site & transportation time)
            st.executeUpdate("""
            INSERT OR IGNORE INTO shift (id, date, shift_type, shift_manager, site_address)
            VALUES 
              (1, '2025-06-03', 'MORNING', 3, '123 Center St'),
              (2, '2025-06-04', 'MORNING', 3, '456 North Ave'),
              (3, '2025-06-10', 'MORNING', 3, '123 Center St'),
              (4, '2025-06-11', 'MORNING', 3, '123 Center St');
        """);

            // Required Roles per shift
            st.executeUpdate("""
            INSERT OR IGNORE INTO shift_required_role (shift_id, role_id)
            VALUES (1, 1), (2, 1), (3, 1), (4, 1);
        """);

            // Shift Assignments (assign drivers)
            st.executeUpdate("""
            INSERT OR IGNORE INTO shift_assignment (shift_id, employee_id)
            VALUES 
              (1, 1), (2, 4), (3, 5), (4, 5);
        """);

            // Employee Availability (match shift dates)
            st.executeUpdate("""
            INSERT OR IGNORE INTO employee_availability (employee_id, date, shift_type)
            VALUES 
              (1, '2025-06-03', 'MORNING'), 
              (4, '2025-06-04', 'MORNING'),
              (5, '2025-06-10', 'MORNING'), 
              (5, '2025-06-11', 'MORNING');
        """);

            // Transportations
            st.executeUpdate("""
            INSERT OR IGNORE INTO Transportations
            (id, driver_id, truck_licensePlate, source_address, destination_address, source_time, destination_time, comment)
            VALUES
              (1, 1, 'ABC-123', '123 Center St', '456 North Ave', '2025-06-03T10:00', '2025-06-03T15:00', 'Shipment A'),
              (2, 4, 'XYZ-789', '456 North Ave', '789 South Blvd', '2025-06-04T11:00', '2025-06-04T16:00', 'Shipment B');
        """);

            // Stops
            st.executeUpdate("""
            INSERT OR IGNORE INTO Transportation_Stops (transportation_id, address, arrival_time)
            VALUES
              (1, '123 Center St', '2025-06-03T10:00'),
              (1, '456 North Ave', '2025-06-03T15:00'),
              (2, '456 North Ave', '2025-06-04T11:00'),
              (2, '789 South Blvd', '2025-06-04T16:00');
        """);

            // Items
            st.executeUpdate("""
            INSERT OR IGNORE INTO Item_At_Stop
            (transportation_id, stop_address, item_name, pickup, item_weight)
            VALUES
              (1, '123 Center St', 'Pallet A', 1, 300.0),
              (1, '456 North Ave', 'Pallet A', 0, 300.0),
              (2, '456 North Ave', 'Box B', 1, 200.0),
              (2, '789 South Blvd', 'Box B', 0, 200.0);
        """);

            System.out.println("✅ All initial data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to insert initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void clearAllData() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM Item_At_Stop;");
            st.execute("DELETE FROM Transportation_Stops;");
            st.execute("DELETE FROM Transportations;");
            st.execute("DELETE FROM Truck_TimeIntervals;");
            st.execute("DELETE FROM Trucks;");
            st.execute("DELETE FROM Driver_License;");
            st.execute("DELETE FROM shift_assignment;");
            st.execute("DELETE FROM shift_required_role;");
            st.execute("DELETE FROM shift;");
            st.execute("DELETE FROM employee_availability;");
            st.execute("DELETE FROM employee_role;");
            st.execute("DELETE FROM employee;");
            st.execute("DELETE FROM role;");
            st.execute("DELETE FROM site;");
        }
    }

    private AppDatabaseManager() {}

    public static Connection getConnection() {
        return conn;
    }

    public static void main(String[] args) {
        System.out.println("Do you want to load the database with sample data? (yes/no): ");
        String response = new java.util.Scanner(System.in).nextLine().trim().toLowerCase();
        boolean loadWithData = response.equals("yes") || response.equals("y");
        if(loadWithData)
        {
            insertInitialData();
        }
        else
        {
            try {
                clearAllData();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
