#!/usr/bin/env python3
"""
图像格式转换工具
将不支持的格式转换为标准PNG格式
运行: python convert_images.py
"""
import os
import sys
import tkinter as tk
from tkinter import ttk, filedialog, messagebox, scrolledtext
from pathlib import Path
import threading

# 添加项目路径
sys.path.insert(0, os.path.dirname(__file__))

class ImageConverter:
    """图像转换器GUI"""
    
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("图像格式转换工具 - 湖南工商大学数字图像处理")
        self.root.geometry("850x700")
        
        # 设置图标和样式
        self.setup_styles()
        
        # 设置UI
        self.setup_ui()
        
        # 控制变量
        self.converting = False
        self.stop_requested = False
        
    def setup_styles(self):
        """设置样式"""
        style = ttk.Style()
        style.configure("Title.TLabel", font=("微软雅黑", 18, "bold"))
        style.configure("Subtitle.TLabel", font=("微软雅黑", 12))
        style.configure("Accent.TButton", font=("微软雅黑", 10, "bold"))
        style.configure("Success.TLabel", foreground="green")
        style.configure("Error.TLabel", foreground="red")
    
    def setup_ui(self):
        """设置用户界面"""
        # 主框架
        main_frame = ttk.Frame(self.root, padding=20)
        main_frame.pack(fill=tk.BOTH, expand=True)
        
        # 标题
        ttk.Label(
            main_frame, 
            text="📷 图像格式转换工具", 
            style="Title.TLabel"
        ).pack(pady=(0, 10))
        
        ttk.Label(
            main_frame,
            text="解决数字图像处理系统中图像加载问题",
            style="Subtitle.TLabel"
        ).pack(pady=(0, 30))
        
        # 问题诊断区域
        diag_frame = ttk.LabelFrame(main_frame, text="🔍 问题诊断", padding=15)
        diag_frame.pack(fill=tk.X, pady=(0, 20))
        
        ttk.Label(
            diag_frame,
            text="如果你的图像无法在系统中加载，可能的原因：",
            font=("微软雅黑", 10)
        ).pack(anchor=tk.W, pady=(0, 10))
        
        problems = [
            "1. 📂 文件格式不受支持（如HEIC、WEBP等）",
            "2. 🐛 文件已损坏或编码错误",
            "3. 🐌 文件过大（超过50MB）",
            "4. 🔤 文件路径包含中文或特殊字符",
            "5. 🔒 文件被其他程序占用"
        ]
        
        for problem in problems:
            ttk.Label(
                diag_frame,
                text=problem,
                font=("微软雅黑", 9)
            ).pack(anchor=tk.W, padx=20)
        
        # 解决方案区域
        solution_frame = ttk.LabelFrame(main_frame, text="💡 解决方案", padding=15)
        solution_frame.pack(fill=tk.X, pady=(0, 30))
        
        ttk.Label(
            solution_frame,
            text="使用此工具将图像转换为标准PNG格式：",
            font=("微软雅黑", 10)
        ).pack(anchor=tk.W, pady=(0, 10))
        
        solutions = [
            "✅ 支持格式: JPG, JPEG, PNG, BMP, TIFF, GIF, WEBP 等",
            "✅ 自动修复: 尝试修复损坏的图像文件",
            "✅ 尺寸优化: 自动调整过大图像尺寸",
            "✅ 批量处理: 支持整个文件夹转换",
            "✅ 质量保持: PNG无损格式，保持图像质量"
        ]
        
        for solution in solutions:
            ttk.Label(
                solution_frame,
                text=solution,
                font=("微软雅黑", 9)
            ).pack(anchor=tk.W, padx=20)
        
        # 转换控制区域
        control_frame = ttk.Frame(main_frame)
        control_frame.pack(fill=tk.X, pady=(0, 20))
        
        # 单文件转换
        single_frame = ttk.Frame(control_frame)
        single_frame.pack(side=tk.LEFT, padx=(0, 20))
        
        ttk.Button(
            single_frame,
            text="选择单个图像文件",
            command=self.convert_single,
            width=25,
            style="Accent.TButton"
        ).pack()
        ttk.Label(
            single_frame,
            text="转换单个问题图像",
            font=("微软雅黑", 8)
        ).pack(pady=(5, 0))
        
        # 批量转换
        batch_frame = ttk.Frame(control_frame)
        batch_frame.pack(side=tk.LEFT)
        
        ttk.Button(
            batch_frame,
            text="选择文件夹批量转换",
            command=self.convert_batch,
            width=25,
            style="Accent.TButton"
        ).pack()
        ttk.Label(
            batch_frame,
            text="转换整个文件夹的图像",
            font=("微软雅黑", 8)
        ).pack(pady=(5, 0))
        
        # 进度区域
        self.progress_frame = ttk.Frame(main_frame)
        self.progress_frame.pack(fill=tk.X, pady=(0, 10))
        
        self.progress_label = ttk.Label(
            self.progress_frame,
            text="就绪",
            font=("微软雅黑", 10)
        )
        self.progress_label.pack()
        
        self.progress_bar = ttk.Progressbar(
            self.progress_frame,
            orient=tk.HORIZONTAL,
            length=400,
            mode='determinate'
        )
        self.progress_bar.pack(pady=5)
        
        # 控制按钮
        self.control_buttons_frame = ttk.Frame(main_frame)
        self.control_buttons_frame.pack(fill=tk.X, pady=(0, 20))
        
        self.start_button = ttk.Button(
            self.control_buttons_frame,
            text="开始转换",
            command=self.start_conversion,
            state=tk.DISABLED,
            width=15
        )
        self.start_button.pack(side=tk.LEFT, padx=(0, 10))
        
        self.stop_button = ttk.Button(
            self.control_buttons_frame,
            text="停止",
            command=self.stop_conversion,
            state=tk.DISABLED,
            width=15
        )
        self.stop_button.pack(side=tk.LEFT)
        
        # 日志区域
        log_frame = ttk.LabelFrame(main_frame, text="📝 转换日志", padding=10)
        log_frame.pack(fill=tk.BOTH, expand=True)
        
        self.log_text = scrolledtext.ScrolledText(
            log_frame,
            height=15,
            font=("Consolas", 9)
        )
        self.log_text.pack(fill=tk.BOTH, expand=True)
        
        # 底部按钮
        bottom_frame = ttk.Frame(main_frame)
        bottom_frame.pack(fill=tk.X, pady=(20, 0))
        
        ttk.Button(
            bottom_frame,
            text="打开输出目录",
            command=self.open_output_dir
        ).pack(side=tk.LEFT, padx=(0, 10))
        
        ttk.Button(
            bottom_frame,
            text="清空日志",
            command=self.clear_log
        ).pack(side=tk.LEFT, padx=(0, 10))
        
        ttk.Button(
            bottom_frame,
            text="帮助",
            command=self.show_help
        ).pack(side=tk.LEFT, padx=(0, 10))
        
        ttk.Button(
            bottom_frame,
            text="退出",
            command=self.root.quit
        ).pack(side=tk.LEFT)
        
        # 初始化变量
        self.files_to_convert = []
        self.output_dir = None
    
    def log(self, message, level="INFO"):
        """添加日志"""
        import datetime
        timestamp = datetime.datetime.now().strftime("%H:%M:%S")
        
        if level == "ERROR":
            tag = "error"
            prefix = "❌ ERROR"
        elif level == "WARNING":
            tag = "warning"
            prefix = "⚠️  WARNING"
        elif level == "SUCCESS":
            tag = "success"
            prefix = "✅ SUCCESS"
        else:
            tag = "info"
            prefix = "ℹ️  INFO"
        
        log_entry = f"[{timestamp}] {prefix}: {message}\n"
        
        # 插入日志
        self.log_text.insert(tk.END, log_entry, tag)
        
        # 设置标签样式
        self.log_text.tag_config("error", foreground="red")
        self.log_text.tag_config("warning", foreground="orange")
        self.log_text.tag_config("success", foreground="green")
        self.log_text.tag_config("info", foreground="blue")
        
        # 滚动到底部
        self.log_text.see(tk.END)
        self.root.update()
    
    def clear_log(self):
        """清空日志"""
        self.log_text.delete(1.0, tk.END)
        self.log("日志已清空", "INFO")
    
    def convert_single(self):
        """选择单个文件"""
        file_path = filedialog.askopenfilename(
            title="选择要转换的图像文件",
            filetypes=[
                ("所有图像文件", "*.jpg *.jpeg *.png *.bmp *.tiff *.tif *.gif *.webp"),
                ("所有文件", "*.*")
            ]
        )
        
        if file_path:
            self.files_to_convert = [file_path]
            self.output_dir = os.path.join(os.path.dirname(file_path), "converted_images")
            self.start_button.config(state=tk.NORMAL)
            self.log(f"选择单个文件: {os.path.basename(file_path)}", "INFO")
            self.progress_label.config(text=f"已选择1个文件，输出到: {self.output_dir}")
    
    def convert_batch(self):
        """批量选择文件"""
        folder_path = filedialog.askdirectory(title="选择包含图像的文件夹")
        
        if folder_path:
            # 查找所有图像文件
            image_extensions = {'.jpg', '.jpeg', '.png', '.bmp', '.tiff', '.tif', '.gif', '.webp'}
            self.files_to_convert = []
            
            for root, dirs, files in os.walk(folder_path):
                for file in files:
                    if Path(file).suffix.lower() in image_extensions:
                        self.files_to_convert.append(os.path.join(root, file))
            
            if self.files_to_convert:
                self.output_dir = os.path.join(folder_path, "converted_images")
                self.start_button.config(state=tk.NORMAL)
                self.log(f"选择文件夹: {folder_path}", "INFO")
                self.log(f"找到 {len(self.files_to_convert)} 个图像文件", "INFO")
                self.progress_label.config(text=f"已选择{len(self.files_to_convert)}个文件")
            else:
                messagebox.showwarning("无图像文件", f"在文件夹中未找到支持的图像文件: {folder_path}")
    
    def start_conversion(self):
        """开始转换"""
        if not self.files_to_convert:
            messagebox.showwarning("无文件", "请先选择要转换的文件或文件夹")
            return
        
        # 禁用开始按钮，启用停止按钮
        self.start_button.config(state=tk.DISABLED)
        self.stop_button.config(state=tk.NORMAL)
        self.converting = True
        self.stop_requested = False
        
        # 在后台线程中运行转换
        thread = threading.Thread(target=self.run_conversion, daemon=True)
        thread.start()
    
    def stop_conversion(self):
        """停止转换"""
        self.stop_requested = True
        self.log("正在停止转换...", "WARNING")
    
    def run_conversion(self):
        """运行转换过程"""
        import cv2
        import numpy as np
        
        total_files = len(self.files_to_convert)
        success_count = 0
        fail_count = 0
        
        # 创建输出目录
        os.makedirs(self.output_dir, exist_ok=True)
        
        self.log(f"开始转换 {total_files} 个文件...", "INFO")
        self.log(f"输出目录: {self.output_dir}", "INFO")
        
        self.progress_bar.config(maximum=total_files, value=0)
        
        for i, file_path in enumerate(self.files_to_convert, 1):
            if self.stop_requested:
                self.log("转换已停止", "WARNING")
                break
            
            filename = os.path.basename(file_path)
            self.progress_label.config(text=f"正在转换: {filename} ({i}/{total_files})")
            
            try:
                # 尝试读取图像
                img = cv2.imread(file_path, cv2.IMREAD_COLOR)
                
                # 如果cv2失败，尝试PIL
                if img is None:
                    try:
                        from PIL import Image
                        pil_img = Image.open(file_path)
                        if pil_img.mode == 'RGBA':
                            pil_img = pil_img.convert('RGB')
                        img = cv2.cvtColor(np.array(pil_img), cv2.COLOR_RGB2BGR)
                    except:
                        pass
                
                if img is None:
                    self.log(f"无法读取: {filename}", "ERROR")
                    fail_count += 1
                    continue
                
                # 调整大小（如果太大）
                height, width = img.shape[:2]
                if max(height, width) > 2000:
                    scale = 2000 / max(height, width)
                    new_width = int(width * scale)
                    new_height = int(height * scale)
                    img = cv2.resize(img, (new_width, new_height), cv2.INTER_AREA)
                    self.log(f"  调整大小: {width}x{height} → {new_width}x{new_height}", "INFO")
                
                # 生成输出文件名
                stem = Path(filename).stem
                output_path = os.path.join(self.output_dir, f"{stem}.png")
                
                # 保存为PNG
                success = cv2.imwrite(output_path, img, [cv2.IMWRITE_PNG_COMPRESSION, 3])
                
                if success:
                    self.log(f"转换成功: {filename} → {stem}.png", "SUCCESS")
                    success_count += 1
                else:
                    self.log(f"保存失败: {filename}", "ERROR")
                    fail_count += 1
                
            except Exception as e:
                self.log(f"转换失败 {filename}: {str(e)}", "ERROR")
                fail_count += 1
            
            # 更新进度条
            self.progress_bar.config(value=i)
            self.root.update()
        
        # 完成后的处理
        self.converting = False
        self.start_button.config(state=tk.NORMAL)
        self.stop_button.config(state=tk.DISABLED)
        
        # 显示结果
        self.progress_label.config(text=f"转换完成: {success_count}成功, {fail_count}失败")
        self.progress_bar.config(value=total_files)
        
        summary = f"\n转换完成！\n"
        summary += f"成功: {success_count} 个文件\n"
        summary += f"失败: {fail_count} 个文件\n"
        summary += f"输出目录: {self.output_dir}"
        
        self.log(summary, "SUCCESS")
        
        if success_count > 0:
            messagebox.showinfo("转换完成", summary)
    
    def open_output_dir(self):
        """打开输出目录"""
        if self.output_dir and os.path.exists(self.output_dir):
            import platform
            system = platform.system()
            
            if system == "Windows":
                os.startfile(self.output_dir)
            elif system == "Darwin":  # macOS
                os.system(f"open '{self.output_dir}'")
            elif system == "Linux":
                os.system(f"xdg-open '{self.output_dir}'")
        else:
            messagebox.showinfo("目录不存在", "输出目录不存在或未指定")
    
    def show_help(self):
        """显示帮助"""
        help_text = """图像格式转换工具 - 使用帮助

为什么需要转换图像？
1. 数字图像处理系统只支持常见格式
2. 有些图像格式可能包含特殊编码
3. 过大的图像会导致内存不足

支持的输入格式：
• JPEG (.jpg, .jpeg)
• PNG (.png)
• BMP (.bmp)
• TIFF (.tiff, .tif)
• GIF (.gif)
• WEBP (.webp)

输出格式：PNG（无损压缩）

使用步骤：
1. 点击"选择单个图像文件"或"选择文件夹批量转换"
2. 点击"开始转换"
3. 等待转换完成
4. 使用"打开输出目录"查看结果

注意：
• 转换后的图像保存在"converted_images"文件夹中
• 原始文件不会被修改
• 如果转换失败，请检查原文件是否损坏
"""
        messagebox.showinfo("帮助", help_text)
    
    def run(self):
        """运行应用"""
        self.root.mainloop()

def main():
    """主函数"""
    converter = ImageConverter()
    converter.run()

if __name__ == "__main__":
    main()