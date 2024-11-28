import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== Sistema de Gerenciamento ===");
            System.out.println("1. Gerenciar Clientes");
            System.out.println("2. Gerenciar Motoristas");
            System.out.println("3. Gerenciar Produtos");
            System.out.println("4. Gerenciar Viagens");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            int op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {
                case 1 -> menuEntidade(scanner, "Cliente");
                case 2 -> menuEntidade(scanner, "Motorista");
                case 3 -> menuEntidade(scanner, "Produto");
                case 4 -> menuViagem(scanner);
                case 5 -> {
                    continuar = false;
                    System.out.println("Saindo do sistema...");
                }
                default -> System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void menuEntidade(Scanner scanner, String entidade) {
        boolean continuar = true;

        while (continuar) {
            System.out.printf("\n=== Gerenciamento de %s ===%n", entidade + "s");
            System.out.println("1. Cadastrar " + entidade);
            System.out.println("2. Listar " + entidade + "s");
            System.out.println("3. Atualizar " + entidade);
            System.out.println("4. Excluir " + entidade);
            System.out.println("5. Voltar");
            System.out.print("Escolha uma opção: ");

            int op = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (op) {
                    case 1 -> cadastrarEntidade(scanner, entidade);
                    case 2 -> listarEntidades(entidade);
                    case 3 -> atualizarEntidade(scanner, entidade);
                    case 4 -> excluirEntidade(scanner, entidade);
                    case 5 -> continuar = false;
                    default -> System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao processar a operação: " + e.getMessage());
            }
        }
    }

    private static void cadastrarEntidade(Scanner scanner, String entidade) throws SQLException {
        switch (entidade) {
            case "Cliente" -> {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("CPF (11 dígitos): ");
                String cpf = scanner.nextLine();
                System.out.print("Cidade: ");
                String cidade = scanner.nextLine();
                Cliente cliente = new Cliente(nome, cpf, cidade);
                cliente.salvarClienteNoBanco();
            }
            case "Motorista" -> {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("CPF (11 dígitos): ");
                String cpf = scanner.nextLine();
                System.out.print("CNH: ");
                String cnh = scanner.nextLine();
                System.out.print("Cidade: ");
                String cidade = scanner.nextLine();
                Motorista motorista = new Motorista(nome, cpf, cnh, cidade);
                motorista.salvarMotoristaNoBanco();
            }
            case "Produto" -> {
                System.out.print("Nome do Produto: ");
                String nome = scanner.nextLine();
                System.out.print("Quantidade: ");
                int quantidade = scanner.nextInt();
                scanner.nextLine();
                Produto produto = new Produto(nome, quantidade);
                produto.salvarProdutoNoBanco();
            }
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        }
    }

    private static void listarEntidades(String entidade) throws SQLException {
        switch (entidade) {
            case "Cliente" -> {
                List<Cliente> clientes = Cliente.listarClientes();
                for (Cliente cliente : clientes) {
                    System.out.printf("ID: %d, Nome: %s, CPF: %s, Cidade: %s%n",
                            cliente.getId(), cliente.getNome(), cliente.getCpf(), cliente.getCidade());
                }
            }
            case "Motorista" -> {
                List<Motorista> motoristas = Motorista.listarMotoristas();
                for (Motorista motorista : motoristas) {
                    System.out.printf("ID: %d, Nome: %s, CPF: %s, CNH: %s, Cidade: %s%n",
                            motorista.getId(), motorista.getNome(), motorista.getCpf(), motorista.getCnh(),
                            motorista.getCidade());
                }
            }
            case "Produto" -> {
                List<Produto> produtos = Produto.listarProdutos();
                for (Produto produto : produtos) {
                    System.out.printf("ID: %d, Nome: %s, Quantidade: %d%n",
                            produto.getId(), produto.getNome(), produto.getQuantidade());
                }
            }
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        }
    }

    private static void atualizarEntidade(Scanner scanner, String entidade) throws SQLException {
        System.out.println("Deseja buscar por (1) ID ou (2) Nome?");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        int id = -1;

        if (escolha == 1) {
            System.out.print("Digite o ID do " + entidade.toLowerCase() + " a ser atualizado: ");
            id = scanner.nextInt();
            scanner.nextLine();
        } else if (escolha == 2) {
            System.out.print("Digite o Nome do " + entidade.toLowerCase() + " a ser atualizado: ");
            String nome = scanner.nextLine();
            id = buscarIdPorNome(nome, entidade);
            if (id == -1) {
                System.out.println(entidade + " não encontrado(a) com o nome especificado.");
                return;
            }
        } else {
            System.out.println("Opção inválida!");
            return;
        }

        switch (entidade) {
            case "Cliente" -> {
                System.out.print("Novo Nome: ");
                String nome = scanner.nextLine();
                System.out.print("Novo CPF: ");
                String cpf = scanner.nextLine();
                System.out.print("Nova Cidade: ");
                String cidade = scanner.nextLine();
                Cliente cliente = new Cliente(id, nome, cpf, cidade);
                cliente.atualizarClienteNoBanco();
            }
            case "Motorista" -> {
                System.out.print("Novo Nome: ");
                String nome = scanner.nextLine();
                System.out.print("Novo CPF: ");
                String cpf = scanner.nextLine();
                System.out.print("Nova CNH: ");
                String cnh = scanner.nextLine();
                System.out.print("Nova Cidade: ");
                String cidade = scanner.nextLine();
                Motorista motorista = new Motorista(id, nome, cpf, cnh, cidade);
                motorista.atualizarMotoristaNoBanco();
            }
            case "Produto" -> {
                System.out.print("Novo Nome do Produto: ");
                String nome = scanner.nextLine();
                System.out.print("Nova Quantidade: ");
                int quantidade = scanner.nextInt();
                scanner.nextLine();
                Produto produto = new Produto(id, nome, quantidade);
                produto.atualizarProdutoNoBanco();
            }
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        }
    }

    private static void excluirEntidade(Scanner scanner, String entidade) throws SQLException {
        System.out.println("Deseja buscar por (1) ID ou (2) Nome?");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        int id = -1;

        if (escolha == 1) {
            System.out.print("Digite o ID do " + entidade.toLowerCase() + " a ser excluído: ");
            id = scanner.nextInt();
        } else if (escolha == 2) {
            System.out.print("Digite o Nome do " + entidade.toLowerCase() + " a ser excluído: ");
            String nome = scanner.nextLine();
            id = buscarIdPorNome(nome, entidade);
            if (id == -1) {
                System.out.println(entidade + " não encontrado(a) com o nome especificado.");
                return;
            }
        } else {
            System.out.println("Opção inválida!");
            return;
        }

        switch (entidade) {
            case "Cliente" -> new Cliente(id, null, null, null).excluirClienteDoBanco();
            case "Motorista" -> new Motorista(id, null, null, null, null).excluirMotoristaDoBanco();
            case "Produto" -> new Produto(id, null, 0).excluirProdutoDoBanco();
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        }
    }

    private static int buscarIdPorNome(String nome, String entidade) throws SQLException {
        switch (entidade) {
            case "Cliente" -> {
                List<Cliente> clientes = Cliente.listarClientes();
                for (Cliente cliente : clientes) {
                    if (cliente.getNome().equalsIgnoreCase(nome)) {
                        return cliente.getId();
                    }
                }
            }
            case "Motorista" -> {
                List<Motorista> motoristas = Motorista.listarMotoristas();
                for (Motorista motorista : motoristas) {
                    if (motorista.getNome().equalsIgnoreCase(nome)) {
                        return motorista.getId();
                    }
                }
            }
            case "Produto" -> {
                List<Produto> produtos = Produto.listarProdutos();
                for (Produto produto : produtos) {
                    if (produto.getNome().equalsIgnoreCase(nome)) {
                        return produto.getId();
                    }
                }
            }
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        }
        return -1;
    }

    private static void menuViagem(Scanner scanner) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== Gerenciamento de Viagens ===");
            System.out.println("1. Iniciar Viagem");
            System.out.println("2. Finalizar Viagem");
            System.out.println("3. Voltar");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (opcao) {
                    case 1 -> iniciarViagem(scanner);
                    case 2 -> finalizarViagem(scanner);
                    case 3 -> continuar = false;
                    default -> System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao processar viagem: " + e.getMessage());
            }
        }
    }

    private static void iniciarViagem(Scanner scanner) throws SQLException {
        // Implementar
    }

    private static void finalizarViagem(Scanner scanner) throws SQLException {
        // Implementar
    }
}
