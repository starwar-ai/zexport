const express = require('express');
const bodyParser = require('body-parser');
const swaggerUi = require('swagger-ui-express');
const swaggerSpec = require('./swagger');
const database = require('./database');
const exchangeRateService = require('./exchangeRateService');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Swagger UI
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec, {
  customSiteTitle: 'Exchange Rate API Documentation',
  customCss: '.swagger-ui .topbar { display: none }'
}));

// Download Swagger JSON specification
app.get('/api-docs.json', (req, res) => {
  res.setHeader('Content-Type', 'application/json');
  res.setHeader('Content-Disposition', 'attachment; filename="exchange-rate-api-swagger.json"');
  res.send(swaggerSpec);
});

// View Swagger JSON specification (without download)
app.get('/swagger.json', (req, res) => {
  res.setHeader('Content-Type', 'application/json');
  res.send(swaggerSpec);
});

// Initialize database
async function initDatabase() {
  try {
    await database.connect();
    database.createTable();
    console.log('Database initialized successfully');
  } catch (error) {
    console.error('Failed to initialize database:', error);
    process.exit(1);
  }
}

/**
 * @swagger
 * /api/rates/fetch:
 *   post:
 *     tags:
 *       - Exchange Rates
 *     summary: Fetch and save exchange rate for a specific date
 *     description: Fetches exchange rate from external API or retrieves from database cache if available
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/FetchRateRequest'
 *     responses:
 *       200:
 *         description: Exchange rate fetched or retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       400:
 *         description: Invalid request parameters
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
app.post('/api/rates/fetch', async (req, res) => {
  try {
    const { date, from = 'USD', to = 'CNY' } = req.body;

    if (!date) {
      return res.status(400).json({ 
        error: 'Date is required',
        example: { date: '2024-01-15', from: 'USD', to: 'CNY' }
      });
    }

    if (!exchangeRateService.isValidDate(date)) {
      return res.status(400).json({ 
        error: 'Invalid date format. Use YYYY-MM-DD' 
      });
    }

    // Validate currency codes
    const fromUpper = from.toUpperCase();
    const toUpper = to.toUpperCase();
    
    if (!exchangeRateService.isValidCurrency(fromUpper)) {
      return res.status(400).json({ 
        error: 'Invalid from currency. Use 3-letter ISO code (e.g., USD)' 
      });
    }
    
    if (!exchangeRateService.isValidCurrency(toUpper)) {
      return res.status(400).json({ 
        error: 'Invalid to currency. Use 3-letter ISO code (e.g., CNY)' 
      });
    }

    // Check database first
    let existingRate = database.getRateByDate(date, fromUpper, toUpper);
    
    if (existingRate) {
      return res.json({
        success: true,
        data: existingRate,
        message: 'Exchange rate retrieved from database',
        source: 'database'
      });
    }

    // Fetch rate from API if not in database
    const rate = await exchangeRateService.fetchRateByDate(date, fromUpper, toUpper);

    // Save to database
    const result = database.saveRate(date, rate, fromUpper, toUpper, 'Frankfurter API');

    res.json({
      success: true,
      data: result,
      message: 'Exchange rate fetched and saved successfully',
      source: 'api'
    });
  } catch (error) {
    console.error('Error in /api/rates/fetch:', error);
    res.status(500).json({ 
      error: 'Failed to fetch exchange rate',
      message: error.message 
    });
  }
});

/**
 * @swagger
 * /api/rates/fetch-range:
 *   post:
 *     tags:
 *       - Exchange Rates
 *     summary: Fetch and save exchange rates for a date range
 *     description: Fetches multiple exchange rates for a date range from external API or retrieves from database cache if available
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/FetchRangeRequest'
 *     responses:
 *       200:
 *         description: Exchange rates fetched or retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       400:
 *         description: Invalid request parameters
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
app.post('/api/rates/fetch-range', async (req, res) => {
  try {
    const { startDate, endDate, from = 'USD', to = 'CNY' } = req.body;

    if (!startDate || !endDate) {
      return res.status(400).json({ 
        error: 'Both startDate and endDate are required',
        example: { startDate: '2024-01-01', endDate: '2024-01-31', from: 'USD', to: 'CNY' }
      });
    }

    if (!exchangeRateService.isValidDate(startDate) || !exchangeRateService.isValidDate(endDate)) {
      return res.status(400).json({ 
        error: 'Invalid date format. Use YYYY-MM-DD' 
      });
    }

    // Validate currency codes
    const fromUpper = from.toUpperCase();
    const toUpper = to.toUpperCase();
    
    if (!exchangeRateService.isValidCurrency(fromUpper)) {
      return res.status(400).json({ 
        error: 'Invalid from currency. Use 3-letter ISO code (e.g., USD)' 
      });
    }
    
    if (!exchangeRateService.isValidCurrency(toUpper)) {
      return res.status(400).json({ 
        error: 'Invalid to currency. Use 3-letter ISO code (e.g., CNY)' 
      });
    }

    // Check database first
    const existingRates = database.getRatesByDateRange(startDate, endDate, fromUpper, toUpper);
    
    if (existingRates && existingRates.length > 0) {
      return res.json({
        success: true,
        data: existingRates,
        count: existingRates.length,
        message: 'Exchange rates retrieved from database',
        source: 'database'
      });
    }

    // Fetch rates from API if not in database
    const rates = await exchangeRateService.fetchRatesByDateRange(startDate, endDate, fromUpper, toUpper);

    // Save all rates to database
    const savedRates = [];
    for (const rateData of rates) {
      const result = database.saveRate(rateData.date, rateData.rate, fromUpper, toUpper, 'Frankfurter API');
      savedRates.push(result);
    }

    res.json({
      success: true,
      data: savedRates,
      count: savedRates.length,
      message: 'Exchange rates fetched and saved successfully',
      source: 'api'
    });
  } catch (error) {
    console.error('Error in /api/rates/fetch-range:', error);
    res.status(500).json({ 
      error: 'Failed to fetch exchange rates',
      message: error.message 
    });
  }
});

/**
 * @swagger
 * /api/rates/{date}:
 *   get:
 *     tags:
 *       - Exchange Rates
 *     summary: Get exchange rate for a specific date from database
 *     description: Retrieves exchange rate from database for a specific date and currency pair
 *     parameters:
 *       - $ref: '#/components/parameters/dateParam'
 *       - $ref: '#/components/parameters/fromCurrencyQuery'
 *       - $ref: '#/components/parameters/toCurrencyQuery'
 *     responses:
 *       200:
 *         description: Exchange rate found
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       400:
 *         description: Invalid request parameters
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       404:
 *         description: Exchange rate not found
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
app.get('/api/rates/:date', async (req, res) => {
  try {
    const { date } = req.params;
    const { from = 'USD', to = 'CNY' } = req.query;

    if (!exchangeRateService.isValidDate(date)) {
      return res.status(400).json({ 
        error: 'Invalid date format. Use YYYY-MM-DD' 
      });
    }

    // Validate currency codes
    const fromUpper = from.toUpperCase();
    const toUpper = to.toUpperCase();
    
    if (!exchangeRateService.isValidCurrency(fromUpper)) {
      return res.status(400).json({ 
        error: 'Invalid from currency. Use 3-letter ISO code (e.g., USD)' 
      });
    }
    
    if (!exchangeRateService.isValidCurrency(toUpper)) {
      return res.status(400).json({ 
        error: 'Invalid to currency. Use 3-letter ISO code (e.g., CNY)' 
      });
    }

    const rate = database.getRateByDate(date, fromUpper, toUpper);

    if (!rate) {
      return res.status(404).json({ 
        error: 'Rate not found for this date and currency pair',
        message: 'Try fetching it first using POST /api/rates/fetch'
      });
    }

    res.json({
      success: true,
      data: rate
    });
  } catch (error) {
    console.error('Error in GET /api/rates/:date:', error);
    res.status(500).json({ 
      error: 'Failed to retrieve exchange rate',
      message: error.message 
    });
  }
});

/**
 * @swagger
 * /api/rates:
 *   get:
 *     tags:
 *       - Exchange Rates
 *     summary: Get all exchange rates or filter by date range and currency pair
 *     description: Retrieves exchange rates from database with optional filters
 *     parameters:
 *       - $ref: '#/components/parameters/startDateQuery'
 *       - $ref: '#/components/parameters/endDateQuery'
 *       - $ref: '#/components/parameters/fromCurrencyQuery'
 *       - $ref: '#/components/parameters/toCurrencyQuery'
 *       - $ref: '#/components/parameters/limitQuery'
 *     responses:
 *       200:
 *         description: Exchange rates retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       400:
 *         description: Invalid request parameters
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
app.get('/api/rates', async (req, res) => {
  try {
    const { startDate, endDate, from = 'USD', to = 'CNY', limit } = req.query;

    let rates;

    if (startDate && endDate) {
      if (!exchangeRateService.isValidDate(startDate) || !exchangeRateService.isValidDate(endDate)) {
        return res.status(400).json({ 
          error: 'Invalid date format. Use YYYY-MM-DD' 
        });
      }
      
      // Validate currency codes
      const fromUpper = from.toUpperCase();
      const toUpper = to.toUpperCase();
      
      if (!exchangeRateService.isValidCurrency(fromUpper)) {
        return res.status(400).json({ 
          error: 'Invalid from currency. Use 3-letter ISO code (e.g., USD)' 
        });
      }
      
      if (!exchangeRateService.isValidCurrency(toUpper)) {
        return res.status(400).json({ 
          error: 'Invalid to currency. Use 3-letter ISO code (e.g., CNY)' 
        });
      }
      
      rates = database.getRatesByDateRange(startDate, endDate, fromUpper, toUpper);
    } else {
      const limitNum = limit ? parseInt(limit) : 100;
      rates = database.getAllRates(limitNum);
    }

    res.json({
      success: true,
      data: rates,
      count: rates.length
    });
  } catch (error) {
    console.error('Error in GET /api/rates:', error);
    res.status(500).json({ 
      error: 'Failed to retrieve exchange rates',
      message: error.message 
    });
  }
});

/**
 * @swagger
 * /api/rates/current/fetch:
 *   get:
 *     tags:
 *       - Exchange Rates
 *     summary: Fetch and save current exchange rate
 *     description: Fetches current exchange rate from external API or retrieves from database cache if available for today
 *     parameters:
 *       - $ref: '#/components/parameters/fromCurrencyQuery'
 *       - $ref: '#/components/parameters/toCurrencyQuery'
 *     responses:
 *       200:
 *         description: Current exchange rate fetched or retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       400:
 *         description: Invalid request parameters
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
app.get('/api/rates/current/fetch', async (req, res) => {
  try {
    const { from = 'USD', to = 'CNY' } = req.query;
    const date = exchangeRateService.getTodayDate();
    
    // Validate currency codes
    const fromUpper = from.toUpperCase();
    const toUpper = to.toUpperCase();
    
    if (!exchangeRateService.isValidCurrency(fromUpper)) {
      return res.status(400).json({ 
        error: 'Invalid from currency. Use 3-letter ISO code (e.g., USD)' 
      });
    }
    
    if (!exchangeRateService.isValidCurrency(toUpper)) {
      return res.status(400).json({ 
        error: 'Invalid to currency. Use 3-letter ISO code (e.g., CNY)' 
      });
    }
    
    // Check database first
    let existingRate = database.getRateByDate(date, fromUpper, toUpper);
    
    if (existingRate) {
      return res.json({
        success: true,
        data: existingRate,
        message: 'Current exchange rate retrieved from database',
        source: 'database'
      });
    }
    
    // Fetch from API if not in database
    const rate = await exchangeRateService.fetchCurrentRate(fromUpper, toUpper);
    const result = database.saveRate(date, rate, fromUpper, toUpper, 'Exchange Rate API');

    res.json({
      success: true,
      data: result,
      message: 'Current exchange rate fetched and saved successfully',
      source: 'api'
    });
  } catch (error) {
    console.error('Error in /api/rates/current/fetch:', error);
    res.status(500).json({ 
      error: 'Failed to fetch current exchange rate',
      message: error.message 
    });
  }
});

/**
 * @swagger
 * /:
 *   get:
 *     tags:
 *       - General
 *     summary: API documentation overview
 *     description: Returns API information and available endpoints
 *     responses:
 *       200:
 *         description: API information
 */
app.get('/', (req, res) => {
  res.json({
    message: 'Multi-Currency Exchange Rate API',
    version: '2.1.0',
    documentation: {
      swagger: 'http://localhost:3000/api-docs',
      description: 'Interactive API documentation with Swagger UI',
      downloads: {
        json: 'http://localhost:3000/api-docs.json',
        view: 'http://localhost:3000/swagger.json'
      }
    },
    endpoints: {
      'POST /api/rates/fetch': {
        description: 'Fetch and save exchange rate for a specific date',
        body: { date: 'YYYY-MM-DD', from: 'USD', to: 'CNY' },
        example: 'curl -X POST http://localhost:3000/api/rates/fetch -H "Content-Type: application/json" -d "{\\"date\\":\\"2024-01-15\\"}"'
      },
      'POST /api/rates/fetch-range': {
        description: 'Fetch and save exchange rates for a date range',
        body: { startDate: 'YYYY-MM-DD', endDate: 'YYYY-MM-DD', from: 'USD', to: 'CNY' },
        example: 'curl -X POST http://localhost:3000/api/rates/fetch-range -H "Content-Type: application/json" -d "{\\"startDate\\":\\"2024-01-01\\",\\"endDate\\":\\"2024-01-31\\"}"'
      },
      'GET /api/rates/:date': {
        description: 'Get exchange rate for a specific date from database',
        query: 'from, to',
        example: 'curl "http://localhost:3000/api/rates/2024-01-15?from=USD&to=CNY"'
      },
      'GET /api/rates': {
        description: 'Get all exchange rates or filter by date range and currency pair',
        query: 'startDate, endDate, from, to, limit',
        examples: [
          'curl http://localhost:3000/api/rates',
          'curl "http://localhost:3000/api/rates?startDate=2024-01-01&endDate=2024-01-31&from=USD&to=CNY"',
          'curl "http://localhost:3000/api/rates?limit=50"'
        ]
      },
      'GET /api/rates/current/fetch': {
        description: 'Fetch and save current exchange rate',
        query: 'from, to',
        example: 'curl "http://localhost:3000/api/rates/current/fetch?from=USD&to=CNY"'
      }
    },
    supportedCurrencies: 'Any valid 3-letter ISO currency code (e.g., USD, CNY, EUR, GBP, JPY, etc.)',
    defaultCurrencyPair: 'USD/CNY'
  });
});

// Start server
async function startServer() {
  await initDatabase();
  
  app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
    console.log(`API documentation available at http://localhost:${PORT}`);
    console.log(`Swagger UI available at http://localhost:${PORT}/api-docs`);
  });
}

startServer();

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\nShutting down gracefully...');
  database.close();
  process.exit(0);
});
