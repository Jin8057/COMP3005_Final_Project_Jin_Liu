# COMP 3005 - Final Project
## Fitness Club Management System
### Name: Jin Liu
### Student Number: 101344075
### Date: November 2025

---

## 1. Project Description
This project implements a **Fitness Club Management System** using:

- **PostgreSQL** for relational database storage
- **Java (JDBC)** for application logic
- **SQL (DDL + DML)** for schema creation, sample data, and database operations

The system supports **all required operations** for:
- **Members**
- **Trainers**
- **Admins**

Database design follows the principles of **Entity-Relationship Modeling**, **Normalization (3NF)**, and implemented one view, one trigger and one index.

---

## 2. Implemented Features (8 Required Operations)

### **Member Functions**
1. **User Registration**
   - Create a new member with unique email and basic profile info
2. **Profile Management**
   - Update personal details, fitness goals, and input new
     health metrics
3. **Health History**
   - Log multiple time-stamped metric entries
4. **Dashboardd**
   - Show latest health stats, active goals, past class count, upcoming sessions

### **Trainer Functions**
1. **Schedule View**
   - See assigned PT sessions
2. **Member Lookup**
   - Search by name (case-insensitive and partial search) and view current goal and last metric.

### **Admin Functions**
1. **Equipment Maintenance**
   - Create maintenance log
   - View all maintenance logs
   - Update maintenance status (trigger records resolved time)
   - View equipment list
2. **Billing & Payment**
   - Create new bill
   - Pay existing bill

All functions are fully integrated with the PostgreSQL schema.

---

## 3. Database Design

### **ERD**
The Entity-Relationship Diagram (ERD) is included in `/docs/ERD.pdf` and contains:

- **7 entities:** Member, Trainer, PTSession, HealthMetric, Billing, Equipment, MaintenanceLog
- **5 relationships**  
- **1:N mappings**
- **No weak entities**
- **3NF normalization**

### **Relational Mapping**
The relational schema is implemented in **DDL.sql**, defining:
- Primary keys
- Foreign keys with ON DELETE CASCADE
- Appropriate data types
- Constraints (NOT NULL, CHECK)
- A view
- A trigger
- An index

---

## 4. Important Schema Features

### ✔ **View (MemberLatestMetric)**
A view that returns **the latest health metric for each member**.  
Used by **Member: Dashboard** and **Trainer: Member Lookup** functions.

### ✔ **Trigger (maintenance_resolved_time)**
Automatically sets `resolved_at` when an admin updates status to *resolved*.

### ✔ **Index**
CREATE INDEX idx_member_name ON Member(name)  
Used to speed up Member Lookup (trainer search).

## 5. How to Run the Project

### **Folder Structure**
/project-root  
│── \Docs  
│ └── ERD.pdf  
│  
│── \SQL  
│ ├── DDL.sql (Schema + View + Trigger + Index)  
│ └── DML.sql (Sample data)  
│  
│── \App\src\main\java\org\example  
│ ├── DBConnection.java  
│ ├── Main.java  
│ ├── MemberService.java  
│ ├── TrainerService.java  
│ └── AdminService.java  
│  
└── README.md

### **Step 1 — Create Database**
In pgAdmin: Create a Database named "COMP3005_Final_Project"  

### **Step 2 — Run Schema**
Execute: sql/DDL.sql  
This will create all tables, constraints, view, trigger, and index.

### **Step 3 — Insert Sample Data**
Execute: sql/DML.sql  
This seeds the database with:
- Members
- Trainers
- PT sessions (all future-dated)
- Health metrics (distinct timestamps)
- Billings
- Equipment inventory
- Maintenance logs

### **Step 4 — Update DB Credentials**
In `DBConnection.java`:  
private static final String URL = "jdbc:postgresql://localhost:5432/COMP3005_Final_Project";  
private static final String USER = "postgres";  
private static final String PASSWORD = "postgres"; 


### **Step 5 — Run Program**
Using IntelliJ:

`Main.java` → Run

---

## 6. Demo Video
Video Link: https://youtu.be/jEUCAoUGOfo

---

## 7. Notes
- All input is validated to prevent crashes on invalid user entries.
- English is not my first language, so my pronunciation in the demo video may sound accented.
  I tried my best to clearly explain each step and function. Thank you for your understanding!




