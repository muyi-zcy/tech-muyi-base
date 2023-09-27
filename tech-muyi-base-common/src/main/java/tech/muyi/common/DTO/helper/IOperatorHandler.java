package tech.muyi.common.DTO.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public interface IOperatorHandler {
    String convertOperator(Object operator);
}
