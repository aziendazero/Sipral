var express = require('express');
var http = require('http');
var fs = require('fs');
var path = require('path');
var bodyParser = require('body-parser');


var app = express();
var multer = require('multer');
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb', extended: true}));


var storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/')
      },
      filename: function (req, file, cb) {
        cb(null, file.originalname) 
      }
  });
  var upload = multer({ storage: storage });


app.get('/',function(req,res){
    res.sendFile("./upload.html",{root: __dirname});
  });

app.post('/protocollo',   upload.single('document'), function(req, res, next){
    
    console.log("Invocato il servizio di protocollo V2:");
    var jsonMessage = req.body;
    
    var protocolloRequest = jsonMessage["protocolloRequest"];
    var Applicazione = protocolloRequest["Applicazione"];
    var Ente = protocolloRequest["Ente"];
    var ULSS = protocolloRequest["ULSS"];
    var Username = protocolloRequest["Username"];
    var Password = protocolloRequest["Password"];
    var Oggetto = protocolloRequest["Oggetto"];
    var Destinatari = protocolloRequest["Destinatari"];
    var IdDocumento = protocolloRequest["IdDocumento"];
    var Contenuto = protocolloRequest["Contenuto"];
    var NomeFileContenuto = protocolloRequest["NomeFileContenuto"];
    var Note = protocolloRequest["Note"];
    var LineaDiLavoro = protocolloRequest["LineaDiLavoro"];

    console.log("Username: "+Username);
    console.log("Password: "+Password);
    console.log("IdDocumento: "+IdDocumento);
    console.log("NomeFileContenuto: "+NomeFileContenuto);
    console.log("LineaDiLavoro: "+LineaDiLavoro);

    res.setHeader('Content-Type', 'application/json');

    var jsonResponse = {};
    var protocolloResponse = {};
    jsonResponse.protocolloResponse = protocolloResponse;

    protocolloResponse.CodiceEsito = "000";
    protocolloResponse.DescrizioneEsito = "Operazione eseguita senza errori";
    protocolloResponse.IdDocumento = IdDocumento;
    protocolloResponse.NumeroProtocollo = getNextRegisterNumber();
    protocolloResponse.DataProtocollo = getTodayDate();

    console.log(JSON.stringify(jsonResponse));

    res.send(JSON.stringify(jsonResponse));


    console.log("--------------------------------------------------");
});

function getTodayDate(){

    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth()+1;
    
    var yyyy = today.getFullYear();
    if(dd<10){
        dd='0'+dd;
    } 
    if(mm<10){
        mm='0'+mm;
    } 
    var today = dd+'-'+mm+'-'+yyyy;
    return today;
}


function getNextRegisterNumber(){
    var mockRegister = JSON.parse(fs.readFileSync("./mockRegister.json", "utf8")); 
    var newRegisterNumber = mockRegister.n_registrer+1;
    console.log("numero di protocollo assegnato: "+newRegisterNumber);
     var newJson = {
        n_registrer: newRegisterNumber
    };
    fs.writeFile("./mockRegister.json", JSON.stringify(newJson), "utf8");
    return mockRegister.n_registrer;
}


app.post('/signature', function(req, res){
    
    console.log("Invocato il servizio di firma digitale:");
        
    
    var jsonMessage = req.body;
    
    var username = jsonMessage["signatureRequest"]["User"]
    var password = jsonMessage["signatureRequest"]["UserPWD"]
    var pin = jsonMessage["signatureRequest"]["OtpPWD"]
    var testo = jsonMessage["signatureRequest"]["Testo"]
    var page = jsonMessage["signatureRequest"]["Page"]

    console.log("username: "+username);
    console.log("password: "+password);
    console.log("pin: "+pin);
    console.log("page: "+page);
    console.log("testo: "+testo);

    var binaryInput = jsonMessage["signatureRequest"]["Binaryinput"];
    console.log("file ricevuto: " + binaryInput.substring(0,20)+"...");

    var base64signed = binaryInput;
    //var base64signed = base64_encode("firmato.pdf");
    console.log("file inviato: " + base64signed.substring(0,20)+"...");

    res.setHeader('Content-Type', 'application/json');
    
       
    var jsonResponse = {};
    var signatureResponse = {};
    jsonResponse.signatureResponse = signatureResponse;
    jsonResponse.signatureResponse.Status="OK";
    jsonResponse.signatureResponse.Description="OK";
    jsonResponse.signatureResponse.Binaryoutput=base64signed;
    res.send(JSON.stringify(jsonResponse));
    console.log("--------------------------------------------------");
  });




function base64_encode(file) {
    var bitmap = fs.readFileSync(file);
    return new Buffer(bitmap).toString('base64');
}


var server = app.listen(8081, function () {
  var host = server.address().address;
  var port = server.address().port;
  console.log("Servizio di firma digitale avviato su http://localhost:%s", port);
  console.log("----------------------------------------------------------------------------------");
});
