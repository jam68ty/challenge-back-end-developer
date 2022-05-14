package com.example.ebankingbackend.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperUtil {
    public void objectCovert(Object source, Object target) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.registerModule(new JavaTimeModule());
            ObjectReader objectReader = objectMapper.readerForUpdating(target);

            objectReader.readValue(JSON.toJSONString(source));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
