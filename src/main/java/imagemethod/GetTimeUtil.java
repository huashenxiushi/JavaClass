package imagemethod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 这是一个工具类，用于生成随机名称。
 * 它提供了一个方法，该方法返回一个基于精确计算机时间和随机数的字符串。
 * 该类的构造函数是私有的，防止实例化。
 */
public class GetTimeUtil {

    // 私有构造函数，防止实例化
    private GetTimeUtil() {
    }

    /**
     * 返回一个基于精确计算机时间和随机数的字符串。
     * 首先，获取当前的日期和时间。
     * 然后，创建一个SimpleDateFormat对象，用于将日期和时间格式化为字符串。
     * 格式为"yyyyMMddHHmmssSSSS"，表示年、月、日、小时、分钟、秒和毫秒。
     * 接着，创建一个Random对象，用于生成一个介于100和999之间的随机整数。
     * 最后，将格式化的日期和时间字符串与随机整数拼接在一起，作为返回的字符串。
     * @return 基于精确计算机时间和随机数的字符串
     */
    public static String randomName() {
        String name;
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        Random random = new Random();
        name = simpleDateFormat.format(date) + random.nextInt(100, 999);
        return name;
    }
}