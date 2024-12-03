import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;



public class Main {


    // Conexão com o banco
    public static class PostgresConnection {
        private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
        private static final String USER = "postgres";
        private static final String PASSWORD = "postgres";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

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

            try {
                String input = scanner.nextLine();
                int op = Integer.parseInt(input);

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
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
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
        Cliente clienteCpf = new Cliente("", "", "");
        Motorista motoristaCpf = new Motorista("", "", "", "");
        switch (entidade) {
            case "Cliente" -> {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("CPF (11 dígitos): ");
                String cpf = scanner.nextLine();
                boolean validaCpf = clienteCpf.validaCpfDoCliente(cpf);
                if (!validaCpf) {
                    System.out.println("Cpf não é válido!");
                    return;
                }
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
                boolean validaCpf = motoristaCpf.validaCpfDoMotorista(cpf);
                if (!validaCpf) {
                    System.out.println("Cpf não é válido!");
                    return;
                }
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
            scanner.nextLine();
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


        boolean podeExcluir = switch (entidade) {
            case "Cliente" -> !Cliente.clienteTemViagemAndamento(id);
            case "Motorista" -> !Motorista.motoristaTemViagemAndamento(id);
            case "Produto" -> !Produto.produtoTemViagemAndamento(id);
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        };

        if (!podeExcluir) {
            System.out.println("Este " + entidade + " não pode ser removido, pois está associado(a) a uma viagem em andamento.");
            return;
        }

        // validação do usuário
        System.out.print("Tem certeza de que deseja excluir o " + entidade.toLowerCase() + " com ID " + id + "? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if (!confirmacao.equals("S")) {
            System.out.println(entidade + " não foi excluído(a).");
            return;
        }

        switch (entidade) {
            case "Cliente" -> new Cliente(id, null, null, null).excluirClienteDoBanco();
            case "Motorista" -> new Motorista(id, null, null, null, null).excluirMotoristaDoBanco();
            case "Produto" -> new Produto(id, null, 0).excluirProdutoDoBanco();
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        }

        System.out.println(entidade + " excluído(a) com sucesso.");
    }

    private static int buscarIdPorNome(String nome, String entidade) throws SQLException {
        if (nome == null || nome.isBlank()) {
            System.out.println("Nome não pode ser vazio ou nulo.");
            return -1;
        }

        switch (entidade) {
            case "Cliente" -> {
                List<Cliente> clientes = Cliente.listarClientes();
                for (Cliente cliente : clientes) {
                    if (cliente.getNome().equalsIgnoreCase(nome.trim())) {
                        return cliente.getId();
                    }
                }
            }
            case "Motorista" -> {
                List<Motorista> motoristas = Motorista.listarMotoristas();
                for (Motorista motorista : motoristas) {
                    if (motorista.getNome().equalsIgnoreCase(nome.trim())) {
                        return motorista.getId();
                    }
                }
            }
            case "Produto" -> {
                List<Produto> produtos = Produto.listarProdutos();
                for (Produto produto : produtos) {
                    if (produto.getNome().equalsIgnoreCase(nome.trim())) {
                        return produto.getId();
                    }
                }
            }
            default -> throw new IllegalArgumentException("Entidade inválida: " + entidade);
        }

        System.out.println(entidade + " não encontrado(a) com o nome especificado.");
        return -1;
    }
    private static void iniciarViagem (Scanner scanner) throws SQLException {
        System.out.print("Descrição da viagem: ");
        String descricao = scanner.nextLine();

        System.out.print("Cidade de origem: ");
        String cidadeOrigem = scanner.nextLine();

        System.out.print("Cidade de destino: ");
        String cidadeDestino = scanner.nextLine();

        System.out.print("ID do Motorista: ");
        int idMotorista = scanner.nextInt();
        scanner.nextLine();

        System.out.print("ID do Cliente: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine();

        Viagem viagem = new Viagem(descricao, cidadeOrigem, cidadeDestino, "Iniciada", idMotorista, idCliente);
        viagem.salvarViagemNoBanco();
        System.out.println("Viagem iniciada com sucesso!");
    }
    private static void finalizarViagem (Scanner scanner) throws SQLException {
        System.out.print("ID da viagem a ser finalizada: ");
        int idViagem = scanner.nextInt();
        scanner.nextLine();

        String sql = "SELECT * FROM viagem WHERE id = ?";
        try (Connection conn = Viagem.PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idViagem);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Viagem viagem = new Viagem(
                        rs.getInt("id"),
                        rs.getString("descricao"),
                        rs.getString("cidade_origem"),
                        rs.getString("cidade_destino"),
                        rs.getString("status"),
                        rs.getInt("motorista_id"),
                        rs.getInt("cliente_id")
                );

                if (!viagem.getStatus().equalsIgnoreCase("Iniciada")) {
                    System.out.println("Viagem não está em andamento.");
                    return;
                }

                viagem.setStatus("Finalizada");
                viagem.atualizarViagemNoBanco();
                System.out.println("Viagem finalizada com sucesso!");
            } else {
                System.out.println("Viagem não encontrada.");
            }
        }
    }
    private static void menuViagem(Scanner scanner) {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== Gerenciamento de Viagens ===");
            System.out.println("1. Iniciar Viagem");
            System.out.println("2. Finalizar Viagem");
            System.out.println("3. Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine();
                System.out.println(opcao);
                switch (opcao) {
                    case 1 -> {
                        List<Viagem> viagens = Viagem.listarViagens();
                        for (Viagem viagem : viagens) {
                            System.out.printf("Descrição: %s, Cidade de Origem: %s, Cidade de Destino: %s, ID do Motorista: %d, Id do Cliente: %d%n",
                                    viagem.getDescricao(), viagem.getCidadeOrigem(), viagem.getCidadeDestino(), viagem.getMotoristaId(), viagem.getClienteId());
                            System.out.println("Iniciando nova viagem...");
                            iniciarViagem(scanner);
                        return;
                        }
                    }
                    case 2 -> {
                        List<Viagem> viagens = Viagem.listarViagens();
                        for (Viagem viagem : viagens) {
                            System.out.printf("Descrição: %s, Cidade de Origem: %s, Cidade de Destino: %s, ID do Motorista: %d, Id do Cliente: %d%n",
                                    viagem.getDescricao(), viagem.getCidadeOrigem(), viagem.getCidadeDestino(), viagem.getMotoristaId(), viagem.getClienteId());
                            System.out.println("Finalizando viagem...");
                            finalizarViagem(scanner);
                            return;
                        }
                    }
                    case 3 -> continuar = false;
                    default -> System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao processar viagem: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Erro inesperado: " + e.getMessage());
                e.printStackTrace();
                scanner.nextLine();
            }
        }
    }
}