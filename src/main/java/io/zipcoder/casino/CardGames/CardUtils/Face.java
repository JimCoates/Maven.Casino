package io.zipcoder.casino.CardGames.CardUtils;

public enum Face {

        ACE("A",11),
        TWO("2",2),
        THREE("3",3),
        FOUR("4",4),
        FIVE("5",5),
        SIX("6", 6),
        SEVEN("7",7),
        EIGHT("8",8),
        NINE("9",9),
        TEN("10", 10),
        JACK("J", 10),
        QUEEN("Q", 10),
        KING("K", 10);

        String faceValue;
        int value;

        private Face(String f, int v){
                this.faceValue = f;
                this.value = v;
        }

        public String getFaceValue(){
                return faceValue;
        }

        public int getValue(){
                return this.value;
        }
}



