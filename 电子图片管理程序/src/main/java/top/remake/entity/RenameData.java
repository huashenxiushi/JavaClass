package top.remake.entity;

/**
 * 重命名数据类，用于存储用户在重命名时选择的名称前缀、起始编号和编号位数。
 *
 * @author gzz
 */
public class RenameData {
    // 用户选择的名称前缀
    private final String name;

    // 用户选择的起始编号
    private final int startNum;

    // 用户选择的编号位数
    private final int digit;

    /**
     * 构造函数，初始化名称前缀、起始编号和编号位数
     *
     * @param name 名称前缀
     * @param start 起始编号
     * @param digit 编号位数
     */
    public RenameData(String name, int start, int digit) {
        this.name = name;
        this.startNum = start;
        this.digit = digit;
    }

    /**
     * 获取起始编号
     *
     * @return 起始编号
     */
    public int getStartNum() {
        return startNum;
    }

    /**
     * 获取名称前缀
     *
     * @return 名称前缀
     */
    public String getName() {
        return name;
    }

    /**
     * 获取编号位数
     *
     * @return 编号位数
     */
    public int getDigit() {
        return digit;
    }
}