package sgs.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import sgs.model.gridObjects.SmartGridObject;
import testing.Out_;

public class ReflectionStuff {

	/** Flag defining if packages below the (parameter) package should be used at related functions **/
	public static boolean useSubPackages = true;
	
	/**
	 * Testing
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void mainTest(String[] args) throws FileNotFoundException{

		ArrayList<Class<?>> classes;
		
		classes = getClassesForPackage(SmartGridObject.class);
		classes = getSubClasses(SmartGridObject.class, classes, false);
		Out_.pl("---------------------------------------");
		Out_.pl(classes);
		
//		OutputStream fos = new FileOutputStream("./console.txt");
//		Out_.out = new PrintStream( fos );
//		classes = getClassesForPackage(SmartGridObject.class);
//		Out_.pl("---------------------------------------");
//		Out_.pl(classes);
//		Out_.out.close();
	}
	
	/**
	 * Returns a list of sub-classes from the defined list of classes.
	 * @param topClass
	 * @param classes
	 * @return sub-classes of topClass, excluding topClass, excluding abstract classes
	 * @see {@link #getClassesForPackage(String pkgName)}
	 */
	public static ArrayList<Class<?>> getSubClasses(Class<?> topClass, ArrayList<Class<?>> classes, boolean canBeAbstract) {
		
		ArrayList<Class<?>> subClasses = new ArrayList<Class<?>>(classes.size());
		
		// --- add classes if they are sub-classes of the top class --- 
		for(Class<?> c : classes){
			if( topClass.isAssignableFrom(c) && topClass!=c){
				if( canBeAbstract || !Modifier.isAbstract( c.getModifiers() ) ){
					subClasses.add(c);
				}
			}
		}
		
		return subClasses;
	}
	
	/**
	 * @param topClass
	 * @return all classes in the same package.
	 * @see {@link #getClassesForPackage(String pkgName)}
	 */
	public static ArrayList<Class<?>> getClassesForPackage(Class<?> topClass) {
		Package pkg = topClass.getPackage();
		return getClassesForPackage(pkg);
	}
	/**
	 * @see {@link #getClassesForPackage(String pkgName)}
	 */
	public static ArrayList<Class<?>> getClassesForPackage(Package pkg) {
		return getClassesForPackage(pkg.getName());
	}
	
	/**
	 * @author Original from Dave Dopson, https://github.com/ddopson/java-class-enumerator
	 * @param pkgName - e.g. "sgs.controller.interfaces"
	 * @return all classes in the package.
	 */
	public static ArrayList<Class<?>> getClassesForPackage(String pkgName) {
//		String pkgName = pkg.getName();
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		// Get a File object for the package
		File directory = null;
		String fullPath;
		String relPath = pkgName.replace('.', '/');
		//Out_.pl("ClassDiscovery: Package: " + pkgName + " becomes Path:" + relPath);
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		//Out_.pl("ClassDiscovery: Resource = " + resource);
		if (resource == null) {
			throw new RuntimeException("No resource for " + relPath);
		}
		fullPath = resource.getFile();
		//Out_.pl("ClassDiscovery: FullPath = " + resource);

		try {
			directory = new File(resource.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(pkgName + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
		} catch (IllegalArgumentException e) {
			directory = null;
		}
		//Out_.pl("ClassDiscovery: Directory = " + directory);
		
		if (directory != null && directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
//			ArrayList<String> files = new ArrayList<String>(100);
//			files.addAll( Arrays.asList(directory.list()) );
			
			for (int i = 0; i < files.length; i++) {	// --- we are interested in .class files ---
				
				String f = files[i];
				//Out_.pl("Seen file/dir: '" + f +"'");
				
				if (f.endsWith(".class")) {
					// removes the .class extension
					String className = pkgName + '.' + f.substring(0, f.length()-6);
					//System.out.println("ClassDiscovery: className = " + className);
					try {
						classes.add(Class.forName(className));
					} 
					catch (ClassNotFoundException e) {
						throw new RuntimeException("ClassNotFoundException loading " + className);
					}
				}//if_class_
				else{									// --- recursion for directories ---
					//Out_.pl("Found non class: '" + f +"'");
					if(!useSubPackages){
						continue;	// continue if sub packages should not be used
					}
					
					File file = new File(directory.getAbsolutePath()+"/"+f);
					if(file.isDirectory()){
						try{
							classes.addAll(getClassesForPackage(pkgName+"."+f));
						}
						catch(RuntimeException e){
							System.err.println(e.getMessage());
						}
					}
				}//else_directory_
			}
		}
		else {
			try {
				String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
				@SuppressWarnings("resource")
				JarFile jarFile = new JarFile(jarPath);         
				Enumeration<JarEntry> entries = jarFile.entries();
				
				while(entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String entryName = entry.getName();
					//Out_.pl("Seen JAR-entry: '" + entryName +"'");
					
					if( entryName.startsWith(relPath) && 
							entryName.endsWith(".class") &&
							entryName.length() > (relPath.length()+1)) {
						
						if(!useSubPackages && entryName.substring(relPath.length()+1).contains("/")){
							continue; // continue at sub packages if they are not allowed
						}
						
						//Out_.pl("ClassDiscovery: JarEntry: " + entryName);
						String className = entryName
								.substring(0, entryName.length()-6)		// remove ".class"
								.replace('/', '.')						// slash to dot
								.replace('\\', '.');					// double backslash to dot
						//System.out.println("ClassDiscovery: className = " + className);
						try {
							classes.add(Class.forName(className));
						} 
						catch (ClassNotFoundException e) {
							throw new RuntimeException("ClassNotFoundException loading " + className);
						}
					}
				}//_while_
			} catch (IOException e) {
				throw new RuntimeException(pkgName + " (" + directory + ") does not appear to be a valid package", e);
			}
		}
		return classes;
	}
	

}
