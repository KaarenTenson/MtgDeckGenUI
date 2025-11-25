package org.tts.ttsdeckgen;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class HelloApplication extends Application {

    static Text tehtudtekst = new Text("");
    static DoubleProperty doneProp = new SimpleDoubleProperty();
    static Thread current;
    static Scene GlobalScene;
    static ArrayList<Pane> notEasyPanes = new ArrayList<>();
    @Override
    public void start(Stage stage) throws Exception {
        // Create the main layout container (VBox)
        tehtudtekst.getStyleClass().add("labels");

        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(15));

        // Deck Size
        HBox deckSizeBox = createLabeledTextField("Deck Size:", "suurus");

        // Random Check
        HBox randomCheckBox = createCheckBox("Random:", "RandomCheck");

        // Land Count
        HBox landCountBox = createLabeledTextField("Land Count:", "landcountfield");
        notEasyPanes.add(landCountBox);

        // Colors Box

        HBox newColorBox=createColorCheckBoxes();
        notEasyPanes.add(newColorBox);

        // Nonbasic Land Percentage
        HBox nonBasicLandBox = createLabeledTextField("Nonbasic Land %:", "Protsent");
        notEasyPanes.add(nonBasicLandBox);

        // URL Inputs
        HBox urlBox = createLabeledTextField("URL Nonland:", "url");
        notEasyPanes.add(urlBox);
        HBox landUrlBox = createLabeledTextField("URL Land:", "Landurl");
        notEasyPanes.add(landUrlBox);

        // Progress Bar
        HBox progressBox = new HBox();
        ProgressBar prog = new ProgressBar(0);
        progressBox.getChildren().add(prog);

        // Action Buttons
        HBox buttonBox = createButtonBox(prog);

        // Add all elements to root container
        root.getChildren().addAll(deckSizeBox, randomCheckBox, landCountBox, newColorBox,nonBasicLandBox, urlBox, landUrlBox, progressBox, buttonBox, tehtudtekst);

        // Create the Scene
        Scene scene = new Scene(root, 400, 450);
        GlobalScene=scene;
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        // Set up Stage
        stage.setTitle("Deck Generator");
        stage.setScene(scene);
        stage.show();
    }
    private HBox createColorCheckBoxes(){
        HBox box = new HBox();
        CheckBox gCheck=new CheckBox("");
        gCheck.getStyleClass().add("green");
        gCheck.setId("green");
        CheckBox bCheck=new CheckBox("");
        bCheck.getStyleClass().add("black");
        bCheck.setId("black");
        CheckBox wCheck=new CheckBox("");
        wCheck.getStyleClass().add("white");
        wCheck.setId("white");
        CheckBox uCheck=new CheckBox("");
        uCheck.getStyleClass().add("blue");
        uCheck.setId("blue");
        CheckBox rCheck=new CheckBox("");
        rCheck.getStyleClass().add("red");
        rCheck.setId("red");
        box.getChildren().addAll(gCheck,bCheck,wCheck,uCheck,rCheck);
        return box;
    }
    private HBox createLabeledTextField(String label, String id) {
        HBox box = new HBox(10);
        Text labelText = new Text(label);
        labelText.getStyleClass().add("labels");
        javafx.scene.control.TextField textField = new javafx.scene.control.TextField();
        textField.setId(id);
        box.getChildren().addAll(labelText, textField);
        return box;
    }

    private HBox createCheckBox(String label, String id) {
        HBox box = new HBox(10);
        Text labelText = new Text(label);
        labelText.getStyleClass().add("labels");
        CheckBox checkBox = new CheckBox();
        checkBox.getStyleClass().add("easy-check-box");
        checkBox.setId(id);
        box.getChildren().addAll(labelText, checkBox);
        checkBox.setOnAction(getCheckBoxAction());
        return box;
    }
    EventHandler<ActionEvent> getCheckBoxAction() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (event.getSource() instanceof CheckBox) {
                    CheckBox chk = (CheckBox) event.getSource();
                    if(chk.isSelected()) {
                        for (Pane pane : notEasyPanes) {
                            pane.setVisible(false);
                        }
                    }else{
                        for (Pane pane : notEasyPanes) {
                            pane.setVisible(true);
                        }
                    }
                }
            }
        };

    }

    private HBox createButtonBox(ProgressBar prog) {
        HBox box = new HBox(15);
        javafx.scene.control.Button requestButton = new javafx.scene.control.Button("Request");
        javafx.scene.control.Button stopButton = new javafx.scene.control.Button("Stop");

        // Event Handlers for buttons
        requestButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (current == null || !current.isAlive()) {
                    tehtudtekst.setText("");
                    doneProp.setValue(1);
                    requestData reData;
                    String color=(getCheckBoxValue("black") ? "B" : "")+
                            (getCheckBoxValue("white") ? "W": "") +
                            (getCheckBoxValue("green") ? "G" : "")+
                            (getCheckBoxValue("blue") ? "U" : "") +
                            (getCheckBoxValue("red") ? "R" : "");
                    try{
                    reData=new requestData(
                            getTextFieldValue("suurus"),
                            getCheckBoxValue("RandomCheck"),
                            color,
                            getTextFieldValue("landcountfield"),
                            getTextFieldValue("Protsent"),
                            getTextFieldValue("url"),
                            getTextFieldValue("Landurl")
                            );
                    }catch (parseRequestError ex){
                        System.out.println("error");
                        tehtudtekst.setText(ex.getMessage());
                        return;
                    }
                    prog.progressProperty().bind(doneProp.divide(reData.deckSize));
                    Main thread = new Main(reData, doneProp);
                    current = thread;
                    thread.start();
                }
            }
        });

        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                doneProp.setValue(0);
                if (current.isAlive()) {
                    current.interrupt();
                }
            }
        });

        box.getChildren().addAll(requestButton, stopButton);
        return box;
    }

    private String getTextFieldValue(String fieldId) {
        return ((javafx.scene.control.TextField) GlobalScene.lookup("#" + fieldId)).getText();
    }

    private boolean getCheckBoxValue(String checkBoxId) {
        return ((CheckBox) GlobalScene.lookup("#" + checkBoxId)).isSelected();
    }
    public  static void SetTekst(String tekst){
        HelloApplication.tehtudtekst.setText(tekst);
    }

    public static void main(String[] args) {
        launch();
    }
}
