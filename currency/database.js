const initSqlJs = require('sql.js');
const fs = require('fs');
const path = require('path');

class DatabaseManager {
  constructor() {
    this.db = null;
    this.SQL = null;
    this.dbPath = path.join(__dirname, 'exchange_rates.db');
  }

  // Initialize database connection
  async connect() {
    try {
      this.SQL = await initSqlJs();
      
      // Load existing database or create new one
      if (fs.existsSync(this.dbPath)) {
        const buffer = fs.readFileSync(this.dbPath);
        this.db = new this.SQL.Database(buffer);
        console.log('Connected to existing SQLite database');
      } else {
        this.db = new this.SQL.Database();
        console.log('Created new SQLite database');
      }
    } catch (error) {
      throw error;
    }
  }

  // Save database to file
  save() {
    try {
      const data = this.db.export();
      const buffer = Buffer.from(data);
      fs.writeFileSync(this.dbPath, buffer);
    } catch (error) {
      console.error('Error saving database:', error);
    }
  }

  // Create exchange rates table
  createTable() {
    try {
      const sql = `
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
      
      this.db.run(sql);
      this.save();
      console.log('Exchange rates table created or already exists');
    } catch (error) {
      throw error;
    }
  }

  // Insert or update exchange rate
  saveRate(date, rate, fromCurrency = 'USD', toCurrency = 'CNY', source = 'API') {
    try {
      // Check if record exists
      const checkSql = 'SELECT id FROM exchange_rates WHERE date = ? AND from_currency = ? AND to_currency = ?';
      const checkResult = this.db.exec(checkSql, [date, fromCurrency, toCurrency]);
      
      let sql;
      let params;
      let id;
      
      if (checkResult.length > 0 && checkResult[0].values.length > 0) {
        // Update existing record
        sql = `UPDATE exchange_rates SET rate = ?, source = ?, created_at = CURRENT_TIMESTAMP WHERE date = ? AND from_currency = ? AND to_currency = ?`;
        params = [rate, source, date, fromCurrency, toCurrency];
        this.db.run(sql, params);
        id = checkResult[0].values[0][0];
      } else {
        // Insert new record
        sql = `INSERT INTO exchange_rates (date, from_currency, to_currency, rate, source) VALUES (?, ?, ?, ?, ?)`;
        params = [date, fromCurrency, toCurrency, rate, source];
        this.db.run(sql, params);
        
        // Get last insert id
        const lastIdResult = this.db.exec('SELECT last_insert_rowid()');
        id = lastIdResult[0].values[0][0];
      }
      
      this.save();
      return { id, date, from_currency: fromCurrency, to_currency: toCurrency, rate };
    } catch (error) {
      throw error;
    }
  }

  // Get rate by date and currency pair
  getRateByDate(date, fromCurrency = 'USD', toCurrency = 'CNY') {
    try {
      const sql = 'SELECT * FROM exchange_rates WHERE date = ? AND from_currency = ? AND to_currency = ?';
      const result = this.db.exec(sql, [date, fromCurrency, toCurrency]);
      
      if (result.length === 0 || result[0].values.length === 0) {
        return null;
      }
      
      const columns = result[0].columns;
      const values = result[0].values[0];
      
      const row = {};
      columns.forEach((col, index) => {
        row[col] = values[index];
      });
      
      return row;
    } catch (error) {
      throw error;
    }
  }

  // Get rates by date range and currency pair
  getRatesByDateRange(startDate, endDate, fromCurrency = 'USD', toCurrency = 'CNY') {
    try {
      const sql = 'SELECT * FROM exchange_rates WHERE date BETWEEN ? AND ? AND from_currency = ? AND to_currency = ? ORDER BY date DESC';
      const result = this.db.exec(sql, [startDate, endDate, fromCurrency, toCurrency]);
      
      if (result.length === 0 || result[0].values.length === 0) {
        return [];
      }
      
      const columns = result[0].columns;
      const rows = result[0].values.map(values => {
        const row = {};
        columns.forEach((col, index) => {
          row[col] = values[index];
        });
        return row;
      });
      
      return rows;
    } catch (error) {
      throw error;
    }
  }

  // Get all rates
  getAllRates(limit = 100) {
    try {
      const sql = 'SELECT * FROM exchange_rates ORDER BY date DESC LIMIT ?';
      const result = this.db.exec(sql, [limit]);
      
      if (result.length === 0 || result[0].values.length === 0) {
        return [];
      }
      
      const columns = result[0].columns;
      const rows = result[0].values.map(values => {
        const row = {};
        columns.forEach((col, index) => {
          row[col] = values[index];
        });
        return row;
      });
      
      return rows;
    } catch (error) {
      throw error;
    }
  }

  // Close database connection
  close() {
    try {
      if (this.db) {
        this.save();
        this.db.close();
        console.log('Database connection closed');
      }
    } catch (error) {
      throw error;
    }
  }
}

module.exports = new DatabaseManager();
