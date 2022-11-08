package com.akarakoutev.agmdoz.controllers.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/v1")
class UserController {

    @PostMapping("/chat")
    fun addChatMessage(@RequestBody chatMessageJson: String): String {
        TODO("Not yet implemented")
    }
}