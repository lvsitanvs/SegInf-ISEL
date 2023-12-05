const express = require('express')
const cookieParser = require('cookie-parser');
const axios = require('axios');
const FormData = require('form-data');// more info at:
// https://github.com/auth0/node-jsonwebtoken
// https://jwt.io/#libraries
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
const casbin = require('casbin');

const port = 3001

// TODO: Add secret to Envionment
const SECRET = {
    "web":{
        "client_id":"128385859912-6og9d0vd3601r8r5m6cg6u3d9liser18.apps.googleusercontent.com",
        "project_id":"geocell-359114",
        "auth_uri":"https://accounts.google.com/o/oauth2/auth",
        "token_uri":"https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs",
        "client_secret":"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"
    }
}

// CASBIN ----------------------------------------------------------------------------------
const enforcer = casbin.newEnforcer('./casbin/model.conf', './casbin/policy.csv');

// system variables where Client credentials are stored
//const CLIENT_ID = process.env.CLIENT_ID
const CLIENT_ID = SECRET.web.client_id
//const CLIENT_SECRET = process.env.CLIENT_SECRET
const CLIENT_SECRET = SECRET.web.client_secret
// callback URL configured during Client registration in OIDC provider
const CALLBACK = "callback"

const app = express()

app.use(cookieParser());
let appState = crypto.randomBytes(20).toString('hex');
/*app.use(session({
  secret: crypto.randomBytes(20).toString('hex'),
  state: appState
}));*/


app.get('/', (req, resp) => {
  resp.send('<a href=/login>Use Google Account</a>')
})

// More information at:
//      https://developers.google.com/identity/protocols/OpenIDConnect

app.get('/login', (req, resp) => {
    resp.redirect(302,
        // authorization endpoint
        'https://accounts.google.com/o/oauth2/v2/auth?'
        
        // client id
        + 'client_id='+ CLIENT_ID +'&'
        
        // OpenID scope "openid email"
        + 'scope=openid%20email&'
        
        // parameter state is used to check if the user-agent requesting login is the same making the request to the callback URL
        // more info at https://www.rfc-editor.org/rfc/rfc6749#section-10.12
        + 'state=' + appState + '&'
        
        // responde_type for "authorization code grant"
        + 'response_type=code&'
        
        // redirect uri used to register RP
        //+ 'redirect_uri=http://localhost:3001/'+CALLBACK)
        + 'redirect_uri=http://www.secure-server.edu:3001/'+CALLBACK)
})


app.get('/'+CALLBACK, (req, res) => {
    //
    // TODO: check if 'state' is correct for this session
    //
    const { code, state } = req.query;

    if (state !== appState) {
        res.status(403).send('Invalid state parameter');
        return;
    }


    console.log('making request to token endpoint')
    // content-type: application/x-www-form-urlencoded (URL-Encoded Forms)
    const form = new FormData();
    form.append('code', req.query.code);
    form.append('client_id', CLIENT_ID);
    form.append('client_secret', CLIENT_SECRET);
    //form.append('redirect_uri', 'http://localhost:3001/'+CALLBACK);
    form.append('redirect_uri', 'http://www.secure-server.edu:3001/'+CALLBACK);
    form.append('grant_type', 'authorization_code');
    //console.log(form);

    axios.post(
        // token endpoint
        'https://www.googleapis.com/oauth2/v3/token', 
        // body parameters in form url encoded
        form,
        { headers: form.getHeaders() }
      )
      .then(function (response) {
        // AXIOS assumes by default that response type is JSON: https://github.com/axios/axios#request-config
        // Property response.data should have the JSON response according to schema described here: https://openid.net/specs/openid-connect-core-1_0.html#TokenResponse

        console.log(response.data)
        // decode id_token from base64 encoding
        // note: method decode does not verify signature
        var jwt_payload = jwt.decode(response.data.id_token)
        console.log(jwt_payload)

        console.log(jwt_payload.email)

        // a simple cookie example
        res.cookie("DemoCookie", jwt_payload.email)
        // HTML response with the code and access token received from the authorization server
        res.send(
            '<div> callback with code = <code>' + req.query.code + '</code></div><br>' +
            '<div> client app received access code = <code>' + response.data.access_token + '</code></div><br>' +
            '<div> id_token = <code>' + response.data.id_token + '</code></div><br>' +
            '<div> Hi <b>' + jwt_payload.email + '</b> </div><br>' +
            'Go back to <a href="/">Home screen</a>'
        );
      })
      .catch(function (error) {
        console.log(error)
        res.send()
      });
})

app.get('/tasks', (req, res) => {
  // pdp - decide if requests is allowed or not
  const { email } = req.cookies;
  const pdp = async function(s, o, a){
    const enforcer = await casbin.newEnforcer('./casbin/model.conf', './casbin/policy.csv');
    r = await enforcer.enforce(s, o, a);
    return {res: r, sub: s, obj: o, act: a};
  }

  const execute = function(decision){
    console.log(decision);
    if(decision.res  !== true) {
      res.send('deny access');
    } else {
      res.send('allow access');
    } 
  }

  pdp(email, 'tasks', 'read').then((decision) => {
    execute(decision);
  });
  pdp(email, 'tasks', 'write').then((decision) => {
    execute(decision);
  });

  // TODO: Google Tasks API
  // TODO: GitHub API to milestones

})


app.listen(port, (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }
    console.log(`server is listening on ${port}`)
    console.log(`http://www.secure-server.edu:${port}/`)
})

