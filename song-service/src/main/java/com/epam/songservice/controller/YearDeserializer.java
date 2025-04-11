package com.epam.songservice.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class YearDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        var rawValue = parser.getText();
        if (!rawValue.matches("^[1-2]\\d{3}$")) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseInt(rawValue);
    }
}
