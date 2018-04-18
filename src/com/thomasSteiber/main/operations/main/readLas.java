package com.thomasSteiber.main.operations.main;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

import static com.thomasSteiber.main.operations.main.indexes.*;

public class readLas {

    double data[][] = new double[1][1];
    String[][] header = new String[500][4];
    String curve[][] = new String[1000][4];
    int wellIndex = 0, curveIndex = 0;
    double startValue, stopValue, stepValue, nullValue;
    NumberAxis yAxis = new NumberAxis();
    HBox curves = new HBox();

    public double[][] readFile(File lasFile){
        BufferedReader bufferedReader;
        inner: try {
            bufferedReader = new BufferedReader(new FileReader(lasFile));
            String text;
            boolean Isversion = false, Iswell = false, Iscurve = false, Isother = false, Isdata = false;
            int textInd = 0, dataRowIndex = 0;
            int indexArray[] = {};

            LineChart<Number,Number> lineChartDepth;

            LineChartWithMarkers<Number,Number> lineChartGr = null;
            XYChart.Series grSeries = new XYChart.Series();

            AreaChart<Number,Number> areaChartVshale;
            XYChart.Series vShaleSeries= new XYChart.Series();

            LineChart<Number,Number> lineChartNphi = null;
            XYChart.Series NPhiSeries = new XYChart.Series();

            LineChart<Number,Number> lineChartRhob = null;
            XYChart.Series RhobSeries = new XYChart.Series();

            getIndex ob = new getIndex();

            while ((text = bufferedReader.readLine()) != null) {
                if (text.replaceAll("\\s", "").length()==0 || text.replaceAll("\\s", "").charAt(0)=='#')
                    continue;
                if(text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~V")){
                    Isversion = true; Iswell = false; Iscurve = false; Isother = false; Isdata = false;
                    continue;
                }
                else if(text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~W")){
                    Isversion = false; Iswell = true; Iscurve = false; Isother = false; Isdata = false;
                    continue;
                }
                else if(text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~C")){
                    Isversion = false; Iswell = false; Iscurve = true; Isother = false; Isdata = false;
                    continue;
                }
                else if(text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~A")){
                    Isversion = false; Iswell = false; Iscurve = false; Isother = false; Isdata = true;
                    indexArray = ob.get(curve, curveIndex);
                    if (indexArray[0]==-1) {
                        data[0][0] = -999999;
                        break inner;
                    }
                    data = new double[(int)Math.ceil((stopValue-startValue)/stepValue)+1][totalIndexes];
                    yAxis = new NumberAxis(stopValue, startValue,-100*stepValue);

                    NumberAxis xDepthAxis = new NumberAxis();
                    NumberAxis yDepthAxis = new NumberAxis();
                    yDepthAxis.setLowerBound(stopValue);
                    yDepthAxis.setUpperBound(startValue);
                    lineChartDepth = new LineChart<>(xDepthAxis, yDepthAxis);
                    lineChartDepth.setTitle("Depth");
                    lineChartDepth.getXAxis().setTickLabelsVisible(false);
                    lineChartDepth.getXAxis().setOpacity(0);
                    lineChartDepth.setPrefWidth(30);
                    lineChartDepth.setMaxWidth(70);
                    lineChartDepth.setMinWidth(30);
                    lineChartDepth.setPadding(new Insets(0,0,10,0));
                    curves.getChildren().add(lineChartDepth);

                    NumberAxis xGrAxis = new NumberAxis();
                    lineChartGr = new LineChartWithMarkers<>(xGrAxis,yAxis);
                    lineChartGr.setCreateSymbols(false);
                    lineChartGr.setLegendVisible(false);
                    lineChartGr.setAnimated(false);
                    lineChartGr.setTitle("GR");
                    lineChartGr.getYAxis().setTickLabelsVisible(false);
                    lineChartGr.getYAxis().setOpacity(0);
                    lineChartGr.setPadding(new Insets(0));
                    lineChartGr.getData().add(grSeries);
                    XYChart.Data<Number, Number> horizontalMarker = new XYChart.Data<>(0, 25);
                    lineChartGr.addHorizontalValueMarker(horizontalMarker);
                    Slider horizontalMarkerSlider = new Slider(startValue, stopValue, 0);
                    horizontalMarkerSlider.setOrientation(Orientation.VERTICAL);
                    horizontalMarkerSlider.setShowTickLabels(false);
                    horizontalMarkerSlider.setShowTickMarks(false);
                    horizontalMarkerSlider.setCursor(Cursor.HAND);
                    horizontalMarkerSlider.setTooltip(new Tooltip("Mark boundary of regions"));
                    horizontalMarkerSlider.setRotate(180);
                    horizontalMarkerSlider.valueProperty().bindBidirectional(horizontalMarker.YValueProperty());
                    horizontalMarkerSlider.setPadding(new Insets(30));
                    BorderPane borderPane = new BorderPane(lineChartGr, null, null, null, horizontalMarkerSlider);
                    curves.getChildren().add(borderPane);

                    NumberAxis xVshaleAxis = new NumberAxis();
                    areaChartVshale = new AreaChart<>(xVshaleAxis, yAxis);
                    areaChartVshale.getYAxis().setTickLabelsVisible(false);
                    areaChartVshale.getYAxis().setOpacity(0);
                    areaChartVshale.setCreateSymbols(false);
                    areaChartVshale.setLegendVisible(false);
                    areaChartVshale.setTitle("Vshale");
                    curves.getChildren().add(areaChartVshale);
                    areaChartVshale.getData().add(vShaleSeries);

                    lineChartNphi = linecharts("Nphi");
                    curves.getChildren().add(lineChartNphi);
                    lineChartNphi.getData().add(NPhiSeries);

                    lineChartRhob = linecharts("Rhob");
                    curves.getChildren().add(lineChartRhob);
                    lineChartRhob.getData().add(RhobSeries);

                    continue;
                }
                else if(text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~P") || text.replaceAll("\\s", "").charAt(0)=='~'){
                    Isversion = false; Iswell = false; Iscurve = false; Isother = false; Isdata = false;
                    continue;
                }

                if (Isversion || Isother){}
                else if (Isdata) {
                    if(textInd==curveIndex){
                        textInd = 0;
                        ++dataRowIndex;
                    }

                    text += " ";
                    text = (text.replaceAll("[ ]+", " ")).substring(1);
                    int textindex = 0;

                    while (text.indexOf(" ", textindex) > 0) {
                        int indexOf = text.indexOf(" ", textindex);
                        double value = Double.parseDouble(text.substring(textindex, indexOf));
                        if (textInd == indexArray[ob.getDepthIndex()])
                            data[dataRowIndex][depthIndex] = value;
                        else  if (textInd == indexArray[ob.getGrIndex()]){
                            data[dataRowIndex][grIndex] = value;
                            if (value!=nullValue)
                                grSeries.getData().add(new XYChart.Data(value, data[dataRowIndex][depthIndex]));
                        }
                        else  if (textInd == indexArray[ob.getnPhiIndex()]){
                            data[dataRowIndex][nPhiIndex] = value;
                            if (value!=nullValue)
                                NPhiSeries.getData().add(new XYChart.Data(value, data[dataRowIndex][depthIndex]));
                        }
                        else  if (textInd == indexArray[ob.getRhobIndex()]){
                            data[dataRowIndex][rhobIndex] = value;
                            if (value!=nullValue)
                                RhobSeries.getData().add(new XYChart.Data(value, data[dataRowIndex][depthIndex]));
                        }
                        textindex = indexOf + 1;
                        ++textInd;
                    }
                }
                else if (Iswell){
                    header[wellIndex][0] = text.substring(0,text.indexOf(".")).replaceAll("\\s", "");
                    header[wellIndex][1] = text.substring(text.indexOf(".")+1,text.indexOf(" ", text.indexOf(".")+1));
                    header[wellIndex][2] = text.substring(text.indexOf(" ", text.indexOf(".")+1),text.indexOf(":")).trim();
                    if (header[wellIndex][0].equalsIgnoreCase("STRT"))
                        startValue = Double.parseDouble(header[wellIndex][2]);
                    else if (header[wellIndex][0].equalsIgnoreCase("STOP"))
                        stopValue = Double.parseDouble(header[wellIndex][2]);
                    else if (header[wellIndex][0].equalsIgnoreCase("STEP"))
                        stepValue = Double.parseDouble(header[wellIndex][2]);
                    else if (header[wellIndex][0].equalsIgnoreCase("NULL"))
                        nullValue = Double.parseDouble(header[wellIndex][2]);
                    header[wellIndex++][3] = text.substring(text.indexOf(":")+1).trim();
                }
                else if (Iscurve){
                    curve[curveIndex][0] = text.substring(0,text.indexOf(".")).replaceAll("\\s", "");
                    curve[curveIndex][1] = text.substring(text.indexOf(".")+1,text.indexOf(" ", text.indexOf(".")+1));
                    curve[curveIndex][2] = text.substring(text.indexOf(" ", text.indexOf(".")+1), text.indexOf(":")).trim();
                    curve[curveIndex++][3] = text.substring(text.indexOf(":")+1).trim();
                }
            }

            lineChartGr.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            grSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: red;");
            lineChartNphi.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            NPhiSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: red;");
            lineChartRhob.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            RhobSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: red;");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            data[0][0] = -999999;
        }
        return data;
    }

    public void plot(){
        Stage stage = new Stage();
        Scene scene = new Scene(curves);
        stage.setScene(scene);
        stage.show();
    }

    public HBox losLoad(Stage stage) {

        HBox lasHb = new HBox(10);
        lasHb.setPadding(new Insets(10));

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

        Button loadFile = new Button("Load las");
        loadFile.setPadding(new Insets(10));

        loadFile.setOnAction(e->{
            FileChooser loadlasdirectory = new FileChooser();
            loadlasdirectory.getExtensionFilters().add(new FileChooser.ExtensionFilter("LAS Files", "*.las"));
            loadlasdirectory.setTitle("Load LAS file for thomas steiber");
            File selectedlas =  loadlasdirectory.showOpenDialog(stage);

            if(selectedlas != null){
                double data[][] = readFile(selectedlas);
                if(data[0][0]!=-999999){
                    error.setStyle("-fx-text-fill: green;");
                    error.setText(selectedlas.getName()+" loaded successfully.");
                    sleeper.setOnSucceeded(event-> error.setText(""));
                    new Thread(sleeper).start();
                    plot();
                }
                else{
                    error.setStyle("-fx-text-fill: red;");
                    error.setText("Error reading las file");
                    sleeper.setOnSucceeded(event-> error.setText(""));
                    new Thread(sleeper).start();
                }
            }
        });

        lasHb.getChildren().addAll(loadFile, error);
        return lasHb;
    }

    public LineChart<Number, Number> linecharts(String plotName){
        NumberAxis xAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setTitle(plotName);
        lineChart.getYAxis().setTickLabelsVisible(false);
        lineChart.getYAxis().setOpacity(0);
        lineChart.setPadding(new Insets(0));
        return lineChart;
    }

    private class LineChartWithMarkers<X,Y> extends LineChart {

        private ObservableList<Data<X, Y>> horizontalMarkers;

        public LineChartWithMarkers(NumberAxis xAxis, NumberAxis yAxis) {
            super(xAxis, yAxis);
            horizontalMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.YValueProperty()});
            horizontalMarkers.addListener((InvalidationListener) observable -> layoutPlotChildren());
        }

        public void addHorizontalValueMarker(Data<X, Y> marker) {
            Objects.requireNonNull(marker, "the marker must not be null");
            if (horizontalMarkers.contains(marker)) return;
            Line line = new Line();
            marker.setNode(line );
            getPlotChildren().add(line);
            horizontalMarkers.add(marker);
        }

        public void removeHorizontalValueMarker(Data<X, Y> marker) {
            Objects.requireNonNull(marker, "the marker must not be null");
            if (marker.getNode() != null) {
                getPlotChildren().remove(marker.getNode());
                marker.setNode(null);
            }
            horizontalMarkers.remove(marker);
        }

        @Override
        protected void layoutPlotChildren() {
            super.layoutPlotChildren();
            for (Data<X, Y> horizontalMarker : horizontalMarkers) {
                Line line = (Line) horizontalMarker.getNode();
                line.setStartX(0);
                line.setEndX(getBoundsInLocal().getWidth());
                line.setStartY(getYAxis().getDisplayPosition(horizontalMarker.getYValue()) + 0.5); // 0.5 for crispness
                line.setEndY(line.getStartY());
                line.toFront();
            }
        }
    }
}
