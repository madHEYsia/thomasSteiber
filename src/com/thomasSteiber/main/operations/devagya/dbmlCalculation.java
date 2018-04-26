package com.thomasSteiber.main.operations.devagya;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class dbmlCalculation {

    String[] names = {"Default Offshore [FLAG]","True Vertical Depth [FEET]","KB from surface(MSL) [FEET]","Water Depth plus KB [FEET]"};
    double[] values = {1,1000,500,400};

    public void module(){

        GridPane grids = new GridPane();
        grids.setAlignment(Pos.CENTER);
        grids.setPadding(new Insets(10));
        grids.setVgap(20);
        grids.setHgap(10);

        double[] dbml = {0.0};
        if ((int)values[0]==1)
            dbml[0] = values[1]-values[2]-values[3];
        else
            dbml[0] = values[1]-values[2];

        Label answerLabel = new Label("DBML calculated [FEET]");
        answerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        Label answerValue = new Label(dbml[0]+"");
        answerValue.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        ChoiceBox<String> choice = new ChoiceBox<>(FXCollections.observableArrayList("1", "Other"));
        choice.setValue("1");
        choice.setTooltip(new Tooltip("Select the flag"));
        for (int i=0;i<names.length;++i){
            Label labelText = new Label(names[i]);
            labelText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            grids.add(labelText,0,i,2,1);

            if (i==0){
                choice.getSelectionModel().selectedIndexProperty().addListener((ov, new_value, old_value) -> {
                    values[0] = new_value.equals("1") ? 1 : 0;
                    if ((int)values[0]==1)
                        dbml[0] = values[1]-values[2]-values[3];
                    else
                        dbml[0] = values[1]-values[2];
                    answerValue.setText(dbml[0]+"");
                });
                grids.add(choice,2,i,5,1);
            }
            else{
                TextField value = new TextField(values[i]+"");
                int finalI = i;
                value.textProperty().addListener((observable, oldValue, newValue) -> {
                    try{
                        double temp = Double.parseDouble(newValue);
                        values[finalI] = temp;
                        if ((int)values[0]==1)
                            dbml[0] = values[1]-values[2]-values[3];
                        else
                            dbml[0] = values[1]-values[2];
                    }
                    catch (Exception e){
                        value.setText(oldValue);
                    }
                    answerValue.setText(dbml[0]+"");
                });
                grids.add(value,2,i,5,1);
            }
        }

        grids.add(answerLabel,0,names.length,2,1);
        grids.add(answerValue,2,names.length,5,1);

        BorderPane layout = new BorderPane(grids);
        Scene scene = new Scene(layout);

        Stage stage = new Stage();
        stage.setTitle("DBML Calculation");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        stage.show();
    }

}
