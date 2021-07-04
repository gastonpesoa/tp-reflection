package utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UConexion {

	private static UConexion uc;
	private static ResourceBundle rB;
	
	private UConexion() {
		rB = ResourceBundle.getBundle("utilidades.framework");
	}
	
	public static UConexion obtenerInstancia() {
		if(uc == null) {
			uc = new UConexion();	
		}
		return uc;
	}
	
	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(rB.getString("driver"));
			connection = DriverManager.getConnection(rB.getString("connstr"), rB.getString("user"), rB.getString("password"));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
