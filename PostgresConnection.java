// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;


// public class PostgresConnection {

//     private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
//     private static final String USER = "postgres";
//     private static final String PASSWORD = "postgres";

//     public static Connection getConnection() throws SQLException {
//         return DriverManager.getConnection(URL, USER, PASSWORD);
//     }

//     public static void main(String[] args) {
//       System.out.println("Conexão!");

//         Connection conn = null;
//         PreparedStatement pstmt = null;
//         ResultSet rs = null;
//         Statement stmt = null;


//         try {
//             // Opcional: Carregar o driver explicitamente
//             Class.forName("org.postgresql.Driver");

//             // Estabelecendo a conexão
//             conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             System.out.println("Conexão estabelecida com sucesso!");

//             String sql = "SELECT id FROM cliente"; // Substitua 'tabela' pelo nome da sua tabela
//             pstmt = conn.prepareStatement(sql);
//             rs = pstmt.executeQuery();

//             while (rs.next()) {
//                 int id = rs.getInt("id");
//                 System.out.println("ID: " + id);
//             }

//         } catch (ClassNotFoundException e) {
//             System.out.println("Driver JDBC do PostgreSQL não encontrado.");
//             e.printStackTrace();
//         } catch (SQLException e) {
//             System.out.println("Erro ao conectar ou consultar o banco de dados:");
//             e.printStackTrace();
//         } finally {
//             try {
//                 if (rs != null) rs.close();
//                 if (pstmt != null) pstmt.close();
//                 if (conn != null) conn.close();
//             } catch (SQLException ex) {
//                 ex.printStackTrace();
//             }
//         }
//     }
// }

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Verifique se o nome do banco está correto
    private static final String USER = "postgres"; // Confirme o usuário
    private static final String PASSWORD = "postgres"; // Confirme a senha

    public static void main(String[] args) {
        System.out.println("Iniciando conexão...");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM cliente");
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Conexão estabelecida com sucesso!");

            while (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("ID: " + id);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ou consultar o banco de dados:");
            e.printStackTrace();
        }
    }
}
