========================================================================
                      HR SPHERE SAAS APPLICATION
========================================================================

A Multi-Tenant HR & Payroll Management SaaS Platform built with Java 21,
Spring Boot 3.3, Spring Security, Spring Data JPA, and Thymeleaf.


------------------------------------------------------------------------
1. PREREQUISITES
------------------------------------------------------------------------
Before running the application on another machine, make sure you have:
1. Java Development Kit (JDK): JDK 17 or JDK 21+ installed.
2. Apache Maven: Maven installed and added to your system PATH (or use Maven wrapper).
3. Web Browser: Google Chrome, Firefox, Edge, or Safari.


------------------------------------------------------------------------
2. HOW TO RUN THE APPLICATION FROM ZIP
------------------------------------------------------------------------
Step 1: Extract the ZIP archive to any folder on your computer.
        (e.g., C:\Projects\java-maven or ~/Projects/java-maven)

Step 2: Open a Command Prompt (Windows) or Terminal (Mac/Linux).

Step 3: Navigate to the extracted project root directory:
        cd "path/to/extracted/java maven"

Step 4: Execute the Spring Boot run command:

        Windows (Command Prompt / PowerShell):
        mvn clean package 
        
        mvn spring-boot:run


        Linux / Mac Terminal:
        mvn spring-boot:run

        Note: If JAVA_HOME is not set, set JAVA_HOME to your JDK folder:
        PowerShell: $env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21"
        Linux/Mac:  export JAVA_HOME=/path/to/jdk-21

Step 5: Wait until you see the following log line in your terminal:
        "Started HrSphereApplication in X seconds"


------------------------------------------------------------------------
3. ACCESSING THE APPLICATION
------------------------------------------------------------------------
Once started, open your web browser and navigate to:

        http://localhost:8080


------------------------------------------------------------------------
4. INITIAL SUPER ADMIN LOGIN CREDENTIALS
------------------------------------------------------------------------
On initial application startup, the system automatically seeds the default
Super Admin account:

        Username: rajputrajpalk
        Password: Rajpal@18


------------------------------------------------------------------------
5. ACCOUNT CREATION & HIERARCHY WORKFLOW
------------------------------------------------------------------------
To populate and test the multi-tenant workflow:

Step 1 [Super Admin]:
       - Log in with `rajputrajpalk` / `Rajpal@18`.
       - Create a new Tenant Company (e.g. Acme Corp) and assign its Company Admin
         username and password.
       - Use the Company Status toggle (Active / Suspended) to test the Cascade
         Access Lock feature.

Step 2 [Company Admin]:
       - Log out and log in with the Company Admin credentials created in Step 1.
       - Navigate to the dashboard to create HR Managers for your company.

Step 3 [HR Manager]:
       - Log out and log in with the HR Manager credentials created in Step 2.
       - Register Employees under your supervision.
       - Create and delegate tasks with title, description, priority, and due dates.
       - Review and Approve/Reject Employee leave requests.
       - Generate monthly payroll/salary slips for employees.

Step 4 [Employee]:
       - Log out and log in with the Employee credentials created in Step 3.
       - View assigned tasks and update progress status (Pending -> In Progress -> Completed).
       - Apply for leave requests and track approval status.
       - View and download monthly payslips and salary breakdowns.


------------------------------------------------------------------------
6. DATABASE & WEB CONSOLE ACCESS
------------------------------------------------------------------------
The application uses a persistent file database located at `./data/hrspheredb`.
All companies, users, tasks, leave requests, and payslips automatically persist
across application restarts.

You can inspect the live SQL database through the web console at:

        URL:       http://localhost:8080/h2-console
        JDBC URL:  jdbc:h2:file:./data/hrspheredb
        Username:  sa
        Password:  sa

========================================================================
