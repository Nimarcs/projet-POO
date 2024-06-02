package fr.ul.miage.marcus.POOfxml;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.util.logging.Logger;

public class Robinet extends ScheduledService<Baignoire> {

    public static final Logger LOG = Logger.getLogger(Robinet.class.getName());

    /**
     * Debit par default du robinet
     */
    private static final double DEFAUT_DEBIT = 10;

    /**
     * Baignoire associée a ce robinet
     */
    private final Baignoire baignoire;

    /**
     * Debit du robinet
     */
    private double debit;

    /**
     * Contructeur de robinet
     * @param baignoire baignoire associée
     */
    public Robinet(Baignoire baignoire){
        LOG.setLevel(App.currentLogLevel);
        this.baignoire = baignoire;
        this.debit = DEFAUT_DEBIT;
    }

    /**
     * Constructeur de robinet avec un débit précis
     * @param baignoire baignoire associée
     * @param debit debit du robinet
     */
    public Robinet(Baignoire baignoire, double debit){
        this(baignoire);
        if (checkDebit(debit)){
            this.debit = debit;
        } else
            LOG.severe("La valeur de débit est interdite lors de la creation du robinet, la valeur par défaut va donc être attribué");
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
                synchronized (baignoire){
                    baignoire.ajouterEau(debit);
                    return baignoire;
                }
            }
        };
    }

    /**
     * Getter du débit
     * @return debit du robinet
     */
    public double getDebit() {
        return debit;
    }

    /**
     * Setter du debit
     * @param debit debit à définir
     */
    public void setDebit(double debit) {
        if (checkDebit(debit))
            this.debit = debit;
        else
            LOG.severe("Le debit doit être positif, changement ignoré");
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
