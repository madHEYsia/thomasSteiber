package com.thomasSteiber.main.operations.main;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static com.thomasSteiber.main.operations.main.indexes.*;

public class lasAreaPlot {

    public void plot(double data[][]){
        Stage stage = new Stage();
        HBox curves = new HBox();

        int len = data[0].length;
        for(int i=0;i<len;++i){
            if (i==depthIndex){
                NumberAxis xAxis = new NumberAxis();
                NumberAxis yAxis = new NumberAxis();
                LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Depth");
                lineChart.getXAxis().setTickLabelsVisible(false);
                lineChart.getXAxis().setOpacity(0);
                lineChart.setPrefWidth(30);
                lineChart.setMaxWidth(30);
                lineChart.setMinWidth(30);
                lineChart.setPadding(new Insets(0,0,10,0));
                curves.getChildren().add(lineChart);
            }
            else if (i==grIndex){
                NumberAxis xAxis = new NumberAxis(1, 31, 1);
                NumberAxis yAxis = new NumberAxis();
                AreaChart<Number,Number> areaChart = new AreaChart<>(xAxis, yAxis);
                areaChart.getYAxis().setTickLabelsVisible(false);
                areaChart.getYAxis().setOpacity(0);
                areaChart.setTitle("Temperature Monitoring (in Degrees C)");

                XYChart.Series seriesApril= new XYChart.Series();
                seriesApril.setName("April");
                seriesApril.getData().add(new XYChart.Data(1, 4));
                seriesApril.getData().add(new XYChart.Data(3, 10));
                seriesApril.getData().add(new XYChart.Data(6, 15));
                seriesApril.getData().add(new XYChart.Data(9, 8));
                seriesApril.getData().add(new XYChart.Data(12, 5));
                seriesApril.getData().add(new XYChart.Data(15, 18));
                seriesApril.getData().add(new XYChart.Data(18, 15));
                seriesApril.getData().add(new XYChart.Data(21, 13));
                seriesApril.getData().add(new XYChart.Data(24, 19));
                seriesApril.getData().add(new XYChart.Data(27, 21));
                seriesApril.getData().add(new XYChart.Data(30, 21));
                areaChart.getData().add(seriesApril);
                curves.getChildren().add(areaChart);
            }
            else if(i==nPhiIndex || i==rhobIndex){
                NumberAxis xAxis = new NumberAxis();
                NumberAxis yAxis = new NumberAxis();
                LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.getYAxis().setTickLabelsVisible(false);
                lineChart.getYAxis().setOpacity(0);
                lineChart.setPadding(new Insets(0));
                curves.getChildren().add(lineChart);
            }
        }
        Scene scene = new Scene(curves);
        stage.setScene(scene);
        stage.show();
    }

}
