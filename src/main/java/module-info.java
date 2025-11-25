module org.tts.ttsdeckgen {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jsobject;
    requires java.desktop;
    requires com.google.gson;


    opens org.tts.ttsdeckgen to javafx.fxml;
    exports org.tts.ttsdeckgen;
}