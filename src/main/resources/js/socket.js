function Socket(url, port, view) {
    this.url = url;
    this.port = port;
    this.view = view;
}

Socket.prototype.connect = function() {
    this.connection = new WebSocket("ws://" + this.url + ":" + this.port + "/websocket");

    var self = this;
    this.connection.onmessage = function (event) {
        self.view.addMessage(event.data);
    }
};

Socket.prototype.send = function (msg) {
    this.connection.send(msg);
};