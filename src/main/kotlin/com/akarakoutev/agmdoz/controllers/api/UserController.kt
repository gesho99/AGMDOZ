package com.akarakoutev.agmdoz.controllers.api

import com.akarakoutev.agmdoz.services.ChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/v1")
class UserController @Autowired constructor(val chatService: ChatService){

    @PostMapping("/chat")
    fun addChatMessage(@RequestBody messageJsonString: String): ResponseEntity<String> {
        try {
            chatService.add(messageJsonString)
        } catch (e: Exception) {
            // Not important for response
        }
        return ResponseEntity<String>("OK", HttpStatus.OK)
    }
}