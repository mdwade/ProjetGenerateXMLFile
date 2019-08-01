package view;

import java.util.ArrayList;

public class ClassProperty {
    private String name;
    private String type;

    static ArrayList<ClassProperty> listPropreties = new ArrayList<>();

    public ClassProperty() {
    }

    public ClassProperty(String name, String type) {
	this.name = name;
	this.type = type;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }
}
