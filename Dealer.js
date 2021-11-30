const {Card} = require("./Card");
class Dealer {
    constructor() {
        this.deckOfCards = [];
        this.pot = 0;
        this.currentBet = 0;
        this.cardsOnTable = [5];
        this.cardLocation = 0;
        this.phase = 0;
        this.sbLoc = 0;
        this.bettorLoc = 0;
        this.blindLoc = 0;
        this.firstGo = 0;
    }

    deal()
    {
        var nextCard;
        
        if(this.cardLocation < 52)
        {
            nextCard = this.deckOfCards[this.cardLocation++]
            return nextCard;
        }
        else
        {
            //error case
            return 1;
        }
    }
  
    shuffle()
    {
        var deck =[]
        var q = 0;
        var names =["club", "diamond", "spade", "heart"];
        for(let i = 0; i < 4; i++)
        {
             for(let j = 1; j < 14 ; j++)
               {
                   deck.push(new Card(i+1, names[i]+"_"+j,j, q++))
               }
        }
        
        var l = deck.length;
        var k;
        var temp;
        while(l != 0)
        {
            k = Math.floor(Math.random()*l);
            l--;
            temp = deck[l];
            deck[l] = deck[k];
            deck[k] = temp;
        }
        this.deckOfCards = deck;
        this.cardLocation = 0;
        return deck;
    }
  
    flop(){
      this.cardsOnTable[0] = this.deal();
      this.cardsOnTable[1] = this.deal();
      this.cardsOnTable[2] = this.deal();
    }
  
    turn(){
      this.cardsOnTable[3] = this.deal();
    }
  
    river(){
      this.cardsOnTable[4] = this.deal();
    }
    display(){
      for (let i = 0; i < this.deckOfCards.length; i++){
        console.log(this.deckOfCards[i].name);
      }
    }
}

module.exports = {Dealer}