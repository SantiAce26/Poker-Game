const express = require('express');
const bodyParser = require('body-parser');


const socketio = require('socket.io')
var app = express();

const path = require('path');
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const port = process.env.PORT || 3000;

app.use(bodyParser.urlencoded({ extended: true }));

app.use(bodyParser.json());


server.listen(3000, () => {
  console.log('listening on *:3000');
});


io.on('connection', (socket) => {
    console.log(`Connection : SocketId = ${socket.id}`)

    var userName = "";
    var storedString = "nothing yet";

    socket.on('joinRoom', function(data) {
      console.log('joining room triggered')
      const room_data = JSON.parse(data)
      userName = room_data.userName;
      const roomName = room_data.roomName;

      socket.join(`${roomName}`)
      console.log(`Username : ${userName} joined Room Name : ${roomName}`)

      io.to(`${roomName}`).emit('newUserToRoom',userName);

    })

    socket.on('leaveRoom',function(data) {
        console.log('leave room triggered')
        const room_data = JSON.parse(data)
        userName = room_data.userName;
        const roomName = room_data.roomName;
    
        console.log(`Username : ${userName} leaved Room Name : ${roomName}`)
        socket.broadcast.to(`${roomName}`).emit('userLeftChatRoom',userName)
        socket.leave(`${roomName}`)
    })

    socket.on('store this',function(data) {
      console.log('storing this lmao')
      const room_data = JSON.parse(data)
      const storeString = room_data.storeString;
      const roomName = room_data.roomName;
  
      storedString = storeString;
  })

  socket.on('grab string', (arg) => {
    console.log('getting string')
    const room_data = arg;
    io.to(room_data).emit("refresh string", storedString)

})

    socket.on('disconnect', function () {
      console.log("One of sockets disconnected from our server.")
  });

    socket.on('updateCard', function(data) {
      const card_data = JSON.parse(data)
      const cardSelect = card_data.cardSelect;
      const roomName = card_data.roomName;
        console.log('card Updated')
        if(strcmp(cardSelect, "Left")==0){
          console.log('LEFT card SELECTED in room', roomName)
          io.to(roomName).emit("update card", "Left")
        }
        else{
          console.log('RIGHT card SELECTED')
          io.to(roomName).emit("update card", "Right")
        }
    })

    socket.on('testcard', (arg) => {
      console.log("this is " + arg);
  })

         
        
    })

    function strcmp ( str1, str2 ) {
      // http://kevin.vanzonneveld.net
      // +   original by: Waldo Malqui Silva
      // +      input by: Steve Hilder
      // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
      // +    revised by: gorthaur
      // *     example 1: strcmp( 'waldo', 'owald' );
      // *     returns 1: 1
      // *     example 2: strcmp( 'owald', 'waldo' );
      // *     returns 2: -1
  
      return ( ( str1 == str2 ) ? 0 : ( ( str1 > str2 ) ? 1 : -1 ) );
  }
