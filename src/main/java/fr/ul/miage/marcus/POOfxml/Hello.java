package fr.ul.miage.marcus.POOfxml;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Hello {

    //Creation d'un logger
    public static final Logger LOG = Logger.getLogger(Hello.class.getName());
    private List<String> prenoms = new ArrayList<>(Arrays.asList("Nasgul", "Maugluk"));

    public Hello(String filename){
        chargerPrenoms(filename);
    }

    /**
     * Donne le nom le plus proche selon la liste chargé, si aucune liste n'est chargé utilise celle par défaut
     * @param s nom fourni
     * @return nom le plus proche
     */
    public String lePlusProche(String s) {
        String res = prenoms.get(0);
        double dmax =1.0;
        NormalizedLevenshtein l = new NormalizedLevenshtein();
        for (String p: prenoms) {
            double d = l.distance(s, p);
            LOG.info(String.format("distance(%s;%s)=%.2f)", s, p, d));
            if (d < dmax) {
                dmax = d;
                res = p;
            }
        }
        return res;
    }

    /**
     * Permet de charger une liste de nom
     * @param filename chemin vers le fichier
     * @return nombre de nom chargé
     */
    public int chargerPrenoms(String filename) {
        int res = 0;
        try {
            Scanner sc = new Scanner(new File(filename));
            prenoms = new ArrayList<String>();
            while (sc.hasNextLine()) {
                prenoms.add(sc.nextLine());
                res++;
            }
        } catch (FileNotFoundException e) {
            LOG.warning(String.format("Fichier %s introuvable", filename));
        }
        LOG.info(String.format("%d prénoms chargés%n", res));
        return res;
    }

}
