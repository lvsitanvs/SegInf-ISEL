const express = require('express')
const cookieParser = require('cookie-parser');
const axios = require('axios');
const FormData = require('form-data');// more info at:
// https://github.com/auth0/node-jsonwebtoken
// https://jwt.io/#libraries
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
const casbin = require('casbin');
const { access } = require('fs');

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
            '<div><a href="/tasks">Tasks</a></div>'            
        );
      })
      .catch(function (error) {
        console.log(error)
        res.send()
      });
})

let writeAccess = null;
let readAccess = null;

app.get('/tasks', async (req, res) => {
  console.log("TASKS DEBUG -------------------")
  // pdp - decide if requests is allowed or not
  const email = req.cookies.DemoCookie;
  console.log(email)
  const pdp = async function(s, o, a){
    const enforcer = await casbin.newEnforcer('./casbin/model.conf', './casbin/policy.csv');
    r = await enforcer.enforce(s, o, a);
    return {res: r, sub: s, obj: o, act: a};
  }

  const execute = function(decision){
    console.log(decision);
    //return decision.res
    if(decision.res  !== true) {
      if (decision.act == 'read') {
        res.send(decision.act + 'deny access')
      } else {
        writeAccess = 'deny access'
      }
    } else {
      if (decision.act == 'read') {
        readAccess = 'allow access'
      } else {
        writeAccess = 'allow access'
      }
    }
  }

  
  pdp(email, 'tasks', 'write').then((decision) => { execute(decision) })
  pdp(email, 'tasks', 'read').then((decision) => { execute(decision) });

  Promise.all([writeAccess, readAccess]).then(() => {
    if (writeAccess == 'allow access'){
      res.send(
        '<p>' + email + ' - Premium acount</p>' + 
        '<div><a href="/createtl">Create Task List</a></div>' +
        '<div><a href="/viewtl">View Task List</a></div>' +
        '</br>Go back to <a href="/">Home screen</a>'
      )
    } else {
      res.send(
        '<p>' + email + ' - Free acount</p>' + 
        '<div><a href="/viewtl">View Task List</a></div>' +  
        '</br>Go back to <a href="/">Home screen</a>'
      )
    }    
  });

  app.get('/createtl', (req, res) => {
    res.send(
      '<div>' +
        '<form action="https://tasks.googleapis.com/tasks/v1/users/@me/lists" method="POST">' +
          '<label for "createList">Create Task List </label>' +
          '<input type="text" id="createList" name="createList" placeholder="list name">' + 
          '<button onclick="/createTaskList/{createList}">Create</button></br>' +
        '</form></br>' + 
      '</div>' +
      '</br>Go back to <a href="/">Home screen</a>'
    )
  });

  app.post('/createTaskList/{createList}', (req, res) => {
    let taskId = 0; 
    
    function createTaskList() {
      const title = createList;
      taskId = taskId + 1;
      const etag = title.substring(0, 2);
      const now = new Date();
      const timestamp = now.toISOString();
      const link = "https://tasks.googleapis.com/tasks/v1/users/@me/lists/" + title;
      const id_token = document.getElementById("id_toke").value.split(" ")[2];
      console.log(id_token);
      const body = JSON.stringify({
        "kind": "tasks#taskList",
        "id": taskId,
        "etag": etag,
        "title": title,
        "updated": timestamp,
        "selfLink": link
      });
      
      const xhr = new XMLHttpRequest();
      xhr.open("POST", "https://tasks.googleapis.com/tasks/v1/users/@me/lists");
      xhr.setRequestHeader("Content-Type", "application/json");
      xhr.setRequestHeader("Authorization", "Bearer " + id_token);
      xhr.onload = function() {
        if (xhr.status === 200) {
          console.log(xhr.responseText);
        } else {
          console.error(xhr.statusText);
        }
      };
      xhr.onerror = function() {
        console.error(xhr.statusText);
      };
      xhr.send(body);
  }
  createTaskList()
})

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

// GOOGLE API FUNCTIONS -------------------------------------------------------------
/*
'<div>' +
                '<form action="https://tasks.googleapis.com/tasks/v1/users/@me/lists" method="POST">' +
                  '<label for "createList">Create Task List </label>' +
                  '<input type="text" id="createList" name="createList" placeholder="list name">' + 
                  '<button onclick="createTaskList()">Create</button></br>' +
                '</form></br>' + 
                '<form>' +
                  '<label for "viewList">View Task List </label>' +
                  '<input type="text" id="viewList" name="viewList" placeholder="list name">' + 
                  '<button>View</button></br>' +
                '</form></br>' + 
                '<form>' +
                  '<label for "addTask">Add Task to List </label>' +
                  '<input type="text" id="viewList2" name="viewList2" placeholder="list name">' + 
                  '<input type="text" id="addTask" name="addTask" placeholder="task name">' + 
                  '<button>Add</button></br>' +
                '</form></br>' + 
                '<form>' +
                  '<label for "viewTask">View Task </label>' +
                  '<input type="text" id="viewList3" name="viewList3" placeholder="list name">' + 
                  '<input type="text" id="viewTask" name="viewTask" placeholder="task name">' + 
                  '<button>View</button></br>' +
                '</form></br>' + 
              '<div/>' +   
              '</br>Go back to <a href="/">Home screen</a>' + 
              '<script>' +
               'let taskId = 0;' + 
               'function createTaskList() {' +
                 'const title = document.getElementById("createList").value;' +
                 'taskId = taskId + 1;' +
                 'const etag = title.substring(0, 2);' +
                 'const now = new Date();' +
                 'const timestamp = now.toISOString();' +
                 'const link = "https://tasks.googleapis.com/tasks/v1/users/@me/lists/" + title;' +
                 'const id_token = document.getElementById("id_toke").value.split(" ")[2];' +
                 'console.log(id_token);' +
                 'const body = JSON.stringify({' +
                   '"kind": "tasks#taskList",' +
                   '"id": taskId,' +
                   '"etag": etag,' +
                   '"title": title,' +
                   '"updated": timestamp,' +
                   '"selfLink": link' +
                 '});   ' +            
                 'const xhr = new XMLHttpRequest();' +
                 'xhr.open("POST", "https://tasks.googleapis.com/tasks/v1/users/@me/lists");' +
                 'xhr.setRequestHeader("Content-Type", "application/json");' +
                 'xhr.setRequestHeader("Authorization", "Bearer " + id_token);' +
                 'xhr.onload = function() {' +
                   'if (xhr.status === 200) {' +
                     'console.log(xhr.responseText);' +
                   '} else {' +
                     'console.error(xhr.statusText);' +
                   '}' +
                 '};' +
                 'xhr.onerror = function() {' +
                   'console.error(xhr.statusText);' +
                 '};' +
                 'xhr.send(body);' +
               '};' +
              '</script>'
*/
