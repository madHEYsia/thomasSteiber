package com.thomasSteiber.main.operations.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class main extends Application {
    public static Stage window;
    public BorderPane layout;
    public static Scene scene;

    public static void main(String args[])
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window=primaryStage;
        window.setTitle("Sandy Shale Lamination using Thomas Steiber Method");

        layout = new BorderPane();

        layout.setCenter(new Label("Enter Input here"));

        scene = new Scene(layout,600,500);
        window.setScene(scene);

//        scene.getStylesheets().add(main.class.getResource("../../resources/css/main.css").toExternalForm());
        window.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));

//        window.setMaximized(true);
        window.show();

    }
}