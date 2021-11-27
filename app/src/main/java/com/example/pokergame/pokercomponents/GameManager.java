package com.example.pokergame.pokercomponents;

public class GameManager {
    private final int numPlayers; // Get number of players from Lobby info
    private int[] playerOrders; // Possibly unnecessary, can just change order of listOfPlayers
    private Dealer cardDealer; // Dealer responsible for dealing cards
    private boolean isRunning; // tells whether game is still running or not
    private Player[] listOfPlayers;
    private int pot;

    private int dealerLoc;

    private final int START_CHIPS = 30000;
    private int bigBlindAnte = 200;
    private int lilBlindAnte = bigBlindAnte/2;

    public GameManager(Player[] players) {
        listOfPlayers = players; // GameManager should be called in the lobby when the game is started
        cardDealer = new Dealer();
        numPlayers = listOfPlayers.length;
        isRunning = false;
        playerOrders = new int[numPlayers];
        for (int i = 0; i < numPlayers; i++)
            playerOrders[i] = -1;
        dealerLoc = -1;
        pot = 0;
    }

    public void gameSetup() {
        dealerLoc = 0; // Dealer chip starts at first player and moves up the array (clockwise)
        boolean[] hasSwapped = new boolean[numPlayers];

        // Sets up player positions in the table
        // For even number of players, all will change from original pos
        // For odd number of players, one won't be swapped (can change if it matters)
        for (int i = 0; i < numPlayers; i++){

            if (hasSwapped[i])
                continue;

            Player temp = listOfPlayers[i];
            int rand = (int) Math.floor(Math.random()*numPlayers);
            while(hasSwapped[rand])
                rand = addModPlayers(rand, 1);

            listOfPlayers[i] = listOfPlayers[rand];
            listOfPlayers[rand] = temp;
            hasSwapped[i] = true;
            hasSwapped[rand] = true;
        }

        // Hands each player the starting chips
        for (int i = 0; i < numPlayers; i++)
           listOfPlayers[i].setChipSize(START_CHIPS);

    }

    public boolean isDone(){
        int flag = 0;
        for (Player player: listOfPlayers){
            if (flag == 0 && player.getChipSize() != 0)
                flag = 1;
            else if (player.getChipSize() != 0) // Two or more players still have chips
                return false;
        }
        return true; // Only one player has chips left
    }

    public void calculateHandValue(){

    }

    // Function responsible for main game loop
    public void run() {
        gameSetup();

        // TODO: Finish main game loop
        while (!isDone()){
            // First person to play is person after dealer AKA Big Blind
            int firstPlayerLoc = addModPlayers(dealerLoc, 1);

            for (int i = 0; i < numPlayers; i++)
            {
               pot += ante(addModPlayers(dealerLoc, i));
            }


        }
    }

    public int ante(int playerLoc) {
        Player currPlayer = listOfPlayers[playerLoc];
        int retVal = -1;


        if (playerLoc == addModPlayers(dealerLoc, 1)) { // Big blind
            retVal = bigBlindAnte;
            currPlayer.setChipSize(currPlayer.getChipSize() - bigBlindAnte);
        } else if (playerLoc == addModPlayers(dealerLoc, 2)) { // Lil blind
            retVal = lilBlindAnte;
            currPlayer.setChipSize(currPlayer.getChipSize() - lilBlindAnte); // Put player chips into pot
        } else{
            // TODO: Other players can choose to raise, check/call or fold

        }

        return retVal;
    }

    public void raise() {

    }

    public void fold() {

    }

    public void check(){

    }

    public void call(){

    }

    public void runRound(){

    }

    public void distributeWinnings() {

    }

    private int addModPlayers(int n, int a) {
        return (n + a) % numPlayers;
    }
}