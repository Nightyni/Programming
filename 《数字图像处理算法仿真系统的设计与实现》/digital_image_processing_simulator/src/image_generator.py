
import numpy as np
import cv2
import sys

class ImageGenerator:
    """图像生成器 - 生成各种测试图像"""
    
    def __init__(self, size=(512, 512)):
        """
        初始化图像生成器
        
        参数:
            size: 图像尺寸 (高度, 宽度)，默认 (512, 512)
        """
        self.size = size
        self.height, self.width = size
        print(f"图像生成器初始化: 尺寸={size}")
    
    def generate_chessboard(self, square_size=64):
        """
        生成棋盘格图像
        
        参数:
            square_size: 每个方格的大小，默认64像素
            
        返回:
            numpy数组表示的图像
        """
        img = np.zeros(self.size, dtype=np.uint8)
        
        for i in range(0, self.height, square_size):
            for j in range(0, self.width, square_size):
                if ((i // square_size) + (j // square_size)) % 2 == 0:
                    img[i:i+square_size, j:j+square_size] = 255
        
        return img
    
    def generate_concentric_circles(self, num_circles=5, thickness=3):
        """
        生成同心圆图像
        
        参数:
            num_circles: 同心圆数量，默认5
            thickness: 圆线粗细，默认3像素
            
        返回:
            numpy数组表示的图像
        """
        img = np.zeros(self.size, dtype=np.uint8)
        center = (self.width // 2, self.height // 2)
        max_radius = min(self.width, self.height) // 2 - 10
        
        for i in range(1, num_circles + 1):
            radius = int(max_radius * i / num_circles)
            cv2.circle(img, center, radius, 255, thickness)
        
        return img
    
    def generate_gradient(self, direction='horizontal'):
        """
        生成渐变图像
        
        参数:
            direction: 渐变方向 'horizontal' 或 'vertical'，默认水平
            
        返回:
            numpy数组表示的图像
        """
        if direction == 'horizontal':
            # 水平渐变
            gradient = np.tile(np.linspace(0, 255, self.width).astype(np.uint8), 
                              (self.height, 1))
        else:
            # 垂直渐变
            gradient = np.tile(np.linspace(0, 255, self.height).astype(np.uint8),
                              (self.width, 1)).T
        
        return gradient
    
    def generate_random_shapes(self, min_shapes=5, max_shapes=10):
        """
        生成随机形状图像
        
        参数:
            min_shapes: 最小形状数量，默认5
            max_shapes: 最大形状数量，默认10
            
        返回:
            numpy数组表示的图像
        """
        try:
            # 尝试导入scikit-image
            from skimage.draw import random_shapes
            
            try:
                # 新版本API (channel_axis参数)
                image, _ = random_shapes(
                    self.size, 
                    min_shapes=min_shapes, 
                    max_shapes=max_shapes,
                    min_size=50,
                    max_size=150,
                    channel_axis=None  # 灰度图像
                )
            except TypeError:
                # 旧版本API (multichannel参数)
                image, _ = random_shapes(
                    self.size, 
                    min_shapes=min_shapes, 
                    max_shapes=max_shapes,
                    min_size=50,
                    max_size=150,
                    multichannel=False
                )
            
            return (image * 255).astype(np.uint8)
            
        except ImportError:
            # 如果scikit-image不可用，创建简单替代
            print("⚠️ scikit-image不可用，使用简单替代")
            return self._generate_simple_shapes()
    
    def _generate_simple_shapes(self):
        """生成简单形状（不依赖scikit-image）"""
        img = np.zeros(self.size, dtype=np.uint8)
        
        # 添加各种几何形状
        # 矩形
        cv2.rectangle(img, (50, 50), (150, 150), 100, -1)
        cv2.rectangle(img, (200, 100), (300, 200), 180, -1)
        
        # 圆形
        cv2.circle(img, (100, 300), 40, 150, -1)
        cv2.circle(img, (250, 350), 30, 200, -1)
        
        # 椭圆
        cv2.ellipse(img, (350, 100), (50, 30), 0, 0, 360, 120, -1)
        cv2.ellipse(img, (400, 250), (40, 60), 45, 0, 360, 80, -1)
        
        # 多边形
        pts = np.array([[300, 300], [350, 320], [340, 380], [280, 360]], np.int32)
        cv2.fillPoly(img, [pts], 220)
        
        return img
    
    def generate_test_pattern(self):
        """
        生成综合测试图案（彩色）
        
        返回:
            彩色图像 (RGB)
        """
        img = np.zeros((self.height, self.width, 3), dtype=np.uint8)
        
        # 添加彩色方块
        colors = [(255, 0, 0), (0, 255, 0), (0, 0, 255), (255, 255, 0)]
        for i, color in enumerate(colors):
            x1, y1 = 50 + i*100, 50
            x2, y2 = x1 + 80, y1 + 80
            cv2.rectangle(img, (x1, y1), (x2, y2), color, -1)
        
        # 添加渐变区域
        for i in range(100, 300):
            intensity = int((i - 100) / 200 * 255)
            cv2.line(img, (i, 200), (i, 300), (intensity, intensity, intensity), 1)
        
        # 添加文本
        font = cv2.FONT_HERSHEY_SIMPLEX
        cv2.putText(img, 'Digital Image', (100, 350), font, 1.2, (255, 255, 255), 2)
        cv2.putText(img, 'Processing', (150, 380), font, 1.2, (255, 255, 255), 2)
        
        return img
    
    def generate_text_image(self, text="Hello", font_scale=2):
        """
        生成文本图像
        
        参数:
            text: 显示的文本
            font_scale: 字体大小
            
        返回:
            文本图像
        """
        img = np.zeros(self.size, dtype=np.uint8)
        
        font = cv2.FONT_HERSHEY_SIMPLEX
        text_size = cv2.getTextSize(text, font, font_scale, 2)[0]
        
        # 计算文本位置（居中）
        text_x = (self.width - text_size[0]) // 2
        text_y = (self.height + text_size[1]) // 2
        
        cv2.putText(img, text, (text_x, text_y), font, font_scale, 255, 2)
        
        return img
    
    def generate_combined_test_image(self):
        """
        生成综合测试图像（包含多种特征）
        
        返回:
            综合测试图像
        """
        img = np.zeros(self.size, dtype=np.uint8)
        
        # 1. 棋盘格区域
        for i in range(0, 200, 40):
            for j in range(0, 200, 40):
                if ((i // 40) + (j // 40)) % 2 == 0:
                    img[i:i+40, j:j+40] = 100
        
        # 2. 渐变区域
        for i in range(200, 400):
            intensity = int((i - 200) / 200 * 255)
            img[i, 0:200] = intensity
        
        # 3. 随机点区域
        for _ in range(100):
            x = np.random.randint(300, 500)
            y = np.random.randint(300, 500)
            radius = np.random.randint(5, 15)
            intensity = np.random.randint(50, 200)
            cv2.circle(img, (x, y), radius, intensity, -1)
        
        # 4. 线条区域
        for i in range(0, 200, 20):
            cv2.line(img, (0, 200 + i), (200, 200 + i), 180, 2)
        
        return img
    
    def add_gaussian_noise(self, image, mean=0, sigma=25):
        """
        添加高斯噪声
        
        参数:
            image: 输入图像
            mean: 噪声均值，默认0
            sigma: 噪声标准差，默认25
            
        返回:
            添加噪声后的图像
        """
        if len(image.shape) == 3:
            h, w, c = image.shape
            noise = np.random.normal(mean, sigma, (h, w, c))
        else:
            h, w = image.shape
            noise = np.random.normal(mean, sigma, (h, w))
        
        noisy = image.astype(np.float32) + noise
        return np.clip(noisy, 0, 255).astype(np.uint8)
    
    def add_salt_pepper_noise(self, image, prob=0.05):
        """
        添加椒盐噪声
        
        参数:
            image: 输入图像
            prob: 噪声像素比例，默认0.05 (5%)
            
        返回:
            添加噪声后的图像
        """
        noisy = image.copy()
        
        if len(image.shape) == 3:
            h, w, c = image.shape
            mask = np.random.random((h, w, c)) < prob
        else:
            h, w = image.shape
            mask = np.random.random((h, w)) < prob
        
        salt = np.random.random(mask.shape) > 0.5
        noisy[mask & salt] = 255
        noisy[mask & ~salt] = 0
        
        return noisy
    
    def add_speckle_noise(self, image, sigma=0.1):
        """
        添加斑点噪声
        
        参数:
            image: 输入图像
            sigma: 噪声强度，默认0.1
            
        返回:
            添加噪声后的图像
        """
        noise = np.random.randn(*image.shape)
        noisy = image + image * noise * sigma
        return np.clip(noisy, 0, 255).astype(np.uint8)
    
    def add_poisson_noise(self, image):
        """
        添加泊松噪声
        
        参数:
            image: 输入图像
            
        返回:
            添加噪声后的图像
        """
        # 确保图像值在合理范围内
        image_normalized = image.astype(np.float32) / 255.0
        
        # 生成泊松噪声
        noisy = np.random.poisson(image_normalized * 10) / 10.0
        
        return np.clip(noisy * 255, 0, 255).astype(np.uint8)
    
    def add_uniform_noise(self, image, low=-20, high=20):
        """
        添加均匀分布噪声
        
        参数:
            image: 输入图像
            low: 噪声下界，默认-20
            high: 噪声上界，默认20
            
        返回:
            添加噪声后的图像
        """
        noise = np.random.uniform(low, high, image.shape)
        noisy = image.astype(np.float32) + noise
        return np.clip(noisy, 0, 255).astype(np.uint8)
    
    def add_periodic_noise(self, image, frequency=0.1, amplitude=30):
        """
        添加周期性噪声（条纹噪声）
        
        参数:
            image: 输入图像
            frequency: 频率，默认0.1
            amplitude: 振幅，默认30
            
        返回:
            添加噪声后的图像
        """
        h, w = image.shape[:2]
        
        # 创建正弦波噪声
        x = np.arange(w)
        y = np.arange(h)
        X, Y = np.meshgrid(x, y)
        
        noise = amplitude * np.sin(2 * np.pi * frequency * X)
        
        if len(image.shape) == 3:
            noise = np.repeat(noise[:, :, np.newaxis], 3, axis=2)
        
        noisy = image.astype(np.float32) + noise
        return np.clip(noisy, 0, 255).astype(np.uint8)
    
    def add_mixed_noise(self, image, gaussian_sigma=20, salt_pepper_prob=0.03):
        """
        添加混合噪声（高斯+椒盐）
        
        参数:
            image: 输入图像
            gaussian_sigma: 高斯噪声标准差，默认20
            salt_pepper_prob: 椒盐噪声概率，默认0.03
            
        返回:
            添加噪声后的图像
        """
        # 先添加高斯噪声
        noisy = self.add_gaussian_noise(image, sigma=gaussian_sigma)
        
        # 再添加椒盐噪声
        noisy = self.add_salt_pepper_noise(noisy, prob=salt_pepper_prob)
        
        return noisy
    
    def add_all_noises(self, clean_image):
        """
        添加所有类型的噪声
        
        参数:
            clean_image: 干净图像
            
        返回:
            字典，包含各种噪声图像
        """
        noises = {
            'gaussian': self.add_gaussian_noise(clean_image),
            'salt_pepper': self.add_salt_pepper_noise(clean_image),
            'speckle': self.add_speckle_noise(clean_image),
            'poisson': self.add_poisson_noise(clean_image),
            'uniform': self.add_uniform_noise(clean_image),
            'mixed': self.add_mixed_noise(clean_image)
        }
        
        return noises
    
    def add_noise_with_level(self, clean_image, noise_type='gaussian', level=0.05):
        """
        按等级添加噪声
        
        参数:
            clean_image: 干净图像
            noise_type: 噪声类型
            level: 噪声等级 (0-1)
            
        返回:
            添加噪声后的图像
        """
        if noise_type == 'gaussian':
            sigma = level * 50  # 最大sigma为50
            return self.add_gaussian_noise(clean_image, sigma=sigma)
        
        elif noise_type == 'salt_pepper':
            prob = level  # 概率直接使用level
            return self.add_salt_pepper_noise(clean_image, prob=prob)
        
        elif noise_type == 'speckle':
            sigma = level * 0.2  # 最大sigma为0.2
            return self.add_speckle_noise(clean_image, sigma=sigma)
        
        else:
            return clean_image.copy()
    
    def generate_all_test_images(self):
        """
        生成所有测试图像
        
        返回:
            字典，包含各种测试图像
        """
        images = {
            'chessboard': self.generate_chessboard(),
            'circles': self.generate_concentric_circles(),
            'gradient_horizontal': self.generate_gradient('horizontal'),
            'gradient_vertical': self.generate_gradient('vertical'),
            'random_shapes': self.generate_random_shapes(),
            'test_pattern': self.generate_test_pattern(),
            'text_image': self.generate_text_image("DIP"),
            'combined_test': self.generate_combined_test_image()
        }
        
        return images
    
    def get_image_statistics(self, image):
        """
        获取图像统计信息
        
        参数:
            image: 输入图像
            
        返回:
            字典，包含图像统计信息
        """
        stats = {
            'shape': image.shape,
            'dtype': str(image.dtype),
            'min': float(image.min()),
            'max': float(image.max()),
            'mean': float(image.mean()),
            'std': float(image.std())
        }
        
        if len(image.shape) == 2:
            stats['type'] = 'grayscale'
        else:
            stats['type'] = f'color ({image.shape[2]} channels)'
        
        return stats
    
    def create_noise_comparison(self, clean_image, noise_types=None):
        """
        创建噪声对比图
        
        参数:
            clean_image: 干净图像
            noise_types: 噪声类型列表
            
        返回:
            噪声对比图像
        """
        if noise_types is None:
            noise_types = ['gaussian', 'salt_pepper', 'speckle', 'poisson']
        
        # 计算布局
        num_noises = len(noise_types)
        cols = min(3, num_noises + 1)  # +1 用于原始图像
        rows = (num_noises + 1 + cols - 1) // cols
        
        # 计算每个图像的大小
        cell_height = self.height // rows
        cell_width = self.width // cols
        
        # 创建大图像
        comparison = np.zeros((rows * cell_height, cols * cell_width), dtype=np.uint8)
        
        # 添加原始图像
        resized_clean = cv2.resize(clean_image, (cell_width, cell_height))
        comparison[0:cell_height, 0:cell_width] = resized_clean
        
        # 添加标题
        title_img = np.zeros((30, cols * cell_width), dtype=np.uint8)
        cv2.putText(title_img, "Original", (10, 20), 
                   cv2.FONT_HERSHEY_SIMPLEX, 0.6, 255, 1)
        
        # 添加各种噪声图像
        for idx, noise_type in enumerate(noise_types):
            row = (idx + 1) // cols
            col = (idx + 1) % cols
            
            noisy = self.add_noise_with_level(clean_image, noise_type, 0.05)
            resized_noisy = cv2.resize(noisy, (cell_width, cell_height))
            
            start_row = row * cell_height
            start_col = col * cell_width
            comparison[start_row:start_row+cell_height, 
                      start_col:start_col+cell_width] = resized_noisy
            
            # 添加噪声类型标签
            label_y = start_row + 20
            label_x = start_col + 10
            cv2.putText(comparison, noise_type, (label_x, label_y),
                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, 255, 1)
        
        return comparison
    
    def save_images(self, images_dict, output_dir='output'):
        """
        保存图像到文件
        
        参数:
            images_dict: 图像字典 {名称: 图像}
            output_dir: 输出目录
            
        返回:
            保存的文件列表
        """
        import os
        os.makedirs(output_dir, exist_ok=True)
        
        saved_files = []
        
        for name, image in images_dict.items():
            # 清理文件名
            safe_name = name.replace(' ', '_').replace(':', '').replace('/', '_')
            filename = os.path.join(output_dir, f"{safe_name}.png")
            
            # 保存图像
            cv2.imwrite(filename, image)
            saved_files.append(filename)
            
            print(f"  保存: {filename}")
        
        return saved_files
    
    def demo(self):
        """
        运行演示
        
        返回:
            演示结果字典
        """
        print("=" * 60)
        print("图像生成器演示")
        print("=" * 60)
        
        # 生成测试图像
        print("\n1. 生成测试图像...")
        test_images = self.generate_all_test_images()
        
        for name, img in test_images.items():
            stats = self.get_image_statistics(img)
            print(f"   {name:20} - 尺寸: {stats['shape']}, 范围: [{stats['min']:.1f}, {stats['max']:.1f}]")
        
        # 添加噪声演示
        print("\n2. 噪声添加演示...")
        clean_img = test_images['chessboard']
        noises = self.add_all_noises(clean_img)
        
        for noise_type, noisy_img in noises.items():
            psnr = self.calculate_psnr(clean_img, noisy_img)
            print(f"   {noise_type:20} - PSNR: {psnr:.2f} dB")
        
        # 保存结果
        print("\n3. 保存结果...")
        all_images = {**test_images, **noises}
        saved_files = self.save_images(all_images)
        
        print(f"\n✅ 演示完成！保存了 {len(saved_files)} 张图像")
        
        return {
            'test_images': test_images,
            'noises': noises,
            'saved_files': saved_files
        }
    
    def calculate_psnr(self, img1, img2):
        """
        计算PSNR
        
        参数:
            img1: 图像1
            img2: 图像2
            
        返回:
            PSNR值
        """
        mse = np.mean((img1.astype(float) - img2.astype(float)) ** 2)
        if mse == 0:
            return float('inf')
        
        max_pixel = 255.0
        psnr = 20 * np.log10(max_pixel / np.sqrt(mse))
        return psnr


# 测试代码
if __name__ == "__main__":
    print("测试图像生成器...")
    
    # 创建图像生成器
    generator = ImageGenerator(size=(400, 400))
    
    # 运行演示
    results = generator.demo()
    
    print("\n🎉 图像生成器测试完成！")
    print(f"生成的图像保存在: output/ 目录")