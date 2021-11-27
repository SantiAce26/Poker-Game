package com.example.pokergame.pokercomponents;

public class Player {
    private int[] cards; // The player's hand
    private int chipSize; // The number of chips the player has
    int playerID; // The player's ID, unique to them (possibly can generate from username if unique)

    public Player(int chipSize, int playerID) {
        this.chipSize = chipSize;
        this.playerID = playerID;
        cards = new int[2];
        for (int i = 0; i < 2; i++) {
            cards[i] = -1;
        }
    }

    public int[] getCards() {
        return cards;
    }

    public void setCards(int[] newCards){
        cards[0] = newCards[0];
        cards[1] = newCards[1];
    }

    public int getChipSize() {
        return chipSize;
    }

    public void setChipSize(int newChipTotal) {
        chipSize = newChipTotal;
    }

    public int getPlayerID(){
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}
