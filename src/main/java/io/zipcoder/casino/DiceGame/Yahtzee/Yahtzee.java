package io.zipcoder.casino.DiceGame.Yahtzee;

import io.zipcoder.casino.DiceGame.DiceUtils.Dice;
import io.zipcoder.casino.DiceGame.DiceUtils.DiceGame;
import io.zipcoder.casino.Utilities.Player;
import io.zipcoder.casino.Utilities.Console;
import io.zipcoder.casino.Utilities.DisplayGraphics;

import java.util.*;

public class Yahtzee extends DiceGame {
    private YahtzeePlayer yahtzeePlayer;
    private int score;
    private TreeMap<String, Integer> scoreCard;
    private ArrayList<Dice> savedDice;
    private ArrayList<Dice> rolledDice;
    private boolean playing = true;
    Console console = Console.getInstance();
    DisplayGraphics displayGraphics = new DisplayGraphics();
    String input = "";


    public Yahtzee(Player player) {
        this.yahtzeePlayer = new YahtzeePlayer(player);
        this.score = 0;
        this.scoreCard = setUpScoreCard();
        this.savedDice = new ArrayList<>();
        this.rolledDice = new ArrayList<>();
    }

    @Override
    public void play() {
        console.println(DisplayGraphics.welcomeToYahtzeeString());
        input = console.getStringInput("\nHello %s!  Welcome to Yahtzee!  Type 'roll' to begin!", yahtzeePlayer.getName());

        while (playing) {
            YahtzeeAction.valueOf(input.toUpperCase()).perform(this);
            invalidInputCheck();
        }
    }


    // walkAway takes user back to the casino
    public void walkAway() {
        playing = false;
        System.out.println("Thank you for playing Yahtzee!");
    }


    // getAllDice merges all rolledDice and savedDice into one ArrayList
    public ArrayList<Dice> getAllDice(ArrayList<Dice> rolledDice, ArrayList<Dice> savedDice) {
        ArrayList<Dice> allDice = rolledDice;
        for (Dice die : savedDice) {
            allDice.add(die);
        }
        return allDice;
    }


    // this method will get the score for the entered category based on the dice
    public Integer getScoreForCategory(String category, ArrayList<Dice> allDice) {
        int score = 0;
        String categoryToScore = category.toLowerCase();

        switch (categoryToScore) {
            case "aces":
                score = scoreUpperSection(allDice, 1);
                break;

            case "twos":
                score = scoreUpperSection(allDice, 2);
                break;

            case "threes":
                score = scoreUpperSection(allDice, 3);
                break;

            case "fours":
                score = scoreUpperSection(allDice, 4);
                break;

            case "fives":
                score = scoreUpperSection(allDice, 5);
                break;

            case "sixes":
                score = scoreUpperSection(allDice, 6);
                break;

            case "3 of a kind":
                score = scoreThreeOfAKind(allDice);
                break;

            case "4 of a kind":
                score = scoreFourOfAKind(allDice);
                break;

            case "full house":
                score = scoreFullHouse(allDice);
                break;

            case "small straight":
                score = scoreSmallStraight(allDice);
                break;

            case "large straight":
                score = scoreLargeStraight(allDice);
                break;

            case "yahtzee":
                score = scoreYahtzee(allDice);
                break;

            case "chance":
                score = scoreChance(allDice);
                break;

            default:
                System.out.println("Invalid category.");
        }
        return score;
    }

    public int scoreUpperSection(ArrayList<Dice> allDice, int value){
        int score = 0;
        for (Dice die : allDice){
            if (die.getValue() == value){
                score += value;
            }
        }
        return score;
    }

    public Integer upperSectionBonus() {
        if (getUpperSectionTotal() >= 63) {
            return 35;
        } else {
            return 0;
        }
    }

    public Integer getUpperSectionTotal() {
        Integer upperTotal = 0;

        for(String s : getUpperSectionCategories()){
            if(scoreCard.get(s) != null){
                upperTotal += scoreCard.get(s);
            }
        }

        return upperTotal;
    }

    public boolean hasThreeOfAKind(ArrayList<Dice> allDice) {
        Integer[] diceCount = countDice(allDice);
        for (Integer dieCount : diceCount) {
            if (dieCount >= 3) {
                return true;
            }
        }
        return false;
    }

    public int scoreThreeOfAKind(ArrayList<Dice> allDice) {
        if (hasThreeOfAKind(allDice)) {
            return getSumOfDice(allDice);
        }
        return 0;
    }


    public boolean hasFourOfAKind(ArrayList<Dice> allDice) {
        Integer[] diceCount = countDice(allDice);
        for (Integer dieCount : diceCount) {
            if (dieCount >= 4) {
                return true;
            }
        }
        return false;
    }


    public int scoreFourOfAKind(ArrayList<Dice> allDice) {
        if (hasFourOfAKind(allDice)) {
            return getSumOfDice(allDice);
        }
        return 0;
    }


    public boolean hasFullHouse(ArrayList<Dice> allDice) {
        Integer[] diceCount = countDice(allDice);
        boolean hasTwoCount = false;
        boolean hasThreeCount = false;

        for (Integer dieCount : diceCount) {
            if (dieCount == 2) {
                hasTwoCount = true;
            }
            if (dieCount == 3) {
                hasThreeCount = true;
            }
        }
        return (hasTwoCount && hasThreeCount);
    }


    public int scoreFullHouse(ArrayList<Dice> allDice) {
        if (hasFullHouse(allDice)) {
            return 25;
        } else {
            return 0;
        }
    }


    public boolean hasSmallStraight(ArrayList<Dice> allDice) {
        Integer[] diceCount = countDice(allDice);

        if ((diceCount[0] >= 1) && (diceCount[1] >= 1) && (diceCount[2] >= 1) && (diceCount[3] >= 1)) {
            return true;
        }
        if ((diceCount[1] >= 1) && (diceCount[2] >= 1) && (diceCount[3] >= 1) && (diceCount[4] >= 1)) {
            return true;
        }
        if ((diceCount[2] >= 1) && (diceCount[3] >= 1) && (diceCount[4] >= 1) && (diceCount[5] >= 1)) {
            return true;
        } else {
            return false;
        }
    }


    public int scoreSmallStraight(ArrayList<Dice> allDice) {
        if (hasSmallStraight(allDice)) {
            return 30;
        } else {
            return 0;
        }
    }


    public boolean hasLargeStraight(ArrayList<Dice> allDice) {
        Integer[] diceCount = countDice(allDice);

        if ((diceCount[0] == 1) && (diceCount[1] == 1) && (diceCount[2] == 1) && (diceCount[3] == 1) && (diceCount[4] == 1)) {
            return true;
        }
        if ((diceCount[1] == 1) && (diceCount[2] == 1) && (diceCount[3] == 1) && (diceCount[4] == 1) && (diceCount[5] == 1)) {
            return true;
        } else {
            return false;
        }
    }


    public int scoreLargeStraight(ArrayList<Dice> allDice) {
        if (hasLargeStraight(allDice)) {
            return 40;
        } else {
            return 0;
        }
    }


    public boolean hasYahtzee(ArrayList<Dice> allDice) {
        Integer[] diceCount = countDice(allDice);

        for (Integer dieCount : diceCount) {
            if (dieCount == 5) {
                return true;
            }
        }
        return false;
    }


    public int scoreYahtzee(ArrayList<Dice> allDice) {
        if (hasYahtzee(allDice)) {
            return 50;
        } else {
            return 0;
        }
    }


    public int scoreChance(ArrayList<Dice> allDice) {
        return getSumOfDice(allDice);
    }


    public int getLowerSectionTotal() {
        int lowerTotal = 0;

        for (String s : getLowerSectionCategories()){
            if (scoreCard.get(s) != null){
                lowerTotal += scoreCard.get(s);
            }
        }
        return lowerTotal;
    }


    public int getTotalScore() {
        return getLowerSectionTotal() + getUpperSectionTotal() + upperSectionBonus();
    }

    public YahtzeePlayer getYahtzeePlayer() {
        return this.yahtzeePlayer;
    }

    public TreeMap<String, Integer> getScoreCard() {
        return this.scoreCard;
    }

    public ArrayList<Dice> getSavedDice() {
        return this.savedDice;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Dice> getRolledDice() {
        return rolledDice;
    }


    public Integer[] countDice(ArrayList<Dice> dice) {
        Integer[] diceCounter = {0, 0, 0, 0, 0, 0};
        for (Dice die : dice) {
            diceCounter[die.getValue() - 1]++;
        }
        return diceCounter;
    }


    public int getSumOfDice(ArrayList<Dice> dice) {
        int sum = 0;

        for (Dice die : dice) {
            sum += die.getValue();
        }
        return sum;
    }

    public void markScoreCard(String category, ArrayList<Dice> dice) {
        int score = getScoreForCategory(category, dice);
        scoreCard.put(category.toLowerCase(), score);

        if (upperSectionScoresComplete()) {
            scoreCard.put("upper bonus", upperSectionBonus());
        }
    }


    public String listOfDiceToDiceString(ArrayList<Dice> diceList) {
        String diceString = "";
        for (Dice die : diceList) {
            if (die.getValue() == 1) {
                diceString = diceString + "  ⚀  |";
            } else if (die.getValue() == 2) {
                diceString = diceString + "  ⚁  |";
            } else if (die.getValue() == 3) {
                diceString = diceString + "  ⚂  |";
            } else if (die.getValue() == 4) {
                diceString = diceString + "  ⚃  |";
            } else if (die.getValue() == 5) {
                diceString = diceString + "  ⚄  |";
            } else if (die.getValue() == 6) {
                diceString = diceString + "  ⚅  |";
            }
        }
        return diceString;

    }


    public String getCurrentDiceString(ArrayList<Dice> rolledDice, ArrayList<Dice> savedDice) {
        String currentDiceString = "";
        String spacerString = "\n|------------------------------------------|\n";
        String numberString = "|            |  1  |  2  |  3  |  4  |  5  |";


        String rolledDiceString = "|Rolled Dice |" + listOfDiceToDiceString(rolledDice);
        for (int i = 0; i < 5 - rolledDice.size(); i++) {
            rolledDiceString = rolledDiceString + "     |";
        }

        String savedDiceString = "| Saved Dice |";

        for (int i = 0; i < rolledDice.size(); i++) {
            savedDiceString = savedDiceString + "     |";
        }

        savedDiceString = savedDiceString + listOfDiceToDiceString(savedDice);
        currentDiceString = spacerString + numberString + spacerString + rolledDiceString + spacerString + savedDiceString + spacerString;

        return currentDiceString;
    }


    public String getScoreCardString() {
        String scoreCardString = "";
        String spacerString = "|---------------------------------|\n";
        String categoryString = "  Category         |  Score        \n";
        scoreCardString = scoreCardString + spacerString +
                categoryString + spacerString +
                getAcesScoreString() + spacerString +
                getTwosScoreString() + spacerString +
                getThreesScoreString() + spacerString +
                getFoursScoreString() + spacerString +
                getFivesScoreString() + spacerString +
                getSixesScoreString() + spacerString +
                getUpperBonusScoreString() + spacerString +
                getThreeOfAKindScoreString() + spacerString +
                getFourOfAKindScoreString() + spacerString +
                getFullHouseScoreString() + spacerString +
                getSmallStraightScoreString() + spacerString +
                getLargeStraightScoreString() + spacerString +
                getYahtzeeScoreString() + spacerString +
                getChanceScoreString() + spacerString +
                getTotalScoreString() + spacerString;

        return scoreCardString;
    }


    public TreeMap<String, Integer> setUpScoreCard() {
        TreeMap<String, Integer> scoreCard = new TreeMap<>();
        for(String s : getAllCategories()){
            scoreCard.put(s, null);
        }
        scoreCard.put("upper bonus", null);
        scoreCard.put("total score", null);

        return scoreCard;
    }


    public String getAcesScoreString() {
        if (scoreCard.get("aces") == null) {
            return "   Aces            |\n";
        } else {
            return "   Aces            |    " + scoreCard.get("aces") + "\n";
        }
    }


    public String getTwosScoreString() {
        if (scoreCard.get("twos") == null) {
            return "   Twos            |\n";
        } else {
            return "   Twos            |    " + scoreCard.get("twos") + "\n";
        }
    }


    public String getThreesScoreString() {
        if (scoreCard.get("threes") == null) {
            return "   Threes          |\n";
        } else {
            return "   Threes          |    " + scoreCard.get("threes") + "\n";
        }
    }


    public String getFoursScoreString() {
        if (scoreCard.get("fours") == null) {
            return "   Fours           |\n";
        } else {
            return "   Fours           |    " + scoreCard.get("fours") + "\n";
        }
    }


    public String getFivesScoreString() {
        if (scoreCard.get("fives") == null) {
            return "   Fives           |\n";
        } else {
            return "   Fives           |    " + scoreCard.get("fives") + "\n";
        }
    }


    public String getSixesScoreString() {
        if (scoreCard.get("sixes") == null) {
            return "   Sixes           |\n";
        } else {
            return "   Sixes           |    " + scoreCard.get("sixes") + "\n";
        }
    }


    public String getThreeOfAKindScoreString() {
        if (scoreCard.get("3 of a kind") == null) {
            return "   3 of a Kind     |\n";
        } else {
            return "   3 of a Kind     |    " + scoreCard.get("3 of a kind") + "\n";
        }
    }

    public String getFourOfAKindScoreString() {
        if (scoreCard.get("4 of a kind") == null) {
            return "   4 of a Kind     |\n";
        } else {
            return "   4 of a Kind     |    " + scoreCard.get("4 of a kind") + "\n";
        }
    }


    public String getFullHouseScoreString() {
        if (scoreCard.get("full house") == null) {
            return "   Full House      |\n";
        } else {
            return "   Full House      |    " + scoreCard.get("full house") + "\n";
        }
    }


    public String getSmallStraightScoreString() {
        if (scoreCard.get("small straight") == null) {
            return "   Small Straight  |\n";
        } else {
            return "   Small Straight  |    " + scoreCard.get("small straight") + "\n";
        }
    }


    public String getLargeStraightScoreString() {
        if (scoreCard.get("large straight") == null) {
            return "   Large Straight  |\n";
        } else {
            return "   Large Straight  |    " + scoreCard.get("large straight") + "\n";
        }
    }


    public String getYahtzeeScoreString() {
        if (scoreCard.get("yahtzee") == null) {
            return "   Yahtzee         |\n";
        } else {
            return "   Yahtzee         |    " + scoreCard.get("yahtzee") + "\n";
        }
    }


    public String getChanceScoreString() {
        if (scoreCard.get("chance") == null) {
            return "   Chance          |\n";
        } else {
            return "   Chance          |    " + scoreCard.get("chance") + "\n";
        }
    }


    public String getUpperBonusScoreString() {
        if (scoreCard.get("upper bonus") == null) {
            return "   Upper Bonus     |\n";
        } else {
            return "   Upper Bonus     |    " + scoreCard.get("upper bonus") + "\n";
        }
    }


    public String getTotalScoreString() {
        if (scoreCard.get("total score") == null) {
            return "   Total Score     |\n";
        } else {
            return "   Total Score     |    " + scoreCard.get("total score") + "\n";
        }
    }


    public boolean upperSectionScoresComplete() {

        for(String s : getUpperSectionCategories()){
            if (scoreCard.get(s) == null){
                return false;
            }
        }
        return true;
    }


    public boolean scorecardComplete() {
        Set<String> scorecardCategories = scoreCard.keySet();
        for (String s : scorecardCategories){
            if (scoreCard.get(s) == null){
                return false;
            }
        }
        return true;
    }


    public boolean isValidCategory(String categoryInput) {
        String input = categoryInput.toLowerCase();

        for(String s :getAllCategories()){
            if(input.equals(s)){
                return true;
            }
        }

        return false;
    }



    public String allOptions() {
        return "Type 'save' to save rolled dice.\n" +
                "Type 'return' to return saved dice to be rolled again.\n" +
                "Type 'roll' to roll again.\n" +
                "Type 'scorecard' to see scorecard.\n" +
                "Type 'mark' to mark a score on you scorecard.\n" +
                "Type 'exit' to walk away.";
    }


    public void roll() {
        try {
            rolledDice = yahtzeePlayer.playerRollDice(5 - savedDice.size());
            console.println("\nRoll #%d", yahtzeePlayer.getRollNumber());
            console.println(getCurrentDiceString(rolledDice, savedDice));
            input = console.getStringInput(allOptions());

        } catch (YahtzeePlayer.TooManyRollsException tooManyRollsException) {
            input = console.getStringInput("\nYou have already rolled 3 times.  Type 'mark' to mark your scorecard.");
        }
    }

    public void saveDice() {
        input = console.getStringInput("Type the locations of the dice you want to save.\n" +
                "(Ex: '123' to save first three dice)");
        try {
            for (Dice die : yahtzeePlayer.saveDice(rolledDice, input)) {
                savedDice.add(die);
            }
            console.println("Dice saved.");
            console.println("\nRoll #%d", yahtzeePlayer.getRollNumber());
            console.println(getCurrentDiceString(rolledDice, savedDice));
            input = console.getStringInput(allOptions());

        } catch (IndexOutOfBoundsException i) {
            input = console.getStringInput("Invalid input.  " + allOptions());
        }
    }


    public void returnDice() {
        input = console.getStringInput("Type the locations of the dice you want to return.\n" +
                "(Ex: '345' to return last three dice)");
        try {
            for (Dice die : yahtzeePlayer.returnDice(savedDice, input)) {
                rolledDice.add(die);
            }
            console.println("Dice returned");
            console.println("\nRoll #%d", yahtzeePlayer.getRollNumber());
            console.println(getCurrentDiceString(rolledDice, savedDice));
            input = console.getStringInput(allOptions());

        } catch (ArrayIndexOutOfBoundsException aioobEx) {
            input = console.getStringInput("Invalid input.  " + allOptions());
        }
    }

    public void showScorecard() {
        console.println(getScoreCardString());
        input = console.getStringInput("Type 'back' to go back.  Type 'mark' to mark scorecard");
        if (input.toLowerCase().equals("back")) {
            console.println("\nRoll #%d", yahtzeePlayer.getRollNumber());
            console.println(getCurrentDiceString(rolledDice, savedDice));
            input = console.getStringInput(allOptions());
        }
    }


    public String categoryString() {
        return "Enter the category you want to mark on your scorecard.\n" +
                " 'aces', 'twos', 'threes', fours', 'fives', 'sixes'\n" +
                "     '3 of a kind', '4 of a kind', 'full house',\n" +
                "'small straight', large straight', 'yahtzee', 'chance'\n" +
                "           Enter 'back' to go back.\n";
    }

    public void back() {
        console.println("\nRoll #%d", yahtzeePlayer.getRollNumber());
        console.println(getCurrentDiceString(rolledDice, savedDice));
        input = console.getStringInput(allOptions());
    }


    public void markScore() {
        if (isValidCategory(input)) {
            if (scoreCard.get(input.toLowerCase()) != null) {
                console.println("You already have a score for %s", input);
            } else {
                markScoreCard(input.toLowerCase(), getAllDice(rolledDice, savedDice));
                scoreCard.put("total score", getTotalScore());
                rolledDice.removeAll(rolledDice);
                savedDice.removeAll(savedDice);
                console.println(getScoreCardString());
                yahtzeePlayer.setRollNumber(0);

                checkScorecardComplete();
            }
        } else {
            input = console.getStringInput("Invalid category. Enter 'mark' to try again.");
        }
    }


    public void checkScorecardComplete() {
        if (scorecardComplete()) {
            console.println("Thank you for playing Yahtzee!  Your final score is %d.", getTotalScore());
            playing = false;
            input = "back";
        } else {
            input = console.getStringInput("Type 'roll' to start your next turn.");
        }
    }


    public void invalidInputCheck(){
        if (!(input.toLowerCase().equals("roll") || input.toLowerCase().equals("save") || input.toLowerCase().equals("return") ||
                input.toLowerCase().equals("scorecard") || input.toLowerCase().equals("mark") || input.toLowerCase().equals("back")
                || input.toLowerCase().equals("exit"))) {

            input = console.getStringInput("Invalid input.  " + allOptions());
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public Collection<String> getUpperSectionCategories(){
        Collection<String> upperSectionCategories = new ArrayList<>();
        upperSectionCategories.add("aces");
        upperSectionCategories.add("twos");
        upperSectionCategories.add("threes");
        upperSectionCategories.add("fours");
        upperSectionCategories.add("fives");
        upperSectionCategories.add("sixes");

        return upperSectionCategories;
    }

    public Collection<String> getLowerSectionCategories(){
        Collection<String> lowerSectionCategories = new ArrayList<>();
        lowerSectionCategories.add("3 of a kind");
        lowerSectionCategories.add("4 of a kind");
        lowerSectionCategories.add("full house");
        lowerSectionCategories.add("small straight");
        lowerSectionCategories.add("large straight");
        lowerSectionCategories.add("yahtzee");
        lowerSectionCategories.add("chance");
        return lowerSectionCategories;
    }

    public Collection<String> getAllCategories(){
        Collection<String> allCategories = new ArrayList<>();
        ((ArrayList<String>) allCategories).addAll(getUpperSectionCategories());
        ((ArrayList<String>) allCategories).addAll(getLowerSectionCategories());

        return allCategories;
    }

    public void checkForBack(){
        input = console.getStringInput(categoryString());
        if (input.toLowerCase().equals("back")) {
            back();


        } else {
            markScore();
        }
    }
}

