package utilidades;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class UBean {
	
	public static ArrayList<Field> obtenerAtributos(Object o) {
		Class reflector = o.getClass();
		ArrayList<Field> fields = new ArrayList<Field>();
		for(Field field: reflector.getDeclaredFields()) {
			fields.add(field);
		}
		return fields;
	}

	public static boolean ejecutarSet(Object o, String att, Object valor) {
		boolean result = false;
		Class reflector = o.getClass();
		String setterName = "set".concat(String.valueOf(att.charAt(0)).toUpperCase().concat(att.substring(1)));
		for(Method m: reflector.getDeclaredMethods()) {
			if(m.getName().equals(setterName)) {
				Object[] results = new Object[1];
				results[0] = valor;
				try {
					m.invoke(o, results);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static Object ejecutarGet(Object o, String att) {
		Object result = null;
		Class reflector = o.getClass();
		String getterName = "get".concat(String.valueOf(att.charAt(0)).toUpperCase().concat(att.substring(1)));
		for(Method m: reflector.getDeclaredMethods()) {
			if(m.getName().equals(getterName)) {
				try {
					result = m.invoke(o, new Object[0]);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
