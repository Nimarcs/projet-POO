package fr.ul.miage.marcus.POOfxml;

import java.util.logging.Logger;

public class Baignoire {

    //Creation d'un logger
    public static final Logger LOG = Logger.getLogger(Baignoire.class.getName());


    public static final double DEFAULT = 100;
    private double capacite;
    private double volume;

    public Baignoire() {
        this(DEFAULT);
    }

    public Baignoire(double capacite){
        LOG.setLevel(App.currentLogLevel);
        this.capacite = capacite;
    }

    public boolean estPlein() {
        return volume >= capacite;
    }
    public boolean estVide() {
        return volume <= 0;
    }

    public double ajouterEau(double eau){
        if (eau < 0.0) throw new IllegalArgumentException("L'eau est censé être positive");
        volume += eau;
        if (estPlein()){
            volume = capacite;
        }
        return volume;
    }

    public double retirerEau(double eau){
        if (eau < 0.0) throw new IllegalArgumentException("L'eau est censé être positive");
        volume -= eau;
        if (estVide()){
            volume = 0.0;
        }
        return volume;
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
    }
}
