package com.thomasSteiber.main.operations.main;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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

            NumberAxis xDepthAxis = new NumberAxis();
            LineChart<Number,Number> lineChartDepth;

            NumberAxis xGRAxis = new NumberAxis();
            LineChart<Number,Number> lineChartGr = null;
            XYChart.Series grSeries = new XYChart.Series();

            NumberAxis xVshaleAxis = new NumberAxis();
            AreaChart<Number,Number> areaChartVshale = null;
            XYChart.Series vShaleSeries= new XYChart.Series();

            NumberAxis xNPhiAxis = new NumberAxis();
            LineChart<Number,Number> lineChartNphi = null;
            XYChart.Series NPhiSeries = new XYChart.Series();

            NumberAxis xRhobAxis = new NumberAxis();
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
                    yAxis = new NumberAxis(stopValue, startValue,-100);

                    lineChartDepth = new LineChart<>(xDepthAxis, yAxis);
                    lineChartDepth.setTitle("Depth");
                    lineChartDepth.getXAxis().setTickLabelsVisible(false);
                    lineChartDepth.getXAxis().setOpacity(0);
                    lineChartDepth.setPrefWidth(30);
                    lineChartDepth.setMaxWidth(70);
                    lineChartDepth.setMinWidth(30);
                    lineChartDepth.setPadding(new Insets(0,0,10,0));
                    curves.getChildren().add(lineChartDepth);

                    lineChartGr = new LineChart<>(xGRAxis, yAxis);
                    lineChartGr.setCreateSymbols(false);
                    lineChartGr.setLegendVisible(false);
                    lineChartGr.setAnimated(false);
                    lineChartGr.setTitle("GR");
                    lineChartGr.getYAxis().setTickLabelsVisible(false);
                    lineChartGr.getYAxis().setOpacity(0);
                    lineChartGr.setPadding(new Insets(0));
                    curves.getChildren().add(lineChartGr);
                    lineChartGr.getData().add(grSeries);

                    areaChartVshale = new AreaChart<>(xVshaleAxis, yAxis);
                    areaChartVshale.getYAxis().setTickLabelsVisible(false);
                    areaChartVshale.getYAxis().setOpacity(0);
                    areaChartVshale.setCreateSymbols(false);
                    areaChartVshale.setLegendVisible(false);
                    areaChartVshale.setTitle("Vshale");
                    curves.getChildren().add(areaChartVshale);
                    areaChartVshale.getData().add(vShaleSeries);

                    lineChartNphi = new LineChart<>(xNPhiAxis, yAxis);
                    lineChartNphi.setCreateSymbols(false);
                    lineChartNphi.setLegendVisible(false);
                    lineChartNphi.setAnimated(false);
                    lineChartNphi.setTitle("Nphi");
                    lineChartNphi.getYAxis().setTickLabelsVisible(false);
                    lineChartNphi.getYAxis().setOpacity(0);
                    lineChartNphi.setPadding(new Insets(0));
                    curves.getChildren().add(lineChartNphi);
                    lineChartNphi.getData().add(NPhiSeries);

                    lineChartRhob = new LineChart<>(xRhobAxis, yAxis);
                    lineChartRhob.setCreateSymbols(false);
                    lineChartRhob.setLegendVisible(false);
                    lineChartRhob.setAnimated(false);
                    lineChartRhob.setTitle("RHOB");
                    lineChartRhob.getYAxis().setTickLabelsVisible(false);
                    lineChartRhob.getYAxis().setOpacity(0);
                    lineChartRhob.setPadding(new Insets(0));
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
}
