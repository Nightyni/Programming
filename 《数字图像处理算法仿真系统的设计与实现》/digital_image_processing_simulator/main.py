#!/usr/bin/env python3
"""
数字图像处理算法仿真系统 - 主程序入口
支持GUI模式和命令行模式
"""
import sys
import os
import argparse
# === 在导入任何matplotlib相关模块之前，先设置中文字体 ===
import matplotlib
matplotlib.use('TkAgg')  # 必须放在导入pyplot之前

# 设置中文字体
def setup_chinese_font():
    import matplotlib.font_manager as fm
    import platform
    
    # 设置matplotlib参数
    matplotlib.rcParams['axes.unicode_minus'] = False
    
    # 根据系统选择字体
    system = platform.system()
    if system == "Windows":
        font_candidates = ['Microsoft YaHei', 'SimHei', 'SimSun']
    elif system == "Darwin":  # macOS
        font_candidates = ['Arial Unicode MS', 'Hiragino Sans GB']
    else:  # Linux
        font_candidates = ['DejaVu Sans', 'WenQuanYi Zen Hei']
    
    # 查找可用字体
    available_fonts = set([f.name for f in fm.fontManager.ttflist])
    for font in font_candidates:
        if font in available_fonts:
            matplotlib.rcParams['font.sans-serif'] = [font]
            matplotlib.rcParams['font.family'] = 'sans-serif'
            print(f"✅ 已设置中文字体: {font}")
            return
    
    print("⚠️  未找到中文字体，使用默认字体")

# 调用字体设置
setup_chinese_font()

# ... 后续代码保持不变 ...
# 添加src目录到Python路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

def setup_argparse():
    """设置命令行参数解析"""
    parser = argparse.ArgumentParser(
        description='数字图像处理算法仿真系统',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
使用示例:
  python main.py --gui           # 启动图形界面
  python main.py --cmd           # 命令行演示模式
  python main.py --test          # 运行算法测试
  python main.py --demo filter   # 演示滤波算法
        """
    )
    
    parser.add_argument('--gui', action='store_true', help='启动图形界面模式')
    parser.add_argument('--cmd', action='store_true', help='启动命令行演示模式')
    parser.add_argument('--test', action='store_true', help='运行算法测试')
    parser.add_argument('--demo', type=str, choices=['filter', 'edge', 'enhance', 'segment'], 
                       help='演示特定算法')
    parser.add_argument('--output', type=str, default='output', 
                       help='输出目录路径 (默认: output)')
    
    return parser

def gui_mode():
    """GUI模式"""
    print("启动图形界面...")
    try:
        from gui.main_window import ImageProcessingApp
        app = ImageProcessingApp()
        app.run()
    except ImportError as e:
        print(f"❌ GUI模块导入失败: {e}")
        print("请确保所有依赖已正确安装")
        cmd_mode()

def cmd_mode():
    """命令行模式"""
    print("=" * 60)
    print("数字图像处理算法仿真系统 - 命令行模式")
    print("=" * 60)
    
    try:
        # 导入核心模块
        from image_generator import ImageGenerator
        from filters import FilterComparison
        from edge_detectors import EdgeDetector
        from enhancement import ImageEnhancer
        
        print("✅ 核心模块导入成功")
        
        # 创建实例
        generator = ImageGenerator()
        filter_comp = FilterComparison()
        edge_detector = EdgeDetector()
        enhancer = ImageEnhancer()
        
        # 演示流程
        print("\n1. 生成测试图像...")
        test_images = generator.generate_all_test_images()
        test_img = test_images['chessboard']
        print(f"   图像尺寸: {test_img.shape}")
        
        print("\n2. 添加噪声...")
        noisy = generator.add_gaussian_noise(test_img)
        print(f"   已添加高斯噪声 (σ=25)")
        
        print("\n3. 滤波处理...")
        filtered_results = filter_comp.apply_all_filters(noisy) if filter_comp else {"原始图像": noisy}
        print(f"   已应用 {len(filtered_results)} 种滤波算法")
        
        print("\n4. 边缘检测...")
        edge_results = edge_detector.detect_all_edges(test_img)
        print(f"   已应用 {len(edge_results)} 种边缘检测算法")
        
        print("\n5. 图像增强...")
        enhance_results = enhancer.apply_all_enhancements(test_img)
        print(f"   已应用 {len(enhance_results)} 种图像增强算法")
        
        print("\n" + "=" * 60)
        print("🎉 命令行演示完成!")
        print(f"共演示了 {len(filtered_results) + len(edge_results) + len(enhance_results)} 种算法")
        print("=" * 60)
        
    except ImportError as e:
        print(f"❌ 模块导入失败: {e}")
        print("\n请运行以下命令安装依赖:")
        print("  1. 激活虚拟环境: .\\venv\\Scripts\\Activate.ps1")
        print("  2. 运行安装脚本: python setup.py")

def test_mode():
    """测试模式"""
    print("运行算法测试...")
    try:
        import examples.test_algorithms as test
        test.test_all_algorithms()
    except ImportError as e:
        print(f"测试模块导入失败: {e}")

def demo_mode(algorithm):
    """特定算法演示"""
    print(f"演示 {algorithm} 算法...")
    # 这里可以添加特定算法的演示代码
    pass

def main():
    """主函数"""
    parser = setup_argparse()
    args = parser.parse_args()
    
    # 确保输出目录存在
    os.makedirs(args.output, exist_ok=True)
    
    # 根据参数选择模式
    if args.gui:
        gui_mode()
    elif args.cmd:
        cmd_mode()
    elif args.test:
        test_mode()
    elif args.demo:
        demo_mode(args.demo)
    else:
        # 默认显示帮助信息
        parser.print_help()
        print("\n提示: 使用 --gui 启动图形界面")

if __name__ == "__main__":
    main()


