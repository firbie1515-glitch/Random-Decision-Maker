# Random Decision Maker

A Java Swing desktop application that helps users make decisions through multiple strategies, including random choice, preference-based weighting, filtering, and a decision wheel.

## Features
- User registration and login
- Random decision mode
- Preference-based decision mode
- Conditional filtering and weighted decisions
- Decision wheel
- Option management
- Decision history and result sharing
- MySQL persistence

## Tech Stack
Java · Java Swing · MySQL · JDBC · Object-Oriented Programming

## Project Structure
```text
code/
├─ db/       # Database connection
├─ model/    # Domain models and decision logic
├─ service/  # Supporting services
└─ ui/       # Swing user interface
```

## Database Configuration
Real database credentials are not stored in source code. Set these environment variables before running:

```text
DB_URL=jdbc:mysql://localhost:3306/dbproject
DB_USER=root
DB_PASSWORD=your_password
```

`DB_URL` and `DB_USER` have local-development defaults. `DB_PASSWORD` must be supplied through the environment.

## Database Schema
Use `資料庫關聯綱目的描述檔.sql` to create the required database structure.

## How to Run
1. Install Java and MySQL.
2. Create `dbproject` using the provided SQL file.
3. Add the MySQL JDBC driver to the classpath.
4. Configure the database environment variables above.
5. Compile and run `code/Main.java`.

## My Contribution
I served as a primary developer and participated in feature planning, GUI design, database integration, core Java implementation, testing, debugging, and project presentation.

This project demonstrates end-to-end development experience including object-oriented design, desktop UI implementation, relational database operations, and Java/MySQL integration.

## Security
Do not commit real database passwords or other secrets. Compiled files and local IDE settings are excluded through `.gitignore`.
