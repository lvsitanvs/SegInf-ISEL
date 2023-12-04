package isel.seginf.group3.tasks.http

import jakarta.servlet.http.Cookie
import org.springframework.web.bind.annotation.PostMapping
import isel.seginf.group3.tasks.domain.AuthenticatedUser
import isel.seginf.group3.tasks.http.model.UserLoginInputModel
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

class UsersController {
    @PostMapping(Uris.Users.LOGIN)
    fun login(
        @RequestBody inpuy: UserLoginInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        // TODO
        return ResponseEntity.status(200).build<Unit>()  // TODO, only for void error compilation
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(user: AuthenticatedUser) {
        // TODO
    }

    private fun createCookie(token: String): Cookie {
        val cookie = Cookie("token", token)
        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.maxAge = 3600
        cookie.path = "/"
        return cookie
    }
}
