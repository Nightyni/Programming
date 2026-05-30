import cv2
import numpy as np
from skimage import exposure

class ImageEnhancer:
    """图像增强算法"""
    
    def histogram_equalization(self, image):
        """
        直方图均衡化
        
        参数:
            image: 输入图像
            
        返回:
            直方图均衡化后的图像
        """
        if len(image.shape) == 3:
            # 彩色图像 - 转换到YUV空间，对Y通道均衡化
            img_yuv = cv2.cvtColor(image, cv2.COLOR_BGR2YUV)
            img_yuv[:,:,0] = cv2.equalizeHist(img_yuv[:,:,0])
            enhanced = cv2.cvtColor(img_yuv, cv2.COLOR_YUV2BGR)
        else:
            # 灰度图像
            enhanced = cv2.equalizeHist(image)
        
        return enhanced
    
    def clahe_enhancement(self, image, clip_limit=2.0, grid_size=(8,8)):
        """
        对比度受限的自适应直方图均衡化
        
        参数:
            image: 输入图像
            clip_limit: 对比度限制，默认2.0
            grid_size: 网格大小，默认(8,8)
            
        返回:
            CLAHE增强后的图像
        """
        clahe = cv2.createCLAHE(clipLimit=clip_limit, tileGridSize=grid_size)
        
        if len(image.shape) == 3:
            # 彩色图像 - 转换到LAB空间
            lab = cv2.cvtColor(image, cv2.COLOR_BGR2LAB)
            lab[:,:,0] = clahe.apply(lab[:,:,0])
            enhanced = cv2.cvtColor(lab, cv2.COLOR_LAB2BGR)
        else:
            enhanced = clahe.apply(image)
        
        return enhanced
    
    def gamma_correction(self, image, gamma=1.5):
        """
        伽马校正
        
        参数:
            image: 输入图像
            gamma: 伽马值，默认1.5
            
        返回:
            伽马校正后的图像
        """
        # 归一化
        img_normalized = image.astype(np.float32) / 255.0
        
        # 伽马校正
        corrected = np.power(img_normalized, gamma)
        
        # 恢复范围
        corrected = (corrected * 255).astype(np.uint8)
        return corrected
    
    def log_transform(self, image, c=1):
        """
        对数变换
        
        参数:
            image: 输入图像
            c: 缩放常数，默认1
            
        返回:
            对数变换后的图像
        """
        img_float = image.astype(np.float32)
        
        # 避免log(0)
        img_float[img_float == 0] = 1
        
        # 对数变换
        enhanced = c * np.log(1 + img_float)
        
        # 归一化到0-255
        enhanced = (enhanced / enhanced.max() * 255).astype(np.uint8)
        return enhanced
    
    def contrast_stretching(self, image, low_percent=2, high_percent=98):
        """
        对比度拉伸
        
        参数:
            image: 输入图像
            low_percent: 低百分比，默认2
            high_percent: 高百分比，默认98
            
        返回:
            对比度拉伸后的图像
        """
        if len(image.shape) == 3:
            # 对每个通道分别处理
            channels = []
            for i in range(3):
                channel = image[:,:,i]
                p_low, p_high = np.percentile(channel, (low_percent, high_percent))
                stretched = exposure.rescale_intensity(channel, 
                                                      in_range=(p_low, p_high),
                                                      out_range=(0, 255))
                channels.append(stretched)
            enhanced = np.stack(channels, axis=-1).astype(np.uint8)
        else:
            p_low, p_high = np.percentile(image, (low_percent, high_percent))
            enhanced = exposure.rescale_intensity(image, 
                                                 in_range=(p_low, p_high),
                                                 out_range=(0, 255)).astype(np.uint8)
        
        return enhanced
    
    def sharpening(self, image, method='laplacian', strength=0.5):
        """
        图像锐化
        
        参数:
            image: 输入图像
            method: 锐化方法 'laplacian', 'unsharp', 'highboost'
            strength: 锐化强度，默认0.5
            
        返回:
            锐化后的图像
        """
        if method == 'laplacian':
            # Laplacian锐化
            kernel = np.array([[0, -1, 0],
                               [-1, 5, -1],
                               [0, -1, 0]])
            sharpened = cv2.filter2D(image, -1, kernel)
        elif method == 'unsharp':
            # 非锐化掩蔽
            blurred = cv2.GaussianBlur(image, (0, 0), 3)
            sharpened = cv2.addWeighted(image, 1.0 + strength, blurred, -strength, 0)
        else:
            # 高提升滤波
            blurred = cv2.GaussianBlur(image, (0, 0), 3)
            sharpened = cv2.addWeighted(image, 1.5, blurred, -0.5, 0)
        
        return np.clip(sharpened, 0, 255).astype(np.uint8)
    
    def retinex_enhancement(self, image, sigma_list=[15, 80, 250]):
        """
        Retinex增强算法（简化版）
        
        参数:
            image: 输入图像
            sigma_list: 高斯核标准差列表
            
        返回:
            Retinex增强后的图像
        """
        if len(image.shape) == 3:
            # 彩色图像，对每个通道分别处理
            channels = cv2.split(image)
            enhanced_channels = []
            
            for channel in channels:
                retinex = np.zeros_like(channel, dtype=np.float32)
                
                for sigma in sigma_list:
                    # 高斯模糊
                    blurred = cv2.GaussianBlur(channel.astype(np.float32), (0, 0), sigma)
                    # 计算单尺度Retinex
                    retinex += np.log(channel.astype(np.float32) + 1) - np.log(blurred + 1)
                
                # 多尺度平均
                retinex = retinex / len(sigma_list)
                
                # 归一化
                retinex = (retinex - retinex.min()) / (retinex.max() - retinex.min()) * 255
                enhanced_channels.append(retinex.astype(np.uint8))
            
            enhanced = cv2.merge(enhanced_channels)
        else:
            # 灰度图像
            retinex = np.zeros_like(image, dtype=np.float32)
            
            for sigma in sigma_list:
                blurred = cv2.GaussianBlur(image.astype(np.float32), (0, 0), sigma)
                retinex += np.log(image.astype(np.float32) + 1) - np.log(blurred + 1)
            
            retinex = retinex / len(sigma_list)
            retinex = (retinex - retinex.min()) / (retinex.max() - retinex.min()) * 255
            enhanced = retinex.astype(np.uint8)
        
        return enhanced
    
    def apply_all_enhancements(self, image):
        """
        应用所有增强算法
        
        参数:
            image: 输入图像
            
        返回:
            字典，包含各种增强结果
        """
        results = {
            '原始图像': image,
            '直方图均衡化': self.histogram_equalization(image),
            '自适应直方图均衡化': self.clahe_enhancement(image),
            '伽马校正(γ=1.5)': self.gamma_correction(image, 1.5),
            '伽马校正(γ=0.5)': self.gamma_correction(image, 0.5),
            '对数变换': self.log_transform(image),
            '对比度拉伸': self.contrast_stretching(image),
            '图像锐化': self.sharpening(image),
            'Retinex增强': self.retinex_enhancement(image)
        }
        
        return results
    
    def calculate_contrast(self, image):
        """
        计算图像对比度
        
        参数:
            image: 输入图像
            
        返回:
            对比度值
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        # 计算局部对比度（标准差）
        contrast = np.std(gray)
        return contrast
    
    def calculate_entropy(self, image):
        """
        计算图像熵
        
        参数:
            image: 输入图像
            
        返回:
            熵值
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        # 计算直方图
        hist = cv2.calcHist([gray], [0], None, [256], [0, 256])
        hist = hist.ravel() / hist.sum()
        
        # 计算熵
        entropy = -np.sum(hist * np.log2(hist + 1e-10))
        return entropy