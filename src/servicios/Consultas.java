package servicios;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;



import anotaciones.Columna;
import anotaciones.Tabla;
import utilidades.UBean;

public class Consultas {
	
	public Consultas() {
		super();
	}

	public static void guardar(Object o) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.64.4/test", "root", "root");
			System.out.println("conectado");
			
			Class reflector = o.getClass();
			Tabla tabla = (Tabla) reflector.getAnnotation(Tabla.class);
			
			String query = "INSERT INTO " + tabla.nombre() + " (";
			
			//System.out.println("query: "+ query);
			
			ArrayList<Field> fields = UBean.obtenerAtributos(o);
			for(Field field: fields) {
				Columna columna = field.getAnnotation(Columna.class);
				if(columna!=null) {
					query += field.getAnnotation(Columna.class).nombre() + ", ";
					System.out.println("query: "+ query);		
				}
			}
			query = query.substring(0, query.length()-2);
			query += ") VALUES (";
			for(Field field: fields) {
				Columna columna = field.getAnnotation(Columna.class);
				if(columna!=null) {
					if(field.getType().equals(String.class)) {
						query += "'" + UBean.ejecutarGet(o, field.getName()) + "'" + ", ";
					} else {
						query += UBean.ejecutarGet(o, field.getName()) + ", ";
					}
				}
			}
			query = query.substring(0, query.length()-2);
			query += ")";
			
			System.out.println(query);
			PreparedStatement ps = connection.prepareStatement(query);
			ps.execute();
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
