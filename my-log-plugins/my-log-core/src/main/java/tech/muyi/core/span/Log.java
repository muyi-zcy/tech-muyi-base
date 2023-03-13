package tech.muyi.core.span;

import java.util.Map;

/**
 * @author: muyi
 * @date: 2022/12/27
 **/
public class Log {

    public static final String EVENT_KEY = "event";
    public static final String EVENT_OBJECT = "error.object";

    private final long time;
    private final Map<String, ?> fields;

    public Log(long time, Map<String, ?> fields) {
        this.time = time;
        this.fields = fields;
    }

    public long getTime() {
        return time;
    }

    public Map<String, ?> getFields() {
        return fields;
    }

    public Object getField(String key){
        if(this.fields == null){
            return  null;
        }
        return this.fields.get(key);
    }
}
