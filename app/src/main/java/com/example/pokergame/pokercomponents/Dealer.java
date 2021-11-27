package com.example.pokergame.pokercomponents;

import java.net.URISyntaxException;


class Dealer {
    private Deck deckCards;
    private int[] cardsOnBoard;


    public Dealer() {
        deckCards = new Deck();
        cardsOnBoard = new int[5];

        // Dealer starts with no cards on the board
        for (int i = 0; i < 5; i++) {
            cardsOnBoard[i] = -1;
        }
    }



    public void flop()
    {
        for (int i = 0; i < 3; i++) {
            cardsOnBoard[i] = deckCards.deal();
        }
    }

    public void turn()
    {
        cardsOnBoard[3] = deckCards.deal();
    }

    public void river()
    {
        cardsOnBoard[4] = deckCards.deal();
    }
}