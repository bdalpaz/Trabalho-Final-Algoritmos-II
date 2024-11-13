import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {
	public static String status = "Não conectou...";
	public ConexaoMySQL() {     }

public static java.sql.Connection getConexaoMySQL() {
	Connection connection = null;
	
	String driverName = "com.mysql.cj.jdbc.Driver";
	try {
		Class.forName(driverName);
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	}

	// Endereço do servidor do BD
	String serverName = "localhost";
	
	// Nome do seu banco de dados
	String mydatabase = "Driver DataBase Java";
	
	// String de Conexão.
	String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
	
	// Nome de um usuário de seu BD
	String username = "AliceDalPaz"; 
	
	// A senha de acesso do usuário informado acima.
	String password = "311447"; 
	
	try {
		connection = DriverManager.getConnection(url, username, password);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
	return connection;
	}
}