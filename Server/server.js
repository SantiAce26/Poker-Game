
const express = require('express');
const bodyParser = require('body-parser');
const roomdata = require('roomdata');
const {GameManager} = require('./gameManager');
const {CardEvaluator} = require('./CardEvaluator');
const {Card} = require("./Card");



const socketio = require('socket.io')
var app = express();

const path = require('path');
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const port = process.env.PORT || 3000;
var games = new GameManager;

app.use(bodyParser.urlencoded({ extended: true }));

app.use(bodyParser.json());


server.listen(3000, () => {
  console.log('listening on *:3000');
});




// Need to make blinds pay up front and show on their screen why


var storedString = "nothing yet";
io.on('connection', (socket) => {
  io.sockets.setMaxListeners(0);
  
  
  console.log(`Connection : SocketId = ${socket.id}`)

  var userName = "";
   

  socket.on('joinRoom', function(data) {
    console.log('joining room triggered');
    const room_data = JSON.parse(data)
    userName = room_data.userName;
    const roomName = room_data.roomName;
    const intent = room_data.intent;
      
    if(intent == "create") {
      if(!games.checkRoomName(roomName)) {
        socket.emit("room already exists");
        return;
      } else {
        console.log("new room created");
        games.addGame(socket.id, roomName);
      }
    }
    if(intent == "join") {
      if(games.checkRoomName(roomName)) {
        console.log("room doesnt exist");
        socket.emit("room doesnt exist");
        return;
      }
    }
      
    if(games.getGameByRoom(roomName).inProgress == 1){
      socket.emit("room doesnt exist");
    }
      
    games.addPlayer(roomName, userName, socket.id)
    socket.join(`${roomName}`)
     
    console.log(`Username : ${userName} joined Room Name : ${roomName}`)
    socket.broadcast.to(`${roomName}`).emit('new player joined', userName)

    var playerList = games.getFromRoom(roomName);
    console.log(playerList[0].id);
    updatePlayers(playerList);
  })
     
  socket.on('leave room',function(data) {
    console.log('leave room triggered')
    const room_data = JSON.parse(data)
    userName = room_data.userName;
    const roomName = room_data.roomName;
    
        
    socket.leave(`${roomName}`);
    console.log(`Username : ${userName} left Room Name : ${roomName}`)
  })

  socket.on('start game', (roomName) => {
    console.log("game start requested")
    if(games.isHostOrPlayer(socket.id) != "HOST") {
      console.log("NOT HOST DONT START")
      socket.emit("error handler", "only host can start game");
      
      return;
    }
    
    if(!games.playerCheck(roomName)) {
      console.log("player count bad no start")
      socket.emit("error handler", "not enough players");
      socket.emit("player count bad")
      return;
    }
    
    console.log("starting game")
    
    io.to(`${roomName}`).emit("no start button");
    games.startGame(roomName);
    io.to(`${roomName}`).emit("check your turn");
  })
  
  socket.on('check turn', (arg) =>{
    var game = games.getGameByRoom(arg);
    
    
    if(game.dealer.phase == 0) {  
      console.log("SETUP");
    
      game.handEnd = false;
      
      //setup phase, updates blind
      game.dealer.shuffle();
      
      // Makes sure players who have no chips aren't in the hand
      for (let i = 0; i < game.playersInGame.length; i++) {
        // console.log("THis is " + i);
        if (game.playersInGame[i].chipSize == 0)
          game.playersInGame[i].isInHand = false;
        else 
          game.playersInGame[i].isInHand = true;
      }
      
      socket.emit('get your hand');
      socket.broadcast.to(arg).emit('get your hand');
      console.log(game.playersInGame);
      
      // This should change from phase to phase, talk about how to do 
      game.dealer.sbLoc = (game.dealer.sbLoc + 1) % game.playersInGame.length;
      
      // Updates small blind location
      var count = 0;
      while (game.playersInGame[game.dealer.sbLoc].chipSize == 0 && count < game.playersInGame.length) {
        game.dealer.sbLoc = (game.dealer.sbLoc + 1) % game.playersInGame.length;
        count++;
      }
 
      game.dealer.blindLoc = (game.dealer.sbLoc + 1) % game.playersInGame.length;
      
      // Updates big blind location
      count = 0;
      while (game.playersInGame[game.dealer.blindLoc].chipSize == 0 && count < game.playersInGame.length){
        game.dealer.blindLoc = (game.dealer.blindLoc + 1) % game.playersInGame.length;
        count++;
      }
        
      game.dealer.bettorLoc = (game.dealer.blindLoc + 1) % game.playersInGame.length;
      
      count = 0;
      while (game.playersInGame[game.dealer.bettorLoc].chipSize == 0 && count < game.playersInGame.length) {
          game.dealer.bettorLoc = (game.dealer.bettorLoc + 1) % game.playersInGame.length;
          count++;
      }
      var blindMoney = 200;
      
      
      console.log("Big blind is " + game.playersInGame[game.dealer.blindLoc].name);
      game.playersInGame[game.dealer.blindLoc].moneyInPot += blindMoney;
      game.playersInGame[game.dealer.blindLoc].moneyInRound += blindMoney;
      game.playersInGame[game.dealer.blindLoc].chipSize -= blindMoney;
      console.log("Big blind has this many chips" + game.playersInGame[game.dealer.blindLoc].chipSize);
      // Now update client side
      
      
      
      game.playersInGame[game.dealer.sbLoc].moneyInPot += blindMoney/2;
      game.playersInGame[game.dealer.sbLoc].moneyInRound += blindMoney/2;
      game.playersInGame[game.dealer.sbLoc].chipSize -= blindMoney/2;  
      // Now update client side
      
      game.dealer.pot += blindMoney + blindMoney/2;
      
      game.dealer.currentBet = 200;
      io.to(`${arg}`).emit('money update', game.playersInGame[game.dealer.blindLoc].username, game.playersInGame[game.dealer.blindLoc].chipSize, game.dealer.pot );
      io.to(`${arg}`).emit('money update', game.playersInGame[game.dealer.sbLoc].username, game.playersInGame[game.dealer.sbLoc].chipSize, game.dealer.pot );
      //socket.emit('self money update', game.playersInGame[game.dealer.blindLoc].chipSize);
      
      
      //socket.emit('money update', game.dealer.pot );
      io.to(arg).emit('status update', game.playersInGame[game.dealer.blindLoc].username, "Big Blind: " + blindMoney);
      io.to(arg).emit('status update', game.playersInGame[game.dealer.sbLoc].username, "Small Blind: " + blindMoney/2);
      
      console.log("ANTES HAVE HAPPENED");
      
      game.turn = game.dealer.bettorLoc
      game.currentTurn = games.getGameByRoom(arg).turn + 1;
      game.dealer.phase++;
      game.dealer.firstGo = 1;
     
      io.to(`${arg}`).emit("check your turn"); 
      return;
    }
    
    if(games.isTurn(arg, socket.id)) {
        if(games.isHand(arg, socket.id)) {
          console.log("it is the turn of", socket.id);
          socket.emit('your turn');
        } else {
          games.nextTurn(arg);
          io.to(`${arg}`).emit("check your turn");
        }
    } else {
        socket.emit('not your turn')
        return;
    }
    
    var thisRoundDealer = games.getGameByRoom(arg).dealer;
    if(thisRoundDealer.firstGo == 1) {
        //thisRoundDealer.firstGo = 0;
      console.log("it's the first go of ", games.getGameByRoom(arg).playersInGame[games.getGameByRoom(arg).turn].username)
    } else {
    
      if(thisRoundDealer.bettorLoc == games.getGameByRoom(arg).turn)
      {
        console.log("Turn has come back to last bettor, next phase");
        if(thisRoundDealer.phase == 1) {
          console.log("ENTERED PRE TURN");
          io.to(arg).emit('reset bet and check');
          resetRoundMoney(games.getGameByRoom(arg).playersInGame);
          thisRoundDealer.currentBet = 0;
          game.dealer.bettorLoc = thisRoundDealer.sbLoc;
          game.turn = thisRoundDealer.sbLoc;
          game.currentTurn = game.turn +1;
          thisRoundDealer.flop();
          thisRoundDealer.firstGo = 1;
          io.to(arg).emit('display flop', thisRoundDealer.cardsOnTable[0].displayVal, thisRoundDealer.cardsOnTable[1].displayVal, thisRoundDealer.cardsOnTable[2].displayVal)
          thisRoundDealer.phase++;
          
          if (game.handEnd){
            console.log("OUT OF PHASE 1");
            game.dealer.phase = 4;
            for (let i = 0; i < game.playersInGame.length; i++){
              if (game.playersInGame[i].isInHand){
                game.dealer.bettorLoc = i;
                break;
              }
            }
            game.turn = game.dealer.bettorLoc;
            game.currentTurn = game.turn +1;
            game.dealer.firstGo = 0;
          }
          console.log("WE ABOUT TO GO TO PHASE " + game.dealer.phase);
          io.to(`${arg}`).emit("check your turn"); 
          
          return;
          //end of round 1, flop and proceed  to 2
        } else if(thisRoundDealer.phase == 2) {
          console.log("ENTERED PRE RIVER");
          io.to(arg).emit('reset bet and check');
          thisRoundDealer.currentBet = 0;
          resetRoundMoney(games.getGameByRoom(arg).playersInGame);
          game.dealer.bettorLoc = thisRoundDealer.sbLoc;
          game.turn = thisRoundDealer.sbLoc;
          game.currentTurn = game.turn +1;
          game.currentTurn = game.turn +1;
          
          thisRoundDealer.turn()
          io.to(arg).emit('display turn',thisRoundDealer.cardsOnTable[3].displayVal)
          thisRoundDealer.firstGo = 1;
          thisRoundDealer.phase++;
          
          if (game.handEnd){
            game.dealer.phase = 4;
            for (let i = 0; i < game.playersInGame.length; i++){
              if (game.playersInGame[i].isInHand){
                game.dealer.bettorLoc = i;
                break;
              }
            }
            game.turn = game.dealer.bettorLoc;
            game.currentTurn = game.turn +1;
            game.dealer.firstGo = 0;
          }
          
          console.log("WE ABOUT TO GO TO PHASE " + game.dealer.phase);
          io.to(arg).emit('check your turn');
          return;
            //end of round 2, turn and proceed  to 3;
        } else if(thisRoundDealer.phase == 3) {
          console.log("ENTERED POST RIVER, LAST ROUND OF BETTING");
          io.to(arg).emit('reset bet and check');
          thisRoundDealer.currentBet = 0;
          game.dealer.bettorLoc = thisRoundDealer.sbLoc;
          game.turn = thisRoundDealer.sbLoc;
          game.currentTurn = game.turn +1;
          resetRoundMoney(games.getGameByRoom(arg).playersInGame);
          thisRoundDealer.river()
          thisRoundDealer.firstGo = 1;
          io.to(arg).emit('display river',thisRoundDealer.cardsOnTable[4].displayVal)
          thisRoundDealer.phase++;
          if (game.handEnd){
            // Show all 5 cards
            game.dealer.phase = 4
            for (let i = 0; i < game.playersInGame.length; i++){
              if (game.playersInGame[i].isInHand){
                game.dealer.bettorLoc = i;
                break;
              }
            }
            game.turn = game.dealer.bettorLoc;
            game.currentTurn = game.turn +1;
            game.dealer.firstGo = 0;
          }
          
          
          console.log("WE ABOUT TO GO TO PHASE " + game.dealer.phase);
          io.to(arg).emit('check your turn');
          return;
          //end of round 3, river and proceed  to 4
        } else if(thisRoundDealer.phase == 4) {
          
          console.log('ENTERED ROUND END');
          var playersInHand = [];
          var playersInPot = [];
          resetRoundMoney(games.getGameByRoom(arg).playersInGame);
          for (let i = 0; i < game.playersInGame.length; i++) {
            if (game.playersInGame[i].isInHand)
              playersInHand.push(game.playersInGame[i]);
            if (game.playersInGame[i].moneyInPot != 0)
              playersInPot.push(game.playersInGame[i]);
          }
          
            
          var handValues = [];
          var cardEval = new CardEvaluator();
            
          // Find the value of each player's hand, numerically
          if (playersInHand.length == 1){
            playersInHand[0].handValue = 20000000;
          } else {
            for(let i = 0; i < playersInHand.length ; i++) {
              var card = [playersInHand[i].cards[0], playersInHand[i].cards[1], thisRoundDealer.cardsOnTable[0], thisRoundDealer.cardsOnTable[1],thisRoundDealer.cardsOnTable[2],thisRoundDealer.cardsOnTable[3],thisRoundDealer.cardsOnTable[4]];
              console.log("About to evaluate " + playersInHand[i].username);
              playersInHand[i].handValue = cardEval.evaluateCards(card);
              handValues.push(playersInHand[i].handValue);
            }
          }
          
            
          // How to divy pot
          // People should be paid by whoever has best hand first.
          // If player A has less money contribution than player B but has a better hand, he should get paid first.
          // Then, people who still have money contribution are thus in a side pot and able to pay to the next best hand.
          // If player B has a better hand than all other players and covers their contributions, he should get all of the pots.
          var playersByHand = sortByHandValue(playersInHand);
          var pHandLen = playersByHand.length;
          var pPotLen = playersInPot.length;
            
          var tieID = [];
          var beenPaid = [];
          for (let i = pHandLen - 1; i >= 0; i--) {
            var earnings = 0;
            // Skip people who have already been paid due to ties
            if (beenPaid.includes((playersByHand[i].id)))
              continue;
            for (let j = 0; j < pPotLen; j++) {
              if (playersInPot[j].id == playersByHand[i].id) // cant give money to self
                continue;
              // Skip people who have no more money in pot
              if (playersInPot[j].moneyInPot == 0)
                continue;
              // If two players tie, they should not give money to each other
              if (playersInPot[j].handValue == playersByHand[i].handValue &&
                  playersInPot[j].id != playersByHand[i].id) {
                // console.log("TIE BETWEEN " + playersInPot[j].id + " and " + playersByHand[i].id);
                tieID.push(playersInPot[j].id);
                continue;
              }
              // Player with weaker hand has more pot contribution
              if (playersInPot[j].moneyInPot >= playersByHand[i].moneyInPot) {
                // console.log(playersByHand[i].id +" is getting " + playersByHand[i].moneyInPot + " chips from " + playersInPot[j].id + "(" + playersByHand[i].handValue + " v.s. " + playersInPot[j].handValue+ ")");
                earnings += playersByHand[i].moneyInPot; // Pay out all of winner's pot contribution from this player
                game.dealer.pot -= playersByHand[i].moneyInPot; // Subtract the money given to this winning player from the total pot
                playersInPot[j].moneyInPot -= playersByHand[i].moneyInPot; // Subtract the amount given to winner from loser's contribution to pot
              } else {
                // console.log(playersByHand[i].id +" is getting " + playersInPot[j].moneyInPot + " chips from " + playersInPot[j].id + "(" + playersByHand[i].handValue + " v.s. " + playersInPot[j].handValue+ ")");
                earnings += playersInPot[j].moneyInPot; // Give as much as possible from loser to player
                game.dealer.pot -= playersInPot[j].moneyInPot; // Subtract this amount from total pot
                playersInPot[j].moneyInPot = 0; // Player is covered, so has no more money in pot
              }
            }
              
            // console.log("Player " + playersByHand[i].id + " has earned" + earnings + " chips");
            // Once a player is paid out by other players, make sure they get their money back
            if (tieID.length > 0) {
              // console.log("Player " + playersByHand[i].id + " earnings will now be divided with the following players due to a tie");
                  
              // console.log(tieID);
              var dividedEarnings = Math.floor(earnings / (tieID.length + 1)); // +1 cause current person also tied
              // console.log("Divided earnings" + dividedEarnings);
              for (let j = 0; j < pPotLen; j++) {
                if(tieID.includes(playersInPot[j].id)) {
                  // console.log(playersInPot[j].id);
                  playersInPot[j].chipSize += playersInPot[j].moneyInPot + dividedEarnings;
                  playersInPot[j].moneyInPot = 0;
                  io.to(`${arg}`).emit('money update', playersInPot[j].username, playersInPot[j].chipSize, 0 );
                  beenPaid.push(playersInPot[j].id);
                  tieID = tieID.filter(function(e) {return e !== playersInPot[j].id});
                }
              }
              
              playersByHand[i].chipSize += playersByHand[i].moneyInPot + dividedEarnings;
            } else {
              playersByHand[i].chipSize += playersByHand[i].moneyInPot + earnings;
              io.to(`${arg}`).emit('money update', playersByHand[i].username, playersByHand[i].chipSize, 0 );
              beenPaid.push(playersByHand[i].id);
            }
                
            playersByHand[i].moneyInPot = 0;
          }
          
          game.dealer.pot = 0;
            
          // check that individual earnings add up to pot
          var total = 0;
          var max = -1;
          var maxPlayer;
          for (let i = 0; i < playersByHand.length; i++){
            console.log(playersByHand[i].username + " has hand value " + playersByHand[i].handValue + " finished with " + playersByHand[i].chipSize + " chips.");
            total += playersByHand[i].chipSize;
            if (playersByHand[i].handValue > max){
              max = playersByHand[i].handValue;
              maxPlayer = playersByHand[i].username;
            }
          }
          console.log("WINNER IS " + maxPlayer);
          console.log("TOTAL" + total);
          socket.emit('hand win', maxPlayer);
          // Now, make sure all players who have no chips are not in hand
          for (let i = 0; i < game.playersInGame.length; i++)
            if (game.playersInGame[i].chipSize == 0) {
              game.playersInGame[i].isInHand = false;
            }
               
          //thisRoundDealer.phase = 1
          //final Winner check
          var winList = [];
          for(let i = 0 ; i < game.playersInGame.length ; i++)
            if(game.playersInGame[i].chipSize > 0)
              winList.push(game.playersInGame[i]);
          if(winList.length < 2) {
            io.to(arg).emit('winner found', winList[0].username);
            return;
          }
            
          
          thisRoundDealer.phase = 0;
          io.to(`${arg}`).emit("check your turn");
          io.to(`${arg}`).emit("hide your cards");
          return;
          //end round 
        }
      }
    }
    socket.on('bet', doBet)
    socket.on('call', doCall)
    socket.on('fold', doFold)
       function doBet(data) {
       socket.off('call', doCall)
      socket.off('fold', doFold)
         socket.off('bet', doBet)
      const room_data = JSON.parse(data);
      const roomName = room_data.roomName;
      var betValue = parseInt(room_data.betVal);
      
      var game = games.getGameByRoom(roomName);
      
      var maxMoneyInPot = game.playersInGame[game.dealer.bettorLoc].moneyInPot;
      var totalBet = betValue + game.dealer.currentBet
      var betLoss = totalBet - game.playersInGame[game.turn].moneyInRound;
      
      if (games.isTurn(roomName, socket.id)) {
        console.log("RAISING ")
        console.log('bet/raise ')
        if (game.playersInGame[game.turn].chipSize < betLoss) {
          console.log("IN THIS ")
          socket.emit('status update', game.playersInGame[game.turn].username, "Max bet/raise: " + (game.playersInGame[game.turn].chipSize + game.playersInGame[game.turn].moneyInRound -  game.dealer.currentBet) );
          socket.emit("not enough money");
          io.to(`${roomName}`).emit('check your turn');
          return;
        } else console.log("OUT THIS ");
        
        if (game.dealer.currentBet > totalBet) {
          socket.emit('status update', games.findGameByRoom(roomName).playersInGame[game.turn], "bet too low");
          io.to(`${roomName}`).emit('check your turn');
         
          return;
        }
          
        game.playersInGame[game.turn].moneyInPot += betLoss;
        game.playersInGame[game.turn].moneyInRound += betLoss;
        game.dealer.currentBet = totalBet;
        game.dealer.pot += betLoss;
        game.dealer.bettorLoc = game.turn;
        game.playersInGame[game.turn].chipSize = game.playersInGame[game.turn].chipSize - betLoss;
        io.to(`${roomName}`).emit('money update', game.playersInGame[game.turn].username, game.playersInGame[game.turn].chipSize, games.getGameByRoom(roomName).dealer.pot );
          //socket.emit('self money update', game.playersInGame[game.turn].chipSize);
        socket.broadcast.to(`${roomName}`).emit('status update', game.playersInGame[game.turn].username, "Bet " + totalBet );
        games.nextTurn(roomName);
        game.dealer.firstGo = 0;
        io.to(`${roomName}`).emit('check your turn');
      }
      
      socket.emit('not your turn');
    }
    
       function doFold(roomName){
      socket.off('call', doCall)
      socket.off('bet', doBet)
      socket.off('fold', doFold)
      if(games.isTurn(roomName, socket.id)) {
        if(!(game.playersInGame[game.turn].chipSize == 0 && game.playersInGame[game.turn].moneyInPot > 0)){
        console.log('player folding');
          
        socket.emit("player folding");
        socket.broadcast.to(`${roomName}`).emit('status update', game.playersInGame[game.turn].username, "Folded");
        
        
        games.getGameByRoom(roomName).playersInGame[games.getGameByRoom(roomName).turn].isInHand = false;
          games.nextTurn(roomName);
        //game.dealer.firstGo = 0;
        
        var count = 0;
        while(!game.playersInGame[game.dealer.bettorLoc].isInHand && count <= game.playersInGame.length){
          console.log("Player folded updating bettor location");
          game.dealer.bettorLoc = (game.dealer.bettorLoc + 1) % game.playersInGame.length;
          count++;
        }
        
          console.log("Player" + game.playersInGame[game.dealer.bettorLoc].username + "is in hand" + game.playersInGame[game.dealer.bettorLoc].isInHand);
          console.log("Count is " + count <= game.playersInGame.length);
        
        
        var playersInHand = game.playersInGame.length;
        for (let i = 0; i < game.playersInGame.length; i++){
          if (!game.playersInGame[i].isInHand)
            playersInHand--;
        }
        
        if (playersInHand == 1){
          console.log("HAND END BE")
          game.handEnd = true;
        }
      }
        io.to(`${roomName}`).emit('check your turn');
      }
      socket.emit('not your turn');
    }

    
    function doCall(roomName) {
      socket.off('fold', doFold)
      socket.off('bet', doBet)
      socket.off('call', doCall)
      if (games.isTurn(roomName, socket.id)) {
        var game = games.getGameByRoom(roomName);
        var callMoney;
        console.log('call/check ')
        // all in
        if (game.playersInGame[game.turn].chipsSize < game.dealer.currentBet) {
          io.to(`${roomName}`).emit("player going all in");
          io.to(`${roomName}`).emit('check your turn');
          game.dealer.pot += game.playersInGame[game.turn].chipSize;
          game.playersInGame[game.turn].moneyInPot += game.playersInGame[game.turn].chipSize;
          game.playersInGame[game.turn].moneyInRound += game.playersInGame[game.turn].chipSize;
          game.playersInGame[game.turn].chipSize = 0;
          io.to(`${roomName}`).emit('money update', game.playersInGame[game.turn].username, game.playersInGame[game.turn].chipSize, games.getGameByRoom(roomName).dealer.pot );
          games.nextTurn(roomName);
          game.dealer.firstGo = 0;
          io.to(`${roomName}`).emit('check your turn')
          return;
        }

        if (game.dealer.currentBet != 0) {
          callMoney = game.dealer.currentBet - game.playersInGame[game.turn].moneyInRound;
          console.log("This is call money " + callMoney);
          game.dealer.pot += callMoney;
          game.playersInGame[game.turn].chipSize = game.playersInGame[game.turn].chipSize - callMoney;
          game.playersInGame[game.turn].moneyInPot += callMoney;
          game.playersInGame[game.turn].moneyInRound += callMoney;
              
          io.to(`${roomName}`).emit('money update', game.playersInGame[game.turn].username, game.playersInGame[game.turn].chipSize, games.getGameByRoom(roomName).dealer.pot );
          //socket.emit('self money update', game.playersInGame[game.turn].chipSize);
          socket.broadcast.to(`${roomName}`).emit('status update', game.playersInGame[game.turn].username, "Call " + game.dealer.currentBet );
        } else {
          socket.broadcast.to(`${roomName}`).emit('status update', game.playersInGame[game.turn].username, "Check" );
        }
          
        games.nextTurn(roomName);
        game.dealer.firstGo = 0;
        io.to(`${roomName}`).emit('check your turn');
      }
      
      socket.emit('not your turn');
    }
    
    /*socket.on('get money values', roomName=>{
      var play = games.getPlayerBySocket(socket.id);
      console.log('updating yo vals you got a pot of' + games.getGameByRoom(roomName).dealer.pot + "chipsize of" + play.chipSize);
      socket.emit('pot update', games.getGameByRoom(roomName).dealer.pot, play.chipSize)
      updatePlayers(games.getFromRoom(roomName))
      
    })*/
    
    
  })

  socket.on('draw cards', (roomName) => {
    var thisPlayer = games.getPlayerBySocket(socket.id);
    if (! thisPlayer.isInHand)
      return;
    thisPlayer.cards[0] = games.getGameByRoom(roomName).dealer.deal();
    thisPlayer.cards[1] = games.getGameByRoom(roomName).dealer.deal();
    //console.log(thisPlayer.cards[0]);
    //console.log(thisPlayer.cards[1]);
    socket.emit('display hand', thisPlayer.cards[0].displayVal, thisPlayer.cards[1].displayVal)
  })
  
  socket.on('update players', (roomName) => {
    var playerList = games.getFromRoom(roomName);
    updatePlayers(playerList);
  })
  
  socket.on('disconnect', () => {
    
    console.log("One of sockets disconnected from our server.")
    var discPlayer = games.getPlayerBySocket(socket.id)
    if(discPlayer != undefined) {
      var roomName = discPlayer.room
   
      if(games.isHostOrPlayer(socket.id) == "HOST") { 
        console.log("host left");
        games.removeGame(socket.id);
        socket.broadcast.to(`${roomName}`).emit('hostDisconnected',userName) 
      }
      // console.log('removing player with id', socket.id)
      games.removePlayer(socket.id);
      var playerList = games.getFromRoom(roomName);
      var usernameList = [];
      for(let i = 0 ; i < playerList.length ; i++) {
        usernameList = playerList[i].username;
      }
      console.log(usernameList)
    }
    socket.broadcast.to(`${roomName}`).emit('user disconnected', usernameList)
  });
  
  function updatePlayers(pList) {
    socket.emit('reset players')
    let i = 0;
    while(pList[i].id != socket.id) i++;
  
    i = (i+1) % pList.length;
    var j = 1;
    while(pList[i].id != socket.id){
      console.log('updating player ' + pList[i].username + " they got this many chips" + pList[i].chipSize);
      socket.emit('update player'+j, pList[i].username, pList[i].chipSize.toString()) 
      j++
      i = (i+1) % pList.length;
    }
  }  
  function resetRoundMoney(pList)
  {
    for(let i = 0 ; i < pList.length ; i++)
      pList[i].moneyInRound = 0;
  }
  
  function sortByHandValue(players) {
      var i, j, min_j;
      for (i = 0; i < players.length; i ++ ) {
        min_j = i; 
        for (j = i+1; j < players.length; j++ )
          if (players[j].handValue < players[min_j].handValue)
            min_j = j;      
        var help = players[i];
        players[i] = players[min_j];
        players[min_j] = help;
      }
      return players;
    }

})
