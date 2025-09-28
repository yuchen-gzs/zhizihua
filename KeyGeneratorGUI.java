package kms;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.List;

/**
 * 密钥生成器图形界面
 * 实时生成和显示各种类型的密钥
 */
public class KeyGeneratorGUI extends JFrame {
    
    private KeyManager keyManager;
    private Timer autoGenerateTimer;
    private JTextArea keyDisplayArea;
    private JLabel timestampLabel;
    private JComboBox<String> keyTypeCombo;
    private JSpinner lengthSpinner;
    private JCheckBox autoGenerateCheck;
    private JCheckBox uppercaseCheck;
    private JCheckBox lowercaseCheck;
    private JCheckBox digitsCheck;
    private JCheckBox specialCheck;
    private JTextField customPatternField;
    private JTextField prefixField;
    private List<String> keyHistory;
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;
    
    public KeyGeneratorGUI() {
        keyManager = new KeyManager();
        keyHistory = new ArrayList<>();
        historyModel = new DefaultListModel<>();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("屿宸网络科技工作室·密钥生成器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // 启动自动生成
        startAutoGenerate();
    }
    
    private void initializeComponents() {
        // 密钥显示区域
        keyDisplayArea = new JTextArea(10, 50);
        keyDisplayArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        keyDisplayArea.setEditable(false);
        keyDisplayArea.setBackground(Color.BLACK);
        keyDisplayArea.setForeground(Color.GREEN);
        keyDisplayArea.setBorder(BorderFactory.createTitledBorder("实时生成的密钥"));
        
        // 时间戳标签
        timestampLabel = new JLabel();
        timestampLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        timestampLabel.setForeground(Color.BLUE);
        
        // 密钥类型选择
        String[] keyTypes = {
            "随机密码", "十六进制密钥", "UUID", "时间戳密钥", 
            "数字密钥", "Base64密钥", "自定义格式", "带前缀密钥"
        };
        keyTypeCombo = new JComboBox<>(keyTypes);
        
        // 长度设置
        lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 4, 128, 1));
        
        // 选项复选框
        autoGenerateCheck = new JCheckBox("自动生成", true);
        uppercaseCheck = new JCheckBox("大写字母", true);
        lowercaseCheck = new JCheckBox("小写字母", true);
        digitsCheck = new JCheckBox("数字", true);
        specialCheck = new JCheckBox("特殊字符", false);
        
        // 自定义模式输入
        customPatternField = new JTextField("XXXX-XXXX-XXXX-XXXX", 20);
        prefixField = new JTextField("KEY", 10);
        
        // 历史记录列表
        historyList = new JList<>(historyModel);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFont(new Font("Consolas", Font.PLAIN, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部控制面板
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("密钥生成设置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 第一行
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("密钥类型:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(keyTypeCombo, gbc);
        gbc.gridx = 2;
        controlPanel.add(new JLabel("长度:"), gbc);
        gbc.gridx = 3;
        controlPanel.add(lengthSpinner, gbc);
        gbc.gridx = 4;
        controlPanel.add(autoGenerateCheck, gbc);
        
        // 第二行 - 密码选项
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(uppercaseCheck, gbc);
        gbc.gridx = 1;
        controlPanel.add(lowercaseCheck, gbc);
        gbc.gridx = 2;
        controlPanel.add(digitsCheck, gbc);
        gbc.gridx = 3;
        controlPanel.add(specialCheck, gbc);
        
        // 第三行 - 自定义选项
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("自定义模式:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        controlPanel.add(customPatternField, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("前缀:"), gbc);
        gbc.gridx = 4;
        controlPanel.add(prefixField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton generateButton = new JButton("立即生成");
        JButton copyButton = new JButton("复制密钥");
        JButton clearButton = new JButton("清空显示");
        JButton saveButton = new JButton("保存历史");
        
        generateButton.addActionListener(e -> generateKey());
        copyButton.addActionListener(e -> copyToClipboard());
        clearButton.addActionListener(e -> clearDisplay());
        saveButton.addActionListener(e -> saveHistory());
        
        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        
        // 中央显示面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(keyDisplayArea), BorderLayout.CENTER);
        
        // 底部状态面板
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("当前时间: "));
        statusPanel.add(timestampLabel);
        
        // 右侧历史面板
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("密钥历史"));
        historyPanel.add(new JScrollPane(historyList), BorderLayout.CENTER);
        historyPanel.setPreferredSize(new Dimension(200, 0));
        
        // 组装界面
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        add(historyPanel, BorderLayout.EAST);
    }
    
    private void setupEventHandlers() {
        // 自动生成定时器
        autoGenerateTimer = new Timer(2000, e -> {
            if (autoGenerateCheck.isSelected()) {
                generateKey();
            }
            updateTimestamp();
        });
        
        // 自动生成复选框事件
        autoGenerateCheck.addActionListener(e -> {
            if (autoGenerateCheck.isSelected()) {
                autoGenerateTimer.start();
            } else {
                autoGenerateTimer.stop();
            }
        });
        
        // 历史记录双击复制
        historyList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedKey = historyList.getSelectedValue();
                    if (selectedKey != null) {
                        copyToClipboard(selectedKey);
                        JOptionPane.showMessageDialog(KeyGeneratorGUI.this, 
                            "密钥已复制到剪贴板！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }
    
    private void generateKey() {
        String keyType = (String) keyTypeCombo.getSelectedItem();
        int length = (Integer) lengthSpinner.getValue();
        String key = "";
        
        switch (keyType) {
            case "随机密码":
                key = keyManager.generatePassword(length, 
                    uppercaseCheck.isSelected(),
                    lowercaseCheck.isSelected(),
                    digitsCheck.isSelected(),
                    specialCheck.isSelected());
                break;
            case "十六进制密钥":
                key = keyManager.generateHexKey(length);
                break;
            case "UUID":
                key = keyManager.generateUUID();
                break;
            case "时间戳密钥":
                key = keyManager.generateTimestampKey();
                break;
            case "数字密钥":
                key = keyManager.generateNumericKey(length);
                break;
            case "Base64密钥":
                key = keyManager.generateBase64Key(length);
                break;
            case "自定义格式":
                key = keyManager.generateCustomKey(customPatternField.getText());
                break;
            case "带前缀密钥":
                key = keyManager.generatePrefixedKey(prefixField.getText(), length);
                break;
        }
        
        // 显示密钥
        String timestamp = keyManager.getCurrentTimestamp();
        String displayText = String.format("[%s] %s: %s\n", timestamp, keyType, key);
        keyDisplayArea.append(displayText);
        keyDisplayArea.setCaretPosition(keyDisplayArea.getDocument().getLength());
        
        // 添加到历史记录
        String historyEntry = String.format("%s (%s)", key, keyType);
        historyModel.addElement(historyEntry);
        keyHistory.add(key);
        
        // 限制历史记录数量
        if (historyModel.size() > 100) {
            historyModel.removeElementAt(0);
            keyHistory.remove(0);
        }
    }
    
    private void updateTimestamp() {
        timestampLabel.setText(keyManager.getCurrentTimestamp());
    }
    
    private void copyToClipboard() {
        String text = keyDisplayArea.getText();
        if (!text.isEmpty()) {
            // 获取最后一个生成的密钥
            String[] lines = text.split("\n");
            if (lines.length > 0) {
                String lastLine = lines[lines.length - 1];
                int colonIndex = lastLine.lastIndexOf(": ");
                if (colonIndex != -1 && colonIndex + 2 < lastLine.length()) {
                    String lastKey = lastLine.substring(colonIndex + 2);
                    copyToClipboard(lastKey);
                    JOptionPane.showMessageDialog(this, 
                        "最新密钥已复制到剪贴板！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    private void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(text);
        clipboard.setContents(selection, null);
    }
    
    private void clearDisplay() {
        keyDisplayArea.setText("");
    }
    
    private void saveHistory() {
        if (keyHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "没有密钥历史记录可保存！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("密钥历史_" + 
            System.currentTimeMillis() + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write("屿宸网络科技工作室·密钥生成器 - 历史记录\n");
                writer.write("生成时间: " + keyManager.getCurrentTimestamp() + "\n");
                writer.write("=" + "=".repeat(50) + "\n\n");
                
                for (int i = 0; i < historyModel.size(); i++) {
                    writer.write((i + 1) + ". " + historyModel.getElementAt(i) + "\n");
                }
                
                writer.close();
                JOptionPane.showMessageDialog(this, 
                    "密钥历史已保存！", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "保存失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void startAutoGenerate() {
        updateTimestamp();
        autoGenerateTimer.start();
        generateKey(); // 立即生成一个密钥
    }
}