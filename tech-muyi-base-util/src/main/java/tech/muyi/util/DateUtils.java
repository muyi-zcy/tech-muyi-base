package tech.muyi.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类
 *
 * @Author: muyi
 * @Date: 2021/1/3 22:38
 */
public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public final static long ONE_DAY_SECONDS = 86400;

    public final static String shortFormat = "yyyyMMdd";

    public final static String longFormat = "yyyyMMddHHmmss";

    public final static String webFormat = "yyyy-MM-dd";

    public final static String timeFormat = "HHmmss";

    public final static String monthFormat = "yyyyMM";

    public final static String chineseDtFormat = "yyyy年MM月dd日";

    public final static String newFormat = "yyyy-MM-dd HH:mm:ss";

    public final static String fullDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";


    public static long ONE_DAY_MILL_SECONDS = 86400000;

    /**
     * 获取当前日期（默认）
     *
     * @return 日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取日期（输入毫秒）
     *
     * @param millsecord 毫秒
     * @return 日期
     */
    public static Date getDate(long millsecord) {
        return new Date(millsecord);
    }

    /**
     * 字符串转DateFormat
     *
     * @param pattern 日期格式
     * @return DateFormat
     */
    public static DateFormat formatDate(String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        df.setLenient(false);
        return df;
    }


    /**
     * 格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 日期
     */
    public static Date formatDate(Date date, String pattern) {
        return formatDate(date, formatDate(pattern));
    }

    /**
     * 格式化日期
     *
     * @param date   日期
     * @param format 日期格式
     * @return 日期
     */
    public static Date formatDate(Date date, DateFormat format) {
        String dateString = format.format(date);
        ParsePosition pos = new ParsePosition(0);
        return format.parse(dateString, pos);
    }


    /**
     * 格式化时间戳日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 格式化之后日期
     */
    public static String formatByTimestamp(long date, String pattern) {
        return formatDate(pattern).format(new Date(date));
    }

    /**
     * 格式化时间戳日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 格式化之后日期
     */
    public static String formatByTimestamp(String date, String pattern) {
        if (StringUtils.isNotBlank(date)) {
            return formatByTimestamp(NumberUtils.toLong(date), pattern);
        }
        return StringUtils.EMPTY;
    }


    /**
     * 计算当前时间几天之后的时间
     *
     * @param date 日期
     * @param days 天数
     * @return 新的日期
     */
    public static Date addDays(Date date, long days) {
        return addSeconds(date, days * ONE_DAY_SECONDS);
    }


    /**
     * 计算当前时间几小时之后的时间
     *
     * @param date  日期
     * @param hours 增加时间
     * @return 日期
     */
    public static Date addHours(Date date, long hours) {
        return addMinutes(date, hours * 60);
    }

    /**
     * 计算当前时间几分钟之后的时间
     *
     * @param date    日期
     * @param minutes 增加分钟
     * @return 日期
     */
    public static Date addMinutes(Date date, long minutes) {
        return addSeconds(date, minutes * 60);
    }

    /**
     * 计算当前时间几秒之后的时间
     *
     * @param date 日期
     * @param secs 增加秒钟
     * @return 日期
     */

    public static Date addSeconds(Date date, long secs) {
        return new Date(date.getTime() + (secs * 1000));
    }

    /**
     * 判断输入的字符串是否为合法的小时格式
     *
     * @param hourStr 待判断时间
     * @return true/false
     */
    public static boolean isValidHour(String hourStr) {
        if (!StringUtils.isEmpty(hourStr) && StringUtils.isNumeric(hourStr)) {
            int hour = Integer.parseInt(hourStr);

            if ((hour >= 0) && (hour <= 23)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断输入的字符串是否为合法的分或秒
     *
     * @param str 待判断的时间
     * @return true/false
     */
    public static boolean isValidMinuteOrSecond(String str) {
        if (!StringUtils.isEmpty(str) && StringUtils.isNumeric(str)) {
            int hour = Integer.parseInt(str);
            return (hour >= 0) && (hour <= 59);
        }
        return false;
    }


    /**
     * 取得两个日期间隔毫秒数
     *
     * @param one 较小是日期
     * @param two 较大的日期
     * @return 间隔毫秒数
     */
    public static long getDiffMilliseconds(Date one, Date two) {
        Calendar sysDate = new GregorianCalendar();

        sysDate.setTime(one);

        Calendar failDate = new GregorianCalendar();

        failDate.setTime(two);
        return (sysDate.getTimeInMillis() - failDate.getTimeInMillis());
    }

    /**
     * 取得两个日期间隔秒数
     *
     * @param one 较小是日期
     * @param two 较大的日期
     * @return 间隔秒数
     */
    public static long getDiffSeconds(Date one, Date two) {
        Calendar sysDate = new GregorianCalendar();

        sysDate.setTime(one);

        Calendar failDate = new GregorianCalendar();

        failDate.setTime(two);
        return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / 1000;
    }

    /**
     * 取得两个日期间分钟数
     *
     * @param one 较小是日期
     * @param two 较大的日期
     * @return 间隔分钟数
     */
    public static long getDiffMinutes(Date one, Date two) {
        Calendar sysDate = new GregorianCalendar();

        sysDate.setTime(one);

        Calendar failDate = new GregorianCalendar();

        failDate.setTime(two);
        return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / (60 * 1000);
    }

    /**
     * 取得两个日期的间隔天数
     *
     * @param one 较小是日期
     * @param two 较大的日期
     * @return 间隔天数
     */
    public static long getDiffDays(Date one, Date two) {
        Calendar sysDate = new GregorianCalendar();

        sysDate.setTime(one);

        Calendar failDate = new GregorianCalendar();

        failDate.setTime(two);
        return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);
    }


    /**
     * 切换不同日期格式日期
     *
     * @param dateString 处理日期
     * @param formatIn   输入日期格式
     * @param formatOut  输出日期格式
     * @return 新格式日期
     */
    public static String convert(String dateString, DateFormat formatIn, DateFormat formatOut) {
        try {
            Date date = formatIn.parse(dateString);
            return formatOut.format(date);
        } catch (ParseException e) {
            return "";
        }
    }


    /**
     * 比较日期大小
     *
     * @param date1  被比较日期
     * @param date2  比较日期
     * @param format 日期格式
     * @return 比较结果
     */
    public static boolean dateNotLessThan(String date1, String date2,
                                          DateFormat format) {
        try {
            Date d1 = format.parse(date1);
            Date d2 = format.parse(date2);

            if (d1.before(d2)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 转换为sql Date
     *
     * @param value 日期
     * @return date
     */
    public static java.sql.Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }
        if (value instanceof Date) {
            return new java.sql.Date(((Date) value).getTime());
        }
        if (value instanceof Calendar) {
            return new java.sql.Date(((Calendar) value).getTime().getTime());
        }
        if (value instanceof String) {
            return java.sql.Date.valueOf((String) value);
        }
        if (value instanceof Number) {
            return new java.sql.Date(((Number) value).longValue());
        }
        if (value instanceof LocalDate) {
            return java.sql.Date.valueOf((LocalDate) value);
        }
        String m = "Unable to convert [" + value.getClass().getName() + "] into a java.sql.Date.";
        throw new RuntimeException(m);
    }

    /**
     * 转换为时间戳
     *
     * @param value 日期
     * @return Timestamp
     */
    public static Timestamp toTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        }
        if (value instanceof Calendar) {
            return new Timestamp(((Calendar) value).getTime().getTime());
        }
        if (value instanceof String) {
            LocalDateTime date = LocalDateTime.parse((String) value);
            return Timestamp.valueOf(date);
        } else if (value instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) value);
        } else {
            if (value instanceof Number) {
                return new Timestamp(((Number) value).longValue());
            }
            String msg = "Unable to convert [" + value.getClass().getName() + "] into a Timestamp.";
            throw new RuntimeException(msg);
        }
    }

    /**
     * 转换为time
     *
     * @param value 时间
     * @return time
     */
    public static Time toTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Time) {
            return (Time) value;
        }
        if (value instanceof String) {
            return Time.valueOf((String) value);
        }
        if (value instanceof LocalTime) {
            return Time.valueOf((LocalTime) value);
        }
        String m = "Unable to convert [" + value.getClass().getName() + "] into a java.sql.Date.";
        throw new RuntimeException(m);
    }

    /**
     * 转换为util date
     *
     * @param value 日期
     * @return util date
     */
    public static Date toUtilDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return new Date(((Timestamp) value).getTime());
        }
        if (value instanceof java.sql.Date) {
            return new Date(((java.sql.Date) value).getTime());
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }
        if (value instanceof String) {
            return new Date(Timestamp.valueOf((String) value).getTime());
        }
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        throw new RuntimeException("Unable to convert [" + value.getClass().getName() + "] into a java.util.Date");
    }

    /**
     * 转换为 Calendar
     *
     * @param value Calendar
     * @return Calendar
     */
    public static Calendar toCalendar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return (Calendar) value;
        }
        if (value instanceof Date) {
            Date date = (Date) value;
            return toCalendarFromDate(date);
        } else if (value instanceof String) {
            Date date2 = toUtilDate(value);
            return toCalendarFromDate(date2);
        } else if (value instanceof Number) {
            Date date3 = new Date(((Number) value).longValue());
            return toCalendarFromDate(date3);
        } else {
            String m = "Unable to convert [" + value.getClass().getName() + "] into a java.util.Date";
            throw new RuntimeException(m);
        }
    }

    private static Calendar toCalendarFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}