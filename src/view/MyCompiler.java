package view;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class MyCompiler {

    @SuppressWarnings("rawtypes")
    public static void compile(File file) {
	// get system compiler:
	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

	// for compilation diagnostic message processing on compilation WARNING/ERROR
	MyDiagnosticListener c = new MyDiagnosticListener();

	StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);

	// specify classes output folder
	Iterable<? extends JavaFileObject> compilationUnit = fileManager
		.getJavaFileObjectsFromFiles(Arrays.asList(file));

	Iterable options = Arrays.asList("-d", "bin/");

	JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, c, options, null, compilationUnit);

	Boolean result = task.call();

	if (result) {
	    System.out.println("Succeeded");

	    // URLClassLoader classLoader = null;
	    //
	    // try {
	    // classLoader = new URLClassLoader(new URL[] { new File("./").toURI().toURL()
	    // });
	    // } catch (MalformedURLException e1) {
	    // e1.printStackTrace();
	    // }
	    //
	    // try {
	    // Class loadedClass = classLoader.loadClass("com.gn.Livre");
	    //
	    // Object obj = loadedClass.newInstance();
	    //
	    // Class[] paramString = new Class[1];
	    //
	    // paramString[0] = String.class;
	    //
	    // Method setEditeur = loadedClass.getDeclaredMethod("setEditeur", paramString);
	    //
	    // setEditeur.invoke(obj, "ESP");
	    //
	    // Method getEditeur = loadedClass.getDeclaredMethod("getEditeur", null);
	    //
	    // System.out.println(getEditeur.invoke(obj, null));
	    //
	    // } catch (ClassNotFoundException | InstantiationException |
	    // IllegalAccessException | NoSuchMethodException
	    // | SecurityException | IllegalArgumentException | InvocationTargetException e)
	    // {
	    // e.printStackTrace();
	    // }
	}
    }

    public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {

	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
	    System.out.println("Line Number->" + diagnostic.getLineNumber());
	    System.out.println("code->" + diagnostic.getCode());
	    System.out.println("Message->" + diagnostic.getMessage(Locale.ENGLISH));
	    System.out.println("Source->" + diagnostic.getSource());
	    System.out.println(" ");
	}
    }

}
