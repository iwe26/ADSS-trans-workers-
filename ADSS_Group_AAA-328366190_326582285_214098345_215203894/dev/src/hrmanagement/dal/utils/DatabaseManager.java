//package hrmanagement.dal.utils;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * Singleton-style manager that initializes the HR module database schema
// * and provides a shared Connection.
// */
//public final class DatabaseManager {
//    private static final String DB_URL = "jdbc:sqlite:HRModule.db";
//    private static Connection conn;
//
//    static {
//        try {
//            Class.forName("org.sqlite.JDBC");
//            conn = DriverManager.getConnection(DB_URL);
//            System.out.println("Connected to SQLite at " + DB_URL);
//
//            try (Statement st = conn.createStatement()) {
//                // Sites (branches and transport sites)
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS site (
//                      name         TEXT    PRIMARY KEY,
//                      address      TEXT    NOT NULL,
//                      contact_name TEXT    NOT NULL,
//                      phone        TEXT    NOT NULL,
//                      zone         TEXT    NOT NULL DEFAULT 'CENTER'
//                    );
//                """);
//
//
//                // If the table existed without 'zone', add it now
//                try {
//                    st.executeUpdate("""
//                    ALTER TABLE site
//                      ADD COLUMN zone TEXT NOT NULL DEFAULT 'CENTER';
//                """);
//                    System.out.println("Added 'zone' column to existing site table.");
//                } catch (SQLException e) {
//                    // SQLite will throw if column already exists—ignore
//                }
//                try
//                {
//                    // change any old ZONE0 values to CENTER
//                    st.executeUpdate("UPDATE site SET zone = 'CENTER' WHERE zone = 'ZONE0';");
//                } catch (SQLException ignored) {}
//
//                // Employees
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS employee (
//                      id               INTEGER PRIMARY KEY,
//                      name             TEXT    NOT NULL,
//                      bank_account     TEXT    NOT NULL,
//                      salary           REAL    NOT NULL,
//                      employment_terms TEXT    NOT NULL,
//                      start_date       TEXT    NOT NULL,
//                      is_active        INTEGER NOT NULL CHECK(is_active IN (0,1)),
//                      site_name        TEXT    NOT NULL,
//                      FOREIGN KEY(site_name) REFERENCES site(name)
//                    );
//                """);
//
//                // Roles
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS role (
//                      id   INTEGER PRIMARY KEY AUTOINCREMENT,
//                      name TEXT    NOT NULL UNIQUE
//                    );
//                """);
//
//                // Employee ↔ Role
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS employee_role (
//                      employee_id INTEGER,
//                      role_id     INTEGER,
//                      PRIMARY KEY (employee_id, role_id),
//                      FOREIGN KEY(employee_id) REFERENCES employee(id),
//                      FOREIGN KEY(role_id)     REFERENCES role(id)
//                    );
//                """);
//
//                // Shifts
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS shift (
//                      id             INTEGER PRIMARY KEY AUTOINCREMENT,
//                      date           TEXT    NOT NULL,
//                      shift_type     TEXT    NOT NULL,
//                      shift_manager  INTEGER NOT NULL,
//                      FOREIGN KEY(shift_manager) REFERENCES employee(id)
//                    );
//                """);
//
//                // Roles required per shift
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS shift_required_role (
//                      shift_id INTEGER,
//                      role_id  INTEGER,
//                      PRIMARY KEY (shift_id, role_id),
//                      FOREIGN KEY(shift_id) REFERENCES shift(id),
//                      FOREIGN KEY(role_id)  REFERENCES role(id)
//                    );
//                """);
//
//                // Employees assigned to a shift
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS shift_assignment (
//                      shift_id    INTEGER,
//                      employee_id INTEGER,
//                      PRIMARY KEY (shift_id, employee_id),
//                      FOREIGN KEY(shift_id)    REFERENCES shift(id),
//                      FOREIGN KEY(employee_id) REFERENCES employee(id)
//                    );
//                """);
//
//                // Employee availability (unavailable slots)
//                st.executeUpdate("""
//                    CREATE TABLE IF NOT EXISTS employee_availability (
//                      employee_id INTEGER,
//                      date        TEXT,
//                      shift_type  TEXT,
//                      PRIMARY KEY (employee_id, date, shift_type),
//                      FOREIGN KEY(employee_id) REFERENCES employee(id)
//                    );
//                """);
//
//                System.out.println("Ensured HR database schema exists");
//            }
//        } catch (Exception e) {
//            System.err.println("HR DB initialization failed: " + e.getMessage());
//            throw new ExceptionInInitializerError(e);
//        }
//    }
//
//    private DatabaseManager() { }
//
//    /**
//     * @return the shared SQLite Connection
//     */
//    public static Connection getConnection() {
//        return conn;
//    }
//
//    public static void main(String[] args) {
//    }
//}
