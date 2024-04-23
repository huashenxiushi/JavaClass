package top.remake.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * RandomUtil类提供了生成随机名称的功能
 * @author gzz
 */
public class RandomUtil {

    // 私有构造函数防止实例化
    private RandomUtil() {
    }

    /**
     * 返回一个基于当前精确时间和随机数的字符串
     * 该字符串可用作生成唯一的随机名称
     */
    public static String randomName() {
        String name;
        // 获取当前时间
        Date date = new Date();
        // 创建一个日期格式化对象，格式为"年月日时分秒毫秒"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        // 创建一个新的随机数生成器
        Random random = new Random();
        // 格式化当前时间并添加一个100到999之间的随机数，生成唯一的名称
        name = simpleDateFormat.format(date) + random.nextInt(100, 999);
        return name;
    }
}