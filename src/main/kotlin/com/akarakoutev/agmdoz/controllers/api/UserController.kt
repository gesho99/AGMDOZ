package com.akarakoutev.agmdoz.controllers.api

import com.akarakoutev.agmdoz.services.ChatService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/v1")
class UserController @Autowired constructor(val chatService: ChatService){

    private final val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/chat")
    fun addChatMessage(@RequestBody messageJsonString: String): ResponseEntity<String> {
        try {
            chatService.add(messageJsonString)
        } catch (e: Exception) {
            logger.error("Error!", e)
            // Not important for response
        }
        return ResponseEntity<String>("OK", HttpStatus.OK)
    }
}