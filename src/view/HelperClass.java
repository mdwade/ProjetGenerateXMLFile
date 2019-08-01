package view;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class HelperClass {

    public static ArrayList<File> listFiles = new ArrayList<>();

    public static void generateClassFromXSD() {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	try {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    factory.setIgnoringElementContentWhitespace(true);

	    File xmlFile = new File("fichiers/test.xsd");

	    Document xml = builder.parse(xmlFile);
	    Element root = xml.getDocumentElement();
	    NodeList list = root.getElementsByTagName("xs:element");

	    for (int i = 0; i < list.getLength(); i++) {
		Element e = (Element) list.item(i);

		if (list.item(i) instanceof Element) {
		    if (e.hasChildNodes()) {

			ClassItem classItem = new ClassItem();

			classItem.setClassName(e.getAttribute("name"));

			NodeList childList = e.getChildNodes();

			createPropertyFromElement(childList, classItem);

			ClassItem.listClass.add(classItem);

		    } else {
			/*
			 * Dans cette section on verifie d'abord si l'element a un attribut #name <br>
			 * 1-Si c'est le cas on regarde s'il n'existe pas deja dans la liste des
			 * attributs deja créé sinon on l'ajoute dans la liste<br>. Et s'il existe, on
			 * regarde dans la liste si le type du propriete dans la liste ressemble à celui
			 * de l'element en cours sinon on le remplace par celui de l'element en cours.
			 */
			ClassProperty property = new ClassProperty();

			if (e.hasAttribute("name")) {
			    String nomAttribut = e.getAttribute("name");

			    if (!checkPropertyExist(nomAttribut)) {
				property.setName(nomAttribut);
				property.setType(e.getAttribute("type").split(":")[1]);

				ClassProperty.listPropreties.add(property);
			    } else {

				for (ClassProperty p : ClassProperty.listPropreties) {
				    if (p.getName().equals(nomAttribut) && p.getType().equals(nomAttribut)) {
					p.setType(e.getAttribute("type").split(":")[1]);
				    }
				}
			    }
			} else {
			    if (!checkPropertyExist(e.getAttribute("ref"))) {
				property.setName(e.getAttribute("ref"));
				property.setType(e.getAttribute("ref"));

				ClassProperty.listPropreties.add(property);
			    }
			}
		    }
		}
	    }

	    generateJavaClasse();

	    // for (int i = (listFiles.size() - 1); i > -1; i--) {
	    // compile(listFiles.get(i));
	    // }
	} catch (ParserConfigurationException | SAXException | IOException e) {
	    e.printStackTrace();
	}
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

    public static void createPropertyFromElement(NodeList list, ClassItem currentClass) {
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

				if (item.hasAttribute("maxOccurs")) {
				    String nbreItem = item.getAttribute("maxOccurs");

				    if (nbreItem.equals("unbounded")) {
					currentClass.addToHashmap(property.getName(), "*");
				    }
				}
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

    public static void generateJavaClasse() {
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

		// currentClass.
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
}
