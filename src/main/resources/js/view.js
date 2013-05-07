function View() {}
View.prototype.addMessage = function(msg) {
    var container = document.getElementById('container');
    var txtElem = document.createElement('p');
    var txt = document.createTextNode(msg);

    txtElem.appendChild(txt);
    container.appendChild(txtElem);
};