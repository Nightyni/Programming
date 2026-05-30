#!/usr/bin/env python3
"""
文件工具模块 - 安全加载和处理图像文件
用于解决图像加载问题
"""
import os
import cv2
import numpy as np
import logging
from pathlib import Path
import sys

# 设置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

def setup_chinese_font():
    """设置中文字体支持"""
    try:
        import matplotlib
        import matplotlib.font_manager as fm
        import platform
        
        # 设置matplotlib参数
        matplotlib.rcParams['axes.unicode_minus'] = False
        
        # 根据系统选择字体
        system = platform.system()
        if system == "Windows":
            font_candidates = ['Microsoft YaHei', 'SimHei', 'SimSun']
        elif system == "Darwin":  # macOS
            font_candidates = ['Arial Unicode MS', 'Hiragino Sans GB']
        else:  # Linux
            font_candidates = ['DejaVu Sans', 'WenQuanYi Zen Hei']
        
        # 查找可用字体
        available_fonts = set([f.name for f in fm.fontManager.ttflist])
        for font in font_candidates:
            if font in available_fonts:
                matplotlib.rcParams['font.sans-serif'] = [font]
                matplotlib.rcParams['font.family'] = 'sans-serif'
                logger.info(f"已设置中文字体: {font}")
                return
        
        logger.warning("未找到中文字体，使用默认字体")
    except Exception as e:
        logger.error(f"设置中文字体失败: {e}")

def safe_load_image(file_path, max_size_mb=50, max_dimension=4000, try_pil=True, try_decode=True):
    """
    安全加载图像文件，支持各种格式和编码问题
    
    参数:
        file_path: 图像文件路径
        max_size_mb: 最大文件大小(MB)
        max_dimension: 最大图像尺寸
        try_pil: 是否尝试使用PIL库
        try_decode: 是否尝试二进制解码
        
    返回:
        (图像numpy数组, 错误信息)
        成功: (image, None)
        失败: (None, error_message)
    """
    try:
        # 1. 检查文件是否存在
        if not os.path.exists(file_path):
            return None, f"文件不存在: {file_path}"
        
        # 2. 检查文件大小
        file_size = os.path.getsize(file_path)
        if file_size == 0:
            return None, "文件为空 (0字节)"
        
        if file_size > max_size_mb * 1024 * 1024:
            size_mb = file_size / (1024 * 1024)
            return None, f"文件过大 ({size_mb:.1f}MB > {max_size_mb}MB)"
        
        # 3. 检查文件扩展名
        file_ext = Path(file_path).suffix.lower()
        supported_ext = {'.jpg', '.jpeg', '.png', '.bmp', '.tiff', '.tif', '.webp', '.gif'}
        
        if file_ext not in supported_ext:
            logger.warning(f"不支持的扩展名: {file_ext}")
        
        # 4. 尝试读取文件头，判断格式
        file_format = detect_image_format(file_path)
        logger.info(f"文件格式检测: {file_format}")
        
        # 5. 尝试多种方法读取图像
        img = None
        
        # 方法1: cv2.imread (最常用)
        img = cv2.imread(file_path, cv2.IMREAD_COLOR)
        if img is not None:
            logger.info(f"使用cv2.imread成功读取: {Path(file_path).name}")
        
        # 方法2: 如果失败，尝试灰度模式
        if img is None:
            img = cv2.imread(file_path, cv2.IMREAD_GRAYSCALE)
            if img is not None:
                # 转换为3通道
                img = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
                logger.info(f"使用cv2.imread(灰度)成功读取: {Path(file_path).name}")
        
        # 方法3: 尝试不同的颜色模式
        if img is None:
            for mode in [cv2.IMREAD_UNCHANGED, cv2.IMREAD_ANYCOLOR, cv2.IMREAD_ANYDEPTH]:
                img = cv2.imread(file_path, mode)
                if img is not None:
                    logger.info(f"使用cv2.imread(模式{mode})成功读取: {Path(file_path).name}")
                    break
        
        # 方法4: 使用PIL库（如果需要）
        if img is None and try_pil:
            img = load_image_with_pil(file_path)
        
        # 方法5: 读取二进制并解码
        if img is None and try_decode:
            img = load_image_with_decode(file_path)
        
        if img is None:
            return None, f"所有方法都无法读取图像: {Path(file_path).name}"
        
        # 6. 验证图像数据
        if not validate_image_data(img):
            return None, "图像数据验证失败"
        
        # 7. 调整图像大小（如果需要）
        img = resize_image_if_needed(img, max_dimension)
        
        logger.info(f"成功加载图像: {Path(file_path).name} ({img.shape[1]}x{img.shape[0]})")
        return img, None
        
    except Exception as e:
        error_msg = f"加载图像时发生错误: {str(e)}"
        logger.error(error_msg)
        return None, error_msg

def detect_image_format(file_path):
    """
    检测图像文件格式
    
    返回:
        格式名称字符串
    """
    try:
        with open(file_path, 'rb') as f:
            header = f.read(16)  # 读取前16字节
            
        # 常见图像格式的文件头
        if header.startswith(b'\xff\xd8\xff'):
            return "JPEG"
        elif header.startswith(b'\x89PNG\r\n\x1a\n'):
            return "PNG"
        elif header.startswith(b'BM'):
            return "BMP"
        elif header.startswith(b'II*\x00') or header.startswith(b'MM\x00*'):
            return "TIFF"
        elif header.startswith(b'GIF87a') or header.startswith(b'GIF89a'):
            return "GIF"
        elif header.startswith(b'RIFF') and header[8:12] == b'WEBP':
            return "WEBP"
        else:
            return "未知格式"
    except:
        return "无法检测"

def load_image_with_pil(file_path):
    """
    使用PIL库加载图像
    
    返回:
        图像numpy数组 或 None
    """
    try:
        from PIL import Image, ImageFile
        
        # 允许加载截断的图像
        ImageFile.LOAD_TRUNCATED_IMAGES = True
        
        pil_img = Image.open(file_path)
        
        # 转换图像模式
        if pil_img.mode == 'RGBA':
            # 创建白色背景
            background = Image.new('RGB', pil_img.size, (255, 255, 255))
            background.paste(pil_img, mask=pil_img.split()[3])
            pil_img = background
        elif pil_img.mode != 'RGB':
            pil_img = pil_img.convert('RGB')
        
        img_array = np.array(pil_img)
        
        # PIL图像是RGB，OpenCV需要BGR
        img = cv2.cvtColor(img_array, cv2.COLOR_RGB2BGR)
        
        logger.info(f"使用PIL成功读取: {Path(file_path).name}")
        return img
    except ImportError:
        logger.warning("PIL库未安装，无法使用此方法")
        return None
    except Exception as e:
        logger.error(f"PIL读取失败: {e}")
        return None

def load_image_with_decode(file_path):
    """
    读取二进制数据并解码
    
    返回:
        图像numpy数组 或 None
    """
    try:
        with open(file_path, 'rb') as f:
            img_bytes = f.read()
        
        img_array = np.frombuffer(img_bytes, np.uint8)
        
        # 尝试多种解码标志
        decode_flags = [
            cv2.IMREAD_COLOR,
            cv2.IMREAD_GRAYSCALE,
            cv2.IMREAD_UNCHANGED,
            cv2.IMREAD_ANYCOLOR,
            cv2.IMREAD_ANYDEPTH,
            cv2.IMREAD_IGNORE_ORIENTATION
        ]
        
        for flag in decode_flags:
            img = cv2.imdecode(img_array, flag)
            if img is not None:
                if len(img.shape) == 2:  # 灰度图
                    img = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
                logger.info(f"使用imdecode(标志{flag})成功读取: {Path(file_path).name}")
                return img
        
        return None
    except Exception as e:
        logger.error(f"二进制解码失败: {e}")
        return None

def validate_image_data(img):
    """
    验证图像数据是否有效
    
    返回:
        True 或 False
    """
    if img is None:
        return False
    
    # 检查图像尺寸
    if img.shape[0] == 0 or img.shape[1] == 0:
        logger.error("图像尺寸为0")
        return False
    
    # 检查数据类型
    if img.dtype not in [np.uint8, np.uint16, np.float32]:
        logger.error(f"不支持的数据类型: {img.dtype}")
        return False
    
    # 检查NaN或Inf值
    if np.any(np.isnan(img)) or np.any(np.isinf(img)):
        logger.error("图像包含NaN或Inf值")
        return False
    
    return True

def resize_image_if_needed(img, max_dimension):
    """
    如果需要，调整图像大小
    
    返回:
        调整后的图像
    """
    height, width = img.shape[:2]
    
    if max(height, width) > max_dimension:
        scale = max_dimension / max(height, width)
        new_width = int(width * scale)
        new_height = int(height * scale)
        
        # 选择插值方法
        if scale < 0.5:
            interpolation = cv2.INTER_AREA  # 缩小
        else:
            interpolation = cv2.INTER_LINEAR  # 放大
        
        img_resized = cv2.resize(img, (new_width, new_height), interpolation=interpolation)
        
        logger.info(f"调整大小: {width}x{height} → {new_width}x{new_height}")
        return img_resized
    
    return img

def check_image_file(file_path):
    """
    快速检查图像文件是否可读
    
    返回:
        (是否可读, 详细信息)
    """
    try:
        if not os.path.exists(file_path):
            return False, "文件不存在"
        
        if not os.path.isfile(file_path):
            return False, "不是有效的文件"
        
        # 检查文件大小
        file_size = os.path.getsize(file_path)
        if file_size == 0:
            return False, "文件为空 (0字节)"
        
        # 检查文件头
        format_name = detect_image_format(file_path)
        
        if format_name == "无法检测":
            return False, "无法检测文件格式"
        elif format_name == "未知格式":
            return True, "未知格式，尝试读取"
        else:
            return True, f"{format_name} 格式"
            
    except Exception as e:
        return False, f"检查文件时出错: {str(e)}"

def convert_to_png(input_path, output_dir=None, quality=95):
    """
    将图像转换为PNG格式
    
    参数:
        input_path: 输入文件路径
        output_dir: 输出目录
        quality: 质量 (1-100)
        
    返回:
        成功: 输出文件路径
        失败: None
    """
    try:
        # 加载图像
        img, error = safe_load_image(input_path)
        if img is None:
            logger.error(f"无法读取图像进行转换: {error}")
            return None
        
        # 设置输出目录
        if output_dir is None:
            output_dir = "converted_images"
        
        os.makedirs(output_dir, exist_ok=True)
        
        # 生成输出文件名
        filename = Path(input_path).stem
        output_path = os.path.join(output_dir, f"{filename}.png")
        
        # 确保文件名唯一
        counter = 1
        original_output_path = output_path
        while os.path.exists(output_path):
            output_path = os.path.join(output_dir, f"{filename}_{counter}.png")
            counter += 1
        
        # 保存为PNG
        params = [
            cv2.IMWRITE_PNG_COMPRESSION, 9,  # 压缩级别 (0-9)
            cv2.IMWRITE_PNG_BILEVEL, 0,      # 二进制级别
        ]
        
        success = cv2.imwrite(output_path, img, params)
        
        if success:
            logger.info(f"转换成功: {Path(input_path).name} → {Path(output_path).name}")
            return output_path
        else:
            logger.error(f"保存PNG失败: {output_path}")
            return None
            
    except Exception as e:
        logger.error(f"转换失败: {e}")
        return None

def batch_convert_to_png(input_dir, output_dir=None, recursive=True):
    """
    批量转换文件夹中的图像为PNG格式
    
    参数:
        input_dir: 输入目录
        output_dir: 输出目录
        recursive: 是否递归处理子目录
        
    返回:
        成功转换的文件数量
    """
    if not os.path.exists(input_dir):
        logger.error(f"输入目录不存在: {input_dir}")
        return 0
    
    if output_dir is None:
        output_dir = os.path.join(input_dir, "converted_images")
    
    os.makedirs(output_dir, exist_ok=True)
    
    # 支持的图像格式
    image_extensions = {
        '.jpg', '.jpeg', '.png', '.bmp', '.tiff', '.tif', 
        '.gif', '.webp', '.JPG', '.JPEG', '.PNG', '.BMP', 
        '.TIFF', '.TIF', '.GIF', '.WEBP'
    }
    
    success_count = 0
    fail_count = 0
    
    if recursive:
        # 递归处理所有子目录
        for root, dirs, files in os.walk(input_dir):
            # 计算相对路径
            rel_path = os.path.relpath(root, input_dir)
            if rel_path == '.':
                current_output_dir = output_dir
            else:
                current_output_dir = os.path.join(output_dir, rel_path)
                os.makedirs(current_output_dir, exist_ok=True)
            
            for file in files:
                if Path(file).suffix in image_extensions:
                    input_file = os.path.join(root, file)
                    output_file = convert_to_png(input_file, current_output_dir)
                    
                    if output_file is not None:
                        success_count += 1
                    else:
                        fail_count += 1
    else:
        # 只处理当前目录
        for file in os.listdir(input_dir):
            file_path = os.path.join(input_dir, file)
            if os.path.isfile(file_path) and Path(file).suffix in image_extensions:
                output_file = convert_to_png(file_path, output_dir)
                
                if output_file is not None:
                    success_count += 1
                else:
                    fail_count += 1
    
    logger.info(f"批量转换完成: 成功 {success_count} 个，失败 {fail_count} 个")
    return success_count

def get_image_info(file_path):
    """
    获取图像的详细信息
    
    返回:
        图像信息字典
    """
    info = {
        'filename': Path(file_path).name,
        'path': file_path,
        'exists': os.path.exists(file_path),
        'format': '未知',
        'size_bytes': 0,
        'size_human': '0B',
        'dimensions': '未知',
        'channels': 0,
        'dtype': '未知',
        'readable': False,
        'error': None
    }
    
    if info['exists']:
        # 文件大小
        info['size_bytes'] = os.path.getsize(file_path)
        if info['size_bytes'] < 1024:
            info['size_human'] = f"{info['size_bytes']}B"
        elif info['size_bytes'] < 1024 * 1024:
            info['size_human'] = f"{info['size_bytes']/1024:.1f}KB"
        else:
            info['size_human'] = f"{info['size_bytes']/(1024*1024):.1f}MB"
        
        # 文件格式
        info['format'] = detect_image_format(file_path)
        
        # 尝试读取图像获取更多信息
        img, error = safe_load_image(file_path, max_size_mb=100)
        if img is not None:
            info['readable'] = True
            info['dimensions'] = f"{img.shape[1]}×{img.shape[0]}"
            info['channels'] = img.shape[2] if len(img.shape) == 3 else 1
            info['dtype'] = str(img.dtype)
        else:
            info['readable'] = False
            info['error'] = error
    
    return info

# 测试函数
def test_image_loading():
    """测试图像加载功能"""
    import tkinter as tk
    from tkinter import filedialog
    
    root = tk.Tk()
    root.withdraw()  # 隐藏主窗口
    
    file_path = filedialog.askopenfilename(
        title="选择要测试的图像文件",
        filetypes=[("所有文件", "*.*")]
    )
    
    if not file_path:
        print("❌ 未选择文件")
        return
    
    print("=" * 60)
    print("图像加载测试")
    print("=" * 60)
    
    info = get_image_info(file_path)
    
    print(f"文件名: {info['filename']}")
    print(f"文件路径: {info['path']}")
    print(f"文件大小: {info['size_human']} ({info['size_bytes']} 字节)")
    print(f"文件格式: {info['format']}")
    print(f"是否存在: {'是' if info['exists'] else '否'}")
    print(f"是否可读: {'是' if info['readable'] else '否'}")
    
    if info['readable']:
        print(f"图像尺寸: {info['dimensions']}")
        print(f"通道数: {info['channels']}")
        print(f"数据类型: {info['dtype']}")
    else:
        print(f"错误信息: {info['error']}")
    
    print("=" * 60)

if __name__ == "__main__":
    # 运行测试
    test_image_loading()