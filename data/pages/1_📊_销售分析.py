"""
销售分析页面
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
    page_title="销售分析 - EPlus",
    page_icon="📊",
    layout="wide"
)

st.title("📊 销售分析")
st.markdown("---")


def get_default_date_range():
    """获取默认日期范围（本年）"""
    today = date.today()
    year_start = today.replace(month=1, day=1)
    return year_start, today


# 侧边栏筛选条件
st.sidebar.header("🔍 筛选条件")

# 日期范围
default_start, default_end = get_default_date_range()
col1, col2 = st.sidebar.columns(2)
with col1:
    start_date = st.date_input("开始日期", value=default_start)
with col2:
    end_date = st.date_input("结束日期", value=default_end)

# 客户名称筛选
cust_name_filter = st.sidebar.text_input("客户名称", placeholder="输入客户名称筛选...")

# 时间粒度
time_granularity = st.sidebar.selectbox(
    "时间粒度",
    options=["month", "day", "year"],
    format_func=lambda x: {"day": "按日", "month": "按月", "year": "按年"}[x]
)

# 尝试获取数据
try:
    from src.services import SalesStatsService
    
    # 获取汇总数据
    summary = SalesStatsService.get_sales_summary(start_date, end_date)
    
    # 获取客户排名
    customer_ranking = SalesStatsService.get_sales_by_customer(start_date, end_date, limit=10)
    
    # 获取趋势数据
    trend_data = SalesStatsService.get_sales_trend(start_date, end_date, group_by=time_granularity)
    
    # 获取报价转化率
    conversion = SalesStatsService.get_quotation_conversion_rate(start_date, end_date)
    
    # 获取明细数据
    detail_data = SalesStatsService.get_sales_detail(start_date, end_date, cust_name_filter if cust_name_filter else None)
    
    # 获取客户明细汇总
    customer_summary = SalesStatsService.get_customer_summary(start_date, end_date)
    
    db_connected = True
    
except Exception as e:
    st.warning(f"⚠️ 数据库连接失败，显示示例数据。错误: {str(e)}")
    db_connected = False
    
    # 示例数据
    summary = {'total_count': 520, 'total_amount': 35800000}
    
    customer_ranking = pd.DataFrame([
        {'客户名称': 'ABC Company', '合同数量': 45, '销售金额': 5800000},
        {'客户名称': 'XYZ Corp', '合同数量': 38, '销售金额': 4200000},
        {'客户名称': 'DEF Ltd', '合同数量': 32, '销售金额': 3500000},
        {'客户名称': 'GHI Inc', '合同数量': 28, '销售金额': 2800000},
        {'客户名称': 'JKL Trading', '合同数量': 25, '销售金额': 2200000},
    ])
    
    trend_data = pd.DataFrame([
        {'日期': '2026-01', '合同数量': 42, '销售金额': 2850000},
        {'日期': '2025-12', '合同数量': 58, '销售金额': 4200000},
        {'日期': '2025-11', '合同数量': 52, '销售金额': 3800000},
        {'日期': '2025-10', '合同数量': 48, '销售金额': 3500000},
        {'日期': '2025-09', '合同数量': 45, '销售金额': 3200000},
        {'日期': '2025-08', '合同数量': 50, '销售金额': 3600000},
    ])
    
    conversion = {'quotation_count': 680, 'contract_count': 520, 'conversion_rate': 76.47}
    
    detail_data = pd.DataFrame([
        {'合同编号': 'SC2026010001', '客户名称': 'ABC Company', '合同金额': 125000, '币种': 'USD', '状态': 1, '创建人': '张三', '创建时间': datetime.now()},
        {'合同编号': 'SC2026010002', '客户名称': 'XYZ Corp', '合同金额': 98000, '币种': 'USD', '状态': 1, '创建人': '李四', '创建时间': datetime.now()},
        {'合同编号': 'SC2026010003', '客户名称': 'DEF Ltd', '合同金额': 156000, '币种': 'EUR', '状态': 2, '创建人': '王五', '创建时间': datetime.now()},
    ])
    
    customer_summary = pd.DataFrame([
        {'客户ID': 1, '客户名称': 'ABC Company', '成交合同数': 45, '成交金额(USD)': 5800000, '首次成交时间': datetime.now(), '最近成交时间': datetime.now()},
        {'客户ID': 2, '客户名称': 'XYZ Corp', '成交合同数': 38, '成交金额(USD)': 4200000, '首次成交时间': datetime.now(), '最近成交时间': datetime.now()},
        {'客户ID': 3, '客户名称': 'DEF Ltd', '成交合同数': 32, '成交金额(USD)': 3500000, '首次成交时间': datetime.now(), '最近成交时间': datetime.now()},
    ])


# 汇总指标
st.subheader("📈 销售汇总")
col1, col2, col3, col4 = st.columns(4)

with col1:
    st.metric("合同总数", f"{summary['total_count']} 笔")

with col2:
    amount = summary['total_amount_usd']
    if amount >= 10000:
        st.metric("销售总额(USD)", f"${amount/10000:.2f} 万")
    else:
        st.metric("销售总额(USD)", f"${amount:,.2f}")

with col3:
    st.metric("报价单数", f"{conversion['quotation_count']} 笔")

with col4:
    st.metric("报价转化率", f"{conversion['conversion_rate']:.1f}%")

st.markdown("---")

# 图表区域
col1, col2 = st.columns(2)

with col1:
    st.subheader("📈 销售趋势")
    if not trend_data.empty:
        st.bar_chart(trend_data.set_index('日期')['销售金额(USD)'])
    else:
        st.info("暂无趋势数据")

with col2:
    st.subheader("🏆 客户销售排名 (Top 10)")
    if not customer_ranking.empty:
        st.dataframe(
            customer_ranking,
            width='stretch',
            hide_index=True,
            column_config={
                '销售金额(USD)': st.column_config.NumberColumn(format="$%.2f")
            }
        )
    else:
        st.info("暂无客户数据")

st.markdown("---")

# 客户明细汇总
st.subheader("👥 已成交客户明细")

if not customer_summary.empty:
    # 显示汇总统计
    col1, col2, col3 = st.columns(3)
    with col1:
        st.metric("已成交客户数", f"{len(customer_summary)} 家")
    with col2:
        total_deal_amount = customer_summary['成交金额(USD)'].sum()
        if total_deal_amount >= 10000:
            st.metric("成交总金额(USD)", f"${total_deal_amount/10000:.2f} 万")
        else:
            st.metric("成交总金额(USD)", f"${total_deal_amount:,.2f}")
    with col3:
        total_contracts = customer_summary['成交合同数'].sum()
        st.metric("成交合同总数", f"{total_contracts} 笔")
    
    st.dataframe(
        customer_summary,
        width='stretch',
        hide_index=True,
        column_config={
            '成交金额(USD)': st.column_config.NumberColumn(format="$%.2f"),
            '首次成交时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm"),
            '最近成交时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm")
        }
    )
else:
    st.info("暂无已成交客户数据")

st.markdown("---")

# 明细数据
st.subheader("📋 销售明细")

if not detail_data.empty:
    st.dataframe(
        detail_data,
        width='stretch',
        hide_index=True,
        column_config={
            '合同金额': st.column_config.NumberColumn(format="%.2f"),
            '创建时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm")
        }
    )
    
    # 导出按钮
    st.markdown("---")
    
    col1, col2, col3 = st.columns([1, 1, 4])
    
    with col1:
        # 导出明细（包含成交客户sheet）
        detail_sheets = {
            "销售明细": detail_data,
            "已成交客户": customer_summary
        }
        excel_data = ExcelExporter.export_multiple_sheets(
            detail_sheets,
            title=f"销售明细 ({start_date} ~ {end_date})"
        )
        filename = ExcelExporter.get_filename("销售明细")
        st.download_button(
            label="📥 导出明细 Excel",
            data=excel_data,
            file_name=filename,
            mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    
    with col2:
        # 导出汇总报表
        sheets = {
            "客户排名": customer_ranking,
            "销售趋势": trend_data,
            "已成交客户明细": customer_summary,
            "销售明细": detail_data
        }
        excel_summary = ExcelExporter.export_multiple_sheets(
            sheets,
            title=f"销售分析报表 ({start_date} ~ {end_date})"
        )
        summary_filename = ExcelExporter.get_filename("销售分析报表")
        st.download_button(
            label="📥 导出汇总报表",
            data=excel_summary,
            file_name=summary_filename,
            mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
else:
    st.info("暂无销售明细数据")

# 页脚
st.markdown("---")
st.markdown(
    f"<div style='text-align: center; color: #6c757d;'>"
    f"数据范围: {start_date} ~ {end_date} | "
    f"数据库状态: {'🟢 已连接' if db_connected else '🔴 未连接'}"
    f"</div>",
    unsafe_allow_html=True
)
