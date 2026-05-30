import cv2
import numpy as np
from scipy import ndimage

class FilterComparison:
    """滤波算法对比"""
    
    def apply_mean_filter(self, image, kernel_size=5):
        """均值滤波"""
        return cv2.blur(image, (kernel_size, kernel_size))
    
    def apply_gaussian_filter(self, image, kernel_size=5, sigma=1.0):
        """高斯滤波"""
        return cv2.GaussianBlur(image, (kernel_size, kernel_size), sigma)
    
    def apply_median_filter(self, image, kernel_size=5):
        """中值滤波"""
        return cv2.medianBlur(image, kernel_size)
    
    def apply_bilateral_filter(self, image, d=9, sigma_color=75, sigma_space=75):
        """双边滤波"""
        return cv2.bilateralFilter(image, d, sigma_color, sigma_space)
    
    def apply_wavelet_denoise(self, image):
        """小波去噪（简化版）"""
        # 使用小波变换进行去噪
        import pywt
        
        if len(image.shape) == 3:
            # 彩色图像，对每个通道分别处理
            channels = cv2.split(image)
            denoised_channels = []
            
            for channel in channels:
                coeffs = pywt.wavedec2(channel, 'db1', level=2)
                # 阈值处理
                coeffs_thresh = [coeffs[0]]  # 保留近似系数
                for i in range(1, len(coeffs)):
                    coeffs_thresh.append(
                        tuple(pywt.threshold(c, np.std(c)*0.5, 'soft') for c in coeffs[i])
                    )
                denoised = pywt.waverec2(coeffs_thresh, 'db1')
                denoised_channels.append(denoised)
            
            denoised = cv2.merge(denoised_channels)
        else:
            # 灰度图像
            coeffs = pywt.wavedec2(image, 'db1', level=2)
            # 阈值处理
            coeffs_thresh = [coeffs[0]]
            for i in range(1, len(coeffs)):
                coeffs_thresh.append(
                    tuple(pywt.threshold(c, np.std(c)*0.5, 'soft') for c in coeffs[i])
                )
            denoised = pywt.waverec2(coeffs_thresh, 'db1')
        
        return np.clip(denoised, 0, 255).astype(np.uint8)
    
    def apply_nlm_filter(self, image, h=10, template_window=7, search_window=21):
        """非局部均值去噪"""
        return cv2.fastNlMeansDenoising(image, None, h, template_window, search_window)
    
    def apply_all_filters(self, noisy_image):
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
            return {'原始图像': noisy_image if noisy_image is not None else np.zeros((100, 100), dtype=np.uint8)}

        