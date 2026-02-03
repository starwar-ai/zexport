"""
EPlus Data Analytics Dashboard
主入口 - KPI 概览页面
"""
import sys
import os

# 添加项目根目录到 Python 路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import streamlit as st
from datetime import datetime, date, timedelta
import pandas as pd

# 页面配置
st.set_page_config(
    page_title="EPlus 数据分析",
    page_icon="📊",
    layout="wide",
    initial_sidebar_state="expanded"
)

# 侧边栏标题
st.sidebar.title("🏠 首页")

# 自定义样式 - 隐藏默认的 "app" 文字并美化界面
st.markdown("""
<style>
    /* 隐藏默认的页面标题 "app" */
    [data-testid="stSidebarNav"] > ul > li:first-child > a > span {
        display: none;
    }
    [data-testid="stSidebarNav"] > ul > li:first-child > a::after {
        content: "首页";
    }
    
    .metric-card {
        background-color: #f8f9fa;
        border-radius: 10px;
        padding: 20px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .metric-label {
        font-size: 14px;
        color: #6c757d;
        margin-bottom: 5px;
    }
    .metric-value {
        font-size: 28px;
        font-weight: bold;
        color: #212529;
    }
    .metric-delta-positive {
        color: #28a745;
        font-size: 12px;
    }
    .metric-delta-negative {
        color: #dc3545;
        font-size: 12px;
    }
</style>
""", unsafe_allow_html=True)


def get_date_ranges():
    """获取今日、本月、本年的日期范围"""
    today = date.today()
    
    # 今日
    today_start = today
    today_end = today
    
    # 本月
    month_start = today.replace(day=1)
    month_end = today
    
    # 本年
    year_start = today.replace(month=1, day=1)
    year_end = today
    
    return {
        'today': (today_start, today_end),
        'month': (month_start, month_end),
        'year': (year_start, year_end)
    }


def format_amount(amount: float) -> str:
    """格式化金额显示"""
    if amount >= 100000000:
        return f"¥{amount/100000000:.2f}亿"
    elif amount >= 10000:
        return f"¥{amount/10000:.2f}万"
    else:
        return f"¥{amount:,.2f}"


def main():
    # 标题
    st.title("📊 EPlus 数据分析平台")
    st.markdown("---")
    
    # 获取日期范围
    date_ranges = get_date_ranges()
    
    # 尝试连接数据库并获取数据
    try:
        from src.services import SalesStatsService, PurchaseStatsService, FinanceStatsService
        
        # 获取各时间段的统计数据
        kpi_data = {
            'today': {},
            'month': {},
            'year': {}
        }
        
        for period, (start, end) in date_ranges.items():
            # 销售数据
            sales = SalesStatsService.get_sales_summary(start, end)
            kpi_data[period]['sales_count'] = sales['total_count']
            kpi_data[period]['sales_amount'] = sales['total_amount']
            
            # 采购数据
            purchase = PurchaseStatsService.get_purchase_summary(start, end)
            kpi_data[period]['purchase_count'] = purchase['total_count']
            kpi_data[period]['purchase_amount'] = purchase['total_amount']
            
            # 收款数据
            receipt = FinanceStatsService.get_receipt_summary(start, end)
            kpi_data[period]['receipt_count'] = receipt['total_count']
            kpi_data[period]['receipt_amount'] = receipt['total_amount']
            
            # 付款数据
            payment = FinanceStatsService.get_payment_summary(start, end)
            kpi_data[period]['payment_count'] = payment['total_count']
            kpi_data[period]['payment_amount'] = payment['total_amount']
        
        db_connected = True
        
    except Exception as e:
        st.warning(f"⚠️ 数据库连接失败，显示示例数据。错误: {str(e)}")
        db_connected = False
        
        # 示例数据
        kpi_data = {
            'today': {
                'sales_count': 5, 'sales_amount': 125000,
                'purchase_count': 3, 'purchase_amount': 85000,
                'receipt_count': 8, 'receipt_amount': 200000,
                'payment_count': 4, 'payment_amount': 95000
            },
            'month': {
                'sales_count': 45, 'sales_amount': 2850000,
                'purchase_count': 38, 'purchase_amount': 1950000,
                'receipt_count': 62, 'receipt_amount': 3200000,
                'payment_count': 41, 'payment_amount': 1800000
            },
            'year': {
                'sales_count': 520, 'sales_amount': 35800000,
                'purchase_count': 410, 'purchase_amount': 24500000,
                'receipt_count': 680, 'receipt_amount': 32000000,
                'payment_count': 450, 'payment_amount': 22000000
            }
        }
    
    # KPI 概览
    st.subheader("📈 核心指标概览")
    
    # 时间维度选择
    period_tabs = st.tabs(["📅 今日", "📆 本月", "📊 本年"])
    
    period_keys = ['today', 'month', 'year']
    
    for idx, tab in enumerate(period_tabs):
        with tab:
            period_key = period_keys[idx]
            data = kpi_data[period_key]
            
            # 第一行：销售和采购
            col1, col2, col3, col4 = st.columns(4)
            
            with col1:
                st.metric(
                    label="💰 销售金额",
                    value=format_amount(data['sales_amount']),
                    delta=f"{data['sales_count']} 笔"
                )
            
            with col2:
                st.metric(
                    label="🛒 采购金额",
                    value=format_amount(data['purchase_amount']),
                    delta=f"{data['purchase_count']} 笔"
                )
            
            with col3:
                st.metric(
                    label="📥 收款金额",
                    value=format_amount(data['receipt_amount']),
                    delta=f"{data['receipt_count']} 笔"
                )
            
            with col4:
                st.metric(
                    label="📤 付款金额",
                    value=format_amount(data['payment_amount']),
                    delta=f"{data['payment_count']} 笔"
                )
            
            # 净现金流
            net_cashflow = data['receipt_amount'] - data['payment_amount']
            st.markdown("---")
            
            col1, col2, col3 = st.columns([1, 2, 1])
            with col2:
                delta_color = "normal" if net_cashflow >= 0 else "inverse"
                st.metric(
                    label="💵 净现金流",
                    value=format_amount(abs(net_cashflow)),
                    delta="流入" if net_cashflow >= 0 else "流出",
                    delta_color=delta_color
                )
    
    st.markdown("---")
    
    # 快速导航
    st.subheader("🚀 快速导航")
    
    col1, col2, col3, col4 = st.columns(4)
    
    with col1:
        st.markdown("""
        ### 📊 销售分析
        - 销售趋势
        - 客户排名
        - 报价转化率
        """)
        st.page_link("pages/1_📊_销售分析.py", label="进入销售分析 →")
    
    with col2:
        st.markdown("""
        ### 🛒 采购分析
        - 采购趋势
        - 供应商排名
        - 采购明细
        """)
        st.page_link("pages/2_🛒_采购分析.py", label="进入采购分析 →")
    
    with col3:
        st.markdown("""
        ### 📦 库存分析
        - 库存概况
        - 仓库分布
        - 出入库趋势
        """)
        st.page_link("pages/3_📦_库存分析.py", label="进入库存分析 →")
    
    with col4:
        st.markdown("""
        ### 💰 财务分析
        - 收付款趋势
        - 客户收款排名
        - 供应商付款排名
        """)
        st.page_link("pages/4_💰_财务分析.py", label="进入财务分析 →")
    
    # 页脚
    st.markdown("---")
    st.markdown(
        f"<div style='text-align: center; color: #6c757d;'>"
        f"EPlus Data Analytics | 数据更新时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')} | "
        f"数据库状态: {'🟢 已连接' if db_connected else '🔴 未连接'}"
        f"</div>",
        unsafe_allow_html=True
    )


if __name__ == "__main__":
    main()
