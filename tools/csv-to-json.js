/**
 * 将 CSV 转换为 JSON（支持 quoted newline / quoted comma）。
 *
 * 用法：
 *   node csv-to-json.js                          # 默认：data.csv -> data.json
 *   node csv-to-json.js in.csv out.json
 *   node csv-to-json.js --input in.csv --output out.json
 *   node csv-to-json.js --auto-type             # 尝试把数字/布尔/null 转为对应类型（更“像 JSON”）
 *
 * 说明：
 * - 默认保持字段为字符串（空串会变为 null），避免把像 vender_code 这类“数字字符串”误转为 number。
 */
/* eslint-disable no-console */

const fs = require('fs');
const path = require('path');
const { parse } = require('csv-parse');

function parseArgs(argv) {
  const args = {
    input: null,
    output: null,
    autoType: false,
    pretty: 2,
  };

  const positional = [];
  for (let i = 0; i < argv.length; i += 1) {
    const a = argv[i];
    if (a === '--input' || a === '-i') {
      args.input = argv[i + 1];
      i += 1;
      continue;
    }
    if (a === '--output' || a === '-o') {
      args.output = argv[i + 1];
      i += 1;
      continue;
    }
    if (a === '--auto-type') {
      args.autoType = true;
      continue;
    }
    if (a.startsWith('--pretty=')) {
      const v = Number(a.slice('--pretty='.length));
      if (!Number.isNaN(v)) args.pretty = v;
      continue;
    }
    if (a === '--pretty') {
      const v = Number(argv[i + 1]);
      if (!Number.isNaN(v)) args.pretty = v;
      i += 1;
      continue;
    }
    if (a === '--help' || a === '-h') {
      args.help = true;
      continue;
    }
    positional.push(a);
  }

  if (!args.input && positional[0]) args.input = positional[0];
  if (!args.output && positional[1]) args.output = positional[1];

  return args;
}

function printHelp() {
  console.log(
    [
      'csv-to-json',
      '',
      '用法：',
      '  node csv-to-json.js                          # 默认：data.csv -> data.json',
      '  node csv-to-json.js in.csv out.json',
      '  node csv-to-json.js --input in.csv --output out.json',
      '  node csv-to-json.js --auto-type',
      '',
      '参数：',
      '  -i, --input       输入 CSV 路径',
      '  -o, --output      输出 JSON 路径',
      '  --auto-type       自动类型转换（number / boolean / null）',
      '  --pretty <n>      JSON 缩进空格数（默认 2）',
    ].join('\n'),
  );
}

function toNullIfEmpty(v) {
  if (v == null) return null;
  if (typeof v !== 'string') return v;
  return v === '' ? null : v;
}

function autoCast(v) {
  if (v == null) return null;
  if (typeof v !== 'string') return v;
  if (v === '') return null;

  const t = v.trim();
  if (t === '') return null;

  if (/^(true|false)$/i.test(t)) return t.toLowerCase() === 'true';
  if (/^null$/i.test(t)) return null;

  // 避免把有前导 0 的“编号字符串”误转 number，例如 00820009
  if (/^0\d+$/.test(t)) return v;

  // 整数：超出安全整数范围则保留字符串
  if (/^-?\d+$/.test(t)) {
    const n = Number(t);
    if (!Number.isSafeInteger(n)) return v;
    return n;
  }

  // 小数
  if (/^-?\d+\.\d+$/.test(t)) {
    const n = Number(t);
    return Number.isFinite(n) ? n : v;
  }

  return v;
}

async function main() {
  const args = parseArgs(process.argv.slice(2));
  if (args.help) {
    printHelp();
    process.exit(0);
  }

  const defaultInput = path.resolve(__dirname, 'data.csv');
  const defaultOutput = path.resolve(__dirname, 'data.json');

  const inputPath = path.resolve(process.cwd(), args.input || defaultInput);
  const outputPath = path.resolve(process.cwd(), args.output || defaultOutput);

  if (!fs.existsSync(inputPath)) {
    console.error(`输入文件不存在：${inputPath}`);
    process.exit(1);
  }

  const records = [];

  await new Promise((resolve, reject) => {
    const parser = parse({
      columns: true,
      bom: true,
      skip_empty_lines: true,
      relax_quotes: true,
      relax_column_count: true,
      // 注意：不要 trim，避免影响字段值（例如地址/备注里的空格）
    });

    fs.createReadStream(inputPath)
      .on('error', reject)
      .pipe(parser)
      .on('data', (row) => {
        if (!row || typeof row !== 'object') return;
        const out = {};
        for (const [k, v] of Object.entries(row)) {
          const base = toNullIfEmpty(v);
          out[k] = args.autoType ? autoCast(base) : base;
        }
        records.push(out);
      })
      .on('error', reject)
      .on('end', resolve);
  });

  fs.writeFileSync(outputPath, JSON.stringify(records, null, args.pretty), 'utf8');
  console.log(`已生成：${outputPath}`);
  console.log(`记录数：${records.length}`);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});

