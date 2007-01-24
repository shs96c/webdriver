function Command() {
	// It's empty
}

Command.split = function(toSplit) {
	var toReturn = [];
	toSplit = toSplit.replace(/\n$/, '').replace(/\r$/, '');
	var index = toSplit.indexOf(" ");
	
	if (index == -1) {
		toReturn[0] = toSplit;
	} else {
		toReturn[0] = toSplit.substring(0, index);
		toReturn[1] = toSplit.substring(index + 1);
	}
	return toReturn;
}

function toScriptableInputStream(stream) {
    sstream = Components.classes["@mozilla.org/scriptableinputstream;1"];
    sstream = sstream.createInstance(Components.interfaces.nsIScriptableInputStream);
    sstream.init(stream);
    return sstream;
}

function WebDriverServer() {
    this.serverSocket = Components.classes["@mozilla.org/network/server-socket;1"];
    this.serverSocket = this.serverSocket.createInstance(Components.interfaces.nsIServerSocket);
}

WebDriverServer.prototype.startListening = function(port) {
    var listenOn = port || 7055;
    this.serverSocket.init(listenOn, true, -1);
    this.serverSocket.asyncListen(this);
}

WebDriverServer.prototype.onSocketAccepted = function(socket, transport)
{
    try
    {
        this.outstream = transport.openOutputStream(0, 0, 0);
        this.stream = transport.openInputStream(0, 0, 0);
        this.instream = toScriptableInputStream(this.stream);

        var driver = new FxDriver(this.outstream);
        var socketListener = new SocketListener(this.instream, driver);
        var pump = Components.classes["@mozilla.org/network/input-stream-pump;1"].createInstance(Components.interfaces.nsIInputStreamPump);
        pump.init(this.stream, -1, -1, 0, 0, false);
        pump.asyncRead(socketListener, null);
    } catch(ex2) {
        dump("::" + ex2);
    }
};

WebDriverServer.prototype.onStopListening = function(socket, status)
{
    this.stream.close();
};


WebDriverServer.prototype.close = function()
{
    this.instream.close();
}

WebDriverServer.prototype.sendResponse = function(response)
{
	dump("Send response called");
    var toSend = response + "\n";
    this.outstream.write(toSend, toSend.length);
    this.outstream.flush();
}

function SocketListener(inputStream, driver)
{
    this.inputStream = inputStream;
    this.driver = driver;
    this.data = "";
}

SocketListener.prototype.onStartRequest = function(request, context) 
{
}

SocketListener.prototype.onStopRequest = function(request, context, status)
{
}

SocketListener.prototype.onDataAvailable = function(request, context, inputStream, offset, count)
{
    this.data += this.inputStream.read(count);
    if (this.data.indexOf("\n") != -1)
    {
    	this.driver.handleData(this.data);
        this.data = "";
    }
}

window.addEventListener("load", function(e) {
    new WebDriverServer().startListening();
}, false); 
