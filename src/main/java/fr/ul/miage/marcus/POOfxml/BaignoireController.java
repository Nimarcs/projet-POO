package fr.ul.miage.marcus.POOfxml;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.time.Instant;
import java.util.Arrays;
import java.util.logging.Logger;

public class BaignoireController {

    public static final Logger LOG = Logger.getLogger(BaignoireController.class.getName());

    private static final int MAX_INOUT = 4;


    @FXML
    public Button btn_reglageFuite1;


//    @FXML
//    public LineChart linechart;

    @FXML
    private Rectangle eauBaignoire;

    @FXML
    private GridPane reglageFuite;

    @FXML
    private GridPane boucherFuite;

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
    private TextField tf_reglageFuite1;

    @FXML
    private TextField tf_reglageFuite2;

    @FXML
    private TextField tf_reglageFuite3;

    @FXML
    private TextField tf_reglageFuite4;

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

    private boolean simulationEnCours;

    private int[] debitRobinet, debitFuite;

    private Instant top;

    private Robinet[] robinets;
    private Fuite[] fuites;

    public BaignoireController(double capacite, int[] debitRobinet, int[] debitFuite) {
        LOG.setLevel(App.currentLogLevel);
        baignoire = new Baignoire(capacite);
        this.simulationEnCours = false;
        robinets = new Robinet[MAX_INOUT];
        fuites = new Fuite[MAX_INOUT];

        if (!checkDebitArray(debitRobinet)){
            LOG.severe("Les débits fourni pour les fuites sont interdits");
            throw new IllegalArgumentException("Débit des robinets illégaux");
        }
        if (!checkDebitArray(debitFuite)){
            LOG.severe("Les débits fourni pour les fuites sont interdits");
            throw new IllegalArgumentException("Débit des fuites illégaux");
        }

        this.debitRobinet = debitRobinet;
        this.debitFuite = debitFuite;

    }


    @FXML
    void demarrerArreter(){
        btn_demarrerArreter.setDisable(true);

        if (simulationEnCours) {
            LOG.info("Arret de la simulation");
            simulationEnCours = false;
            java.time.Duration duration = java.time.Duration.between(top, Instant.now());
            System.out.println("Arret de la simulation après : " + duration.toMillis() + "ms");

            for (int i = 0; i < MAX_INOUT; i++) {
                if (fuites[i] != null) {fuites[i].cancel();}
                if (robinets[i] != null) {robinets[i].cancel();}
            }

            btn_demarrerArreter.setText("Demarrer simulation");

        } else {
            //Initialisation
            LOG.info("Demarrage de la simulation");
            top = Instant.now();
            simulationEnCours = true;
            baignoire.vider();
            eauBaignoire.setHeight(0.0);

            setDisplayInOut();

            //On defini les robinets
            robinets = new Robinet[MAX_INOUT];
            for (int i = 0; i < debitRobinet.length; i++) {
                Robinet robinet = new Robinet(baignoire, debitRobinet[i]);
                robinet.setOnSucceeded((WorkerStateEvent e) -> {
                    //On met a jour l'affichage
                    LOG.info("Robinet deverse");
                    eauBaignoire.setHeight(baignoire.getVolume());

                    //Si la baignoire pleine on arrete tout
                    if (baignoire.estPlein()){
                        java.time.Duration duration = java.time.Duration.between(top, Instant.now());
                        System.out.println("Temps de remplissage : " + duration.toMillis() + "ms");
                        robinet.cancel();
                    }
                });
                robinets[i] = robinet;
                robinet.setPeriod(Duration.millis(1000));
            }

            //On defini les robinets
            fuites = new Fuite[MAX_INOUT];
            for (int i = 0; i < debitFuite.length; i++) {
                Fuite fuite = new Fuite(baignoire, debitFuite[i]);
                fuite.setOnSucceeded((WorkerStateEvent e) -> {
                    LOG.info("Fuite vide");
                    eauBaignoire.setHeight(baignoire.getVolume());
                });

                fuite.setPeriod(Duration.millis(1000));
                fuites[i] = fuite;
            }

            //On demarre la simulation
            for (int i = 0; i < MAX_INOUT; i++) {
                if (fuites[i] != null) {fuites[i].start();}
                if (robinets[i] != null) {robinets[i].start();}
            }

            btn_demarrerArreter.setText("Arreter simulation");

        }
        btn_demarrerArreter.setDisable(false);
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


    private boolean checkDebitArray(int[] debits) {
        if (debits.length > MAX_INOUT) return false;
        for (int debit: debits) {
            if (debit < 0.0) return false;
        }
        return true;
    }

    private void setDisplayInOut() {
        //robinets
        setVisibilityInOut(robinet1, robinet2, robinet3, robinet4, debitRobinet);
        //fuites
        setVisibilityInOut(fuite1, fuite2, fuite3, fuite4, debitFuite);
    }

    private void setVisibilityInOut(Pane pane1, Pane pane2, Pane pane3, Pane pane4, int[] debits) {
        pane1.setVisible(true);
        pane2.setVisible(true);
        pane3.setVisible(true);
        pane4.setVisible(true);
        switch (debits.length){
            case 0:
                pane1.setVisible(false);
            case 1 :
                pane2.setVisible(false);
            case 2 :
                pane3.setVisible(false);
            case 3 :
                pane4.setVisible(false);
            case 4:
                //Nothing
        }
    }

    public void reglageCapacite() {
        //TODO
    }
}
