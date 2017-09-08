package com.thomasSteiber.main.operations.main;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class graphUI {

    double coordinates[][] = new double[4][2];
    public void plot(double values[]){

        getCoordinates(values);

        for (int i=0;i<4;++i)
            System.out.println(coordinates[i][0]+", "+coordinates[i][1]);

        BorderPane layout = new BorderPane(null,null,null,null,lineGraph());

        Scene scene = new Scene(layout,500,500);
//        scene.getStylesheets().add(main.class.getResource("../../resources/css/main.css").toExternalForm());

        Stage window = new Stage();
        window.setTitle("Graphical representation");
        window.setScene(scene);
        window.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
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
        return layout;
    }

}
