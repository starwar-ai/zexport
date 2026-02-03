# EPlus Data Analytics

ERP 数据统计分析平台，基于 Streamlit 构建的可视化 Dashboard。

## 功能特性

- 📊 **销售分析**：销售趋势、客户排名、报价转化率
- 🛒 **采购分析**：采购趋势、供应商排名、采购明细
- 📦 **库存分析**：库存概况、仓库分布、出入库趋势
- 💰 **财务分析**：收付款趋势、资金流分析、客户/供应商排名
- 📥 **Excel 导出**：支持按筛选条件导出数据

## 目录结构

```
eplus-data/
├── app.py                  # 主入口（首页 KPI 概览）
├── requirements.txt        # Python 依赖
├── run.bat                 # Windows 启动脚本
├── .env.example            # 环境变量模板
├── config/
│   └── settings.py         # 配置管理
├── src/
│   ├── database.py         # 数据库连接
│   ├── models/             # ORM 模型
│   │   ├── sales.py
│   │   ├── purchase.py
│   │   ├── inventory.py
│   │   ├── finance.py
│   │   └── customer.py
│   ├── services/           # 统计业务逻辑
│   │   ├── sales_stats.py
│   │   ├── purchase_stats.py
│   │   ├── inventory_stats.py
│   │   └── finance_stats.py
│   └── export/
│       └── excel_exporter.py
└── pages/                  # Streamlit 页面
    ├── 1_📊_销售分析.py
    ├── 2_🛒_采购分析.py
    ├── 3_📦_库存分析.py
    └── 4_💰_财务分析.py
```

## 快速开始

### 1. 配置环境变量

复制 `.env.example` 为 `.env`，修改数据库连接配置：

```bash
cp .env.example .env
```

编辑 `.env` 文件：

```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=eplus
```

### 2. 安装依赖

```bash
pip install -r requirements.txt
```

### 3. 启动服务

**Windows:**
```bash
run.bat
```

**命令行:**
```bash
streamlit run app.py --server.port 8501
```

### 4. 访问 Dashboard

打开浏览器访问：http://localhost:8501

## 技术栈

| 组件 | 版本 |
|------|------|
| Python | 3.10+ |
| Streamlit | 1.29+ |
| Pandas | 2.0+ |
| SQLAlchemy | 2.0+ |
| PyMySQL | 1.1+ |
| openpyxl | 3.1+ |

## 数据筛选

所有页面支持以下筛选条件：

- **日期范围**：默认本年（1月1日至今）
- **时间粒度**：按日/按月/按年
- **名称筛选**：客户名称、供应商名称、仓库名称等

## 导出功能

每个分析页面提供：

- **导出明细 Excel**：导出当前筛选条件下的明细数据
- **导出汇总报表**：导出包含多个工作表的汇总报表

导出的 Excel 文件包含：
- 格式化的表头样式
- 自动调整列宽
- 数字格式化
- 时间戳文件名

## 注意事项

1. 首次运行时如果数据库未连接，会显示示例数据
2. 确保数据库中存在对应的表结构（与 EPlus ERP 系统一致）
3. 大数据量查询时建议缩小日期范围
