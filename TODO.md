# TODO - hr-sphere multi-tenant SaaS (Spring Boot + Maven)

## Step 1: Project scaffolding
- [x] Create Maven Spring Boot project structure
- [x] Add `pom.xml` with required dependencies
- [x] Add `src/main/resources/application.yml`


## Step 2: Domain model (multi-tenant)
- [ ] Create entities: Company, AppUser, Task
- [ ] Add tenant isolation via `company_id` (no cross-company access)

## Step 3: Security + Auth
- [ ] Spring Security configuration (form login)
- [ ] Role model: SUPERADMIN, HR, EMPLOYEE
- [ ] Seed superadmin account on startup

## Step 4: Approval workflow
- [ ] Company registration request by SUPERADMIN (create + approval)
- [ ] HR registration request by HR-candidate (pending until approved by SUPERADMIN)
- [ ] Employee registration request by HR-candidate (pending until approved by HR)

## Step 5: Services + Controllers
- [ ] Superadmin endpoints: manage company/HR approvals
- [ ] HR endpoints: manage employee approvals + task assignment
- [ ] Employee endpoints: view assigned tasks

## Step 6: Frontend (Thymeleaf)
- [ ] Login page
- [ ] Superadmin dashboard + company/HR approval screens
- [ ] HR dashboard + employee approval + task assignment
- [ ] Employee dashboard + task list

## Step 7: Build & run
- [ ] Verify `mvn spring-boot:run`
- [ ] Verify seed login for superadmin
- [ ] Smoke test end-to-end flow

