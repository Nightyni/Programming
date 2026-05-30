import cv2
import numpy as np
from scipy import ndimage

class EdgeDetector:
    """边缘检测算法对比"""
    
    def __init__(self):
        """
        初始化边缘检测器
        定义各种边缘检测算子
        """
        # Sobel算子内核
        self.sobel_x = np.array([[-1, 0, 1],
                                 [-2, 0, 2],
                                 [-1, 0, 1]])
        
        self.sobel_y = np.array([[-1, -2, -1],
                                 [0,  0,  0],
                                 [1,  2,  1]])
        
        # Prewitt算子内核
        self.prewitt_x = np.array([[-1, 0, 1],
                                   [-1, 0, 1],
                                   [-1, 0, 1]])
        
        self.prewitt_y = np.array([[-1, -1, -1],
                                   [0,  0,  0],
                                   [1,  1,  1]])
        
        # Laplacian算子内核
        self.laplacian_4 = np.array([[0,  1, 0],
                                     [1, -4, 1],
                                     [0,  1, 0]])
        
        self.laplacian_8 = np.array([[1,  1, 1],
                                     [1, -8, 1],
                                     [1,  1, 1]])
    
    def sobel_edges(self, image, convert_to_uint8=True):
        """
        Sobel边缘检测
        
        参数:
            image: 输入图像
            convert_to_uint8: 是否转换为8位图像
            
        返回:
            梯度幅值图像，梯度x方向，梯度y方向
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        # 使用OpenCV的Sobel函数
        grad_x = cv2.Sobel(gray, cv2.CV_64F, 1, 0, ksize=3)
        grad_y = cv2.Sobel(gray, cv2.CV_64F, 0, 1, ksize=3)
        
        # 计算梯度幅值
        magnitude = np.sqrt(grad_x**2 + grad_y**2)
        
        # 计算梯度方向
        direction = np.arctan2(grad_y, grad_x) * 180 / np.pi
        
        if convert_to_uint8:
            magnitude = np.uint8(np.clip(magnitude, 0, 255))
        
        return magnitude, grad_x, grad_y, direction
    
    def prewitt_edges(self, image, convert_to_uint8=True):
        """
        Prewitt边缘检测
        
        参数:
            image: 输入图像
            convert_to_uint8: 是否转换为8位图像
            
        返回:
            Prewitt边缘检测结果
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        # 使用卷积计算
        grad_x = ndimage.convolve(gray.astype(float), self.prewitt_x)
        grad_y = ndimage.convolve(gray.astype(float), self.prewitt_y)
        
        magnitude = np.sqrt(grad_x**2 + grad_y**2)
        
        if convert_to_uint8:
            magnitude = np.uint8(np.clip(magnitude, 0, 255))
        
        return magnitude
    
    def laplacian_edges(self, image, kernel_type='4邻域'):
        """
        Laplacian边缘检测
        
        参数:
            image: 输入图像
            kernel_type: '4邻域' 或 '8邻域'
            
        返回:
            Laplacian边缘检测结果
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        if kernel_type == '4邻域':
            laplacian = cv2.filter2D(gray, cv2.CV_64F, self.laplacian_4)
        else:
            laplacian = cv2.filter2D(gray, cv2.CV_64F, self.laplacian_8)
        
        laplacian = np.uint8(np.clip(np.abs(laplacian), 0, 255))
        return laplacian
    
    def canny_edges(self, image, low_threshold=50, high_threshold=150, aperture_size=3):
        """
        Canny边缘检测
        
        参数:
            image: 输入图像
            low_threshold: 低阈值
            high_threshold: 高阈值
            aperture_size: Sobel算子孔径大小
            
        返回:
            Canny边缘检测结果
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        # 应用高斯滤波去噪
        blurred = cv2.GaussianBlur(gray, (5, 5), 1.4)
        
        # Canny边缘检测
        edges = cv2.Canny(blurred, low_threshold, high_threshold, apertureSize=aperture_size)
        
        return edges
    
    def roberts_edges(self, image):
        """
        Roberts边缘检测
        
        参数:
            image: 输入图像
            
        返回:
            Roberts边缘检测结果
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        # Roberts算子
        roberts_cross_v = np.array([[1, 0],
                                    [0, -1]])
        
        roberts_cross_h = np.array([[0, 1],
                                    [-1, 0]])
        
        vertical = ndimage.convolve(gray.astype(float), roberts_cross_v)
        horizontal = ndimage.convolve(gray.astype(float), roberts_cross_h)
        
        magnitude = np.sqrt(vertical**2 + horizontal**2)
        return np.uint8(np.clip(magnitude, 0, 255))
    
    def scharr_edges(self, image):
        """
        Scharr边缘检测
        
        参数:
            image: 输入图像
            
        返回:
            Scharr边缘检测结果
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        grad_x = cv2.Scharr(gray, cv2.CV_64F, 1, 0)
        grad_y = cv2.Scharr(gray, cv2.CV_64F, 0, 1)
        
        magnitude = np.sqrt(grad_x**2 + grad_y**2)
        return np.uint8(np.clip(magnitude, 0, 255))
    
    def detect_all_edges(self, image):
        """
        应用所有边缘检测算法
        
        参数:
            image: 输入图像
            
        返回:
            字典，包含各种边缘检测结果
        """
        results = {}
        
        # 获取灰度图像用于显示
        if len(image.shape) == 3:
            gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray_image = image.copy()
        
        results['原始图像'] = gray_image
        
        # Sobel
        sobel_mag, _, _, _ = self.sobel_edges(image)
        results['Sobel算子'] = sobel_mag
        
        # Prewitt
        results['Prewitt算子'] = self.prewitt_edges(image)
        
        # Laplacian (4邻域)
        results['Laplacian(4邻域)'] = self.laplacian_edges(image, '4邻域')
        
        # Laplacian (8邻域)
        results['Laplacian(8邻域)'] = self.laplacian_edges(image, '8邻域')
        
        # Canny
        results['Canny算子'] = self.canny_edges(image)
        
        # Roberts
        results['Roberts算子'] = self.roberts_edges(image)
        
        # Scharr
        results['Scharr算子'] = self.scharr_edges(image)
        
        return results
    
    def evaluate_edge_detection(self, clean_image, noisy_image, edge_method='canny'):
        """
        评估边缘检测效果
        
        参数:
            clean_image: 干净图像
            noisy_image: 噪声图像
            edge_method: 边缘检测方法
            
        返回:
            包含评估结果的字典
        """
        # 获取干净图像的边缘（作为参考）
        if edge_method == 'canny':
            clean_edges = self.canny_edges(clean_image)
            noisy_edges = self.canny_edges(noisy_image)
        elif edge_method == 'sobel':
            clean_edges = self.sobel_edges(clean_image)[0]
            noisy_edges = self.sobel_edges(noisy_image)[0]
        elif edge_method == 'laplacian':
            clean_edges = self.laplacian_edges(clean_image)
            noisy_edges = self.laplacian_edges(noisy_image)
        else:
            clean_edges = self.prewitt_edges(clean_image)
            noisy_edges = self.prewitt_edges(noisy_image)
        
        # 计算边缘保持度
        edge_pixels_clean = np.sum(clean_edges > 0)
        edge_pixels_noisy = np.sum(noisy_edges > 0)
        
        if edge_pixels_clean > 0:
            edge_preservation = np.sum((clean_edges > 0) & (noisy_edges > 0)) / edge_pixels_clean
        else:
            edge_preservation = 0
        
        # 计算误检率
        if edge_pixels_noisy > 0:
            false_alarm = np.sum((clean_edges == 0) & (noisy_edges > 0)) / edge_pixels_noisy
        else:
            false_alarm = 0
        
        return {
            'clean_edges': clean_edges,
            'noisy_edges': noisy_edges,
            'edge_preservation': edge_preservation,
            'false_alarm_rate': false_alarm,
            'edge_pixels_clean': edge_pixels_clean,
            'edge_pixels_noisy': edge_pixels_noisy
        }