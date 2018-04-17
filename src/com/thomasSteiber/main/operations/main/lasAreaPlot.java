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

    public void plot(double data[][], double nullvalue){
        Stage stage = new Stage();
        HBox curves = new HBox();

        int len = data[0].length;
        int rows = data.length;
        for(int i=0;i<len;++i){
            if (i==depthIndex){
                NumberAxis xAxis = new NumberAxis();
                NumberAxis yAxis = new NumberAxis(data[rows-1][depthIndex],data[0][depthIndex],-100);
                LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Depth");
                lineChart.getXAxis().setTickLabelsVisible(false);
                lineChart.getXAxis().setOpacity(0);
                lineChart.setPrefWidth(30);
                lineChart.setMaxWidth(70);
                lineChart.setMinWidth(30);
                lineChart.setPadding(new Insets(0,0,10,0));
                curves.getChildren().add(lineChart);
            }
            else if (i==vshaleIndex){
                NumberAxis xAxis = new NumberAxis(1, 31, 1);
                NumberAxis yAxis = new NumberAxis();
                AreaChart<Number,Number> areaChart = new AreaChart<>(xAxis, yAxis);
                areaChart.getYAxis().setTickLabelsVisible(false);
                areaChart.getYAxis().setOpacity(0);
                areaChart.setCreateSymbols(false);
                areaChart.setLegendVisible(false);
                areaChart.setTitle("Vshale");

                XYChart.Series seriesApril= new XYChart.Series();
                seriesApril.getData().add(new XYChart.Data(1, 4));

                areaChart.getData().add(seriesApril);
                curves.getChildren().add(areaChart);
            }
            else if(i==nPhiIndex || i==rhobIndex || i==grIndex){
                String plotName = "";
                if (i==nPhiIndex) plotName = "Nphi";
                if (i==rhobIndex) plotName = "Rhob";
                if (i==grIndex) plotName = "GRI";

                NumberAxis xAxis = new NumberAxis();
                NumberAxis yAxis = new NumberAxis(data[rows-1][depthIndex],data[0][depthIndex],5);
                LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setCreateSymbols(false);
                lineChart.setLegendVisible(false);
                lineChart.setAnimated(false);
                lineChart.setTitle(plotName);
                lineChart.getYAxis().setTickLabelsVisible(false);
                lineChart.getYAxis().setOpacity(0);
                lineChart.setPadding(new Insets(0));

                XYChart.Series series = new XYChart.Series();
                for (int j=0;j<rows;++j)
                    if(data[j][i]!=nullvalue) {
                        series.getData().add(new XYChart.Data(data[j][i], data[j][depthIndex]));
                    }
                lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);

                lineChart.getData().add(series);
                series.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: red;");

                curves.getChildren().add(lineChart);
            }
        }
        Scene scene = new Scene(curves);
        stage.setScene(scene);
        stage.show();
    }

}
