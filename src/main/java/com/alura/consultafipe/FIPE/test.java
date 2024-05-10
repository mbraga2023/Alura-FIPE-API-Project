package com.alura.consultafipe.FIPE;

import com.alura.consultafipe.FIPE.models.VersoesComPreco;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

public class test {
        public static void main(String[] args) {
            String json = "{\"TipoVeiculo\": 1, \"Valor\": \"R$ 191.218,00\", \"Marca\": \"VW - VolksWagen\", \"Modelo\": \"AMAROK High.CD 2.0 16V TDI 4x4 Dies. Aut\", \"AnoModelo\": 2021, \"Combustivel\": \"Diesel\", \"CodigoFipe\": \"005340-6\", \"MesReferencia\": \"maio de 2024\", \"SiglaCombustivel\": \"D\"}";

            // Extract numeric part of Valor and remove non-numeric characters
            String numericValor = json.replaceAll("[^0-9,]", "");

            // Replace ',' with '.' for proper decimal format
            numericValor = numericValor.replace(",", ".");

            // Ensure there's only one decimal point in the numeric value
            numericValor = numericValor.replaceFirst("\\.", "");

            // Convert the numeric value to BigDecimal
            BigDecimal valor = new BigDecimal(numericValor);

            // Deserialize the JSON string with the corrected numeric value
            ObjectMapper mapper = new ObjectMapper();
            try {
                VersoesComPreco versao = mapper.readValue(json.replace("R$ " + numericValor, valor.toString()), VersoesComPreco.class);
                System.out.println(versao);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
}