# USD/CNY Exchange Rate API

A Node.js application that fetches and stores USD to CNY exchange rates by date using SQLite database.

## Features

- Fetch exchange rates for specific dates
- Fetch exchange rates for date ranges
- Store rates in SQLite database
- RESTful API endpoints
- No UI required - pure API interface

## Installation

```bash
npm install
```

## Usage

### Start the server

```bash
npm start
```

The server will start on `http://localhost:3000`

### API Endpoints

#### 1. Fetch rate for a specific date

**POST** `/api/rates/fetch`

Request body:
```json
{
  "date": "2024-01-15"
}
```

PowerShell example:
```powershell
$body = @{date='2024-01-15'} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:3000/api/rates/fetch -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
```

#### 2. Fetch rates for a date range

**POST** `/api/rates/fetch-range`

Request body:
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31"
}
```

PowerShell example:
```powershell
$body = @{startDate='2024-01-01'; endDate='2024-01-31'} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:3000/api/rates/fetch-range -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
```

#### 3. Get rate for a specific date from database

**GET** `/api/rates/:date`

PowerShell example:
```powershell
Invoke-WebRequest -Uri http://localhost:3000/api/rates/2024-01-15 -UseBasicParsing
```

#### 4. Get all rates or filter by date range

**GET** `/api/rates?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&limit=100`

PowerShell examples:
```powershell
# Get all rates (default limit 100)
Invoke-WebRequest -Uri http://localhost:3000/api/rates -UseBasicParsing

# Get rates within a date range
Invoke-WebRequest -Uri "http://localhost:3000/api/rates?startDate=2024-01-01&endDate=2024-01-31" -UseBasicParsing

# Get rates with custom limit
Invoke-WebRequest -Uri "http://localhost:3000/api/rates?limit=50" -UseBasicParsing
```

#### 5. Fetch and save current exchange rate

**GET** `/api/rates/current/fetch`

PowerShell example:
```powershell
Invoke-WebRequest -Uri http://localhost:3000/api/rates/current/fetch -UseBasicParsing
```

## Database

The application uses SQLite database stored in `exchange_rates.db` file. The database schema:

```sql
CREATE TABLE exchange_rates (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  date TEXT NOT NULL UNIQUE,
  rate REAL NOT NULL,
  source TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
```

## Exchange Rate Data Source

The application uses [Frankfurter API](https://www.frankfurter.app/) to fetch historical exchange rates. The API is free and doesn't require an API key.

## Project Structure

```
.
├── index.js                 # Main application server
├── database.js             # Database operations
├── exchangeRateService.js  # Exchange rate fetching service
├── package.json            # Dependencies
├── exchange_rates.db       # SQLite database (auto-generated)
└── README.md              # Documentation
```

## Dependencies

- **express**: Web framework
- **sql.js**: SQLite database (pure JavaScript implementation)
- **axios**: HTTP client for API requests
- **body-parser**: Request body parsing middleware

## Notes

- Date format must be `YYYY-MM-DD`
- The application automatically creates the database and table on first run
- If a rate for a specific date already exists, it will be updated
- The server runs on port 3000 by default (can be changed via PORT environment variable)
