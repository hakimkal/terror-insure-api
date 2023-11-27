package com.terron.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class utility {

    public static final String decodeToken(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        JsonNode rolesArray = jsonNode.get("roles");
        if (rolesArray != null && rolesArray.isArray() && rolesArray.size() > 0) {
            JsonNode firstRole = rolesArray.get(0);
            if (firstRole != null && firstRole.isTextual()) {
                return firstRole.asText();
            }
        }
        return null;
    }
}
