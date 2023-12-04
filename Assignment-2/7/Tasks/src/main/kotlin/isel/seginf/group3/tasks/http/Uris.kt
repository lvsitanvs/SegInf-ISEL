package isel.seginf.group3.tasks.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    const val PREFIX = "/"
    const val HOME = PREFIX

    fun home() : URI = URI(HOME)

    object Users {
        const val USERS = "$PREFIX/users/"
        const val LOGIN = "$PREFIX/users/login"
        const val LOGOUT = "$PREFIX/users/logout"
        const val GET_BY_ID = "$PREFIX/users/{id}"   // Not sure if its necessary, we use Oauth, so we know always the user
    }

    object Tasks {
        const val TASKS = "$PREFIX/tasks/"
        const val TASK = "/tasks/{id}"
        fun byId(id: Int) = UriTemplate(TASKS).expand(id)
    }
}