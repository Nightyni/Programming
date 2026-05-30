import tkinter as tk
from tkinter import ttk
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
import numpy as np
import cv2

class DisplayPanel:
    """显示面板组件"""
    
    def __init__(self, parent, controller):
        """
        初始化显示面板
        
        参数:
            parent: 父容器
            controller: 控制器对象
        """
        self.parent = parent
        self.controller = controller
        
        # 创建显示面板框架
        self.frame = ttk.LabelFrame(parent, text="结果展示", padding=5)
        self.frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # 初始化UI
        self.setup_ui()
        
        # 当前显示的图像
        self.current_images = []
        self.current_titles = []
    
    def setup_ui(self):
        """设置UI"""
        # 创建Matplotlib图形
        self.fig, self.axes = plt.subplots(2, 3, figsize=(12, 8))
        self.fig.tight_layout(pad=3.0)
        
        # 创建Canvas
        self.canvas = FigureCanvasTkAgg(self.fig, master=self.frame)
        self.canvas.draw()
        
        # 添加工具栏
        toolbar_frame = ttk.Frame(self.frame)
        toolbar_frame.pack(fill=tk.X)
        
        self.toolbar = NavigationToolbar2Tk(self.canvas, toolbar_frame)
        self.toolbar.update()
        
        # 包装Canvas
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # 状态栏
        self.status_bar = ttk.Label(self.frame, text="就绪", relief=tk.SUNKEN, 
                                   anchor=tk.W, font=("微软雅黑", 9))
        self.status_bar.pack(side=tk.BOTTOM, fill=tk.X, padx=2, pady=2)
        
        # 图像信息栏
        self.info_bar = ttk.Label(self.frame, text="", relief=tk.SUNKEN,
                                 anchor=tk.W, font=("Consolas", 9))
        self.info_bar.pack(side=tk.BOTTOM, fill=tk.X, padx=2, pady=2)
    
    def display_single_image(self, image, title="图像", cmap=None):
        """
        显示单张图像
        
        参数:
            image: 要显示的图像
            title: 图像标题
            cmap: 色彩映射（灰度图时使用）
        """
        # 清除所有子图
        for ax in self.axes.flat:
            ax.clear()
            ax.axis('off')
        
        # 显示图像
        if len(image.shape) == 3:
            # 彩色图像，BGR转RGB
            display_img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            self.axes[0, 0].imshow(display_img)
        else:
            # 灰度图像
            self.axes[0, 0].imshow(image, cmap=cmap or 'gray')
        
        self.axes[0, 0].set_title(title, fontsize=12, fontweight='bold')
        self.axes[0, 0].axis('on')
        
        # 添加图像信息
        self.update_image_info(image, title)
        
        # 隐藏其他子图
        for i in range(1, 6):
            self.axes.flatten()[i].axis('off')
        
        self.fig.tight_layout()
        self.canvas.draw()
        
        # 更新状态栏
        self.status_bar.config(text=f"显示: {title}")
    
    def display_multiple_images(self, images, titles, layout=(2, 3)):
        """
        显示多张图像
        
        参数:
            images: 图像列表
            titles: 标题列表
            layout: 布局 (行, 列)
        """
        rows, cols = layout
        
        # 调整图形大小
        self.fig.set_size_inches(4*cols, 3*rows)
        
        # 清除所有子图
        for ax in self.axes.flat:
            ax.clear()
            ax.axis('off')
        
        # 显示图像
        for idx, (image, title) in enumerate(zip(images, titles)):
            if idx >= rows * cols:
                break
                
            row = idx // cols
            col = idx % cols
            
            if len(image.shape) == 3:
                display_img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
                self.axes[row, col].imshow(display_img)
            else:
                self.axes[row, col].imshow(image, cmap='gray')
            
            self.axes[row, col].set_title(title, fontsize=10, fontweight='bold')
            self.axes[row, col].axis('on')
        
        # 隐藏剩余的子图
        total_images = len(images)
        for idx in range(total_images, rows * cols):
            row = idx // cols
            col = idx % cols
            self.axes[row, col].axis('off')
            self.axes[row, col].text(0.5, 0.5, "空", 
                                    transform=self.axes[row, col].transAxes,
                                    ha='center', va='center', fontsize=12, alpha=0.3)
        
        self.fig.tight_layout()
        self.canvas.draw()
        
        # 更新状态栏
        self.status_bar.config(text=f"显示 {len(images)} 张图像")
        
        # 更新图像信息（显示第一张图像的信息）
        if images:
            self.update_image_info(images[0], titles[0])
    
    def display_comparison(self, original, processed, original_title="原始图像", 
                          processed_title="处理结果", noise_image=None, noise_title="噪声图像"):
        """
        显示对比图像（原始vs处理）
        
        参数:
            original: 原始图像
            processed: 处理后的图像
            original_title: 原始图像标题
            processed_title: 处理结果标题
            noise_image: 噪声图像（可选）
            noise_title: 噪声图像标题
        """
        images = [original]
        titles = [original_title]
        
        if noise_image is not None:
            images.append(noise_image)
            titles.append(noise_title)
        
        images.append(processed)
        titles.append(processed_title)
        
        self.display_multiple_images(images, titles, layout=(1, len(images)))
    
    def display_algorithm_results(self, original, noisy, results, algorithm_name):
        """
        显示算法处理结果
        
        参数:
            original: 原始图像
            noisy: 噪声图像
            results: 处理结果字典
            algorithm_name: 算法名称
        """
        # 准备所有要显示的图像
        images = [original]
        titles = ["原始图像"]
        
        # 添加噪声图像
        if noisy is not None and not np.array_equal(original, noisy):
            images.append(noisy)
            titles.append("噪声图像")
        
        # 添加处理结果（最多4个）
        result_items = list(results.items())[:4]
        for title, img in result_items:
            images.append(img)
            titles.append(title)
        
        # 显示所有图像
        self.display_multiple_images(images, titles, layout=(2, 3))
        
        # 设置总标题
        algorithm_names = {
            'filter': '滤波去噪算法',
            'edge': '边缘检测算法',
            'enhance': '图像增强算法',
            'segment': '图像分割算法'
        }
        
        display_name = algorithm_names.get(algorithm_name, '图像处理算法')
        self.fig.suptitle(f"{display_name} - 结果对比", 
                         fontsize=14, fontweight='bold', y=0.98)
        
        self.canvas.draw()
        
        # 更新状态栏
        self.status_bar.config(text=f"{display_name} - 显示 {len(images)} 个结果")
    
    def update_image_info(self, image, title):
        """更新图像信息栏"""
        if image is None:
            self.info_bar.config(text="")
            return
        
        height, width = image.shape[:2]
        
        if len(image.shape) == 3:
            channels = image.shape[2]
            dtype = image.dtype
            min_val = image.min()
            max_val = image.max()
            mean_val = image.mean()
            
            info_text = (f"{title}: {width}×{height}×{channels} | "
                        f"类型: {dtype} | 范围: [{min_val:.1f}, {max_val:.1f}] | "
                        f"均值: {mean_val:.1f}")
        else:
            dtype = image.dtype
            min_val = image.min()
            max_val = image.max()
            mean_val = image.mean()
            
            info_text = (f"{title}: {width}×{height} | "
                        f"类型: {dtype} | 范围: [{min_val:.1f}, {max_val:.1f}] | "
                        f"均值: {mean_val:.1f}")
        
        self.info_bar.config(text=info_text)
    
    def clear_display(self):
        """清除显示"""
        for ax in self.axes.flat:
            ax.clear()
            ax.axis('off')
        
        # 添加提示文本
        self.axes[0, 0].text(0.5, 0.5, "请生成或加载图像开始处理", 
                            transform=self.axes[0, 0].transAxes,
                            ha='center', va='center', fontsize=14, alpha=0.5)
        self.axes[0, 0].axis('on')
        
        self.fig.tight_layout()
        self.canvas.draw()
        
        # 清空信息栏
        self.info_bar.config(text="")
        self.status_bar.config(text="等待输入...")
    
    def show_histogram(self, image, title="直方图"):
        """
        显示图像直方图
        
        参数:
            image: 输入图像
            title: 直方图标题
        """
        # 清除所有子图
        for ax in self.axes.flat:
            ax.clear()
            ax.axis('off')
        
        if len(image.shape) == 3:
            # 彩色图像，分别显示RGB通道直方图
            colors = ('b', 'g', 'r')
            channel_names = ('蓝色通道', '绿色通道', '红色通道')
            
            for i, (color, name) in enumerate(zip(colors, channel_names)):
                if i < 3:  # 最多显示3个子图
                    row = i // 3
                    col = i % 3
                    
                    # 计算直方图
                    hist = cv2.calcHist([image], [i], None, [256], [0, 256])
                    hist = hist.flatten()
                    
                    # 绘制直方图
                    self.axes[row, col].plot(hist, color=color)
                    self.axes[row, col].set_title(f"{name}直方图", fontsize=10)
                    self.axes[row, col].set_xlim([0, 256])
                    self.axes[row, col].grid(True, alpha=0.3)
                    self.axes[row, col].axis('on')
            
            # 显示原始图像
            display_img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            self.axes[1, 2].imshow(display_img)
            self.axes[1, 2].set_title("原始图像", fontsize=10)
            self.axes[1, 2].axis('on')
            
        else:
            # 灰度图像
            # 计算直方图
            hist = cv2.calcHist([image], [0], None, [256], [0, 256])
            hist = hist.flatten()
            
            # 左侧显示直方图
            self.axes[0, 0].plot(hist, color='black')
            self.axes[0, 0].set_title("灰度直方图", fontsize=12, fontweight='bold')
            self.axes[0, 0].set_xlabel("像素值")
            self.axes[0, 0].set_ylabel("频率")
            self.axes[0, 0].set_xlim([0, 256])
            self.axes[0, 0].grid(True, alpha=0.3)
            self.axes[0, 0].axis('on')
            
            # 右侧显示图像
            self.axes[0, 1].imshow(image, cmap='gray')
            self.axes[0, 1].set_title("原始图像", fontsize=12, fontweight='bold')
            self.axes[0, 1].axis('on')
        
        self.fig.suptitle(title, fontsize=14, fontweight='bold', y=0.98)
        self.fig.tight_layout(rect=[0, 0.02, 1, 0.95])
        self.canvas.draw()
        
        # 更新状态栏
        self.status_bar.config(text=f"显示: {title}")
    
    def save_current_display(self, filename="output.png", dpi=150):
        """
        保存当前显示内容
        
        参数:
            filename: 保存文件名
            dpi: 分辨率
            
        返回:
            bool: 是否保存成功
        """
        try:
            self.fig.savefig(filename, dpi=dpi, bbox_inches='tight')
            self.status_bar.config(text=f"已保存: {filename}")
            return True
        except Exception as e:
            self.status_bar.config(text=f"保存失败: {str(e)}")
            return False
    
    def set_status(self, message):
        """设置状态栏消息"""
        self.status_bar.config(text=message)
    
    def set_info(self, message):
        """设置信息栏消息"""
        self.info_bar.config(text=message)