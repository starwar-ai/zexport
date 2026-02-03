"""
库存统计服务
"""
from datetime import datetime, date
from typing import Optional
import json
import pandas as pd
from sqlalchemy import func
from sqlalchemy.orm import Session

from src.database import get_session
from src.models.inventory import WmsStock, WmsBill, WmsBillItem
from src.models.user import get_user_nickname_map


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


class InventoryStatsService:
    """库存统计服务"""
    
    @staticmethod
    def get_stock_summary(session: Optional[Session] = None) -> dict:
        """
        获取库存汇总数据
        
        Args:
            session: 数据库会话
            
        Returns:
            dict: 包含库存汇总信息
        """
        def _query(sess: Session):
            # 使用正确的字段名: init_quantity, lock_quantity, available_quantity
            query = sess.query(
                func.count(WmsStock.id).label('sku_count'),
                func.sum(WmsStock.init_quantity).label('total_quantity'),
                func.sum(WmsStock.lock_quantity).label('locked_quantity'),
                func.sum(WmsStock.available_quantity).label('available_quantity')
            ).filter(WmsStock.deleted == 0)
            
            result = query.first()
            return {
                'sku_count': result.sku_count or 0,
                'total_quantity': int(result.total_quantity or 0),
                'locked_quantity': int(result.locked_quantity or 0),
                'available_quantity': int(result.available_quantity or 0)
            }
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_stock_by_warehouse(session: Optional[Session] = None) -> pd.DataFrame:
        """
        按仓库统计库存
        
        Args:
            session: 数据库会话
            
        Returns:
            DataFrame: 仓库库存统计
        """
        def _query(sess: Session):
            query = sess.query(
                WmsStock.warehouse_id,
                WmsStock.warehouse_name,
                func.count(WmsStock.id).label('sku_count'),
                func.sum(WmsStock.init_quantity).label('total_quantity'),
                func.sum(WmsStock.available_quantity).label('available_quantity')
            ).filter(
                WmsStock.deleted == 0
            ).group_by(WmsStock.warehouse_id, WmsStock.warehouse_name)
            
            results = query.all()
            return pd.DataFrame([
                {
                    '仓库ID': r.warehouse_id,
                    '仓库名称': r.warehouse_name,
                    'SKU数量': r.sku_count,
                    '库存总量': int(r.total_quantity or 0),
                    '可用数量': int(r.available_quantity or 0)
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_stock_detail(
        warehouse_name: Optional[str] = None,
        sku_name: Optional[str] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取库存明细数据 (用于导出)
        
        Args:
            warehouse_name: 仓库名称筛选
            sku_name: 产品名称筛选
            session: 数据库会话
            
        Returns:
            DataFrame: 库存明细数据
        """
        def _query(sess: Session):
            query = sess.query(WmsStock).filter(WmsStock.deleted == 0)
            
            if warehouse_name:
                query = query.filter(WmsStock.warehouse_name.like(f'%{warehouse_name}%'))
            if sku_name:
                query = query.filter(WmsStock.sku_name.like(f'%{sku_name}%'))
            
            query = query.order_by(WmsStock.warehouse_name, WmsStock.sku_name)
            
            results = query.all()
            return pd.DataFrame([
                {
                    '仓库名称': r.warehouse_name,
                    '产品名称': r.sku_name,
                    'SKU编码': r.sku_code,
                    '批次号': r.batch_code,
                    '入库数量': int(r.init_quantity or 0),
                    '已出库数量': int(r.used_quantity or 0),
                    '锁定数量': int(r.lock_quantity or 0),
                    '可用数量': int(r.available_quantity or 0),
                    '存放位置': r.position,
                    '供应商': r.vender_name
                }
                for r in results
            ])
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_inout_summary(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        session: Optional[Session] = None
    ) -> dict:
        """
        获取出入库汇总
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            session: 数据库会话
            
        Returns:
            dict: 出入库汇总数据
        """
        def _query(sess: Session):
            # 入库统计 - 从 wms_bill 表统计单据数
            in_bill_query = sess.query(
                func.count(WmsBill.id).label('count')
            ).filter(
                WmsBill.deleted == 0,
                WmsBill.bill_type == 1  # 入库
            )
            
            # 入库数量 - 从 wms_bill_item 表统计
            in_qty_query = sess.query(
                func.sum(WmsBillItem.act_quantity).label('quantity')
            ).filter(
                WmsBillItem.deleted == 0,
                WmsBillItem.bill_type == 1  # 入库
            )
            
            # 出库统计 - 从 wms_bill 表统计单据数
            out_bill_query = sess.query(
                func.count(WmsBill.id).label('count')
            ).filter(
                WmsBill.deleted == 0,
                WmsBill.bill_type == 2  # 出库
            )
            
            # 出库数量 - 从 wms_bill_item 表统计
            out_qty_query = sess.query(
                func.sum(WmsBillItem.act_quantity).label('quantity')
            ).filter(
                WmsBillItem.deleted == 0,
                WmsBillItem.bill_type == 2  # 出库
            )
            
            if start_date:
                in_bill_query = in_bill_query.filter(WmsBill.create_time >= start_date)
                out_bill_query = out_bill_query.filter(WmsBill.create_time >= start_date)
                in_qty_query = in_qty_query.filter(WmsBillItem.create_time >= start_date)
                out_qty_query = out_qty_query.filter(WmsBillItem.create_time >= start_date)
            if end_date:
                in_bill_query = in_bill_query.filter(WmsBill.create_time <= end_date)
                out_bill_query = out_bill_query.filter(WmsBill.create_time <= end_date)
                in_qty_query = in_qty_query.filter(WmsBillItem.create_time <= end_date)
                out_qty_query = out_qty_query.filter(WmsBillItem.create_time <= end_date)
            
            in_count = in_bill_query.scalar() or 0
            in_quantity = in_qty_query.scalar() or 0
            out_count = out_bill_query.scalar() or 0
            out_quantity = out_qty_query.scalar() or 0
            
            return {
                'in_count': in_count,
                'in_quantity': int(in_quantity),
                'out_count': out_count,
                'out_quantity': int(out_quantity)
            }
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_inout_trend(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        group_by: str = 'month',
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取出入库趋势数据
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            group_by: 分组方式 ('day', 'month', 'year')
            session: 数据库会话
            
        Returns:
            DataFrame: 出入库趋势数据
        """
        def _query(sess: Session):
            if group_by == 'day':
                date_format = '%Y-%m-%d'
            elif group_by == 'year':
                date_format = '%Y'
            else:
                date_format = '%Y-%m'
            
            # 从 wms_bill_item 表按日期和类型统计数量
            query = sess.query(
                func.date_format(WmsBillItem.create_time, date_format).label('period'),
                WmsBillItem.bill_type,
                func.sum(WmsBillItem.act_quantity).label('total_quantity')
            ).filter(
                WmsBillItem.deleted == 0
            )
            
            if start_date:
                query = query.filter(WmsBillItem.create_time >= start_date)
            if end_date:
                query = query.filter(WmsBillItem.create_time <= end_date)
            
            query = query.group_by('period', WmsBillItem.bill_type).order_by('period')
            
            results = query.all()
            
            # 转换为宽表格式
            data = {}
            for r in results:
                if r.period not in data:
                    data[r.period] = {'日期': r.period, '入库数量': 0, '出库数量': 0}
                if r.bill_type == 1:
                    data[r.period]['入库数量'] = int(r.total_quantity or 0)
                else:
                    data[r.period]['出库数量'] = int(r.total_quantity or 0)
            
            return pd.DataFrame(list(data.values()))
        
        if session:
            return _query(session)
        else:
            with get_session() as sess:
                return _query(sess)
    
    @staticmethod
    def get_bill_detail(
        start_date: Optional[date] = None,
        end_date: Optional[date] = None,
        bill_type: Optional[int] = None,
        session: Optional[Session] = None
    ) -> pd.DataFrame:
        """
        获取出入库单明细 (用于导出)
        
        Args:
            start_date: 开始日期
            end_date: 结束日期
            bill_type: 单据类型 (1入库 2出库)
            session: 数据库会话
            
        Returns:
            DataFrame: 出入库单明细
        """
        def _query(sess: Session):
            # 仅查询单据信息
            query = sess.query(WmsBill).filter(WmsBill.deleted == 0)
            
            if start_date:
                query = query.filter(WmsBill.create_time >= start_date)
            if end_date:
                query = query.filter(WmsBill.create_time <= end_date)
            if bill_type:
                query = query.filter(WmsBill.bill_type == bill_type)
            
            query = query.order_by(WmsBill.create_time.desc())
            
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
                    '单据编号': r.code,
                    '单据类型': '入库' if r.bill_type == 1 else '出库',
                    '仓库名称': r.warehouse_name,
                    '单据状态': r.bill_status,
                    '入/出库时间': r.bill_time,
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
