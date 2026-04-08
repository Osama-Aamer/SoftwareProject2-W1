# Shopping Cart Application — Week 3 (DB Localization)

A **JavaFX** shopping cart application that:
- Reads all UI messages from a **MySQL/MariaDB database** (`localization_strings` table).
- Persists every cart calculation into `cart_records` and `cart_items` tables.
- Supports **5 locales**: English, Finnish, Swedish, Japanese, and Arabic (RTL).

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 21 |
| Maven | 3.9+ |
| MySQL / MariaDB | 10.6+ / 8.0+ |
| Docker (optional) | Latest |

---

## Database Setup

1. Start your MySQL / MariaDB server.
2. Run the provided schema (creates the database, tables, and seed data):

```bash
mysql -u root -p < schema.sql
```

This creates the `shopping_cart_localization` database and populates the `localization_strings` table with rows for `en_US`, `fi_FI`, `sv_SE`, `ja_JP`, and `ar_SA`.

---

## Configuration

Edit `DatabaseConnection.java` if your DB credentials differ from the defaults:

```java
private static final String URL  = "jdbc:mariadb://localhost:3306/shopping_cart_localization";
private static final String USER = "root";
private static final String PASS = "password";
```

---

## Running Locally

```bash
# Build
mvn clean package -DskipTests

# Run (JavaFX via Maven plugin)
mvn javafx:run
```

---

## Running with Docker

> **Important:** The Docker container runs a headless JRE and cannot display a JavaFX GUI.  
> For headless environments, use X11 forwarding or VNC.

```bash
# Build the image
docker build -t osamaaa1/shopping-cart:latest .

# Run (X11 forwarding on Linux/Mac)
docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix osamaaa1/shopping-cart:latest
```

---

## CI/CD Pipeline (Jenkins)

The `Jenkinsfile` defines the following stages:

1. **Build** — `mvn clean package`
2. **Test** — `mvn test`
3. **JaCoCo Coverage Report** — `mvn jacoco:report`
4. **Publish Coverage** — posts the HTML report to Jenkins
5. **Build Docker Image** — builds `osamaaa1/shopping-cart:v1`
6. **Push to Docker Hub** — uses `docker-hub-cred` credentials

### Required Jenkins Configuration

- Maven tool named **`Maven3`**
- Docker Hub credentials with ID **`docker-hub-cred`**
- JaCoCo plugin installed

---

## Project Structure

```
week1-swp2/
├── src/main/java/org/example/
│   ├── DatabaseConnection.java   # DB connection manager
│   ├── ShoppingCart.java         # Core cart logic
│   ├── ShoppingCartApp.java      # JavaFX Application entry
│   ├── ShoppingCartController.java
│   └── service/
│       ├── CartService.java      # Saves cart records & items to DB
│       └── LocalizationService.java  # Loads strings from DB → Properties fallback
├── src/main/resources/
│   ├── shopping_cart.fxml
│   ├── style.css
│   ├── MessagesBundle_en_US.properties  (fallback)
│   ├── MessagesBundle_fi_FI.properties
│   ├── MessagesBundle_sv_SE.properties
│   ├── MessagesBundle_ja_JP.properties
│   └── MessagesBundle_ar_SA.properties
├── schema.sql        # Full DB schema + seed data
├── Dockerfile
├── Jenkinsfile
└── pom.xml
```

---

## Database Tables

### `cart_records`
| Column | Type | Description |
|--------|------|-------------|
| id | INT PK | Auto-increment |
| total_items | INT | Number of item rows |
| total_cost | DOUBLE | Grand total in EUR |
| language | VARCHAR(10) | Locale code, e.g. `en_US` |
| created_at | TIMESTAMP | Automatically set |

### `cart_items`
| Column | Type | Description |
|--------|------|-------------|
| id | INT PK | Auto-increment |
| cart_record_id | INT FK | References `cart_records.id` |
| item_number | INT | 1-based item index |
| price | DOUBLE | Unit price |
| quantity | INT | Quantity |
| subtotal | DOUBLE | `price × quantity` |

### `localization_strings`
| Column | Type | Description |
|--------|------|-------------|
| id | INT PK | Auto-increment |
| key | VARCHAR(100) | UI string key, e.g. `app.title` |
| value | VARCHAR(255) | Translated string |
| language | VARCHAR(10) | Locale code, e.g. `en_US` |

---

## Author

**Osama Aamer** — Software Project 2, Year 2  
Docker Hub: [osamaaa1](https://hub.docker.com/u/osamaaa1)
