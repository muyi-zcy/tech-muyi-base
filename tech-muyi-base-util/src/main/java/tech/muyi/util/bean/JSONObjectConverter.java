package tech.muyi.util.bean;

import com.alibaba.fastjson.JSONObject;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;


/**
 * fastjson JSONObject 透传转换器。
 *
 * <p>Orika 默认转换在处理动态 JSON 结构时可能尝试做字段级映射，
 * 这里显式返回原对象，保持 JSONObject 语义不变。</p>
 */
public class JSONObjectConverter extends CustomConverter<JSONObject, JSONObject> {
    @Override
    public JSONObject convert(JSONObject source, Type<? extends JSONObject> destinationType, MappingContext mappingContext) {
        // 直接透传，避免结构被重建或字段丢失。
        return source;
    }
}