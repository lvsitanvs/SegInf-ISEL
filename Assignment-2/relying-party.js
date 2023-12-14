const express = require('express')
const https = require('https')
const cookieParser = require('cookie-parser');
const axios = require('axios');
const FormData = require('form-data');// more info at:
// https://github.com/auth0/node-jsonwebtoken
// https://jwt.io/#libraries
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
let fs = require('fs');
const {newEnforcer} = require("casbin");
let ROLES
const port = 3001
const COOKIE_GOOGLE = 'google'
const COOKIE_TASKS = 'tasks'
const COOKIE_STATE = 'state'
const COOKIE_ROLE = 'role'
const STATE = 'state'
// TODO: Add secret to Environment
const SECRET = {
    web: {
        client_id: "128385859912-6og9d0vd3601r8r5m6cg6u3d9liser18.apps.googleusercontent.com",
        project_id: "geocell-359114",
        auth_uri: "https://accounts.google.com/o/oauth2/auth",
        token_uri: "https://oauth2.googleapis.com/token",
        auth_provider_x509_cert_url: "https://www.googleapis.com/oauth2/v1/certs",
        client_secret: "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"
    }
}

// system variables where Client credentials are stored
//const CLIENT_ID = process.env.CLIENT_ID
const CLIENT_ID = SECRET.web.client_id
//const CLIENT_SECRET = process.env.CLIENT_SECRET
const CLIENT_SECRET = SECRET.web.client_secret
// callback URL configured during Client registration in OIDC provider
const CALLBACK = "callback"

const app = express()

app.use(cookieParser());
app.use(express.urlencoded({extended: true}));
const appState = crypto.randomBytes(20).toString('hex');

// TODO: GitHub API to milestones


// More information at:
//  https://developers.google.com/identity/protocols/OpenIDConnect

app.get('/task', async (req, res) => {
    const tasks_cookie = req.cookies.tasks
    console.log(tasks_cookie)
    const taskListId = "MTQyNjU4NjY4NzYwNjY2NjY2NjY2"
    const taskId = "MTQyNjU4NjY4NzYwNjY2NjY2NjY2"
    if (tasks_cookie == null) {
        res.redirect(302, '/login')
    } else {
        try {
            const response = await axios.get(`https://tasks.googleapis.com/tasks/v1/lists/${taskListId}/tasks/${taskId}`,
                {
                    headers: {
                        'Authorization': ` Bearer ${tasks_cookie}`,
                        'Accept': 'application/json'
                    }
                })
            const task = response.data
            const body = `
            <html lang=en>
            <div>  <h1>Task</h1>  </div>
            <table>
            <tr>
            <th>id</th>
            <th>Title</th>
            <th>Updated</th>
            <th>Due</th>
            <th>Completed</th>
            </tr>
            <tr>
            <td>${task.id}</td>
            <td>${task.title}</td>
            <td>${task.updated.substring(0, 10)}</td>
            <td>${task.due}</td>
            <td>${task.completed}</td>
            </tr>
            </table>
            <a href="/home">Go back to Home screen</a>
            </html>
            `
            res.statusCode = 200
            res.send(body)
        } catch (error) {
            console.log(error)
            res.redirect(302, '/')
        }
    }
})

app.get('/tasksList', async (req, res) => {
    const tasks_cookie = req.cookies.tasks
    //const jwt_payload_google = req.cookies.tasks
    //console.log(jwt_payload_google)
    console.log(tasks_cookie)
    const response = await axios.get('https://tasks.googleapis.com/tasks/v1/users/@me/lists', {
            headers: {
                'Authorization': `Bearer ${tasks_cookie}`,
                'Accept': 'application/json'
            }
        })
    const data = await response.data
    console.log(data)
    const body = `
    <html lang=en>
    <div>  <h1>Task Lists</h1>  </div>
    <table> 
    <tr>
    <th>id</th>
    <th>Title</th>
    <th>Updated</th>
    </tr>
    ${data.items.map(item => {
        return `<tr>
        <td>${item.id}</td>
        <td>${item.title}</td>
        <td>${item.updated.substring(0, 10)}</td>
        </tr> `
    })}
    </table>
    <a href="/home">Go back to Home screen</a>
    </html>
    `
    res.statusCode = 200
    res.send(body)
})

app.get('/formAddTask', async (req, res) => {
    const tasks_cookie = req.cookies.tasks
    console.log(tasks_cookie)
    if (tasks_cookie == null) {
        res.redirect(302, '/login')
    } else {
        const body = `
        <html lang=en>
        <form action="/addTask" method="POST">
        <label for="title">Title</label>
        <input type="text" id="title" name="title" placeholder="title">
        <label for="state">State: open or closed</label>
        <input type="text" id="state" name="state" placeholder="state">
        <label for="description">Discription</label>
        <input type="text" id="description" name="description" placeholder="description">
        <label for="due_on">Due Date</label>
        <input type="datetime-local" id="due_on" name="due_on" placeholder="due_on" value="2021-01-01T00:00">
        <input type="submit" value="Submit">
        </form>
        </html>`
        res.statusCode = 200
        res.send(body)

    }

})

app.post('/addTask', async (req, res) => {
    const tasks_cookie = req.cookies.tasks
    const listName = req.body.viewList2
    console.log(tasks_cookie)
    const response = await axios.post('https://tasks.googleapis.com/tasks/v1/lists/' + listName + '/tasks',
        {
            title: req.body.title,
            notes: req.body.description,
            due: new Date(req.body.due_on).toISOString()
        },
        {
            headers: {
                'Authorization': `Bearer ${tasks_cookie}`,
                'Accept': 'application/json'
            }
        }
    )
    if(response.status === 200){
        res.redirect(302, '/tasksList')
    } else {
        res.redirect(302, '/home')
    }
})

app.get('/', (req, resp) => {
    resp.send('<a href=/login>Use Google Account</a>')
})

app.get('/' + CALLBACK, async (req, res) => {
    const code = req.query.code
    // content-type: application/x-www-form-urlencoded (URL-Encoded Forms)
    const form = new FormData();
    form.append('client_id', CLIENT_ID);
    form.append('client_secret', CLIENT_SECRET);
    form.append('code', code);
    form.append('state', appState);
    form.append('redirect_uri', 'https://www.secure-server.edu:3001/' + CALLBACK);
    form.append('grant_type', 'authorization_code');
    try {

        const staResult = req.cookies.state === appState ? req.cookies.state : null
        if (staResult === null) {
            return res.redirect(302, '/')
        }

        const response = await axios.post(
            'https://oauth2.googleapis.com/token',
            form,
            {
                headers: form.getHeaders()
            }
        )
        const access_token = response.data.access_token
        const id_token = response.data.id_token

        const role = req.cookies.role

        if (role != null && (role === 'free' || role === 'premium' || role === 'admin')) {
            res.redirect(302, '/home')
        } else {
            res.cookie(COOKIE_GOOGLE, id_token, {httpOnly: true, secure: true})
            res.cookie(COOKIE_TASKS, access_token, {httpOnly: true, secure: true})
            res.redirect(302, '/getAccessTasks')
        }
    } catch (error) {
       // console.log(error)
        res.redirect(302, '/')
    }
})

app.get('/getAccessTasks', async (req, res) => {
    const google_cookie = req.cookies.google
    const jwt_payload_google = jwt.decode(google_cookie)
    const email = jwt_payload_google.email
    let scope = ''
    const role = await getRole(email)
    scope = role !== 'free' ? 'https://www.googleapis.com/auth/tasks' : 'https://www.googleapis.com/auth/tasks.readonly'
    res.cookie(COOKIE_ROLE, role, {httpOnly: true, secure: true})
    console.log(scope)
    res.redirect(302,
        'https://accounts.google.com/o/oauth2/v2/auth?' +
        'client_id=' + CLIENT_ID + '&' +
        'scope=' + scope + '&' +
        'response_type=code&' +
        'redirect_uri=https://www.secure-server.edu:3001/' + CALLBACK)
});

app.get('/login', (req, resp) => {
    resp.cookie(COOKIE_STATE, appState, {httpOnly: true, secure: true})
    resp.redirect(302,
        // authorization endpoint
        'https://accounts.google.com/o/oauth2/v2/auth?'

        // client id
        + 'client_id=' + CLIENT_ID + '&'

        // OpenID scope "openid email profile"
        + 'scope=openid%20profile%20email&'

        // parameter state is used to check if the user-agent requesting login is the same making the request to the callback URL
        // more info at https://www.rfc-editor.org/rfc/rfc6749#section-10.12
        + 'state=' + appState + '&'

        // responde_type for "authorization code grant"
        + 'response_type=code&'

        // redirect uri used to register RP
        //+ 'redirect_uri=http://localhost:3001/'+CALLBACK)
        + 'redirect_uri=https://www.secure-server.edu:3001/' + CALLBACK)
})

app.get('/home', async (req, resp) => {
    const g_cookie = req.cookies.google
    if (g_cookie == null) {
        resp.redirect(302, '/')
    } else {
        const jwt_payload_google = jwt.decode(g_cookie)
        const email = jwt_payload_google.email
        const role = await getRole(email)
        let options = ''
        switch (role) {
            case 'admin':
                options = '<a href=/createtl>Create Task List</a></br>' +
                    '<a href=/tasksList>View Task List</a></br>' +
                    '<a href=/formAddTask>Add Task to List</a></br>' +
                    '<a href=/viewtask>View Task</a></br>' +
                    '<a href=/logout>Logout</a>'
                break;
            case 'premium':
                options = '<a href=/createtl>Create Task List</a></br>' +
                    '<a href=/tasksList>View Task List</a></br>' +
                    '<a href=/formAddTask>Add Task to List</a></br>' +
                    '<a href=/viewtask>View Task</a></br>' +
                    '<a href=/logout>Logout</a>'
                break;
            case 'free':
                options = '<a href=/tasksList>View Task List</a></br>' +
                    '<a href=/viewtask>View Task</a></br>' +
                    '<a href=/logout>Logout</a>'
                break;

        }
        resp.send(`
              <html lang=en>
                <div>  <h1>Welcome ${email}</h1>  </div>
                <div> <img src=${jwt_payload_google.picture} alt="user picture"> </div>
                <div>  <h2>Role: ${role}</h2>  </div>
                <div>  <h3>Options:</h3>  </div>
                <div>  ${options}  </div>
                </html>
              `)
    }
})

app.get('/logout', (req, resp) => {
    resp.clearCookie(COOKIE_GOOGLE)
    resp.clearCookie(COOKIE_TASKS)
    resp.clearCookie(COOKIE_STATE)
    resp.clearCookie(COOKIE_ROLE)
    resp.redirect(302, '/')
})

app.get('/createtl', (req, res) => {
    const form = new FormData()
    form.append('<label for "createList">Create Task List </label>')
    form.append('<input type="text" id="createList" name="createList" placeholder="list name">')
    form.append('<input type="text" id="createList" name="createList" placeholder="list name">')
    res.send(
        form +
        '<div>' +
        '<form action="https://tasks.googleapis.com/tasks/v1/users/@me/lists" method="POST">' +
        '<label for "createList">Create Task List </label>' +
        '<input type="text" id="createList" name="createList" placeholder="list name">' +
        c +
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
        xhr.onload = function () {
            if (xhr.status === 200) {
                console.log(xhr.responseText);
            } else {
                console.error(xhr.statusText);
            }
        };
        xhr.onerror = function () {
            console.error(xhr.statusText);
        };
        xhr.send(body);
    }

    createTaskList()
})

async function getRole(owner) {
    return (await ROLES.getRolesForUser(owner))[0].trim()
}

const option = {
    key: fs.readFileSync('../6/keys/secure-server-key-17nov.pem'),
    cert: fs.readFileSync('../6/keys/secure-server-17nov.pem') + fs.readFileSync('../6/keys/CA1-int.crt'),
    requestCert: false,
    rejectUnauthorized: false
}

https.createServer(option, app).listen(port, async (err) => {
    if (err) {
        return console.log('something bad happened', err)
    }
    ROLES = await newEnforcer("./casbin/model.conf", "./casbin/policy.csv")
    console.log(`server is listening on ${port}`)
    console.log(`https://www.secure-server.edu:${port}/`)
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
