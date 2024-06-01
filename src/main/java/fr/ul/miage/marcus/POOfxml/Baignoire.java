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

    public double getVolume() {
        return volume;
    }

    public void vider() {
        volume = 0.0;
    }
}
