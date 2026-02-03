const initSqlJs = require('sql.js');
const fs = require('fs');
const path = require('path');

async function importCsvToDb() {
  const dbPath = path.join(__dirname, 'exchange_rates.db');
  const csvPath = path.join(__dirname, 'daily_currencys_rate.csv');

  // Initialize SQL.js
  const SQL = await initSqlJs();

  // Load or create database
  let db;
  if (fs.existsSync(dbPath)) {
    const buffer = fs.readFileSync(dbPath);
    db = new SQL.Database(buffer);
    console.log('Connected to existing database');
  } else {
    db = new SQL.Database();
    console.log('Created new database');
  }

  // Ensure exchange_rates table exists (same schema as database.js)
  const createTableSql = `
    CREATE TABLE IF NOT EXISTS exchange_rates (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      date TEXT NOT NULL,
      from_currency TEXT NOT NULL DEFAULT 'USD',
      to_currency TEXT NOT NULL DEFAULT 'CNY',
      rate REAL NOT NULL,
      source TEXT,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(date, from_currency, to_currency)
    )
  `;
  db.run(createTableSql);
  console.log('Table exchange_rates ready');

  // Read and parse CSV
  const csvContent = fs.readFileSync(csvPath, 'utf-8');
  const lines = csvContent.split('\n');

  // Parse header
  const header = lines[0].replace(/"/g, '').split(',');
  console.log('CSV Headers:', header);
  console.log('Expected: daily_curr_date, daily_curr_name, daily_curr_rate');

  // Prepare insert statement - map to exchange_rates schema
  const insertSql = `
    INSERT OR REPLACE INTO exchange_rates 
    (date, from_currency, to_currency, rate, source)
    VALUES (?, ?, 'CNY', ?, 'CSV_IMPORT')
  `;

  let importedCount = 0;
  let skippedCount = 0;

  // Process each line (skip header)
  for (let i = 1; i < lines.length; i++) {
    const line = lines[i].trim();
    if (!line) continue;

    // Parse CSV line (handle quoted values)
    const values = line.match(/(".*?"|[^",\s]+)(?=\s*,|\s*$)/g);
    if (!values || values.length < 3) {
      skippedCount++;
      continue;
    }

    // Clean quotes from values
    const cleanValues = values.map(v => v.replace(/^"|"$/g, ''));

    const [
      daily_curr_date,    // Date (YYYY-MM-DD)
      daily_curr_name,    // Currency name (USD, EUR, CNY, etc.)
      daily_curr_rate     // Exchange rate
    ] = cleanValues;

    // Validate data
    if (!daily_curr_date || !daily_curr_name || !daily_curr_rate) {
      skippedCount++;
      continue;
    }

    // Parse rate
    const rate = parseFloat(daily_curr_rate);
    if (isNaN(rate) || rate <= 0) {
      console.warn(`Invalid rate on line ${i}: ${daily_curr_rate}`);
      skippedCount++;
      continue;
    }

    try {
      db.run(insertSql, [
        daily_curr_date,           // date
        daily_curr_name,           // from_currency (USD, EUR, CNY, etc.)
        rate                       // rate
      ]);
      importedCount++;

      if (importedCount % 500 === 0) {
        console.log(`Imported ${importedCount} records...`);
      }
    } catch (err) {
      console.error(`Error on line ${i}:`, err.message);
      skippedCount++;
    }
  }

  // Save database to file
  const data = db.export();
  const buffer = Buffer.from(data);
  fs.writeFileSync(dbPath, buffer);

  console.log('\n=== Import Complete ===');
  console.log(`Total imported: ${importedCount}`);
  console.log(`Skipped: ${skippedCount}`);
  console.log(`Database saved to: ${dbPath}`);

  // Verify import
  const countResult = db.exec('SELECT COUNT(*) as count FROM exchange_rates');
  console.log(`Total records in exchange_rates: ${countResult[0].values[0][0]}`);

  // Show sample data
  const sampleResult = db.exec('SELECT * FROM exchange_rates ORDER BY date DESC LIMIT 5');
  if (sampleResult.length > 0) {
    console.log('\nSample data:');
    console.log('Columns:', sampleResult[0].columns);
    sampleResult[0].values.forEach(row => {
      console.log(row);
    });
  }

  db.close();
}

importCsvToDb().catch(console.error);
