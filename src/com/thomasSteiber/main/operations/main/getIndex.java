package com.thomasSteiber.main.operations.main;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class getIndex {

    int depthIndex = 0;
    int grIndex = 1;
    int nPhiIndex = 2;
    int rhobIndex = 3;
    String[] curvesRequired = {"Depth: ","GR: ","Nphi: ","Rhob: "};      //Just make changes here for curves required

    public int[] get(String[][] curves, int curveIndex){
        Stage stage = new Stage();

        VBox layout = new VBox(10);
        String[] index = new String[curveIndex+1];
        String[] names = new String[curveIndex+1];
        index[0] = "0";
        names[0] = "Select parameter (Description)";
        for(int i=1;i<=curveIndex;++i){
            index[i] = i+"";
            names[i] = curves[i-1][0]+" ("+curves[i-1][3]+")";
        }

        int indexes[] = new int[curvesRequired.length];

        GridPane grids = new GridPane();
        grids.setPadding(new Insets(10));
        grids.setHgap(15);
        grids.setVgap(10);
        for(int i=0;i<curvesRequired.length;++i){

            Label curve_i = new Label(curvesRequired[i]);
            grids.add(curve_i,0,i,2,1);

            ChoiceBox<String> choice_i = new ChoiceBox<>(FXCollections.observableArrayList(names));
            int finalI = i;
            choice_i.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
                String intValue = index[new_value.intValue()];
                indexes[finalI] = Integer.parseInt(intValue);
            });
            grids.add(choice_i,2,i,5,1);
        }

        Label error = new Label("");
        error.setFont(new Font("Arial", 11));
        error.setStyle("-fx-text-fill: red;");
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Button proceed = new Button("Proceed");
        proceed.setOnAction(e-> {
            boolean errorFound = false;
            for(int i=0;i<curvesRequired.length;++i){
                if(indexes[i]==0){
                    errorFound = true;
                    error.setText("Please select parameter "+curvesRequired[i]);
                    sleeper.setOnSucceeded(event-> error.setText(""));
                    new Thread(sleeper).start();
                }
            }
            if (!errorFound){
                for(int i=0;i<curvesRequired.length;++i)
                    indexes[i] = indexes[i]-1;
                stage.close();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e-> {
            indexes[0] = -1;
            stage.close();
        });

        layout.getChildren().addAll(grids, error, new HBox(10, proceed, cancel));

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
