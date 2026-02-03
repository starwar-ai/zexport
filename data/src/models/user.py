"""
系统用户模块 ORM 模型
对应表: system_users
"""
from sqlalchemy import Column, BigInteger, String, DateTime, Integer
from src.database import Base


class SystemUser(Base):
    """系统用户"""
    __tablename__ = "system_users"
    
    id = Column(BigInteger, primary_key=True, comment="主键ID")
    username = Column(String(30), comment="用户账号")
    nickname = Column(String(30), comment="用户昵称")
    email = Column(String(50), comment="邮箱")
    mobile = Column(String(11), comment="手机号码")
    sex = Column(Integer, comment="性别")
    status = Column(Integer, comment="状态")
    dept_id = Column(BigInteger, comment="部门ID")
    creator = Column(String(64), comment="创建人")
    create_time = Column(DateTime, comment="创建时间")
    updater = Column(String(64), comment="更新人")
    update_time = Column(DateTime, comment="更新时间")
    deleted = Column(Integer, default=0, comment="是否删除")
    
    def __repr__(self):
        return f"<SystemUser(id={self.id}, nickname={self.nickname})>"


def get_user_nickname_map(session) -> dict:
    """
    获取用户ID到昵称的映射字典
    
    Args:
        session: 数据库会话
        
    Returns:
        dict: {user_id: nickname}
    """
    users = session.query(SystemUser.id, SystemUser.nickname).filter(
        SystemUser.deleted == 0
    ).all()
    return {str(u.id): u.nickname or f'用户{u.id}' for u in users}
