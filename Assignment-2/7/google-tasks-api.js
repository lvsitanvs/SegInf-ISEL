
const uri = {
    base: "https://tasks.googleapis.com",
    tasklist: {
        one:    "/tasks/v1/users/@me/lists/{tasklist}",        // DELETE, GET, PATCH, UPDATE
        all:    "/tasks/v1/users/@me/lists",                   // POST, GET
    },
    tasks: {
        clear:  "/tasks/v1/lists/{tasklist}/clear",             // POST
        delete: "/tasks/v1/lists/{tasklist}/tasks/{task}",      // DELETE
        get:    "/tasks/v1/lists/{tasklist}/tasks/{task}",      // GET
        insert: "/tasks/v1/lists/{tasklist}/tasks",             // POST
        list:   "/tasks/v1/lists/{tasklist}/tasks",             // GET
        move:   "/tasks/v1/lists/{tasklist}/tasks/{task}/move", // POST
        patch:  "/tasks/v1/lists/{tasklist}/tasks/{task}",      // PATCH
        update: "/tasks/v1/lists/{tasklist}/tasks/{task}",      // PUT
    }
}

export const taskListJSON = {
    "kind": "string: tasks#taskList",
    "id": "string: TaskList identifier",
    "etag": "string: ETag of the resourse",
    "title": "string: Title",
    "updated": "string: Last modification time (as a RFC 3339 timestamp)",
    "selfLink": "string: URL pointing to this taskList"
}

export const taskJSON = {
    "kind": "string: tasks#task",
    "id": "string: Task Identifier",
    "etag": "string: ETag of the resource",
    "title": "string: Title",
    "updated": "string: Last modification time (as a RFC 3339 timestamp)",
    "selfLink": "string: URL pointing to this Task",
    "parent": "string: Parent task identifier. This field is omitted if it is a top-level task. This field is read-only. Use the MOVE method to move the task under a different parent or to the top level.",
    "position": "string: Position of the task among its sibling tasks under the same parent task or at the top level. If this string is greater than another task's corresponding position string according to lexicographical ordering, the task is positioned after the other task under the same parent task (or at the top level). This field is read-only. Use the MOVE method to move the task to another position.",
    "notes": "string: Notes (optional)",
    "status": "string: needsAction or completed",
    "due": "string: Due date of the task (as a RFC 3339 timestamp). Optional.",
    "completed": "string: Completion date of the task (as a RFC 3339 timestamp). This field is omitted if the task has not been completed.",
    "deleted": "boolean: Flag indicating whether the task has been deleted. The default is False.",
    "hidden": "boolean Flag indicating whether the task is hidden. This is the case if the task had been marked completed when the task list was last cleared. The default is False. This field is read-only.",
    "links": [
      {
        "type": "string: Collection of links. This collection is read-only.",
        "description": "string: The description. In HTML speak: Everything between <a> and </a>.",
        "link": "string: the URL"
      }
    ]
  }

  function getListOfTaskList(){ return uri.base + uri.tasklist.all }

  function getTaskList(taskList){ return uri.base + uri.tasklist.all + "/" + taskList }