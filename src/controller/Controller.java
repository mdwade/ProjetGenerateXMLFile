package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import view.ClassItem;
import view.HelperClass;
import view.MyCompiler;

public class Controller implements Initializable {
    @FXML
    private Button btnGenerateClasse;

    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	textArea.setEditable(false);
	btnGenerateClasse.setDisable(false);
    }

    @FXML
    void generateClasse(ActionEvent event) {
	HelperClass.generateClassFromXSD();

	HelperClass.listFiles.forEach(file -> {
	    MyCompiler.compile(file);
	});

	String text = new String();
	for (ClassItem c : ClassItem.listClass) {
	    text += c;
	}
	// ClassItem.listClass.forEach(cl ->
	// Controller.textArea.setText(cl.toString()));
	textArea.setText(text);
    }

    @FXML
    void chooseXSDFile(ActionEvent event) {
	Node node = (Node) event.getSource();

	FileChooser fileChooser = new FileChooser();

	File selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());

	Path d = Paths.get(selectedFile.getPath());
	Path monFichierCopie = Paths.get("fichiers/" + selectedFile.getName());

	try {
	    Files.copy(d, monFichierCopie);

	    btnGenerateClasse.setDisable(false);
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (RuntimeException e) {
	    System.out.println("erreur");
	}
    }
}
