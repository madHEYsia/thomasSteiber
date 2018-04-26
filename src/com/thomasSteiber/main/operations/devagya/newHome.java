package com.thomasSteiber.main.operations.devagya;

import com.thomasSteiber.main.operations.main.main;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class newHome extends Application {

    public static void main(String args[])
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Button dbmlModule = new Button("DBML Calculation");
        dbmlModule.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Button VshModule = new Button("Vshale Calculation");
        VshModule.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Button porosityModule = new Button("Porosity Calculation");
        porosityModule.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Button saturationModule = new Button("Saturation Calculation");
        saturationModule.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        BorderPane layout = new BorderPane(new HBox(dbmlModule, VshModule, porosityModule, saturationModule));
        layout.setMinHeight(400);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add(main.class.getResource("../../resources/css/main.css").toExternalForm());

        primaryStage.setTitle("Sandy Shale Lamination using Thomas Steiber Method");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        primaryStage.show();

    }

}
