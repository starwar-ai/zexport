"""
销售模块 ORM 模型
对应表: sms_sale_contract, sms_quotation
"""
from sqlalchemy import Column, BigInteger, String, DateTime, DECIMAL, Text, Integer
from src.database import Base


class SmsSaleContract(Base):
    """销售合同"""
    __tablename__ = "sms_sale_contract"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="合同编号")
    cust_id = Column(BigInteger, comment="客户ID")
    cust_code = Column(String(64), comment="客户编号")
    cust_name = Column(String(255), comment="客户名称")
    company_id = Column(BigInteger, comment="内部法人单位ID")
    company_name = Column(String(255), comment="内部法人单位名称")
    # total_amount 是 JSON 类型，存储 JsonAmount 格式: {"value": 100.00, "currency": "USD"}
    total_amount = Column(Text, comment="合同总金额(JSON)")
    total_amount_usd = Column(Text, comment="合同总金额美元(JSON)")
    currency = Column(String(16), comment="币种")
    sale_type = Column(Integer, comment="销售合同类型")
    auto_flag = Column(Integer, default=0, comment="内部合同标识 0-正常合同 1-内部合同")
    status = Column(Integer, comment="状态")
    audit_status = Column(Integer, comment="审核状态")
    sale_contract_date = Column(DateTime, comment="销售合同日期")
    sales = Column(Text, comment="销售人员(JSON)")
    settlement_term_type = Column(String(32), comment="价格条款")
    transport_type = Column(Integer, comment="运输方式")
    total_quantity = Column(Integer, comment="数量合计")
    remark = Column(Text, comment="备注")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<SmsSaleContract(id={self.id}, code={self.code})>"


class SmsQuotation(Base):
    """报价单"""
    __tablename__ = "sms_quotation"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="报价单号")
    cust_id = Column(BigInteger, comment="客户ID")
    status = Column(Integer, comment="状态")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<SmsQuotation(id={self.id}, code={self.code})>"
