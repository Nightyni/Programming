#!/usr/bin/env python3
"""
测试修复后的 main.py
"""
import sys
import os

# 添加src目录到Python路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

def test_gui():
    """测试GUI"""
    print("测试GUI模式...")
    try:
        from gui.main_window import ImageProcessingApp
        
        print("✅ ImageProcessingApp 导入成功")
        
        # 创建应用
        app = ImageProcessingApp()
        print("✅ ImageProcessingApp 实例化成功")
        
        # 运行应用
        app.run()
        
    except AttributeError as e:
        print(f"❌ 属性错误: {e}")
        print("这可能是由于 status_bar 初始化问题")
    except Exception as e:
        print(f"❌ 其他错误: {e}")
        import traceback
        traceback.print_exc()

def main():
    print("=" * 60)
    print("测试修复后的 main.py")
    print("=" * 60)
    test_gui()

if __name__ == "__main__":
    main()
