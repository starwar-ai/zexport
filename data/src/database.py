"""
数据库连接管理
"""
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from contextlib import contextmanager

from config.settings import settings

# 创建数据库引擎
engine = create_engine(
    settings.DATABASE_URL,
    pool_size=settings.POOL_SIZE,
    pool_recycle=settings.POOL_RECYCLE,
    pool_pre_ping=True,  # 自动检测连接有效性
    echo=False,  # 设为 True 可以打印 SQL 语句
)

# 创建会话工厂
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 声明基类
Base = declarative_base()


@contextmanager
def get_session():
    """
    获取数据库会话的上下文管理器
    
    使用示例:
        with get_session() as session:
            result = session.query(Model).all()
    """
    session = SessionLocal()
    try:
        yield session
        session.commit()
    except Exception:
        session.rollback()
        raise
    finally:
        session.close()


def get_db():
    """
    获取数据库会话 (用于依赖注入)
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
