const express = require('express');
const router = express.Router();
const dbManager = require('../config/database');
const { asyncHandler } = require('../middleware/errorHandler');

/**
 * @swagger
 * /api/query/execute:
 *   post:
 *     summary: Execute a custom SQL query
 *     tags: [Queries]
 *     description: Execute a custom SQL query on a specified database with optional parameterized values
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/QueryRequest'
 *           examples:
 *             selectQuery:
 *               summary: SELECT query
 *               value:
 *                 database: local
 *                 sql: SELECT * FROM users WHERE status = ?
 *                 params: ["active"]
 *             insertQuery:
 *               summary: INSERT query
 *               value:
 *                 database: local
 *                 sql: INSERT INTO users (username, email, password) VALUES (?, ?, ?)
 *                 params: ["john_doe", "john@example.com", "hashed_pass"]
 *     responses:
 *       200:
 *         description: Query executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                       example: local
 *                     queryType:
 *                       type: string
 *                       example: SELECT
 *                     rowCount:
 *                       type: number
 *                       example: 2
 *                     affectedRows:
 *                       type: number
 *                     insertId:
 *                       type: number
 *                     results:
 *                       type: array
 *                       items:
 *                         type: object
 *       400:
 *         description: Bad request - missing required fields
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/execute', asyncHandler(async (req, res) => {
  const { database, sql, params = [] } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!sql) {
    return res.status(400).json({
      success: false,
      error: { message: 'SQL query is required' }
    });
  }

  // Get database pool
  const pool = dbManager.getPool(database);

  // Execute query
  const [results] = await pool.query(sql, params);

  // Determine query type
  const queryType = sql.trim().split(' ')[0].toUpperCase();
  const isSelect = queryType === 'SELECT' || queryType === 'SHOW' || queryType === 'DESCRIBE';

  res.json({
    success: true,
    data: {
      database,
      queryType,
      rowCount: Array.isArray(results) ? results.length : 0,
      affectedRows: results.affectedRows,
      insertId: results.insertId,
      results: isSelect ? results : undefined
    }
  });
}));

/**
 * @swagger
 * /api/query/batch:
 *   post:
 *     summary: Execute multiple queries in a transaction
 *     tags: [Queries]
 *     description: Execute multiple SQL queries in a transaction. All queries will be rolled back if any query fails.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/BatchQueryRequest'
    *           example:
  *             database: local
  *             queries:
  *               - sql: INSERT INTO users (username, email, password) VALUES (?, ?, ?)
  *                 params: ["new_user", "new@example.com", "hashed_pass"]
  *               - sql: UPDATE products SET stock = stock - 1 WHERE id = ?
  *                 params: [1]
 *     responses:
 *       200:
 *         description: Batch queries executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                       example: local
 *                     totalQueries:
 *                       type: number
 *                       example: 2
 *                     results:
 *                       type: array
 *                       items:
 *                         type: object
 *                         properties:
 *                           index:
 *                             type: number
 *                           affectedRows:
 *                             type: number
 *                           insertId:
 *                             type: number
 *       400:
 *         description: Bad request
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error (transaction rolled back)
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/batch', asyncHandler(async (req, res) => {
  const { database, queries } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!queries || !Array.isArray(queries) || queries.length === 0) {
    return res.status(400).json({
      success: false,
      error: { message: 'Queries array is required and must not be empty' }
    });
  }

  // Get database pool and connection
  const pool = dbManager.getPool(database);
  const connection = await pool.getConnection();

  try {
    // Start transaction
    await connection.beginTransaction();

    const results = [];

    // Execute each query
    for (let i = 0; i < queries.length; i++) {
      const query = queries[i];
      
      if (!query.sql) {
        throw new Error(`Query at index ${i} is missing SQL statement`);
      }

      const [result] = await connection.query(query.sql, query.params || []);
      
      results.push({
        index: i,
        affectedRows: result.affectedRows,
        insertId: result.insertId
      });
    }

    // Commit transaction
    await connection.commit();

    res.json({
      success: true,
      data: {
        database,
        totalQueries: queries.length,
        results
      }
    });
  } catch (error) {
    // Rollback on error
    await connection.rollback();
    throw error;
  } finally {
    // Release connection
    connection.release();
  }
}));

/**
 * @swagger
 * /api/query/prepared:
 *   post:
 *     summary: Execute a prepared statement
 *     tags: [Queries]
 *     description: Execute a prepared statement with parameters (recommended for user input to prevent SQL injection)
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/QueryRequest'
 *             example:
 *             database: local
 *             sql: SELECT * FROM users WHERE email = ? AND status = ?
 *             params: ["john@example.com", "active"]
 *     responses:
 *       200:
 *         description: Prepared statement executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                     queryType:
 *                       type: string
 *                     rowCount:
 *                       type: number
 *                     affectedRows:
 *                       type: number
 *                     insertId:
 *                       type: number
 *                     results:
 *                       type: array
 *                       items:
 *                         type: object
 *       400:
 *         description: Bad request
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/prepared', asyncHandler(async (req, res) => {
  const { database, sql, params = [] } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!sql) {
    return res.status(400).json({
      success: false,
      error: { message: 'SQL query is required' }
    });
  }

  // Get database pool
  const pool = dbManager.getPool(database);

  // Execute prepared statement
  const [results] = await pool.execute(sql, params);

  // Determine query type
  const queryType = sql.trim().split(' ')[0].toUpperCase();
  const isSelect = queryType === 'SELECT' || queryType === 'SHOW' || queryType === 'DESCRIBE';

  res.json({
    success: true,
    data: {
      database,
      queryType,
      rowCount: Array.isArray(results) ? results.length : 0,
      affectedRows: results.affectedRows,
      insertId: results.insertId,
      results: isSelect ? results : undefined
    }
  });
}));

/**
 * @swagger
 * /api/query/delayed-signback-contracts:
 *   post:
 *     summary: Query purchase contracts with delayed sign-back (15+ days)
 *     tags: [Queries]
 *     description: Find purchase contracts where the sign-back date (回签日期) is 15+ days later than the creation date within a specified period
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - database
 *               - startDate
 *               - endDate
 *             properties:
 *               database:
 *                 type: string
 *                 description: Database name
 *                 example: local
 *               startDate:
 *                 type: string
 *                 format: date
 *                 description: Start date of the query period (YYYY-MM-DD)
 *                 example: "2024-01-01"
 *               endDate:
 *                 type: string
 *                 format: date
 *                 description: End date of the query period (YYYY-MM-DD)
 *                 example: "2024-12-31"
 *               minDays:
 *                 type: integer
 *                 description: Minimum delay days (default 15)
 *                 example: 15
 *                 default: 15
 *     responses:
 *       200:
 *         description: Query executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                       example: local
 *                     period:
 *                       type: object
 *                       properties:
 *                         startDate:
 *                           type: string
 *                         endDate:
 *                           type: string
 *                     minDelayDays:
 *                       type: integer
 *                       example: 15
 *                     totalCount:
 *                       type: integer
 *                       example: 5
 *                     contracts:
 *                       type: array
 *                       items:
 *                         type: object
 *                         properties:
 *                           id:
 *                             type: integer
 *                             description: Contract ID
 *                           code:
 *                             type: string
 *                             description: Contract code
 *                           vender_name:
 *                             type: string
 *                             description: Vendor name
 *                           create_time:
 *                             type: string
 *                             format: date-time
 *                             description: Creation date
 *                           sign_back_time:
 *                             type: string
 *                             format: date-time
 *                             description: Sign-back date
 *                           delay_days:
 *                             type: integer
 *                             description: Number of days between creation and sign-back
 *                           total_amount:
 *                             type: object
 *                             description: Total amount (JSON)
 *       400:
 *         description: Bad request - missing required fields
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/delayed-signback-purchase-contracts', asyncHandler(async (req, res) => {
  const { database, startDate, endDate, minDays = 15 } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!startDate || !endDate) {
    return res.status(400).json({
      success: false,
      error: { message: 'Start date and end date are required' }
    });
  }

  // Build SQL query
  const sql = `
    SELECT 
      id,
      code,
      vender_name,
      create_time,
      sign_back_time,
      DATEDIFF(sign_back_time, create_time) as delay_days,
      total_amount,
      vender_code,
      company_id,
      contract_status,
      audit_status
    FROM scm_purchase_contract
    WHERE deleted = 0
      AND create_time >= ?
      AND create_time <= ?
      AND sign_back_time IS NOT NULL
      AND DATEDIFF(sign_back_time, create_time) >= ?
    ORDER BY delay_days DESC, create_time DESC
  `;

  // Get database pool
  const pool = dbManager.getPool(database);

  // Execute query with parameters
  const [results] = await pool.execute(sql, [
    `${startDate} 00:00:00`,
    `${endDate} 23:59:59`,
    minDays
  ]);

  res.json({
    success: true,
    data: {
      database,
      period: {
        startDate,
        endDate
      },
      minDelayDays: minDays,
      totalCount: results.length,
      contracts: results
    }
  });
}));

/**
 * @swagger
 * /api/query/purchase-contracts-with-advantage-products:
 *   post:
 *     summary: Query purchase contract items for advantaged products
 *     tags: [Queries]
 *     description: Find all purchase contract line items for advantaged products (优势产品) within a specified time period
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - database
 *               - startDate
 *               - endDate
 *             properties:
 *               database:
 *                 type: string
 *                 description: Database name
 *                 example: local
 *               startDate:
 *                 type: string
 *                 format: date
 *                 description: Start date of the query period (YYYY-MM-DD)
 *                 example: "2024-01-01"
 *               endDate:
 *                 type: string
 *                 format: date
 *                 description: End date of the query period (YYYY-MM-DD)
 *                 example: "2024-12-31"
 *     responses:
 *       200:
 *         description: Query executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                       example: local
 *                     period:
 *                       type: object
 *                       properties:
 *                         startDate:
 *                           type: string
 *                         endDate:
 *                           type: string
 *                     totalCount:
 *                       type: integer
 *                       example: 50
 *                     items:
 *                       type: array
 *                       items:
 *                         type: object
 *                         properties:
 *                           item_id:
 *                             type: integer
 *                             description: Contract item ID
 *                           purchase_contract_id:
 *                             type: integer
 *                             description: Purchase contract ID
 *                           purchase_contract_code:
 *                             type: string
 *                             description: Purchase contract code
 *                           sku_id:
 *                             type: integer
 *                             description: Product SKU ID
 *                           sku_code:
 *                             type: string
 *                             description: Product SKU code
 *                           sku_name:
 *                             type: string
 *                             description: Product name
 *                           csku_code:
 *                             type: string
 *                             description: Customer SKU code
 *                           advantage_flag:
 *                             type: integer
 *                             description: Advantage product flag (1=yes)
 *                           quantity:
 *                             type: integer
 *                             description: Purchase quantity
 *                           unit_price:
 *                             type: object
 *                             description: Unit price (JSON)
 *                           total_price:
 *                             type: object
 *                             description: Total price (JSON)
 *                           with_tax_price:
 *                             type: object
 *                             description: Price with tax (JSON)
 *                           total_price_rmb:
 *                             type: object
 *                             description: Total price in RMB (JSON)
 *                           currency:
 *                             type: string
 *                             description: Currency code
 *                           vender_name:
 *                             type: string
 *                             description: Vendor name
 *                           purchase_user_name:
 *                             type: string
 *                             description: Purchase user name
 *                           tax_rate:
 *                             type: number
 *                             description: Tax rate
 *                           sale_contract_code:
 *                             type: string
 *                             description: Related sale contract code
 *                           cust_name:
 *                             type: string
 *                             description: Customer name
 *                           purchase_time:
 *                             type: string
 *                             format: date-time
 *                             description: Purchase date
 *                           create_time:
 *                             type: string
 *                             format: date-time
 *                             description: Item creation date
 *       400:
 *         description: Bad request - missing required fields
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/purchase-contracts-with-advantage-products', asyncHandler(async (req, res) => {
  const { database, startDate, endDate } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!startDate || !endDate) {
    return res.status(400).json({
      success: false,
      error: { message: 'Start date and end date are required' }
    });
  }

  // Build SQL query - returns detailed items for advantaged products
  const sql = `
    SELECT 
      pci.id as item_id,
      pci.purchase_contract_id,
      pci.purchase_contract_code,
      pc.purchase_time,
      pci.sku_id,
      pci.sku_code,
      pci.sku_name,
      pci.csku_code,
      ps.advantage_flag,
      pci.quantity,
      pci.unit_price,
      pci.total_price,
      pci.with_tax_price,
      pci.total_price_rmb,
      pci.currency,
      pc.vender_name,
      pci.purchase_user_name,
      pci.tax_rate,
      pci.sale_contract_code,
      pci.cust_name,
      pci.create_time
    FROM scm_purchase_contract_item pci
    INNER JOIN scm_purchase_contract pc ON pci.purchase_contract_id = pc.id
    INNER JOIN pms_sku ps ON pci.sku_code = ps.code
    WHERE pci.deleted = 0
      AND pc.deleted = 0
      AND ps.deleted = 0
      AND ps.advantage_flag = 1
      AND pc.purchase_time >= ?
      AND pc.purchase_time <= ?
    ORDER BY pc.purchase_time DESC, pci.id DESC
  `;

  // Get database pool
  const pool = dbManager.getPool(database);

  // Execute query with parameters
  const [results] = await pool.execute(sql, [
    `${startDate} 00:00:00`,
    `${endDate} 23:59:59`
  ]);

  res.json({
    success: true,
    data: {
      database,
      period: {
        startDate,
        endDate
      },
      totalCount: results.length,
      items: results
    }
  });
}));

/**
 * @swagger
 * /api/query/delayed-signback-sale-contracts:
 *   post:
 *     summary: Query sales contracts with delayed sign-back (15+ days)
 *     tags: [Queries]
 *     description: Find sales contracts (外销合同) where the sign-back date (回签日期) is 15+ days later than the creation date within a specified period
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - database
 *               - startDate
 *               - endDate
 *             properties:
 *               database:
 *                 type: string
 *                 description: Database name
 *                 example: local
 *               startDate:
 *                 type: string
 *                 format: date
 *                 description: Start date of the query period (YYYY-MM-DD)
 *                 example: "2024-01-01"
 *               endDate:
 *                 type: string
 *                 format: date
 *                 description: End date of the query period (YYYY-MM-DD)
 *                 example: "2024-12-31"
 *               minDays:
 *                 type: integer
 *                 description: Minimum delay days (default 15)
 *                 example: 15
 *                 default: 15
 *     responses:
 *       200:
 *         description: Query executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                       example: local
 *                     period:
 *                       type: object
 *                       properties:
 *                         startDate:
 *                           type: string
 *                         endDate:
 *                           type: string
 *                     minDelayDays:
 *                       type: integer
 *                       example: 15
 *                     totalCount:
 *                       type: integer
 *                       example: 5
 *                     contracts:
 *                       type: array
 *                       items:
 *                         type: object
 *                         properties:
 *                           id:
 *                             type: integer
 *                             description: Contract ID
 *                           code:
 *                             type: string
 *                             description: Contract code
 *                           cust_name:
 *                             type: string
 *                             description: Customer name
 *                           create_time:
 *                             type: string
 *                             format: date-time
 *                             description: Creation date
 *                           sign_back_date:
 *                             type: string
 *                             format: date-time
 *                             description: Sign-back date
 *                           delay_days:
 *                             type: integer
 *                             description: Number of days between creation and sign-back
 *                           total_amount:
 *                             type: object
 *                             description: Total amount (JSON)
 *       400:
 *         description: Bad request - missing required fields
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/delayed-signback-sale-contracts', asyncHandler(async (req, res) => {
  const { database, startDate, endDate, minDays = 15 } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!startDate || !endDate) {
    return res.status(400).json({
      success: false,
      error: { message: 'Start date and end date are required' }
    });
  }

  // Build SQL query
  const sql = `
    SELECT 
      id,
      code,
      cust_name,
      create_time,
      sign_back_date,
      DATEDIFF(sign_back_date, create_time) as delay_days,
      total_amount,
      cust_code,
      company_id,
      status,
      audit_status
    FROM sms_sale_contract
    WHERE deleted = 0
      AND create_time >= ?
      AND create_time <= ?
      AND sign_back_date IS NOT NULL
      AND DATEDIFF(sign_back_date, create_time) >= ?
    ORDER BY delay_days DESC, create_time DESC
  `;

  // Get database pool
  const pool = dbManager.getPool(database);

  // Execute query with parameters
  const [results] = await pool.execute(sql, [
    `${startDate} 00:00:00`,
    `${endDate} 23:59:59`,
    minDays
  ]);

  res.json({
    success: true,
    data: {
      database,
      period: {
        startDate,
        endDate
      },
      minDelayDays: minDays,
      totalCount: results.length,
      contracts: results
    }
  });
}));

/**
 * @swagger
 * /api/query/purchase-contracts-by-merchandiser:
 *   post:
 *     summary: Query purchase contracts by date range and merchandiser
 *     tags: [Queries]
 *     description: Find purchase contracts within a specified time period, optionally filtered by merchandiser (跟单员)
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - database
 *               - startDate
 *               - endDate
 *             properties:
 *               database:
 *                 type: string
 *                 description: Database name
 *                 example: local
 *               startDate:
 *                 type: string
 *                 format: date
 *                 description: Start date of the query period (YYYY-MM-DD)
 *                 example: "2024-01-01"
 *               endDate:
 *                 type: string
 *                 format: date
 *                 description: End date of the query period (YYYY-MM-DD)
 *                 example: "2024-12-31"
 *               merchandiserId:
 *                 type: integer
 *                 description: Merchandiser user ID (optional filter)
 *                 example: 123
 *               merchandiserName:
 *                 type: string
 *                 description: Merchandiser name (optional filter, fuzzy match)
 *                 example: "张三"
 *     responses:
 *       200:
 *         description: Query executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                       example: local
 *                     period:
 *                       type: object
 *                       properties:
 *                         startDate:
 *                           type: string
 *                         endDate:
 *                           type: string
 *                     filters:
 *                       type: object
 *                       properties:
 *                         merchandiserId:
 *                           type: integer
 *                         merchandiserName:
 *                           type: string
 *                     totalCount:
 *                       type: integer
 *                       example: 25
 *                     contracts:
 *                       type: array
 *                       items:
 *                         type: object
 *                         properties:
 *                           id:
 *                             type: integer
 *                             description: Contract ID
 *                           code:
 *                             type: string
 *                             description: Contract code
 *                           vender_name:
 *                             type: string
 *                             description: Vendor name
 *                           vender_code:
 *                             type: string
 *                             description: Vendor code
 *                           purchase_time:
 *                             type: string
 *                             format: date-time
 *                             description: Purchase date
 *                           create_time:
 *                             type: string
 *                             format: date-time
 *                             description: Creation date
 *                           total_amount:
 *                             type: object
 *                             description: Total amount (JSON)
 *                           currency:
 *                             type: string
 *                             description: Currency code
 *                           manager:
 *                             type: object
 *                             description: Merchandiser info (JSON)
 *                           sales:
 *                             type: object
 *                             description: Sales personnel (JSON)
 *                           contract_status:
 *                             type: integer
 *                             description: Contract status
 *                           audit_status:
 *                             type: integer
 *                             description: Audit status
 *                           company_id:
 *                             type: integer
 *                             description: Company ID
 *       400:
 *         description: Bad request - missing required fields
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/purchase-contracts-by-merchandiser', asyncHandler(async (req, res) => {
  const { database, startDate, endDate, merchandiserId, merchandiserName } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!startDate || !endDate) {
    return res.status(400).json({
      success: false,
      error: { message: 'Start date and end date are required' }
    });
  }

  // Build SQL query with optional merchandiser filter
  let sql = `
    SELECT 
      id,
      code,
      vender_name,
      vender_code,
      vender_id,
      purchase_time,
      create_time,
      total_amount,
      total_amount_rmb,
      currency,
      manager,
      sales,
      purchase_user_id,
      purchase_user_name,
      contract_status,
      audit_status,
      company_id,
      tax_rate,
      sign_back_time,
      remark
    FROM scm_purchase_contract
    WHERE deleted = 0
      AND purchase_time >= ?
      AND purchase_time <= ?
  `;

  const params = [
    `${startDate} 00:00:00`,
    `${endDate} 23:59:59`
  ];

  // Add merchandiser filter if provided
  if (merchandiserId) {
    sql += ` AND JSON_EXTRACT(manager, '$.id') = ?`;
    params.push(merchandiserId);
  }

  if (merchandiserName) {
    sql += ` AND JSON_EXTRACT(manager, '$.nickname') LIKE ?`;
    params.push(`%${merchandiserName}%`);
  }

  sql += ` ORDER BY purchase_time DESC, id DESC`;

  // Get database pool
  const pool = dbManager.getPool(database);

  // Execute query with parameters
  const [results] = await pool.execute(sql, params);

  res.json({
    success: true,
    data: {
      database,
      period: {
        startDate,
        endDate
      },
      filters: {
        merchandiserId: merchandiserId || null,
        merchandiserName: merchandiserName || null
      },
      totalCount: results.length,
      contracts: results
    }
  });
}));

/**
 * @swagger
 * /api/query/sale-contracts-with-advantage-products:
 *   post:
 *     summary: Query sales contract items for advantaged products
 *     tags: [Queries]
 *     description: Find all sales contract line items for advantaged products (优势产品) within a specified time period
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - database
 *               - startDate
 *               - endDate
 *             properties:
 *               database:
 *                 type: string
 *                 description: Database name
 *                 example: local
 *               startDate:
 *                 type: string
 *                 format: date
 *                 description: Start date of the query period (YYYY-MM-DD)
 *                 example: "2024-01-01"
 *               endDate:
 *                 type: string
 *                 format: date
 *                 description: End date of the query period (YYYY-MM-DD)
 *                 example: "2024-12-31"
 *     responses:
 *       200:
 *         description: Query executed successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                   example: true
 *                 data:
 *                   type: object
 *                   properties:
 *                     database:
 *                       type: string
 *                       example: local
 *                     period:
 *                       type: object
 *                       properties:
 *                         startDate:
 *                           type: string
 *                         endDate:
 *                           type: string
 *                     totalCount:
 *                       type: integer
 *                       example: 50
 *                     items:
 *                       type: array
 *                       items:
 *                         type: object
 *                         properties:
 *                           item_id:
 *                             type: integer
 *                             description: Contract item ID
 *                           sale_contract_id:
 *                             type: integer
 *                             description: Sales contract ID
 *                           sale_contract_code:
 *                             type: string
 *                             description: Sales contract code
 *                           sale_contract_date:
 *                             type: string
 *                             format: date-time
 *                             description: Sales contract date
 *                           sku_id:
 *                             type: integer
 *                             description: Product SKU ID
 *                           sku_code:
 *                             type: string
 *                             description: Product SKU code
 *                           sku_name:
 *                             type: string
 *                             description: Product name
 *                           csku_code:
 *                             type: string
 *                             description: Customer SKU code
 *                           advantage_flag:
 *                             type: integer
 *                             description: Advantage product flag (1=yes)
 *                           quantity:
 *                             type: integer
 *                             description: Sales quantity
 *                           unit_price:
 *                             type: object
 *                             description: Unit price (JSON)
 *                           total_sale_amount:
 *                             type: object
 *                             description: Total sales amount (JSON)
 *                           purchase_with_tax_price:
 *                             type: object
 *                             description: Purchase price with tax (JSON)
 *                           total_sale_amount_usd:
 *                             type: object
 *                             description: Total sales amount in USD (JSON)
 *                           currency:
 *                             type: string
 *                             description: Currency code
 *                           cust_name:
 *                             type: string
 *                             description: Customer name
 *                           sales:
 *                             type: object
 *                             description: Sales personnel (JSON)
 *                           tax_rate:
 *                             type: number
 *                             description: Tax rate
 *                           vender_name:
 *                             type: string
 *                             description: Vendor name
 *                           manager:
 *                             type: object
 *                             description: Merchandiser (JSON)
 *                           create_time:
 *                             type: string
 *                             format: date-time
 *                             description: Creation date
 *       400:
 *         description: Bad request - missing required fields
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/sale-contracts-with-advantage-products', asyncHandler(async (req, res) => {
  const { database, startDate, endDate } = req.body;

  // Validation
  if (!database) {
    return res.status(400).json({
      success: false,
      error: { message: 'Database name is required' }
    });
  }

  if (!startDate || !endDate) {
    return res.status(400).json({
      success: false,
      error: { message: 'Start date and end date are required' }
    });
  }

  // Build SQL query - returns detailed items for advantaged products
  const sql = `
    SELECT 
      sci.id as item_id,
      sci.contract_id as sale_contract_id,
      sci.contract_code as sale_contract_code,
      sc.sale_contract_date,
      sci.sku_id,
      sci.sku_code,
      sci.name as sku_name,
      sci.csku_code,
      ps.advantage_flag,
      sci.quantity,
      sci.unit_price,
      sci.total_sale_amount,
      sci.purchase_with_tax_price,
      sci.total_sale_amount_usd,
      sc.currency,
      sc.cust_name,
      sc.sales,
      sci.tax_rate,
      sci.vender_name,
      sci.manager,
      sc.create_time
    FROM sms_sale_contract_item sci
    INNER JOIN sms_sale_contract sc ON sci.contract_id = sc.id
    INNER JOIN pms_sku ps ON sci.sku_code = ps.code
    WHERE sci.deleted = 0
      AND sc.deleted = 0
      AND ps.deleted = 0
      AND ps.advantage_flag = 1
      AND sc.sale_contract_date >= ?
      AND sc.sale_contract_date <= ?
    ORDER BY sc.sale_contract_date DESC, sci.id DESC
  `;

  // Get database pool
  const pool = dbManager.getPool(database);

  // Execute query with parameters
  const [results] = await pool.execute(sql, [
    `${startDate} 00:00:00`,
    `${endDate} 23:59:59`
  ]);

  res.json({
    success: true,
    data: {
      database,
      period: {
        startDate,
        endDate
      },
      totalCount: results.length,
      items: results
    }
  });
}));

module.exports = router;
