"""
数据库配置设置
"""
import os
from dataclasses import dataclass
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()


@dataclass
class Settings:
    """应用配置"""
    
    # 数据库配置
    DB_HOST: str = os.getenv("DB_HOST", "localhost")
    DB_PORT: int = int(os.getenv("DB_PORT", "3306"))
    DB_USER: str = os.getenv("DB_USER", "root")
    DB_PASSWORD: str = os.getenv("DB_PASSWORD", "")
    DB_NAME: str = os.getenv("DB_NAME", "eplus")
    DB_CHARSET: str = os.getenv("DB_CHARSET", "utf8mb4")
    
    # 连接池配置
    POOL_SIZE: int = int(os.getenv("POOL_SIZE", "5"))
    POOL_RECYCLE: int = int(os.getenv("POOL_RECYCLE", "3600"))
    
    # 缓存配置 (Streamlit cache TTL, 单位秒)
    CACHE_TTL: int = int(os.getenv("CACHE_TTL", "300"))  # 5分钟
    
    @property
    def DATABASE_URL(self) -> str:
        """获取数据库连接URL"""
        return (
            f"mysql+pymysql://{self.DB_USER}:{self.DB_PASSWORD}"
            f"@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"
            f"?charset={self.DB_CHARSET}"
        )


settings = Settings()
