package org.tts.ttsdeckgen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;
public class Main extends Thread{
    String colors;
    String nonlandsearch;
    String landsearch;
    int decksize;
    Boolean kasRandom;
    String lands;
    String basiclandPro;
    boolean running=true;
  DoubleProperty done=HelloApplication.doneProp;
    public Main(String colors, String nonlandsearch, String landsearch, int decksize, Boolean kasRandom, String lands, String basiclandPro) {
        this.colors = colors;
        this.nonlandsearch = nonlandsearch;
        this.landsearch = landsearch;
        this.decksize = decksize;
        this.kasRandom = kasRandom;
        this.lands = lands;
        this.basiclandPro = basiclandPro;
    }

    public void request(String url1, ArrayList<String> urlid, ArrayList<String> nimed, FileWriter writer){
        try {
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder informationstring = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    informationstring.append(scanner.nextLine());
                }
                scanner.close();

                JSONParser parser = new JSONParser();
                JSONObject card = (JSONObject) parser.parse(String.valueOf(informationstring));


                String name = (String) card.get("name");

                writer.write(name+"\n");
                System.out.println(name);
                nimed.add(name);

                if (card.get("image_uris")!=null){
                    JSONObject yes = (JSONObject)card.get("image_uris");
                    urlid.add((String) yes.get("normal"));
                }
                else{
                    JSONArray imageeforflip = (JSONArray) card.get("card_faces");
                    JSONObject fimagee = (JSONObject) imageeforflip.get(0);
                    fimagee = (JSONObject) fimagee.get("image_uris");
                    urlid.add((String) fimagee.get("normal"));
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    public void interrupt() {
        HelloApplication.SetTekst("Töö on Peatatud");
        running=false;
    }

    public void run(){
        ArrayList<String> nimed = new ArrayList<String>();  /////////llist kaartidde nimede jaoks
        ArrayList<String> urlid = new ArrayList<String>();  //////////list kaartide urlide jaoks
        int LandsCount=0;
        int basicp=30;
        char[] varvid={'W','U','B','G','R'};
        Scanner s = new Scanner(System.in);

        if(!kasRandom) {
            try{
                LandsCount = Integer.parseInt(lands);
                basicp = Integer.parseInt(basiclandPro);
            }catch (RuntimeException e){
                System.out.println(e.toString());
                LandsCount=(int)(decksize*0.4);
            }

        }
        else{
            LandsCount=(int)(decksize*0.4);
            Random rand=new Random();
            StringBuilder build=new StringBuilder();
            while (build.length()<2){
                char varv=varvid[rand.nextInt(varvid.length)];
                if(build.isEmpty() ||build.charAt(0)!=varv){
                    build.append(varv);
                }
            }
            colors=build.toString();
            nonlandsearch="-type%3Aland+commander%3A"+colors+"+%28game%3Apaper%29";
            //nonlandsearch="-type%3Aland+color<%3DWU+%28game%3Apaper%29";
            landsearch="type%3Aland+commander%3A"+colors+"+%28game%3Apaper%29";

        }
        if(!running){
            return;
        }
        int nonlands = decksize-LandsCount;
        System.out.println(nonlands);
        float protent = basicp/100.0f;
        int nonbasiclands = (int)(LandsCount*protent);
        System.out.println(nonbasiclands);
        int basiclands = LandsCount-nonbasiclands;
        System.out.println(basiclands);
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
            request((String) ("https://api.scryfall.com/cards/random?q="+nonlandsearch),urlid, nimed,writer);
            done.setValue(done.getValue()+1);
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
            request("https://api.scryfall.com/cards/random?q="+landsearch,urlid, nimed,writer);
            done.setValue(done.getValue()+1);
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
            request("https://api.scryfall.com/cards/random?q=Plains+-type%3Asnow+%28type%3Abasic+type%3APlains%29+%28game%3Apaper%29",urlid,nimed,writer);
            for (int i = 0; i < landid.get('W')-1; i++) {
                done.setValue(done.getValue()+1);
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
            request("https://api.scryfall.com/cards/random?q=Island+-type%3Asnow+%28type%3Abasic+type%3AIsland%29+%28game%3Apaper%29",urlid,nimed,writer);

            for (int i = 0; i < landid.get('U')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                done.setValue(done.getValue()+1);
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
            request("https://api.scryfall.com/cards/random?q=Swamp+-type%3Asnow+%28type%3Abasic+type%3ASwamp%29+%28game%3Apaper%29",urlid,nimed,writer);
            for (int i = 0; i < landid.get('B')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                done.setValue(done.getValue()+1);
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
            request("https://api.scryfall.com/cards/random?q=Mountain+-type%3Asnow+%28type%3Abasic+type%3AMountain%29+%28game%3Apaper%29",urlid,nimed,writer);
            for (int i = 0; i < landid.get('R')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                done.setValue(done.getValue()+1);
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
            request("https://api.scryfall.com/cards/random?q=Forest+-type%3Asnow+%28type%3Abasic+type%3AForest%29+%28game%3Apaper%29",urlid,nimed,writer);
            for (int i = 0; i < landid.get('G')-1; i++) {
                urlid.add(urlid.getLast());
                nimed.add(nimed.getLast());
                done.setValue(done.getValue()+1);
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
        HelloApplication.SetTekst("Deck on Tehtud");
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Main.yield();
    }
}
