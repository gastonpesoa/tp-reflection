import java.util.ArrayList;
import java.lang.reflect.Field;
import utilidades.UBean;

public class Program {

	public static void main(String[] args) {
		System.out.println("Program main");
		Persona pTest = new Persona();
		ArrayList<Field> fields = UBean.obtenerAtributos(pTest);
		
		UBean.ejecutarSet(pTest, "nombre", "Juan");
		UBean.ejecutarSet(pTest, "dni", 123);
		System.out.println(pTest.toString());
		
		String attNombre = (String) UBean.ejecutarGet(pTest, "nombre");
		Integer attDni = (Integer) UBean.ejecutarGet(pTest, "dni");
		System.out.println("attNombre: "+attNombre);
		System.out.println("attDni: "+attDni);
	}

}
