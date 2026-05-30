#!/usr/bin/env python3

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
        
        print("2. 添加噪声...")
        noisy = generator.add_gaussian_noise(chessboard)
        print(f"   高斯噪声图像: {noisy.shape}")
        
        print("3. 滤波处理...")
        mean_filtered = cv2.blur(noisy, (5, 5))
        median_filtered = cv2.medianBlur(noisy, 5)
        print(f"   均值滤波: {mean_filtered.shape}")
        print(f"   中值滤波: {median_filtered.shape}")
        
        print("4. 边缘检测...")
        edges = cv2.Canny(chessboard, 50, 150)
        print(f"   Canny边缘: {edges.shape}")
        
        print("5. 保存结果...")
        os.makedirs("simple_output", exist_ok=True)
        cv2.imwrite("simple_output/01_chessboard.png", chessboard)
        cv2.imwrite("simple_output/02_noisy.png", noisy)
        cv2.imwrite("simple_output/03_mean_filtered.png", mean_filtered)
        cv2.imwrite("simple_output/04_median_filtered.png", median_filtered)
        cv2.imwrite("simple_output/05_edges.png", edges)
        
        print("✅ 演示完成！结果保存在 simple_output/ 目录")
        
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
