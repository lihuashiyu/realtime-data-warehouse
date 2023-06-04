package issac.utils;

import issac.constant.SignalConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimestampLtz3CompareUtil
{
    // 数据格式 2022-04-01 10:20:47.302Z
    // 数据格式 2022-04-01 10:20:47.041Z
    // 数据格式 2022-04-01 10:20:47.410Z
    // 数据格式 2022-04-01 10:20:47.41Z
    public static int compare(String timestamp1, String timestamp2)
    {
        // 1. 去除末尾的时区标志，'Z' 表示 0 时区
        String cleanedTime1 = timestamp1.substring(0, timestamp1.length() - 1);
        String cleanedTime2 = timestamp2.substring(0, timestamp2.length() - 1);
        
        // 2. 提取小于 1 秒的部分
        String[] timeArr1 = cleanedTime1.split(SignalConstant.RE_DOT);
        String[] timeArr2 = cleanedTime2.split(SignalConstant.RE_DOT);
        String microseconds1 = new StringBuilder(timeArr1[timeArr1.length - 1]).append("000").substring(0, 3);
        String microseconds2 = new StringBuilder(timeArr2[timeArr2.length - 1]).append("000").substring(0, 3);
        
        int micro1 = Integer.parseInt(microseconds1);
        int micro2 = Integer.parseInt(microseconds2);
        
        // 3. 提取 yyyy-MM-dd HH:mm:ss 的部分
        String date1 = timeArr1[0];
        String date2 = timeArr2[0];
        Long ts1 = DateFormatUtil.toTs(date1, true);
        Long ts2 = DateFormatUtil.toTs(date2, true);
        
        // 4. 获得精确到毫秒的时间戳
        long microTs1 = ts1 + micro1;
        long microTs2 = ts2 + micro2;
        
        long divTs = microTs1 - microTs2;
        
        return divTs < 0 ? -1 : divTs == 0 ? 0 : 1;
    }
}
