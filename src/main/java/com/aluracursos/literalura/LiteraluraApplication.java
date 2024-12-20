package com.aluracursos.literalura;

import com.aluracursos.literalura.principal.Principal;
import com.aluracursos.literalura.repositorio.AutorRepository;
import com.aluracursos.literalura.repositorio.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.naming.NameNotFoundException;

@SpringBootApplication
public class LiteraluraApplication {
	@Autowired
	private LivroRepository libroRepository;

	@Autowired
	private AutorRepository autorRepository;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}
	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroRepository, autorRepository);
		principal.mostraMenu();
	}
}
