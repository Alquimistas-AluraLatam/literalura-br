package com.aluracursos.literalura.repositorio;

import com.aluracursos.literalura.modelo.Autor;
import com.aluracursos.literalura.modelo.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long>  {
    Autor findByNome(String nome);
    List<Autor> findByDataDeNascimento(Integer dataDeNascimiento);
    List<Autor> findByDataDeNascimentoGreaterThanEqualAndDataDeFalecimentoLessThan(Integer dataDeNascimento, Integer dataDeFalecimento);
    List<Autor> findByDataDeNascimentoLessThanEqualAndDataDeFalecimentoGreaterThan(Integer dataDeNascimento, Integer dataDeFalecimento);
    List<Autor> findAllByNomeContainingIgnoreCase(String nomeAutor);
}
