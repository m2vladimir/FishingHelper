package com.fishbot.ui.app;

import com.fishbot.ui.controller.FishbotController;
import com.fishbot.ui.controller.HowtoController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;

public class FisherApp extends Application {

    private static Stage primaryStage;
    private static FishbotController fishbotController;

    public static void main(String[] args) {

        File lib_path;

        if (System.getProperty("os.arch").contains("64")) {

            lib_path = new File("lib\\x64\\" + Core.NATIVE_LIBRARY_NAME + ".dll");

        } else {

            lib_path = new File("lib\\x86\\" + Core.NATIVE_LIBRARY_NAME + ".dll");
        }

        System.load(lib_path.getAbsolutePath());

        launch(args);
    }

    public static void openHowToWindow() throws IOException {

        FXMLLoader loader = new FXMLLoader(FisherApp.class.getClassLoader().getResource("com/fishbot/ui/view/howto.fxml"));

        Parent root = loader.load();

        HowtoController controller = loader.getController();

        if (!controller.isWindowExist()) {

            Scene scene = new Scene(root);
            scene.getStylesheets().add(FisherApp.class.getClassLoader().getResource("com/fishbot/ui/resources/css/fishbot.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("User manual");
            stage.initStyle(StageStyle.UTILITY);
            stage.initOwner(primaryStage);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                    controller.setIsWindowExist(false);
                }
            });
            stage.show();

            controller.setIsWindowExist(true);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FisherApp.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(FisherApp.class.getClassLoader().getResource("com/fishbot/ui/view/fishbot.fxml"));
        Parent root = loader.load();

        fishbotController = loader.getController();
        fishbotController.setHostServices(getHostServices());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("com/fishbot/ui/resources/css/fishbot.css").toExternalForm());

        primaryStage.setTitle("Fishing Helper");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("com/fishbot/ui/resources/images/icon.png")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();

        //TODO -DELAYED: Configuration scene + controller
        //TODO -DELAYED: maven structure
    }
}
