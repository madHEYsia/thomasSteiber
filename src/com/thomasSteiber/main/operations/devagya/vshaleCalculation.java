package com.thomasSteiber.main.operations.devagya;

import com.thomasSteiber.main.operations.main.main;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class vshaleCalculation {

    Stage stage = new Stage();
    double[][] data;
    String[][] curve;
    double grInterval[][];
    int curveIndex, grIndex, intervalIndex = 1;
    double startValue, stopValue, stepValue, nullValue;
    Label error = new Label("");
    HBox hb = new HBox(10);
    XYChart.Series[] areaSeries;
    CheckBox linear = new CheckBox("Linear");
    CheckBox larionovTer = new CheckBox("Larionov Tertiary Rocks");
    CheckBox Steiber = new CheckBox("Steiber");
    CheckBox clavier = new CheckBox("Clavier");
    CheckBox larionovOld = new CheckBox("Larionov Older Rocks");

    public void module(){

        linear.setSelected(true);
        larionovTer.setSelected(true);
        Steiber.setSelected(true);
        clavier.setSelected(true);
        larionovOld.setSelected(true);

        HBox checkboxes = new HBox(5, linear, larionovTer, Steiber, clavier, larionovOld);
        checkboxes.setPadding(new Insets(5));
        BorderPane header = new BorderPane(null, null,checkboxes, null,lasLoadButton());

        BorderPane layout = new BorderPane(hb, header, null, null, null);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add(vshaleCalculation.class.getResource("../../resources/css/main.css").toExternalForm());

        stage.setTitle("Vshale Calculation");
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
            grInterval = new double[20][4];
            curveIndex = 0;

            XYChart.Series grSeries = new XYChart.Series();
            LineChartWithMarkers<Number, Number> lineChartGr = null;

            String[] titles = {"Linear", "Larionov Tertiary Rocks", "Steiber", "Clavier","Larionov Older Rocks"};
            areaSeries = new XYChart.Series[titles.length];
            modifiedAreaPlot<Number, Number>[] vshale = new modifiedAreaPlot[titles.length];

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

                    grIndex = getGRIndex(curve);
                    if (grIndex == -1) {
                        data[0][0] = -999999;
                        break inner;
                    }

                    data = new double[(int) Math.ceil((stopValue - startValue) / stepValue) + 1][2];

                    lineChartGr = new LineChartWithMarkers<>(new NumberAxis(), new NumberAxis(stopValue, startValue, -stepValue));
                    lineChartGr.setCreateSymbols(false);
                    lineChartGr.setLegendVisible(false);
                    lineChartGr.setAnimated(false);
                    lineChartGr.setTitle("GR plot");
                    lineChartGr.getYAxis().setTickLabelsVisible(false);
                    lineChartGr.getYAxis().setOpacity(0);
                    lineChartGr.getData().add(grSeries);

                    for (int i = 0; i < titles.length; ++i) {
                        areaSeries[i] = new XYChart.Series();
                        vshale[i] =  new modifiedAreaPlot<>(new NumberAxis(), new NumberAxis(stopValue, startValue, -stepValue), areaSeries[i]);
                        vshale[i].getYAxis().setOpacity(0);
                        vshale[i].getYAxis().setTickLabelsVisible(false);
                        vshale[i].setTitle(titles[i]);
                        vshale[i].setCreateSymbols(false);
                        vshale[i].setLegendVisible(false);
                        vshale[i].getData().add(areaSeries[i]);
                    }

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
                        if (textInd == 0)
                            data[dataRowIndex][0] = value;
                        else if (textInd == grIndex) {
                            data[dataRowIndex][1] = value;
                            if (value!=nullValue) {
                                grSeries.getData().add(new XYChart.Data(value, data[dataRowIndex][0]));
                                if (grInterval[0][2]==nullValue){
                                    grInterval[0][2] = value;
                                    grInterval[0][3] = value;
                                }
                                else {
                                    grInterval[0][2] = Math.min(grInterval[0][2], value);
                                    grInterval[0][3] = Math.max(grInterval[0][3], value);
                                }
                            }
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
                        grInterval[0][2] = nullValue;
                        grInterval[0][3] = nullValue;
                    }
                } else if (Iscurve) {
                    curve[curveIndex][0] = text.substring(0, text.indexOf(".")).replaceAll("\\s", "");
                    curve[curveIndex][1] = text.substring(text.indexOf(".") + 1, text.indexOf(" ", text.indexOf(".") + 1));
                    curve[curveIndex][2] = text.substring(text.indexOf(" ", text.indexOf(".") + 1), text.indexOf(":")).trim();
                    curve[curveIndex++][3] = text.substring(text.indexOf(":") + 1).trim();
                }
            }

            lineChartGr.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
            grSeries.getNode().setStyle("-fx-stroke-width: 1;-fx-stroke: #8bcad9;");

            hb.getChildren().clear();
            hb.getChildren().addAll(lineChartGr);
            updateVshale(0, (int) Math.ceil((stopValue - startValue) / stepValue) + 1, grInterval[0][2], grInterval[0][3]);
            hb.getChildren().addAll(vshale);

            linear.selectedProperty().addListener((ov, old_val, new_val) -> hb.getChildren().get(1).setVisible(new_val));
            larionovTer.selectedProperty().addListener((ov, old_val, new_val) -> hb.getChildren().get(2).setVisible(new_val));
            Steiber.selectedProperty().addListener((ov, old_val, new_val) -> hb.getChildren().get(3).setVisible(new_val));
            clavier.selectedProperty().addListener((ov, old_val, new_val) -> hb.getChildren().get(4).setVisible(new_val));
            larionovOld.selectedProperty().addListener((ov, old_val, new_val) -> hb.getChildren().get(5).setVisible(new_val));

            stage.setFullScreen(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            data[0][0] = -999999;
        }
    }

    public int getGRIndex(String curve[][]){
        Stage stage = new Stage();
        stage.setHeight(150);
        stage.setResizable(false);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        final int[] grIndex = {0};
        String[] index = new String[curveIndex];
        String[] names = new String[curveIndex];
        for(int i=0;i<curveIndex;++i){
            index[i] = i+"";
            names[i] = curve[i][0]+" ("+curve[i][3]+")";
        }

        Label grLabel = new Label("GR Curve :");

        ChoiceBox<String> choice = new ChoiceBox<>(FXCollections.observableArrayList(names));
        choice.setValue(names[0]);
        choice.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
            grIndex[0] = Integer.parseInt(index[new_value.intValue()]);
        });

        Button ok = new Button("Ok");
        ok.setOnAction(e-> stage.close());
        Button cancel = new Button("Cancel");

        cancel.setOnAction(e-> {
            grIndex[0] = -1;
            stage.close();
        });

        layout.setTop(new HBox(10, grLabel, choice));
        layout.setBottom(new HBox(10, ok, cancel));

        Scene scene = new Scene(layout);
        stage.setTitle("Choose GR value");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        return grIndex[0];
    }

    public void updateVshale(int startIndex, int endIndex, double grMin, double grMax){
        for (int i=startIndex; i<endIndex; ++i){
            double Igr = (data[i][1] - grMin)/(grMax - grMin);
            Igr = Igr<=0 ? 0.020 : Igr;
            Igr = Igr>=1 ? 0.999 : Igr;
            double linearVshale = Igr;
            areaSeries[0].getData().add(new XYChart.Data(linearVshale, data[i][0]));
            double larionovTerVshale = 0.083*(Math.pow(2,3.7*Igr) -1);
            areaSeries[1].getData().add(new XYChart.Data(larionovTerVshale, data[i][0]));
            double steiberVshale = Igr/(3-2*Igr);
            areaSeries[2].getData().add(new XYChart.Data(steiberVshale, data[i][0]));
            double clavierVshale = 1.7 - Math.sqrt(3.38 - Math.pow((Igr + 0.7),2));
            areaSeries[3].getData().add(new XYChart.Data(clavierVshale, data[i][0]));
            double larionovOldVshale = 0.33*(Math.pow(2,2*Igr) -1);
            areaSeries[4].getData().add(new XYChart.Data(larionovOldVshale, data[i][0]));
        }
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