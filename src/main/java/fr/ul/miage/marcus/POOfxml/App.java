
package fr.ul.miage.marcus.POOfxml;

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
    //Création d'un logger
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    public static Level currentLogLevel = Level.WARNING;

    //Attribut
    private static Baignoire hello = null;

    private static String fichier = null;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Exercice JavaFX : Hello");
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/baignoire.fxml"));
            loader.setControllerFactory(c-> new BaignoireController(100, new int[]{1, 1, 1, 1}, new int[]{1, 1, 1, 1}));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        //arguments
        String fprenoms = "samples/prenoms.txt";
        //syntaxe
        Options options = new Options();
        Option f = new Option("f", "file", true, "fichier des prénoms");
        options.addOption(f);
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
            if (line.hasOption("f")) {
                fprenoms = line.getOptionValue('f');
            }
        } catch (Exception exp) {
            LOG.severe("Erreur dans la ligne de commande");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("hello", options);
            System.exit(1);
        }
        hello = new Baignoire();
        //process
        LOG.info("Démarrage du traitement");
        fichier = fprenoms;
        launch(args);
        LOG.info("Fin du traitement");
    }
}
