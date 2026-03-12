# ADSS_Group_AAA
HR & Transportation Combined Management System
==============================================

👥 Students' IDs
----------------
Branch name in Git: 328366190_326582285_214098345_215203894


🖥️ Execution Instructions
--------------------------

📦 Prerequisites:
- Java JDK 17 or higher
- IntelliJ IDEA (or any Java-compatible IDE)
- SQLite (managed via SQLite JDBC)
- Required external libraries (place inside a `lib/` folder and add to project classpath):
  - sqlite-jdbc-3.43.2.1.jar
  - slf4j-api-1.7.36.jar
  - slf4j-simple-1.7.36.jar

📁 Project Structure:
project/
│
├── src/
│   ├── hrmanagement/
│   │   └── Main.java
│   ├── transportation/
│   │   └── Application.java
│   ├── main/
│   │   └── MainApplication.java
│
├── lib/
│   ├── sqlite-jdbc-3.43.2.1.jar
│   ├── slf4j-api-1.7.36.jar
│   └── slf4j-simple-1.7.36.jar
│
├── MainApp.db
├── AppDatabaseManager.java
└── README.txt


🚀 How to Run:
1. Open IntelliJ and load the project.
2. Ensure the JDK version is set to 17 or above.
3. Add all `.jar` files in the `lib/` folder to your classpath (Project Structure → Libraries).
4. Run `MainApplication.java` from the `main/` package.

5. On startup, you'll be prompted:
   "Do you want to load seed data?"
   - Press 1 to load initial employees, sites, trucks, shifts, etc.
   - Press 0 to skip and work from an empty database.

6. Next, choose which system to use:
   1) HR Management System
   2) Transportation Management System
   0) Exit

7. Each system will present its own interactive menu with clear instructions.
   - HR supports employee creation, shift management, role assignment.
   - Transportation supports creating and tracking transportations, assigning drivers and trucks, and more.

🧰 Libraries Used
------------------
| Library                  | Usage                                |
|--------------------------|--------------------------------------|
| sqlite-jdbc              | SQLite connection                    |
| slf4j-api + slf4j-simple | Logging framework                    |
| Java 17+                 | Core language                        |
| IntelliJ IDEA            | Development environment              |


🧪 Testing Notes
----------------
- All key features were manually tested through the command line interface.
- To reset the database, simply delete `MainApp.db` from the project root.
- Alternatively, run the application and choose to reload seed data when prompted.

📓 Notes:
- Ensure no foreign key errors by verifying referenced entries exist (e.g., sites, roles).
- If required JARs are missing, download and place in `lib/` directory.