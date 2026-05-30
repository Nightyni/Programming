"""
可视化工具模块
提供图像显示、对比、图表绘制等功能
"""
import matplotlib.pyplot as plt
import numpy as np
import cv2
from pathlib import Path
import os

class Visualization:
    """可视化工具类"""
    
    @staticmethod
    def display_images(images_dict, title="图像显示", cols=3, figsize=(15, 10)):
        """
        显示多个图像
        
        参数:
            images_dict: 字典，{标题: 图像}
            title: 总标题
            cols: 每行显示的图像数量
            figsize: 图形大小
            
        返回:
            matplotlib图形对象
        """
        num_images = len(images_dict)
        rows = (num_images + cols - 1) // cols
        
        fig, axes = plt.subplots(rows, cols, figsize=figsize)
        
        # 如果只有一行，确保axes是列表
        if rows == 1 and cols == 1:
            axes = np.array([[axes]])
        elif rows == 1:
            axes = axes.reshape(1, -1)
        elif cols == 1:
            axes = axes.reshape(-1, 1)
        
        axes = axes.flat if hasattr(axes, 'flat') else [axes]
        
        for idx, (name, image) in enumerate(images_dict.items()):
            if idx < len(axes):
                if len(image.shape) == 3:
                    # 彩色图像，OpenCV是BGR，需要转RGB
                    display_img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
                    axes[idx].imshow(display_img)
                else:
                    axes[idx].imshow(image, cmap='gray')
                axes[idx].set_title(name, fontsize=10, fontweight='bold')
                axes[idx].axis('off')
        
        # 隐藏多余的子图
        for idx in range(num_images, len(axes)):
            axes[idx].axis('off')
        
        plt.suptitle(title, fontsize=16, fontweight='bold', y=0.98)
        plt.tight_layout(rect=[0, 0.02, 1, 0.95])
        plt.show()
        
        return fig
    
    @staticmethod
    def compare_filters(filter_results, title="滤波效果对比"):
        """
        对比滤波效果
        
        参数:
            filter_results: 滤波结果字典
            title: 对比图标题
            
        返回:
            matplotlib图形对象
        """
        return Visualization.display_images(filter_results, title)
    
    @staticmethod
    def plot_histograms(original, processed, title="直方图对比"):
        """
        绘制直方图对比
        
        参数:
            original: 原始图像
            processed: 处理后的图像
            title: 图表标题
            
        返回:
            matplotlib图形对象
        """
        fig, axes = plt.subplots(2, 2, figsize=(12, 10))
        
        # 原始图像直方图
        if len(original.shape) == 3:
            colors = ('r', 'g', 'b')
            color_names = ('红色通道', '绿色通道', '蓝色通道')
            for i, (color, name) in enumerate(zip(colors, color_names)):
                hist = cv2.calcHist([original], [i], None, [256], [0, 256])
                axes[0, 0].plot(hist, color=color, label=name, alpha=0.7)
            axes[0, 0].legend()
            axes[0, 0].set_title("原始图像直方图（彩色）")
        else:
            hist = cv2.calcHist([original], [0], None, [256], [0, 256])
            axes[0, 0].plot(hist, color='black', linewidth=2)
            axes[0, 0].set_title("原始图像直方图（灰度）")
        
        axes[0, 0].set_xlabel("像素值")
        axes[0, 0].set_ylabel("频率")
        axes[0, 0].grid(True, alpha=0.3)
        
        # 处理图像直方图
        if len(processed.shape) == 3:
            colors = ('r', 'g', 'b')
            for i, color in enumerate(colors):
                hist = cv2.calcHist([processed], [i], None, [256], [0, 256])
                axes[0, 1].plot(hist, color=color, alpha=0.7)
            axes[0, 1].set_title("处理图像直方图（彩色）")
        else:
            hist = cv2.calcHist([processed], [0], None, [256], [0, 256])
            axes[0, 1].plot(hist, color='black', linewidth=2)
            axes[0, 1].set_title("处理图像直方图（灰度）")
        
        axes[0, 1].set_xlabel("像素值")
        axes[0, 1].set_ylabel("频率")
        axes[0, 1].grid(True, alpha=0.3)
        
        # 原始图像
        if len(original.shape) == 3:
            display_original = cv2.cvtColor(original, cv2.COLOR_BGR2RGB)
            axes[1, 0].imshow(display_original)
        else:
            axes[1, 0].imshow(original, cmap='gray')
        axes[1, 0].set_title("原始图像")
        axes[1, 0].axis('off')
        
        # 处理图像
        if len(processed.shape) == 3:
            display_processed = cv2.cvtColor(processed, cv2.COLOR_BGR2RGB)
            axes[1, 1].imshow(display_processed)
        else:
            axes[1, 1].imshow(processed, cmap='gray')
        axes[1, 1].set_title("处理图像")
        axes[1, 1].axis('off')
        
        plt.suptitle(title, fontsize=16, fontweight='bold')
        plt.tight_layout(rect=[0, 0.02, 1, 0.95])
        plt.show()
        
        return fig
    
    @staticmethod
    def plot_3d_surface(image, title="图像3D表面图", cmap='viridis'):
        """
        绘制3D表面图
        
        参数:
            image: 输入图像
            title: 图表标题
            cmap: 色彩映射
            
        返回:
            matplotlib图形对象
        """
        from mpl_toolkits.mplot3d import Axes3D
        
        if len(image.shape) == 3:
            image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        
        # 降低分辨率以提高绘制速度
        if image.shape[0] > 200 or image.shape[1] > 200:
            scale = 200 / max(image.shape)
            new_height = int(image.shape[0] * scale)
            new_width = int(image.shape[1] * scale)
            image = cv2.resize(image, (new_width, new_height))
        
        height, width = image.shape
        x = np.arange(0, width)
        y = np.arange(0, height)
        X, Y = np.meshgrid(x, y)
        Z = image
        
        fig = plt.figure(figsize=(12, 8))
        ax = fig.add_subplot(111, projection='3d')
        
        surf = ax.plot_surface(X, Y, Z, cmap=cmap, 
                              linewidth=0, antialiased=True, 
                              alpha=0.8, rstride=1, cstride=1)
        
        ax.set_xlabel('宽度', fontsize=10)
        ax.set_ylabel('高度', fontsize=10)
        ax.set_zlabel('像素值', fontsize=10)
        ax.set_title(title, fontsize=14, fontweight='bold', y=0.98)
        
        # 添加颜色条
        fig.colorbar(surf, ax=ax, shrink=0.5, aspect=5, label='像素强度')
        
        plt.tight_layout()
        plt.show()
        
        return fig
    
    @staticmethod
    def plot_algorithm_comparison(results_dict, metric='psnr', title="算法性能对比"):
        """
        绘制算法性能对比图
        
        参数:
            results_dict: 结果字典，{算法名: 指标值}
            metric: 评估指标名称
            title: 图表标题
            
        返回:
            matplotlib图形对象
        """
        algorithms = list(results_dict.keys())
        values = list(results_dict.values())
        
        fig, ax = plt.subplots(figsize=(10, 6))
        
        # 创建柱状图
        bars = ax.bar(algorithms, values, color=plt.cm.Set3(np.arange(len(algorithms))/len(algorithms)))
        
        # 添加数值标签
        for bar, value in zip(bars, values):
            height = bar.get_height()
            ax.text(bar.get_x() + bar.get_width()/2., height + 0.01,
                   f'{value:.2f}', ha='center', va='bottom', fontsize=10)
        
        # 设置图表属性
        metric_names = {
            'psnr': 'PSNR (dB)',
            'ssim': 'SSIM',
            'mse': 'MSE',
            'time': '处理时间 (ms)'
        }
        
        ylabel = metric_names.get(metric, metric.upper())
        ax.set_ylabel(ylabel, fontsize=12)
        ax.set_xlabel('算法', fontsize=12)
        ax.set_title(title, fontsize=16, fontweight='bold')
        ax.grid(True, alpha=0.3, axis='y')
        
        plt.xticks(rotation=45, ha='right')
        plt.tight_layout()
        plt.show()
        
        return fig
    
    @staticmethod
    def plot_noise_comparison(noise_results, title="不同噪声类型对比"):
        """
        绘制不同噪声类型对比
        
        参数:
            noise_results: 噪声结果字典，{噪声类型: 噪声图像}
            title: 图表标题
            
        返回:
            matplotlib图形对象
        """
        fig, axes = plt.subplots(2, 3, figsize=(15, 10))
        axes = axes.flatten()
        
        for idx, (noise_type, noisy_image) in enumerate(noise_results.items()):
            if idx < len(axes):
                if len(noisy_image.shape) == 3:
                    display_img = cv2.cvtColor(noisy_image, cv2.COLOR_BGR2RGB)
                    axes[idx].imshow(display_img)
                else:
                    axes[idx].imshow(noisy_image, cmap='gray')
                
                # 中文噪声名称
                noise_names = {
                    'gaussian': '高斯噪声',
                    'salt_pepper': '椒盐噪声',
                    'speckle': '斑点噪声',
                    'poisson': '泊松噪声'
                }
                
                display_name = noise_names.get(noise_type, noise_type)
                axes[idx].set_title(display_name, fontsize=12, fontweight='bold')
                axes[idx].axis('off')
        
        # 隐藏多余的子图
        for idx in range(len(noise_results), len(axes)):
            axes[idx].axis('off')
        
        plt.suptitle(title, fontsize=16, fontweight='bold', y=0.98)
        plt.tight_layout(rect=[0, 0.02, 1, 0.95])
        plt.show()
        
        return fig
    
    @staticmethod
    def save_figure(fig, filename, dpi=300, format='png'):
        """
        保存图形到文件
        
        参数:
            fig: matplotlib图形对象
            filename: 保存文件名
            dpi: 分辨率
            format: 保存格式
            
        返回:
            bool: 是否保存成功
        """
        try:
            # 确保目录存在
            output_dir = os.path.dirname(filename)
            if output_dir and not os.path.exists(output_dir):
                os.makedirs(output_dir)
            
            fig.savefig(filename, dpi=dpi, format=format, bbox_inches='tight')
            print(f"✅ 图形已保存: {filename}")
            return True
        except Exception as e:
            print(f"❌ 保存图形失败: {e}")
            return False
    
    @staticmethod
    def create_animation(images, output_path="animation.gif", duration=500):
        """
        创建图像动画（GIF）
        
        参数:
            images: 图像列表
            output_path: 输出路径
            duration: 每帧持续时间（毫秒）
            
        返回:
            bool: 是否创建成功
        """
        try:
            from PIL import Image
            
            # 转换图像为PIL格式
            pil_images = []
            for img in images:
                if len(img.shape) == 3:
                    # BGR转RGB
                    img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                    pil_img = Image.fromarray(img_rgb)
                else:
                    pil_img = Image.fromarray(img)
                pil_images.append(pil_img)
            
            # 保存为GIF
            if len(pil_images) > 0:
                pil_images[0].save(
                    output_path,
                    save_all=True,
                    append_images=pil_images[1:],
                    duration=duration,
                    loop=0
                )
                print(f"✅ 动画已保存: {output_path}")
                return True
            else:
                print("❌ 没有图像可创建动画")
                return False
                
        except ImportError:
            print("❌ 需要PIL库来创建动画")
            return False
        except Exception as e:
            print(f"❌ 创建动画失败: {e}")
            return False
    
    @staticmethod
    def plot_parameter_sensitivity(results, parameter_name, metric='psnr', title="参数敏感性分析"):
        """
        绘制参数敏感性分析图
        
        参数:
            results: 结果字典，{参数值: 指标值}
            parameter_name: 参数名称
            metric: 评估指标
            title: 图表标题
            
        返回:
            matplotlib图形对象
        """
        parameter_values = list(results.keys())
        metric_values = list(results.values())
        
        fig, ax = plt.subplots(figsize=(10, 6))
        
        # 绘制折线图
        ax.plot(parameter_values, metric_values, 'o-', linewidth=2, markersize=8)
        
        # 标记最佳点
        if metric in ['psnr', 'ssim']:
            best_idx = np.argmax(metric_values)
        else:  # mse, time等越小越好
            best_idx = np.argmin(metric_values)
        
        best_param = parameter_values[best_idx]
        best_value = metric_values[best_idx]
        
        ax.plot(best_param, best_value, 'ro', markersize=10)
        ax.annotate(f'最佳: {best_param}\n值: {best_value:.2f}',
                   xy=(best_param, best_value),
                   xytext=(best_param*1.1, best_value*0.9),
                   arrowprops=dict(arrowstyle='->', color='red'),
                   fontsize=10)
        
        # 设置图表属性
        metric_names = {
            'psnr': 'PSNR (dB)',
            'ssim': 'SSIM',
            'mse': 'MSE',
            'time': '处理时间 (ms)'
        }
        
        ylabel = metric_names.get(metric, metric.upper())
        ax.set_xlabel(parameter_name, fontsize=12)
        ax.set_ylabel(ylabel, fontsize=12)
        ax.set_title(title, fontsize=16, fontweight='bold')
        ax.grid(True, alpha=0.3)
        
        plt.tight_layout()
        plt.show()
        
        return fig