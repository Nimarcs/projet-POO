package fr.ul.miage.marcus.POOfxml;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class BaignoireController {

    @FXML
    private

    Hello hello;

    public BaignoireController(String fichier) {
        hello = new Hello(fichier);
    }

    @FXML
    void sayHello() {
        try {
            ta.appendText(String.format("Hello %s !%n", hello.lePlusProche(tf.getText())));
        }catch (Exception e){
            Hello.LOG.severe("ERREUR : " + Arrays.toString(e.getStackTrace()));
        }
    }

    @FXML
    void load(){

    }

}
