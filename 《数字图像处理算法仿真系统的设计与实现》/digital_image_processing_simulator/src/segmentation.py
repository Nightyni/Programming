import cv2
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

import matplotlib.pyplot as plt

class ImageSegmentor:
    """图像分割算法"""
    
    def threshold_segmentation(self, image, method='otsu', threshold_value=127):
        """
        阈值分割
        
        参数:
            image: 输入图像
            method: 阈值方法 'otsu', 'adaptive', 'global', 'triangle'
            threshold_value: 全局阈值法的阈值，默认127
            
        返回:
            二值分割图像
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        if method == 'otsu':
            # Otsu阈值法
            _, binary = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
        elif method == 'adaptive':
            # 自适应阈值
            binary = cv2.adaptiveThreshold(gray, 255, 
                                          cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                          cv2.THRESH_BINARY, 11, 2)
        elif method == 'triangle':
            # 三角形阈值法
            _, binary = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_TRIANGLE)
        elif method == 'multi_otsu':
            # 多级Otsu阈值
            try:
                from skimage.filters import threshold_multiotsu
                thresholds = threshold_multiotsu(gray, classes=3)
                regions = np.digitize(gray, bins=thresholds)
                binary = (regions * 85).astype(np.uint8)  # 转换为3级灰度
            except ImportError:
                print("⚠️ scikit-image不可用，使用普通Otsu")
                _, binary = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
        else:
            # 全局阈值
            _, binary = cv2.threshold(gray, threshold_value, 255, cv2.THRESH_BINARY)
        
        return binary
    
    def kmeans_segmentation(self, image, k=3, max_iter=100):
        """
        K-means聚类分割
        
        参数:
            image: 输入图像
            k: 聚类数量，默认3
            max_iter: 最大迭代次数，默认100
            
        返回:
            聚类分割图像
        """
        # 将图像转换为一维数组
        if len(image.shape) == 3:
            # 彩色图像，使用颜色特征
            pixels = image.reshape(-1, 3).astype(np.float32)
        else:
            # 灰度图像，添加空间特征
            h, w = image.shape
            x_coords = np.tile(np.arange(w), h).reshape(-1, 1)
            y_coords = np.repeat(np.arange(h), w).reshape(-1, 1)
            intensity = image.reshape(-1, 1).astype(np.float32)
            pixels = np.hstack([intensity, x_coords / w, y_coords / h])
        
        # 应用K-means
        kmeans = KMeans(n_clusters=k, random_state=42, max_iter=max_iter, n_init=10)
        labels = kmeans.fit_predict(pixels)
        
        # 重新构造图像
        if len(image.shape) == 3:
            segmented = kmeans.cluster_centers_[labels].reshape(image.shape).astype(np.uint8)
        else:
            # 对于灰度图像，只使用强度信息
            segmented = kmeans.cluster_centers_[labels, 0].reshape(image.shape).astype(np.uint8)
        
        return segmented, labels.reshape(image.shape[:2])
    
    def watershed_segmentation(self, image):
        """
        分水岭分割
        
        参数:
            image: 输入图像
            
        返回:
            分水岭分割结果
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        # 噪声去除
        blurred = cv2.medianBlur(gray, 5)
        
        # 阈值处理
        _, thresh = cv2.threshold(blurred, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
        
        # 形态学操作去除噪声
        kernel = np.ones((3,3), np.uint8)
        opening = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, kernel, iterations=2)
        
        # 确定背景区域
        sure_bg = cv2.dilate(opening, kernel, iterations=3)
        
        # 距离变换
        dist_transform = cv2.distanceTransform(opening, cv2.DIST_L2, 5)
        _, sure_fg = cv2.threshold(dist_transform, 0.7*dist_transform.max(), 255, 0)
        
        # 找到未知区域
        sure_fg = np.uint8(sure_fg)
        unknown = cv2.subtract(sure_bg, sure_fg)
        
        # 标记连通区域
        _, markers = cv2.connectedComponents(sure_fg)
        
        # 为分水岭算法增加标记
        markers = markers + 1
        markers[unknown == 255] = 0
        
        # 应用分水岭算法
        if len(image.shape) == 3:
            markers = cv2.watershed(image, markers)
            segmented = image.copy()
            segmented[markers == -1] = [255, 0, 0]  # 标记边界为红色
        else:
            # 灰度图像需要转为彩色
            color_image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)
            markers = cv2.watershed(color_image, markers)
            color_image[markers == -1] = [255, 0, 0]
            segmented = color_image
        
        return segmented, markers
    
    def region_growing(self, image, seed_points=None, threshold=10):
        """
        区域生长算法
        
        参数:
            image: 输入图像
            seed_points: 种子点列表，如果为None则使用图像中心
            threshold: 生长阈值，默认10
            
        返回:
            区域生长结果
        """
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image.copy()
        
        height, width = gray.shape
        segmented = np.zeros_like(gray)
        
        # 如果没有提供种子点，使用图像中心
        if seed_points is None:
            seed_points = [(width//2, height//2)]
        
        # 8邻域
        neighbors = [(-1,-1), (-1,0), (-1,1),
                    (0,-1),         (0,1),
                    (1,-1),  (1,0),  (1,1)]
        
        # 对每个种子点进行区域生长
        for seed_point in seed_points:
            if not (0 <= seed_point[0] < width and 0 <= seed_point[1] < height):
                continue
                
            seed_value = gray[seed_point[1], seed_point[0]]
            
            # 待检查点
            points_to_check = [seed_point]
            segmented[seed_point[1], seed_point[0]] = 255
            
            while points_to_check:
                x, y = points_to_check.pop(0)
                
                for dx, dy in neighbors:
                    nx, ny = x + dx, y + dy
                    
                    if 0 <= nx < width and 0 <= ny < height:
                        if segmented[ny, nx] == 0:  # 未访问
                            pixel_value = gray[ny, nx]
                            
                            if abs(int(pixel_value) - int(seed_value)) < threshold:
                                segmented[ny, nx] = 255
                                points_to_check.append((nx, ny))
        
        return segmented
    
    def mean_shift_segmentation(self, image, spatial_radius=10, color_radius=10, max_level=2):
        """
        Mean Shift分割（使用OpenCV的pyrMeanShiftFiltering）
        
        参数:
            image: 输入图像
            spatial_radius: 空间半径
            color_radius: 颜色半径
            max_level: 金字塔最大层数
            
        返回:
            Mean Shift分割结果
        """
        if len(image.shape) != 3:
            # 灰度图像转为彩色
            image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)
        
        # 应用Mean Shift
        shifted = cv2.pyrMeanShiftFiltering(image, spatial_radius, color_radius, max_level)
        
        # 转换为灰度并阈值化
        gray = cv2.cvtColor(shifted, cv2.COLOR_BGR2GRAY)
        _, binary = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
        
        return shifted, binary
    
    def apply_all_segmentation(self, image):
        """
        应用所有分割算法
        
        参数:
            image: 输入图像
            
        返回:
            字典，包含各种分割结果
        """
        results = {
            '原始图像': image,
            'Otsu阈值分割': self.threshold_segmentation(image, 'otsu'),
            '自适应阈值分割': self.threshold_segmentation(image, 'adaptive'),
            '三角形阈值分割': self.threshold_segmentation(image, 'triangle'),
        }
        
        try:
            # K-means聚类
            kmeans_result, _ = self.kmeans_segmentation(image, 3)
            results['K-means聚类(K=3)'] = kmeans_result
            
            kmeans_result2, _ = self.kmeans_segmentation(image, 5)
            results['K-means聚类(K=5)'] = kmeans_result2
            
            # 分水岭分割
            watershed_result, _ = self.watershed_segmentation(image)
            results['分水岭分割'] = watershed_result
            
            # 区域生长
            height, width = image.shape[:2]
            seed_points = [(width//3, height//3), (2*width//3, 2*height//3)]
            region_growing_result = self.region_growing(image, seed_points)
            results['区域生长'] = region_growing_result
            
            # Mean Shift分割
            mean_shift_result, mean_shift_binary = self.mean_shift_segmentation(image)
            results['Mean Shift分割'] = mean_shift_result
            results['Mean Shift二值化'] = mean_shift_binary
            
        except Exception as e:
            print(f"⚠️ 部分分割算法执行失败: {e}")
        
        return results
    
    def calculate_segmentation_metrics(self, segmentation_result, ground_truth=None):
        """
        计算分割评价指标
        
        参数:
            segmentation_result: 分割结果
            ground_truth: 真实标注（如果可用）
            
        返回:
            评价指标字典
        """
        metrics = {}
        
        # 如果没有真实标注，计算一些基本指标
        if ground_truth is None:
            # 计算分割区域的均匀性
            if len(segmentation_result.shape) == 3:
                gray_seg = cv2.cvtColor(segmentation_result, cv2.COLOR_BGR2GRAY)
            else:
                gray_seg = segmentation_result.copy()
            
            # 计算区域数量（通过连通组件）
            _, labels = cv2.connectedComponents(gray_seg)
            metrics['region_count'] = labels.max()
            
            # 计算区域大小变异系数
            region_sizes = []
            for i in range(1, metrics['region_count'] + 1):
                size = np.sum(labels == i)
                if size > 0:
                    region_sizes.append(size)
            
            if region_sizes:
                region_sizes = np.array(region_sizes)
                metrics['avg_region_size'] = np.mean(region_sizes)
                metrics['std_region_size'] = np.std(region_sizes)
                metrics['cv_region_size'] = metrics['std_region_size'] / metrics['avg_region_size'] if metrics['avg_region_size'] > 0 else 0
            else:
                metrics['avg_region_size'] = 0
                metrics['std_region_size'] = 0
                metrics['cv_region_size'] = 0
        
        return metrics