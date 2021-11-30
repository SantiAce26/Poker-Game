class Player{
  
  constructor(room, name, socketID){
    this.username = name;
    this.id = socketID;
    this.room = room;
    this.chipSize = 5000;
    this.cards = [2];
    this.isInHand = true;
    this.moneyInPot = 0;
    this.moneyInRound = 0;
    this.handValue = 0;
  }
  
  getCards(){
    return this.cards;
  }
  
  setCards(newCards){
    this.cards[0] = newCards[0];
    this.cards[1] = newCards[1];
  }
  
  getChipSize(){
    return this.chipSize;
  }
  
  setChipSize(newChipSize){
    this.setChipSize = newChipSize;
  }
  
  getPlayerID(){
    return this.id;
  }
  
  setPlayerID(newPlayerID){
    this.id = newPlayerID;
  }
  
  getIsInHand(){
    return this.isInHand;
  }
  
  setIsInHand(newHandPos){
    this.isInhand = newHandPos;
  }
}

module.exports = {Player}