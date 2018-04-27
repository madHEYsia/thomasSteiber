package com.thomasSteiber.main.operations.devagya;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class vshaleCalculation {

    Stage stage = new Stage();
    double[][] data;
    String[][] curve;
    int curveIndex, grIndex;
    double startValue, stopValue, stepValue, nullValue;
    BorderPane layout;
    public void module(){

        layout = new BorderPane(lasLoadButton());
        Scene scene = new Scene(layout);

        stage.setTitle("Vshale Calculation");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        stage.show();
    }

    public HBox lasLoadButton(){

        Button loadLas = new Button("Load LAS");
        loadLas.setPadding(new Insets(10));

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
                    sleeper.setOnSucceeded(event-> error.setText(""));
                    new Thread(sleeper).start();
                    
                }
                else{
                    error.setStyle("-fx-text-fill: red;");
                    error.setText("Error reading las file");
                    sleeper.setOnSucceeded(event-> error.setText(""));
                    new Thread(sleeper).start();
                }
            }
        });

        return new HBox(loadLas, error);
    }


    public void readFile(File lasFile){
        BufferedReader bufferedReader;
        inner: try {

            bufferedReader = new BufferedReader(new FileReader(lasFile));
            String text;
            boolean Isversion = false, Iswell = false, Iscurve = false, Isother = false, Isdata = false;
            int textInd = 0, dataRowIndex = 0;
            data = new double[1][1];
            curve = new String[1000][4];
            curveIndex = 0;

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

                    grIndex = getGRIndex(curve, curveIndex);
                    if (grIndex == -1) {
                        data[0][0] = -999999;
                        break inner;
                    }

                    data = new double[(int) Math.ceil((stopValue - startValue) / stepValue) + 1][2];

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
                        else if (textInd == 1) {
                            data[dataRowIndex][grIndex] = value;
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
                    }
                } else if (Iscurve) {
                    curve[curveIndex][0] = text.substring(0, text.indexOf(".")).replaceAll("\\s", "");
                    curve[curveIndex][1] = text.substring(text.indexOf(".") + 1, text.indexOf(" ", text.indexOf(".") + 1));
                    curve[curveIndex][2] = text.substring(text.indexOf(" ", text.indexOf(".") + 1), text.indexOf(":")).trim();
                    curve[curveIndex++][3] = text.substring(text.indexOf(":") + 1).trim();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            data[0][0] = -999999;
        }
    }

    public int getGRIndex(String curve[][], int curveIndex){
        final int[] grIndex = {-1};

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

        Scene scene = new Scene(new HBox(grLabel, choice));
        Stage stage = new Stage();
        stage.setTitle("Choose GR value");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/images/main_favicon.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        return grIndex[0];
    }
}