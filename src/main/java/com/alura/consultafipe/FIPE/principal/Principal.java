package com.alura.consultafipe.FIPE.principal;

import com.alura.consultafipe.FIPE.models.Marcas;
import com.alura.consultafipe.FIPE.models.Modelos;
import com.alura.consultafipe.FIPE.models.Versoes;
import com.alura.consultafipe.FIPE.models.VersoesComPreco;
import com.alura.consultafipe.FIPE.service.ConsumoApi;
import com.alura.consultafipe.FIPE.service.ConverteDados;
import com.alura.consultafipe.FIPE.service.ConverteDadosUnitario;
import com.alura.consultafipe.FIPE.service.ConverteDadosModelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private static final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private static final ConsumoApi consumo = new ConsumoApi();
    private static final ConverteDados conversor = new ConverteDados();
    private static final ConverteDadosModelo conversorModelos = new ConverteDadosModelo();

    public static void exibeMenu() {

        Scanner scanner = new Scanner(System.in);
        boolean menu=true;

        while (menu){
            System.out.println("Tabela FIPE");

            //menu
            System.out.print(""" 
                ******** OPÇÕES *******
                1) Carro
                2) Moto
                3) Caminhão
                4) Sair
                Digite uma opção:""");
            int option = scanner.nextInt();

            switch (option){
                case 1:
                    consultaVeiculo("carros");
                    break;
                case 2:
                    consultaVeiculo("motos");
                    break;
                case 3:
                    consultaVeiculo("caminhoes");
                    break;
                case 4:
                    menu = false;
                    break;
                default:
                    break;

            }
        }

    }

    private static void consultaVeiculo(String tipo) {
        String tipoVeiculo = tipo + "/marcas";
        var json = consumo.obterDados(URL_BASE + tipoVeiculo);
        List<Marcas> jsonMarcas = conversor.obterDados(json, Marcas.class);

        // Calculate the number of brands per column
        int brandsPerColumn = (int) Math.ceil((double) jsonMarcas.size() / 4);

// Display the menu of brands in four columns
        System.out.println("Menu de Marcas:");
        for (int i = 0; i < brandsPerColumn; i++) {
            int index2 = i + brandsPerColumn;
            int index3 = i + 2 * brandsPerColumn;
            int index4 = i + 3 * brandsPerColumn;

            String marca1 = i < jsonMarcas.size() ? formatBrand(jsonMarcas.get(i)) : "";
            String marca2 = index2 < jsonMarcas.size() ? formatBrand(jsonMarcas.get(index2)) : "";
            String marca3 = index3 < jsonMarcas.size() ? formatBrand(jsonMarcas.get(index3)) : "";
            String marca4 = index4 < jsonMarcas.size() ? formatBrand(jsonMarcas.get(index4)) : "";

            System.out.printf("%-30s%-30s%-30s%s%n", marca1, marca2, marca3, marca4);
        }

        // Pedindo a Marca
        Scanner scanner = new Scanner(System.in);
        System.out.print("Escolha uma marca (digite o número correspondente): ");
        int buscaCodigoMarca = scanner.nextInt();
        String marcaEscolhida = null;

        for (Marcas marca : jsonMarcas) {
            if (marca.codigo() == buscaCodigoMarca) {
                marcaEscolhida = marca.nome();
                break;
            }
        }

        if (marcaEscolhida != null) {
            System.out.println("Você escolheu: " + marcaEscolhida);
        } else {
            System.out.println("Opção inválida. Por favor, escolha um número correspondente à marca.");
        }

        System.out.println("**************************");

        String modelosJson = consumo.obterDados(URL_BASE + tipoVeiculo + "/" + buscaCodigoMarca + "/modelos");
        List<Modelos> modelosList = conversorModelos.obterDados(modelosJson, Modelos.class);

        System.out.println("Modelos disponíveis para a marca " + marcaEscolhida + ":");
        for (Modelos modelo : modelosList) {
            System.out.println(modelo.codigo() + " - " + modelo.nome());
        }

        System.out.println("****************************");
        System.out.print("Digite o código do modelo ou parte do nome: ");
        scanner = new Scanner(System.in);
        String buscaModelo = scanner.nextLine();

        String modeloEscolhido = null;

        boolean isNumeric = buscaModelo.matches("\\d+");

        if (isNumeric) {
            // If busca is an integer, directly search for the model with that codigo
            for (Modelos modelo : modelosList) {
                if (modelo.codigo().equals(buscaModelo)) {
                    modeloEscolhido = modelo.nome();
                    break;
                }
            }
        } else {
            // If busca is not an integer, search for models with the term in their nome
            List<Modelos> matchingModelos = new ArrayList<>();
            for (Modelos modelo : modelosList) {
                if (modelo.nome().toLowerCase().contains(buscaModelo.toLowerCase())) {
                    matchingModelos.add(modelo);
                }
            }
            if (!matchingModelos.isEmpty()) {
                System.out.println("Modelos encontrados com o termo '" + buscaModelo + "':");
                for (Modelos modelo : matchingModelos) {
                    System.out.println(modelo.codigo() + " - " + modelo.nome());
                }
                System.out.print("Digite o código do modelo desejado: ");
                buscaModelo = scanner.nextLine();

                for (Modelos modelo : matchingModelos) {
                    if (modelo.codigo().equals(buscaModelo)) {
                        modeloEscolhido = modelo.nome();
                        break;
                    }
                }
            } else {
                System.out.println("Nenhum modelo encontrado com o código ou similaridade fornecida.");
            }
        }

        if (modeloEscolhido != null) {
            System.out.println("Modelos disponíveis para: " + modeloEscolhido);
        } else {
            System.out.println("Opção inválida. Por favor, escolha um número correspondente ao modelo.");
        }



        String modelosDisponiveis = consumo.obterDados(URL_BASE + tipoVeiculo + "/" + buscaCodigoMarca + "/modelos/" + buscaModelo + "/anos");
        List<Versoes> versoesList = conversor.obterDados(modelosDisponiveis, Versoes.class);

        for (Versoes versoes : versoesList) {
            String modeloConsultadoComPreco = consumo.obterDados(URL_BASE + tipoVeiculo + "/" + buscaCodigoMarca + "/modelos/" + buscaModelo + "/anos/" + versoes.codigo());

            // Use the new ConverteDadosUnitario service to deserialize individual JSON objects
            VersoesComPreco versaoComPreco = new ConverteDadosUnitario().obterDados(modeloConsultadoComPreco, VersoesComPreco.class);

            //System.out.println("Versões com Preço para " + modeloEscolhido + ":");
            // Print the details of the deserialized object
            System.out.println(
                    //"Tipo de Veículo: " + versaoComPreco.tipoVeiculo() +
                    " Valor: " + versaoComPreco.valor() +
                            //", Marca: " + versaoComPreco.marca() +
                            //", Modelo: " + versaoComPreco.modelo() +
                            ", Ano Modelo: " + versaoComPreco.anoModelo() +
                            ", Combustível: " + versaoComPreco.combustivel() +
                            //", Código FIPE: " + versaoComPreco.codigoFipe() +
                            ", Mês de Referência: " + versaoComPreco.mesReferencia());
            //+ ", Sigla Combustível: " + versaoComPreco.siglaCombustivel())

        }
        System.out.println("*******************");


    }
    // Format brand for display
    private static String formatBrand(Marcas marca) {
        return marca.codigo() + " - " + marca.nome();
    }


}

