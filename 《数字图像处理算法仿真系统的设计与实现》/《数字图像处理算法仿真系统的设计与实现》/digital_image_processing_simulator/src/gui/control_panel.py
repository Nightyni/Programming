import tkinter as tk
from tkinter import ttk

class ControlPanel:
    """控制面板组件"""
    
    def __init__(self, parent, controller):
        """
        初始化控制面板
        
        参数:
            parent: 父容器
            controller: 控制器对象（通常是主窗口）
        """
        self.parent = parent
        self.controller = controller
        
        # 创建控制面板框架
        self.frame = ttk.LabelFrame(parent, text="控制面板", padding=10)
        self.frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # 初始化UI
        self.setup_ui()
    
    def setup_ui(self):
        """设置UI"""
        # 创建滚动条容器
        canvas = tk.Canvas(self.frame)
        scrollbar = ttk.Scrollbar(self.frame, orient="vertical", command=canvas.yview)
        scrollable_frame = ttk.Frame(canvas)
        
        scrollable_frame.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )
        
        canvas.create_window((0, 0), window=scrollable_frame, anchor="nw")
        canvas.configure(yscrollcommand=scrollbar.set)
        
        canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")
        
        # 图像生成部分
        self.setup_image_generation(scrollable_frame)
        
        # 噪声添加部分
        self.setup_noise_addition(scrollable_frame)
        
        # 算法选择部分
        self.setup_algorithm_selection(scrollable_frame)
        
        # 参数调整部分
        self.setup_parameter_adjustment(scrollable_frame)
        
        # 控制按钮部分
        self.setup_control_buttons(scrollable_frame)
    
    def setup_image_generation(self, parent):
        """设置图像生成部分"""
        frame = ttk.LabelFrame(parent, text="1. 图像生成", padding=10)
        frame.pack(fill=tk.X, padx=5, pady=5)
        
        ttk.Label(frame, text="选择图像类型:").pack(anchor=tk.W, pady=(0,5))
        
        # 图像类型选择
        self.image_type = tk.StringVar(value="chessboard")
        
        types = [
            ("棋盘格图像", "chessboard", "适合测试滤波和边缘检测"),
            ("同心圆图像", "circles", "适合测试边缘检测和分割"),
            ("渐变图像", "gradient", "适合测试图像增强"),
            ("随机形状", "random_shapes", "适合测试分割算法"),
            ("测试图案", "test_pattern", "综合测试图案")
        ]
        
        for text, value, tip in types:
            rb = ttk.Radiobutton(frame, text=text, variable=self.image_type, 
                               value=value)
            rb.pack(anchor=tk.W, padx=20)
            self.create_tooltip(rb, tip)
        
        # 分隔线
        ttk.Separator(frame, orient='horizontal').pack(fill=tk.X, pady=10)
        
        # 外部图像加载
        load_frame = ttk.Frame(frame)
        load_frame.pack(fill=tk.X, pady=5)
        
        ttk.Button(load_frame, text="📁 加载图像文件", 
                  command=self.controller.load_external_image,
                  width=20).pack(side=tk.LEFT, padx=(0,10))
        
        ttk.Button(load_frame, text="📸 摄像头捕获", 
                  command=self.capture_from_camera,
                  width=20).pack(side=tk.LEFT)
    
    def setup_noise_addition(self, parent):
        """设置噪声添加部分"""
        frame = ttk.LabelFrame(parent, text="2. 添加噪声", padding=10)
        frame.pack(fill=tk.X, padx=5, pady=5)
        
        # 噪声类型选择
        self.noise_type = tk.StringVar(value="gaussian")
        
        noises = [
            ("无噪声", "none", "保持原始图像"),
            ("高斯噪声", "gaussian", "模拟传感器噪声"),
            ("椒盐噪声", "salt_pepper", "模拟传输噪声"),
            ("斑点噪声", "speckle", "模拟乘性噪声"),
            ("泊松噪声", "poisson", "模拟光子噪声")
        ]
        
        for text, value, tip in noises:
            rb = ttk.Radiobutton(frame, text=text, variable=self.noise_type,
                               value=value)
            rb.pack(anchor=tk.W, padx=20)
            self.create_tooltip(rb, tip)
        
        # 噪声强度控制
        ttk.Label(frame, text="噪声强度:").pack(anchor=tk.W, pady=(10,0))
        
        intensity_frame = ttk.Frame(frame)
        intensity_frame.pack(fill=tk.X, pady=5)
        
        self.noise_intensity = tk.DoubleVar(value=0.05)
        scale = ttk.Scale(intensity_frame, from_=0.01, to=0.2, 
                         variable=self.noise_intensity,
                         orient=tk.HORIZONTAL)
        scale.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=(0,10))
        
        # 强度值显示
        self.intensity_label = ttk.Label(intensity_frame, text="0.05", width=5)
        self.intensity_label.pack(side=tk.RIGHT)
        
        # 绑定强度值更新
        self.noise_intensity.trace_add('write', self.update_intensity_label)
    
    def setup_algorithm_selection(self, parent):
        """设置算法选择部分"""
        frame = ttk.LabelFrame(parent, text="3. 处理算法", padding=10)
        frame.pack(fill=tk.X, padx=5, pady=5)
        
        self.algorithm = tk.StringVar(value="filter")
        
        algorithms = [
            ("滤波去噪", "filter", "去除图像噪声，改善图像质量"),
            ("边缘检测", "edge", "提取图像边缘特征"),
            ("图像增强", "enhance", "改善图像视觉效果"),
            ("图像分割", "segment", "将图像分成有意义的区域")
        ]
        
        for text, value, tip in algorithms:
            rb = ttk.Radiobutton(frame, text=text, variable=self.algorithm,
                               value=value)
            rb.pack(anchor=tk.W, padx=20)
            self.create_tooltip(rb, tip)
        
        # 绑定算法选择变化
        self.algorithm.trace_add('write', self.on_algorithm_changed)
    
    def setup_parameter_adjustment(self, parent):
        """设置参数调整部分"""
        self.param_frame = ttk.LabelFrame(parent, text="4. 算法参数", padding=10)
        self.param_frame.pack(fill=tk.X, padx=5, pady=5)
        
        # 创建不同算法的参数面板
        self.param_panels = {}
        
        # 滤波参数面板
        filter_panel = self.create_filter_panel()
        self.param_panels['filter'] = filter_panel
        
        # 边缘检测参数面板
        edge_panel = self.create_edge_panel()
        self.param_panels['edge'] = edge_panel
        
        # 图像增强参数面板
        enhance_panel = self.create_enhance_panel()
        self.param_panels['enhance'] = enhance_panel
        
        # 图像分割参数面板
        segment_panel = self.create_segment_panel()
        self.param_panels['segment'] = segment_panel
        
        # 初始显示滤波参数面板
        self.show_parameter_panel('filter')
    
    def create_filter_panel(self):
        """创建滤波参数面板"""
        panel = ttk.Frame(self.param_frame)
        
        # 滤波器大小
        ttk.Label(panel, text="滤波器大小:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.filter_size = tk.IntVar(value=5)
        scale = ttk.Scale(panel, from_=3, to=15, variable=self.filter_size,
                         orient=tk.HORIZONTAL, length=180)
        scale.grid(row=0, column=1, sticky=tk.EW, padx=10, pady=5)
        
        self.filter_size_label = ttk.Label(panel, text="5", width=5)
        self.filter_size_label.grid(row=0, column=2, padx=(0,10))
        self.filter_size.trace_add('write', lambda *args: self.update_filter_size_label())
        
        # 高斯滤波sigma
        ttk.Label(panel, text="高斯Sigma:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.gaussian_sigma = tk.DoubleVar(value=1.0)
        sigma_scale = ttk.Scale(panel, from_=0.1, to=5.0, variable=self.gaussian_sigma,
                              orient=tk.HORIZONTAL, length=180)
        sigma_scale.grid(row=1, column=1, sticky=tk.EW, padx=10, pady=5)
        
        self.sigma_label = ttk.Label(panel, text="1.0", width=5)
        self.sigma_label.grid(row=1, column=2, padx=(0,10))
        self.gaussian_sigma.trace_add('write', lambda *args: self.update_sigma_label())
        
        return panel
    
    def create_edge_panel(self):
        """创建边缘检测参数面板"""
        panel = ttk.Frame(self.param_frame)
        
        # Canny低阈值
        ttk.Label(panel, text="Canny低阈值:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.canny_low = tk.IntVar(value=50)
        low_scale = ttk.Scale(panel, from_=10, to=100, variable=self.canny_low,
                            orient=tk.HORIZONTAL, length=180)
        low_scale.grid(row=0, column=1, sticky=tk.EW, padx=10, pady=5)
        
        self.canny_low_label = ttk.Label(panel, text="50", width=5)
        self.canny_low_label.grid(row=0, column=2, padx=(0,10))
        self.canny_low.trace_add('write', lambda *args: self.update_canny_low_label())
        
        # Canny高阈值
        ttk.Label(panel, text="Canny高阈值:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.canny_high = tk.IntVar(value=150)
        high_scale = ttk.Scale(panel, from_=50, to=250, variable=self.canny_high,
                             orient=tk.HORIZONTAL, length=180)
        high_scale.grid(row=1, column=1, sticky=tk.EW, padx=10, pady=5)
        
        self.canny_high_label = ttk.Label(panel, text="150", width=5)
        self.canny_high_label.grid(row=1, column=2, padx=(0,10))
        self.canny_high.trace_add('write', lambda *args: self.update_canny_high_label())
        
        return panel
    
    def create_enhance_panel(self):
        """创建图像增强参数面板"""
        panel = ttk.Frame(self.param_frame)
        
        # 伽马值
        ttk.Label(panel, text="伽马值:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.gamma_value = tk.DoubleVar(value=1.5)
        gamma_scale = ttk.Scale(panel, from_=0.1, to=3.0, variable=self.gamma_value,
                              orient=tk.HORIZONTAL, length=180)
        gamma_scale.grid(row=0, column=1, sticky=tk.EW, padx=10, pady=5)
        
        self.gamma_label = ttk.Label(panel, text="1.5", width=5)
        self.gamma_label.grid(row=0, column=2, padx=(0,10))
        self.gamma_value.trace_add('write', lambda *args: self.update_gamma_label())
        
        # CLAHE参数
        ttk.Label(panel, text="CLAHE限幅:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.clahe_clip = tk.DoubleVar(value=2.0)
        clahe_scale = ttk.Scale(panel, from_=1.0, to=5.0, variable=self.clahe_clip,
                              orient=tk.HORIZONTAL, length=180)
        clahe_scale.grid(row=1, column=1, sticky=tk.EW, padx=10, pady=5)
        
        self.clahe_label = ttk.Label(panel, text="2.0", width=5)
        self.clahe_label.grid(row=1, column=2, padx=(0,10))
        self.clahe_clip.trace_add('write', lambda *args: self.update_clahe_label())
        
        return panel
    
    def create_segment_panel(self):
        """创建图像分割参数面板"""
        panel = ttk.Frame(self.param_frame)
        
        # 聚类数量
        ttk.Label(panel, text="聚类数量:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.cluster_count = tk.IntVar(value=3)
        cluster_spinbox = ttk.Spinbox(panel, from_=2, to=8, 
                                     textvariable=self.cluster_count, width=10)
        cluster_spinbox.grid(row=0, column=1, sticky=tk.W, padx=10, pady=5)
        
        # 阈值方法
        ttk.Label(panel, text="阈值方法:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.threshold_method = tk.StringVar(value="otsu")
        
        method_frame = ttk.Frame(panel)
        method_frame.grid(row=1, column=1, columnspan=2, sticky=tk.W, padx=10, pady=5)
        
        methods = [("Otsu", "otsu"), ("自适应", "adaptive"), ("三角形", "triangle")]
        for text, value in methods:
            rb = ttk.Radiobutton(method_frame, text=text, variable=self.threshold_method,
                               value=value)
            rb.pack(side=tk.LEFT, padx=(0,10))
        
        return panel
    
    def setup_control_buttons(self, parent):
        """设置控制按钮部分"""
        frame = ttk.Frame(parent)
        frame.pack(fill=tk.X, padx=5, pady=10)
        
        # 按钮样式
        style = ttk.Style()
        style.configure('Accent.TButton', font=('微软雅黑', 10, 'bold'))
        
        # 主按钮
        buttons = [
            ("🚀 开始处理", self.controller.process_image, 'Accent.TButton'),
            ("💾 保存结果", self.controller.save_results, None),
            ("📊 性能评估", self.controller.show_metrics, None),
            ("↩️ 上一步", self.controller.undo_step, None),
            ("🔄 重置", self.controller.reset_all, None),
            ("❓ 帮助", self.controller.show_help, None)
        ]
        
        for text, command, style_name in buttons:
            btn = ttk.Button(frame, text=text, command=command, style=style_name)
            btn.pack(fill=tk.X, pady=2)
    
    def show_parameter_panel(self, algorithm):
        """显示指定算法的参数面板"""
        # 隐藏所有参数面板
        for panel in self.param_panels.values():
            panel.pack_forget()
        
        # 显示当前算法的参数面板
        if algorithm in self.param_panels:
            self.param_panels[algorithm].pack(fill=tk.X)
    
    def on_algorithm_changed(self, *args):
        """算法选择变化时的处理"""
        algorithm = self.algorithm.get()
        self.show_parameter_panel(algorithm)
    
    def update_intensity_label(self, *args):
        """更新噪声强度标签"""
        value = self.noise_intensity.get()
        self.intensity_label.config(text=f"{value:.2f}")
    
    def update_filter_size_label(self, *args):
        """更新滤波器大小标签"""
        value = self.filter_size.get()
        self.filter_size_label.config(text=str(value))
    
    def update_sigma_label(self, *args):
        """更新Sigma标签"""
        value = self.gaussian_sigma.get()
        self.sigma_label.config(text=f"{value:.1f}")
    
    def update_canny_low_label(self, *args):
        """更新Canny低阈值标签"""
        value = self.canny_low.get()
        self.canny_low_label.config(text=str(value))
    
    def update_canny_high_label(self, *args):
        """更新Canny高阈值标签"""
        value = self.canny_high.get()
        self.canny_high_label.config(text=str(value))
    
    def update_gamma_label(self, *args):
        """更新伽马值标签"""
        value = self.gamma_value.get()
        self.gamma_label.config(text=f"{value:.1f}")
    
    def update_clahe_label(self, *args):
        """更新CLAHE标签"""
        value = self.clahe_clip.get()
        self.clahe_label.config(text=f"{value:.1f}")
    
    def capture_from_camera(self):
        """从摄像头捕获图像"""
        # 这里可以实现摄像头捕获功能
        # 目前显示提示信息
        import tkinter.messagebox as messagebox
        messagebox.showinfo("摄像头捕获", "摄像头功能将在后续版本中实现")
    
    def create_tooltip(self, widget, text):
        """为部件创建工具提示"""
        def show_tooltip(event):
            tooltip = tk.Toplevel()
            tooltip.wm_overrideredirect(True)
            tooltip.wm_geometry(f"+{event.x_root+10}+{event.y_root+10}")
            
            label = ttk.Label(tooltip, text=text, background="lightyellow",
                            relief="solid", borderwidth=1, padding=5)
            label.pack()
            
            widget.tooltip_window = tooltip
        
        def hide_tooltip(event):
            if hasattr(widget, 'tooltip_window'):
                widget.tooltip_window.destroy()
                delattr(widget, 'tooltip_window')
        
        widget.bind("<Enter>", show_tooltip)
        widget.bind("<Leave>", hide_tooltip)
    
    def get_parameters(self):
        """获取所有参数"""
        return {
            'image_type': self.image_type.get(),
            'noise_type': self.noise_type.get(),
            'noise_intensity': self.noise_intensity.get(),
            'algorithm': self.algorithm.get(),
            'filter_size': self.filter_size.get(),
            'gaussian_sigma': self.gaussian_sigma.get(),
            'canny_low': self.canny_low.get(),
            'canny_high': self.canny_high.get(),
            'gamma_value': self.gamma_value.get(),
            'clahe_clip': self.clahe_clip.get(),
            'cluster_count': self.cluster_count.get(),
            'threshold_method': self.threshold_method.get()
        }