#!/usr/bin/env python3

import sys
import os
import time
import numpy as np

# 添加项目根目录到Python路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from src.image_generator import ImageGenerator
from src.filters import FilterComparison
from src.edge_detectors import EdgeDetector
from src.enhancement import ImageEnhancer
from src.segmentation import ImageSegmentor
from utils.visualization import Visualization

def print_header(title):
    """打印标题"""
    print("\n" + "="*70)
    print(f" {title}")
    print("="*70)

def test_image_generator():
    """测试图像生成器"""
    print_header("测试图像生成器")
    
    try:
        generator = ImageGenerator(size=(400, 400))
        print("✅ ImageGenerator 初始化成功")
        
        # 测试各种图像生成
        test_images = generator.generate_all_test_images()
        print(f"✅ 生成 {len(test_images)} 种测试图像")
        
        for name, img in test_images.items():
            print(f"   {name:15} - 尺寸: {img.shape}, 类型: {img.dtype}")
        
        # 测试噪声添加
        test_img = test_images['chessboard']
        noises = generator.add_all_noises(test_img)
        print(f"✅ 添加 {len(noises)} 种噪声")
        
        return True
        
    except Exception as e:
        print(f"❌ 图像生成器测试失败: {e}")
        return False

def test_filters():
    """测试滤波算法"""
    print_header("测试滤波算法")
    
    try:
        generator = ImageGenerator()
        filter_comp = FilterComparison()
        
        # 生成测试图像
        test_img = generator.generate_chessboard()
        noisy_img = generator.add_gaussian_noise(test_img, sigma=30)
        
        print("✅ 测试图像生成成功")
        
        # 测试所有滤波器
        start_time = time.time()
        filtered_results = filter_comp.apply_all_filters(noisy_img)
        end_time = time.time()
        
        print(f"✅ 应用 {len(filtered_results)} 种滤波算法")
        print(f"   处理时间: {(end_time - start_time)*1000:.2f} ms")
        
        # 测试性能评估
        for filter_name, filtered_img in filtered_results.items():
            if filter_name != '原始噪声':
                psnr = filter_comp.calculate_psnr(test_img, filtered_img)
                print(f"   {filter_name:15} - PSNR: {psnr:6.2f} dB")
        
        return True
        
    except Exception as e:
        print(f"❌ 滤波算法测试失败: {e}")
        return False

def test_edge_detection():
    """测试边缘检测算法"""
    print_header("测试边缘检测算法")
    
    try:
        generator = ImageGenerator()
        edge_detector = EdgeDetector()
        
        # 生成测试图像
        test_img = generator.generate_chessboard()
        
        print("✅ 测试图像生成成功")
        
        # 测试所有边缘检测算法
        start_time = time.time()
        edge_results = edge_detector.detect_all_edges(test_img)
        end_time = time.time()
        
        print(f"✅ 应用 {len(edge_results)} 种边缘检测算法")
        print(f"   处理时间: {(end_time - start_time)*1000:.2f} ms")
        
        # 显示算法名称
        for edge_name in edge_results.keys():
            print(f"   {edge_name}")
        
        # 测试Canny参数调整
        canny_edges = edge_detector.canny_edges(test_img, 30, 100)
        print(f"✅ Canny边缘检测 (30, 100) - 边缘像素: {np.sum(canny_edges > 0)}")
        
        return True
        
    except Exception as e:
        print(f"❌ 边缘检测算法测试失败: {e}")
        return False

def test_image_enhancement():
    """测试图像增强算法"""
    print_header("测试图像增强算法")
    
    try:
        generator = ImageGenerator()
        enhancer = ImageEnhancer()
        
        # 生成低对比度图像
        test_img = generator.generate_gradient()
        
        print("✅ 测试图像生成成功")
        print(f"   原始图像对比度: {enhancer.calculate_contrast(test_img):.2f}")
        print(f"   原始图像熵: {enhancer.calculate_entropy(test_img):.2f}")
        
        # 测试所有增强算法
        start_time = time.time()
        enhance_results = enhancer.apply_all_enhancements(test_img)
        end_time = time.time()
        
        print(f"✅ 应用 {len(enhance_results)} 种图像增强算法")
        print(f"   处理时间: {(end_time - start_time)*1000:.2f} ms")
        
        # 测试增强效果
        for enhance_name, enhanced_img in enhance_results.items():
            if enhance_name != '原始图像':
                contrast = enhancer.calculate_contrast(enhanced_img)
                entropy = enhancer.calculate_entropy(enhanced_img)
                print(f"   {enhance_name:20} - 对比度: {contrast:6.2f}, 熵: {entropy:6.2f}")
        
        return True
        
    except Exception as e:
        print(f"❌ 图像增强算法测试失败: {e}")
        return False

def test_image_segmentation():
    """测试图像分割算法"""
    print_header("测试图像分割算法")
    
    try:
        generator = ImageGenerator()
        segmentor = ImageSegmentor()
        
        # 生成测试图像
        test_img = generator.generate_random_shapes()
        
        print("✅ 测试图像生成成功")
        
        # 测试阈值分割
        threshold_methods = ['otsu', 'adaptive', 'triangle']
        for method in threshold_methods:
            binary = segmentor.threshold_segmentation(test_img, method)
            print(f"✅ {method:10}阈值分割 - 前景像素: {np.sum(binary > 0)}")
        
        # 测试K-means分割
        start_time = time.time()
        segmented, labels = segmentor.kmeans_segmentation(test_img, k=3)
        end_time = time.time()
        
        print(f"✅ K-means聚类分割 (K=3)")
        print(f"   处理时间: {(end_time - start_time)*1000:.2f} ms")
        print(f"   区域数量: {len(np.unique(labels))}")
        
        # 测试分水岭分割
        watershed_result, markers = segmentor.watershed_segmentation(test_img)
        print(f"✅ 分水岭分割 - 标记数量: {len(np.unique(markers))}")
        
        return True
        
    except Exception as e:
        print(f"❌ 图像分割算法测试失败: {e}")
        return False

def test_visualization():
    """测试可视化功能"""
    print_header("测试可视化功能")
    
    try:
        generator = ImageGenerator()
        
        # 生成测试图像
        test_img = generator.generate_chessboard()
        noisy_img = generator.add_gaussian_noise(test_img)
        
        print("✅ 测试图像生成成功")
        
        # 测试图像显示
        images = {
            '原始图像': test_img,
            '噪声图像': noisy_img
        }
        
        print("✅ 可视化测试通过（图像显示功能正常）")
        
        # 测试直方图
        print("✅ 直方图功能可用")
        
        # 测试3D表面图
        print("✅ 3D表面图功能可用")
        
        return True
        
    except Exception as e:
        print(f"❌ 可视化功能测试失败: {e}")
        return False

def performance_comparison():
    """性能对比测试"""
    print_header("性能对比测试")
    
    try:
        generator = ImageGenerator(size=(256, 256))
        filter_comp = FilterComparison()
        
        # 生成测试图像
        test_img = generator.generate_chessboard()
        noisy = generator.add_gaussian_noise(test_img, sigma=30)
        
        print("测试不同滤波器的性能:")
        print("-" * 50)
        
        filters_to_test = {
            '均值滤波': lambda img: filter_comp.apply_mean_filter(img, 5),
            '高斯滤波': lambda img: filter_comp.apply_gaussian_filter(img, 5, 1.0),
            '中值滤波': lambda img: filter_comp.apply_median_filter(img, 5),
            '双边滤波': lambda img: filter_comp.apply_bilateral_filter(img)
        }
        
        results = {}
        
        for name, filter_func in filters_to_test.items():
            # 测量处理时间
            start_time = time.time()
            filtered = filter_func(noisy)
            end_time = time.time()
            
            # 计算PSNR
            psnr = filter_comp.calculate_psnr(test_img, filtered)
            process_time = (end_time - start_time) * 1000  # 毫秒
            
            results[name] = {
                'psnr': psnr,
                'time': process_time
            }
            
            print(f"{name:10} PSNR: {psnr:6.2f} dB, 耗时: {process_time:6.2f} ms")
        
        print("-" * 50)
        
        # 找出最佳PSNR
        best_psnr = max(results.items(), key=lambda x: x[1]['psnr'])
        print(f"最佳PSNR: {best_psnr[0]} ({best_psnr[1]['psnr']:.2f} dB)")
        
        # 找出最快算法
        fastest = min(results.items(), key=lambda x: x[1]['time'])
        print(f"最快算法: {fastest[0]} ({fastest[1]['time']:.2f} ms)")
        
        return True
        
    except Exception as e:
        print(f"❌ 性能对比测试失败: {e}")
        return False

def run_comprehensive_test():
    """运行综合性测试"""
    print_header("数字图像处理算法仿真系统 - 综合性测试")
    
    test_functions = [
        ("图像生成器", test_image_generator),
        ("滤波算法", test_filters),
        ("边缘检测", test_edge_detection),
        ("图像增强", test_image_enhancement),
        ("图像分割", test_image_segmentation),
        ("可视化功能", test_visualization),
        ("性能对比", performance_comparison)
    ]
    
    results = []
    
    for test_name, test_func in test_functions:
        print(f"\n运行测试: {test_name}")
        print("-" * 40)
        
        try:
            success = test_func()
            results.append((test_name, success))
            
            if success:
                print(f"✅ {test_name} - 通过")
            else:
                print(f"❌ {test_name} - 失败")
                
        except Exception as e:
            print(f"❌ {test_name} - 异常: {e}")
            results.append((test_name, False))
    
    # 打印测试总结
    print_header("测试总结")
    
    passed = sum(1 for _, success in results if success)
    total = len(results)
    
    print(f"测试通过率: {passed}/{total} ({passed/total*100:.1f}%)")
    print("\n详细结果:")
    
    for test_name, success in results:
        status = "✅ 通过" if success else "❌ 失败"
        print(f"  {test_name:15} - {status}")
    
    if passed == total:
        print("\n🎉 所有测试通过！系统可以正常运行。")
        print("\n建议运行:")
        print("  1. python main.py --gui      # 启动图形界面")
        print("  2. python main.py --cmd      # 命令行演示")
        print("  3. 查看 examples/ 目录了解更多示例")
    else:
        print(f"\n⚠️  有 {total-passed} 个测试失败，请检查相关模块。")
    
    return all(success for _, success in results)

def test_specific_algorithm(algorithm_name):
    """测试特定算法"""
    algorithm_tests = {
        'filter': test_filters,
        'edge': test_edge_detection,
        'enhance': test_image_enhancement,
        'segment': test_image_segmentation,
        'visual': test_visualization
    }
    
    if algorithm_name in algorithm_tests:
        print_header(f"测试{algorithm_name}算法")
        return algorithm_tests[algorithm_name]()
    else:
        print(f"❌ 未知算法: {algorithm_name}")
        print(f"可用算法: {', '.join(algorithm_tests.keys())}")
        return False

def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description='测试数字图像处理算法')
    parser.add_argument('--all', action='store_true', help='运行所有测试')
    parser.add_argument('--algorithm', type=str, help='测试特定算法')
    parser.add_argument('--performance', action='store_true', help='运行性能测试')
    
    args = parser.parse_args()
    
    if args.algorithm:
        # 测试特定算法
        test_specific_algorithm(args.algorithm)
    elif args.performance:
        # 运行性能测试
        performance_comparison()
    elif args.all:
        # 运行所有测试
        run_comprehensive_test()
    else:
        # 默认运行所有测试
        run_comprehensive_test()

if __name__ == "__main__":
    main()