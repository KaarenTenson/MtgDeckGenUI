package org.tts.ttsdeckgen;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    static Text tehtudtekst = new Text("");
    static DoubleProperty doneProp = new SimpleDoubleProperty();
    static Thread current;
    static Scene GlobalScene;
    @Override
    public void start(Stage stage) throws Exception {
        // Create the main layout container (VBox)
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(15));

        // Deck Size
        HBox deckSizeBox = createLabeledTextField("Deck Size:", "suurus");

        // Random Check
        HBox randomCheckBox = createCheckBox("Easy:", "RandomCheck");

        // Land Count
        HBox landCountBox = createLabeledTextField("Land Count:", "landcountfield");

        // Colors Box
        HBox colorsBox = createLabeledTextField("Colors:", "varvidfield");

        // Nonbasic Land Percentage
        HBox nonBasicLandBox = createLabeledTextField("Nonbasic Land %:", "Protsent");

        // URL Inputs
        HBox urlBox = createLabeledTextField("URL Nonland:", "url");
        HBox landUrlBox = createLabeledTextField("URL Land:", "Landurl");

        // Progress Bar
        HBox progressBox = new HBox();
        ProgressBar prog = new ProgressBar(0);
        progressBox.getChildren().add(prog);

        // Action Buttons
        HBox buttonBox = createButtonBox(prog);

        // Add all elements to root container
        root.getChildren().addAll(deckSizeBox, randomCheckBox, landCountBox, colorsBox, nonBasicLandBox, urlBox, landUrlBox, progressBox, buttonBox, tehtudtekst);

        // Create the Scene
        Scene scene = new Scene(root, 400, 450);
        GlobalScene=scene;
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        // Set up Stage
        stage.setTitle("Deck Generator");
        stage.setScene(scene);
        stage.show();
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
        CheckBox checkBox = new CheckBox();
        checkBox.setId(id);
        box.getChildren().addAll(labelText, checkBox);
        return box;
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

                    prog.progressProperty().bind(doneProp.divide(Double.parseDouble(getTextFieldValue("suurus"))));
                    Main thread = new Main(getTextFieldValue("varvidfield"), getTextFieldValue("url"), getTextFieldValue("Landurl"),
                            Integer.parseInt(getTextFieldValue("suurus")), getCheckBoxValue("RandomCheck"),
                            getTextFieldValue("landcountfield"), getTextFieldValue("Protsent"), doneProp);
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
