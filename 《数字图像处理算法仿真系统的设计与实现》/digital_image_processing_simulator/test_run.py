#!/usr/bin/env python3
"""
测试项目是否能正常运行
"""
import sys
import traceback

print("=" * 60)
print("数字图像处理算法仿真系统 - 运行测试")
print("=" * 60)

# 测试导入各模块
modules_to_test = [
    ("numpy", "import numpy as np"),
    ("OpenCV", "import cv2"),
    ("Matplotlib", "import matplotlib.pyplot as plt"),
    ("scikit-image", "from skimage import data, io"),
    ("Pillow", "from PIL import Image"),
    ("SciPy", "import scipy"),
    ("PyWavelets", "import pywt"),
]

print("\n1. 测试模块导入...")
for module_name, import_stmt in modules_to_test:
    try:
        exec(import_stmt)
        print(f"  ✅ {module_name:15} - 导入成功")
    except ImportError as e:
        print(f"  ❌ {module_name:15} - 导入失败: {e}")

# 测试项目模块
print("\n2. 测试项目模块...")
try:
    from src.image_generator import ImageGenerator
    print("  ✅ ImageGenerator - 导入成功")
    
    from src.filters import FilterComparison
    print("  ✅ FilterComparison - 导入成功")
    
    from src.edge_detectors import EdgeDetector
    print("  ✅ EdgeDetector - 导入成功")
    
    from src.enhancement import ImageEnhancer
    print("  ✅ ImageEnhancer - 导入成功")
    
    print("  ✅ 所有核心模块导入成功")
    
except Exception as e:
    print(f"  ❌ 模块导入失败: {e}")
    traceback.print_exc()

# 简单功能测试
print("\n3. 简单功能测试...")
try:
    # 测试图像生成
    generator = ImageGenerator()
    test_image = generator.generate_chessboard()
    print(f"  ✅ 图像生成成功 - 尺寸: {test_image.shape}")
    
    # 测试加噪
    noisy = generator.add_gaussian_noise(test_image)
    print(f"  ✅ 噪声添加成功 - 尺寸: {noisy.shape}")
    
    # 测试滤波
    filter_comp = FilterComparison()
    filtered = filter_comp.apply_mean_filter(noisy)
    print(f"  ✅ 滤波处理成功 - 尺寸: {filtered.shape}")
    
    print("\n🎉 所有测试通过！项目可以正常运行。")
    print("\n现在可以运行主程序:")
    print("  python main.py --gui      # 启动图形界面")
    print("  python main.py --cmd      # 命令行模式")
    print("  python examples/test_algorithms.py  # 测试所有算法")
    
except Exception as e:
    print(f"  ❌ 功能测试失败: {e}")
    traceback.print_exc()

print("\n" + "=" * 60)