package clases;
import java.util.ArrayList;
import java.util.ResourceBundle;

import servicios.Consultas;

import java.lang.reflect.Field;
import utilidades.UBean;

public class Program {

	public static void main(String[] args) {
		
		ResourceBundle rB = ResourceBundle.getBundle("clases.nombre");
		System.out.println(rB.getString("dato"));
		
		System.out.println("Program main");
		Persona pTest = new Persona();
		ArrayList<Field> fields = UBean.obtenerAtributos(pTest);
		
		UBean.ejecutarSet(pTest, "dni", 123);
		UBean.ejecutarSet(pTest, "nombre", "Juan");
		UBean.ejecutarSet(pTest, "apellido", "Perez");
		System.out.println(pTest.toString());
		
		Integer attDni = (Integer) UBean.ejecutarGet(pTest, "dni");
		String attNombre = (String) UBean.ejecutarGet(pTest, "nombre");
		String attApe = (String) UBean.ejecutarGet(pTest, "apellido");
		System.out.println("attNombre: "+attNombre);
		System.out.println("attDni: "+attDni);
		
		Consultas.guardar(pTest);
	}

}
