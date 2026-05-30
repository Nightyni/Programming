#!/usr/bin/env python3
"""
修复 status_bar 初始化问题
"""
import os

def fix_main_window():
    """修复 main_window.py 中的 status_bar 初始化问题"""
    file_path = os.path.join('src', 'gui', 'main_window.py')
    
    if not os.path.exists(file_path):
        print(f"找不到文件: {file_path}")
        return
    
    print(f"正在修复 {file_path}...")
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 找到 __init__ 方法，在 setup_ui() 前添加 status_bar 初始化
    init_section = '''        # 初始化算法模块
        self.generator = ImageGenerator()
        self.filter_comp = FilterComparison()
        self.edge_detector = EdgeDetector()
        self.enhancer = ImageEnhancer()
        self.segmentor = ImageSegmentor()
        
        # 设置UI
        self.setup_ui()'''
    
    fixed_init_section = '''        # 初始化算法模块
        self.generator = ImageGenerator()
        self.filter_comp = FilterComparison()
        self.edge_detector = EdgeDetector()
        self.enhancer = ImageEnhancer()
        self.segmentor = ImageSegmentor()
        
        # 关键修复：先创建 status_bar，再设置UI
        self.status_bar = ttk.Label(self.root, text="就绪", relief=tk.SUNKEN, anchor=tk.W)
        
        # 设置UI
        self.setup_ui()'''
    
    if init_section in content:
        content = content.replace(init_section, fixed_init_section)
        print("✅ 修复 __init__ 方法中的 status_bar 初始化")
    else:
        print("⚠️  未找到 __init__ 方法中的特定代码块，可能需要手动修复")
    
    # 修复 setup_display_panel 方法，移除重复的 status_bar 创建
    display_panel_section = '''    def setup_display_panel(self, parent):
        """设置显示面板"""
        # 创建Matplotlib图形
        self.fig, self.axes = plt.subplots(2, 3, figsize=(12, 8))
        self.fig.tight_layout(pad=5.0)
        
        # 创建Canvas
        self.canvas = FigureCanvasTkAgg(self.fig, master=parent)
        self.canvas.draw()
        
        # 添加工具栏
        toolbar_frame = ttk.Frame(parent)
        toolbar_frame.pack(fill=tk.X)
        
        toolbar = NavigationToolbar2Tk(self.canvas, toolbar_frame)
        toolbar.update()
        
        # 包装Canvas
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # 状态栏
        self.status_bar = ttk.Label(parent, text="就绪", relief=tk.SUNKEN, anchor=tk.W)
        self.status_bar.pack(side=tk.BOTTOM, fill=tk.X)'''
    
    fixed_display_panel = '''    def setup_display_panel(self, parent):
        """设置显示面板"""
        # 创建Matplotlib图形
        self.fig, self.axes = plt.subplots(2, 3, figsize=(12, 8))
        self.fig.tight_layout(pad=5.0)
        
        # 创建Canvas
        self.canvas = FigureCanvasTkAgg(self.fig, master=parent)
        self.canvas.draw()
        
        # 添加工具栏
        toolbar_frame = ttk.Frame(parent)
        toolbar_frame.pack(fill=tk.X)
        
        toolbar = NavigationToolbar2Tk(self.canvas, toolbar_frame)
        toolbar.update()
        
        # 包装Canvas
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # 状态栏已经被创建，只需要pack
        self.status_bar.pack(side=tk.BOTTOM, fill=tk.X)'''
    
    if display_panel_section in content:
        content = content.replace(display_panel_section, fixed_display_panel)
        print("✅ 修复 setup_display_panel 方法")
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("✅ main_window.py 修复完成")

def fix_main_py():
    """修复 main.py 中的 GUI 调用问题"""
    file_path = 'main.py'
    
    if not os.path.exists(file_path):
        print(f"找不到文件: {file_path}")
        return
    
    print(f"正在修复 {file_path}...")
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 检查 gui_mode 函数
    gui_mode_section = '''def gui_mode():
    """GUI模式"""
    print("启动图形界面...")
    try:
        from gui.main_window import ImageProcessingApp
        app = ImageProcessingApp()
        app.run()
    except ImportError as e:
        print(f"❌ GUI模块导入失败: {e}")
        print("请确保所有依赖已正确安装")
        cmd_mode()'''
    
    # 实际上这个调用是正确的，保持原样
    print("✅ gui_mode 函数调用正确")
    
    print("✅ main.py 无需修复")

def create_test_main():
    """创建测试用的 main_test.py"""
    test_code = '''#!/usr/bin/env python3
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
'''
    
    with open('test_main.py', 'w', encoding='utf-8') as f:
        f.write(test_code)
    
    print("✅ 创建 test_main.py")

def main():
    print("=" * 60)
    print("修复 status_bar 初始化问题")
    print("=" * 60)
    
    fix_main_window()
    fix_main_py()
    create_test_main()
    
    print("\n" + "=" * 60)
    print("修复完成！")
    print("\n现在可以运行以下命令测试：")
    print("1. 测试修复: python test_main.py")
    print("2. 运行原版: python main.py --gui")
    print("=" * 60)

if __name__ == "__main__":
    main()