var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/chat-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        var chatId = $("#chat").val();
        console.log('Connected: ' + frame + 'chat id ' + chatId);

        stompClient.subscribe('/user/chat/' + chatId, function (chatLine) {
            var parse = JSON.parse(chatLine.body);
            showGreeting(parse.user.email, parse.content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    var chatId = $("#chat").val();
    console.log('Send to: chat id ' + chatId);
    stompClient.send("/app/" + chatId, {}, JSON.stringify({'content': $("#name").val()}));
}

function showGreeting(from, message) {
    $("#greetings").append("<tr><td><strong>" + from + "<strong></td><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
});

