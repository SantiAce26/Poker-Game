const {Dealer} = require("./Dealer");
const {Player} = require("./Player");

class GameManager {
  constructor() {
        this.games = [];
        this.players = [];
        
    }

  addGame(hostID, roomName) {
        let game = {
            host: hostID,
            room: roomName,
            dealer: new Dealer(),
            inProgress: 0,
            turn: 0,
            currentTurn: 0,
            playersInGame: [],
            handEnd: false,
          
        }
        this.games.push(game);
        return game
    };

  addPlayer(room, name, socketID) {
        const player = new Player(room, name, socketID);
        player.setChipSize(5000);
        this.players.push(player);
        return player;
    };

  removeGame(socketID) {
        var game = this.getGameByHost(socketID);

        if(game) {
            this.games = this.games.filter((game) => {
                return game.host != socketID;
            });
        };

        return game;
    };

  removePlayer(socketID) {
        var player = this.getPlayerBySocket(socketID);

        if(player) {
            this.players = this.players.filter((player) => {
                return player.id != socketID;
            });
        };

        return player;
    };

  removeFromRoom(room) {
        var removedPlayers = [];

        this.players = this.players.filter((player) => {
            if(player.room === room) {
                removedPlayers.push(player);
            } else {
                return player;
            };
        });

        return removedPlayers;
    };

  getFromRoom(room) {

        var players = this.players.filter((player) => {
            return player.room === room;
        });

        return players;
    };

  isHostOrPlayer(socketID) {
        if(this.getGameByHost(socketID) != undefined) {
            return "HOST";
        } else if(this.getPlayerBySocket(socketID) != undefined) {
            return "PLAYER";
        } else return "NOTFOUND";

    };

  getGameByHost(hostID) {
        return this.games.filter((game) => {
            return game.host === hostID;
        })[0];
    };

  getGameByRoom(roomName) {
        return this.games.filter((game) => {
            return game.room === roomName;
        })[0];
    };

  getPlayerBySocket(socketID) {
        return this.players.filter((player) => {
            return player.id === socketID;
        })[0];
    };

  checkUsername(room, username) {
        var players = this.getFromRoom(room);
        var available = true;

        players.filter((player) => {
            if(player.username === username) {
                available = false;
            };
        });
        
        return available;
    };

  checkRoomName(room) {
        var game = this.getGameByRoom(room);

        if(game) {
            return false;
        } else {
            return true;
        };

    };
  
  playerCheck(room)
  {
    var playersInGame = this.getFromRoom(room);
      if(playersInGame.length < 2 || playersInGame.length > 5)
      {
        console.log("there are this many players:", playersInGame.length);
          return false;
      }
    return true;
  }
  
  startGame(room){
      var thisGame = this.getGameByRoom(room)
      thisGame.playersInGame = this.getFromRoom(room)
      thisGame.inProgress = 1;
      thisGame.turn = 0;
      thisGame.currentTurn = 1;
  }
  
  gameSetup(){
    // Dealer location is set upon creating 
    var numPlayers = this.players.length;
    var hasSwapped = [numPlayers];
    for (let i = 0; i < numPlayers; i++)
      hasSwapped[i] = false;
    
    // Sets up player positions in the table
    // For even number of players, all will change from original pos
    // For odd number of players, one won't be swapped (can change if it matters)
    for (let i = 0; i < numPlayers; i++) {
      if (hasSwapped[i])
        continue;
      
      const temp = this.players[i];
      var rand = Math.floor(Math.random() * numPlayers);
      while(hasSwapped[rand])
        rand = (rand + 1) % numPlayers;
      
      this.players[i] = this.players[rand];
      this.players[rand] = temp;
      hasSwapped[i] = true;
      hasSwapped[rand]= true;
    } 
    
    // Hands each player the starting chips
    for (let i = 0; i < numPlayers; i++){
      this.players[i].setChipSize(5000);
    }
  }
  
  isTurn(room, socketID)
  {
    var game = this.getGameByRoom(room)
    console.log("checking is it is the turn of", game.turn)
    if(game.playersInGame[game.turn].id == socketID)
      return true;
    return false;
  }
  
    isHand(room, socketID)
  {
    var game = this.getGameByRoom(room)
    if(game.playersInGame[game.turn].isInHand)
      return true;
    return false;
  }
  
  nextTurn(room){
    var game = this.getGameByRoom(room)
    game.turn = game.currentTurn++ % game.playersInGame.length;
    return game.turn;  
  }
}

module.exports = {GameManager}