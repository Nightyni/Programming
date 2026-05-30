#!/usr/bin/env python3
"""
数字图像处理项目 - 环境安装脚本
"""
import subprocess
import sys
import os
from pathlib import Path

def print_header(title):
    """打印标题"""
    print("\n" + "="*60)
    print(f" {title}")
    print("="*60)

def check_python_version():
    """检查Python版本"""
    print_header("检查Python环境")
    print(f"Python版本: {sys.version}")
    print(f"Python路径: {sys.executable}")
    
    if sys.version_info < (3, 8):
        print("⚠️  建议使用Python 3.8或更高版本")
        return False
    return True

def check_venv():
    """检查是否在虚拟环境中"""
    in_venv = (hasattr(sys, 'real_prefix') or 
               (hasattr(sys, 'base_prefix') and sys.base_prefix != sys.prefix))
    
    if in_venv:
        print("✅ 已在虚拟环境中")
        print(f"虚拟环境路径: {sys.prefix}")
        return True
    else:
        print("⚠️  不在虚拟环境中")
        print("建议在虚拟环境中运行:")
        print("    python -m venv venv")
        print("    .\\venv\\Scripts\\Activate.ps1")
        return False

def install_requirements():
    """安装依赖包"""
    print_header("安装项目依赖")
    
    # 升级pip
    print("升级pip...")
    subprocess.run([sys.executable, "-m", "pip", "install", "--upgrade", "pip"], 
                   capture_output=True, text=True)
    
    # 检查requirements.txt
    req_file = Path("requirements.txt")
    if not req_file.exists():
        print("❌ requirements.txt 不存在")
        return False
    
    print(f"从 requirements.txt 安装依赖...")
    result = subprocess.run(
        [sys.executable, "-m", "pip", "install", "-r", "requirements.txt"],
        capture_output=True,
        text=True,
        encoding='utf-8'
    )
    
    if result.returncode == 0:
        print("✅ 依赖安装成功")
        return True
    else:
        print("❌ 依赖安装失败")
        print(f"错误信息: {result.stderr}")
        return False

def verify_installation():
    """验证安装结果"""
    print_header("验证安装")
    
    test_cases = [
        ("numpy", "import numpy as np"),
        ("OpenCV", "import cv2"),
        ("Matplotlib", "import matplotlib.pyplot as plt"),
        ("scikit-image", "from skimage import io"),
        ("Pillow", "from PIL import Image"),
        ("SciPy", "import scipy"),
        ("PyWavelets", "import pywt")
    ]
    
    all_passed = True
    for name, import_stmt in test_cases:
        try:
            exec(import_stmt)
            print(f"  ✅ {name:15} - 导入成功")
        except ImportError as e:
            print(f"  ❌ {name:15} - 导入失败: {str(e)[:50]}")
            all_passed = False
    
    return all_passed

def create_project_structure():
    """创建项目文件结构"""
    print_header("创建项目结构")
    
    directories = [
        "src/gui",
        "configs",
        "utils",
        "examples",
        "docs",
        "output",
        "data"
    ]
    
    for dir_path in directories:
        Path(dir_path).mkdir(exist_ok=True)
        print(f"  创建目录: {dir_path}")
    
    print("✅ 项目结构创建完成")

def main():
    """主函数"""
    print_header("数字图像处理算法仿真系统 - 环境配置")
    
    # 检查环境
    check_python_version()
    
    if not check_venv():
        response = input("\n是否继续安装？(y/n): ")
        if response.lower() != 'y':
            print("安装中止")
            return
    
    # 创建项目结构
    create_project_structure()
    
    # 安装依赖
    if not install_requirements():
        print("❌ 依赖安装失败，请手动安装")
        return
    
    # 验证安装
    if verify_installation():
        print_header("🎉 环境配置完成")
        print("现在可以运行以下命令:")
        print("  1. 图形界面: python main.py --gui")
        print("  2. 命令行模式: python main.py --cmd")
        print("  3. 算法测试: python examples/test_algorithms.py")
    else:
        print_header("⚠️  环境配置存在问题")
        print("部分依赖安装失败，请检查网络连接或手动安装")

if __name__ == "__main__":
    main()