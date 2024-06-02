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

/**
 * @author Marcus Richier
 * Controleur de l'application, gère les actions à effectuer lors d'interaction avec l'interface
 */
public class BaignoireController {

    /*
    Constantes
     */

    /**
     * Logger du controller
     */
    public static final Logger LOG = Logger.getLogger(BaignoireController.class.getName());

    /**
     * Maximum de robinet et de fuite géré par l'application
     * /!\ Changer cette valeur nécéssite de changer l'interface graphique
     */
    private static final int MAX_INOUT = 4;

    /**
     * Vitesse de l'expérience
     * Nombre de milliseconde entre chaque ajout/ponction d'eau dans la baignoire
     */
    public static final int VITESSE = 500;

    /*
    FXML
     */

    @FXML
    private LineChart<Number, Number> linechart;

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
    private Button btn_reglageFuite;

    @FXML
    private Button btn_reglageCapacite;

    @FXML
    private Text txt_boucherFuite;

    /*
    Attribut du controller
     */

    /**
     * Baignoire qui sert de support aux expériences
     */
    private final Baignoire baignoire;

    /**
     * Booléen à vrai si une simulation est en cours, à faux sinon
     */
    private boolean simulationEnCours;

    /**
     * Array qui stocke les débits des robinets/fuites pour pouvoir recréer une simulation
     */
    private double[] debitRobinet, debitFuite;

    /**
     * Début de la dernière simulation
     */
    private Instant top;

    /**
     * Array des robinets qui verse dans la baignoire
     * contient des null pour les robinets éteins
     */
    private Robinet[] robinets;

    /**
     * Array des fuites de la baignoire
     * contient des null pour les fuites rebouchées/inexistante
     */
    private Fuite[] fuites;

    /**
     * Contructeur du controller
     * @param capacite capacité la baignoire par défaut
     * @param debitRobinet debit des robinets par défaut <b>/!\ Doit avoir une longueur de MAX_INOUT</b>
     * @param debitFuite debit des fuites par défaut <b>/!\ Doit avoir une longueur de MAX_INOUT</b>
     * @throws IllegalArgumentException renvoyé si les valeurs par défaut sont illégale
     */
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

        NumberAxis axeX = new NumberAxis();
        axeX.setLabel("Temps");
        NumberAxis axeY = new NumberAxis();
        axeY.setLabel("Volume");
        linechart = new LineChart<>(axeX, axeY);

    }


    /*
    Reaction a l'appui de bouton
     */

    /**
     * Méthode qui gère le bouton Démarrer et le bouton Arreter
     * Si la simulation est en cours l'arrete et change le texte du bouton, sinon la démarre et change le texte du bouton
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
            top = Instant.now();
            baignoire.setTop(top);

            linechart.getData().clear();
            linechart.getData().add(baignoire.getXySeries());

            mettreAJourAffichageBouton(simulationEnCours);

            regleVisibiliteEntreSortieEau();

            //On defini les robinets
            robinets = new Robinet[MAX_INOUT];
            for (int i = 0; i < debitRobinet.length; i++) {
                double debit = debitRobinet[i];
                Robinet robinet = creerRobinet(debit);
                robinets[i] = robinet;
                robinet.start();
            }

            //On defini les fuites
            fuites = new Fuite[MAX_INOUT];
            for (int i = 0; i < debitFuite.length; i++) {
                Fuite fuite = new Fuite(baignoire, debitFuite[i]);
                fuite.setOnSucceeded((WorkerStateEvent e) -> {
                    LOG.info("Fuite vide");
                   mettreAJourBaignoire();

                });

                fuite.setPeriod(Duration.millis(VITESSE));
                fuites[i] = fuite;
                fuite.start();
            }

            btn_demarrerArreter.setText("Arreter simulation");

        }
        btn_demarrerArreter.setDisable(false);
    }

    /**
     * Méthode qui gère les MAX_INOUT boutons de rebouchage de fuite
     * Traite sur quel bouton l'on a appuyé et rebouche la fuite correspondante
     * @param event evenement d'appui sur le bouton
     */
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

    /**
     * Méthode qui met à jour le réglage des robinets
     * Peut fonctionner alors que la simulation est en cours
     */
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

            regleVisibiliteEntreSortieEau();

            afficheInformation("Les robinets ont bien été mis à jour");

        } catch (IllegalArgumentException ignored) {
            afficheErreur("Valeur d'un des robinets erronée");
        }
    }

    /**
     * Méthode qui permet de changer les débits des différentes fuites
     * Ne doit pas être lancé alors que la simulation est en cours
     */
    @FXML
    void reglageFuite() {

        if (simulationEnCours) {
            afficheErreur("On ne peut pas parametrer les fuites en cours de route");
            return;
        }

        debitFuite = new double[]{recupereDouble(tf_reglageFuite1), recupereDouble(tf_reglageFuite2), recupereDouble(tf_reglageFuite3), recupereDouble(tf_reglageFuite4)};
        afficheInformation("Les fuites ont bien été mise à jour");
    }

    /**
     * Methode qui permet de changer la capacité de la baignoire
     * Ne doit pas être lancé alors que la simulation est en cours
     */
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

    /**
     * Méthode qui permet d'exporter la version CSV du graphique
     */
    @FXML
    public void exporterCSV() {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("export.csv"));
            writer.writeAll(baignoire.getListeExport());
            writer.close();

            afficheInformation("Sauvegarde de l'export réussite, le fichier est à la racine");

        } catch (IOException e) {
            e.printStackTrace();
            afficheErreur("Erreur lors de l'export en CSV");
        }
    }


    /*
    PRIVATE FONCTION
     */

    /**
     * Méthode qui permet de créer un nouveau robinet
     * @param debit debit du nouveau robinet
     * @return robinet créé
     */
    private Robinet creerRobinet(double debit) {
        Robinet robinet = new Robinet(baignoire, debit);
        robinet.setOnSucceeded((WorkerStateEvent e) -> {
            LOG.info("Robinet deverse");

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

    /**
     * Méthode qui permet de mettre fin à la simulation
     */
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

    /**
     * Méthode qui récupère le double contenu dans un TextField
     * Si le textField est vide, on suppose 0
     *
     * @param textField TextField supposé contenir un double
     * @return double contenu
     * @throws NumberFormatException Si le TextField ne contient pas de double
     */
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

    /**
     * Methode qui vérifie la validité d'un array de debit
     * @param debits array de débit
     * @throws IllegalArgumentException renvoyé en cas de non validité
     */
    private void verifieDebitArray(double[] debits) throws IllegalArgumentException {
        if (debits.length != MAX_INOUT)
            throw new IllegalArgumentException("La liste de debit par default doit faire une longueur de 5");
        for (double debit : debits) {
            if (debit < 0.0) throw new IllegalArgumentException("Les debits doivent être positif");
        }
    }

    /**
     * Methode qui met a jour le visuel des robinet et fuite sur l'interface graphique
     */
    private void regleVisibiliteEntreSortieEau() {
        LOG.info("Modification de l'affichage des entrées/sortie d'eau");
        //robinets
        regleVisibiliteEntreSortieEau(robinet1, robinet2, robinet3, robinet4, debitRobinet);
        //fuites
        regleVisibiliteEntreSortieEau(fuite1, fuite2, fuite3, fuite4, debitFuite);
    }

    /**
     * Méthode qui gére l'affichage de Panel en fonction du nombre de débit non null
     * @param pane1 Panel à afficher ou non
     * @param pane2 Panel à afficher ou non
     * @param pane3 Panel à afficher ou non
     * @param pane4 Panel à afficher ou non
     * @param debits array de debit qui permet de savoir combien seront affiché
     */
    private void regleVisibiliteEntreSortieEau(Pane pane1, Pane pane2, Pane pane3, Pane pane4, double[] debits) {
        pane1.setVisible(true);
        pane2.setVisible(true);
        pane3.setVisible(true);
        pane4.setVisible(true);
        switch (Arrays.stream(debits).filter((elem) -> elem > 0).toArray().length) {
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

    /**
     * Méthode qui permet d'afficher un message d'erreur dans l'application
     * @param message message d'erreur a afficher
     */
    private void afficheErreur(String message) {
        LOG.severe(message);
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.show();
    }

    /**
     * Méthode qui permet d'afficher un message d'information dans l'application
     * @param message message à afficher
     */
    private void afficheInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
    }

    /**
     * Méthode qui met à jour l'affichage et l'aspect cliquable des boutons en fonction de si la simulation est en cours
     * @param simulationEnCours booleen à vrai si la simulation est en cours, faux sinon
     */
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

    /**
     * Méthode qui met à jour l'aspect de la baignoire pour que sont remplisage soit proportionel à sa taille
     */
    private void mettreAJourBaignoire() {
        double maxHeight = baignoirePane.getHeight();
        double capacity = baignoire.getCapacite();
        double volume = baignoire.getVolume();
        eauBaignoire.setHeight((volume * maxHeight) / capacity);
        // On fait un produit en crois pour que ça complete tout le temps la baignoire
    }
}
