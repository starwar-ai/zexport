"""
采购统计服务
"""
from datetime import datetime, date
from typing import Optional
import json
import pandas as pd
from sqlalchemy import func, text, Float
from sqlalchemy.orm import Session

from src.database import get_session
from src.models.purchase import ScmPurchaseContract, ScmVender
from src.models.user import get_user_nickname_map


# 采购合同状态映射
PURCHASE_CONTRACT_STATUS_MAP = {
    1: '待提交',
    2: '待审批',
    3: '已驳回',
    4: '待下单',
    5: '待到货',
    6: '已完成',
    7: '已结案',
}

# 审核状态映射
AUDIT_STATUS_MAP = {
    0: '待提交',
    1: '审核中',
    2: '已通过',
    3: '已拒绝',
    4: '已撤回',
}


def get_purchase_contract_status_label(status) -> str:
    """获取采购合同状态中文名称"""
    if status is None:
        return '未知'
    return PURCHASE_CONTRACT_STATUS_MAP.get(int(status), f'未知({status})')


def get_audit_status_label(audit_status) -> str:
    """获取审核状态中文名称"""
    if audit_status is None:
        return '未知'
    return AUDIT_STATUS_MAP.get(int(audit_status), f'未知({audit_status})')


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


def parse_json_amount(json_str):
    """解析 JSON 金额字段"""
    if not json_str:
        return 0.0, ''
    try:
        data = json.loads(json_str) if isinstance(json_str, str) else json_str
        return float(data.get('amount', 0) or 0), data.get('currency', '')
    except:
        return 0.0, ''


class PurchaseStatsService:
    """采购统计服务"""
    
    @staticmethod
    def get_purchase_summary(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> dict:
        """
        获取采购汇总数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            dict: 包含采购汇总信息
        """
        def _query(sess: Session):
            # total_amount 是 JSON 类型，使用 JSON_EXTRACT 提取 value 字段
            query = sess.query(
                func.count(ScmPurchaseContract.id).label('total_count'),
                func.sum(
                    func.cast(
                        json_extract_value(ScmPurchaseContract.total_amount),
                        Float
                    )
                ).label('total_amount')
            ).filter(
                ScmPurchaseContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                ScmPurchaseContract.audit_status == 2,
                # 排除已结案 (contract_status = 7)
                func.coalesce(ScmPurchaseContract.contract_status, 0) != 7
            )
            
            if start_date:
                query = query.filter(ScmPurchaseContract.create_time >= start_date)
            if end_date:
                query = query.filter(ScmPurchaseContract.create_time <= end_date)
            
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
    def get_purchase_by_vender(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        limit: int = 10,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        按供应商统计采购额 (Top N)
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            limit: 返回数量限制
            session: 数据库会话
            
        Returns:
            DataFrame: 供应商采购统计
        """
        def _query(sess: Session):
            # 使用 JSON_EXTRACT 提取金额值进行聚合
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(ScmPurchaseContract.total_amount),
                    Float
                )
            )
            
            query = sess.query(
                ScmPurchaseContract.vender_id,
                ScmPurchaseContract.vender_name,
                func.count(ScmPurchaseContract.id).label('contract_count'),
                amount_expr.label('total_amount')
            ).filter(
                ScmPurchaseContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                ScmPurchaseContract.audit_status == 2,
                # 排除已结案 (contract_status = 7)
                func.coalesce(ScmPurchaseContract.contract_status, 0) != 7
            )
            
            if start_date:
                query = query.filter(ScmPurchaseContract.create_time >= start_date)
            if end_date:
                query = query.filter(ScmPurchaseContract.create_time <= end_date)
            
            query = query.group_by(ScmPurchaseContract.vender_id, ScmPurchaseContract.vender_name)\
                        .order_by(amount_expr.desc())\
                        .limit(limit)
            
            results = query.all()
            return pd.DataFrame([
                {
                    '供应商ID': r.vender_id,
                    '供应商名称': r.vender_name,
                    '合同数量': r.contract_count,
                    '采购金额': r.total_amount if r.total_amount else 0.0
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_purchase_trend(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        group_by: str = 'month',
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取采购趋势数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            group_by: 分组方式 ('day', 'month', 'year')
            session: 数据库会话
            
        Returns:
            DataFrame: 采购趋势数据
        """
        def _query(sess: Session):
            if group_by == 'day':
                date_format = '%Y-%m-%d'
            elif group_by == 'year':
                date_format = '%Y'
            else:
                date_format = '%Y-%m'
            
            # 使用 JSON_EXTRACT 提取金额值
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(ScmPurchaseContract.total_amount),
                    Float
                )
            )
            
            query = sess.query(
                func.date_format(ScmPurchaseContract.create_time, date_format).label('period'),
                func.count(ScmPurchaseContract.id).label('contract_count'),
                amount_expr.label('total_amount')
            ).filter(
                ScmPurchaseContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                ScmPurchaseContract.audit_status == 2,
                # 排除已结案 (contract_status = 7)
                func.coalesce(ScmPurchaseContract.contract_status, 0) != 7
            )
            
            if start_date:
                query = query.filter(ScmPurchaseContract.create_time >= start_date)
            if end_date:
                query = query.filter(ScmPurchaseContract.create_time <= end_date)
            
            query = query.group_by('period').order_by('period')
            
            results = query.all()
            return pd.DataFrame([
                {
                    '日期': r.period,
                    '合同数量': r.contract_count,
                    '采购金额': r.total_amount if r.total_amount else 0.0
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_purchase_detail(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        vender_name: Optional[str] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取采购明细数据 (用于导出)
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            vender_name: 供应商名称筛选
            session: 数据库会话
            
        Returns:
            DataFrame: 采购明细数据
        """
        def _query(sess: Session):
            # 只选择需要的字段，避免查询已删除的 tenant_id 列
            query = sess.query(
                ScmPurchaseContract.id,
                ScmPurchaseContract.vender_name,
                ScmPurchaseContract.total_amount,
                ScmPurchaseContract.currency,
                ScmPurchaseContract.contract_status,
                ScmPurchaseContract.audit_status,
                ScmPurchaseContract.creator,
                ScmPurchaseContract.create_time
            ).filter(
                ScmPurchaseContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                ScmPurchaseContract.audit_status == 2,
                # 排除已结案 (contract_status = 7)
                func.coalesce(ScmPurchaseContract.contract_status, 0) != 7
            )
            
            if start_date:
                query = query.filter(ScmPurchaseContract.create_time >= start_date)
            if end_date:
                query = query.filter(ScmPurchaseContract.create_time <= end_date)
            if vender_name:
                query = query.filter(ScmPurchaseContract.vender_name.like(f'%{vender_name}%'))
            
            query = query.order_by(ScmPurchaseContract.create_time.desc())
            
            results = query.all()
            
            # 获取用户ID到昵称的映射
            user_map = get_user_nickname_map(sess)
            
            def get_creator_name(creator_id):
                """获取创建人名称"""
                if not creator_id:
                    return ''
                return user_map.get(str(creator_id), f'用户{creator_id}')
            
            return pd.DataFrame([
                {
                    'ID': r.id,
                    '供应商名称': r.vender_name,
                    '合同金额': parse_json_amount(r.total_amount)[0],
                    '币种': parse_json_amount(r.total_amount)[1] or r.currency or '',
                    '合同状态': get_purchase_contract_status_label(r.contract_status),
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
    def get_vender_count(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> int:
        """
        获取供应商数量
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            int: 供应商数量
        """
        def _query(sess: Session):
            query = sess.query(func.count(ScmVender.id)).filter(ScmVender.deleted == 0)
            
            if start_date:
                query = query.filter(ScmVender.create_time >= start_date)
            if end_date:
                query = query.filter(ScmVender.create_time <= end_date)
            
            return query.scalar() or 0
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_vender_summary(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取已成交供应商明细汇总
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            DataFrame: 供应商明细汇总数据
        """
        def _query(sess: Session):
            # 使用 JSON_EXTRACT 提取金额值进行聚合
            amount_expr = func.sum(
                func.cast(
                    json_extract_value(ScmPurchaseContract.total_amount),
                    Float
                )
            )
            
            query = sess.query(
                ScmPurchaseContract.vender_id,
                ScmPurchaseContract.vender_name,
                func.count(ScmPurchaseContract.id).label('contract_count'),
                amount_expr.label('total_amount'),
                func.min(ScmPurchaseContract.create_time).label('first_deal_time'),
                func.max(ScmPurchaseContract.create_time).label('last_deal_time')
            ).filter(
                ScmPurchaseContract.deleted == 0,
                # 只统计审核已通过 (audit_status = 2)
                ScmPurchaseContract.audit_status == 2,
                # 排除已结案 (contract_status = 7)
                func.coalesce(ScmPurchaseContract.contract_status, 0) != 7
            )
            
            if start_date:
                query = query.filter(ScmPurchaseContract.create_time >= start_date)
            if end_date:
                query = query.filter(ScmPurchaseContract.create_time <= end_date)
            
            query = query.group_by(ScmPurchaseContract.vender_id, ScmPurchaseContract.vender_name)\
                        .order_by(amount_expr.desc())
            
            results = query.all()
            return pd.DataFrame([
                {
                    '供应商ID': r.vender_id,
                    '供应商名称': r.vender_name,
                    '成交合同数': r.contract_count,
                    '成交金额': float(r.total_amount or 0),
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