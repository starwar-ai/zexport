"""
财务统计服务
"""
from datetime import datetime, date
from typing import Optional
import json
import pandas as pd
from sqlalchemy import func, text, Float
from sqlalchemy.orm import Session

from src.database import get_session
from src.models.finance import FmsReceipt, FmsPayment
from src.models.user import get_user_nickname_map
from src.models.customer import CrmCust
from src.models.purchase import ScmVender


def json_extract_value(column, path='$.amount'):
    """
    使用 MySQL JSON_UNQUOTE + JSON_EXTRACT 提取 JSON 字段中的值
    JsonAmount 格式: {"amount": 100.00, "currency": "RMB"}

    注意: JSON_EXTRACT 返回带引号的字符串，需要用 JSON_UNQUOTE 去除引号
    """
    # 使用 JSON_UNQUOTE 去除引号，然后用 COALESCE 处理 NULL 值
    return func.coalesce(
        func.json_unquote(func.json_extract(column, path)),
        '0'
    )


def json_extract_currency(column, path='$.currency'):
    """
    使用 MySQL JSON_EXTRACT 提取 JSON 字段中的币种
    """
    return func.json_extract(column, path)


def parse_json_amount(json_str):
    """解析 JSON 金额字段"""
    if not json_str:
        return 0.0, ''
    try:
        data = json.loads(json_str) if isinstance(json_str, str) else json_str
        return float(data.get('amount', 0) or 0), data.get('currency', '')
    except:
        return 0.0, ''


def parse_json_user(json_str):
    """解析 JSON 用户字段 (UserDept 格式)"""
    if not json_str:
        return ''
    try:
        data = json.loads(json_str) if isinstance(json_str, str) else json_str
        return data.get('userName', '') or data.get('name', '')
    except:
        return ''


class FinanceStatsService:
    """财务统计服务"""
    
    @staticmethod
    def get_receipt_summary(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> dict:
        """
        获取收款汇总数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            dict: 包含收款汇总信息
        """
        def _query(sess: Session):
            # amount 是 JSON 类型，使用 JSON_EXTRACT 提取 value 字段
            query = sess.query(
                func.count(FmsReceipt.id).label('total_count'),
                func.sum(
                    func.cast(
                        json_extract_value(FmsReceipt.amount),
                        Float
                    )
                ).label('total_amount')
            ).filter(FmsReceipt.deleted == 0)
            
            if start_date:
                query = query.filter(FmsReceipt.create_time >= start_date)
            if end_date:
                query = query.filter(FmsReceipt.create_time <= end_date)
            
            result = query.first()
            return {
                'total_count': result.total_count or 0,
                'total_amount': float(result.total_amount or 0)
            }
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_payment_summary(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> dict:
        """
        获取付款汇总数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            dict: 包含付款汇总信息
        """
        def _query(sess: Session):
            # amount 是 JSON 类型，使用 JSON_EXTRACT 提取 value 字段
            query = sess.query(
                func.count(FmsPayment.id).label('total_count'),
                func.sum(
                    func.cast(
                        json_extract_value(FmsPayment.amount),
                        Float
                    )
                ).label('total_amount')
            ).filter(FmsPayment.deleted == 0)
            
            if start_date:
                query = query.filter(FmsPayment.create_time >= start_date)
            if end_date:
                query = query.filter(FmsPayment.create_time <= end_date)
            
            result = query.first()
            return {
                'total_count': result.total_count or 0,
                'total_amount': float(result.total_amount or 0)
            }
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_receipt_by_business(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        limit: int = 10,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        按业务类型统计收款额 (Top N)
        
        注意: fms_receipt 表没有直接的 cust_name 字段，
        需要通过 business_subject_code 关联客户表查询
        这里按 business_code 分组统计
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            limit: 返回数量限制
            session: 数据库会话
            
        Returns:
            DataFrame: 业务收款统计
        """
        def _query(sess: Session):
            # 使用 JSON_EXTRACT 提取金额值进行聚合
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(FmsReceipt.amount),
                    Float
                )
            )
            
            query = sess.query(
                FmsReceipt.business_type,
                FmsReceipt.business_code,
                func.count(FmsReceipt.id).label('receipt_count'),
                amount_expr.label('total_amount')
            ).filter(
                FmsReceipt.deleted == 0
            )
            
            if start_date:
                query = query.filter(FmsReceipt.create_time >= start_date)
            if end_date:
                query = query.filter(FmsReceipt.create_time <= end_date)
            
            query = query.group_by(FmsReceipt.business_type, FmsReceipt.business_code)\
                        .order_by(amount_expr.desc())\
                        .limit(limit)
            
            results = query.all()
            return pd.DataFrame([
                {
                    '业务类型': r.business_type,
                    '业务编号': r.business_code,
                    '收款笔数': r.receipt_count,
                    '收款金额': float(r.total_amount or 0)
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_receipt_by_customer(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        limit: int = 10,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        按客户统计收款额 (Top N)
        
        注意: fms_receipt 表没有直接的客户名称字段，
        这里按 business_subject_code 分组统计
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            limit: 返回数量限制
            session: 数据库会话
            
        Returns:
            DataFrame: 客户收款统计
        """
        def _query(sess: Session):
            # 使用 JSON_EXTRACT 提取金额值进行聚合
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(FmsReceipt.amount),
                    Float
                )
            )
            
            query = sess.query(
                FmsReceipt.business_subject_code.label('customer_code'),
                func.count(FmsReceipt.id).label('receipt_count'),
                amount_expr.label('total_amount')
            ).filter(
                FmsReceipt.deleted == 0,
                FmsReceipt.business_subject_code.isnot(None)
            )
            
            if start_date:
                query = query.filter(FmsReceipt.create_time >= start_date)
            if end_date:
                query = query.filter(FmsReceipt.create_time <= end_date)
            
            query = query.group_by(FmsReceipt.business_subject_code)\
                        .order_by(amount_expr.desc())\
                        .limit(limit)
            
            results = query.all()
            return pd.DataFrame([
                {
                    '客户名称': r.customer_code or '未知',
                    '收款笔数': r.receipt_count,
                    '收款金额': float(r.total_amount or 0)
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_payment_by_vender(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        limit: int = 10,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        按供应商统计付款额 (Top N)
        
        注意: fms_payment 表没有直接的供应商名称字段，
        这里按 code 前缀分组统计
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            limit: 返回数量限制
            session: 数据库会话
            
        Returns:
            DataFrame: 供应商付款统计
        """
        def _query(sess: Session):
            # 使用 JSON_EXTRACT 提取金额值进行聚合
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(FmsPayment.amount),
                    Float
                )
            )
            
            query = sess.query(
                func.left(FmsPayment.code, 10).label('vender_code'),
                func.count(FmsPayment.id).label('payment_count'),
                amount_expr.label('total_amount')
            ).filter(
                FmsPayment.deleted == 0
            )
            
            if start_date:
                query = query.filter(FmsPayment.create_time >= start_date)
            if end_date:
                query = query.filter(FmsPayment.create_time <= end_date)
            
            query = query.group_by('vender_code')\
                        .order_by(amount_expr.desc())\
                        .limit(limit)
            
            results = query.all()
            return pd.DataFrame([
                {
                    '供应商名称': r.vender_code or '未知',
                    '付款笔数': r.payment_count,
                    '付款金额': float(r.total_amount or 0)
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_cashflow_trend(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        group_by: str = 'month',
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取资金流趋势数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            group_by: 分组方式 ('day', 'month', 'year')
            session: 数据库会话
            
        Returns:
            DataFrame: 资金流趋势数据
        """
        def _query(sess: Session):
            if group_by == 'day':
                date_format = '%Y-%m-%d'
            elif group_by == 'year':
                date_format = '%Y'
            else:
                date_format = '%Y-%m'
            
            # 收款趋势 - 使用 JSON_EXTRACT 提取金额
            receipt_query = sess.query(
                func.date_format(FmsReceipt.create_time, date_format).label('period'),
                func.sum(
                    func.cast(
                        json_extract_value(FmsReceipt.amount),
                        Float
                    )
                ).label('amount')
            ).filter(FmsReceipt.deleted == 0)
            
            # 付款趋势 - 使用 JSON_EXTRACT 提取金额
            payment_query = sess.query(
                func.date_format(FmsPayment.create_time, date_format).label('period'),
                func.sum(
                    func.cast(
                        json_extract_value(FmsPayment.amount),
                        Float
                    )
                ).label('amount')
            ).filter(FmsPayment.deleted == 0)
            
            if start_date:
                receipt_query = receipt_query.filter(FmsReceipt.create_time >= start_date)
                payment_query = payment_query.filter(FmsPayment.create_time >= start_date)
            if end_date:
                receipt_query = receipt_query.filter(FmsReceipt.create_time <= end_date)
                payment_query = payment_query.filter(FmsPayment.create_time <= end_date)
            
            receipt_query = receipt_query.group_by('period')
            payment_query = payment_query.group_by('period')
            
            receipt_results = {r.period: float(r.amount or 0) for r in receipt_query.all()}
            payment_results = {r.period: float(r.amount or 0) for r in payment_query.all()}
            
            # 合并数据
            all_periods = sorted(set(receipt_results.keys()) | set(payment_results.keys()))
            data = []
            for period in all_periods:
                receipt = receipt_results.get(period, 0)
                payment = payment_results.get(period, 0)
                data.append({
                    '日期': period,
                    '收款金额': receipt,
                    '付款金额': payment,
                    '净现金流': receipt - payment
                })
            
            return pd.DataFrame(data)
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_receipt_detail(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        business_code: Optional[str] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取收款明细数据 (用于导出)
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            business_code: 业务编号筛选
            session: 数据库会话
            
        Returns:
            DataFrame: 收款明细数据
        """
        def _query(sess: Session):
            query = sess.query(FmsReceipt).filter(FmsReceipt.deleted == 0)
            
            if start_date:
                query = query.filter(FmsReceipt.create_time >= start_date)
            if end_date:
                query = query.filter(FmsReceipt.create_time <= end_date)
            if business_code:
                query = query.filter(FmsReceipt.business_code.like(f'%{business_code}%'))
            
            query = query.order_by(FmsReceipt.create_time.desc())
            
            results = query.all()
            
            # 获取用户ID到昵称的映射
            user_map = get_user_nickname_map(sess)
            
            # 获取客户编码到名称的映射
            cust_codes = [r.business_subject_code for r in results if r.business_subject_code]
            cust_map = {}
            if cust_codes:
                custs = sess.query(CrmCust.code, CrmCust.name).filter(
                    CrmCust.code.in_(cust_codes),
                    CrmCust.deleted == 0
                ).all()
                cust_map = {c.code: c.name for c in custs}
            
            def get_creator_name(creator_id):
                """获取创建人名称"""
                if not creator_id:
                    return ''
                return user_map.get(str(creator_id), f'用户{creator_id}')
            
            def get_cust_name(subject_code):
                """获取客户名称"""
                if not subject_code:
                    return ''
                return cust_map.get(subject_code, subject_code)
            
            return pd.DataFrame([
                {
                    '收款单号': r.code,
                    '客户名称': get_cust_name(r.business_subject_code),
                    '业务编号': r.business_code,
                    '收款金额': parse_json_amount(r.amount)[0],
                    '币种': parse_json_amount(r.amount)[1],
                    '收款时间': r.receipt_time,
                    '收款方式': r.receipt_type,
                    '状态': r.status,
                    '审核状态': r.audit_status,
                    '收款人': parse_json_user(r.receipt_user),
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
    def get_payment_detail(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取付款明细数据 (用于导出)
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            DataFrame: 付款明细数据
        """
        def _query(sess: Session):
            query = sess.query(FmsPayment).filter(FmsPayment.deleted == 0)
            
            if start_date:
                query = query.filter(FmsPayment.create_time >= start_date)
            if end_date:
                query = query.filter(FmsPayment.create_time <= end_date)
            
            query = query.order_by(FmsPayment.create_time.desc())
            
            results = query.all()
            
            # 获取用户ID到昵称的映射
            user_map = get_user_nickname_map(sess)
            
            # 获取供应商编码到名称的映射
            vender_codes = [r.business_subject_code for r in results if r.business_subject_code]
            vender_map = {}
            if vender_codes:
                venders = sess.query(ScmVender.code, ScmVender.name).filter(
                    ScmVender.code.in_(vender_codes),
                    ScmVender.deleted == 0
                ).all()
                vender_map = {v.code: v.name for v in venders}
            
            def get_creator_name(creator_id):
                """获取创建人名称"""
                if not creator_id:
                    return ''
                return user_map.get(str(creator_id), f'用户{creator_id}')
            
            def get_vender_name(subject_code):
                """获取供应商名称"""
                if not subject_code:
                    return ''
                return vender_map.get(subject_code, subject_code)
            
            return pd.DataFrame([
                {
                    '付款单号': r.code,
                    '供应商名称': get_vender_name(r.business_subject_code),
                    '付款金额': parse_json_amount(r.amount)[0],
                    '币种': parse_json_amount(r.amount)[1],
                    '状态': r.status,
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
