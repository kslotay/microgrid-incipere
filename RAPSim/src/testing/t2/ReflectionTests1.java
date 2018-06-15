package testing.t2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import testing.Out_;


public class ReflectionTests1 {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		
		Object o;
//		House h = new House();
//		Object o = new SgsGridModel(5,5,5,5);
//		o = new String();
//		o = new Integer(1);
//		o = new Double(1.2345);
		o = new ReflectionTests1Object();
		Class<?> clazz = o.getClass();
		
		Out_.pl(clazz.getDeclaredFields().length + " declared fields.");
		for(Field f : clazz.getDeclaredFields()){
			
			//int modi = f.getModifiers();
			Out_.pl("Field: "+f.toGenericString() +"; "+ Integer.toBinaryString( f.getModifiers() ));
			
//			if(!Modifier.isPrivate(modi) && !Modifier.isProtected(modi)){	// static..?, Try?
//				//...
//			}
//			else{
				f.setAccessible(true);
//			}
			Object value = f.get(o);
			Out_.pl("  > " + value);
			
//			Modifier.isPrivate(modi);
//			Modifier.isPublic(modi);
//			Modifier.isStatic(modi);
		}
		
		Out_.pl(clazz.getDeclaredConstructors().length + " constructors.");
//		for(Constructor<?> f : clazz.getDeclaredConstructors()){
//			Out_.pl("Constructor: "+f.toString());
//		}
		Out_.pl(clazz.getDeclaredMethods().length + " methods.");
//		for(Method f : clazz.getDeclaredMethods()){
//			Out_.pl("Method: "+f.getName() + getParams(f) +" : "+f.getReturnType().getSimpleName());
//		}
		Out_.pl(clazz.getDeclaredClasses().length + " declared classes.");
//		for(Class<?> f : clazz.getDeclaredClasses()){
//			Out_.pl("DeclaredClass: "+f.getSimpleName());
//		}
		Out_.pl(clazz.getDeclaredAnnotations().length + " annotations.");
//		for(Annotation f : clazz.getDeclaredAnnotations()){
//			Out_.pl("Annotation: "+f.toString());
//		}
		{
			Class<?> f = clazz.getDeclaringClass();
			Out_.pl("DeclaringClass: "+f);
		}
		
	}
	
	static String getParams(Method m){
		String s = "(";
		Class<?>[] cs = m.getParameterTypes();
		for(int i=0; i<cs.length; i++) {
			s += cs[i].getSimpleName();
			if(i<cs.length-1)
				s += ", ";
		}
		s += ")";
		return s;
	}

}
