package com.aluracursos.literalura.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne()
    private Autor autor;
    private String idioma;
    private Double numeroDeDownloads;

    public Livro() {
    }

    public Livro(DadosLivros dados, Autor autor) {
        this.titulo = dados.titulo();
        this.autor = autor;
        this.idioma = dados.idiomas().get(0);
        this.numeroDeDownloads = dados.numeroDeDownloads();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public Double getNumeroDeDownloads() {
        return numeroDeDownloads;
    }

    @Override
    public String toString() {
        return "----- LIVRO -----" +
                "\n Título: " + titulo +
                "\n Autor: " + autor.getNombre() +
                "\n Idioma: " + idioma +
                "\n Número de downloads: " + numeroDeDownloads +
                "\n-----------------\n";
    }
}
