# HR Sphere SaaS Application

A Multi-Tenant HR & Payroll Management SaaS Platform built with Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA, and Thymeleaf.

---

## ⚡ Easiest Way to Run (Windows 1-Click)
Double-click **`run.bat`** in the project folder! It will automatically detect your JDK installation, configure `JAVA_HOME` for you, and start the server!

---

## 🔧 Manual Setup & Fixing JAVA_HOME Error

If you get the error:
> `The JAVA_HOME environment variable is not defined correctly`

Run the following command in PowerShell / Command Prompt before starting:

### Windows (PowerShell):
```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
mvn spring-boot:run
```
*(Replace the path with your JDK installation path, e.g. `C:\Program Files\Java\jdk-21`)*

### Windows (CMD):
```cmd
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot
mvn spring-boot:run
```

---

## 🔑 Default Super Admin Login
- **URL**: `http://localhost:8080`
- **Username**: `rajputrajpalk`
- **Password**: `Rajpal@18`

---

## 👥 Creation & Access Hierarchy Workflow

1. **Super Admin**: Log in with `rajputrajpalk` / `Rajpal@18` to provision Tenant Companies & Company Admins.
2. **Company Admin**: Log in with Company Admin credentials to add HR Managers for your company.
3. **HR Manager**: Log in with HR credentials to register Employees, assign tasks, approve leaves, and generate payslips.
4. **Employee**: Log in with Employee credentials to manage assigned tasks, request leaves, and view payslips.

---

## 🗄️ Database & Web Console
- **Persistent Database**: Saved locally at `./data/hrspheredb`.
- **Interactive Database Web Console**: Accessible at `http://localhost:8080/h2-console`
  - **JDBC URL**: `jdbc:h2:file:./data/hrspheredb`
  - **Username**: `sa`
  - **Password**: `sa`
