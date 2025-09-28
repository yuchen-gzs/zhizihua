package kms;

import javax.swing.*;

/**
 * 屿宸网络科技工作室·密钥生成器
 * 实时生成各种类型的密钥
 * 
 * @author 屿宸网络科技工作室
 * @version 1.0
 */
public class KeyGenerator {
    
    public static void main(String[] args) {
        // 启动图形界面
        SwingUtilities.invokeLater(() -> {
            new KeyGeneratorGUI().setVisible(true);
        });
    }
}