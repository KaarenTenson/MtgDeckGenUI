package org.tts.ttsdeckgen;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Queue;

public class HelloApplication extends Application {
    static  Text tehtudtekst=new Text("");
    static DoubleProperty doneProp=new SimpleDoubleProperty();
    static Thread current;
    @Override
    public void start(Stage stage) throws IOException {

        VBox root=new VBox();
        HBox PaljuKaarte=new HBox();
            javafx.scene.control.TextField suurus= new javafx.scene.control.TextField();
            Text suurustekst=new Text("DECK SIZE:  ");
            PaljuKaarte.getChildren().addAll(suurustekst,suurus);
        HBox KasRanodm=new HBox();
            CheckBox RandomCheck= new CheckBox();
            Text tekstRan=new Text("EASY:  ");
            KasRanodm.getChildren().addAll(tekstRan,RandomCheck);
        HBox LandBox =new HBox();
            Text tekstlandcount=new Text("LAND COUNT :  ");
            javafx.scene.control.TextField landcountfield= new javafx.scene.control.TextField();
            LandBox.getChildren().addAll(tekstlandcount,landcountfield);
        HBox VarvidBox =new HBox();
            Text tekstvarvid=new Text("COLORS :  ");
            javafx.scene.control.TextField varvidfield= new javafx.scene.control.TextField();
            VarvidBox.getChildren().addAll(tekstvarvid,varvidfield);
        HBox NonbasicLand =new HBox();
            Text tekstpros=new Text("NONBASICLAND% :  ");
            javafx.scene.control.TextField Protsent= new javafx.scene.control.TextField();
            NonbasicLand.getChildren().addAll(tekstpros,Protsent);
        HBox tekstURL =new HBox();
            javafx.scene.control.TextField url= new javafx.scene.control.TextField();
            Text tekst=new Text("URL NONLAND :  ");
            tekstURL.getChildren().addAll(tekst,url);
        HBox tekstLandURL =new HBox();
            javafx.scene.control.TextField Landurl= new javafx.scene.control.TextField();
            Text Landtekst=new Text("URL LAND :  ");
            tekstLandURL.getChildren().addAll(Landtekst,Landurl);
        HBox progress=new HBox();
            ProgressBar prog=new ProgressBar(0);
            progress.getChildren().add(prog);

        HBox done =new HBox();
            javafx.scene.control.Button Request=new javafx.scene.control.Button("REQUEST");
        HBox stopBox =new HBox();
            javafx.scene.control.Button STOP=new javafx.scene.control.Button("STOP");
            stopBox.getChildren().add(STOP);
        HBox TehtudTekstbox= new HBox();
            TehtudTekstbox.getChildren().add(tehtudtekst);

        EventHandler<ActionEvent> start = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                if(current==null || !current.isAlive()){
                    tehtudtekst.setText("");
                    doneProp.setValue(1);
                    prog.progressProperty().bind(doneProp.divide(Double.parseDouble(suurus.getText())));
                Main thread=new Main(varvidfield.getText(),url.getText(),Landurl.getText(),Integer.parseInt(suurus.getText()),RandomCheck.isSelected(),landcountfield.getText(),Protsent.getText());
                current=thread;
                thread.start();}
            }
        };
        EventHandler<ActionEvent> stop = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {doneProp.setValue(0);
               if(current.isAlive()){
                   current.interrupt();
               }
            }
        };
        Request.setOnAction(start);
            done.getChildren().add(Request);
            STOP.setOnAction(stop);
        root.getChildren().addAll(PaljuKaarte,KasRanodm,VarvidBox,LandBox,NonbasicLand,tekstURL,tekstLandURL,done,stopBox,progress, TehtudTekstbox);
        Scene scene = new Scene(root, 320, 340);
        stage.setTitle("DECKGEN");
        stage.setScene(scene);
        stage.show();
    }

    public  static void SetTekst(String tekst){
        HelloApplication.tehtudtekst.setText(tekst);
    }
    public static void main(String[] args) {
        launch();
    }

}