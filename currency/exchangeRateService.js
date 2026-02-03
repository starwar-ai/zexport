const axios = require('axios');

class ExchangeRateService {
  constructor() {
    // Using exchangerate-api.com free API
    this.apiUrl = 'https://api.exchangerate-api.com/v4/latest/USD';
    // Alternative: Use frankfurter API
    this.alternativeApiUrl = 'https://api.frankfurter.app';
  }

  /**
   * Fetch exchange rate for a specific date
   * @param {string} date - Date in YYYY-MM-DD format
   * @param {string} fromCurrency - Source currency (default: USD)
   * @param {string} toCurrency - Target currency (default: CNY)
   * @returns {Promise<number>} Exchange rate
   */
  async fetchRateByDate(date, fromCurrency = 'USD', toCurrency = 'CNY') {
    try {
      // Try Frankfurter API first (supports historical data)
      const response = await axios.get(`${this.alternativeApiUrl}/${date}`, {
        params: {
          from: fromCurrency,
          to: toCurrency
        }
      });

      if (response.data && response.data.rates && response.data.rates[toCurrency]) {
        return response.data.rates[toCurrency];
      }

      throw new Error('Rate not found in response');
    } catch (error) {
      console.error(`Error fetching rate for ${date} (${fromCurrency}/${toCurrency}):`, error.message);
      
      // If historical data fails, try current rate as fallback
      if (date === this.getTodayDate()) {
        return await this.fetchCurrentRate(fromCurrency, toCurrency);
      }
      
      throw error;
    }
  }

  /**
   * Fetch current exchange rate
   * @param {string} fromCurrency - Source currency (default: USD)
   * @param {string} toCurrency - Target currency (default: CNY)
   * @returns {Promise<number>} Current exchange rate
   */
  async fetchCurrentRate(fromCurrency = 'USD', toCurrency = 'CNY') {
    try {
      const response = await axios.get(this.apiUrl);
      
      if (response.data && response.data.rates && response.data.rates[toCurrency]) {
        return response.data.rates[toCurrency];
      }

      throw new Error('Rate not found in response');
    } catch (error) {
      console.error(`Error fetching current rate (${fromCurrency}/${toCurrency}):`, error.message);
      throw error;
    }
  }

  /**
   * Fetch exchange rates for a date range
   * @param {string} startDate - Start date in YYYY-MM-DD format
   * @param {string} endDate - End date in YYYY-MM-DD format
   * @param {string} fromCurrency - Source currency (default: USD)
   * @param {string} toCurrency - Target currency (default: CNY)
   * @returns {Promise<Array>} Array of {date, rate} objects
   */
  async fetchRatesByDateRange(startDate, endDate, fromCurrency = 'USD', toCurrency = 'CNY') {
    try {
      const response = await axios.get(`${this.alternativeApiUrl}/${startDate}..${endDate}`, {
        params: {
          from: fromCurrency,
          to: toCurrency
        }
      });

      if (response.data && response.data.rates) {
        const rates = [];
        for (const [date, rateData] of Object.entries(response.data.rates)) {
          rates.push({
            date: date,
            rate: rateData[toCurrency]
          });
        }
        return rates;
      }

      throw new Error('Rates not found in response');
    } catch (error) {
      console.error(`Error fetching rates from ${startDate} to ${endDate} (${fromCurrency}/${toCurrency}):`, error.message);
      throw error;
    }
  }

  /**
   * Get today's date in YYYY-MM-DD format
   * @returns {string} Today's date
   */
  getTodayDate() {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  /**
   * Validate date format (YYYY-MM-DD)
   * @param {string} date - Date string to validate
   * @returns {boolean} True if valid
   */
  isValidDate(date) {
    const regex = /^\d{4}-\d{2}-\d{2}$/;
    if (!regex.test(date)) return false;
    
    const d = new Date(date);
    return d instanceof Date && !isNaN(d);
  }

  /**
   * Validate currency code (3-letter ISO code)
   * @param {string} currency - Currency code to validate
   * @returns {boolean} True if valid
   */
  isValidCurrency(currency) {
    const regex = /^[A-Z]{3}$/;
    return regex.test(currency);
  }
}

module.exports = new ExchangeRateService();
