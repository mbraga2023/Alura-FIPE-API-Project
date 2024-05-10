package com.alura.consultafipe.FIPE.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Marcas (@JsonAlias("codigo") int codigo,
                      @JsonAlias("nome") String nome){
}
