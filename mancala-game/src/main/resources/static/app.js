var stompClient = null;
var conncected = null;
var sessionId = null;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
}

function connect() {
	enablePlayOnePits() 
	var socket = new SockJS('/play', [] , {
		sessionId: () => {
			return createAndGetUUID()
		}
	});
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);

		stompClient.subscribe('/user/queue/client', function(response) {

			var pitsStoneNumber = JSON.parse(response.body).pitsStoneNumber

			newPitStones(pitsStoneNumber)

			var winner = JSON.parse(response.body).winner
			if (winner != null) {
				if (winner == '1') {
					alert("Player One wins!");
					disconnect()
					return;
				} else if (winner == '2') {
					alert("Player Two wins!");
					disconnect()
					return;
				}else if(winner == '0'){
					alert("Draw!");
					disconnect()
					return;
				}
			}

			var nextPlayer = JSON.parse(response.body).nextPlayClient
			if (nextPlayer == '1')
				enablePlayOnePits()
			else if (nextPlayer == '2')
				enablePlayTwoPits()

		});
	});
	conncected = true
}

function disconnect() {

	if (stompClient !== null) {
		stompClient.disconnect();
	}

	setConnected(false);

	for (var i = 1; i <= 14; i++) {

		if (i == 7 || i == 14) {
			document.getElementById("pit_" + i).innerHTML = '0'
		} else {
			document.getElementById("pit_" + i).innerHTML = '6'
		}
	}

	disableAllPits()
}

function sendPit(pitNumber) {
	disableAllPits()
	stompClient.send("/game/action", {}, JSON.stringify({
		'pitNumber' : pitNumber
	}));
}

function disableAllPits() {
	for (var i = 1; i <= 14; i++) {
		document.getElementById("pit_" + i)
				.setAttribute("disabled", "disabled");
	}
}

function newPitStones(pitsStoneNumber) {
	for (var i = 1; i <= 14; i++) {
		var pitNewValue = pitsStoneNumber['pit_' + i];
		if (pitNewValue != undefined) {
			document.getElementById("pit_" + i).innerHTML = pitNewValue
		}
	}
}

function enablePlayOnePits() {
	for (var i = 1; i < 7; i++) {
		document.getElementById("pit_" + i).removeAttribute("disabled");
	}
	for (var i = 8; i < 14; i++) {
		document.getElementById("pit_" + i)
				.setAttribute("disabled", "disabled");
	}
}

function enablePlayTwoPits() {
	for (var i = 1; i < 7; i++) {
		document.getElementById("pit_" + i)
				.setAttribute("disabled", "disabled");
	}
	for (var i = 8; i < 14; i++) {
		document.getElementById("pit_" + i).removeAttribute("disabled");
	}
}

function createAndGetUUID(){
	var s = [];
	var hexDigits = "0123456789abcdef";
	for(var i=0;i<=35;i++){
		s[i] = hexDigits.substr(Math.floor(Math.random()* 0x10),1);
	}
	s[14] = "4";
	s[19] = hexDigits.substr((s[19] & 0x3) | 0x8,1);
	s[8] = s[13] = s[18] = s[23] = "-";
	var uuid = s.join("");
	return uuid;
}

$(function() {
	disableAllPits()

	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	$("#connect").click(function() {
		connect();
	});
	$("#disconnect").click(function() {
		disconnect();
	});

	$("#pit_1").click(function() {
		var disabledPit = document.getElementById("pit_1").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_1").innerHTML!=0)
			sendPit(1);
	});
	$("#pit_2").click(function() {
		var disabledPit = document.getElementById("pit_2").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_2").innerHTML!=0)
			sendPit(2);
	});
	$("#pit_3").click(function() {
		var disabledPit = document.getElementById("pit_3").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_3").innerHTML!=0)
			sendPit(3);
	});
	$("#pit_4").click(function() {
		var disabledPit = document.getElementById("pit_4").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_4").innerHTML!=0)
			sendPit(4);
	});
	$("#pit_5").click(function() {
		var disabledPit = document.getElementById("pit_5").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_5").innerHTML!=0)
			sendPit(5);
	});
	$("#pit_6").click(function() {
		var disabledPit = document.getElementById("pit_6").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_6").innerHTML!=0)
			sendPit(6);
	});
	$("#pit_8").click(function() {
		var disabledPit = document.getElementById("pit_8").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_8").innerHTML!=0)
			sendPit(8);
	});
	$("#pit_9").click(function() {
		var disabledPit = document.getElementById("pit_9").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_9").innerHTML!=0)
			sendPit(9);
	});
	$("#pit_10").click(function() {
		var disabledPit = document.getElementById("pit_10").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_10").innerHTML!=0)
			sendPit(10);
	});
	$("#pit_11").click(function() {
		var disabledPit = document.getElementById("pit_11").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_11").innerHTML!=0)
			sendPit(11);
	});
	$("#pit_12").click(function() {
		var disabledPit = document.getElementById("pit_12").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_12").innerHTML!=0)
			sendPit(12);
	});
	$("#pit_13").click(function() {
		var disabledPit = document.getElementById("pit_13").disabled;
		if (!disabledPit && conncected && document.getElementById("pit_13").innerHTML!=0)
			sendPit(13);
	});

});
