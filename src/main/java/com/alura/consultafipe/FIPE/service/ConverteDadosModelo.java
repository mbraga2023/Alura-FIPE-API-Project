package com.alura.consultafipe.FIPE.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ConverteDadosModelo implements IConverteDados {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> List<T> obterDados(String json, Class<T> classe) {
        try {
            JsonNode rootNode = mapper.readTree(json);
            List<T> resultList = new ArrayList<>();

            // Parse modelos array
            JsonNode modelosNode = rootNode.path("modelos");
            if (modelosNode.isArray()) {
                List<T> modelosList = mapper.readValue(modelosNode.traverse(),
                        mapper.getTypeFactory().constructCollectionType(List.class, classe));
                resultList.addAll(modelosList);
            } else {
                throw new RuntimeException("Missing or invalid 'modelos' array in JSON");
            }

            // Parse anos array
           /* JsonNode anosNode = rootNode.path("anos");
            if (anosNode.isArray()) {
                List<T> anosList = mapper.readValue(anosNode.traverse(),
                        mapper.getTypeFactory().constructCollectionType(List.class, classe));
                resultList.addAll(anosList);
            } else {
                throw new RuntimeException("Missing or invalid 'anos' array in JSON");
            }*/

            return resultList;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}
