package fr.ul.miage.marcus.POOfxml;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.util.logging.Logger;

public class Fuite extends ScheduledService<Baignoire> {

    public static final Logger LOG = Logger.getLogger(Fuite.class.getName());

    /**
     * Debit par default de la fuite
     */
    private static final double DEFAUT_DEBIT = 10;


    /**
     * Baignoire associée
     */
    private final Baignoire baignoire;

    /**
     * Debit du robinet
     */
    private double debit;


    /**
     * Contructeur de fuite
     * @param baignoire baignoire associée à la fuite
     */
    public Fuite(Baignoire baignoire){
        super();
        LOG.setLevel(App.currentLogLevel);
        this.baignoire = baignoire;
    }

    /**
     * Contrcuteur de fuite avec un debit precis
     * @param baignoire baignoire associée à la fuite
     * @param debit debit de la fuite
     */
    public Fuite(Baignoire baignoire, double debit){
        this(baignoire);
        if (checkDebit(debit))
            this.debit = debit;
        else
            LOG.severe("La valeur de débit est interdite lors de la creation de la fuite, la valeur par défaut va donc être attribué");

    }

    /**
     * Tâche lancer a chaque iteration
     * @return Task tache à effectuer
     */
    @Override
    protected Task<Baignoire> createTask() {
        return new Task<>() {
            @Override
            protected Baignoire call() {
                baignoire.retirerEau(debit);
                return baignoire;
            }
        };
    }

    /**
     * Getter du debit de la fuite
     * @return debit de la fuite
     */
    public double getDebit() {
        return debit;
    }

    /**
     * Methode qui rebouche la fuite
     */
    public void boucher(){
        this.debit = 0.0;
    }

    /**
     * Methode qui verifie si la valeur du debit est valide
     * Le débit doit être positif ou nul
     * @param debit valeur du debit a verifier
     * @return booleen à vrai si le débit est valide, faux sinon
     */
    private boolean checkDebit(double debit){
        return debit >= 0.0;
    }

}
