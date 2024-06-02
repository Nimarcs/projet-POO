package fr.ul.miage.marcus.POOfxml;

import com.opencsv.CSVWriter;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class BaignoireController {

    public static final Logger LOG = Logger.getLogger(BaignoireController.class.getName());

    private static final int MAX_INOUT = 4;
    public static final int VITESSE = 500;


    @FXML
    private LineChart<Number, Number> linechart;

    private XYChart.Series<Number, Number> seriesLineChart;

    @FXML
    private Rectangle eauBaignoire;

    @FXML
    private GridPane reglageFuite;

    @FXML
    private GridPane boucherFuite;

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

    @FXML
    private Button btn_reglageCapacite;

    @FXML
    private Button btn_exporterCSV;

    @FXML
    private Text txt_boucherFuite;

    private final Baignoire baignoire;

    private static List<String[]> listeExport;

    private boolean simulationEnCours;

    private double[] debitRobinet, debitFuite;

    private Instant top;

    private Robinet[] robinets;
    private Fuite[] fuites;

    public BaignoireController(double capacite, double[] debitRobinet, double[] debitFuite) throws IllegalArgumentException {
        LOG.setLevel(App.currentLogLevel);
        baignoire = new Baignoire(capacite);
        this.simulationEnCours = false;
        robinets = new Robinet[MAX_INOUT];
        fuites = new Fuite[MAX_INOUT];

        verifieDebitArray(debitRobinet);
        verifieDebitArray(debitFuite);

        this.debitRobinet = debitRobinet;
        this.debitFuite = debitFuite;

        seriesLineChart = new XYChart.Series<>();
        seriesLineChart.setName("Remplisage baignoire");
        NumberAxis axeX = new NumberAxis();
        axeX.setLabel("Temps");
        NumberAxis axeY = new NumberAxis();
        axeY.setLabel("Volume");
        linechart = new LineChart<>(axeX, axeY);


    }


    /*
    Reaction a l'appui de bouton
     */

    @FXML
    void demarrerArreter() {
        btn_demarrerArreter.setDisable(true);

        if (simulationEnCours) {
            terminerSimulation();
        } else {
            //Initialisation
            LOG.info("Demarrage de la simulation");
            simulationEnCours = true;
            baignoire.vider();
            eauBaignoire.setHeight(0.0);

            linechart.getData().clear();
            seriesLineChart.getData().clear();
            linechart.getData().add(seriesLineChart);

            listeExport = new ArrayList<>();

            mettreAJourAffichageBouton(simulationEnCours);

            regleVisibiliteEntreSortieEau();

            //On defini les robinets
            robinets = new Robinet[MAX_INOUT];
            for (int i = 0; i < debitRobinet.length; i++) {
                double debit = debitRobinet[i];
                Robinet robinet = creerRobinet(debit);
                robinets[i] = robinet;
            }

            //On defini les fuites
            fuites = new Fuite[MAX_INOUT];
            for (int i = 0; i < debitFuite.length; i++) {
                Fuite fuite = new Fuite(baignoire, debitFuite[i]);
                fuite.setOnSucceeded((WorkerStateEvent e) -> {
                    LOG.info("Fuite vide");
                    java.time.Duration tempsDepuisDepart = java.time.Duration.between(top, Instant.now());
                    linechart.getData().get(0).getData().add(new XYChart.Data<>(tempsDepuisDepart.toMillis(), baignoire.getVolume()));
                    listeExport.add(new String[]{String.valueOf(tempsDepuisDepart.toMillis()), String.valueOf(baignoire.getVolume())});
                    mettreAJourBaignoire();

                });

                fuite.setPeriod(Duration.millis(VITESSE));
                fuites[i] = fuite;
            }

            top = Instant.now();

            //On demarre la simulation
            for (int i = 0; i < MAX_INOUT; i++) {
                if (robinets[i] != null) {
                    robinets[i].start();
                }
                if (fuites[i] != null) {
                    fuites[i].start();
                }
            }

            btn_demarrerArreter.setText("Arreter simulation");

        }
        btn_demarrerArreter.setDisable(false);
    }

    @FXML
    void reparerFuite(ActionEvent event) {
        int indiceFuite = switch (((Button) event.getSource()).getId()) {
            case "btn_fuite1" -> 0;
            case "btn_fuite2" -> 1;
            case "btn_fuite3" -> 2;
            case "btn_fuite4" -> 3;
            default -> throw new IllegalStateException("Appel de reparerFuite depuis un bouton inconnu");
        };
        //S'il n'y a pas de fuite on s'arrete là
        if (fuites[indiceFuite] == null) return;

        fuites[indiceFuite].boucher();
        fuites[indiceFuite].cancel();
        debitFuite[indiceFuite] = 0;
        regleVisibiliteEntreSortieEau();
    }

    @FXML
    void reglageRobinet() {
        try {

            debitRobinet = new double[]{recupereDouble(tf_reglageRobinet1), recupereDouble(tf_reglageRobinet2), recupereDouble(tf_reglageRobinet3), recupereDouble(tf_reglageRobinet4)};

            //On met a jour on the fly les robinet si la simulation tourne déjà
            if (simulationEnCours) {
                for (int i = 0; i < MAX_INOUT; i++) {
                    // S'il y est censé en avoir un
                    if (debitRobinet[i] > 0) {
                        // Si tout est bon avec le robinet, on ignore
                        if (robinets[i] != null && debitRobinet[i] == robinets[i].getDebit()) continue;

                        //Si il y en avait un, on change sa valeur
                        if (robinets[i] != null) {
                            robinets[i].setDebit(debitRobinet[i]);
                            robinets[i].restart();
                        } else //Sinon, on en démarre un nouveau
                        {
                            Robinet robinet = creerRobinet(debitRobinet[i]);
                            robinets[i] = robinet;
                            robinet.start();
                        }

                    }
                    // Si il n'est pas censé en avoir un
                    else {
                        if (robinets[i] != null) {
                            robinets[i].cancel();
                            robinets[i] = null;
                        }
                    }
                }
            }

            afficheInformation("Les robinets ont bien été mis à jour");

        } catch (IllegalArgumentException ignored) {
            afficheErreur("Valeur d'un des robinets erronée");
        }
    }

    @FXML
    void reglageFuite() {

        if (simulationEnCours)
            afficheErreur("On ne peut pas parametrer les fuites en cours de route");

        debitFuite = new double[]{recupereDouble(tf_reglageFuite1), recupereDouble(tf_reglageFuite2), recupereDouble(tf_reglageFuite3), recupereDouble(tf_reglageFuite4)};
        afficheInformation("Les fuites ont bien été mise à jour");
    }

    @FXML
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
        } catch (IllegalArgumentException ignored) {
            afficheErreur("Valeur de la capacité invalide");
            // On ne met pas à jour
        }
    }

    @FXML
    public void exporterCSV() {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("export.csv"));
            writer.writeAll(listeExport);

            afficheInformation("Sauvegarde de l'export réussite");

        } catch (IOException e) {
            e.printStackTrace();
            afficheErreur("Erreur lors de l'export en CSV");
        }
    }


    /*
    PRIVATE FONCTION
     */
    private Robinet creerRobinet(double debit) {
        Robinet robinet = new Robinet(baignoire, debit);
        robinet.setOnSucceeded((WorkerStateEvent e) -> {
            LOG.info("Robinet deverse");
            java.time.Duration tempsDepuisDepart = java.time.Duration.between(top, Instant.now());
            linechart.getData().get(0).getData().add(new XYChart.Data<>(tempsDepuisDepart.toMillis(), baignoire.getVolume()));
            listeExport.add(new String[]{String.valueOf(tempsDepuisDepart.toMillis()), String.valueOf(baignoire.getVolume())});
            //On met a jour l'affichage
            mettreAJourBaignoire();

            //Si la baignoire pleine on arrete tout
            if (baignoire.estPlein() && simulationEnCours) {
                terminerSimulation();
            }
        });
        robinet.setPeriod(Duration.millis(VITESSE));
        return robinet;
    }

    private void terminerSimulation() {
        LOG.info("Arret de la simulation");
        simulationEnCours = false;
        java.time.Duration duration = java.time.Duration.between(top, Instant.now());
        System.out.println("Arret de la simulation après : " + duration.toMillis() + "ms");

        for (int i = 0; i < MAX_INOUT; i++) {
            if (fuites[i] != null) {
                fuites[i].cancel();
            }
            if (robinets[i] != null) {
                robinets[i].cancel();
            }
        }

        btn_demarrerArreter.setText("Demarrer simulation");

        mettreAJourAffichageBouton(simulationEnCours);
    }


    private double recupereDouble(TextField textField) throws NumberFormatException {
        try {
            String text = textField.getText();
            if (text.isBlank()) text = "0";
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            LOG.severe(String.format("%s - INPUT ERROR - expected a number got %s", textField.getId(), textField.getText()));
            throw e;
        }
    }

    private boolean verifieDebitArray(double[] debits) throws IllegalArgumentException {
        if (debits.length != MAX_INOUT)
            throw new IllegalArgumentException("La liste de debit par default doit faire une longueur de 5");
        for (double debit : debits) {
            if (debit < 0.0) throw new IllegalArgumentException("Les debits doivent être positif");
        }
        return true;
    }

    private void regleVisibiliteEntreSortieEau() {
        LOG.info("Modification de l'affichage des entrées/sortie d'eau");
        //robinets
        regleVisibiliteEntreSortieEau(robinet1, robinet2, robinet3, robinet4, debitRobinet);
        //fuites
        regleVisibiliteEntreSortieEau(fuite1, fuite2, fuite3, fuite4, debitFuite);
    }

    private void regleVisibiliteEntreSortieEau(Pane pane1, Pane pane2, Pane pane3, Pane pane4, double[] debits) {
        pane1.setVisible(true);
        pane2.setVisible(true);
        pane3.setVisible(true);
        pane4.setVisible(true);
        switch (Arrays.stream(debits).filter((elem) -> elem != 0).toArray().length) {
            case 0:
                pane1.setVisible(false);
            case 1:
                pane2.setVisible(false);
            case 2:
                pane3.setVisible(false);
            case 3:
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

    private void mettreAJourAffichageBouton(boolean simulationEnCours) {
        //On active desactives les boutons
        btn_reglageFuite.setDisable(simulationEnCours);
        btn_reglageCapacite.setDisable(simulationEnCours);
        btn_fuite1.setDisable(!simulationEnCours);
        btn_fuite2.setDisable(!simulationEnCours);
        btn_fuite3.setDisable(!simulationEnCours);
        btn_fuite4.setDisable(!simulationEnCours);

        //On masque les boutons des fuites désactivés
        txt_boucherFuite.setVisible(simulationEnCours);
        btn_fuite1.setVisible(simulationEnCours);
        btn_fuite2.setVisible(simulationEnCours);
        btn_fuite3.setVisible(simulationEnCours);
        btn_fuite4.setVisible(simulationEnCours);
    }

    private void mettreAJourBaignoire() {
        double maxHeight = baignoirePane.getHeight();
        double capacity = baignoire.getCapacite();
        double volume = baignoire.getVolume();
        eauBaignoire.setHeight((volume * maxHeight) / capacity);
        // On fait un produit en crois pour que ça complete tout le temps la baignoire
    }
}
