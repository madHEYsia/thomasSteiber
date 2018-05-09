import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class getPorWindow1Index {

    int tvdIndex = 0;
    int grIndex = 1;
    int nPhiIndex = 2;
    int rhobIndex = 3;
    int rxoIndex = 4;
    int MRESHIndex = 5;
    int SRESHIndex = 6;
    int DRESHIndex = 7;
    int flaglocationIndex = 8;
    int flagTempIndex = 9;
    int flagMudIndex = 10;
    int KBIndex = 11;
    int WDIndex = 12;
    int TGRADIndex = 13;
    int TSIndex = 14;
    int TFORIndex = 15;
    int DBMLTFORIndex = 16;
    int TempRwIndex = 17;
    int TempRmfIndex = 18;
    int RwIndex = 19;
    int RmfIndex = 20;

    String[] curvesRequired = {
            "True Vertical Depth:",
            "GR: ",
            "Nphi: ",
            "Rhob: ",
            "Rxo: ",
            "MRESH: ",
            "SRESH : ",
            "DRESH: ",
    };
    String[][] flags ={
            {"Default Offshore","1","Other"},
            {"Temperature in ","Fahrenheit","Celsius"},
            {"Mud", "Water based mud", "Oil based mud"},
    };
    String[][] inputRequired = {
            {"KB from surface(MSL) (log-unit)","25"},
            {"Water Depth plus KB (log-unit) ","132"},
            {"Tempertaure Gradient [DEGF/100(log-unit)]","1"},
            {"Sea Floor Temp [DEGF]","70"},
            {"Formation Temp [DEGF]","0"},
            {"Depth at formation Temperature (log-unit)","0"},
            {"Temperature Rw [DEGF]","75.2"},
            {"Temperature Rmudf [DEGF]","60.8"},
            {"Water Resistivity [OHMM]","0.7"},
            {"Mudfiltrate Resistivity [OHMM]","0.16"},
    };
    String[] output = new String[curvesRequired.length+flags.length+inputRequired.length];

    public String[] get(String[][] curves, int curveIndex){
        Stage stage = new Stage();

        VBox layout = new VBox(5);
        String[] index = new String[curveIndex+1];
        String[] names = new String[curveIndex+1];
        index[0] = "0";
        names[0] = "Select parameter (Description)";
        for(int i=1;i<=curveIndex;++i){
            index[i] = i+"";
            names[i] = curves[i-1][0]+" ("+curves[i-1][3]+")";
        }

        GridPane grids = new GridPane();
        grids.setPadding(new Insets(10));
        grids.setHgap(15);
        grids.setVgap(10);
        int rowIndex = 0, columnIndex = 0, inputsInRows = 3;
        for(int i=0;i<curvesRequired.length+flags.length+inputRequired.length;++i){
            int finalI = i;
            if (i<curvesRequired.length) {
                Label curve_i = new Label(curvesRequired[i]);
                ChoiceBox<String> choice_i = new ChoiceBox<>(FXCollections.observableArrayList(names));
                choice_i.setMaxWidth(200);
                choice_i.setValue(names[0]);
                output[finalI] = index[0];
                choice_i.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> output[finalI] = index[new_value.intValue()]);
                grids.add(new HBox(5,curve_i, choice_i), columnIndex, rowIndex, 5, 1);
            }
            else if (i<curvesRequired.length+flags.length){
                final int flagI = i-curvesRequired.length;
                Label flag_i = new Label(flags[flagI][0]);
                String flagVariables[] = new String[flags[flagI].length-1];
                for (int i1=0;i1<flagVariables.length;++i1)
                    flagVariables[i1] = flags[flagI][i1+1];
                ChoiceBox<String> choice_i = new ChoiceBox<>(FXCollections.observableArrayList(flagVariables));
                choice_i.setMaxWidth(200);
                choice_i.setValue(flags[flagI][1]);
                output[finalI] = flags[flagI][1];
                choice_i.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> output[finalI] = flagVariables[new_value.intValue()]);
                grids.add(new HBox(5,flag_i, choice_i), columnIndex, rowIndex, 5, 1);
            }
            else{
                final int TextI = i-curvesRequired.length-flags.length;
                Label inputLabel = new Label(inputRequired[TextI][0]);
                output[i] = inputRequired[TextI][1];
                TextField value = new TextField(inputRequired[TextI][1] + "");
                value.setMaxWidth(200);
                value.textProperty().addListener((observable, oldValue, newValue) -> output[finalI] = newValue);
                grids.add(new HBox(5, inputLabel, value), columnIndex, rowIndex, 5, 1);
            }
            columnIndex+=5;
            if ((i+1)%inputsInRows==0){
                columnIndex = 0;
                ++rowIndex;
            }
        }

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

        Button proceed = new Button("Proceed");
        proceed.setOnAction(e-> {
            boolean errorFound = false;
            inner: for(int i=0;i<curvesRequired.length+flags.length+inputRequired.length;++i) {
                if (i < curvesRequired.length) {
                    if ((output[i] == null || output[i].equals("0")) &&
                            i != nPhiIndex &&
                            (i != SRESHIndex || output[MRESHIndex].equals("0")) &&
                            (i != MRESHIndex || output[SRESHIndex].equals("0")) &&
                            i != DRESHIndex &&
                            (i != rxoIndex || output[flagMudIndex].equals("Water based mud"))) {
                        errorFound = true;
                        error.setText("Please select parameter " + curvesRequired[i]);
                        sleeper.setOnSucceeded(event -> error.setText(""));
                        new Thread(sleeper).start();
                        break inner;
                    }
                }
                else if (i>=flags.length){
                    final int TextI = i-curvesRequired.length-flags.length;
                    if (output[i] == null) {
                        errorFound = true;
                        error.setText(inputRequired[TextI][0] + " cannot be empty");
                        sleeper.setOnSucceeded(event -> error.setText(""));
                        new Thread(sleeper).start();
                        break inner;
                    }
                }
            }
            if (!errorFound) {
                for (int i = 0; i < curvesRequired.length; ++i) {
                    if (!output[i].equals("0"))
                        output[i] = (Integer.parseInt(output[i]) - 1) + "";
                    else
                        output[i] = null;
                }
                stage.close();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e-> {
            output[0] = null;
            stage.close();
        });

        stage.setOnCloseRequest(e-> output[0] = null);

        HBox bottomButton = new HBox(10, proceed, cancel);
        bottomButton.setAlignment(Pos.BASELINE_CENTER);

        layout.getChildren().addAll(grids, error, bottomButton);

        Scene scene = new Scene(layout, 1000,300);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
        return output;
    }
}
