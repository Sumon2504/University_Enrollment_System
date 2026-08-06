# University Course Enrollment & Academic Management System

The local project is intentionally split into two independent applications.

## Backend — IntelliJ IDEA

1. Open the project root in IntelliJ IDEA as a Maven project.
2. Use JDK 21.
3. Run `com.cognizant.uams.UamsApplication`.
4. The REST API starts at `http://localhost:8082/api`.

The application connects directly to MySQL at `localhost:3306/university_db`. In MySQL Workbench, start the local server and create the `university_db` schema. The defaults are user `root` and password `root`; set `DB_USERNAME` and `DB_PASSWORD` before starting the backend if your Workbench connection uses different credentials.

## Frontend — VS Code

1. Open the `frontend` folder in VS Code.
2. Run **Terminal → Run Task → Run Frontend**.
3. Open `http://localhost:5500`.

The frontend connects to `http://localhost:8082/api`. Start the backend first.

## Default local accounts

- Administrator: `admin` / `admin123`
- Faculty: `faculty` / `faculty123`
- Student: `student` / `student123`

## Verification

From the project root run `./mvnw test` (`mvnw.cmd test` on Windows).

The implementation covers student profiles, course and semester management, enrollment/drop-add with seat limits, faculty grading, synchronized academic records, GPA summaries, and downloadable PDF grade sheets.
