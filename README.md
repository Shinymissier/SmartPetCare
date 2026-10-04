# Smart Pet Care and Vaccination Tracker

Enterprise full-stack web application designed for comprehensive companion health management, vaccination tracking, clinical medical history, feeding routines, appointments, reminders, and XML processing. Built with Java Web Technologies (Servlets, JSP, JDBC, JSTL), Supabase PostgreSQL database with Row Level Security (RLS), and a comprehensive XML Technology Suite (DTD, XML Schema (XSD), DOM Parser, XPath, XSLT).

---

## 1. Key Features

- **Dashboard**: High-level KPI metrics (Total Pets, Upcoming Vaccinations, Overdue Vaccinations, Upcoming Appointments, Medical Encounters), interactive Chart.js visualizations (Vaccination status doughnut & Species demographics), companion quick cards, and attention queue.
- **Authentication & Security**: User registration, login, logout, self-service password reset, change password, BCrypt password hashing, session management, and `AuthFilter` route protection. Each user accesses only their own records.
- **Pet Management**: Full CRUD for companions (Dog, Cat, Bird, Rabbit, etc.), breed, DOB, weight, coat color, allergies, health conditions, notes, photo URLs, and complete health history profile views.
- **Vaccination Management**: Tracking vaccines, auto-calculated status (`Completed`, `Upcoming`, `Overdue` based on booster due dates), veterinarian/clinic logging, batch notes, and filtered status queues.
- **Medical Records**: Visit dates, clinical diagnoses, treatments, medications, attending veterinarians, costs, and follow-up visit reminders.
- **Feeding & Appointments**: Daily feeding schedules (food type, quantity, time, frequency, status), one-click meal logging, and veterinary/grooming appointment scheduling.
- **XML Technology Studio**:
  - **Dynamic XML Export**: Exports user's actual database records into validated XML files (Complete pet care record, pet profiles, vaccinations, medical history, appointments).
  - **DTD Validation**: Structural document type definition enforcement via `petcare.dtd`.
  - **XSD Validation**: Strict XML Schema verification of data types, date formats, status enums, and decimal constraints via `petcare.xsd`.
  - **DOM Parser**: Real Java DOM Parser (`PetCareXMLParser.java`) reading XML streams, extracting `Pet` and `Vaccination` objects without regex, displaying extraction previews, and saving records to Supabase.
  - **XPath Queries**: Interactive XPath console executing predefined and custom queries (find pet by name, find overdue vaccinations, retrieve medical records, and date filtering) displaying the expression and matched XML nodes.
  - **XSLT Stylesheet Report**: Transforms pet care XML into an official printable HTML health report (`petcare.xsl`).
  - **XML Download & Preview**: Live syntax-formatted XML viewer, raw XML download, XSD-validated download, and print-ready report generation.

---

## 2. Technology Stack

- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap, Chart.js 4.4, Print CSS
- **Backend**: Java 17+, Java Servlets 4.0, JSP 2.3, JSTL 1.2
- **XML Technologies**: XML, DTD (`petcare.dtd`), XSD (`petcare.xsd`), XSLT (`petcare.xsl`), Java DOM Parser (`DocumentBuilder`), XPath (`XPathFactory`), Transformer (`TransformerFactory`)
- **Database**: Supabase PostgreSQL with Row Level Security (RLS) & Local In-Memory H2 Fallback
- **Build & Server**: Maven 3.9+, Apache Tomcat 9.0, Eclipse IDE compatible

---

## 3. Project Structure

```
pet/
├── pom.xml                                   # Maven dependencies & WAR build configuration
├── mvnw.cmd                                  # Maven Wrapper
├── deploy_and_run.bat                        # One-click build and launch on Tomcat
├── petcare.dtd                               # DTD Document Type Definition
├── petcare.xsd                               # XML Schema Definition
├── petcare.xsl                               # XSLT Stylesheet for HTML Clinical Reports
├── database/
│   ├── supabase_schema.sql                   # Complete PostgreSQL schema with RLS & triggers
│   └── seed_data.sql                         # Demo seed data
├── src/
│   ├── main/
│   │   ├── java/com/petcare/
│   │   │   ├── config/                       # AppConfig, DatabaseConnection, SupabaseRestClient
│   │   │   ├── dao/                          # UserDAO, PetDAO, VaccinationDAO, MedicalDAO, etc.
│   │   │   ├── filter/                       # AuthFilter (Session security & route protection)
│   │   │   ├── model/                        # Pet, Vaccination, MedicalRecord, Appointment, User...
│   │   │   ├── servlet/                      # Dashboard, Pet, Vaccination, Medical, XML, Reports...
│   │   │   ├── util/                         # DateUtil, PasswordUtil (BCrypt)
│   │   │   └── xml/
│   │   │       ├── PetCareXMLGenerator.java  # Dynamic XML export from DB
│   │   │       ├── PetCareXMLParser.java     # Java DOM Parser implementation
│   │   │       ├── PetCareXMLValidator.java  # DTD & XSD programmatic validators
│   │   │       ├── PetCareXPathService.java  # XPath query execution service
│   │   │       └── PetCareXSLTService.java   # XSLT HTML report transformation
│   │   ├── resources/
│   │   │   ├── supabase.properties           # Database & Supabase connection credentials
│   │   │   ├── schema/                       # petcare.dtd, petcare.xsd, petcare.xsl
│   │   │   └── samples/                      # valid_petcare_sample.xml, invalid_petcare_sample.xml
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                   # Web descriptor
│   │       │   └── views/                    # JSP views (auth, dashboard, pets, xml, reports...)
│   │       ├── assets/                       # Custom CSS, print styles, and JS
│   │       └── index.jsp                     # App entry point redirector
│   └── test/java/com/petcare/
│       └── PetCareXMLTest.java               # JUnit 5 tests for DTD, XSD, DOM, XPath, and XSLT
```

---

## 4. Supabase Setup & Configuration

1. In your **Supabase Dashboard**, open the **SQL Editor**.
2. Run the SQL script from `database/supabase_schema.sql`. This creates the tables:
   - `profiles`, `pets`, `vaccinations`, `medical_records`, `feeding_schedules`, `feeding_logs`, `appointments`, `reminders`.
   - Enables Row Level Security (RLS) policies on every table.
3. (Optional) Run `database/seed_data.sql` to populate sample companions and vaccinations.
4. Copy your Supabase Project settings into `src/main/resources/supabase.properties`:
   - `supabase.url` = `https://[your-project].supabase.co`
   - `supabase.anon.key` = `eyJhbGci...`
   - `supabase.jdbc.url` = `jdbc:postgresql://db.[your-project].supabase.co:5432/postgres?sslmode=require`
   - `supabase.jdbc.user` = `postgres`
   - `supabase.jdbc.password` = `[your-db-password]`
5. Note: The application includes an automatic **LOCAL_FALLBACK** mode. If Supabase credentials are not configured yet, it boots seamlessly in memory with full demo data so you can test every screen and feature out-of-the-box!

---

## 5. Running the Application

### Option A: One-Click Script
Run `deploy_and_run.bat` in the project root:
```cmd
deploy_and_run.bat
```
This packages `target/petcare.war`, copies it to Apache Tomcat, and starts the server.

### Option B: Command Line (Maven Wrapper)
1. Build the WAR:
   ```cmd
   .\mvnw.cmd clean package
   ```
2. Run automated test suite:
   ```cmd
   .\mvnw.cmd test
   ```
3. Deploy `target/petcare.war` to your Apache Tomcat `webapps/` folder.
4. Access the application in your browser:
   **`http://localhost:8080/petcare`**

### Option C: Eclipse IDE Setup
1. Open Eclipse IDE for Enterprise Java and Web Developers.
2. Select **File &rarr; Import... &rarr; Existing Maven Projects**.
3. Select `c:\Users\SHINY\pet` as the Root Directory.
4. In Eclipse, configure Apache Tomcat under the **Servers** tab (**New &rarr; Server &rarr; Apache &rarr; Tomcat v9.0 Server**).
5. Right-click the `petcare` project &rarr; **Run As &rarr; Run on Server**.

---

## 6. Default Demo Credentials

- **Email**: `demo@petcare.com`
- **Password**: `admin123`

---

## 7. XML Demonstration Guide

Navigate to **XML Management** in the sidebar:
1. **XML Export**: Click *Export Complete Pet Care Record* or *Download Validated XML* to export actual database data into standard XML.
2. **DTD & XSD Validation**: Click *Load Valid Sample* or *Load Invalid Sample*, then click *Run Validation Check*. View structured error reports detailing line and column violations.
3. **XPath Query Console**: Select from the 5 predefined query presets (e.g. *Find overdue vaccinations*, *Find pet by name*) or write a custom expression. View the matched node count and raw XML sub-trees.
4. **XSLT HTML Report**: Click *Open Printable Full Screen* to view the styled HTML clinical report generated directly via `petcare.xsl`.
5. **XML Import via DOM Parser**: Upload an XML file or paste XML, click *Parse & Validate via DOM* to preview extracted Java objects, and click *Commit & Save Records to Supabase* to persist them to the database.
