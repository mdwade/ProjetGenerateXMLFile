package view;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassItem {
    private String className;
    private ArrayList<ClassProperty> listProperty = new ArrayList<>();

    public HashMap<String, String> propertiesOcc = new HashMap<>();

    public static ArrayList<ClassItem> listClass = new ArrayList<>();

    public ClassItem() {
    }

    public ClassItem(String className, ArrayList<ClassProperty> listPropertyItem) {
	this.className = className;
	this.listProperty = listPropertyItem;
    }

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public ArrayList<ClassProperty> getListProperty() {
	return listProperty;
    }

    public void setListProperty(ArrayList<ClassProperty> listProperty) {
	this.listProperty = listProperty;
    }

    public void addProperty(ClassProperty prop) {
	this.listProperty.add(prop);

    }

    public void addToHashmap(String propertyName, String propertyOcc) {
	this.propertiesOcc.put(propertyName, propertyOcc);
    }

    public HashMap<String, String> getPropertiesOcc() {
	return this.propertiesOcc;
    }

    @Override
    public String toString() {
	String text = "";
	text += "Nom Class : *" + this.className + "*\n";

	for (ClassProperty item : this.listProperty) {
	    text += "Nom Propriete : " + item.getName() + " Type Propriete : " + item.getType() + "\n";
	}
	text += "\n-------------------FIN---------------\n\n";
	return text;
    }
}
