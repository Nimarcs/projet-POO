
package fr.ul.miage.marcus.POOProjet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application {
    /**
     * Création d'un logger
     */
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    /*
    Attributs
     */

    /**
     * Niveau de LOG de l'application est utilisé par les autres classe pour se mettre au niveau
     */
    public static Level currentLogLevel = Level.WARNING;

    /**
     * Méthode appelé au début de l'application par JavaFX
     * @param primaryStage Le Stage fournit par JavaFX dans lequel notre application doit se trouver
     * @throws IllegalArgumentException Dans le cas ou il y a une erreur sur la définition des paramètres par défaut
     */
    @Override
    public void start(Stage primaryStage) throws IllegalArgumentException{
        primaryStage.setTitle("Exercice JavaFX : Hello");
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/baignoire.fxml"));
            loader.setControllerFactory(c-> new BaignoireController(50, new double[]{10, 10, 0, 0}, new double[]{5, 9, 3, 0}));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            LOG.severe("Erreur lors de la lecture du .fxml");
            e.printStackTrace();
        }
    }

    /**
     * Mathode principale qui va traiter les arguments fourni au lancement
     * @param args argument fourni au lancement de l'application (-h pour plus d'information)
     */
    public static void main(String[] args){
        //syntaxe
        Options options = new Options();
        Option d = new Option("d", "debug", false, "mode debug");
        options.addOption(d);
        //parse
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("d")){
                currentLogLevel = Level.INFO;
            }
            LOG.setLevel(currentLogLevel);
        } catch (Exception exp) {
            LOG.severe("Erreur dans la ligne de commande");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("baignoire", options);
            System.exit(1);
        }
        //process
        LOG.info("Démarrage du traitement");
        launch(args);
        LOG.info("Fin du traitement");
    }
}
