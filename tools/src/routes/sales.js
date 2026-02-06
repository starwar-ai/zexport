const express = require('express');
const router = express.Router();
const dbManager = require('../config/database');
const { asyncHandler } = require('../middleware/errorHandler');

function isValidYYYYMMDD(dateStr) {
  if (typeof dateStr !== 'string') return false;
  if (!/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) return false;
  const d = new Date(`${dateStr}T00:00:00.000Z`);
  return !Number.isNaN(d.getTime());
}

/**
 * @swagger
 * /api/sales/batch-set-sign-back-date:
 *   post:
 *     summary: Batch set sign-back date for sales contracts
 *     tags: [Sales]
 *     description: Batch update sms_sale_contract.sign_back_date by contract code (输入数据结构参考 tools/data.json)
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             oneOf:
 *               - type: object
 *                 required:
 *                   - database
 *                   - contracts
 *                 properties:
 *                   database:
 *                     type: string
 *                     description: Database name
 *                     example: local
 *                   contracts:
 *                     type: array
 *                     description: Contract list
 *                     items:
 *                       type: object
 *                       required:
 *                         - code
 *                         - signdate
 *                       properties:
 *                         code:
 *                           type: string
 *                           example: DT250027C
 *                         signdate:
 *                           type: string
 *                           format: date
 *                           example: "2025-03-20"
 *               - type: array
 *                 description: If request body is an array, pass database via query param `?database=xxx`
 *                 items:
 *                   type: object
 *                   required:
 *                     - code
 *                     - signdate
 *                   properties:
 *                     code:
 *                       type: string
 *                       example: DT250027C
 *                     signdate:
 *                       type: string
 *                       format: date
 *                       example: "2025-03-20"
 *     parameters:
 *       - in: query
 *         name: database
 *         required: false
 *         schema:
 *           type: string
 *         description: Database name when request body is array
 *     responses:
 *       200:
 *         description: Batch update completed
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
 *                     total_processed:
 *                       type: integer
 *                       example: 1
 *                     successful:
 *                       type: integer
 *                       example: 1
 *                     failed:
 *                       type: integer
 *                       example: 0
 *                     details:
 *                       type: array
 *                       items:
 *                         type: object
 *                     errors:
 *                       type: array
 *                       items:
 *                         type: object
 *       400:
 *         description: Invalid request parameters
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router.post('/batch-set-sign-back-date', asyncHandler(async (req, res) => {
  let database;
  let contracts;

  if (Array.isArray(req.body)) {
    database = req.query?.database;
    contracts = req.body;
  } else {
    database = req.body?.database;
    contracts = req.body?.contracts;
  }

  if (!database) {
    return res.status(400).json({
      success: false,
      error: {
        message: 'Database name is required',
        field: 'database'
      }
    });
  }

  if (!contracts || !Array.isArray(contracts) || contracts.length === 0) {
    return res.status(400).json({
      success: false,
      error: {
        message: 'Contracts array is required and must not be empty',
        field: 'contracts'
      }
    });
  }

  const pool = dbManager.getPool(database);

  const results = {
    database,
    total_processed: contracts.length,
    successful: 0,
    failed: 0,
    details: [],
    errors: []
  };

  const sql = `
    UPDATE sms_sale_contract
    SET sign_back_date = ?
    WHERE deleted = 0 AND code = ?
  `;

  for (const contract of contracts) {
    try {
      const code = contract?.code;
      const signdate = contract?.signdate;

      if (!code || typeof code !== 'string') {
        throw new Error('Contract code is required');
      }
      if (!signdate || !isValidYYYYMMDD(signdate)) {
        throw new Error('signdate is required and must be in YYYY-MM-DD format');
      }

      const signBackDateTime = `${signdate} 00:00:00`;

      const [updateResult] = await pool.execute(sql, [signBackDateTime, code]);

      const affectedRows = updateResult?.affectedRows || 0;
      if (affectedRows === 0) {
        results.failed += 1;
        results.errors.push({
          code,
          signdate,
          message: 'No contract updated (code not found or deleted=1)'
        });
        results.details.push({
          code,
          signdate,
          affectedRows,
          status: 'not_found'
        });
        continue;
      }

      results.successful += 1;
      results.details.push({
        code,
        signdate,
        affectedRows,
        status: 'updated'
      });
    } catch (error) {
      results.failed += 1;
      results.errors.push({
        code: contract?.code,
        signdate: contract?.signdate,
        message: error.message
      });
      results.details.push({
        code: contract?.code,
        signdate: contract?.signdate,
        status: 'failed'
      });
    }
  }

  res.json({
    success: true,
    data: results
  });
}));

module.exports = router;

