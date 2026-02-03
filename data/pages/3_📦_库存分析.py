"""
库存分析页面
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import streamlit as st
from datetime import datetime, date, timedelta
import pandas as pd

from src.export import ExcelExporter

# 页面配置
st.set_page_config(
    page_title="库存分析 - EPlus",
    page_icon="📦",
    layout="wide"
)

st.title("📦 库存分析")
st.markdown("---")


def get_default_date_range():
    """获取默认日期范围（本年）"""
    today = date.today()
    year_start = today.replace(month=1, day=1)
    return year_start, today


# 侧边栏筛选条件
st.sidebar.header("🔍 筛选条件")

# 出入库日期范围
default_start, default_end = get_default_date_range()
col1, col2 = st.sidebar.columns(2)
with col1:
    start_date = st.date_input("开始日期", value=default_start)
with col2:
    end_date = st.date_input("结束日期", value=default_end)

# 仓库筛选
warehouse_filter = st.sidebar.text_input("仓库名称", placeholder="输入仓库名称筛选...")

# 产品筛选
product_filter = st.sidebar.text_input("产品名称", placeholder="输入产品名称筛选...")

# 时间粒度
time_granularity = st.sidebar.selectbox(
    "时间粒度",
    options=["month", "day", "year"],
    format_func=lambda x: {"day": "按日", "month": "按月", "year": "按年"}[x]
)

# 单据类型
bill_type_options = {"全部": None, "入库": 1, "出库": 2}
bill_type_label = st.sidebar.selectbox("单据类型", options=list(bill_type_options.keys()))
bill_type_filter = bill_type_options[bill_type_label]

# 尝试获取数据
try:
    from src.services import InventoryStatsService
    
    # 获取库存汇总
    stock_summary = InventoryStatsService.get_stock_summary()
    
    # 获取仓库库存分布
    warehouse_stock = InventoryStatsService.get_stock_by_warehouse()
    
    # 获取出入库汇总
    inout_summary = InventoryStatsService.get_inout_summary(start_date, end_date)
    
    # 获取出入库趋势
    inout_trend = InventoryStatsService.get_inout_trend(start_date, end_date, group_by=time_granularity)
    
    # 获取库存明细
    stock_detail = InventoryStatsService.get_stock_detail(
        warehouse_filter if warehouse_filter else None,
        product_filter if product_filter else None
    )
    
    # 获取出入库单明细
    bill_detail = InventoryStatsService.get_bill_detail(start_date, end_date, bill_type_filter)
    
    db_connected = True
    
except Exception as e:
    st.warning(f"⚠️ 数据库连接失败，显示示例数据。错误: {str(e)}")
    db_connected = False
    
    # 示例数据
    stock_summary = {
        'sku_count': 1250,
        'total_quantity': 85000,
        'locked_quantity': 12000,
        'available_quantity': 73000
    }
    
    inout_summary = {
        'in_count': 320,
        'in_quantity': 45000,
        'out_count': 285,
        'out_quantity': 38000
    }
    
    warehouse_stock = pd.DataFrame([
        {'仓库名称': '主仓库', 'SKU数量': 650, '库存总量': 45000, '可用数量': 38000},
        {'仓库名称': '东仓库', 'SKU数量': 350, '库存总量': 25000, '可用数量': 22000},
        {'仓库名称': '西仓库', 'SKU数量': 250, '库存总量': 15000, '可用数量': 13000},
    ])
    
    inout_trend = pd.DataFrame([
        {'日期': '2026-01', '入库数量': 3800, '出库数量': 3200},
        {'日期': '2025-12', '入库数量': 4500, '出库数量': 4100},
        {'日期': '2025-11', '入库数量': 4200, '出库数量': 3800},
        {'日期': '2025-10', '入库数量': 3900, '出库数量': 3500},
        {'日期': '2025-09', '入库数量': 4100, '出库数量': 3700},
    ])
    
    stock_detail = pd.DataFrame([
        {'仓库名称': '主仓库', '产品名称': '产品A', 'SKU编码': 'SKU001', '库存数量': 1500, '锁定数量': 200, '可用数量': 1300, '单位': '件'},
        {'仓库名称': '主仓库', '产品名称': '产品B', 'SKU编码': 'SKU002', '库存数量': 2300, '锁定数量': 300, '可用数量': 2000, '单位': '件'},
        {'仓库名称': '东仓库', '产品名称': '产品C', 'SKU编码': 'SKU003', '库存数量': 800, '锁定数量': 100, '可用数量': 700, '单位': '箱'},
    ])
    
    bill_detail = pd.DataFrame([
        {'单据编号': 'WB2026010001', '单据类型': '入库', '仓库名称': '主仓库', '数量': 500, '状态': 1, '创建人': '张三', '创建时间': datetime.now()},
        {'单据编号': 'WB2026010002', '单据类型': '出库', '仓库名称': '主仓库', '数量': 320, '状态': 1, '创建人': '李四', '创建时间': datetime.now()},
        {'单据编号': 'WB2026010003', '单据类型': '入库', '仓库名称': '东仓库', '数量': 450, '状态': 2, '创建人': '王五', '创建时间': datetime.now()},
    ])


# 库存概况
st.subheader("📊 库存概况")
col1, col2, col3, col4 = st.columns(4)

with col1:
    st.metric("SKU 数量", f"{stock_summary['sku_count']:,}")

with col2:
    st.metric("库存总量", f"{stock_summary['total_quantity']:,.0f}")

with col3:
    st.metric("锁定数量", f"{stock_summary['locked_quantity']:,.0f}")

with col4:
    st.metric("可用数量", f"{stock_summary['available_quantity']:,.0f}")

st.markdown("---")

# 出入库汇总
st.subheader("📥📤 出入库汇总")
col1, col2, col3, col4 = st.columns(4)

with col1:
    st.metric("入库单数", f"{inout_summary['in_count']} 笔")

with col2:
    st.metric("入库数量", f"{inout_summary['in_quantity']:,.0f}")

with col3:
    st.metric("出库单数", f"{inout_summary['out_count']} 笔")

with col4:
    st.metric("出库数量", f"{inout_summary['out_quantity']:,.0f}")

st.markdown("---")

# 图表区域
col1, col2 = st.columns(2)

with col1:
    st.subheader("📈 出入库趋势")
    if not inout_trend.empty:
        st.line_chart(inout_trend.set_index('日期')[['入库数量', '出库数量']])
    else:
        st.info("暂无趋势数据")

with col2:
    st.subheader("🏭 仓库库存分布")
    if not warehouse_stock.empty:
        st.dataframe(
            warehouse_stock,
            width='stretch',
            hide_index=True,
            column_config={
                '库存总量': st.column_config.NumberColumn(format="%d"),
                '可用数量': st.column_config.NumberColumn(format="%d")
            }
        )
    else:
        st.info("暂无仓库数据")

st.markdown("---")

# 明细数据 Tabs
tab1, tab2 = st.tabs(["📦 库存明细", "📋 出入库单明细"])

with tab1:
    st.subheader("📦 库存明细")
    if not stock_detail.empty:
        st.dataframe(
            stock_detail,
            width='stretch',
            hide_index=True
        )
        
        # 导出按钮
        col1, col2 = st.columns([1, 5])
        with col1:
            excel_data = ExcelExporter.export_dataframe(
                stock_detail, 
                sheet_name="库存明细",
                title="库存明细"
            )
            filename = ExcelExporter.get_filename("库存明细")
            st.download_button(
                label="📥 导出 Excel",
                data=excel_data,
                file_name=filename,
                mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
    else:
        st.info("暂无库存明细数据")

with tab2:
    st.subheader("📋 出入库单明细")
    if not bill_detail.empty:
        st.dataframe(
            bill_detail,
            width='stretch',
            hide_index=True,
            column_config={
                '创建时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm")
            }
        )
        
        # 导出按钮
        col1, col2 = st.columns([1, 5])
        with col1:
            excel_data = ExcelExporter.export_dataframe(
                bill_detail, 
                sheet_name="出入库明细",
                title=f"出入库明细 ({start_date} ~ {end_date})"
            )
            filename = ExcelExporter.get_filename("出入库明细")
            st.download_button(
                label="📥 导出 Excel",
                data=excel_data,
                file_name=filename,
                mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
    else:
        st.info("暂无出入库明细数据")

# 汇总导出
st.markdown("---")
col1, col2 = st.columns([1, 5])
with col1:
    sheets = {
        "仓库分布": warehouse_stock,
        "出入库趋势": inout_trend,
        "库存明细": stock_detail,
        "出入库明细": bill_detail
    }
    excel_summary = ExcelExporter.export_multiple_sheets(
        sheets,
        title="库存分析报表"
    )
    summary_filename = ExcelExporter.get_filename("库存分析报表")
    st.download_button(
        label="📥 导出汇总报表",
        data=excel_summary,
        file_name=summary_filename,
        mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

# 页脚
st.markdown("---")
st.markdown(
    f"<div style='text-align: center; color: #6c757d;'>"
    f"出入库数据范围: {start_date} ~ {end_date} | "
    f"数据库状态: {'🟢 已连接' if db_connected else '🔴 未连接'}"
    f"</div>",
    unsafe_allow_html=True
)
