"""
采购模块 ORM 模型
对应表: scm_purchase_contract, scm_vender
"""
from sqlalchemy import Column, BigInteger, String, DateTime, DECIMAL, Text, Integer
from src.database import Base


class ScmPurchaseContract(Base):
    """采购合同"""
    __tablename__ = "scm_purchase_contract"

    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="合同编号")
    vender_id = Column(BigInteger, comment="供应商ID")
    vender_code = Column(String(64), comment="供应商编码")
    vender_name = Column(String(255), comment="供应商名称")
    # total_amount 是 JSON 类型，存储 JsonAmount 格式: {"value": 100.00, "currency": "USD"}
    total_amount = Column(Text, comment="合同总金额(JSON)")
    total_amount_rmb = Column(Text, comment="采购总额人民币(JSON)")
    total_quantity = Column(Integer, comment="采购总数量")
    currency = Column(String(16), comment="币种")
    contract_status = Column(Integer, comment="合同状态")
    audit_status = Column(Integer, comment="审核状态")
    company_id = Column(BigInteger, comment="采购主体")
    sale_contract_id = Column(BigInteger, comment="销售合同ID")
    sale_contract_code = Column(String(64), comment="销售合同编号")
    purchase_time = Column(DateTime, comment="采购时间")
    delivery_date = Column(DateTime, comment="交货日期")
    remark = Column(Text, comment="备注")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")

    def __repr__(self):
        return f"<ScmPurchaseContract(id={self.id}, code={self.code})>"


class ScmVender(Base):
    """供应商"""
    __tablename__ = "scm_vender"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="供应商编码")
    name = Column(String(255), comment="供应商名称")
    contact_name = Column(String(64), comment="联系人")
    contact_phone = Column(String(32), comment="联系电话")
    address = Column(String(512), comment="地址")
    status = Column(Integer, comment="状态")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<ScmVender(id={self.id}, name={self.name})>"
