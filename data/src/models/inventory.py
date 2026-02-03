"""
库存模块 ORM 模型
对应表: wms_stock, wms_bill, wms_warehouse
"""
from sqlalchemy import Column, BigInteger, String, DateTime, DECIMAL, Text, Integer
from src.database import Base


class WmsStock(Base):
    """库存明细"""
    __tablename__ = "wms_stock"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    batch_code = Column(String(64), comment="批次号")
    bill_id = Column(BigInteger, comment="单据主键")
    bill_item_id = Column(BigInteger, comment="单据明细主键")
    sku_id = Column(BigInteger, comment="产品主键")
    sku_code = Column(String(64), comment="产品编码")
    sku_name = Column(String(255), comment="产品中文名称")
    csku_code = Column(String(64), comment="客户货号")
    basic_sku_code = Column(String(64), comment="基础产品编号")
    receipt_time = Column(DateTime, comment="入库时间")
    producing_quantity = Column(Integer, comment="在制数量")
    init_quantity = Column(Integer, comment="入库数量")
    used_quantity = Column(Integer, comment="出库数量")
    lock_quantity = Column(Integer, comment="锁定数量")
    available_quantity = Column(Integer, comment="可用数量")
    qty_per_outerbox = Column(Integer, comment="外箱装量")
    qty_per_innerbox = Column(Integer, comment="内盒装量")
    # price 是 JSON 类型，存储 JsonAmount 格式
    price = Column(Text, comment="单价(JSON)")
    total_amount = Column(Text, comment="总金额(JSON)")
    purchase_contract_id = Column(BigInteger, comment="采购合同主键")
    purchase_contract_code = Column(String(64), comment="采购合同号")
    sale_contract_id = Column(BigInteger, comment="外销合同主键")
    sale_contract_code = Column(String(64), comment="外销合同号")
    warehouse_id = Column(BigInteger, comment="仓库主键")
    warehouse_name = Column(String(128), comment="仓库名称")
    position = Column(String(128), comment="存放位置")
    vender_id = Column(BigInteger, comment="供应商主键")
    vender_code = Column(String(64), comment="供应商编码")
    vender_name = Column(String(255), comment="供应商名称")
    cust_id = Column(BigInteger, comment="客户主键")
    cust_code = Column(String(64), comment="客户编码")
    cust_name = Column(String(255), comment="客户名称")
    company_id = Column(BigInteger, comment="归属公司主键")
    company_name = Column(String(255), comment="归属公司名称")
    remark = Column(String(512), comment="备注")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<WmsStock(id={self.id}, sku_name={self.sku_name})>"


class WmsBill(Base):
    """出入库单"""
    __tablename__ = "wms_bill"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="单号")
    bill_type = Column(Integer, comment="入/出库类型: 1-入库单 2-出库单")
    bill_status = Column(Integer, comment="单据状态: 1-未确认 2-已确认 3-作废")
    notice_code = Column(String(64), comment="入/出库通知单号")
    sale_contract_id = Column(BigInteger, comment="销售合同主键")
    sale_contract_code = Column(String(64), comment="销售合同号")
    bill_time = Column(DateTime, comment="入/出库时间")
    warehouse_id = Column(BigInteger, comment="仓库主键")
    warehouse_name = Column(String(128), comment="仓库名称")
    print_flag = Column(Integer, comment="打印状态")
    print_times = Column(Integer, comment="打印次数")
    company_id = Column(BigInteger, comment="归属公司主键")
    company_name = Column(String(255), comment="归属公司名称")
    remark = Column(String(512), comment="备注")
    sku_codes = Column(String(1024), comment="产品编码")
    pictures = Column(Text, comment="图片列表(JSON)")
    source_type = Column(Integer, comment="来源单类型")
    source_code = Column(String(64), comment="来源单编号")
    source_id = Column(BigInteger, comment="来源单主键")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<WmsBill(id={self.id}, code={self.code})>"


class WmsBillItem(Base):
    """出入库单据明细"""
    __tablename__ = "wms_bill_item"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    source_id = Column(BigInteger, comment="来源单主键")
    source_type = Column(Integer, comment="来源单据类型")
    bill_type = Column(Integer, comment="入/出库类型 1-入库单、2-出库单")
    sku_id = Column(BigInteger, comment="产品主键")
    sku_code = Column(String(64), comment="产品编号")
    sku_name = Column(String(255), comment="产品中文名称")
    order_quantity = Column(Integer, comment="应收/出数量")
    act_quantity = Column(Integer, comment="实收/出数量")
    warehouse_id = Column(BigInteger, comment="仓库主键")
    warehouse_name = Column(String(255), comment="仓库名称")
    price = Column(Text, comment="单价(JSON)")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<WmsBillItem(id={self.id}, bill_id={self.bill_id})>"


class WmsWarehouse(Base):
    """仓库"""
    __tablename__ = "wms_warehouse"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    code = Column(String(64), comment="仓库编码")
    name = Column(String(128), comment="仓库名称")
    address = Column(String(512), comment="仓库地址")
    enable_flag = Column(Integer, comment="启用标识")
    manager_ids = Column(Text, comment="仓管主键列表(JSON)")
    vender_flag = Column(Integer, comment="供应仓标识")
    vender_code = Column(String(64), comment="供应商编码")
    vender_name = Column(String(255), comment="供应商名称")
    remark = Column(String(512), comment="备注")
    default_flag = Column(Integer, comment="默认仓库标记")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<WmsWarehouse(id={self.id}, name={self.name})>"
