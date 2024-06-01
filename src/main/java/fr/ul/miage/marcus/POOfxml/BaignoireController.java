package fr.ul.miage.marcus.POOfxml;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.time.Instant;
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
    private TextField tf_capacity;

    @FXML
    private Button btn_fuite1;

    @FXML
    private Button btn_fuite2;

    @FXML
    private Button btn_fuite3;

    @FXML
    private Button btn_fuite4;

    @FXML
    private Button btn_demarrerArreter;

    @FXML
    private Button btn_reglageRobinet;

    @FXML
    private Button btn_reglageFuite;

    private Button btn_reglageCapacite;

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


    /*
    Reaction a l'appui de bouton
     */

    @FXML
    void demarrerArreter(){
        //TODO ajouter une désactivation de bouton propre
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

        }
        else
        {
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
    void reparerFuite(ActionEvent event){
        System.out.println("Reparer fuite : " + ((Button) event.getSource()).getId());
    }

    @FXML
    void reglageRobinet(){
        //btn_reglageRobinet
        System.out.println("reglage robinet" + tf_reglageRobinet1.getText() + tf_reglageRobinet2.getText() + tf_reglageRobinet3.getText() + tf_reglageRobinet4.getText());
    }

    @FXML
    void reglageFuite(){
        System.out.println("reglage fuite" + tf_reglageFuite1.getText() + tf_reglageFuite2.getText() + tf_reglageFuite3.getText() + tf_reglageFuite4.getText());

    }


    public void reglageCapacite() {
        try {
            double nouvelleCapacite = recupereDouble(tf_capacity);
            if (simulationEnCours) {
                afficheErreur("La simulation est en cours, le reglage de la capacité n'est pas censé être disponible");
                return;
            }

            baignoire.setCapacite(nouvelleCapacite);
            LOG.info("Nouvelle capacité de la baignoire : " + nouvelleCapacite);
            afficheInformation("Capacité de la baignoire changée");
        } catch (IllegalArgumentException ignored){
            afficheErreur("Valeur de la capacité invalide");
            // On ne met pas à jour
        }
    }

    /*
    PRIVATE FONCTION
     */

    private double recupereDouble(TextField textField) throws NumberFormatException{
        try {
            return Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            LOG.severe(String.format("%s - INPUT ERROR - expected a number got %s",textField.getId(), textField.getText()));
            throw e;
        }
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

    private void afficheErreur(String s) {
        LOG.severe(s);
        Alert alert = new Alert(Alert.AlertType.ERROR, s);
        alert.show();
    }

    private void afficheInformation(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, s);
        alert.show();
    }
}
