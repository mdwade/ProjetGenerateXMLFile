package view;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

    static String TYPE_COMPLEX = "xs:complexType";
    static ArrayList<File> listFiles = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
	try {
	    Parent root = FXMLLoader.load(getClass().getResource("View.fxml"));

	    Scene scene = new Scene(root);
	    FileChooser fileChooser = new FileChooser();

	    Button button = new Button("Select File");
	    button.setOnAction(e -> {
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
	    });
	    // scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    primaryStage.setScene(scene);
	    primaryStage.show();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	launch(args);
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	//
	// try {
	// DocumentBuilder builder = factory.newDocumentBuilder();
	// factory.setIgnoringElementContentWhitespace(true);
	//
	// File xmlFile = new File("test.xsd");
	//
	// Document xml = builder.parse(xmlFile);
	// Element root = xml.getDocumentElement();
	// NodeList list = root.getElementsByTagName("xs:element");
	//
	// for (int i = 0; i < list.getLength(); i++) {
	// Element e = (Element) list.item(i);
	//
	// if (list.item(i) instanceof Element) {
	// if (e.hasChildNodes()) {
	//
	// ClassItem classItem = new ClassItem();
	//
	// classItem.setClassName(e.getAttribute("name"));
	//
	// NodeList childList = e.getChildNodes();
	//
	// createProppertyFromElement(childList, classItem);
	//
	// ClassItem.listClass.add(classItem);
	//
	// } else {
	// /*
	// * Dans cette section on verifie d'abord si l'element a un attribut #name 1-Si
	// * c'est le cas on regarde s'il n'existe pas deja dans la liste des attributs
	// * deja créé sinon on l'ajoute dans la liste.Et s'il existe, on regarde dans
	// la
	// * liste si le type du propriete dans la liste ressemble à celui de l'element
	// en
	// * cours sinon on le remplace par celui de l'element en cours.
	// */
	// ClassProperty property = new ClassProperty();
	//
	// if (e.hasAttribute("name")) {
	// if (!checkPropertyExist(e.getAttribute("name"))) {
	//
	// property.setName(e.getAttribute("name"));
	// property.setType(e.getAttribute("type").split(":")[1]);
	//
	// ClassProperty.listPropreties.add(property);
	// } else {
	//
	// for (ClassProperty p : ClassProperty.listPropreties) {
	// if (p.getName().equals(e.getAttribute("name"))
	// && p.getType().equals(e.getAttribute("name"))) {
	// p.setType(e.getAttribute("type").split(":")[1]);
	// }
	// }
	// }
	//
	// } else {
	// if (!checkPropertyExist(e.getAttribute("ref"))) {
	// property.setName(e.getAttribute("ref"));
	// property.setType(e.getAttribute("ref"));
	//
	// ClassProperty.listPropreties.add(property);
	// }
	// }
	// }
	// }
	// }
	// ClassProperty.listPropreties
	// .forEach(prop -> System.out.println(prop.getName() + " Type : " +
	// prop.getType()));
	// ClassItem.listClass.forEach(cl -> System.out.println(cl));
	//
	// createJavaClasse();
	//
	// for (int i = (listFiles.size() - 1); i > -1; i--) {
	// compile(listFiles.get(i));
	// }
	// testLoading();
	// } catch (ParserConfigurationException | SAXException | IOException e) {
	// e.printStackTrace();
	// }
    }

    public static boolean checkPropertyExist(String name) {
	boolean response = false;

	for (ClassProperty prop : ClassProperty.listPropreties) {
	    if (prop.getName().equals(name)) {
		response = true;
		break;
	    }
	}

	return response;
    }

    public static void createProppertyFromElement(NodeList list, ClassItem currentClass) {
	for (int j = 0; j < list.getLength(); j++) {

	    if (list.item(j) instanceof Element) {
		Element def = (Element) list.item(j);

		// if (def.getTagName().equals(TYPE_COMPLEX)) {
		NodeList defChild = def.getChildNodes();

		Element sequenceNode = (Element) defChild.item(1);

		for (int k = 0; k < sequenceNode.getChildNodes().getLength(); k++) {
		    NodeList els = sequenceNode.getChildNodes();

		    if (els.item(k) instanceof Element) {
			Element item = (Element) els.item(k);

			ClassProperty property = new ClassProperty();

			if (item.hasAttribute("name")) {
			    if (!checkPropertyExist(item.getAttribute("name"))) {

				property.setName(item.getAttribute("name"));
				property.setType(item.getAttribute("type").split(":")[1]);

				currentClass.addProperty(property);
			    }
			} else {
			    if (!checkPropertyExist(item.getAttribute("ref"))) {
				property.setName(item.getAttribute("ref"));
				property.setType(item.getAttribute("ref"));

				currentClass.addProperty(property);
			    }
			}

			ClassProperty.listPropreties.add(property);
		    }
		}
		// }
	    }
	}
    }

    public static void createJavaClasse() {
	for (ClassItem currentClass : ClassItem.listClass) {

	    String className = currentClass.getClassName();

	    className = className.substring(0, 1).toUpperCase() + className.substring(1);

	    ArrayList<ClassProperty> listAttributs = currentClass.getListProperty();
	    StringBuilder header = new StringBuilder();

	    header.append("package com.gn;\n\n" + "public class " + className + "{\n");

	    for (ClassProperty att : listAttributs) {

		StringBuilder atts = new StringBuilder();
		String typeAtt = att.getType().substring(0, 1).toUpperCase() + att.getType().substring(1);
		String nameAtt = att.getName();

		atts.append("private " + typeAtt + " " + nameAtt + ";\n");
		header.append(atts);

		header.append(generateGetteurAndSetteur(typeAtt, nameAtt));

	    }

	    header.append("\npublic " + className + "(){}" + "}\n\n");

	    List<StringBuilder> lines = Arrays.asList(header);

	    Path file = Paths.get(className + ".java");

	    try {
		Files.write(file, lines, StandardCharsets.UTF_8);

		Path monFichierCopie = Paths.get("src/com/gn/" + className + ".java");

		Files.move(file, monFichierCopie);

		File f = new File("src/com/gn/" + className + ".java");

		listFiles.add(f);

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    private static StringBuilder generateGetteurAndSetteur(String typeAtt, String name) {
	StringBuilder setAndGet = new StringBuilder();
	String capName = name.substring(0, 1).toUpperCase() + name.substring(1);

	setAndGet.append("public void set" + capName + " (" + typeAtt + " " + name + "){\nthis." + name + " = " + name
		+ ";\n}\n");

	setAndGet.append("public " + typeAtt + " get" + capName + " (){\nreturn this." + name + ";\n}\n");

	return setAndGet;
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
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
	}
    }

    public static String upperCaseFirst(String name) {
	return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static void testLoading() {
	URLClassLoader classLoader = null;

	try {
	    classLoader = new URLClassLoader(new URL[] { new File("./").toURI().toURL() });
	} catch (MalformedURLException e1) {
	    e1.printStackTrace();
	}

	Class loadedClass = null;

	int taille = ClassItem.listClass.size();

	try {

	    // for (int i = (taille - 1); i > -1; i--) {
	    // ClassItem currentClass = ClassItem.listClass.get(i);
	    // String name = upperCaseFirst(currentClass.getClassName());

	    loadedClass = classLoader.loadClass("com.gn.Livre");

	    // ArrayList<ClassProperty> listAttributs = currentClass.getListProperty();

	    Object obj = loadedClass.newInstance();

	    // for (ClassProperty currentAtt : listAttributs) {
	    // String attName = upperCaseFirst(currentAtt.getName());
	    // String attType = upperCaseFirst(currentAtt.getType());

	    // Class[] paramString = new Class[1];

	    // switch (attType) {
	    // case "String":
	    // paramString[0] = String.class;
	    // break;
	    // case "int":
	    // paramString[0] = Integer.class;
	    // break;
	    // default:
	    // paramString[0] = Object.class;
	    // break;
	    // }

	    Scanner sc = new Scanner(System.in);

	    Class[] paramString = new Class[1];
	    String name = "Livre";

	    switch (name) {
	    case "Livre":
		paramString[0] = String.class;

		Method setEditeur = loadedClass.getDeclaredMethod("setEditeur", paramString);
		setEditeur.invoke(obj, "ESP");

		Method setAuteur = loadedClass.getDeclaredMethod("setAuteur", paramString);
		setAuteur.invoke(obj, "Diadji Ndiaye");

		Method setTitre = loadedClass.getDeclaredMethod("setTitre", paramString);
		setTitre.invoke(obj, "Les enfants terribles");

		Method getEditeur = loadedClass.getDeclaredMethod("getEditeur", null);
		Method getAuteur = loadedClass.getDeclaredMethod("getAuteur", null);
		Method getTitre = loadedClass.getDeclaredMethod("getTitre", null);

		String editeur = (String) getEditeur.invoke(obj, null);
		String auteur = (String) getAuteur.invoke(obj, null);
		String titre = (String) getTitre.invoke(obj, null);

		System.out.println("<livre>" + "<titre>" + titre + "</titre>" + "<auteur>" + auteur + "</auteur>"
			+ "<editeur>" + editeur + "</editeur>" + "</livre>");
		break;
	    case "int":
		paramString[0] = Integer.class;
		break;
	    default:
		paramString[0] = Object.class;
		break;
	    }

	    // }
	    // }
	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
		| SecurityException | IllegalArgumentException |

		InvocationTargetException e) {
	    e.printStackTrace();
	}
    }
}
