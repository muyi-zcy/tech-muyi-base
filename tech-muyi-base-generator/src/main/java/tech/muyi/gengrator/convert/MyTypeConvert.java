package tech.muyi.gengrator.convert;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.converts.TypeConverts;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

/**
 * @author: muyi
 * @date: 2023/3/2
 **/
public class MyTypeConvert extends MySqlTypeConvert {
    public static final MySqlTypeConvert INSTANCE = new MySqlTypeConvert();

    public MyTypeConvert() {
    }

    @Override
    public IColumnType processTypeConvert(GlobalConfig config, String fieldType) {
       return super.processTypeConvert(config,fieldType);
    }

    public static IColumnType toDateType(GlobalConfig config, String type) {
        String dateType = type.replaceAll("\\(\\d+\\)", "");
        switch (config.getDateType()) {
            case ONLY_DATE:
                return DbColumnType.LOCAL_DATE_TIME;
            case SQL_PACK:
                switch (dateType) {
                    case "date":
                    case "year":
                        return DbColumnType.DATE_SQL;
                    case "time":
                        return DbColumnType.TIME;
                    default:
                        return DbColumnType.TIMESTAMP;
                }
            case TIME_PACK:
                switch (dateType) {
                    case "date":
                        return DbColumnType.LOCAL_DATE;
                    case "time":
                        return DbColumnType.LOCAL_TIME;
                    case "year":
                        return DbColumnType.YEAR;
                    default:
                        return DbColumnType.LOCAL_DATE_TIME;
                }
            default:
                return DbColumnType.STRING;
        }
    }
}
