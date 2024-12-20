package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.modelo.Autor;
import com.aluracursos.literalura.modelo.Dados;
import com.aluracursos.literalura.modelo.DadosLivros;
import com.aluracursos.literalura.modelo.Livro;
import com.aluracursos.literalura.repositorio.AutorRepository;
import com.aluracursos.literalura.repositorio.LivroRepository;
import com.aluracursos.literalura.servico.ConsumoAPI;
import com.aluracursos.literalura.servico.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import javax.naming.NameNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    @Autowired
    private final LivroRepository livroRepositorio;
    @Autowired
    private final AutorRepository autorRepositorio;

    private static final String URL_BASE = "https://gutendex.com/books/";
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private final Scanner teclado = new Scanner(System.in);
    private String json;

    public Principal(LivroRepository livroRepositorio, AutorRepository autorRepositorio) {
        this.livroRepositorio = livroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void mostraMenu() throws NameNotFoundException {
        var opcaoEscolhida = -1;

        while (opcaoEscolhida != 0) {
            json = consumoAPI.obterDados(URL_BASE);

            String menu = """
                    ------------
                    Escolha a opção através do seu número:
                    1- procurar livro por título
                    2- top 10 livros mais baixados
                    3- exibir estatísticas de downloads totais
                    4- procurar autor
                    5- listar livros registrados
                    6- lista de autores registrados
                    7- listar autores por ano de nascimento
                    8- listar autores por intervalo de anos
                    9- listar autores vivos por ano
                    10- listar livros por idioma
                    0- sair
                    """;
            System.out.println(menu);

            opcaoEscolhida = teclado.nextInt();
            teclado.nextLine();

            switch (opcaoEscolhida) {
                case 1 -> buscarLivroPorTitulo();
                case 2 -> listarTop10LivrosMaisBaixados(); //extra
                case 3 -> exibirEstatisticasDeDownloads(); //extra
                case 4 -> buscarAutor(); //extra
                case 5 -> listarLivrosRegistrados();
                case 6 -> listarAutoresRegistrados();
                case 7 -> listarAutoresPorAnoDeNascimento(); //extra
                case 8 -> listarAutoresPorIntervaloDeAnos(); //extra
                case 9 -> listarAutoresVivosEnAnoEspecifico();
                case 10 -> listarLivrosPorIdioma();
                case 0 -> System.out.println("Hasta luego...");
                default -> System.out.println("Opcion invalida");
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
            try{
                DadosLivros dadosLivros = encontrarLivro(livro);
                System.out.println(dadosLivros.autor());
            } catch (NullPointerException e){
                throw new NullPointerException();
            }
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

    private DadosLivros encontrarLivro(Optional<DadosLivros> livroBuscado) {
        if (livroBuscado.isPresent()) {
            System.out.println("Livro encontrado ");
            return livroBuscado.get();
        } else {
            System.out.println("Livro não encontrado");
            return null;
        }
    }

    private void listarLivrosRegistrados() {
        livroRepositorio.findAll(Sort.by(Sort.Direction.ASC, "titulo")).forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        autorRepositorio.findAll().forEach(System.out::println);
    }

    /* Consultas com datas (Autores) */
    private void listarAutoresPorAnoDeNascimento() {
        System.out.println("Insira o ano de nascimento do(s) autor(es) que deseja buscar: ");
        var anoNascimento = teclado.nextLine();

        if (!anoNascimento.isBlank()) {
            List<Autor> autoresBuscados = autorRepositorio.findByDataDeNascimento(Integer.valueOf(anoNascimento));

            if (!autoresBuscados.isEmpty()) {
                autoresBuscados.forEach(autor -> System.out.println(autor.toString()));
            } else {
                System.out.println("Não foram encontrados autores nascidos neste ano");
            }
        } else {
            System.out.println("Campo de texto vazio, por favor, tente novamente e insira um número válido.");
        }
    }

    private void listarAutoresPorIntervaloDeAnos() {
        System.out.println("Insira o ano inicial do intervalo: ");
        var anoInicial = teclado.nextLine();

        System.out.println("Insira o ano final do intervalo: ");
        var anoFinal = teclado.nextLine();

        if (!(anoInicial.isBlank() || anoFinal.isBlank())) {
            List<Autor> autoresBuscados = autorRepositorio.findByDataDeNascimentoGreaterThanEqualAndDataDeFalecimentoLessThan(
                    Integer.valueOf(anoInicial), Integer.valueOf(anoFinal));

            if (!autoresBuscados.isEmpty()) {
                autoresBuscados.forEach(autor -> System.out.println(autor.toString()));
            } else {
                System.out.println("Nenhum autor vivo foi encontrado neste intervalo de anos.");
            }
        } else {
            System.out.println("Campo(s) de texto vazio(s), por favor tente novamente e digite um número inteiro válido em cada campo.");
        }
    }

    private void listarAutoresVivosEnAnoEspecifico() {
        System.out.println("Digite o ano vivo de autor(es) que você deseja procurar: ");
        var anoVivo = teclado.nextLine();

        if (!anoVivo.isBlank()) {
            List<Autor> autoresBuscados = autorRepositorio.findByDataDeNascimentoLessThanEqualAndDataDeFalecimentoGreaterThan(
                    Integer.valueOf(anoVivo),
                    Integer.valueOf(anoVivo));

            if (!autoresBuscados.isEmpty()) {
                autoresBuscados.forEach(autor -> System.out.println(autor.toString()));
            } else {
                System.out.println("Nenhum autor vivo encontrado este ano.");
            }
        } else {
            System.out.println("Campo de texto vazio, tente novamente e digite um número inteiro válido.");
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Digite o idioma que você deseja procurar os livros: ");
        var idioma = teclado.nextLine();

        if (!idioma.isBlank()) {
            List<Livro> livrosPorIdioma = livroRepositorio.findByIdiomaContainingIgnoreCase(idioma);
            livrosPorIdioma.forEach(livro -> {
                System.out.println(livro.toString());
            });
        } else {
            System.out.println("Campo de texto vazio, tente novamente e digite um número inteiro válido.");
        }
    }
}
