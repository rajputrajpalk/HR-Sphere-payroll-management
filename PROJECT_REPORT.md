# HR Sphere - Multi-Tenant HR & Payroll Management SaaS Platform
## Comprehensive Detailed Project Report

---

### Chapter 1 — Introduction

#### 1.1 Project Background
Managing human resources, payroll, and employee tasks efficiently is a critical requirement for any modern organization. Many small and medium-sized enterprises (SMEs) struggle with fragmented tools or manual spreadsheet-based processes. The **HR Sphere** platform is a multi-tenant Software as a Service (SaaS) solution designed to centralize and automate HR and payroll operations across multiple independent companies on a single centralized system.

#### 1.2 Purpose of the System
The primary purpose of HR Sphere is to provide an integrated, secure, and user-friendly platform for managing the complete employee lifecycle. It serves as a unified workspace for Super Admins to manage tenant companies, Company Admins to manage their HR departments, HR Managers to process payroll and leaves, and Employees to track their tasks and compensation.

#### 1.3 Scope of the Project
The scope encompasses four primary domains based on user roles:
- **Super Admin Operations:** Global tenant provisioning and company administration.
- **Company Admin Operations:** Internal company setup and HR management delegation.
- **HR Management:** Employee registration, task assignment, leave approval, and payslip generation.
- **Employee Services:** Task tracking, leave requests, and payslip access.

#### 1.4 Document Overview
This document outlines the entire lifecycle of the HR Sphere project, detailing the business analysis, system architecture, technical implementation using the Java Spring Boot ecosystem, and future scalability plans.

---

### Chapter 2 — Business & Problem Analysis

#### 2.1 Problem Statement
Conventional HR management systems often present the following bottlenecks for SMEs:
- **High Costs:** Enterprise HR solutions are prohibitively expensive for small organizations.
- **Data Fragmentation:** Using separate tools for tasks, payroll, and leave management leads to data silos.
- **Maintenance Overhead:** On-premise solutions require dedicated IT staff.
- **Lack of Multi-Tenancy:** Single-tenant solutions require separate deployments for different branches or sister companies, increasing overhead.

#### 2.2 Business Objectives & Goals
- **Provide a Centralized SaaS:** Enable multiple companies to operate independently on the same infrastructure (multi-tenancy).
- **Streamline HR Processes:** Automate payroll generation and leave approvals to reduce administrative burden.
- **Empower Employees:** Provide a self-service portal for transparency regarding tasks and compensation.

#### 2.3 SWOT Analysis
- **Strengths:** Robust Java Spring Boot ecosystem; built-in security; zero-configuration local setup via H2 database.
- **Weaknesses:** Local H2 database is not suitable for large-scale production without migrating to a robust RDBMS like PostgreSQL or MySQL.
- **Opportunities:** Potential for AI-driven performance analytics and automated tax compliance integration.
- **Threats:** High competition in the HR tech market; strict data privacy regulations (e.g., GDPR).

---

### Chapter 3 — Feasibility Study

#### 3.1 Technical Feasibility
The project utilizes an industry-standard, enterprise-grade tech stack (Java 21, Spring Boot 3.3.2, Spring Security, Spring Data JPA, Thymeleaf). The development team possesses strong backend capabilities. The use of Maven for dependency management and embedded Apache Tomcat ensures high portability. Thus, technical feasibility is high.

#### 3.2 Economic Feasibility
Leveraging open-source Java technologies and Spring Boot eliminates licensing costs. The system's multi-tenant architecture significantly reduces cloud hosting costs by maximizing resource utilization across multiple clients. The project is highly economically feasible.

#### 3.3 Operational Feasibility
The role-based access control (RBAC) ensures a minimal learning curve for end-users, as they only see interfaces relevant to their duties. Deployment is straightforward with the provided `run.bat` script, making local testing and operational onboarding seamless.

#### 3.4 Legal & Ethical Feasibility
Handling employee data, payroll, and PII requires strict data isolation. The multi-tenant architecture enforced at the application level ensures data privacy, meaning one company cannot access another company's records.

---

### Chapter 4 — Requirements Engineering / SRS

#### 4.1 User Roles & Elicitation
- **Super Admin:** Global system administrator who provisions new tenant companies.
- **Company Admin:** Manages the HR department for their specific company.
- **HR Manager:** Handles day-to-day HR tasks (payroll, leave, employee onboarding).
- **Employee:** End-user who consumes HR services.

#### 4.2 Functional Requirements
- **FR1:** The system shall allow the Super Admin to register new Companies and assign Company Admins.
- **FR2:** The system shall allow HR Managers to assign Tasks to Employees.
- **FR3:** The system shall process Leave Applications (Pending, Approved, Rejected).
- **FR4:** The system shall generate Payroll records (Payslips) for Employees.
- **FR5:** The system shall strictly isolate data so that users can only view data pertaining to their specific Company.

#### 4.3 Non-Functional Requirements
- **NFR1 (Security):** All passwords must be securely hashed. Spring Security must protect all routes based on roles.
- **NFR2 (Usability):** The UI, powered by Thymeleaf and HTML/CSS, must be responsive and intuitive.
- **NFR3 (Portability):** The application must run on any environment with Java 21 installed, utilizing the embedded H2 database for local environments.

---

### Chapter 5 — Project Methodology (Agile/Scrum)

#### 5.1 Rationale for Agile
An Agile approach allows for iterative development of distinct modules (e.g., focusing on Super Admin first, then HR workflows) and accommodates changing business requirements regarding payroll processing or leave policies.

#### 5.2 Scrum Framework Implementation
- **Sprint 1 (Foundation):** Spring Boot setup, Maven dependencies, H2 database configuration, entity models creation.
- **Sprint 2 (Security & Tenants):** Spring Security configuration, RBAC implementation, Company and User registration logic.
- **Sprint 3 (HR Core):** Task management and Leave application workflows.
- **Sprint 4 (Payroll & UI):** Payroll generation module, Thymeleaf template integration, dashboard designs.
- **Sprint 5 (Testing & Polish):** Bug fixing, local environment scripts (`run.bat`), and documentation.

---

### Chapter 6 — System Architecture & HLD

#### 6.1 Architectural Pattern
The application follows the **Model-View-Controller (MVC)** pattern, utilizing Spring Boot's robust architecture.
- **Model:** JPA Entities (`AppUser`, `Company`, `LeaveApplication`, `Payroll`, `Task`) representing database tables.
- **View:** Server-side rendered HTML using the Thymeleaf template engine.
- **Controller:** Spring `@Controller` classes that handle HTTP requests and bind models to views.

#### 6.2 N-Tier Architecture
1. **Presentation Tier:** Thymeleaf templates providing dynamic web pages.
2. **Application/Business Logic Tier:** Spring Boot Services and Controllers handling business rules, multi-tenancy logic, and security.
3. **Data Access Tier:** Spring Data JPA Repositories abstracting database queries.
4. **Database Tier:** Embedded H2 relational database (persisted to a local file).

#### 6.3 High-Level Data Flow
User Action (Browser) $\rightarrow$ Spring Security Filter Chain $\rightarrow$ Spring MVC Controller $\rightarrow$ Service Layer $\rightarrow$ Spring Data JPA Repository $\rightarrow$ Database.

---

### Chapter 7 — Detailed Design / LLD

#### 7.1 Controller Design
Controllers are logically separated by user roles:
- `DashboardController`: Determines user roles and routes them to their respective dashboards.
- `superadmin/*`: Controllers for managing global companies.
- `companyadmin/*`: Controllers for managing internal HR staff.
- `hr/*`: Controllers for managing employees, tasks, leaves, and payroll.
- `employee/*`: Controllers for employee self-service actions.

#### 7.2 View Architecture
Views are organized in `src/main/resources/templates/` with subdirectories for each role (`superadmin`, `companyadmin`, `hr`, `employee`). This ensures separation of concerns. `login.html` handles universal authentication.

---

### Chapter 8 — Database Design

#### 8.1 Schema Overview
The database is managed via Hibernate ORM (Object-Relational Mapping), automatically generating the schema based on JPA annotations.

#### 8.2 Core Entities
1. **`Company`**: Represents a tenant. Contains `id`, `name`, `address`, `contactDetails`.
2. **`AppUser`**: Represents all users (Super Admin, HR, Employee). Contains `id`, `username`, `password`, `role`, and a Many-To-One relationship to `Company`.
3. **`Task`**: Assigned work. Contains `id`, `title`, `description`, `status`, and relationships to `AppUser` (assignee/assigner).
4. **`LeaveApplication`**: Tracks time off. Contains `id`, `startDate`, `endDate`, `reason`, `status`, and relationship to `AppUser`.
5. **`Payroll`**: Compensation records. Contains `id`, `month`, `year`, `baseSalary`, `deductions`, `netPay`, and relationship to `AppUser`.

#### 8.3 Data Persistence
Configured in `application.yml` to save locally at `./data/hrspheredb` using H2 file-based mode, ensuring data persists across application restarts.

---

### Chapter 9 — Module & Functional Design

#### 9.1 Tenant Management Module
Allows Super Admins to onboard new companies. Automatically creates a default Company Admin account for each new tenant, establishing the hierarchy.

#### 9.2 HR Operations Module
- **Employee Management:** Full CRUD operations for company employees.
- **Task Management:** HR can create tasks, assign them, and track completion statuses.
- **Leave Management:** A workflow engine for approving or rejecting requested time off.
- **Payroll Generation:** Calculates net pay based on base salary and deductions, generating standardized payslip records.

#### 9.3 Employee Portal
A read-write interface where employees can update task statuses (e.g., In Progress, Completed), submit new leave applications, and view their historical payroll data.

---

### Chapter 10 — Security Architecture

#### 10.1 Authentication & Authorization
- **Spring Security:** Acts as a robust shield. Custom `UserDetailsService` implementation loads user data and authorities.
- **Role-Based Access Control:** URLs are secured based on roles (e.g., `/hr/**` requires `ROLE_HR`).
- **Multi-Tenant Isolation:** Database queries at the repository/service level are strictly filtered by the authenticated user's `Company` ID to prevent data leakage between tenants.

#### 10.2 Vulnerability Mitigation
- **CSRF Protection:** Spring Security's default CSRF protection is active, preventing cross-site request forgery on form submissions.
- **Password Hashing:** Utilizing `BCryptPasswordEncoder` to safely store user credentials.
- **SQL Injection:** Spring Data JPA prevents SQL injection through the use of parameterized queries via Hibernate.

---

### Chapter 11 — API Design & Documentation

#### 11.1 Internal Endpoints (MVC)
The application heavily utilizes traditional form submissions rather than a decoupled REST API.
- **Authentication:** `POST /login` (Handled natively by Spring Security).
- **Example Flow (Leaves):**
  - `GET /employee/leaves`: Displays leave history.
  - `POST /employee/leaves/request`: Submits a new leave application.
  - `POST /hr/leaves/{id}/approve`: Updates status to Approved.

---

### Chapter 12 — UI/UX Design

#### 12.1 Design Philosophy
The system prioritizes a clean, professional, and utilitarian interface suitable for enterprise software.
- **Server-Side Rendering:** Thymeleaf ensures fast initial load times and high SEO compliance, reducing the need for heavy client-side JavaScript.
- **Responsive Layouts:** Ensuring HR managers and employees can access dashboards seamlessly via desktop or mobile browsers.

---

### Chapter 13 — Implementation

#### 13.1 Environment & Tooling
- **Java 21:** Utilizing the latest LTS features.
- **Spring Boot 3.3.2:** Core framework simplifying configuration and deployment.
- **Maven:** Project Object Model (`pom.xml`) handles all dependency resolutions.
- **Lombok:** Reduces boilerplate code (Getters, Setters, Constructors) via annotations.

#### 13.2 Core Implementation Details
- **Configuration:** Externalized in `application.yml` for easy environment swapping.
- **Database Console:** H2 web console enabled at `/h2-console` for quick administrative debugging.
- **Startup Scripts:** `run.bat` and `run.ps1` provided for frictionless local execution on Windows environments, automatically managing `JAVA_HOME`.

---

### Chapter 14 — Testing & Quality Assurance

#### 14.1 Testing Strategy
- **Unit Testing:** Enabled via `spring-boot-starter-test` for evaluating core business logic and service layers in isolation.
- **Integration Testing:** Validating the interaction between JPA repositories and the H2 database.
- **Manual UAT:** Verified the full lifecycle by logging in as the default Super Admin (`rajputrajpalk`), creating a company, and cascading down to an employee's leave request.

---

### Chapter 15 — Deployment & DevOps

#### 15.1 Deployment Architecture
- **Embedded Server:** The application compiles into an executable JAR containing an embedded Apache Tomcat server.
- **Portability:** Can be deployed to any cloud provider (AWS EC2, Azure VM, Heroku) requiring only a JRE.

#### 15.2 Future CI/CD
The standard Maven directory structure allows for seamless integration with GitHub Actions or Jenkins to automate building, testing, and Docker image creation.

---

### Chapter 16 — Performance & Scalability

#### 16.1 Performance Optimization
- **Thymeleaf Caching:** Currently disabled (`cache: false`) for development but easily switchable for production to drastically reduce server render times.
- **JPA Optimizations:** Using lazy loading where appropriate to prevent N+1 query problems.

#### 16.2 Scalability Plan
- **Database Migration:** The H2 database must be replaced with PostgreSQL/MySQL by simply changing the `application.yml` JDBC URL and driver.
- **Statelessness:** Transitioning from server-side sessions to JWT (JSON Web Tokens) to allow horizontal scaling across multiple load-balanced instances.

---

### Chapter 17 — Risk Management

#### 17.1 Risk Identification & Assessment
- **Risk 1: Cross-Tenant Data Leakage.** (Low Probability, Critical Impact).
- **Risk 2: Local Database Corruption.** (Medium Probability, High Impact).

#### 17.2 Mitigation Strategies
- **For Data Leakage:** Ensure rigorous testing of all repository queries to guarantee the `company_id` filter is universally applied.
- **For Database Integrity:** Transition to a managed database service (e.g., Amazon RDS) with automated daily backups for production environments.

---

### Chapter 18 — Results & Evaluation

#### 18.1 Key Achievements
The implementation successfully delivers a working multi-tenant architecture from scratch.
- **Hierarchy Enforcement:** Effectively chains permissions from Super Admin down to Employees.
- **Developer Experience:** The `run.bat` script removes setup friction, allowing immediate demonstration and testing.

---

### Chapter 19 — Limitations & Future Scope

#### 19.1 Current Limitations
- **File Storage:** No capability currently for employees to upload medical certificates for leaves or download PDF versions of payslips.
- **Database:** H2 is excellent for development but not suitable for concurrent, high-volume production traffic.

#### 19.2 Future Enhancements
1. **REST APIs:** Decoupling the frontend into a React/Angular SPA and serving data via Spring RestControllers.
2. **Email Integration:** Using JavaMailSender to notify employees of task assignments or leave approvals.
3. **PDF Generation:** Integrating JasperReports or iText to dynamically generate downloadable PDF payslips.

---

### Chapter 20 — Maintenance & Support

#### 20.1 Routine Maintenance
- **Dependency Updates:** Regular Maven updates to patch vulnerabilities in Spring or underlying libraries.
- **Database Schema Management:** Transitioning from `hibernate.ddl-auto: update` to formal migration tools like Flyway or Liquibase for robust schema versioning in production.

---

### Chapter 21 — Conclusion

#### 21.1 Final Summary
The **HR Sphere** project stands as a comprehensive SaaS solution addressing the core HR and payroll needs of modern SMEs. By leveraging the power of Spring Boot and a strict multi-tenant architecture, it provides a centralized platform for secure organizational management.

#### 21.2 Value Proposition
By combining Task management, Leave processing, and Payroll generation into a single unified workspace, HR Sphere drastically reduces administrative overhead and tool fragmentation, offering high value and operational efficiency for multiple independent businesses simultaneously.

---
### References
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Security Reference: https://docs.spring.io/spring-security/reference/
- Thymeleaf Documentation: https://www.thymeleaf.org/
- H2 Database Engine: https://www.h2database.com/

# FOLDER STRUCTUTR:
C:.
├───.mvn
├───.vscode
├───data
├───src
│   └───main
│       ├───java
│       │   └───com
│       │       └───hr
│       │           └───sphere
│       │               ├───bootstrap
│       │               ├───config
│       │               ├───model
│       │               ├───repo
│       │               ├───security
│       │               └───web
│       │                   ├───companyadmin
│       │                   ├───employee
│       │                   ├───hr
│       │                   └───superadmin
│       └───resources
│           ├───static
│           │   └───css
│           └───templates
│               ├───companyadmin
│               ├───employee
│               ├───hr
│               └───superadmin
└───target
    ├───classes
    │   ├───com
    │   │   └───hr
    │   │       └───sphere
    │   │           ├───bootstrap
    │   │           ├───config
    │   │           ├───model
    │   │           ├───repo
    │   │           ├───security
    │   │           └───web
    │   │               ├───companyadmin
    │   │               ├───employee
    │   │               ├───hr
    │   │               └───superadmin
    │   ├───static
    │   │   └───css
    │   └───templates
    │       ├───companyadmin
    │       ├───employee
    │       ├───hr
    │       └───superadmin
    ├───generated-sources
    │   └───annotations
    ├───generated-test-sources
    │   └───test-annotations
    └───maven-status
        └───maven-compiler-plugin
            └───compile
                └───default-compile