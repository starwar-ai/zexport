const swaggerJsdoc = require('swagger-jsdoc');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Multi-Currency Exchange Rate API',
      version: '2.1.0',
      description: `A Node.js API for fetching and storing exchange rates for multiple currency pairs using SQLite database with cache-first strategy.

**Features:**
- Multi-currency support (USD, CNY, EUR, GBP, JPY, etc.)
- Database-first caching strategy
- CSV import support for bulk data loading
- Historical and current exchange rate fetching
- Date range queries

**Data Sources:**
- Frankfurter API (historical rates)
- Exchange Rate API (current rates)
- CSV imports (bulk data)

**Database:** SQLite (exchange_rates.db)`,
      contact: {
        name: 'API Support'
      }
    },
    servers: [
      {
        url: 'http://localhost:3000',
        description: 'Development server'
      }
    ],
    tags: [
      {
        name: 'Exchange Rates',
        description: 'Exchange rate fetching, storing, and querying operations'
      },
      {
        name: 'General',
        description: 'General API information and documentation'
      }
    ],
    components: {
      schemas: {
        ExchangeRate: {
          type: 'object',
          properties: {
            id: {
              type: 'integer',
              description: 'Database record ID',
              example: 1
            },
            date: {
              type: 'string',
              format: 'date',
              description: 'Exchange rate date',
              example: '2024-01-15'
            },
            from_currency: {
              type: 'string',
              description: 'Source currency (3-letter ISO code)',
              example: 'USD',
              pattern: '^[A-Z]{3}$'
            },
            to_currency: {
              type: 'string',
              description: 'Target currency (3-letter ISO code)',
              example: 'CNY',
              pattern: '^[A-Z]{3}$'
            },
            rate: {
              type: 'number',
              format: 'float',
              description: 'Exchange rate',
              example: 7.1749
            },
            source: {
              type: 'string',
              description: 'Data source',
              enum: ['Frankfurter API', 'Exchange Rate API', 'CSV_IMPORT', 'AUTO', 'MANUAL'],
              example: 'Frankfurter API'
            },
            created_at: {
              type: 'string',
              format: 'date-time',
              description: 'Record creation timestamp',
              example: '2024-01-15T10:30:00Z'
            }
          }
        },
        SuccessResponse: {
          type: 'object',
          properties: {
            success: {
              type: 'boolean',
              example: true
            },
            data: {
              oneOf: [
                { $ref: '#/components/schemas/ExchangeRate' },
                {
                  type: 'array',
                  items: { $ref: '#/components/schemas/ExchangeRate' }
                }
              ]
            },
            message: {
              type: 'string',
              example: 'Exchange rate fetched and saved successfully'
            },
            source: {
              type: 'string',
              enum: ['api', 'database'],
              description: 'Data source (api or database cache)',
              example: 'api'
            },
            count: {
              type: 'integer',
              description: 'Number of records (for array responses)',
              example: 10
            }
          }
        },
        ErrorResponse: {
          type: 'object',
          properties: {
            error: {
              type: 'string',
              example: 'Invalid date format'
            },
            message: {
              type: 'string',
              example: 'Use YYYY-MM-DD format'
            }
          }
        },
        FetchRateRequest: {
          type: 'object',
          required: ['date'],
          properties: {
            date: {
              type: 'string',
              format: 'date',
              description: 'Date in YYYY-MM-DD format',
              example: '2024-01-15'
            },
            from: {
              type: 'string',
              description: 'Source currency (3-letter ISO code)',
              default: 'USD',
              example: 'USD',
              pattern: '^[A-Z]{3}$'
            },
            to: {
              type: 'string',
              description: 'Target currency (3-letter ISO code)',
              default: 'CNY',
              example: 'CNY',
              pattern: '^[A-Z]{3}$'
            }
          }
        },
        FetchRangeRequest: {
          type: 'object',
          required: ['startDate', 'endDate'],
          properties: {
            startDate: {
              type: 'string',
              format: 'date',
              description: 'Start date in YYYY-MM-DD format',
              example: '2024-01-01'
            },
            endDate: {
              type: 'string',
              format: 'date',
              description: 'End date in YYYY-MM-DD format',
              example: '2024-01-31'
            },
            from: {
              type: 'string',
              description: 'Source currency (3-letter ISO code)',
              default: 'USD',
              example: 'USD',
              pattern: '^[A-Z]{3}$'
            },
            to: {
              type: 'string',
              description: 'Target currency (3-letter ISO code)',
              default: 'CNY',
              example: 'CNY',
              pattern: '^[A-Z]{3}$'
            }
          }
        }
      },
      parameters: {
        dateParam: {
          name: 'date',
          in: 'path',
          required: true,
          schema: {
            type: 'string',
            format: 'date',
            example: '2024-01-15'
          },
          description: 'Date in YYYY-MM-DD format'
        },
        fromCurrencyQuery: {
          name: 'from',
          in: 'query',
          schema: {
            type: 'string',
            default: 'USD',
            example: 'USD',
            pattern: '^[A-Z]{3}$'
          },
          description: 'Source currency (3-letter ISO code)'
        },
        toCurrencyQuery: {
          name: 'to',
          in: 'query',
          schema: {
            type: 'string',
            default: 'CNY',
            example: 'CNY',
            pattern: '^[A-Z]{3}$'
          },
          description: 'Target currency (3-letter ISO code)'
        },
        startDateQuery: {
          name: 'startDate',
          in: 'query',
          schema: {
            type: 'string',
            format: 'date',
            example: '2024-01-01'
          },
          description: 'Start date for range query'
        },
        endDateQuery: {
          name: 'endDate',
          in: 'query',
          schema: {
            type: 'string',
            format: 'date',
            example: '2024-01-31'
          },
          description: 'End date for range query'
        },
        limitQuery: {
          name: 'limit',
          in: 'query',
          schema: {
            type: 'integer',
            default: 100,
            example: 50
          },
          description: 'Maximum number of records to return'
        }
      }
    }
  },
  apis: ['./index.js']
};

module.exports = swaggerJsdoc(options);
