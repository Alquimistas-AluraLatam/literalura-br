package com.aluracursos.literalura;

import com.aluracursos.literalura.principal.Principal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.naming.NameNotFoundException;

@SpringBootApplication
public class LiteraluraApplication {

	public static void main(String[] args) throws NameNotFoundException {
		Principal principal = new Principal();
		principal.mostraMenu();
	}

}
