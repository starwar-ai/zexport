"""
客户模块 ORM 模型
对应表: crm_cust
"""
from sqlalchemy import Column, BigInteger, String, DateTime, Text, Integer, DECIMAL
from src.database import Base


class CrmCust(Base):
    """客户"""
    __tablename__ = "crm_cust"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    ver = Column(Integer, comment="版本号")
    name = Column(String(100), comment="企业名称")
    shortname = Column(String(100), comment="简称")
    code = Column(String(10), comment="客户编号")
    country_id = Column(BigInteger, comment="国家编码")
    homepage = Column(String(100), comment="官网")
    email = Column(String(100), comment="电子邮件")
    customer_types = Column(String(100), comment="客户类型")
    stage_type = Column(Integer, comment="客户阶段")
    currency = Column(String(3), comment="币种")
    settle_code = Column(BigInteger, comment="收款方式id")
    transport_type = Column(Integer, comment="运输方式")
    address = Column(String(200), comment="营业地址")
    address_shipping = Column(Text, comment="寄件地址")
    phone = Column(String(100), comment="联系电话")
    abroad_flag = Column(Integer, comment="国外客户标志")
    source_type = Column(Integer, comment="客户来源")
    credit_flag = Column(Integer, comment="启用信用额度")
    credit_limit = Column(Text, comment="信用额度")
    zxbquota_flag = Column(Integer, comment="是否是中信保")
    settlement_term_type = Column(String(3), comment="收款类型")
    invoice_header = Column(String(100), comment="开票抬头")
    remark = Column(String(1000), comment="备注信息")
    tax_rate = Column(DECIMAL(19, 6), comment="税率")
    audit_status = Column(Integer, comment="审核状态")
    agent_flag = Column(Integer, comment="是否代理")
    manager_ids = Column(String(100), comment="业务员")
    convert_flag = Column(Integer, comment="转正标识")
    convert_time = Column(DateTime, comment="转正时间")
    enable_flag = Column(Integer, comment="是否启用")
    internal_flag = Column(Integer, default=0, comment="内部企业标识 0-否 1-是")
    annex = Column(Text, comment="附件")
    picture = Column(Text, comment="图片")
    cust_link_code = Column(String(100), comment="关联客户编号")
    creator = Column(Integer, comment="创建者")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(Integer, comment="更新者")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<CrmCust(id={self.id}, name={self.name})>"
