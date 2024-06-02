package fr.ul.miage.marcus.POOfxml;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Baignoire {

    //Creation d'un logger
    public static final Logger LOG = Logger.getLogger(Baignoire.class.getName());

    private XYChart.Series<Number, Number> xySeries;
    private List<String[]> listeExport;

    private Instant top;


    public static final double DEFAULT = 100;
    private double capacite;
    private double volume;

    public Baignoire() {
        this(DEFAULT);
    }

    public Baignoire(double capacite){
        LOG.setLevel(App.currentLogLevel);
        this.capacite = capacite;
        xySeries = new XYChart.Series<>();
        xySeries.setName("Remplisage baignoire");
        listeExport = new ArrayList<>();
    }

    public boolean estPlein(double volume) {
        return volume >= capacite;
    }

    public boolean estPlein() {
        return estPlein(volume);
    }

    public boolean estVide(double volume) {
        return volume <= 0;
    }

    public boolean estVide() {
        return estVide(volume);
    }

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

    public void setCapacite(double capacite) throws IllegalArgumentException {
        if (capacite < 0.0) {
            LOG.severe("Capacité d'une baignoire doit être positif, or " + capacite + " est négatif");
            throw new IllegalArgumentException("Capacité doit être positif");
        }
        this.capacite = capacite;
    }

    public double getVolume() {
        return volume;
    }

    public double getCapacite() {
        return capacite;
    }

    public void vider() {
        volume = 0.0;
        xySeries.getData().clear();
        listeExport.clear();
    }

    public void setTop(Instant top) {
        this.top = top;
    }

    public List<String[]> getListeExport() {
        return Collections.unmodifiableList(listeExport);
    }

    public XYChart.Series<Number, Number> getXySeries() {
        return xySeries;
    }


    private void ajouterNouvelleValeurDansGraphiques() {
        java.time.Duration tempsDepuisDepart = java.time.Duration.between(top, Instant.now());
        listeExport.add(new String[]{String.valueOf(tempsDepuisDepart.toMillis()), String.valueOf(volume)});
        Platform.runLater(
                () -> xySeries.getData().add(new XYChart.Data<>(tempsDepuisDepart.toMillis(), volume))
        );
    }
}
