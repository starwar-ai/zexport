"""
采购分析页面
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
    page_title="采购分析 - EPlus",
    page_icon="🛒",
    layout="wide"
)

st.title("🛒 采购分析")
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

# 供应商名称筛选
vender_name_filter = st.sidebar.text_input("供应商名称", placeholder="输入供应商名称筛选...")

# 时间粒度
time_granularity = st.sidebar.selectbox(
    "时间粒度",
    options=["month", "day", "year"],
    format_func=lambda x: {"day": "按日", "month": "按月", "year": "按年"}[x]
)

# 尝试获取数据
try:
    from src.services import PurchaseStatsService
    
    # 获取汇总数据
    summary = PurchaseStatsService.get_purchase_summary(start_date, end_date)
    
    # 获取供应商数量
    vender_count = PurchaseStatsService.get_vender_count(start_date, end_date)
    
    # 获取供应商排名
    vender_ranking = PurchaseStatsService.get_purchase_by_vender(start_date, end_date, limit=10)
    
    # 获取趋势数据
    trend_data = PurchaseStatsService.get_purchase_trend(start_date, end_date, group_by=time_granularity)
    
    # 获取明细数据
    detail_data = PurchaseStatsService.get_purchase_detail(start_date, end_date, vender_name_filter if vender_name_filter else None)
    
    # 获取供应商明细汇总
    vender_summary = PurchaseStatsService.get_vender_summary(start_date, end_date)
    
    db_connected = True
    
except Exception as e:
    st.warning(f"⚠️ 数据库连接失败，显示示例数据。错误: {str(e)}")
    db_connected = False
    
    # 示例数据
    summary = {'total_count': 410, 'total_amount': 24500000}
    vender_count = 85
    
    vender_ranking = pd.DataFrame([
        {'供应商名称': '宏达供应商', '合同数量': 42, '采购金额': 4800000},
        {'供应商名称': '信达科技', '合同数量': 35, '采购金额': 3500000},
        {'供应商名称': '优质材料', '合同数量': 30, '采购金额': 2800000},
        {'供应商名称': '精工制造', '合同数量': 28, '采购金额': 2500000},
        {'供应商名称': '恒泰贸易', '合同数量': 25, '采购金额': 2200000},
    ])
    
    trend_data = pd.DataFrame([
        {'日期': '2026-01', '合同数量': 38, '采购金额': 2150000},
        {'日期': '2025-12', '合同数量': 45, '采购金额': 2800000},
        {'日期': '2025-11', '合同数量': 42, '采购金额': 2500000},
        {'日期': '2025-10', '合同数量': 40, '采购金额': 2300000},
        {'日期': '2025-09', '合同数量': 38, '采购金额': 2100000},
        {'日期': '2025-08', '合同数量': 43, '采购金额': 2600000},
    ])
    
    detail_data = pd.DataFrame([
        {'合同编号': 'PC2026010001', '供应商名称': '宏达供应商', '合同金额': 85000, '币种': 'CNY', '状态': 1, '创建人': '张三', '创建时间': datetime.now()},
        {'合同编号': 'PC2026010002', '供应商名称': '信达科技', '合同金额': 125000, '币种': 'CNY', '状态': 1, '创建人': '李四', '创建时间': datetime.now()},
        {'合同编号': 'PC2026010003', '供应商名称': '优质材料', '合同金额': 68000, '币种': 'CNY', '状态': 2, '创建人': '王五', '创建时间': datetime.now()},
    ])
    
    vender_summary = pd.DataFrame([
        {'供应商ID': 1, '供应商名称': '宏达供应商', '成交合同数': 42, '成交金额': 4800000, '首次成交时间': datetime.now(), '最近成交时间': datetime.now()},
        {'供应商ID': 2, '供应商名称': '信达科技', '成交合同数': 35, '成交金额': 3500000, '首次成交时间': datetime.now(), '最近成交时间': datetime.now()},
        {'供应商ID': 3, '供应商名称': '优质材料', '成交合同数': 30, '成交金额': 2800000, '首次成交时间': datetime.now(), '最近成交时间': datetime.now()},
    ])


# 汇总指标
st.subheader("📈 采购汇总")
col1, col2, col3, col4 = st.columns(4)

with col1:
    st.metric("合同总数", f"{summary['total_count']} 笔")

with col2:
    amount = summary['total_amount']
    if amount >= 10000:
        st.metric("采购总额", f"¥{amount/10000:.2f} 万")
    else:
        st.metric("采购总额", f"¥{amount:,.2f}")

with col3:
    st.metric("供应商数量", f"{vender_count} 家")

with col4:
    avg_amount = summary['total_amount'] / summary['total_count'] if summary['total_count'] > 0 else 0
    st.metric("平均合同金额", f"¥{avg_amount:,.0f}")

st.markdown("---")

# 图表区域
col1, col2 = st.columns(2)

with col1:
    st.subheader("📈 采购趋势")
    if not trend_data.empty:
        st.bar_chart(trend_data.set_index('日期')['采购金额'])
    else:
        st.info("暂无趋势数据")

with col2:
    st.subheader("🏭 供应商采购排名 (Top 10)")
    if not vender_ranking.empty:
        st.dataframe(
            vender_ranking,
            width='stretch',
            hide_index=True,
            column_config={
                '采购金额': st.column_config.NumberColumn(format="¥%.2f")
            }
        )
    else:
        st.info("暂无供应商数据")

st.markdown("---")

# 供应商明细汇总
st.subheader("🏢 已成交供应商明细")

if not vender_summary.empty:
    # 显示汇总统计
    col1, col2, col3 = st.columns(3)
    with col1:
        st.metric("已成交供应商数", f"{len(vender_summary)} 家")
    with col2:
        total_deal_amount = vender_summary['成交金额'].sum()
        if total_deal_amount >= 10000:
            st.metric("成交总金额", f"¥{total_deal_amount/10000:.2f} 万")
        else:
            st.metric("成交总金额", f"¥{total_deal_amount:,.2f}")
    with col3:
        total_contracts = vender_summary['成交合同数'].sum()
        st.metric("成交合同总数", f"{total_contracts} 笔")
    
    st.dataframe(
        vender_summary,
        width='stretch',
        hide_index=True,
        column_config={
            '成交金额': st.column_config.NumberColumn(format="¥%.2f"),
            '首次成交时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm"),
            '最近成交时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm")
        }
    )
else:
    st.info("暂无已成交供应商数据")

st.markdown("---")

# 明细数据
st.subheader("📋 采购明细")

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
        # 导出明细（包含成交供应商sheet）
        detail_sheets = {
            "采购明细": detail_data,
            "已成交供应商": vender_summary
        }
        excel_data = ExcelExporter.export_multiple_sheets(
            detail_sheets,
            title=f"采购明细 ({start_date} ~ {end_date})"
        )
        filename = ExcelExporter.get_filename("采购明细")
        st.download_button(
            label="📥 导出明细 Excel",
            data=excel_data,
            file_name=filename,
            mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    
    with col2:
        # 导出汇总报表
        sheets = {
            "供应商排名": vender_ranking,
            "采购趋势": trend_data,
            "已成交供应商明细": vender_summary,
            "采购明细": detail_data
        }
        excel_summary = ExcelExporter.export_multiple_sheets(
            sheets,
            title=f"采购分析报表 ({start_date} ~ {end_date})"
        )
        summary_filename = ExcelExporter.get_filename("采购分析报表")
        st.download_button(
            label="📥 导出汇总报表",
            data=excel_summary,
            file_name=summary_filename,
            mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
else:
    st.info("暂无采购明细数据")

# 页脚
st.markdown("---")
st.markdown(
    f"<div style='text-align: center; color: #6c757d;'>"
    f"数据范围: {start_date} ~ {end_date} | "
    f"数据库状态: {'🟢 已连接' if db_connected else '🔴 未连接'}"
    f"</div>",
    unsafe_allow_html=True
)
