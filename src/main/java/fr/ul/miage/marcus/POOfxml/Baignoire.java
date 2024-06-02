package fr.ul.miage.marcus.POOfxml;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Baignoire {

    /**
     * Creation d'un logger
     */
    public static final Logger LOG = Logger.getLogger(Baignoire.class.getName());

    /**
     * Capacité par defaut de la baignoire
     */
    public static final double CAPACITE_PAR_DEFAUT = 100;

    /**
     * Suite de valeurs servant à créer un graphique via JavaFX
     */
    private XYChart.Series<Number, Number> xySeries;

    /**
     * Suite de valeurs servant à générer un csv de valeurs
     */
    private List<String[]> listeExport;

    /**
     * Instant de départ de la simulation pour les tableaux
     */
    private Instant top;

    /**
     * Capacité de la baignoire
     */
    private double capacite;

    /**
     * Volume occupé dans la baignoire
     */
    private double volume;

    /**
     * Constructeur qui contruit une baignoire par défaut
     */
    public Baignoire() {
        this(CAPACITE_PAR_DEFAUT);
    }

    /**
     * Contructeur qui contruit une baignoire avec une capacité spécifique
     * @param capacite capacité de la baignoire
     */
    public Baignoire(double capacite){
        LOG.setLevel(App.currentLogLevel);
        this.capacite = capacite;
        xySeries = new XYChart.Series<>();
        xySeries.setName("Remplisage baignoire");
        listeExport = new ArrayList<>();
    }

    /**
     * Méthode qui permet d'ajouter de l'eau dans la baignoire
     *
     * Cette méthode est synchronisée pour assurer que deux threads concurrents ne peuvent pas se chevaucher
     *
     * @param eau eau à ajouter
     * @throws IllegalArgumentException Si l'eau fournie est négative
     */
    public synchronized void ajouterEau(double eau){
        if (eau < 0.0) throw new IllegalArgumentException("L'eau est censé être positive");
        double nouveauVolume = volume + eau;
        if (estPlein(nouveauVolume)){
            volume = capacite;
        } else {
          volume = nouveauVolume;
        }
        ajouterNouvelleValeurDansGraphiques();
    }

    /**
     * Méthode qui permet de retirer de l'eau dans la baignoire
     *
     * Cette méthode est synchronisée pour assurer que deux threads concurrents ne peuvent pas se chevaucher
     *
     * @param eau eau à retirer
     * @throws IllegalArgumentException Si l'eau fournie est négative
     */
    public synchronized void retirerEau(double eau){
        if (eau < 0.0) throw new IllegalArgumentException("L'eau est censé être positive");
        double nouveauVolume = volume - eau;
        if (estVide(nouveauVolume)){
            LOG.info("Plus d'eau !");
            volume = 0.0;
        }
        else {
            volume = nouveauVolume;
        }
        ajouterNouvelleValeurDansGraphiques();

    }

    /**
     * Permet de savoir si la baignoire serai pleine avec un volume théorique
     * @param volume volume théorique
     * @return vrai si la baignoire serait pleine, faux sinon
     */
    public boolean estPlein(double volume) {
        return volume >= capacite;
    }

    /**
     * Permet de savoir si la baignoire est pleine
     * @return vrai si la baignoire est pleine, faux sinon
     */
    public boolean estPlein() {
        return estPlein(volume);
    }

    /**
     * Permet de savoir si la baignoire serai vide avec un volume théorique
     * @param volume volume théorique
     * @return vrai si la baignoire serait vide, faux sinon
     */
    public boolean estVide(double volume) {
        return volume <= 0;
    }

    /**
     * Permet de savoir si la baignoire est vide
     * @return vrai si la baignoire est vide, faux sinon
     */
    public boolean estVide() {
        return estVide(volume);
    }

    /**
     * Setter de la capacité
     * @param capacite capacité voulue
     * @throws IllegalArgumentException est renvoyée si la capacité est négative
     */
    public void setCapacite(double capacite) throws IllegalArgumentException {
        if (capacite < 0.0) {
            LOG.severe("Capacité d'une baignoire doit être positif, or " + capacite + " est négatif");
            throw new IllegalArgumentException("Capacité doit être positif");
        }
        this.capacite = capacite;
    }

    /**
     * Getter du volume d'eau dans la baignoire
     * @return volume d'eau dans la baignoire
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Capacité de la baignoire
     * @return Capacité de la baignoire
     */
    public double getCapacite() {
        return capacite;
    }

    /**
     * Vide complementement la baignoire
     * Reinitialise les tableaux de valeurs pour une prochaine expérience
     */
    public void vider() {
        volume = 0.0;
        xySeries.getData().clear();
        listeExport.clear();
    }

    /**
     * Définit le temps de début d'enregistrement des données de la baignoire
     * @param top instant de début d'expérience
     */
    public void setTop(Instant top) {
        this.top = top;
    }

    /**
     * Getter de la liste d'export
     * @return liste d'export
     */
    public List<String[]> getListeExport() {
        return Collections.unmodifiableList(listeExport);
    }

    /**
     * Getter du tableau de valeur à destination de JavaFx
     * @return XYChart.Series de l'expérience courante
     */
    public XYChart.Series<Number, Number> getXySeries() {
        return xySeries;
    }

    /**
     * Permet d'ajouter une nouvelle valeur dans les graphiques de l'expérience
     */
    private void ajouterNouvelleValeurDansGraphiques() {
        java.time.Duration tempsDepuisDepart = java.time.Duration.between(top, Instant.now());
        listeExport.add(new String[]{String.valueOf(tempsDepuisDepart.toMillis()), String.valueOf(volume)});
        //On ajoute la tache d'ajouter la valeur car cela ne tourne pas dans le même thread que javaFX
        Platform.runLater(
                () -> xySeries.getData().add(new XYChart.Data<>(tempsDepuisDepart.toMillis(), volume))
        );
    }
}
