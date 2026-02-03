"""
销售统计服务
"""
from datetime import datetime, date
from typing import Optional
import pandas as pd
from sqlalchemy import func, text, Float
from sqlalchemy.orm import Session

from src.database import get_session
from src.models.sales import SmsSaleContract, SmsQuotation
from src.models.customer import CrmCust
from src.models.user import get_user_nickname_map


def json_extract_value(column, path='$.amount'):
    """
    使用 MySQL JSON_UNQUOTE + JSON_EXTRACT 提取 JSON 字段中的值
    JsonAmount 格式: {"amount": 100.00, "currency": "RMB"}

    注意: JSON_EXTRACT 返回带引号的字符串，需要用 JSON_UNQUOTE 去除引号
    或者直接使用 IFNULL 处理 NULL 值，并用 COALESCE 设置默认值
    """
    # 使用 JSON_UNQUOTE 去除引号，然后用 COALESCE 处理 NULL 值
    return func.coalesce(
        func.json_unquote(func.json_extract(column, path)),
        '0'
    )


# 销售合同状态映射
SALE_CONTRACT_STATUS_MAP = {
    1: '待提交',
    2: '待审核',
    3: '待回签',
    4: '已驳回',
    5: '待采购',
    6: '待出运',
    7: '已完成',
    8: '已作废',
    9: '上游变更',
}

# 审核状态映射
AUDIT_STATUS_MAP = {
    0: '待提交',
    1: '审核中',
    2: '已通过',
    3: '已拒绝',
    4: '已撤回',
}


def get_status_label(status: int) -> str:
    """获取销售合同状态中文名称"""
    return SALE_CONTRACT_STATUS_MAP.get(status, f'未知({status})')


def get_audit_status_label(audit_status: int) -> str:
    """获取审核状态中文名称"""
    return AUDIT_STATUS_MAP.get(audit_status, f'未知({audit_status})')


class SalesStatsService:
    """销售统计服务"""
    
    @staticmethod
    def get_sales_summary(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> dict:
        """
        获取销售汇总数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            dict: 包含销售汇总信息
        """
        def _query(sess: Session):
            # 使用 total_amount_usd 统计美元金额
            query = sess.query(
                func.count(SmsSaleContract.id).label('total_count'),
                func.sum(
                    func.cast(
                        json_extract_value(SmsSaleContract.total_amount_usd),
                        Float
                    )
                ).label('total_amount_usd')
            ).outerjoin(
                CrmCust, SmsSaleContract.cust_id == CrmCust.id
            ).filter(
                SmsSaleContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                SmsSaleContract.audit_status == 2,
                # 排除已作废 (status = 8)
                SmsSaleContract.status != 8,
                # 排除内部合同 (auto_flag = 1)
                func.coalesce(SmsSaleContract.auto_flag, 0) == 0,
                # 排除内部客户 (internal_flag = 1)
                func.coalesce(CrmCust.internal_flag, 0) == 0
            )
            
            if start_date:
                query = query.filter(SmsSaleContract.create_time >= start_date)
            if end_date:
                query = query.filter(SmsSaleContract.create_time <= end_date)
            
            result = query.first()
            return {
                'total_count': result.total_count or 0,
                'total_amount_usd': float(result.total_amount_usd or 0)
            }
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_sales_by_customer(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        limit: int = 10,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        按客户统计销售额 (Top N)
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            limit: 返回数量限制
            session: 数据库会话
            
        Returns:
            DataFrame: 客户销售统计
        """
        def _query(sess: Session):
            # 使用 total_amount_usd 统计美元金额
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(SmsSaleContract.total_amount_usd),
                    Float
                )
            )
            
            query = sess.query(
                SmsSaleContract.cust_id,
                SmsSaleContract.cust_name,
                func.count(SmsSaleContract.id).label('contract_count'),
                amount_expr.label('total_amount_usd')
            ).outerjoin(
                CrmCust, SmsSaleContract.cust_id == CrmCust.id
            ).filter(
                SmsSaleContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                SmsSaleContract.audit_status == 2,
                # 排除已作废 (status = 8)
                SmsSaleContract.status != 8,
                # 排除内部合同 (auto_flag = 1)
                func.coalesce(SmsSaleContract.auto_flag, 0) == 0,
                # 排除内部客户 (internal_flag = 1)
                func.coalesce(CrmCust.internal_flag, 0) == 0
            )
            
            if start_date:
                query = query.filter(SmsSaleContract.create_time >= start_date)
            if end_date:
                query = query.filter(SmsSaleContract.create_time <= end_date)
            
            query = query.group_by(SmsSaleContract.cust_id, SmsSaleContract.cust_name)\
                        .order_by(amount_expr.desc())\
                        .limit(limit)
            
            results = query.all()
            return pd.DataFrame([
                {
                    '客户ID': r.cust_id,
                    '客户名称': r.cust_name,
                    '合同数量': r.contract_count,
                    '销售金额(USD)': float(r.total_amount_usd or 0)
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_sales_trend(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        group_by: str = 'month',
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取销售趋势数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            group_by: 分组方式 ('day', 'month', 'year')
            session: 数据库会话
            
        Returns:
            DataFrame: 销售趋势数据
        """
        def _query(sess: Session):
            if group_by == 'day':
                date_format = '%Y-%m-%d'
            elif group_by == 'year':
                date_format = '%Y'
            else:
                date_format = '%Y-%m'
            
            # 使用 total_amount_usd 统计美元金额
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(SmsSaleContract.total_amount_usd),
                    Float
                )
            )
            
            query = sess.query(
                func.date_format(SmsSaleContract.create_time, date_format).label('period'),
                func.count(SmsSaleContract.id).label('contract_count'),
                amount_expr.label('total_amount_usd')
            ).outerjoin(
                CrmCust, SmsSaleContract.cust_id == CrmCust.id
            ).filter(
                SmsSaleContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                SmsSaleContract.audit_status == 2,
                # 排除已作废 (status = 8)
                SmsSaleContract.status != 8,
                # 排除内部合同 (auto_flag = 1)
                func.coalesce(SmsSaleContract.auto_flag, 0) == 0,
                # 排除内部客户 (internal_flag = 1)
                func.coalesce(CrmCust.internal_flag, 0) == 0
            )
            
            if start_date:
                query = query.filter(SmsSaleContract.create_time >= start_date)
            if end_date:
                query = query.filter(SmsSaleContract.create_time <= end_date)
            
            query = query.group_by('period').order_by('period')
            
            results = query.all()
            return pd.DataFrame([
                {
                    '日期': r.period,
                    '合同数量': r.contract_count,
                    '销售金额(USD)': float(r.total_amount_usd or 0)
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_sales_detail(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        cust_name: Optional[str] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取销售明细数据 (用于导出)
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            cust_name: 客户名称筛选
            session: 数据库会话
            
        Returns:
            DataFrame: 销售明细数据
        """
        def _query(sess: Session):
            query = sess.query(SmsSaleContract).outerjoin(
                CrmCust, SmsSaleContract.cust_id == CrmCust.id
            ).filter(
                SmsSaleContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                SmsSaleContract.audit_status == 2,
                # 排除已作废 (status = 8)
                SmsSaleContract.status != 8,
                # 排除内部合同 (auto_flag = 1)
                func.coalesce(SmsSaleContract.auto_flag, 0) == 0,
                # 排除内部客户 (internal_flag = 1)
                func.coalesce(CrmCust.internal_flag, 0) == 0
            )
            
            if start_date:
                query = query.filter(SmsSaleContract.create_time >= start_date)
            if end_date:
                query = query.filter(SmsSaleContract.create_time <= end_date)
            if cust_name:
                query = query.filter(SmsSaleContract.cust_name.like(f'%{cust_name}%'))
            
            query = query.order_by(SmsSaleContract.create_time.desc())
            
            results = query.all()
            
            # 获取用户ID到昵称的映射
            user_map = get_user_nickname_map(sess)
            
            def parse_json_amount(json_str):
                """解析 JSON 金额字段"""
                if not json_str:
                    return 0.0, ''
                try:
                    import json
                    data = json.loads(json_str) if isinstance(json_str, str) else json_str
                    return float(data.get('amount', 0) or 0), data.get('currency', '')
                except:
                    return 0.0, ''
            
            def get_creator_name(creator_id):
                """获取创建人名称"""
                if not creator_id:
                    return ''
                return user_map.get(str(creator_id), f'用户{creator_id}')
            
            return pd.DataFrame([
                {
                    '合同编号': r.code,
                    '客户名称': r.cust_name,
                    '公司名称': r.company_name,
                    '合同金额': parse_json_amount(r.total_amount)[0],
                    '合同金额(USD)': parse_json_amount(r.total_amount_usd)[0],
                    '币种': r.currency,
                    '状态': get_status_label(r.status),
                    '审核状态': get_audit_status_label(r.audit_status),
                    '创建人': get_creator_name(r.creator),
                    '创建时间': r.create_time
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_quotation_conversion_rate(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> dict:
        """
        获取报价转化率
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            dict: 转化率数据
        """
        def _query(sess: Session):
            # 报价单数量
            quotation_query = sess.query(
                func.count(SmsQuotation.id)
            ).filter(SmsQuotation.deleted == 0)
            
            # 销售合同数量（排除内部合同、内部客户和已作废）
            contract_query = sess.query(
                func.count(SmsSaleContract.id)
            ).outerjoin(
                CrmCust, SmsSaleContract.cust_id == CrmCust.id
            ).filter(
                SmsSaleContract.deleted == 0,
                # 排除已作废 (status = 8)
                SmsSaleContract.status != 8,
                # 排除内部合同 (auto_flag = 1)
                func.coalesce(SmsSaleContract.auto_flag, 0) == 0,
                # 排除内部客户 (internal_flag = 1)
                func.coalesce(CrmCust.internal_flag, 0) == 0
            )
            
            if start_date:
                quotation_query = quotation_query.filter(SmsQuotation.create_time >= start_date)
                contract_query = contract_query.filter(SmsSaleContract.create_time >= start_date)
            if end_date:
                quotation_query = quotation_query.filter(SmsQuotation.create_time <= end_date)
                contract_query = contract_query.filter(SmsSaleContract.create_time <= end_date)
            
            quotation_count = quotation_query.scalar() or 0
            contract_count = contract_query.scalar() or 0
            
            conversion_rate = 0
            if quotation_count > 0:
                conversion_rate = round(contract_count / quotation_count * 100, 2)
            
            return {
                'quotation_count': quotation_count,
                'contract_count': contract_count,
                'conversion_rate': conversion_rate
            }
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_customer_summary(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取已成交客户明细汇总
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            DataFrame: 客户明细汇总数据
        """
        def _query(sess: Session):
            # 使用 total_amount_usd 统计美元金额
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(SmsSaleContract.total_amount_usd),
                    Float
                )
            )
            
            query = sess.query(
                SmsSaleContract.cust_id,
                SmsSaleContract.cust_name,
                func.count(SmsSaleContract.id).label('contract_count'),
                amount_expr.label('total_amount_usd'),
                func.min(SmsSaleContract.create_time).label('first_deal_time'),
                func.max(SmsSaleContract.create_time).label('last_deal_time')
            ).outerjoin(
                CrmCust, SmsSaleContract.cust_id == CrmCust.id
            ).filter(
                SmsSaleContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                SmsSaleContract.audit_status == 2,
                # 排除已作废 (status = 8)
                SmsSaleContract.status != 8,
                # 排除内部合同 (auto_flag = 1)
                func.coalesce(SmsSaleContract.auto_flag, 0) == 0,
                # 排除内部客户 (internal_flag = 1)
                func.coalesce(CrmCust.internal_flag, 0) == 0
            )
            
            if start_date:
                query = query.filter(SmsSaleContract.create_time >= start_date)
            if end_date:
                query = query.filter(SmsSaleContract.create_time <= end_date)
            
            query = query.group_by(SmsSaleContract.cust_id, SmsSaleContract.cust_name)\
                        .order_by(amount_expr.desc())
            
            results = query.all()
            return pd.DataFrame([
                {
                    '客户ID': r.cust_id,
                    '客户名称': r.cust_name,
                    '成交合同数': r.contract_count,
                    '成交金额(USD)': float(r.total_amount_usd or 0),
                    '首次成交时间': r.first_deal_time,
                    '最近成交时间': r.last_deal_time
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)