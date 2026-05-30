import tkinter as tk
from tkinter import ttk, filedialog, messagebox
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
import numpy as np
import cv2
import os
import sys
import time
from pathlib import Path
from datetime import datetime

# 添加项目根目录到路径，以便导入 utils
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '../..'))

# 导入项目模块
try:
    from src.image_generator import ImageGenerator
    from src.filters import FilterComparison
    from src.edge_detectors import EdgeDetector
    from src.enhancement import ImageEnhancer
    from src.segmentation import ImageSegmentor
    # 导入文件工具
    from utils.file_utils import safe_load_image, check_image_file
except ImportError as e:
    print(f"导入模块失败: {e}")
    # 定义简单的替代函数
    def safe_load_image(file_path, max_size_mb=50, max_dimension=4000):
        img = cv2.imread(file_path, cv2.IMREAD_COLOR)
        if img is None:
            return None, "cv2.imread无法读取图像"
        return img, None
    
    def check_image_file(file_path):
        if not os.path.exists(file_path):
            return False, "文件不存在"
        return True, "文件存在"


class ImageProcessingApp:
    """图像处理算法仿真系统 - 主应用程序"""
    
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("数字图像处理算法仿真系统 v1.0 - 湖南工商大学")
        self.root.geometry("1400x900")
        
        # 初始化变量
        self.current_image = None
        self.original_image = None
        self.processed_results = {}
        self.image_history = []  # 图像处理历史
        
        # 初始化算法模块
        self.generator = ImageGenerator()
        self.filter_comp = FilterComparison()
        self.edge_detector = EdgeDetector()
        self.enhancer = ImageEnhancer()
        self.segmentor = ImageSegmentor()
        
        # 关键修复：先创建 status_bar，再设置UI
        self.status_bar = ttk.Label(self.root, text="就绪", relief=tk.SUNKEN, anchor=tk.W)
        
        # 设置UI
        self.setup_ui()
        
        # 设置窗口关闭事件
        self.root.protocol("WM_DELETE_WINDOW", self.on_closing)
        
    def setup_ui(self):
        """设置用户界面"""
        # 创建主框架
        main_frame = ttk.Frame(self.root)
        main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # 左侧控制面板
        control_frame = ttk.LabelFrame(main_frame, text="控制面板", width=350)
        control_frame.pack(side=tk.LEFT, fill=tk.Y, padx=(0, 10))
        
        # 右侧显示面板
        display_frame = ttk.LabelFrame(main_frame, text="结果展示", width=950)
        display_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)
        
        # 设置控制面板内容
        self.setup_control_panel(control_frame)
        
        # 设置显示面板
        self.setup_display_panel(display_frame)
        
    def setup_control_panel(self, parent):
        """设置控制面板"""
        # 创建滚动条容器
        canvas_frame = ttk.Frame(parent)
        canvas_frame.pack(fill=tk.BOTH, expand=True)
        
        # 创建Canvas和滚动条
        canvas = tk.Canvas(canvas_frame)
        scrollbar = ttk.Scrollbar(canvas_frame, orient="vertical", command=canvas.yview)
        scrollable_frame = ttk.Frame(canvas)
        
        scrollable_frame.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )
        
        canvas.create_window((0, 0), window=scrollable_frame, anchor="nw")
        canvas.configure(yscrollcommand=scrollbar.set)
        
        canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")
        
        # 1. 图像生成部分
        img_gen_frame = ttk.LabelFrame(scrollable_frame, text="1. 图像生成", padding=10)
        img_gen_frame.pack(fill=tk.X, padx=5, pady=5)
        
        ttk.Label(img_gen_frame, text="选择图像类型:").pack(anchor=tk.W, pady=(0,5))
        
        self.image_type = tk.StringVar(value="chessboard")
        image_types = [
            ("棋盘格图像", "chessboard"),
            ("同心圆图像", "circles"),
            ("渐变图像", "gradient"),
            ("随机形状", "random_shapes"),
            ("测试图案", "test_pattern")
        ]
        
        for text, value in image_types:
            ttk.Radiobutton(img_gen_frame, text=text, variable=self.image_type, 
                          value=value).pack(anchor=tk.W, padx=20)
        
        # 加载外部图像按钮
        ttk.Button(img_gen_frame, text="📁 加载外部图像", 
                  command=self.load_external_image,
                  style="Accent.TButton").pack(pady=10, fill=tk.X)
        
        # 2. 噪声添加部分
        noise_frame = ttk.LabelFrame(scrollable_frame, text="2. 添加噪声", padding=10)
        noise_frame.pack(fill=tk.X, padx=5, pady=5)
        
        self.noise_type = tk.StringVar(value="gaussian")
        noise_types = [
            ("无噪声", "none"),
            ("高斯噪声", "gaussian"),
            ("椒盐噪声", "salt_pepper"),
            ("斑点噪声", "speckle"),
            ("泊松噪声", "poisson")
        ]
        
        for text, value in noise_types:
            ttk.Radiobutton(noise_frame, text=text, variable=self.noise_type,
                          value=value).pack(anchor=tk.W, padx=20)
        
        # 噪声强度控制
        ttk.Label(noise_frame, text="噪声强度:").pack(anchor=tk.W, pady=(10,0))
        self.noise_intensity = tk.DoubleVar(value=0.05)
        intensity_scale = ttk.Scale(noise_frame, from_=0.01, to=0.2, 
                                   variable=self.noise_intensity,
                                   orient=tk.HORIZONTAL)
        intensity_scale.pack(fill=tk.X, pady=5)
        
        # 3. 算法选择部分
        algo_frame = ttk.LabelFrame(scrollable_frame, text="3. 处理算法", padding=10)
        algo_frame.pack(fill=tk.X, padx=5, pady=5)
        
        self.algorithm = tk.StringVar(value="filter")
        algorithms = [
            ("滤波去噪", "filter"),
            ("边缘检测", "edge"),
            ("图像增强", "enhance"),
            ("图像分割", "segment")
        ]
        
        for text, value in algorithms:
            ttk.Radiobutton(algo_frame, text=text, variable=self.algorithm,
                          value=value).pack(anchor=tk.W, padx=20)
        
        # 4. 参数调整部分
        param_frame = ttk.LabelFrame(scrollable_frame, text="4. 算法参数", padding=10)
        param_frame.pack(fill=tk.X, padx=5, pady=5)
        
        # 创建不同算法的参数面板
        self.param_panels = {}
        
        # 滤波参数面板
        filter_panel = ttk.Frame(param_frame)
        ttk.Label(filter_panel, text="滤波器大小:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.filter_size = tk.IntVar(value=5)
        filter_scale = ttk.Scale(filter_panel, from_=3, to=15, variable=self.filter_size,
                               orient=tk.HORIZONTAL, length=150)
        filter_scale.grid(row=0, column=1, sticky=tk.EW, padx=10, pady=5)
        self.param_panels['filter'] = filter_panel
        
        # 边缘检测参数面板
        edge_panel = ttk.Frame(param_frame)
        ttk.Label(edge_panel, text="Canny低阈值:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.canny_low = tk.IntVar(value=50)
        canny_low_scale = ttk.Scale(edge_panel, from_=10, to=100, variable=self.canny_low,
                                  orient=tk.HORIZONTAL, length=150)
        canny_low_scale.grid(row=0, column=1, sticky=tk.EW, padx=10, pady=5)
        
        ttk.Label(edge_panel, text="Canny高阈值:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.canny_high = tk.IntVar(value=150)
        canny_high_scale = ttk.Scale(edge_panel, from_=50, to=250, variable=self.canny_high,
                                   orient=tk.HORIZONTAL, length=150)
        canny_high_scale.grid(row=1, column=1, sticky=tk.EW, padx=10, pady=5)
        self.param_panels['edge'] = edge_panel
        
        # 图像增强参数面板
        enhance_panel = ttk.Frame(param_frame)
        ttk.Label(enhance_panel, text="伽马值:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.gamma_value = tk.DoubleVar(value=1.5)
        gamma_scale = ttk.Scale(enhance_panel, from_=0.1, to=3.0, variable=self.gamma_value,
                              orient=tk.HORIZONTAL, length=150)
        gamma_scale.grid(row=0, column=1, sticky=tk.EW, padx=10, pady=5)
        self.param_panels['enhance'] = enhance_panel
        
        # 图像分割参数面板
        segment_panel = ttk.Frame(param_frame)
        ttk.Label(segment_panel, text="聚类数量:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.cluster_count = tk.IntVar(value=3)
        cluster_spinbox = ttk.Spinbox(segment_panel, from_=2, to=8, 
                                     textvariable=self.cluster_count, width=10)
        cluster_spinbox.grid(row=0, column=1, sticky=tk.W, padx=10, pady=5)
        self.param_panels['segment'] = segment_panel
        
        # 初始显示滤波参数面板
        self.show_parameter_panel()
        
        # 5. 控制按钮部分
        btn_frame = ttk.Frame(scrollable_frame)
        btn_frame.pack(fill=tk.X, padx=5, pady=10)
        
        # 主要操作按钮
        buttons = [
            ("🚀 开始处理", self.process_image),
            ("💾 保存结果", self.save_results),
            ("📊 性能评估", self.show_metrics),
            ("↩️ 上一步", self.undo_step),
            ("🔄 重置", self.reset_all),
            ("❓ 帮助", self.show_help)
        ]
        
        for text, command in buttons:
            btn = ttk.Button(btn_frame, text=text, command=command)
            btn.pack(fill=tk.X, pady=2, ipady=5)
        
        # 绑定算法选择变化事件
        self.algorithm.trace_add('write', lambda *args: self.show_parameter_panel())
        
    def setup_display_panel(self, parent):
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
        self.status_bar.pack(side=tk.BOTTOM, fill=tk.X)
        
    def show_parameter_panel(self):
        """显示当前算法的参数面板"""
        algorithm = self.algorithm.get()
        
        # 隐藏所有参数面板
        for panel in self.param_panels.values():
            panel.pack_forget()
        
        # 显示当前算法的参数面板
        if algorithm in self.param_panels:
            self.param_panels[algorithm].pack(fill=tk.X)
            self.status_bar.config(text=f"已选择{algorithm}算法，可调整参数")
    
    def load_external_image(self):
        """加载外部图像文件"""
        file_path = filedialog.askopenfilename(
            title="选择图像文件",
            filetypes=[
                ("JPEG图像", "*.jpg *.jpeg *.JPG *.JPEG"),
                ("PNG图像", "*.png *.PNG"),
                ("BMP图像", "*.bmp *.BMP"),
                ("TIFF图像", "*.tiff *.tif *.TIFF *.TIF"),
                ("所有文件", "*.*")
            ]
        )
        
        if not file_path:
            return
        
        print(f"{'='*50}")
        print(f"尝试加载图像: {file_path}")
        
        # 首先检查文件
        can_read, check_msg = check_image_file(file_path)
        print(f"文件检查: {check_msg}")
    
        if not can_read:
            error_msg = f"无法读取图像文件:\n\n{check_msg}\n\n"
            error_msg += f"文件路径: {file_path}\n"
            error_msg += "请检查:\n"
            error_msg += "1. 文件是否存在\n"
            error_msg += "2. 文件是否损坏\n"
            error_msg += "3. 文件权限是否正确"
            messagebox.showerror("文件检查失败", error_msg)
            self.status_bar.config(text=f"文件检查失败: {check_msg}")
            return
        
        image, error_message = safe_load_image(file_path, max_size_mb=50, max_dimension=800)
        
        if image is None:
            # 提供详细的错误信息
            error_details = f"无法读取图像文件\n\n"
            error_details += f"文件名: {os.path.basename(file_path)}\n"
            error_details += f"文件路径: {file_path}\n"
            error_details += f"错误信息: {error_message}\n\n"
            error_details += "可能的原因及解决方案:\n"
            error_details += "1. 文件格式不受支持 → 请使用JPEG或PNG格式\n"
            error_details += "2. 文件已损坏 → 用其他软件检查文件\n"
            error_details += "3. 文件过大(>50MB) → 压缩或缩小图像\n"
            error_details += "4. 编码问题 → 使用图像转换工具\n\n"
            error_details += "建议: 运行 'python convert_images.py' 转换图像格式"
        
            messagebox.showerror("图像加载失败", error_details)
            self.status_bar.config(text=f"加载失败: {error_message}")
            return
        
        try:
            # 获取图像尺寸
            height, width = image.shape[:2]
            
            # 确保图像尺寸适合显示
            max_display_size = 800
            
            # 初始化尺寸变量
            new_width = width
            new_height = height
            resize_performed = False
            
            if max(height, width) > max_display_size:
                scale = max_display_size / max(height, width)
                new_width = int(width * scale)
                new_height = int(height * scale)
                image = cv2.resize(image, (new_width, new_height), 
                                 interpolation=cv2.INTER_AREA)
                resize_performed = True
                print(f"显示尺寸调整: {width}x{height} → {new_width}x{new_height}")
            else:
                print(f"显示尺寸: {width}x{height} (无需调整)")
            
            self.original_image = image
            self.current_image = image.copy()
            self.image_history = [(image.copy(), "原始图像")]
        
            # 显示原始图像
            self.display_original_image()

            # 更新状态栏
            filename = os.path.basename(file_path)
            img_size = f"{new_width}×{new_height}"
            
            if len(image.shape) == 3:
                img_size += f"×{image.shape[2]}"
            
            file_size = os.path.getsize(file_path) if os.path.exists(file_path) else 0
            if file_size < 1024:
                size_str = f"{file_size}B"
            elif file_size < 1024*1024:
                size_str = f"{file_size/1024:.1f}KB"
            else:
                size_str = f"{file_size/(1024*1024):.1f}MB"
            
            status_msg = f"已加载: {filename} ({img_size}, {size_str})"
            if resize_performed:
                status_msg += " [已调整]"
            
            self.status_bar.config(text=status_msg)
            
            print(f"✅ 成功加载图像: {filename}")
            print(f"   尺寸: {new_width}x{new_height}{'x'+str(image.shape[2]) if len(image.shape)==3 else ''}")
            print(f"   文件大小: {size_str}")
            print(f"{'='*50}")
            
        except Exception as e:
            error_msg = f"处理图像时出错:\n{str(e)}\n\n"
            error_msg += "图像可能已加载但无法处理。"
            messagebox.showerror("处理错误", error_msg)
            self.status_bar.config(text=f"处理失败: {str(e)}")
            print(f"❌ 处理图像错误: {e}")
            import traceback
            traceback.print_exc()
    
    def generate_base_image(self):
        """生成基础图像"""
        img_type = self.image_type.get()
        
        if img_type == "chessboard":
            return self.generator.generate_chessboard()
        elif img_type == "circles":
            return self.generator.generate_concentric_circles()
        elif img_type == "gradient":
            return self.generator.generate_gradient()
        elif img_type == "random_shapes":
            return self.generator.generate_random_shapes()
        elif img_type == "test_pattern":
            return self.generator.generate_test_pattern()
        else:
            return self.generator.generate_chessboard()
    
    def add_noise_to_image(self, image):
        """添加噪声到图像"""
        noise_type = self.noise_type.get()
        intensity = self.noise_intensity.get()
        
        if noise_type == "none":
            return image.copy()
        elif noise_type == "gaussian":
            return self.generator.add_gaussian_noise(image, sigma=25*intensity)
        elif noise_type == "salt_pepper":
            return self.generator.add_salt_pepper_noise(image, prob=intensity)
        elif noise_type == "speckle":
            return self.generator.add_speckle_noise(image, sigma=intensity)
        elif noise_type == "poisson":
            return self.generator.add_poisson_noise(image)
        else:
            return image.copy()
    
    def get_friendly_name(self, key):
        """将英文键名转换为友好的显示名称"""
        friendly_names = {
            # 滤波算法
            'original_noisy': 'Original Noisy',
            'mean_filter': 'Mean Filter',
            'gaussian_filter': 'Gaussian Filter',
            'median_filter': 'Median Filter',
            'bilateral_filter': 'Bilateral Filter',
            'wavelet_denoise': 'Wavelet Denoise',
            'non_local_mean': 'Non-local Means',
            'original': 'Original Image',
            
            # 边缘检测
            'sobel': 'Sobel Operator',
            'prewitt': 'Prewitt Operator',
            'laplacian': 'Laplacian Operator',
            'canny': 'Canny Edge',
            'roberts': 'Roberts Cross',
            'scharr': 'Scharr Operator',
            
            # 图像增强
            'histogram_equalization': 'Histogram Equalization',
            'adaptive_histogram': 'CLAHE',
            'gamma_correction': 'Gamma Correction',
            'log_transform': 'Log Transform',
            'contrast_stretching': 'Contrast Stretch',
            'sharpening': 'Image Sharpening',
            
            # 图像分割
            'otsu_threshold': 'Otsu Threshold',
            'adaptive_threshold': 'Adaptive Threshold',
            'kmeans_clustering': 'K-means Clustering',
            'watershed': 'Watershed',
            'region_growing': 'Region Growing',
            'mean_shift': 'Mean Shift'
        }
        
        # 动态处理一些特殊情况
        if 'K-means聚类' in key:
            # 提取K值
            import re
            match = re.search(r'K=(\d+)', key)
            if match:
                k_value = match.group(1)
                return f'K-means (K={k_value})'
        
        return friendly_names.get(key, key.replace('_', ' ').title())
    
    def apply_algorithm(self, image):
        """应用选定算法"""
        algorithm = self.algorithm.get()
        
        if algorithm == "filter":
            return self.filter_comp.apply_all_filters(image)
        elif algorithm == "edge":
            # 设置Canny参数
            low = self.canny_low.get()
            high = self.canny_high.get()
            results = self.edge_detector.detect_all_edges(image)
            
            # 将中文键名转换为英文
            english_results = {}
            chinese_to_english = {
                '原始图像': 'original',
                'Sobel算子': 'sobel',
                'Prewitt算子': 'prewitt',
                'Laplacian算子': 'laplacian',
                'Canny算子': 'canny',
                'Roberts算子': 'roberts',
                'Scharr算子': 'scharr'
            }
            
            for chinese_key, value in results.items():
                english_key = chinese_to_english.get(chinese_key, chinese_key)
                english_results[english_key] = value
            
            # 更新Canny结果使用自定义参数
            if len(image.shape) == 3:
                gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            else:
                gray = image.copy()
            
            # 确保canny键存在
            english_results['canny'] = self.edge_detector.canny_edges(gray, low, high)
            return english_results
            
        elif algorithm == "enhance":
            results = self.enhancer.apply_all_enhancements(image)
            # 将中文键名转换为英文
            english_results = {}
            chinese_to_english = {
                '直方图均衡化': 'histogram_equalization',
                '自适应直方图均衡化': 'adaptive_histogram',
                '伽马校正': 'gamma_correction',
                '对数变换': 'log_transform',
                '对比度拉伸': 'contrast_stretching',
                '图像锐化': 'sharpening'
            }
            
            for chinese_key, value in results.items():
                english_key = chinese_to_english.get(chinese_key, chinese_key)
                english_results[english_key] = value
                
            return english_results
            
        elif algorithm == "segment":
            k = self.cluster_count.get()
            segmented, _ = self.segmentor.kmeans_segmentation(image, k)
            
            # 使用英文键名
            results = {
                'original': image,
                f'kmeans_k{k}': segmented
            }
            
            # 添加其他分割结果并转换为英文
            segment_results = self.segmentor.apply_all_segmentation(image)
            chinese_to_english = {
                'Otsu阈值分割': 'otsu_threshold',
                '自适应阈值分割': 'adaptive_threshold',
                'K-means聚类': 'kmeans_clustering',
                '分水岭算法': 'watershed',
                '区域生长': 'region_growing',
                'Mean Shift分割': 'mean_shift'
            }
            
            for chinese_key, value in segment_results.items():
                english_key = chinese_to_english.get(chinese_key, chinese_key)
                results[english_key] = value
                
            return results
        else:
            return {"original": image}
    
    def process_image(self):
        """处理图像"""
        try:
            # 生成或使用当前图像
            if self.current_image is None:
                base_image = self.generate_base_image()
                self.original_image = base_image.copy()
                self.image_history = [(base_image.copy(), "生成图像")]
            else:
                base_image = self.current_image.copy()
            
            # 添加噪声
            if self.noise_type.get() != "none":
                noise_name = self.noise_type.get()
                noisy_image = self.add_noise_to_image(base_image)
                noise_names = {
                    'gaussian': '高斯噪声',
                    'salt_pepper': '椒盐噪声',
                    'speckle': '斑点噪声',
                    'poisson': '泊松噪声'
                }
                noise_desc = noise_names.get(noise_name, noise_name)
                self.image_history.append((noisy_image.copy(), f"添加{noise_desc}"))
            else:
                noisy_image = base_image
            
            # 应用算法
            results = self.apply_algorithm(noisy_image)
            self.processed_results = results
            
            # 记录处理历史
            algorithm_name = self.algorithm.get()
            algo_names = {
                'filter': '滤波',
                'edge': '边缘检测',
                'enhance': '图像增强',
                'segment': '图像分割'
            }
            
            if results:
                first_result_key = list(results.keys())[1] if len(results) > 1 else list(results.keys())[0]
                self.image_history.append((results[first_result_key], 
                                         f"应用{algo_names.get(algorithm_name, '处理')}"))
            
            # 显示结果
            self.display_results(base_image, noisy_image, results)
            
            # 更新状态栏
            self.status_bar.config(text=f"处理完成: {algo_names.get(algorithm_name, algorithm_name)}")
            
        except Exception as e:
            messagebox.showerror("处理错误", f"处理图像时出错:\n{str(e)}")
            self.status_bar.config(text=f"处理失败: {str(e)}")
    
    def display_original_image(self):
        """显示原始图像"""
        if self.original_image is not None:
            # 清除所有子图
            for ax in self.axes.flat:
                ax.clear()
                ax.axis('off')
            
            # 显示原始图像
            if len(self.original_image.shape) == 3:
                display_img = cv2.cvtColor(self.original_image, cv2.COLOR_BGR2RGB)
                self.axes[0, 0].imshow(display_img)
            else:
                self.axes[0, 0].imshow(self.original_image, cmap='gray')
            
            self.axes[0, 0].set_title("原始图像", fontsize=12, fontweight='bold')
            self.axes[0, 0].axis('on')
            
            # 添加图像信息
            height, width = self.original_image.shape[:2]
            if len(self.original_image.shape) == 3:
                channels = self.original_image.shape[2]
                info_text = f"尺寸: {width}×{height}×{channels}"
            else:
                info_text = f"尺寸: {width}×{height}"
            
            self.axes[0, 0].text(0.5, -0.1, info_text, transform=self.axes[0, 0].transAxes,
                               ha='center', fontsize=9)
            
            # 隐藏其他子图
            for i in range(1, 6):
                ax = self.axes.flatten()[i]
                ax.axis('off')
                ax.text(0.5, 0.5, "等待处理...", transform=ax.transAxes,
                       ha='center', va='center', fontsize=12, alpha=0.5)
            
            self.canvas.draw()
    
    def display_results(self, original, noisy, results):
        """显示处理结果"""
        # 清除所有子图
        for ax in self.axes.flat:
            ax.clear()
        
        # 显示原始图像
        if len(original.shape) == 3:
            display_original = cv2.cvtColor(original, cv2.COLOR_BGR2RGB)
            self.axes[0, 0].imshow(display_original)
        else:
            self.axes[0, 0].imshow(original, cmap='gray')
        self.axes[0, 0].set_title("原始图像", fontsize=10, fontweight='bold')
        self.axes[0, 0].axis('off')
        
        # 显示加噪图像
        if len(noisy.shape) == 3:
            display_noisy = cv2.cvtColor(noisy, cv2.COLOR_BGR2RGB)
            self.axes[0, 1].imshow(display_noisy)
        else:
            self.axes[0, 1].imshow(noisy, cmap='gray')
        
        noise_name = self.noise_type.get()
        noise_names = {
            'none': '无噪声',
            'gaussian': '高斯噪声',
            'salt_pepper': '椒盐噪声',
            'speckle': '斑点噪声',
            'poisson': '泊松噪声'
        }
        
        noise_title = noise_names.get(noise_name, '无噪声')
        self.axes[0, 1].set_title(f"加噪图像 ({noise_title})", 
                                 fontsize=10, fontweight='bold')
        self.axes[0, 1].axis('off')
        
        # 显示处理结果（最多4个）
        result_items = list(results.items())[:4]
        positions = [(0, 2), (1, 0), (1, 1), (1, 2)]
        
        for idx, (key, result_img) in enumerate(result_items):
            row, col = positions[idx]
            
            # 将英文键名转换为友好的显示名称
            display_title = self.get_friendly_name(key)
            
            if len(result_img.shape) == 3:
                display_result = cv2.cvtColor(result_img, cv2.COLOR_BGR2RGB)
                self.axes[row, col].imshow(display_result)
            else:
                self.axes[row, col].imshow(result_img, cmap='gray')
            
            self.axes[row, col].set_title(display_title, fontsize=10, fontweight='bold')
            self.axes[row, col].axis('off')
        
        # 如果有空位，显示算法说明
        if len(result_items) < 4:
            empty_pos = positions[len(result_items)]
            ax = self.axes[empty_pos]
            algorithm = self.algorithm.get()
            
            if algorithm == "filter":
                text = "滤波算法对比\n\n包括：\n• 均值滤波\n• 高斯滤波\n• 中值滤波\n• 双边滤波\n• 小波去噪\n• 非局部均值"
            elif algorithm == "edge":
                text = "边缘检测对比\n\n包括：\n• Sobel算子\n• Prewitt算子\n• Laplacian算子\n• Canny算子\n• Roberts算子\n• Scharr算子"
            elif algorithm == "enhance":
                text = "图像增强对比\n\n包括：\n• 直方图均衡化\n• 自适应直方图均衡化\n• 伽马校正\n• 对数变换\n• 对比度拉伸\n• 图像锐化"
            else:
                text = "图像分割对比\n\n包括：\n• Otsu阈值分割\n• 自适应阈值分割\n• K-means聚类\n• 分水岭算法\n• 区域生长\n• Mean Shift分割"
            
            ax.text(0.5, 0.5, text, ha='center', va='center', 
                   transform=ax.transAxes, fontsize=9, linespacing=1.4,
                   bbox=dict(boxstyle="round", facecolor="lightblue", alpha=0.8))
            ax.axis('off')
        
        # 调整布局
        algorithm_names = {
            'filter': '滤波算法',
            'edge': '边缘检测',
            'enhance': '图像增强',
            'segment': '图像分割'
        }
        
        algorithm_title = algorithm_names.get(self.algorithm.get(), self.algorithm.get())
        self.fig.suptitle(f"数字图像处理算法演示 - {algorithm_title}", 
                         fontsize=14, fontweight='bold', y=0.98)
        self.fig.tight_layout(rect=[0, 0.02, 1, 0.95])
        self.canvas.draw()
    
    def save_results(self):
        """保存处理结果"""
        if not self.processed_results:
            messagebox.showwarning("警告", "请先处理图像")
            return
        
        save_dir = filedialog.askdirectory(title="选择保存目录")
        if save_dir:
            try:
                save_path = Path(save_dir)
                
                # 保存原始图像
                if self.original_image is not None:
                    original_path = save_path / "original.png"
                    cv2.imwrite(str(original_path), self.original_image)
                
                # 🔧 关键修复：使用序号作为文件名，避免中文乱码
                # 创建文件索引说明
                index_file = save_path / "file_index.txt"
                with open(index_file, 'w', encoding='utf-8') as idx_f:
                    idx_f.write("文件索引说明\n")
                    idx_f.write("=" * 50 + "\n\n")
                    idx_f.write("文件名 - 算法名称\n")
                    idx_f.write("-" * 50 + "\n")
                
                # 保存所有处理结果
                file_counter = 1
                for key, image in self.processed_results.items():
                    # 🔧 使用序号作为英文文件名
                    filename = save_path / f"result_{file_counter:02d}.png"
                    
                    # 尝试保存
                    try:
                        success = cv2.imwrite(str(filename), image)
                        if not success:
                            # 如果cv2失败，尝试使用PIL
                            from PIL import Image
                            if len(image.shape) == 3:
                                rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
                                pil_image = Image.fromarray(rgb_image)
                            else:
                                pil_image = Image.fromarray(image)
                            pil_image.save(str(filename))
                            print(f"✅ 使用PIL保存: result_{file_counter:02d}.png")
                        else:
                            print(f"✅ 保存成功: result_{file_counter:02d}.png")
                            
                        # 记录到索引文件
                        with open(index_file, 'a', encoding='utf-8') as idx_f:
                            idx_f.write(f"result_{file_counter:02d}.png - {key}\n")
                        
                        file_counter += 1
                            
                    except Exception as e:
                        print(f"❌ 保存失败: {e}")
                        # 使用时间戳重试
                        timestamp = int(time.time() % 10000)
                        simple_name = f"result_{timestamp}.png"
                        simple_path = save_path / simple_name
                        cv2.imwrite(str(simple_path), image)
                
                # 保存对比图
                comparison_path = save_path / "comparison.png"
                self.fig.savefig(str(comparison_path), dpi=150, bbox_inches='tight')
                
                # 保存处理历史
                history_path = save_path / "processing_history.txt"
                with open(history_path, 'w', encoding='utf-8') as f:
                    idx_f.write("文件索引说明\n")
                    idx_f.write("=" * 50 + "\n\n")
                    idx_f.write("保存时间: " + datetime.now().strftime('%Y-%m-%d %H:%M:%S') + "\n")
                    idx_f.write("处理算法: " + self.algorithm.get() + "\n\n")
                    idx_f.write("文件名 - 算法名称 (中文说明)\n")
                    idx_f.write("-" * 50 + "\n")
                
                messagebox.showinfo("成功", f"结果已保存到:\n{save_dir}")
                self.status_bar.config(text=f"结果已保存到: {save_dir}")
                
            except Exception as e:
                messagebox.showerror("保存错误", f"保存文件时出错:\n{str(e)}")
                self.status_bar.config(text=f"保存失败: {str(e)}")
    
    def show_metrics(self):
        """显示性能评估"""
        if self.original_image is None or not self.processed_results:
            messagebox.showwarning("警告", "请先处理图像")
            return
        
        # 创建评估窗口
        metrics_window = tk.Toplevel(self.root)
        metrics_window.title("算法性能评估")
        metrics_window.geometry("700x500")
        
        # 创建笔记本（选项卡）
        notebook = ttk.Notebook(metrics_window)
        notebook.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # 1. PSNR和SSIM评估
        metrics_frame = ttk.Frame(notebook)
        notebook.add(metrics_frame, text="质量评估")
        
        # 创建文本框
        text_frame = ttk.Frame(metrics_frame)
        text_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        text_widget = tk.Text(text_frame, wrap=tk.WORD, font=("Consolas", 10))
        scrollbar = ttk.Scrollbar(text_frame, orient=tk.VERTICAL, command=text_widget.yview)
        text_widget.configure(yscrollcommand=scrollbar.set)
        
        text_widget.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        # 计算评估指标
        text_widget.insert(tk.END, "图像质量评估报告\n")
        text_widget.insert(tk.END, "=" * 50 + "\n\n")
        
        # 这里可以添加具体的评估指标计算
        text_widget.insert(tk.END, "评估指标说明：\n")
        text_widget.insert(tk.END, "1. PSNR（峰值信噪比）：值越大表示质量越好\n")
        text_widget.insert(tk.END, "   一般标准：>30dB（好），20-30dB（可接受），<20dB（差）\n\n")
        text_widget.insert(tk.END, "2. SSIM（结构相似性）：值越接近1表示结构越相似\n")
        text_widget.insert(tk.END, "   范围：0-1，>0.9表示非常好\n\n")
        text_widget.insert(tk.END, "3. 处理时间：算法执行时间（毫秒）\n\n")
        
        text_widget.insert(tk.END, "注：完整评估需要噪声图像作为输入\n")
        text_widget.insert(tk.END, "请使用'开始处理'生成带噪声的图像后再评估。\n")
        
        text_widget.config(state=tk.DISABLED)
        
        # 2. 算法对比
        comparison_frame = ttk.Frame(notebook)
        notebook.add(comparison_frame, text="算法对比")
        
        # 这里可以添加算法对比图表
        comparison_label = ttk.Label(comparison_frame, text="算法对比图表将在此处显示", 
                                    font=("微软雅黑", 12))
        comparison_label.pack(pady=50)
        
        # 3. 使用说明
        help_frame = ttk.Frame(notebook)
        notebook.add(help_frame, text="使用说明")
        
        help_text = """使用说明：
        
        1. 图像生成：
           - 选择图像类型或加载外部图像
        
        2. 添加噪声：
           - 选择噪声类型和强度
           - 用于测试滤波算法
        
        3. 选择算法：
           - 滤波去噪：去除图像噪声
           - 边缘检测：提取图像边缘
           - 图像增强：改善图像质量
           - 图像分割：分割图像区域
        
        4. 调整参数：
           - 根据算法调整相应参数
           - 点击'开始处理'应用算法
        
        5. 保存结果：
           - 保存处理后的图像
           - 可用于课程论文
        """
        
        help_label = ttk.Label(help_frame, text=help_text, justify=tk.LEFT, 
                              font=("微软雅黑", 10))
        help_label.pack(padx=20, pady=20)
    
    def undo_step(self):
        """撤销上一步操作"""
        if len(self.image_history) > 1:
            self.image_history.pop()  # 移除最后一步
            last_image, last_desc = self.image_history[-1]
            self.current_image = last_image.copy()
            
            # 重新显示
            if len(last_image.shape) == 3:
                display_img = cv2.cvtColor(last_image, cv2.COLOR_BGR2RGB)
            else:
                display_img = last_image
            
            # 清除所有子图
            for ax in self.axes.flat:
                ax.clear()
                ax.axis('off')
            
            self.axes[0, 0].imshow(display_img, cmap='gray' if len(last_image.shape) == 2 else None)
            self.axes[0, 0].set_title(f"当前图像: {last_desc}", fontsize=12)
            self.axes[0, 0].axis('on')
            
            self.canvas.draw()
            self.status_bar.config(text=f"已撤销: {last_desc}")
        else:
            self.status_bar.config(text="无法撤销: 已到第一步")
    
    def reset_all(self):
        """重置所有设置"""
        response = messagebox.askyesno("确认重置", "确定要重置所有设置吗？")
        if response:
            self.current_image = None
            self.original_image = None
            self.processed_results = {}
            self.image_history = []
            
            # 重置参数
            self.filter_size.set(5)
            self.canny_low.set(50)
            self.canny_high.set(150)
            self.gamma_value.set(1.5)
            self.cluster_count.set(3)
            
            # 清除显示
            for ax in self.axes.flat:
                ax.clear()
                ax.axis('off')
            
            self.canvas.draw()
            
            self.status_bar.config(text="已重置所有设置")
            messagebox.showinfo("重置", "已重置所有设置")
    
    def show_help(self):
        """显示帮助信息"""
        help_text = """
        数字图像处理算法仿真系统
        版本: 1.0.0
        作者: 湖南工商大学 计算机学院
        
        功能概述：
        1. 支持多种图像生成方式
        2. 提供多种噪声添加方法
        3. 包含滤波、边缘检测、增强、分割四大类算法
        4. 可视化对比不同算法效果
        5. 支持结果保存和性能评估
        
        使用流程：
        1. 生成或加载图像
        2. 添加噪声（可选）
        3. 选择处理算法
        4. 调整算法参数
        5. 开始处理并查看结果
        6. 保存结果用于课程论文
        
        适用课程：
        《数字图像处理及应用》课程项目
        """
        
        messagebox.showinfo("帮助", help_text)
    
    def on_closing(self):
        """窗口关闭事件"""
        response = messagebox.askyesno("退出", "确定要退出程序吗？")
        if response:
            self.root.destroy()
    
    def run(self):
        """运行应用程序"""
        # 设置窗口居中
        self.root.update_idletasks()
        width = self.root.winfo_width()
        height = self.root.winfo_height()
        x = (self.root.winfo_screenwidth() // 2) - (width // 2)
        y = (self.root.winfo_screenheight() // 2) - (height // 2)
        self.root.geometry(f'{width}x{height}+{x}+{y}')
        
        # 运行主循环
        self.root.mainloop()


# 测试运行
if __name__ == "__main__":
    app = ImageProcessingApp()
    app.run()