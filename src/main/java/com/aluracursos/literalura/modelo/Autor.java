package com.aluracursos.literalura.modelo;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Integer dataDeNascimento;
    private Integer dataDeFalecimento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livro> libros = new ArrayList<>();

    public Autor(DadosAutor datosAutor) {
        this.nome = String.valueOf(datosAutor.nome());
        this.dataDeNascimento = Integer.valueOf(datosAutor.dataDeNascimento());
        this.dataDeFalecimento = Integer.valueOf(datosAutor.dataDeFalecimento());
    }

    public Autor() {
    }

    public String getNombre() {
        return nome;
    }

    public Long getId() {
        return id;
    }

    private List<String> getNomeDeLivro() {
        return libros.stream().map(Livro::getTitulo).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "\n Autor: " + nome +
                "\n Data de nacimiento: " + dataDeNascimento +
                "\n Data de fallecimiento: " + dataDeFalecimento +
                "\n Livros: " + getNomeDeLivro()
                ;
    }
}
