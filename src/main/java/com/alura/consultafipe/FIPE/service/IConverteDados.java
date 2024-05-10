package com.alura.consultafipe.FIPE.service;

import java.util.List;

public interface IConverteDados {
    <T> List<T> obterDados(String json, Class<T> classe);

}
