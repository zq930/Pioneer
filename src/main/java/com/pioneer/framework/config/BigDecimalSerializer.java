package com.pioneer.framework.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal序列化处理
 *
 * @author hlm
 * @date 2021-08-10 17:33:05
 */
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // 设置精度：四舍五入保留2位
            value = value.setScale(2, RoundingMode.HALF_UP);
            // 返回
            gen.writeString(value.toString());
        }
    }
}
