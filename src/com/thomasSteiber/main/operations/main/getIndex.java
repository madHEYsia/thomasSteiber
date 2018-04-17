package com.thomasSteiber.main.operations.main;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class getIndex {

    int depthIndex = 0;
    int grIndex = 1;
    int nPhiIndex = 2;
    int rhobIndex = 3;
    String[] curvesRequired = {"Depth: ","GR: ","Nphi: ","Rhob: "};      //Just make changes here for curves required

    public int[] get(String[][] curves){

        VBox layout = new VBox();
        int len = curves.length;
        String[] index = new String[len];
        String[] names = new String[len];
        for(int i=0;i<len;++i){
            index[i] = i+"";
            names[i] = curves[i][0]+" ("+curves[i][3]+")";
        }
        ChoiceBox<String> cb = new ChoiceBox<>(FXCollections.observableArrayList(names));

        int indexes[] = new int[curvesRequired.length];

        GridPane grids = new GridPane();
        grids.setPadding(new Insets(10));
        grids.setHgap(15);
        grids.setVgap(10);
        for(int i=0;i<curvesRequired.length;++i){

            Label curve_i = new Label(curvesRequired[i]);
            grids.add(curve_i,0,i,2,1);

            ChoiceBox<String> choice_i = cb;
            int finalI = i;
            choice_i.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
                String intValue = index[new_value.intValue()];
                indexes[finalI] = Integer.parseInt(intValue);
            });
            grids.add(choice_i,2,i,5,1);
        }

        Stage stage = new Stage();
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
        return indexes;
    }

    public int getDepthIndex() {
        return depthIndex;
    }

    public int getRhobIndex() {
        return rhobIndex;
    }

    public int getGrIndex() {
        return grIndex;
    }

    public int getnPhiIndex() {
        return nPhiIndex;
    }
}
