package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.modelo.Dados;
import com.aluracursos.literalura.modelo.DadosLivros;
import com.aluracursos.literalura.servico.ConsumoAPI;
import com.aluracursos.literalura.servico.ConverteDados;

import javax.naming.NameNotFoundException;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private Scanner teclado = new Scanner(System.in);
    private String json;
    private String menu = """
            ------------
            Escolha a opção através do seu número:
            1- Buscar livro por título
            2- Top 10 livros mais baixados
            3- Exibir estatísticas de downloads
            4- Buscar autor
            0- Sair
            """;

    public void mostraMenu() throws NameNotFoundException {
        var opcaoEscolhida = -1;

        while (opcaoEscolhida != 0) {
            json = consumoAPI.obterDados(URL_BASE);
            System.out.println(menu);

            opcaoEscolhida = teclado.nextInt();
            teclado.nextLine();

            switch (opcaoEscolhida) {
                case 1 -> buscarLivroPorTitulo();
                case 2 -> listarTop10LivrosMaisBaixados();
                case 3 -> exibirEstatisticasDeDownloads();
                case 4 -> buscarAutor();
                case 0 -> System.out.println("Até logo...");
                default -> System.out.println("Opção inválida");
            }
        }

    }

    private void buscarAutor() throws NameNotFoundException {
        System.out.println("Insira o nome do autor que deseja buscar: ");
        var nomeAutor = teclado.nextLine();

        json = consumoAPI.obterDados(URL_BASE + "?search=" + nomeAutor.replace(" ", "+"));
        var dadosBusca = conversor.obterDados(json, Dados.class);

        Optional<DadosLivros> livro = dadosBusca.resultados().stream().filter(l -> l.autor().get(0).nome().toUpperCase().contains(nomeAutor.toUpperCase())).findFirst();

        if(livro.isPresent()){
            DadosLivros dadosLivros = encontrarLivro(livro);
            System.out.println(dadosLivros.autor());
        } else {
            throw new NameNotFoundException("Nome de autor não encontrado.");
        }
    }


    private Dados getDadosWeb() {
        return conversor.obterDados(json, Dados.class);
    }

    private void exibirEstatisticasDeDownloads() {
        Dados datos = getDadosWeb();
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDeDownloads() > 0)
                .collect(Collectors.summarizingDouble(DadosLivros::numeroDeDownloads));
        System.out.println("Quantidade média de downloads: " + est.getAverage());
        System.out.println("Quantidade máxima de downloads: " + est.getMax());
        System.out.println("Quantidade mínima de downloads: " + est.getMin());
        System.out.println("Quantidade de registros avaliados para calcular as estatísticas: " + est.getCount());
    }

    private void listarTop10LivrosMaisBaixados() {
        System.out.println("Top 10 livros mais baixados");

        Dados dados = getDadosWeb();

        dados.resultados().stream()
                .sorted(Comparator.comparing(DadosLivros::numeroDeDownloads).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase() + " de " + l.autor().get(0).nome().toUpperCase())
                .forEach(System.out::println);
    }

    private void buscarLivroPorTitulo() {
        System.out.println("Insira o nome do livro que deseja buscar: ");
        var tituloLivro = teclado.nextLine();

        json = consumoAPI.obterDados(URL_BASE + "?search=" + tituloLivro.replace(" ", "+"));
        var datosBusqueda = conversor.obterDados(json, Dados.class);

        Optional<DadosLivros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo()
                        .toUpperCase()
                        .contains(tituloLivro.toUpperCase()))
                .findFirst();
        DadosLivros dadosLivros = encontrarLivro(libroBuscado);

        System.out.println(dadosLivros);
    }

    private DadosLivros encontrarLivro(Optional<DadosLivros> libroBuscado) {
        if (libroBuscado.isPresent()) {
            System.out.println("Livro encontrado ");
            return libroBuscado.get();
        } else {
            System.out.println("Livro não encontrado");
            return null;
        }
    }
}
