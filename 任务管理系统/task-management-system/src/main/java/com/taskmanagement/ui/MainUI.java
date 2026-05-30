package com.taskmanagement.ui;

import javax.swing.JOptionPane;
import java.util.Scanner;

@SuppressWarnings("all")
public class MainUI {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════");
        System.out.println("      个人任务管理系统");
        System.out.println("═══════════════════════════════════");
        System.out.println("请选择界面模式:");
        System.out.println("1. 控制台界面");
        System.out.println("2. 图形界面");
        System.out.println("═══════════════════════════════════");
        System.out.print("请选择 (1 或 2): ");
        
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        
        if (choice.equals("1")) {
            // 控制台界面
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.showMainMenu();
        } else if (choice.equals("2")) {
            // 图形界面
            try {
                // 在Swing事件调度线程中启动
                javax.swing.SwingUtilities.invokeLater(() -> {
                    new GraphicalUI();
                });
            } catch (Exception e) {
                System.err.println("无法启动图形界面: " + e.getMessage());
                System.out.println("正在切换到控制台界面...");
                ConsoleUI consoleUI = new ConsoleUI();
                consoleUI.showMainMenu();
            }
        } else {
            System.out.println("无效选择，使用默认控制台界面");
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.showMainMenu();
        }
        
        scanner.close();
    }
}