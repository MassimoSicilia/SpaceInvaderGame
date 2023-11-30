package edu.vanier.template;

import com.sun.javafx.logging.Logger;
import edu.vanier.template.controllers.MainAppController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

 
/**
 * This is a JavaFX project template to be used for creating GUI applications.
 * JavaFX 20.0.2 is already linked to this project in the build.gradle file.
 * @link: https://openjfx.io/javadoc/20/
 * @see: Build Scripts/build.gradle
 * @author Sleiman Rabah.
 */
public class MainApp extends Application{

    MainAppController controller;
    public static void main(String[] args){
        launch(args);
    }
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml//mainApp_layout.fxml"));
        controller = new MainAppController();
        loader.setController(controller);
        Pane root = loader.load();
        Scene scene = new Scene(root,600, 820);
        controller.setScene(scene);
        controller.initGameComponents();
        primaryStage.setScene(scene);
        primaryStage.setTitle("game");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.sizeToScene();
        primaryStage.show();
        }catch(IOException e){
            System.out.println(e.getMessage());
            
        }
    }
    @Override
    public void stop() throws Exception {
        controller.stopAnimation();
    }
    

}
