// Built-in HTTPS support
const https = require("https");
// Handling GET request (npm install express)
const express = require("express");
// Load of files from the local file system
var fs = require('fs'); 

const PORT = 4433;
const app = express();

// Get request for resource /
app.get("/", function (req, res) {
    console.log(
        req.socket.remoteAddress
        // + ' ' + req.socket.getPeerCertificate().subject.CN
        + ' ' + req.method
        + ' ' + req.url);
    res.send("<html><body>Secure Hello World with node.js</body></html>");
});


// configure TLS handshake
const options = {
    key: fs.readFileSync('../keys/secure-server-key-17nov.pem'),
    cert: fs.readFileSync('../keys/secure-server-17nov.pem') + fs.readFileSync('../keys/CA1-int.crt'),
    // ca: fs.readFileSync('../keys/certificates-keys/trust-anchors/CA1.cer'),
    /*ca: fs.readFileSync('../keys/CA2.crt'),
    requestCert: true, 
    rejectUnauthorized: true
    */
};

// Create HTTPS server
https.createServer(options, app).listen(PORT, 
    function (req, res) {
        console.log("Server started at port " + PORT);
        console.log("Access it here: https://www.secure-server.edu:4433")
    }
);
