package servicios;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import anotaciones.Columna;
import anotaciones.Id;
import anotaciones.Tabla;
import utilidades.UBean;
import utilidades.UConexion;

public class Consultas {
	
	public static void guardar(Object o) {
		
		try {
			
			Class reflector = o.getClass();
			Tabla tabla = (Tabla) reflector.getAnnotation(Tabla.class);			
			String query = "INSERT INTO ".concat(tabla.nombre()).concat(" (");
			
			ArrayList<Field> fields = UBean.obtenerAtributos(o);
			
			for(Field field: fields) {
				Columna columna = field.getAnnotation(Columna.class);
				if(columna != null) {
					query += field.getAnnotation(Columna.class).nombre().concat(", ");		
				}
			}
			
			query = query.substring(0, query.length()-2).concat(") VALUES (");
			
			for(Field field: fields) {
				Columna columna = field.getAnnotation(Columna.class);
				if(columna != null) {
					if(field.getType().equals(String.class)) {
						query += "'".concat(UBean.ejecutarGet(o, field.getName()).toString()).concat("', ");
					} else {
						query += UBean.ejecutarGet(o, field.getName()).toString().concat(", ");
					}
				}
			}
			
			query = query.substring(0, query.length()-2).concat(")");
			ejecutar(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void modificar(Object o) {
		
		try {
			
			Class reflector = o.getClass();
			Tabla tabla = (Tabla) reflector.getAnnotation(Tabla.class);		
			String query = "UPDATE ".concat(tabla.nombre()).concat(" SET ");
			
			Field idForWhere = null;
			ArrayList<Field> fields = UBean.obtenerAtributos(o);
			
			for(Field field: fields) {
				
				Id id = field.getAnnotation(Id.class);
				Columna columna = field.getAnnotation(Columna.class);
				
				if(columna != null) {
					query += columna.nombre().concat(" = ");
					if(field.getType().equals(String.class)) {
						query += "'".concat(UBean.ejecutarGet(o, field.getName()).toString()).concat("', ");
					} else {
						query += UBean.ejecutarGet(o, field.getName()).toString().concat(", ");
					}
				} else if(id != null) {
					idForWhere = field;
				}
			}
			
			query = query.substring(0, query.length()-2)
					.concat(" WHERE ")
					.concat(idForWhere.getAnnotation(Id.class).nombre())
					.concat(" = ")
					.concat(UBean.ejecutarGet(o, idForWhere.getName()).toString())
					.concat(";");
			
			ejecutar(query);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void eliminar(Object o) {
		
		try {
			
			Class reflector = o.getClass();
			Tabla tabla = (Tabla) reflector.getAnnotation(Tabla.class);		
			String query = "DELETE FROM ".concat(tabla.nombre()).concat(" WHERE ");
			
			ArrayList<Field> fields = UBean.obtenerAtributos(o);
			
			for(Field field: fields) {
				
				Id id = field.getAnnotation(Id.class);
				
				if(id != null) {
					query = query.concat(id.nombre()).concat(" = ").concat(UBean.ejecutarGet(o, field.getName()).toString());
					break;
				}
			}
			
			ejecutar(query);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object obtenerPorId(Class c, Object id) {
		
		Object object = null;
		Tabla tabla = (Tabla) c.getAnnotation(Tabla.class);	
		String query = "SELECT * FROM ".concat(tabla.nombre()).concat(" WHERE ");
		
		try {			
			object = UBean.obtenerInstancia(c);			
			Field idField = UBean.obtenerIdField(c);
			String idColNombre = idField.getAnnotation(Id.class).nombre();
			query = query.concat(idColNombre).concat(" = ").concat(id.toString());
			
			HashMap<Object, HashMap> records = ejecutarQuery(query, idField.getAnnotation(Id.class).nombre());
			Set keys = records.keySet();
			
			for(Object key : keys) {
				HashMap record = records.get(key);
				Set columns = record.keySet();
				
				for(Object col : columns) {
					
					for(Field f: c.getDeclaredFields()) {
						if(f.getName().equals(col)) {						 
							String setterName = "set".concat(String.valueOf(f.getName().charAt(0)).toUpperCase().concat(f.getName().substring(1))); 
							
							for(Method m: c.getDeclaredMethods()) {
								if(m.getName().equals(setterName)) {
									Object[] params = new Object[1];
									params[0] =  record.get(col);
									m.invoke(object, params);
								}
							}
						}
					}
				}
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		return object;
	}
	
	public static List<Object> obtenerTodos(Class c){
		
		List<Object> lista = new ArrayList<Object>();
		Tabla tabla = (Tabla) c.getAnnotation(Tabla.class);	
		String query = "SELECT * FROM ".concat(tabla.nombre());
		
		try {
			
			Field idField = UBean.obtenerIdField(c);
			HashMap<Object, HashMap> records = ejecutarQuery(query, idField.getAnnotation(Id.class).nombre());
			Set keys = records.keySet();
			
			for(Object key : keys) {
				
				Object object = UBean.obtenerInstancia(c);
				HashMap record = records.get(key);
				Set columns = record.keySet();
				
				for(Object column : columns) {
					
					for(Field f: c.getDeclaredFields()) {
						
						if(f.getName().equals(column)) {						 
							
							String setterName = "set".concat(String.valueOf(f.getName().charAt(0)).toUpperCase().concat(f.getName().substring(1))); 
							
							for(Method m: c.getDeclaredMethods()) {
								
								if(m.getName().equals(setterName)) {
									Object[] params = new Object[1];
									params[0] =  record.get(column);
									m.invoke(object, params);
								}
							}
						}
					}
				}
				
				lista.add(object);
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	private static void ejecutar(String query) {
		
		UConexion uc = UConexion.obtenerInstancia();
		Connection connection = uc.getConnection();
		PreparedStatement ps;
		
		try {
			
			ps = connection.prepareStatement(query);
			ps.execute();
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static HashMap<Object, HashMap> ejecutarQuery(String query, String identificador) {
		
		UConexion uc = UConexion.obtenerInstancia();
		Connection connection = uc.getConnection();
		PreparedStatement ps;	
		
		HashMap<Object, HashMap> records = new HashMap<Object, HashMap>(); 		
		
		try {
			
			ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			Integer idRecord = 0;
			
            while(rs.next()){                 	 
            	
            	HashMap resultMap = new HashMap();
                
            	for(int i = 1; i <= rsmd.getColumnCount(); i++){
                	
            		Object val = null;
                	int sqlTypes = rsmd.getColumnType(i);
                	
                	switch(sqlTypes) {
	                	case Types.VARCHAR:	                		
	                    case Types.CHAR:
	                    	val = new String(rs.getString(i));
	                       break;
	                    case Types.TIMESTAMP:	              
			                SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy/hh:mm:ss");
			                val = formatter.parse(rs.getString(i));
	                        break;
	                    case Types.DECIMAL:		                    
	                    case Types.DOUBLE:
	                        val = Double.valueOf(rs.getString(i));
	                        break;
	                    case Types.INTEGER:
	                    case Types.SMALLINT:
	                        val = Integer.valueOf(rs.getString(i));
	                        break;	               
                	}
                	
                	if(identificador.equalsIgnoreCase(rsmd.getColumnLabel(i))) {
                		idRecord = Integer.valueOf(rs.getString(i));
                	}
                	                	
                	resultMap.put(rsmd.getColumnLabel(i), val);
                }
                records.put(idRecord, resultMap);
            }
            
			connection.close();


		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		
		return records;
	}
}
