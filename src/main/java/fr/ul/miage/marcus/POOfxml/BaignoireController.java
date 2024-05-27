package fr.ul.miage.marcus.POOfxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;

public class BaignoireController {

    @FXML
    private Rectangle eauBaignoire;

    @FXML
    private BorderPane baignoirePane;

    @FXML
    private Pane robinet1;

    @FXML
    private Pane robinet2;

    @FXML
    private Pane robinet3;

    @FXML
    private Pane robinet4;

    @FXML
    private Pane fuite1;

    @FXML
    private Pane fuite2;

    @FXML
    private Pane fuite3;

    @FXML
    private Pane fuite4;

    @FXML
    private TextField tf_reglageRobinet1;

    @FXML
    private TextField tf_reglageRobinet2;

    @FXML
    private TextField tf_reglageRobinet3;

    @FXML
    private TextField tf_reglageRobinet4;

    @FXML
    private Button btn_fuite1;

    @FXML
    private Button btn_fuite2;

    @FXML
    private Button btn_fuite3;

    @FXML
    private Button btn_fuite4;

    @FXML
    private Button btn_changerAffichage;

    @FXML
    private Button btn_demarrerArreter;

    @FXML
    private Button btn_reglageRobinet;

    private Baignoire baignoire;


    public BaignoireController(String fichier) {
        baignoire = new Baignoire();
    }

    @FXML
    void sayHello() {
        System.out.println("hello");
    }

    @FXML
    void demarrerArreter(){
        System.out.println("demarrerArreter");
    }


    @FXML
    void changerAffichage(){
        System.out.println("changerAffichage");
    }

    @FXML
    void reparerFuite(ActionEvent event){
        System.out.println("Reparer fuite : " + ((Button) event.getSource()).getId());
    }

    @FXML
    void reglageRobinet(ActionEvent event){
        System.out.println("reglage robinet" + tf_reglageRobinet1.getText() + tf_reglageRobinet2.getText() + tf_reglageRobinet3.getText() + tf_reglageRobinet4.getText());
    }

}
