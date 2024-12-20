package com.aluracursos.literalura.repositorio;

import com.aluracursos.literalura.modelo.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByIdiomaContainingIgnoreCase(String idioma);
}
