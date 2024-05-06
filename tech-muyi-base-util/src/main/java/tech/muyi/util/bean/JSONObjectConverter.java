package tech.muyi.util.bean;

import com.alibaba.fastjson.JSONObject;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;


public class JSONObjectConverter extends CustomConverter<JSONObject, JSONObject> {
    @Override
    public JSONObject convert(JSONObject source, Type<? extends JSONObject> destinationType, MappingContext mappingContext) {
        return source;
    }
}