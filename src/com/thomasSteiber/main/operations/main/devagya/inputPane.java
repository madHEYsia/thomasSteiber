package com.thomasSteiber.main.operations.main.devagya;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class inputPane {

    static BorderPane layout = new BorderPane();
    static VBox loadedLas = new VBox(5);

    public static BorderPane inputs(){

        Button loadLas = lasLoad();
        layout.setTop(new HBox(loadLas, loadedLas));

        



        return layout;

    }
    public static Button lasLoad(){
        Button loadLas = new Button("Add Las file");
        loadLas.setOnAction(e->{

        });
        return loadLas;
    }
}
