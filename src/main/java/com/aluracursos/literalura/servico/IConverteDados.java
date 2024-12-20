package com.aluracursos.literalura.servico;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
}