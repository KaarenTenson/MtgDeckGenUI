package org.tts.ttsdeckgen;

import java.util.Arrays;

public class requestData {
    int deckSize;
    boolean easy;
    char[] colours;
    int landCount;
    float nonBasicLandProcent;
    String nonLandURL;
    String LandURL;

    public requestData(String deckSize, boolean easy, String colours, String louncCount, String nonBasicLandProcent, String nonLandURL, String LandURL) {
        try {this.deckSize = Integer.parseInt(deckSize);
        }catch (NumberFormatException e) {
            throw new parseRequestError("Deck size must be an integer");
        }
        this.easy = easy;
        if(easy){
            fillFields();
            return;
        }
        this.colours =parseColurs(colours);
        try{this.landCount = Integer.parseInt(louncCount);}
        catch (NumberFormatException e) {
            throw new parseRequestError("Land count must be an integer");
        }
        if(this.landCount > this.deckSize){
            throw new parseRequestError("Land count cannot be greater than deck size");
        }
        try{
        this.nonBasicLandProcent = Float.parseFloat(nonBasicLandProcent);
        }catch (NumberFormatException e) {
            throw new parseRequestError("Non basic landprocent must be an number");
        }
        if(this.nonBasicLandProcent>1){
            throw new parseRequestError("Non basic landprocent cannot be greater than 1");
        }
        this.nonLandURL = nonLandURL;
        this.LandURL = LandURL;
    }
    private void fillFields(){

        this.landCount = (int)(this.deckSize*0.4);
        System.out.println("landCount:"+this.landCount);
        getColours();
        this.nonBasicLandProcent=0.3f;


    }
    public void getColours(){
        char[] colourChoices={'G', 'W', 'B', 'U', 'R'};

        this.colours = new char[2];
        for (int i = 0; i < 2; i++) {
            boolean isduplicate=false;
            char randomColour = colourChoices[(int)(Math.random()*colours.length)];
            for(int j = 0; j < colours.length; j++){
                if(randomColour==(colours[j])){
                    i--;
                    isduplicate=true;
                    break;
                }
            }
            if(!isduplicate){
                this.colours[i] = randomColour;
            }

        }
    }
    public char[] parseColurs(String colurs) {
        char[] coloursArr = colurs.toCharArray();
        for (int i = 0; i < coloursArr.length; i++) {
            if(coloursArr[i] == ' '){
                continue;
            }
            coloursArr[i] = Character.toUpperCase(coloursArr[i]);
            switch (coloursArr[i]) {
                case 'G':
                    break;
                case 'W':
                    break;
                case 'B':
                    break;
                case 'U':
                    break;
                case 'R':
                    break;

                default:
                throw new parseRequestError("wrong color");
            }
        }
        return coloursArr;

    }


}
