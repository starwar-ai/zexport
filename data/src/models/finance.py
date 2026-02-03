"""
财务模块 ORM 模型
对应表: fms_receipt, fms_payment
"""
from sqlalchemy import Column, BigInteger, String, DateTime, DECIMAL, Text, Integer
from src.database import Base


class FmsReceipt(Base):
    """收款单"""
    __tablename__ = "fms_receipt"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="申请单号")
    print_flag = Column(Integer, comment="打印状态")
    print_times = Column(Integer, comment="打印次数")
    audit_status = Column(Integer, comment="审核状态")
    company_id = Column(BigInteger, comment="内部法人单位")
    bank = Column(String(128), comment="开户行")
    bank_address = Column(String(256), comment="开户行地址")
    bank_account = Column(String(64), comment="银行账号")
    bank_poc = Column(String(64), comment="开户行联系人")
    bank_code = Column(String(64), comment="银行行号")
    # amount 是 JSON 类型，存储 JsonAmount 格式: {"value": 100.00, "currency": "USD"}
    amount = Column(Text, comment="收款金额(JSON)")
    rate = Column(Text, comment="实时汇率(JSON)")
    receipt_time = Column(DateTime, comment="收款时间")
    remark = Column(String(512), comment="收款备注")
    # receipt_user 是 JSON 类型，存储 UserDept 格式
    receipt_user = Column(Text, comment="收款人(JSON)")
    approver = Column(Text, comment="最终审批人(JSON)")
    approval_time = Column(DateTime, comment="审批时间")
    business_type = Column(Integer, comment="业务类型")
    business_code = Column(String(64), comment="业务编号")
    business_subject_type = Column(Integer, comment="支付对象类型")
    business_subject_code = Column(String(64), comment="支付对象编号")
    status = Column(Integer, comment="收款状态")
    receipt_type = Column(Integer, comment="收款方式")
    cust_claim_id = Column(Integer, comment="回款认领ID")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<FmsReceipt(id={self.id}, code={self.code})>"


class FmsPayment(Base):
    """付款单"""
    __tablename__ = "fms_payment"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="付款单号")
    # amount 是 JSON 类型，存储 JsonAmount 格式
    amount = Column(Text, comment="付款金额(JSON)")
    status = Column(Integer, comment="状态")
    business_type = Column(Integer, comment="业务类型")
    business_code = Column(String(64), comment="业务编号")
    business_subject_type = Column(Integer, comment="支付对象类型")
    business_subject_code = Column(String(64), comment="支付对象编号")
    audit_status = Column(Integer, comment="审核状态")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<FmsPayment(id={self.id}, code={self.code})>"
