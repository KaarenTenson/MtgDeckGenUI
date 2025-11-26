package org.tts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import netscape.javascript.JSObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;
public class Main extends Thread{
    String colors;
    String nonlandsearch;
    String landsearch;
    requestData reData;
    boolean running=true;
    DoubleProperty done;
    public Main(requestData reData,DoubleProperty done) {
        this.reData=reData;
        this.done=done;
    }

    public void request(String url1, ArrayList<String> urlid, String params, ArrayList<String> nimed, FileWriter writer){
        try {
            String encodedParams = URLEncoder.encode(params, StandardCharsets.UTF_8);
            URL url = new URI(url1 + encodedParams).toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder informationstring = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                StringBuilder json = new StringBuilder();

                while (scanner.hasNext()) {
                    json.append(scanner.nextLine());
                }
                scanner.close();

                // Parse with Gson
                JsonObject card = JsonParser.parseString(json.toString()).getAsJsonObject();

                // Extract name
                String name = card.get("name").getAsString();
                writer.write(name + "\n");
                nimed.add(name);
                System.out.println(name);

                // Extract image URL
                if (card.has("image_uris") && card.get("image_uris").isJsonObject()) {
                    JsonObject img = card.getAsJsonObject("image_uris");
                    urlid.add(img.get("normal").getAsString());
                } else if (card.has("card_faces")) {
                    JsonArray faces = card.getAsJsonArray("card_faces");
                    JsonObject front = faces.get(0).getAsJsonObject();
                    JsonObject img = front.getAsJsonObject("image_uris");
                    urlid.add(img.get("normal").getAsString());
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    public void IncrementDoubleProperty(){
        Platform.runLater(() -> {
            // This will execute on the JavaFX Application Thread
            done.set(done.getValue()+1);  // `value` can be your progress value based on the thread's work
            System.out.println(done.getValue());
        });
    }
    public void interrupt() {
        HelloApplication.SetTekst("Töö on Peatatud");
        running=false;
    }

    public void run(){
        ArrayList<String> nimed = new ArrayList<String>();  /////////llist kaartidde nimede jaoks
        ArrayList<String> urlid = new ArrayList<String>();  //////////list kaartide urlide jaoks
        Scanner s = new Scanner(System.in);

        colors=new String(reData.colours);
        System.out.println(colors);
        nonlandsearch="-type%3Aland+commander%3A"+colors+"+%28game%3Apaper%29";
        //nonlandsearch="-type%3Aland+color<%3DWU+%28game%3Apaper%29";
        landsearch="type%3Aland+commander%3A"+colors+"+%28game%3Apaper%29";

        if(!running){
            return;
        }
        int nonlands = reData.deckSize-reData.landCount;
        System.out.println("nonalands:"+nonlands);
        float protsent = reData.nonBasicLandProcent;
        int nonbasiclands = (int)(reData.landCount*protsent);
        System.out.println("nonbasicLand"+nonbasiclands);
        int basiclands = reData.landCount-nonbasiclands;
        System.out.println("basicLands"+basiclands);
        File file = new File("decklist.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i<nonlands ; i++) {  //////////////lisab landid
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                currentThread().interrupt();
            }
            request("https://api.scryfall.com/cards/random?",urlid, "q=" +reData.nonLandURL, nimed,writer);

            IncrementDoubleProperty();
            if(!running){
                return;
            }
        }
        for(int i = 0; i<nonbasiclands ; i++) {  /////////////lisab nonbasic landid
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                currentThread().interrupt();
            }
            request("https://api.scryfall.com/cards/random?",urlid, "q="+reData.LandURL, nimed,writer);
            IncrementDoubleProperty();
            if(!running){
                return;
            }
        }
        int basiccount = 0;
        HashMap<Character, Integer> landid=new HashMap<>();
        int hulk=basiclands/(colors.length());
        int jääk=basiclands%(colors.length());
        for(char de:colors.toCharArray()){
            if(jääk>0){
                landid.put(de,hulk+1);
                jääk-=1;}
            else{
                landid.put(de,hulk);
            }
        }
        if(landid.containsKey('W')){
            try {
                Thread.sleep(50);
                writer.write("Plains"+"\n");
            } catch (InterruptedException e ) {
                currentThread().interrupt();
            }catch (IOException e){
                System.out.println(e.toString());
            }
            basiccount++;
            request("https://api.scryfall.com/cards/random?q=Plains+-type%3Asnow+%28type%3Abasic+type%3APlains%29+%28game%3Apaper%29",urlid,"",nimed,writer);
            for (int i = 0; i < landid.get('W')-1; i++) {
                IncrementDoubleProperty();
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
            }
            if(!running){
                return;
            }
        }
        if(landid.containsKey('U')){
            try {
                Thread.sleep(50);
                writer.write("Island"+"\n");
            } catch (InterruptedException e ) {
                currentThread().interrupt();
            }catch (IOException e){
                System.out.println(e.toString());
            }
            basiccount++;
            request("https://api.scryfall.com/cards/random?q=Island+-type%3Asnow+%28type%3Abasic+type%3AIsland%29+%28game%3Apaper%29",urlid, "",nimed,writer);

            for (int i = 0; i < landid.get('U')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                IncrementDoubleProperty();
            }
            if(!running){
                return;
            }}

        if(landid.containsKey('B')){
            try {
                Thread.sleep(50);
                writer.write("Swamp"+"\n");
            } catch (InterruptedException e ) {
                currentThread().interrupt();
            }catch (IOException e){
                System.out.println(e.toString());
            }
            basiccount++;
            request("https://api.scryfall.com/cards/random?q=Swamp+-type%3Asnow+%28type%3Abasic+type%3ASwamp%29+%28game%3Apaper%29",urlid,"",nimed,writer);
            for (int i = 0; i < landid.get('B')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                IncrementDoubleProperty();
            }
            if(!running){
                return;
            }
        }
        if(landid.containsKey('R')){
            try {
                Thread.sleep(50);
                writer.write("Mountain"+"\n");
            } catch (InterruptedException e ) {
                currentThread().interrupt();
            }catch (IOException e){
                System.out.println(e.toString());
            }

            basiccount++;
            request("https://api.scryfall.com/cards/random?q=Mountain+-type%3Asnow+%28type%3Abasic+type%3AMountain%29+%28game%3Apaper%29",urlid,"",nimed,writer);
            for (int i = 0; i < landid.get('R')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                IncrementDoubleProperty();
            }
            if(!running){
                return;
            }
        }
        if(landid.containsKey('G')){
            try {
                Thread.sleep(50);
                writer.write("Forest"+"\n");
            } catch (InterruptedException e ) {
                currentThread().interrupt();
            }catch (IOException e){
                System.out.println(e.toString());
            }
            basiccount++;
            request("https://api.scryfall.com/cards/random?q=Forest+-type%3Asnow+%28type%3Abasic+type%3AForest%29+%28game%3Apaper%29",urlid,"",nimed,writer);
            for (int i = 0; i < landid.get('G')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                IncrementDoubleProperty();
            }
            if(!running){
                return;
            }
        }
        if(!running){
            return;
        }
        String ttsfile= ("{\"ObjectStates\":[{\"Name\":\"DeckCustom\",\"ContainedObjects\":[");  ////////hakkab kirjutama tts json filei
        int i = 0;
        while (i<nimed.size()){ ////esimene osa kaartidest
            ttsfile=(ttsfile+"{\"CardID\":"+((i+1)*100)+",\"Name\":\"Card\",\"Nickname\":\""+nimed.get(i)+"\",\"Transform\":{\"posX\":0,\"posY\":0,\"posZ\":0,\"rotX\":0,\"rotY\":180,\"rotZ\":180,\"scaleX\":1,\"scaleY\":1,\"scaleZ\":1}}");
            i++;
            if(i<nimed.size()){
                ttsfile = ttsfile+",";
            }
        }
        ttsfile = ttsfile+"],\"DeckIDs\":[";
        i =0;
        while (i<nimed.size()){ ////esimene osa kaartidest
            ttsfile=ttsfile+((i+1)*100);
            i++;
            if(i<nimed.size()){
                ttsfile = ttsfile+",";
            }
        }
        ttsfile = ttsfile +("],\"CustomDeck\":{");

        i = 0;
        while (i<urlid.size()){ ////esimene osa kaartidest
            String cardface = urlid.get(i);

            ttsfile=ttsfile+("\""+(i+1)+"\":{\"FaceURL\":\""+cardface+"\",\"BackURL\":\"https://i.redd.it/qnnotlcehu731.jpg\",\"NumHeight\":1,\"NumWidth\":1,\"BackIsHidden\":true}");
            i++;
            if(i<nimed.size()){
                ttsfile = ttsfile+",";
            }

        }
        ttsfile = ttsfile+"},\"Transform\":{\"posX\":0,\"posY\":1,\"posZ\":0,\"rotX\":0,\"rotY\":180,\"rotZ\":0,\"scaleX\":1,\"scaleY\":1,\"scaleZ\":1}}";

        ttsfile = ttsfile + "]}";

        try{
            FileWriter wwriter = new FileWriter(new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/My Games/Tabletop Simulator/Saves/Saved Objects/deck.json");
            wwriter.write(ttsfile);
            wwriter.close();
        }catch(Exception e){
            FileWriter wwriter = null;
            try {
                wwriter = new FileWriter(System.getProperty("user.dir")+"/OneDrive/Documents/My Games/Tabletop Simulator/Saves/Saved Objects/deck.json");
                wwriter.write(ttsfile);
                wwriter.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
        HelloApplication.SetTekst("Deck is made");
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Main.yield();
    }
}
