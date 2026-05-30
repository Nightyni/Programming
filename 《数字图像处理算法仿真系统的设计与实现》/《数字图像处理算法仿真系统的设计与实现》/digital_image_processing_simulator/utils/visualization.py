import matplotlib.pyplot as plt
import numpy as np
import cv2

class Visualization:
    """可视化工具类"""
    
    @staticmethod
    def display_images(images_dict, title="图像显示", cols=3):
        """显示多个图像"""
        num_images = len(images_dict)
        rows = (num_images + cols - 1) // cols
        
        fig, axes = plt.subplots(rows, cols, figsize=(15, 5*rows))
        axes = axes.flat if hasattr(axes, 'flat') else [axes]
        
        for idx, (name, image) in enumerate(images_dict.items()):
            if idx < len(axes):
                if len(image.shape) == 3:
                    # 彩色图像，OpenCV是BGR，需要转RGB
                    display_img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
                    axes[idx].imshow(display_img)
                else:
                    axes[idx].imshow(image, cmap='gray')
                axes[idx].set_title(name)
                axes[idx].axis('off')
        
        # 隐藏多余的子图
        for idx in range(num_images, len(axes)):
            axes[idx].axis('off')
        
        plt.suptitle(title, fontsize=16)
        plt.tight_layout()
        plt.show()
    
    @staticmethod
    def compare_filters(filter_results, title="滤波效果对比"):
        """对比滤波效果"""
        Visualization.display_images(filter_results, title)
    
    @staticmethod
    def plot_histograms(original, processed, title="直方图对比"):
        """绘制直方图对比"""
        fig, axes = plt.subplots(2, 2, figsize=(12, 8))
        
        # 原始图像直方图
        if len(original.shape) == 3:
            colors = ('r', 'g', 'b')
            for i, color in enumerate(colors):
                hist = cv2.calcHist([original], [i], None, [256], [0, 256])
                axes[0, 0].plot(hist, color=color)
            axes[0, 0].set_title("原始图像直方图")
        else:
            hist = cv2.calcHist([original], [0], None, [256], [0, 256])
            axes[0, 0].plot(hist, color='black')
            axes[0, 0].set_title("原始图像直方图")
        
        # 处理图像直方图
        if len(processed.shape) == 3:
            colors = ('r', 'g', 'b')
            for i, color in enumerate(colors):
                hist = cv2.calcHist([processed], [i], None, [256], [0, 256])
                axes[0, 1].plot(hist, color=color)
            axes[0, 1].set_title("处理图像直方图")
        else:
            hist = cv2.calcHist([processed], [0], None, [256], [0, 256])
            axes[0, 1].plot(hist, color='black')
            axes[0, 1].set_title("处理图像直方图")
        
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
        
        plt.suptitle(title, fontsize=16)
        plt.tight_layout()
        plt.show()
    
    @staticmethod
    def plot_3d_surface(image, title="图像3D表面图"):
        """绘制3D表面图"""
        from mpl_toolkits.mplot3d import Axes3D
        
        if len(image.shape) == 3:
            image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        
        height, width = image.shape
        x = np.arange(0, width)
        y = np.arange(0, height)
        X, Y = np.meshgrid(x, y)
        Z = image
        
        fig = plt.figure(figsize=(12, 8))
        ax = fig.add_subplot(111, projection='3d')
        
        surf = ax.plot_surface(X, Y, Z, cmap='viridis', 
                              linewidth=0, antialiased=True, 
                              alpha=0.8)
        
        ax.set_xlabel('宽度')
        ax.set_ylabel('高度')
        ax.set_zlabel('像素值')
        ax.set_title(title)
        
        fig.colorbar(surf, ax=ax, shrink=0.5, aspect=5)
        plt.tight_layout()
        plt.show()