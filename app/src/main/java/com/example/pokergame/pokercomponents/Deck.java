package com.example.pokergame.pokercomponents;

import java.util.stream.IntStream;

public class Deck {
    private int[] deckOfCards;
    private int cardLocation;

    public Deck()
    {
        shuffle(); // Shuffles deck of cards and creates a new deck
    }

    public void shuffle()
    {
        int[] deck = IntStream.range(1, 53).toArray();
        int i = deck.length;
        int k;
        int temp;
        while(i != 0)
        {
            k = (int) Math.floor(Math.random()*i);
            i--;
            temp = deck[i];
            deck[i] = deck[k];
            deck[k] = temp;
        }
        deckOfCards = deck;
        cardLocation = 0;
    }

    public int deal()
    {
        int nextCard;
        if(cardLocation < 52)
        {
            nextCard = deckOfCards[cardLocation];
            cardLocation += 1;
            return nextCard;
        }
        else
        {
            //error case
            return 1;
        }
    }

    //not sure what uniqueCheck really entails
    public boolean uniqueCheck()
    {
        return true;
    }
}
