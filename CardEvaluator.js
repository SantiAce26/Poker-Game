const {Card} = require("./Card");
class CardEvaluator {
  constructor() {
       console.log("Making card evaluator");
  }
  
  // Takes in set of 7 cards and returns the value of the best hand in it
  evaluateCards(cards) {
    console.log("HERE CARDS" + cards[0].value);
    var possibleSetsOf5 = this.getGroupsOf5(cards);
    
    var bestVal = -1; 
    
    for (let i = 0; i < possibleSetsOf5.length; i++){
      
      var currVal = this.evaluateSetOf5(possibleSetsOf5[i]);
      console.log("Evaluating cards")
      
      if (currVal > bestVal)
        bestVal = currVal;
    }
    
    return bestVal;
  }
  
  
  // Returns the value of a hand(5 cards). Checks for best hands first, then goes down
  // the list of hand values so as to avoid checking one set of cards more than necessary
  evaluateSetOf5(fiveCards) {
    
    if (this.isFlush(fiveCards) && this.isStraight(fiveCards))
      return this.valueStraightFlush(fiveCards);
    else if (this.is4s(fiveCards))
      return this.valueFourOfAkind(fiveCards);
    else if (this.isFullHouse(fiveCards))
      return this.valueFullHouse(fiveCards);
    else if (this.isFlush(fiveCards))
      return this.valueFlush(fiveCards);
    else if (this.isStraight(fiveCards))
      return this.valueStraight(fiveCards);
    else if (this.is3s(fiveCards))
      return this.value3s(fiveCards);
    else if (this.isTwoPair(fiveCards))
      return this.valueTwoPair(fiveCards);
    else if (this.isPair(fiveCards))
      return this.valuePair(fiveCards);
    else
      return this.valueHighCard(fiveCards);
  }
  
  // Checks if five cards make a flush
  isFlush(fiveCards){
    this.sortBySuit(fiveCards);
    return (fiveCards[0].suit == fiveCards[4].suit);
  }
  
  // Checks if five cards make a straight
  isStraight(fiveCards){  
    // First we check for an ace
    
    fiveCards = this.sortByValue(fiveCards);
    
    if (fiveCards[4].value == 14) {
      var a = fiveCards[0].value == 2 && fiveCards[1].value == 3 && fiveCards[2].value == 4 && fiveCards[3].value == 5;
      var b = fiveCards[0].value == 10 && fiveCards[1].value == 11 && fiveCards[2].value == 12 && fiveCards[3].value == 13;
      return (a||b);
    } else {
      for (let i = 0; i < 4; i++){
        if (fiveCards[i].value != fiveCards[i+1].value)
          return false;
      }
      
      return true;
    }
    
  }
  
  // Checks if five cards make 4s
  is4s(fiveCards){
    fiveCards = this.sortByValue(fiveCards);
    var a = fiveCards[0].value == fiveCards[1].value &&
            fiveCards[1].value == fiveCards[2].value &&
            fiveCards[2].value == fiveCards[3].value;
    
    var b = fiveCards[1].value == fiveCards[2].value &&
            fiveCards[2].value == fiveCards[3].value &&
            fiveCards[3].value == fiveCards[4].value;
    
    return (a||b);
  }
  
  // Checks if five cards make a full house
  isFullHouse(fiveCards){
    fiveCards = this.sortByValue(fiveCards);
    var a = fiveCards[0].value == fiveCards[1].value &&
            fiveCards[1].value == fiveCards[2].value &&
            fiveCards[3].value == fiveCards[4].value;
    
    var b = fiveCards[0].value == fiveCards[1].value &&
            fiveCards[2].value == fiveCards[3].value &&
            fiveCards[3].value == fiveCards[4].value;
    
    return (a || b);
  }
  
  // Checks if five cards make 3s
  is3s(fiveCards){
    if (this.is4s(fiveCards) || this.isFullHouse(fiveCards))
      return false;
    
    fiveCards = this.sortByValue(fiveCards);
    
    var a = fiveCards[0].value == fiveCards[1].value &&
            fiveCards[1].value == fiveCards[2].value;
    
    var b = fiveCards[1].value == fiveCards[2].value &&
            fiveCards[2].value == fiveCards[3].value;
    
    var c = fiveCards[2].value == fiveCards[3].value &&
            fiveCards[3].value == fiveCards[4].value;
    
    return (a || b || c);
  }
  
  // Checks if five cards make a two pair
  isTwoPair(fiveCards) {
    if (this.is4s(fiveCards) || this.isFullHouse(fiveCards) || this.is3s(fiveCards))
      return false;
    
    fiveCards = this.sortByValue(fiveCards);
    
    var a = fiveCards[0].value == fiveCards[1].value &&
            fiveCards[2].value == fiveCards[3].value;
    
    var b = fiveCards[0].value == fiveCards[1].value &&
            fiveCards[3].value == fiveCards[4].value;
    
    var c = fiveCards[1].value == fiveCards[2].value &&
            fiveCards[3].value == fiveCards[4].value;
    
    return (a || b || c);
  }
  
  // Checks if five cards make a pair
  isPair(fiveCards) {
    if (this.is4s(fiveCards) || this.isFullHouse(fiveCards) || this.is3s(fiveCards) || this.isTwoPair(fiveCards))
      return false;
    
    var a = fiveCards[0].value == fiveCards[1].value;
    var b = fiveCards[1].value == fiveCards[2].value;
    var c = fiveCards[2].value == fiveCards[3].value;
    var d = fiveCards[3].value == fiveCards[4].value;
    
    return (a || b || c || d);
  }
  
  valueStraightFlush(fiveCards) {
    console.log("Calculating straight flush value");
    return 8000000 + this.valueHighCard(fiveCards);
  }
  
  valueFourOfAKind(fiveCards) {
        console.log("Calculating 4 of a kind value");

    fiveCards = this.sortByValue(fiveCards);
    return 7000000 + fiveCards[2].value;
  }
  
  valueFullHouse(fiveCards) {
        console.log("Calculating full house values");

    fiveCards = this.sortByValue(fiveCards);
    return 6000000 + fiveCards[2].value;
  }
  
  valueFlush(fiveCards) {
        console.log("Calculating flush values");

    return 5000000 + this.valueHighCard(fiveCards);
  }
  
  valueStraight(fiveCards) {
        console.log("Calculating straight values");

    return 4000000 + this.valueHighCard(fiveCards);
  }
  
  value3s(fiveCards) {
        console.log("Calculating 3s values");

    fiveCards = this.sortByValue(fiveCards);
    return 3000000 + fiveCards[2].value;
  }
  
  valueTwoPair(fiveCards){
    console.log("Calculating twopair values");

    fiveCards = this.sortByValue(fiveCards);
    if (fiveCards[0].value == fiveCards[1].value && fiveCards[2].value == fiveCards[3].value)
      return 2000000 + 14*14 * fiveCards[2].value + 14 * fiveCards[0].value + fiveCards[4].value;
    else if (fiveCards[0].value == fiveCards[1].value && fiveCards[3].value == fiveCards[4].value)
      return 2000000 + 14*14 * fiveCards[3].value + 14 * fiveCards[0].value + fiveCards[2].value;
    else
      return 2000000 + 14*14 * fiveCards[3].value + 14 * fiveCards[1].value + fiveCards[0].value;
  }
  
  // Calculates the value of a hand assuming it is a hand with a single pair
  // To accomodate for the different ranking of hands, each ranking has 1000000 added to it compared to the previous ranking.
  valuePair(fiveCards){
    console.log("Calculating pairs");
    fiveCards = this.sortByValue(fiveCards);
    if (fiveCards[0].value == fiveCards[1].value)
      return 1000000 + 14*14*14 * fiveCards[0].value + 14*14 * fiveCards[4].value + 14 * fiveCards[3].value + fiveCards[2].value;
    else if (fiveCards[1].value == fiveCards[2].value)
      return 1000000 + 14*14*14 * fiveCards[1].value + 14*14 * fiveCards[4].value + 14 * fiveCards[3].value + fiveCards[0].value;
    else if (fiveCards[2].value == fiveCards[3].value)
      return 1000000 + 14*14*14 * fiveCards[2].value + 14*14 * fiveCards[4].value + 14 * fiveCards[1].value + fiveCards[0].value;
    else
      return 1000000 + 14*14*14 * fiveCards[3].value + 14*14 * fiveCards[2].value + 14 * fiveCards[1].value + fiveCards[0].value;
  }
  
  
  // Calculates the value of a hand assuming it is a high card hand
  valueHighCard(fiveCards) {
    console.log("Calculating high cards value");
    fiveCards = this.sortByValue(fiveCards);
    console.log("These are the five cards" + fiveCards);
    var val = 0;
    var multiplier = 1;
    for (let i = 0; i < 5; i++) {
      val += fiveCards[i].value * multiplier;
      multiplier *= 14;
    }
    console.log("The value of this highCards value is " + val)
    return val;
  }
  
  // There has to be a better way to do this but my brain didn't want to figure it out rn
  // Takes in 7 cards and returns the different possible groups of 5 
  // The length of the array returned by this should ALWAYS be 21
  getGroupsOf5(cards){
    var fiveCards = [];
    
    for (let i = 0; i < 21; i++)
      fiveCards.push([]);
      
    fiveCards[0].push(cards[0]);
    fiveCards[0].push(cards[1]);
    fiveCards[0].push(cards[2]);
    fiveCards[0].push(cards[3]);
    fiveCards[0].push(cards[4]);
    
      
    fiveCards[1].push(cards[0]);
    fiveCards[1].push(cards[1]);
    fiveCards[1].push(cards[2]);
    fiveCards[1].push(cards[3]);
    fiveCards[1].push(cards[5]);
      
    fiveCards[2].push(cards[0]);
    fiveCards[2].push(cards[1]);
    fiveCards[2].push(cards[2]);
    fiveCards[2].push(cards[3]);
    fiveCards[2].push(cards[6]);
      
    fiveCards[3].push(cards[0]);
    fiveCards[3].push(cards[1]);
    fiveCards[3].push(cards[2]);
    fiveCards[3].push(cards[4]);
    fiveCards[3].push(cards[5]);
      
    fiveCards[4].push(cards[0]);
    fiveCards[4].push(cards[1]);
    fiveCards[4].push(cards[2]);
    fiveCards[4].push(cards[4]);
    fiveCards[4].push(cards[6]);
      
    fiveCards[5].push(cards[0]);
    fiveCards[5].push(cards[1]);
    fiveCards[5].push(cards[2]);
    fiveCards[5].push(cards[5]);
    fiveCards[5].push(cards[6]);
      
    fiveCards[6].push(cards[0]);
    fiveCards[6].push(cards[1]);
    fiveCards[6].push(cards[3]);
    fiveCards[6].push(cards[4]);
    fiveCards[6].push(cards[5]);
      
    fiveCards[7].push(cards[0]);
    fiveCards[7].push(cards[1]);
    fiveCards[7].push(cards[3]);
    fiveCards[7].push(cards[4]);
    fiveCards[7].push(cards[6]);
    
    fiveCards[8].push(cards[0]);
    fiveCards[8].push(cards[1]);
    fiveCards[8].push(cards[3]);
    fiveCards[8].push(cards[5]);
    fiveCards[8].push(cards[6]);
      
    fiveCards[9].push(cards[0]);
    fiveCards[9].push(cards[1]);
    fiveCards[9].push(cards[4]);
    fiveCards[9].push(cards[5]);
    fiveCards[9].push(cards[6]);
  
    fiveCards[10].push(cards[0]);
    fiveCards[10].push(cards[2]);
    fiveCards[10].push(cards[3]);
    fiveCards[10].push(cards[4]);
    fiveCards[10].push(cards[5]);
      
    fiveCards[11].push(cards[0]);
    fiveCards[11].push(cards[2]);
    fiveCards[11].push(cards[3]);
    fiveCards[11].push(cards[4]);
    fiveCards[11].push(cards[6]);
      
    fiveCards[12].push(cards[0]);
    fiveCards[12].push(cards[2]);
    fiveCards[12].push(cards[3]);
    fiveCards[12].push(cards[5]);
    fiveCards[12].push(cards[6]);
      
    fiveCards[13].push(cards[0]);
    fiveCards[13].push(cards[2]);
    fiveCards[13].push(cards[4]);
    fiveCards[13].push(cards[5]);
    fiveCards[13].push(cards[6]);
      
    fiveCards[14].push(cards[0]);
    fiveCards[14].push(cards[3]);
    fiveCards[14].push(cards[4]);
    fiveCards[14].push(cards[5]);
    fiveCards[14].push(cards[6]);
      
    fiveCards[15].push(cards[1]);
    fiveCards[15].push(cards[2]);
    fiveCards[15].push(cards[3]);
    fiveCards[15].push(cards[4]);
    fiveCards[15].push(cards[5]);
      
    fiveCards[16].push(cards[1]);
    fiveCards[16].push(cards[2]);
    fiveCards[16].push(cards[3]);
    fiveCards[16].push(cards[4]);
    fiveCards[16].push(cards[6]);
      
    fiveCards[17].push(cards[1]);
    fiveCards[17].push(cards[2]);
    fiveCards[17].push(cards[3]);
    fiveCards[17].push(cards[5]);
    fiveCards[17].push(cards[6]);
      
    fiveCards[18].push(cards[1]);
    fiveCards[18].push(cards[2]);
    fiveCards[18].push(cards[4]);
    fiveCards[18].push(cards[5]);
    fiveCards[18].push(cards[6]);
      
    fiveCards[19].push(cards[1]);
    fiveCards[19].push(cards[3]);
    fiveCards[19].push(cards[4]);
    fiveCards[19].push(cards[5]);
    fiveCards[19].push(cards[6]);

    fiveCards[20].push(cards[2]);
    fiveCards[20].push(cards[3]);
    fiveCards[20].push(cards[4]);
    fiveCards[20].push(cards[5]);
    fiveCards[20].push(cards[6]);
      
    return fiveCards;
  }

sortBySuit(cardArray)
{
       var i, j, min_j;


      for ( i = 0 ; i < cardArray.length ; i ++ )
      {
         min_j = i; 
         for ( j = i+1 ; j < cardArray.length ; j++ )
         {
            if ( cardArray[j].suit < cardArray[min_j].suit )
            {
               min_j = j;      
            }
         }

         var help = cardArray[i];
         cardArray[i] = cardArray[min_j];
         cardArray[min_j] = help;
      }
  
  return cardArray;
}
  
  
sortByValue(cardArray)
{
       var i, j, min_j;


      for ( i = 0 ; i < cardArray.length ; i ++ )
      {
         min_j = i; 
         for ( j = i+1 ; j < cardArray.length ; j++ )
         {
            if ( cardArray[j].value < cardArray[min_j].value )
            {
               min_j = j;      
            }
         }

         var help = cardArray[i];
         cardArray[i] = cardArray[min_j];
         cardArray[min_j] = help;
      }
  
  return cardArray;
}
  
}

module.exports = {CardEvaluator}