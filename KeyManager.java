package kms;
import java.security.SecureRandom;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 密钥管理类
 * 提供多种密钥生成算法
 */
public class KeyManager {
    
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String HEX_CHARS = "0123456789ABCDEF";
    
    private SecureRandom secureRandom;
    private Random random;
    
    public KeyManager() {
        this.secureRandom = new SecureRandom();
        this.random = new Random();
    }
    
    /**
     * 生成随机密码
     * @param length 密码长度
     * @param includeUppercase 包含大写字母
     * @param includeLowercase 包含小写字母
     * @param includeDigits 包含数字
     * @param includeSpecial 包含特殊字符
     * @return 生成的密码
     */
    public String generatePassword(int length, boolean includeUppercase, 
                                 boolean includeLowercase, boolean includeDigits, 
                                 boolean includeSpecial) {
        StringBuilder charset = new StringBuilder();
        
        if (includeUppercase) charset.append(UPPERCASE);
        if (includeLowercase) charset.append(LOWERCASE);
        if (includeDigits) charset.append(DIGITS);
        if (includeSpecial) charset.append(SPECIAL_CHARS);
        
        if (charset.length() == 0) {
            charset.append(LOWERCASE); // 默认包含小写字母
        }
        
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(charset.length());
            password.append(charset.charAt(index));
        }
        
        return password.toString();
    }
    
    /**
     * 生成十六进制密钥
     * @param length 密钥长度
     * @return 十六进制密钥
     */
    public String generateHexKey(int length) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(HEX_CHARS.length());
            key.append(HEX_CHARS.charAt(index));
        }
        return key.toString();
    }
    
    /**
     * 生成UUID格式密钥
     * @return UUID格式密钥
     */
    public String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * 生成基于时间的密钥
     * @return 时间戳密钥
     */
    public String generateTimestampKey() {
        long timestamp = System.currentTimeMillis();
        String randomSuffix = generateHexKey(8);
        return timestamp + "-" + randomSuffix;
    }
    
    /**
     * 生成数字密钥
     * @param length 密钥长度
     * @return 数字密钥
     */
    public String generateNumericKey(int length) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            key.append(secureRandom.nextInt(10));
        }
        return key.toString();
    }
    
    /**
     * 生成Base64格式密钥
     * @param byteLength 字节长度
     * @return Base64密钥
     */
    public String generateBase64Key(int byteLength) {
        byte[] bytes = new byte[byteLength];
        secureRandom.nextBytes(bytes);
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * 生成自定义格式密钥
     * @param pattern 密钥模式 (例如: "XXXX-XXXX-XXXX")
     * @return 格式化密钥
     */
    public String generateCustomKey(String pattern) {
        StringBuilder key = new StringBuilder();
        for (char c : pattern.toCharArray()) {
            if (c == 'X') {
                key.append(UPPERCASE.charAt(secureRandom.nextInt(UPPERCASE.length())));
            } else if (c == 'x') {
                key.append(LOWERCASE.charAt(secureRandom.nextInt(LOWERCASE.length())));
            } else if (c == '9') {
                key.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
            } else if (c == 'H') {
                key.append(HEX_CHARS.charAt(secureRandom.nextInt(HEX_CHARS.length())));
            } else {
                key.append(c);
            }
        }
        return key.toString();
    }
    
    /**
     * 生成带前缀的密钥
     * @param prefix 前缀
     * @param length 密钥部分长度
     * @return 带前缀的密钥
     */
    public String generatePrefixedKey(String prefix, int length) {
        String key = generateHexKey(length);
        return prefix + "-" + key;
    }
    
    /**
     * 获取当前时间戳
     * @return 格式化的时间戳
     */
    public String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}