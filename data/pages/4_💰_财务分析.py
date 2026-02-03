"""
财务分析页面
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import streamlit as st
from datetime import datetime, date, timedelta
import pandas as pd

from src.export import ExcelExporter

# 状态值映射
RECEIPT_STATUS_MAP = {
    0: '未收款',
    1: '已收款'
}

PAYMENT_STATUS_MAP = {
    0: '未支付',
    1: '已支付',
    2: '支付中',
    3: '部分支付'
}

AUDIT_STATUS_MAP = {
    0: '未提交',
    1: '处理中',
    2: '已通过',
    3: '不通过',
    4: '已取消',
    5: '驳回',
    10: '已作废'
}

# 收款方式映射 (loan_type 字典)
RECEIPT_TYPE_MAP = {
    1: '现金',
    2: '转账',
    3: '承兑汇票'
}

def convert_receipt_status(df: 'pd.DataFrame') -> 'pd.DataFrame':
    """转换收款明细的状态列为中文"""
    if df.empty:
        return df
    df = df.copy()
    if '状态' in df.columns:
        df['状态'] = df['状态'].map(lambda x: RECEIPT_STATUS_MAP.get(x, f'未知({x})'))
    if '审核状态' in df.columns:
        df['审核状态'] = df['审核状态'].map(lambda x: AUDIT_STATUS_MAP.get(x, f'未知({x})'))
    if '收款方式' in df.columns:
        df['收款方式'] = df['收款方式'].map(lambda x: RECEIPT_TYPE_MAP.get(x, f'未知({x})') if x else '')
    return df

def convert_payment_status(df: 'pd.DataFrame') -> 'pd.DataFrame':
    """转换付款明细的状态列为中文"""
    if df.empty:
        return df
    df = df.copy()
    if '状态' in df.columns:
        df['状态'] = df['状态'].map(lambda x: PAYMENT_STATUS_MAP.get(x, f'未知({x})'))
    return df

# 页面配置
st.set_page_config(
    page_title="财务分析 - EPlus",
    page_icon="💰",
    layout="wide"
)

st.title("💰 财务分析")
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

# 客户/供应商筛选
cust_name_filter = st.sidebar.text_input("客户名称", placeholder="筛选收款客户...")
vender_name_filter = st.sidebar.text_input("供应商名称", placeholder="筛选付款供应商...")

# 时间粒度
time_granularity = st.sidebar.selectbox(
    "时间粒度",
    options=["month", "day", "year"],
    format_func=lambda x: {"day": "按日", "month": "按月", "year": "按年"}[x]
)

# 尝试获取数据
try:
    from src.services import FinanceStatsService
    
    # 获取收款汇总
    receipt_summary = FinanceStatsService.get_receipt_summary(start_date, end_date)
    
    # 获取付款汇总
    payment_summary = FinanceStatsService.get_payment_summary(start_date, end_date)
    
    # 获取客户收款排名
    receipt_by_customer = FinanceStatsService.get_receipt_by_customer(start_date, end_date, limit=10)
    
    # 获取供应商付款排名
    payment_by_vender = FinanceStatsService.get_payment_by_vender(start_date, end_date, limit=10)
    
    # 获取资金流趋势
    cashflow_trend = FinanceStatsService.get_cashflow_trend(start_date, end_date, group_by=time_granularity)
    
    # 获取收款明细
    receipt_detail = FinanceStatsService.get_receipt_detail(start_date, end_date, cust_name_filter if cust_name_filter else None)
    receipt_detail = convert_receipt_status(receipt_detail)  # 转换状态为中文
    
    # 获取付款明细
    payment_detail = FinanceStatsService.get_payment_detail(start_date, end_date)
    payment_detail = convert_payment_status(payment_detail)  # 转换状态为中文
    
    db_connected = True
    
except Exception as e:
    st.warning(f"⚠️ 数据库连接失败，显示示例数据。错误: {str(e)}")
    db_connected = False
    
    # 示例数据
    receipt_summary = {'total_count': 680, 'total_amount': 32000000}
    payment_summary = {'total_count': 450, 'total_amount': 22000000}
    
    receipt_by_customer = pd.DataFrame([
        {'客户名称': 'ABC Company', '收款笔数': 45, '收款金额': 5200000},
        {'客户名称': 'XYZ Corp', '收款笔数': 38, '收款金额': 4100000},
        {'客户名称': 'DEF Ltd', '收款笔数': 32, '收款金额': 3200000},
        {'客户名称': 'GHI Inc', '收款笔数': 28, '收款金额': 2600000},
        {'客户名称': 'JKL Trading', '收款笔数': 25, '收款金额': 2100000},
    ])
    
    payment_by_vender = pd.DataFrame([
        {'供应商名称': '宏达供应商', '付款笔数': 38, '付款金额': 4200000},
        {'供应商名称': '信达科技', '付款笔数': 32, '付款金额': 3100000},
        {'供应商名称': '优质材料', '付款笔数': 28, '付款金额': 2500000},
        {'供应商名称': '精工制造', '付款笔数': 25, '付款金额': 2100000},
        {'供应商名称': '恒泰贸易', '付款笔数': 22, '付款金额': 1800000},
    ])
    
    cashflow_trend = pd.DataFrame([
        {'日期': '2026-01', '收款金额': 2800000, '付款金额': 1900000, '净现金流': 900000},
        {'日期': '2025-12', '收款金额': 3500000, '付款金额': 2400000, '净现金流': 1100000},
        {'日期': '2025-11', '收款金额': 3200000, '付款金额': 2100000, '净现金流': 1100000},
        {'日期': '2025-10', '收款金额': 2900000, '付款金额': 2000000, '净现金流': 900000},
        {'日期': '2025-09', '收款金额': 3100000, '付款金额': 2200000, '净现金流': 900000},
    ])
    
    receipt_detail = pd.DataFrame([
        {'收款单号': 'RC2026010001', '客户名称': 'ABC Company', '收款金额': 125000, '币种': 'USD', '收款日期': datetime.now(), '状态': '已收款', '创建人': '张三', '创建时间': datetime.now()},
        {'收款单号': 'RC2026010002', '客户名称': 'XYZ Corp', '收款金额': 98000, '币种': 'USD', '收款日期': datetime.now(), '状态': '已收款', '创建人': '李四', '创建时间': datetime.now()},
    ])
    
    payment_detail = pd.DataFrame([
        {'付款单号': 'PM2026010001', '供应商名称': '宏达供应商', '付款金额': 85000, '币种': 'CNY', '付款日期': datetime.now(), '状态': '已支付', '创建人': '张三', '创建时间': datetime.now()},
        {'付款单号': 'PM2026010002', '供应商名称': '信达科技', '付款金额': 125000, '币种': 'CNY', '付款日期': datetime.now(), '状态': '已支付', '创建人': '李四', '创建时间': datetime.now()},
    ])


# 资金汇总
st.subheader("💵 资金汇总")
col1, col2, col3, col4 = st.columns(4)

with col1:
    amount = receipt_summary['total_amount']
    if amount >= 10000:
        st.metric("收款总额", f"¥{amount/10000:.2f} 万", delta=f"{receipt_summary['total_count']} 笔")
    else:
        st.metric("收款总额", f"¥{amount:,.2f}", delta=f"{receipt_summary['total_count']} 笔")

with col2:
    amount = payment_summary['total_amount']
    if amount >= 10000:
        st.metric("付款总额", f"¥{amount/10000:.2f} 万", delta=f"{payment_summary['total_count']} 笔")
    else:
        st.metric("付款总额", f"¥{amount:,.2f}", delta=f"{payment_summary['total_count']} 笔")

with col3:
    net_cashflow = receipt_summary['total_amount'] - payment_summary['total_amount']
    if abs(net_cashflow) >= 10000:
        display_value = f"¥{abs(net_cashflow)/10000:.2f} 万"
    else:
        display_value = f"¥{abs(net_cashflow):,.2f}"
    st.metric(
        "净现金流", 
        display_value,
        delta="流入" if net_cashflow >= 0 else "流出",
        delta_color="normal" if net_cashflow >= 0 else "inverse"
    )

with col4:
    ratio = (receipt_summary['total_amount'] / payment_summary['total_amount'] * 100) if payment_summary['total_amount'] > 0 else 0
    st.metric("收付比", f"{ratio:.1f}%")

st.markdown("---")

# 资金流趋势
st.subheader("📈 资金流趋势")
if not cashflow_trend.empty:
    col1, col2 = st.columns([2, 1])
    with col1:
        # 收付款对比图
        chart_data = cashflow_trend.set_index('日期')[['收款金额', '付款金额']]
        st.bar_chart(chart_data)
    with col2:
        # 净现金流趋势
        st.line_chart(cashflow_trend.set_index('日期')['净现金流'])
else:
    st.info("暂无趋势数据")

st.markdown("---")

# 排名区域
col1, col2 = st.columns(2)

with col1:
    st.subheader("🏆 客户收款排名 (Top 10)")
    if not receipt_by_customer.empty:
        st.dataframe(
            receipt_by_customer,
            width='stretch',
            hide_index=True,
            column_config={
                '收款金额': st.column_config.NumberColumn(format="¥%.2f")
            }
        )
    else:
        st.info("暂无客户收款数据")

with col2:
    st.subheader("🏭 供应商付款排名 (Top 10)")
    if not payment_by_vender.empty:
        st.dataframe(
            payment_by_vender,
            width='stretch',
            hide_index=True,
            column_config={
                '付款金额': st.column_config.NumberColumn(format="¥%.2f")
            }
        )
    else:
        st.info("暂无供应商付款数据")

st.markdown("---")

# 明细数据 Tabs
tab1, tab2 = st.tabs(["📥 收款明细", "📤 付款明细"])

with tab1:
    st.subheader("📥 收款明细")
    if not receipt_detail.empty:
        st.dataframe(
            receipt_detail,
            width='stretch',
            hide_index=True,
            column_config={
                '收款金额': st.column_config.NumberColumn(format="%.2f"),
                '收款日期': st.column_config.DatetimeColumn(format="YYYY-MM-DD"),
                '创建时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm")
            }
        )
        
        # 导出按钮
        col1, col2 = st.columns([1, 5])
        with col1:
            excel_data = ExcelExporter.export_dataframe(
                receipt_detail, 
                sheet_name="收款明细",
                title=f"收款明细 ({start_date} ~ {end_date})"
            )
            filename = ExcelExporter.get_filename("收款明细")
            st.download_button(
                label="📥 导出 Excel",
                data=excel_data,
                file_name=filename,
                mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
    else:
        st.info("暂无收款明细数据")

with tab2:
    st.subheader("📤 付款明细")
    if not payment_detail.empty:
        st.dataframe(
            payment_detail,
            width='stretch',
            hide_index=True,
            column_config={
                '付款金额': st.column_config.NumberColumn(format="%.2f"),
                '付款日期': st.column_config.DatetimeColumn(format="YYYY-MM-DD"),
                '创建时间': st.column_config.DatetimeColumn(format="YYYY-MM-DD HH:mm")
            }
        )
        
        # 导出按钮
        col1, col2 = st.columns([1, 5])
        with col1:
            excel_data = ExcelExporter.export_dataframe(
                payment_detail, 
                sheet_name="付款明细",
                title=f"付款明细 ({start_date} ~ {end_date})"
            )
            filename = ExcelExporter.get_filename("付款明细")
            st.download_button(
                label="📥 导出 Excel",
                data=excel_data,
                file_name=filename,
                mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
    else:
        st.info("暂无付款明细数据")

# 汇总导出
st.markdown("---")
col1, col2 = st.columns([1, 5])
with col1:
    sheets = {
        "客户收款排名": receipt_by_customer,
        "供应商付款排名": payment_by_vender,
        "资金流趋势": cashflow_trend,
        "收款明细": receipt_detail,
        "付款明细": payment_detail
    }
    excel_summary = ExcelExporter.export_multiple_sheets(
        sheets,
        title=f"财务分析报表 ({start_date} ~ {end_date})"
    )
    summary_filename = ExcelExporter.get_filename("财务分析报表")
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
    f"数据范围: {start_date} ~ {end_date} | "
    f"数据库状态: {'🟢 已连接' if db_connected else '🔴 未连接'}"
    f"</div>",
    unsafe_allow_html=True
)
