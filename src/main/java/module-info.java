module org.tts.ttsdeckgen {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires jdk.jsobject;
    requires java.desktop;


    opens org.tts.ttsdeckgen to javafx.fxml;
    exports org.tts.ttsdeckgen;
}