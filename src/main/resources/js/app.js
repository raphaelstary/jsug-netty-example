function App() {
}

App.prototype.run = function () {
    var view = new View();
    var socket = new Socket("localhost", 8088, view);

    var btn = document.getElementById('btn');
    var txt = document.getElementById('txt');

    btn.addEventListener('click',function (){
        socket.send(txt.value);
    }, false);

    socket.connect();
};