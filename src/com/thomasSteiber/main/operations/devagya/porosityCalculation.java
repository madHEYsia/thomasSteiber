package com.thomasSteiber.main.operations.devagya;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

import static com.thomasSteiber.main.operations.devagya.indexes.*;

public class porosityCalculation {

    Stage stage = new Stage();
    double[][] data;
    String[][] curve;
    int curveIndex, dataSize;
    double grMin, grMax, startValue, stopValue, stepValue, nullValue;
    Label error = new Label("");
    HBox hb = new HBox(10);
    boolean isNphiPresent;
    XYChart.Series areaSeries ;
    NumberAxis yAxis = new NumberAxis();
    String title = "";

    public void module(String vshale){
        title = vshale;
        BorderPane layout = new BorderPane(hb, lasLoadButton(), null, null, null);
        layout.widthProperty().addListener(e-> {
            hb.setPrefWidth(layout.getPrefWidth());
            hb.setMinWidth(layout.getMinWidth());
            hb.setMaxWidth(layout.getMaxWidth());
        });
        Scene scene = new Scene(layout,600,400);
        scene.getStylesheets().add(vshaleCalculation.class.getResource("../../resources/css/plot.css").toExternalForm());

        stage.setTitle("Porosity Calculation");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        stage.show();
    }

    public HBox lasLoadButton(){

        Button loadLas = new Button("Load LAS");
        loadLas.setPadding(new Insets(5));

        error.setFont(new Font("Arial", 11));
        error.setStyle("-fx-text-fill: #6ab25f;");
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

        loadLas.setOnAction(e->{
            FileChooser loadlasdirectory = new FileChooser();
            loadlasdirectory.getExtensionFilters().add(new FileChooser.ExtensionFilter("LAS Files", "*.las"));
            loadlasdirectory.setTitle("Load LAS file for Vshale Calculation");
            File selectedlas =  loadlasdirectory.showOpenDialog(stage);

            if(selectedlas != null){
                readFile(selectedlas);
                if(data[0][0]!=-999999){
                    error.setStyle("-fx-text-fill: green;");
                    error.setText(selectedlas.getName()+" loaded successfully.");
                }
                else{
                    error.setStyle("-fx-text-fill: red;");
                    error.setText("Error reading las file");
                    sleeper.setOnSucceeded(event-> error.setText(""));
                    new Thread(sleeper).start();
                }
            }
        });

        HBox hb = new HBox(10, loadLas, error);
        hb.setPadding(new Insets(10));
        return hb;
    }


    public void readFile(File lasFile){
        error.setStyle("-fx-text-fill: blue;");
        error.setText("Loading "+lasFile.getName()+"......");

        BufferedReader bufferedReader;
        inner: try {

            bufferedReader = new BufferedReader(new FileReader(lasFile));
            String text;
            boolean Isversion = false, Iswell = false, Iscurve = false, Isother = false, Isdata = false;
            int textInd = 0, dataRowIndex = 0;
            data = new double[1][1];
            curve = new String[1000][4];
            int indexArray[] = {};
            curveIndex = 0;
            isNphiPresent = true;
            double avgShaleDensity = 0.0;

            XYChart.Series grSeries = new XYChart.Series();
            LineChartWithMarkers<Number, Number> lineChartGr = null;

            areaSeries = new XYChart.Series();
            modifiedAreaPlot<Number, Number> areaChartVshale = null;

            LineChart<Number, Number> lineChartNphi = null;
            XYChart.Series NPhiSeries = new XYChart.Series();

            LineChart<Number, Number> lineChartRhob = null;
            XYChart.Series RhobSeries = new XYChart.Series();

            LineChart<Number, Number> lineChartRes = null;
            XYChart.Series ResSeries = new XYChart.Series();

            LineChart<Number, Number> lineChartphi = null;
            XYChart.Series phiESeries = new XYChart.Series();
            XYChart.Series phiTSeries = new XYChart.Series();

            LineChart<Number, Number> lineChartSw = null;
            XYChart.Series sWSeries = new XYChart.Series();

            getIndex ob = new getIndex();

            while ((text = bufferedReader.readLine()) != null) {
                if (text.replaceAll("\\s", "").length() == 0 || text.replaceAll("\\s", "").charAt(0) == '#')
                    continue;
                if (text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~V")) {
                    Isversion = true;
                    Iswell = false;
                    Iscurve = false;
                    Isother = false;
                    Isdata = false;
                    continue;
                } else if (text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~W")) {
                    Isversion = false;
                    Iswell = true;
                    Iscurve = false;
                    Isother = false;
                    Isdata = false;
                    continue;
                } else if (text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~C")) {
                    Isversion = false;
                    Iswell = false;
                    Iscurve = true;
                    Isother = false;
                    Isdata = false;
                    continue;
                } else if (text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~A")) {
                    Isversion = false;
                    Iswell = false;
                    Iscurve = false;
                    Isother = false;
                    Isdata = true;

                    indexArray = ob.get(curve, curveIndex);
                    if (indexArray[0] == -1) {
                        data[0][0] = -999999;
                        break inner;
                    }

                    if (indexArray[ob.getnPhiIndex()] == -1)
                        isNphiPresent = false;

                    avgShaleDensity = 0.5*(ob.values[ob.getShaleDensityLowerIndex()]+ob.values[ob.getShaleDensityUpperIndex()]);
                    dataSize = (int) Math.ceil((stopValue - startValue) / stepValue) + 1;
                    data = new double[dataSize][totalIndexes];
                    yAxis = new NumberAxis(stopValue, startValue, -100 * stepValue);

                    lineChartGr = new LineChartWithMarkers<>(new NumberAxis(), yAxis);
                    lineChartGr.setCreateSymbols(false);
                    lineChartGr.setLegendVisible(false);
                    lineChartGr.setAnimated(false);
                    lineChartGr.setTitle("GR plot");
                    lineChartGr.getYAxis().setTickLabelsVisible(false);
                    lineChartGr.getYAxis().setOpacity(0);
                    lineChartGr.setPadding(new Insets(0));
                    lineChartGr.getData().add(grSeries);

                    final double[] yValue = {0.0, 0.0};
                    MenuItem startDepth = new MenuItem("Add start of range");
                    MenuItem endDepth = new MenuItem("Add end of range");
                    endDepth.setDisable(true);
                    LineChartWithMarkers<Number, Number> finalLineChartGr = lineChartGr;

                    startDepth.setOnAction(e->{
                        startDepth.setDisable(true);
                        endDepth.setDisable(false);
                        Data<Number, Number> horizontalMarker = new Data<>(0, yValue[0]);
                        finalLineChartGr.addHorizontalValueMarker(horizontalMarker);
                    });

                    LineChart<Number, Number> finalLineChartSw = lineChartSw;
                    LineChart<Number, Number> finalLineChartphi = lineChartphi;
                    double finalAvgShaleDensity = avgShaleDensity;
                    endDepth.setOnAction(e-> {
                        startDepth.setDisable(false);
                        endDepth.setDisable(true);
                        Data<Number, Number> horizontalMarker = new Data<>(0, yValue[1]);
                        finalLineChartGr.addHorizontalValueMarker(horizontalMarker);
                        double results[] = getGRRange(yValue);
                        int startIndex = Integer.parseInt(((int)results[0])+""),
                                endIndex = Integer.parseInt(((int)results[1])+""),
                                grMinIndex = Integer.parseInt(((int)results[2])+""),
                                grMaxIndex = Integer.parseInt(((int)results[3])+"");
                        if (startIndex!=-1) {
                            error.setStyle("-fx-text-fill: blue;");
                            error.setText("Updated Vshale from "+results[0]+" to "+results[1]);

                            int matrixDensityIndex = 0;
                            int phiDIndex = 1;
                            int phiDXIndex = 2;
                            int phiDCIndex = 3;
                            int phiNCIndex = 4;
                            int phiOIndex = 5;
                            double[][] calculatedValues = new double[data.length][6];
                            for (int i = startIndex; i < endIndex; ++i) {
                                double Igr = (data[i][1] - grMin)/(grMax - grMin);
                                Igr = Igr<=0 ? 0.020 : Igr;
                                Igr = Igr>=1 ? 0.999 : Igr;
                                double vshale = Igr;
                                if (title.equals("Larionov Tertiary Rocks"))
                                    vshale = 0.083*(Math.pow(2,3.7*Igr) -1);
                                else if (title.equals("Steiber"))
                                    vshale = Igr/(3-2*Igr);
                                else if (title.equals("Clavier"))
                                    vshale = 1.7 - Math.sqrt(3.38 - Math.pow((Igr + 0.7),2));
                                else if (title.equals("Larionov Older Rocks"))
                                    vshale = 0.33*(Math.pow(2,2*Igr) -1);
                                areaSeries.getData().add(new Data(vshale, data[i][depthIndex]));

                                double currentShaleDensity = finalAvgShaleDensity;
                                innerWhile: while (true){
                                    calculatedValues[i][matrixDensityIndex] = nullValue;
                                    calculatedValues[i][phiDIndex] = nullValue;
                                    calculatedValues[i][phiDXIndex] = nullValue;
                                    calculatedValues[i][phiDCIndex] = nullValue;
                                    calculatedValues[i][phiNCIndex] = nullValue;
                                    if (data[i][vshaleIndex] != nullValue) {
                                        calculatedValues[i][matrixDensityIndex] = data[i][vshaleIndex] * currentShaleDensity +
                                                (1 - data[i][vshaleIndex]) * ob.values[ob.getSandDensityIndex()];
                                        if (isNphiPresent && data[i][nPhiIndex] != nullValue)
                                            calculatedValues[i][phiNCIndex] = data[i][nPhiIndex] - data[i][vshaleIndex] * data[grMaxIndex][nPhiIndex];
                                        if (data[i][rhobIndex] != nullValue) {
                                            calculatedValues[i][matrixDensityIndex] = calculatedValues[i][matrixDensityIndex] < data[i][rhobIndex]
                                                    ? calculatedValues[i][matrixDensityIndex] + 0.005 : calculatedValues[i][matrixDensityIndex];
                                            calculatedValues[i][phiDIndex] = (calculatedValues[i][matrixDensityIndex] - data[i][rhobIndex]) /
                                                    (calculatedValues[i][matrixDensityIndex] - ob.values[ob.getFluidDensityIndex()]);
                                            calculatedValues[i][phiDXIndex] = (calculatedValues[i][matrixDensityIndex] - data[i][rhobIndex]) /
                                                    (calculatedValues[i][matrixDensityIndex] - ob.values[ob.getMudFiltrateDensityIndex()]);
                                            calculatedValues[i][phiDCIndex] = calculatedValues[i][phiDXIndex] - data[i][vshaleIndex] *
                                                    ((currentShaleDensity - data[i][rhobIndex]) / (currentShaleDensity - 1));
                                        }
                                    }
                                    if (!isNphiPresent || data[i][nPhiIndex] == nullValue || (calculatedValues[i][phiNCIndex] != nullValue && calculatedValues[i][phiNCIndex] > 0.4))
                                        calculatedValues[i][phiOIndex] = calculatedValues[i][phiDXIndex];
                                    if (isNphiPresent && data[i][nPhiIndex] < 0.4) {
                                        if (calculatedValues[i][phiDCIndex] != nullValue && calculatedValues[i][phiNCIndex] == nullValue)
                                            calculatedValues[i][phiOIndex] = calculatedValues[i][phiDCIndex];
                                        else if (calculatedValues[i][phiDCIndex] == nullValue || calculatedValues[i][phiDCIndex] > 0.4)
                                            calculatedValues[i][phiOIndex] = calculatedValues[i][phiNCIndex];
                                        else if (calculatedValues[i][phiDCIndex] != nullValue && calculatedValues[i][phiNCIndex] != nullValue)
                                            calculatedValues[i][phiOIndex] = Math.sqrt((Math.pow(calculatedValues[i][phiDCIndex], 2) + Math.pow(calculatedValues[i][phiNCIndex], 2)) / 2);
                                        else
                                            calculatedValues[i][phiOIndex] = nullValue;
                                    }
                                    if (calculatedValues[i][phiOIndex] != nullValue) {
                                        calculatedValues[i][phiOIndex] = calculatedValues[i][phiOIndex] <= 0 ? 0.001 : calculatedValues[i][phiOIndex];
                                        if (calculatedValues[i][phiOIndex] <= 0.47) {
                                            System.out.println("**depth: " + data[i][depthIndex] + " grMin: " + data[grMinIndex][grIndex] + " grMax: " + data[grMaxIndex][grIndex] + " Vsh: " + data[i][vshaleIndex] + " matrixDenity: " + calculatedValues[i][matrixDensityIndex] + " phiDX: " + calculatedValues[i][phiDXIndex] + " phiDC: " + calculatedValues[i][phiDCIndex] + " phiNC: " + calculatedValues[i][phiNCIndex] + " ShaleDensity: " + currentShaleDensity + " phio: " + calculatedValues[i][phiOIndex]);
                                            break innerWhile;
                                        }
                                        if (calculatedValues[i][phiOIndex] > 0.47)
                                            currentShaleDensity = currentShaleDensity - 0.05;
                                        if (currentShaleDensity < ob.values[ob.getShaleDensityLowerIndex()]) {
                                            System.out.println("depth: " + data[i][depthIndex] + " grMin: " + data[grMinIndex][grIndex] + " grMax: " + data[grMaxIndex][grIndex] + " Vsh: " + data[i][vshaleIndex] + " matrixDenity: " + calculatedValues[i][matrixDensityIndex] + " phiDX: " + calculatedValues[i][phiDXIndex] + " phiDC: " + calculatedValues[i][phiDCIndex] + " phiNC: " + calculatedValues[i][phiNCIndex] + " ShaleDensity: " + currentShaleDensity + " phio: " + calculatedValues[i][phiOIndex]);
//                                            calculatedValues[i][matrixDensityIndex] = nullValue;
//                                            calculatedValues[i][phiDIndex] = nullValue;
//                                            calculatedValues[i][phiDXIndex] = nullValue;
//                                            calculatedValues[i][phiDCIndex] = nullValue;
//                                            calculatedValues[i][phiNCIndex] = nullValue;
//                                            calculatedValues[i][phiOIndex] = nullValue;
                                            break innerWhile;
                                        }
                                    }
                                    else
                                        break innerWhile;
                                }
                            }

                            finalLineChartphi.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
                            phiESeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: red;");
                            phiTSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: black;");
                            finalLineChartSw.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
                            sWSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: pink;");
                        }
                        else {
                            error.setStyle("-fx-text-fill: red;");
                            error.setText("Error in updating Vshale");
                        }
                    });

                    ContextMenu grMenus = new ContextMenu();
                    grMenus.getItems().addAll(startDepth, endDepth);
                    lineChartGr.setOnContextMenuRequested(e -> {
                        grMenus.show(finalLineChartGr, e.getScreenX(), e.getScreenY());
                        if (startDepth.isDisable())
                            yValue[1] = (double) finalLineChartGr.getYAxis().getValueForDisplay(e.getY()-38);
                        else
                            yValue[0] = (double) finalLineChartGr.getYAxis().getValueForDisplay(e.getY()-38);
                    });

                    areaChartVshale = new modifiedAreaPlot<>(new NumberAxis(0, 1, 0.1), yAxis, areaSeries);
                    areaChartVshale.getYAxis().setOpacity(0);
                    areaChartVshale.getYAxis().setTickLabelsVisible(false);
                    areaChartVshale.setTitle("Vshale ("+title+")");
                    areaChartVshale.setCreateSymbols(false);
                    areaChartVshale.setLegendVisible(false);
                    areaChartVshale.setPadding(new Insets(0));
                    areaChartVshale.getData().add(areaSeries);

                    lineChartNphi = linecharts("Nphi");
                    lineChartNphi.getData().add(NPhiSeries);

                    lineChartRhob = linecharts("Rhob");
                    lineChartRhob.getData().add(RhobSeries);

                    lineChartRes = linecharts("Resistivity");
                    lineChartRes.getData().add(ResSeries);

                    lineChartphi = linecharts("PhiE and PhiT");
                    lineChartphi.getData().addAll(phiESeries, phiTSeries);

                    lineChartSw = linecharts("Saturation");
                    lineChartSw.getData().add(sWSeries);

                    continue;
                } else if (text.replaceAll("\\s", "").substring(0, 2).equalsIgnoreCase("~P") || text.replaceAll("\\s", "").charAt(0) == '~') {
                    Isversion = false;
                    Iswell = false;
                    Iscurve = false;
                    Isother = false;
                    Isdata = false;
                    continue;
                }

                if (Isversion || Isother) {
                } else if (Isdata) {
                    if (textInd == curveIndex) {
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
                        else if (textInd == indexArray[ob.getGrIndex()]) {
                            data[dataRowIndex][grIndex] = value;
                            if (value != nullValue) {
                                grSeries.getData().add(new Data(value, data[dataRowIndex][depthIndex]));
                                if (grMin==nullValue){
                                    grMin = value;
                                    grMax = value;
                                }
                                else {
                                    grMin = Math.min(grMin, value);
                                    grMax = Math.max(grMax, value);
                                }
                            } else
                                data[dataRowIndex][vshaleIndex] = nullValue;
                        } else if (textInd == indexArray[ob.getnPhiIndex()]) {
                            data[dataRowIndex][nPhiIndex] = value;
                            if (value != nullValue)
                                NPhiSeries.getData().add(new Data(value, data[dataRowIndex][depthIndex]));
                        } else if (textInd == indexArray[ob.getRhobIndex()]) {
                            data[dataRowIndex][rhobIndex] = value;
                            if (value != nullValue) {
                                RhobSeries.getData().add(new Data(value, data[dataRowIndex][depthIndex]));
                                if (avgShaleDensity < value)
                                    avgShaleDensity = value + 0.005;
                            }
                        } else if (textInd == indexArray[ob.getResIndex()]) {
                            data[dataRowIndex][resIndex] = value;
                            if (value != nullValue)
                                ResSeries.getData().add(new Data(value, data[dataRowIndex][depthIndex]));
                        }
                        textindex = indexOf + 1;
                        ++textInd;
                    }
                } else if (Iswell) {
                    String wellTitle = text.substring(0, text.indexOf(".")).replaceAll("\\s", "");
                    String wellValue = text.substring(text.indexOf(" ", text.indexOf(".") + 1), text.indexOf(":")).trim();
                    if (wellTitle.equalsIgnoreCase("STRT"))
                        startValue = Double.parseDouble(wellValue);
                    else if (wellTitle.equalsIgnoreCase("STOP"))
                        stopValue = Double.parseDouble(wellValue);
                    else if (wellTitle.equalsIgnoreCase("STEP"))
                        stepValue = Double.parseDouble(wellValue);
                    else if (wellTitle.equalsIgnoreCase("NULL")) {
                        nullValue = Double.parseDouble(wellValue);
                        grMin = nullValue;
                        grMax = nullValue;
                    }
                } else if (Iscurve) {
                    curve[curveIndex][0] = text.substring(0, text.indexOf(".")).replaceAll("\\s", "");
                    curve[curveIndex][1] = text.substring(text.indexOf(".") + 1, text.indexOf(" ", text.indexOf(".") + 1));
                    curve[curveIndex][2] = text.substring(text.indexOf(" ", text.indexOf(".") + 1), text.indexOf(":")).trim();
                    curve[curveIndex++][3] = text.substring(text.indexOf(":") + 1).trim();
                }
            }

            lineChartGr.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            grSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: #6ab25f;");
            lineChartNphi.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            NPhiSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: red;");
            lineChartRhob.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            RhobSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: blue;");
            lineChartRes.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            ResSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: pink;");

            stage.setMaximized(true);
            hb.getChildren().clear();
            hb.getChildren().addAll(lineChartGr, areaChartVshale, lineChartNphi, lineChartRhob, lineChartRes, lineChartphi, lineChartSw);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            data[0][0] = -999999;
        }
    }

    public double[] getGRRange(double depth[]){
        double[] results = new double[4];

        Stage vrStage = new Stage();

        int numberOfGroups = 100;
        int GRUpperLimit = 400;
        int group[] = new int[numberOfGroups];
        double[][] grMinMax = new double[numberOfGroups][4];
        for (int i=0;i<numberOfGroups;++i) {
            grMinMax[i][1] = Integer.MAX_VALUE;
            grMinMax[i][3] = Integer.MIN_VALUE;
        }
        inner: for(int i=0; i<dataSize; i++){
            if(data[i][depthIndex]<depth[0])
                results[0] = i;
            else if (data[i][depthIndex]<depth[1])
                results[1] = i;
            if(data[i][depthIndex]>=depth[0] && data[i][depthIndex]<=depth[1]) {
                int groupNumber = (int) (data[i][grIndex] / (GRUpperLimit / numberOfGroups)) - 1;
                group[groupNumber]++;
                if (grMinMax[groupNumber][1]>data[i][grIndex]){
                    grMinMax[groupNumber][0] = i;
                    grMinMax[groupNumber][1] = data[i][grIndex];
                }
                if (grMinMax[groupNumber][3]<data[i][grIndex]){
                    grMinMax[groupNumber][2] = i;
                    grMinMax[groupNumber][3] = data[i][grIndex];
                }
            }
            else if(data[i][depthIndex]>depth[1])
                break inner;
        }

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart = new BarChart<>(xAxis,yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        xAxis.setLabel(" GR values ");
        yAxis.setLabel("frequency");
        barChart.setCategoryGap(0);
        barChart.setBarGap(0);

        XYChart.Series series1 = new XYChart.Series();
        for (int i=0;i<numberOfGroups;++i)
            series1.getData().add(new Data(i*GRUpperLimit/numberOfGroups+"-"+(i+1)*GRUpperLimit/numberOfGroups, group[i]));

        barChart.getData().addAll(series1);
        final boolean[] firstSelected = {false};
        final Glow glow = new Glow(.8);
        for (XYChart.Series<String,Number> serie: barChart.getData()){
            for (XYChart.Data<String, Number> item: serie.getData()){
                final Node n = item.getNode();
                n.setCursor(Cursor.HAND);
                n.setEffect(null);
                n.setOnMouseEntered(e -> n.setEffect(glow));
                n.setOnMouseExited(e -> n.setEffect(null));
                n.setOnMouseClicked(e -> {
                    n.setEffect(glow);
                    String[] range = item.getXValue().split("-");
                    int groupNumber = (int) (Double.parseDouble(range[0])*numberOfGroups/GRUpperLimit);
                    n.setStyle("-fx-bar-fill: red");
                    if (!firstSelected[0]){
                        firstSelected[0] = true;
                        results[2] = grMinMax[groupNumber][0];
                    }
                    else{
                        results[3] = grMinMax[groupNumber][2];
                        vrStage.close();
                    }
                });
                item.getNode().setOnMousePressed((MouseEvent event) -> {
                    item.getNode().setEffect(glow);
                    System.out.println("you clicked "+item.toString()+serie.toString());
                });
            }
        }
        barChart.getData().addAll(series1);

        BorderPane layout = new BorderPane(barChart);
        Scene scene = new Scene(layout, 800, 450);

        vrStage.setTitle("GR Max-Min picking");
        vrStage.setScene(scene);
        vrStage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        vrStage.initModality(Modality.APPLICATION_MODAL);
        vrStage.showAndWait();

        return results;
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
}