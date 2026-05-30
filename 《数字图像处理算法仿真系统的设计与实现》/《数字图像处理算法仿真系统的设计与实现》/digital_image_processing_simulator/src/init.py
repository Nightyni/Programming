"""
数字图像处理算法仿真系统
作者: 湖南工商大学 计算机学院
版本: 1.0.0
"""

__version__ = "1.0.0"
__author__ = "湖南工商大学 计算机学院"
__description__ = "数字图像处理核心算法库"

# 导出核心类
__all__ = [
    'ImageGenerator',
    'NoiseGenerator',
    'FilterComparison',
    'EdgeDetector',
    'ImageEnhancer',
    'ImageSegmentor',
    'MetricsCalculator'
]

# 包初始化代码
print(f"加载数字图像处理算法库 v{__version__}")