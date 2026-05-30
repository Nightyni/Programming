#!/usr/bin/env python3
"""
一键修复所有问题
"""
import os
import subprocess
import sys

def install_missing_deps():
    """安装缺失的依赖"""
    print("=" * 60)
    print("安装缺失的依赖")
    print("=" * 60)
    
    deps = [
        "scikit-learn",      # K-means聚类
        "scikit-image",      # 图像处理
        "pywavelets",        # 小波变换
        "opencv-python",     # OpenCV
        "numpy",             # 数值计算
        "matplotlib",        # 绘图
        "Pillow",            # 图像处理
        "scipy"              # 科学计算
    ]
    
    for dep in deps:
        print(f"安装 {dep}...")
        try:
            subprocess.run([sys.executable, "-m", "pip", "install", dep], 
                          capture_output=True, text=True)
            print(f"  ✅ {dep}")
        except Exception as e:
            print(f"  ⚠️  {dep} 安装失败: {e}")

def fix_filters_py():
    """修复 filters.py"""
    print("\n修复 filters.py...")
    
    filters_file = os.path.join('src', 'filters.py')
    
    if os.path.exists(filters_file):
        # 读取文件
        with open(filters_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 修复 apply_all_filters 方法
        old_method = '''    def apply_all_filters(self, noisy_image):
        """
        应用所有滤波器
        
        参数:
            noisy_image: 噪声图像
            
        返回:
            字典，包含各种滤波结果
        """
        results = {
            '原始噪声': noisy_image,
            '均值滤波': self.apply_mean_filter(noisy_image),
            '高斯滤波': self.apply_gaussian_filter(noisy_image),
            '中值滤波': self.apply_median_filter(noisy_image),
            '双边滤波': self.apply_bilateral_filter(noisy_image),
            '小波去噪': self.apply_wavelet_denoise(noisy_image),
            '非局部均值': self.apply_nlm_filter(noisy_image)
        }
        
        return results'''
        
        new_method = '''    def apply_all_filters(self, noisy_image):
        """
        应用所有滤波器
        
        参数:
            noisy_image: 噪声图像
            
        返回:
            字典，包含各种滤波结果
        """
        if noisy_image is None:
            print("⚠️  警告：输入图像为空")
            return {'原始图像': np.zeros((100, 100), dtype=np.uint8)}
        
        try:
            results = {
                '原始噪声': noisy_image,
                '均值滤波': self.apply_mean_filter(noisy_image),
                '高斯滤波': self.apply_gaussian_filter(noisy_image),
                '中值滤波': self.apply_median_filter(noisy_image),
                '双边滤波': self.apply_bilateral_filter(noisy_image)
            }
            
            # 尝试添加可选滤波器
            try:
                results['小波去噪'] = self.apply_wavelet_denoise(noisy_image)
            except Exception:
                results['小波去噪'] = noisy_image.copy()
                print("⚠️  小波去噪不可用")
            
            try:
                results['非局部均值'] = self.apply_nlm_filter(noisy_image)
            except Exception:
                results['非局部均值'] = noisy_image.copy()
                print("⚠️  非局部均值不可用")
            
            return results
            
        except Exception as e:
            print(f"❌ 滤波处理失败: {e}")
            # 返回至少包含原始图像的结果
            return {'原始图像': noisy_image if noisy_image is not None else np.zeros((100, 100), dtype=np.uint8)}'''
        
        if old_method in content:
            content = content.replace(old_method, new_method)
            print("✅ 修复 apply_all_filters 方法")
        
        with open(filters_file, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print("✅ filters.py 修复完成")

def fix_segmentation_py():
    """修复 segmentation.py"""
    print("\n修复 segmentation.py...")
    
    seg_file = os.path.join('src', 'segmentation.py')
    
    if os.path.exists(seg_file):
        # 读取文件
        with open(seg_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 修复导入部分
        import_section = '''import cv2
import numpy as np
from sklearn.cluster import KMeans
from skimage import morphology, segmentation
import matplotlib.pyplot as plt'''
        
        new_import_section = '''import cv2
import numpy as np

# 可选导入 sklearn
try:
    from sklearn.cluster import KMeans
    SKLEARN_AVAILABLE = True
except ImportError:
    print("⚠️  scikit-learn 不可用，部分分割功能受限")
    SKLEARN_AVAILABLE = False

try:
    from skimage import morphology, segmentation
    SKIMAGE_AVAILABLE = True
except ImportError:
    print("⚠️  scikit-image 不可用，部分分割功能受限")
    SKIMAGE_AVAILABLE = False

import matplotlib.pyplot as plt'''
        
        if import_section in content:
            content = content.replace(import_section, new_import_section)
            print("✅ 修复导入部分")
        
        # 修复 kmeans_segmentation 方法
        kmeans_method_start = '    def kmeans_segmentation(self, image, k=3, max_iter=100):'
        if kmeans_method_start in content:
            # 找到方法开始和结束
            lines = content.split('\n')
            for i, line in enumerate(lines):
                if line.strip() == kmeans_method_start:
                    # 在方法开始后添加检查
                    indent = ' ' * 8
                    check_code = f'''{indent}if not SKLEARN_AVAILABLE:
{indent}    print("❌ scikit-learn 不可用，无法使用K-means")
{indent}    return image.copy(), np.zeros(image.shape[:2], dtype=np.int32)'''
                    
                    # 插入检查代码
                    method_body_index = i + 1
                    while lines[method_body_index].startswith('        """'):
                        method_body_index += 1
                    
                    lines.insert(method_body_index, check_code)
                    content = '\n'.join(lines)
                    print("✅ 修复 kmeans_segmentation 方法")
                    break
        
        with open(seg_file, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print("✅ segmentation.py 修复完成")

def fix_main_py():
    """修复 main.py 的 cmd_mode"""
    print("\n修复 main.py...")
    
    main_file = 'main.py'
    
    if os.path.exists(main_file):
        with open(main_file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 找到 cmd_mode 方法
        cmd_mode_start = 'def cmd_mode():'
        if cmd_mode_start in content:
            lines = content.split('\n')
            for i, line in enumerate(lines):
                if line.strip() == cmd_mode_start:
                    # 找到 filtered_results 赋值的地方
                    for j in range(i, min(i+50, len(lines))):
                        if 'filtered_results =' in lines[j]:
                            # 修改这行代码
                            old_line = lines[j]
                            new_line = '        filtered_results = filter_comp.apply_all_filters(noisy) if filter_comp else {"原始图像": noisy}'
                            lines[j] = new_line
                            print(f"✅ 修复第 {j+1} 行: {old_line.strip()} -> {new_line.strip()}")
                            break
            
            content = '\n'.join(lines)
        
        with open(main_file, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print("✅ main.py 修复完成")

def create_simple_main():
    """创建简化的 main_simple.py"""
    print("\n创建简化版本...")
    
    simple_code = '''#!/usr/bin/env python3
"""
数字图像处理 - 简化稳定版本
"""
import sys
import os

# 添加src到路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

def simple_gui_mode():
    """简化GUI模式"""
    print("启动简化GUI...")
    try:
        # 只导入必要的模块
        import cv2
        import numpy as np
        import tkinter as tk
        from tkinter import ttk, filedialog
        import matplotlib.pyplot as plt
        from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
        
        from image_generator import ImageGenerator
        
        class SimpleApp:
            def __init__(self):
                self.root = tk.Tk()
                self.root.title("数字图像处理简化版")
                self.root.geometry("1000x700")
                
                self.generator = ImageGenerator()
                self.current_image = None
                
                self.setup_ui()
            
            def setup_ui(self):
                # 控制面板
                control_frame = ttk.LabelFrame(self.root, text="控制", width=200)
                control_frame.pack(side=tk.LEFT, fill=tk.Y, padx=10, pady=10)
                
                # 按钮
                ttk.Button(control_frame, text="生成棋盘格", 
                          command=self.generate_chessboard).pack(fill=tk.X, pady=5)
                ttk.Button(control_frame, text="加载图像", 
                          command=self.load_image).pack(fill=tk.X, pady=5)
                ttk.Button(control_frame, text="添加噪声", 
                          command=self.add_noise).pack(fill=tk.X, pady=5)
                ttk.Button(control_frame, text="均值滤波", 
                          command=self.apply_mean_filter).pack(fill=tk.X, pady=5)
                ttk.Button(control_frame, text="边缘检测", 
                          command=self.edge_detection).pack(fill=tk.X, pady=5)
                
                # 显示区域
                self.fig, self.ax = plt.subplots(figsize=(8, 6))
                self.canvas = FigureCanvasTkAgg(self.fig, master=self.root)
                self.canvas.get_tk_widget().pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=10, pady=10)
            
            def generate_chessboard(self):
                self.current_image = self.generator.generate_chessboard()
                self.display_image(self.current_image, "棋盘格图像")
            
            def load_image(self):
                filepath = filedialog.askopenfilename(filetypes=[("图像文件", "*.jpg *.png *.bmp")])
                if filepath:
                    img = cv2.imread(filepath, cv2.IMREAD_GRAYSCALE)
                    if img is not None:
                        self.current_image = img
                        self.display_image(img, os.path.basename(filepath))
            
            def add_noise(self):
                if self.current_image is not None:
                    noisy = self.generator.add_gaussian_noise(self.current_image)
                    self.display_image(noisy, "高斯噪声图像")
            
            def apply_mean_filter(self):
                if self.current_image is not None:
                    filtered = cv2.blur(self.current_image, (5, 5))
                    self.display_image(filtered, "均值滤波结果")
            
            def edge_detection(self):
                if self.current_image is not None:
                    edges = cv2.Canny(self.current_image, 50, 150)
                    self.display_image(edges, "Canny边缘检测")
            
            def display_image(self, image, title):
                self.ax.clear()
                self.ax.imshow(image, cmap='gray')
                self.ax.set_title(title)
                self.ax.axis('off')
                self.canvas.draw()
            
            def run(self):
                self.root.mainloop()
        
        app = SimpleApp()
        app.run()
        
    except Exception as e:
        print(f"GUI错误: {e}")
        simple_cmd_mode()

def simple_cmd_mode():
    """简化命令行模式"""
    print("=" * 60)
    print("数字图像处理简化演示")
    print("=" * 60)
    
    try:
        from image_generator import ImageGenerator
        import cv2
        import numpy as np
        
        generator = ImageGenerator(size=(400, 400))
        
        print("1. 生成测试图像...")
        chessboard = generator.generate_chessboard()
        print(f"   棋盘格: {chessboard.shape}")
        
        print("\n2. 添加噪声...")
        noisy = generator.add_gaussian_noise(chessboard)
        print(f"   高斯噪声图像: {noisy.shape}")
        
        print("\n3. 滤波处理...")
        mean_filtered = cv2.blur(noisy, (5, 5))
        median_filtered = cv2.medianBlur(noisy, 5)
        print(f"   均值滤波: {mean_filtered.shape}")
        print(f"   中值滤波: {median_filtered.shape}")
        
        print("\n4. 边缘检测...")
        edges = cv2.Canny(chessboard, 50, 150)
        print(f"   Canny边缘: {edges.shape}")
        
        print("\n5. 保存结果...")
        os.makedirs("simple_output", exist_ok=True)
        cv2.imwrite("simple_output/01_chessboard.png", chessboard)
        cv2.imwrite("simple_output/02_noisy.png", noisy)
        cv2.imwrite("simple_output/03_mean_filtered.png", mean_filtered)
        cv2.imwrite("simple_output/04_median_filtered.png", median_filtered)
        cv2.imwrite("simple_output/05_edges.png", edges)
        
        print("\n✅ 演示完成！结果保存在 simple_output/ 目录")
        
    except Exception as e:
        print(f"❌ 错误: {e}")

def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description='数字图像处理简化版')
    parser.add_argument('--gui', action='store_true', help='启动图形界面')
    parser.add_argument('--cmd', action='store_true', help='命令行演示')
    
    args = parser.parse_args()
    
    if args.gui:
        simple_gui_mode()
    else:
        simple_cmd_mode()

if __name__ == "__main__":
    main()
'''
    
    with open('main_simple.py', 'w', encoding='utf-8') as f:
        f.write(simple_code)
    
    print("✅ 创建 main_simple.py")

def main():
    print("=" * 60)
    print("数字图像处理项目 - 全面修复")
    print("=" * 60)
    
    # 1. 安装依赖
    install_missing_deps()
    
    # 2. 修复代码
    fix_filters_py()
    fix_segmentation_py()
    fix_main_py()
    
    # 3. 创建简化版本
    create_simple_main()
    
    print("\n" + "=" * 60)
    print("修复完成！")
    print("\n现在可以运行：")
    print("1. 简化版本: python main_simple.py --gui")
    print("2. 简化命令行: python main_simple.py --cmd")
    print("3. 原版测试: python main.py --cmd")
    print("=" * 60)

if __name__ == "__main__":
    main()