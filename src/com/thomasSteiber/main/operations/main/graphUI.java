package com.thomasSteiber.main.operations.main;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class graphUI {

    double coordinates[][] = new double[4][2];
    Stage window;
    static Label error = new Label("");

    public void plot(double values[]){
        window = new Stage();

        getCoordinates(values);

        for (int i=0;i<4;++i)
            System.out.println(coordinates[i][0]+", "+coordinates[i][1]);

        BorderPane layout = new BorderPane(null,null,parameter(),null,lineGraph());

        Scene scene = new Scene(layout,500,500);

        window.setTitle("Graphical representation");
        window.setScene(scene);
        window.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        window.setMinWidth(400);
        window.setMinHeight(400);
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
    }

    public void getCoordinates(double values[]){

        // Y_min
        coordinates[0][0] = values[7];
        // Y_max
        coordinates[2][0] = values[6];

        //line A both Porosity index   Φ_lam = Y*Φ_a + (1-Y)*Φ_b
        coordinates[0][1] = values[7]*values[0] + (1-values[7])*values[1];  // Φ_min at Y_min
        coordinates[2][1] = values[6]*values[0] + (1-values[6])*values[1];  // Φ_max at Y_max

        //line B for lower index
        coordinates[3][1] = values[0]*values[1];  // Φ_min = Φ_a * Φ_b
        if (values[5]==-999999 || values[5] < values[0])  // X_a == null || X_a < Φ_a
            coordinates[3][0] = 1 - (values[0]-values[0]*values[1])/((1-values[1])*(1- values[2]/values[3]));  // Y_min
        else
            coordinates[3][0] = 1- values[0];

        //line D for upper index
        coordinates[1][0] = 1- values[0];  // Y_min
        coordinates[1][1] = values[0] + (1-values[7])*values[1];  // Φ_min = Φ_a + (1-Y)*Φ_b

    }

    public BorderPane lineGraph(){

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Gamma y");
        yAxis.setLabel("Porosity Φ");
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("Plot of Gamma Vs Porosity");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Line A");
        series1.getData().add(new XYChart.Data(coordinates[0][0], coordinates[0][1]));
        series1.getData().add(new XYChart.Data(coordinates[2][0], coordinates[2][1]));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Line B");
        series2.getData().add(new XYChart.Data(coordinates[2][0], coordinates[2][1]));
        series2.getData().add(new XYChart.Data(coordinates[3][0], coordinates[3][1]));

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Line C");
        series3.getData().add(new XYChart.Data(coordinates[0][0], coordinates[0][1]));
        series3.getData().add(new XYChart.Data(coordinates[3][0], coordinates[3][1]));

        XYChart.Series series4 = new XYChart.Series();
        series4.setName("Line D");
        series4.getData().add(new XYChart.Data(coordinates[1][0], coordinates[1][1]));
        series4.getData().add(new XYChart.Data(coordinates[2][0], coordinates[2][1]));

        lineChart.getData().addAll(series1, series2, series3, series4);

        BorderPane layout = new BorderPane(lineChart);
        window.widthProperty().addListener(e-> layout.setPrefWidth(window.getWidth()*0.6));
        return layout;
    }

    public BorderPane parameter(){

        VBox parameters = new VBox(20);

        String labelTitle[] = {"Line A Division", "Line B Division"};
        for (int i=0;i<2;++i){
            Label label = new Label(labelTitle[i]);
            label.setFont(Font.font("Open Sans", FontWeight.NORMAL, 15));
            label.setPrefWidth(100);
            label.setWrapText(true);

            TextField value = new TextField();
            value.setPrefColumnCount(15);
            value.setPrefHeight(30);
            int finalI = i;
            value.textProperty().addListener((observable, oldValue, newValue) -> {
                double temp;
                try{
                    newValue = newValue.replaceAll("\\s+",""); //this removes all white spaces from input
                    temp = Double.parseDouble(newValue);
                    value.setText(temp+"");
                }
                catch (Exception e){
                    value.setText(oldValue);
                    errorPopup("Invalid "+labelTitle[finalI]+" entry");
                }
            });

            Button submit = new Button("Divide");
            submit.setFont(Font.font("Open Sans", FontWeight.SEMI_BOLD, 15));
            submit.setAlignment(Pos.CENTER);
            submit.setTextFill(Color.web("e5e5e5"));
            submit.setStyle("-fx-background-color : #004c00");
            submit.setPadding(new Insets(5));
            submit.setCursor(Cursor.HAND);
            submit.setOnAction(e-> {
                if (Integer.parseInt(value.getText())>0)
                    divide(finalI);
                else
                    errorPopup("Positive Numeric Values required");

            });

            HBox divideRow = new HBox(10, label, value, submit);
            parameters.getChildren().add(divideRow);
        }



        error.setPadding(new Insets(5,15,5,15));
        error.setFont(Font.font("Open Sans", FontWeight.SEMI_BOLD, 15));
        error.setTextFill(Color.web("#330000"));
        error.setStyle("-fx-background-color: transparent");

        parameters.getChildren().add(error);

        BorderPane layout = new BorderPane(parameters);
        window.widthProperty().addListener(e-> layout.setPrefWidth(window.getWidth()*0.4));
        return layout;

    }

    public static void divide(int i){

    }

    public static void errorPopup(String errorText){
        error.setStyle("-fx-background-color: #ff7f7f");
        error.setText(errorText);
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(ee-> {
            error.setStyle("-fx-background-color: transparent");
            error.setText("");
        });
        new Thread(sleeper).start();
    }

}
