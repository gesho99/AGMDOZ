package com.akarakoutev.agmdoz.services

import com.akarakoutev.agmdoz.core.ChatMessage
import com.akarakoutev.agmdoz.core.MessageType
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException

@Service
class ChatService @Autowired constructor (val objectMapper: ObjectMapper) {
    fun getAll() {
        TODO("Get all from DB")
    }

    fun get(idStr: String) {
        val id = UUID.fromString(idStr)
        TODO("Get from DB")
        throw NoSuchElementException("This message does not exist")
    }

    fun delete(idStr: String) {
        val id = UUID.fromString(idStr)
        TODO("Delete from DB")
    }

    fun add(messageJsonString: String) {
        val message = objectMapper.readValue(messageJsonString, ChatMessage::class.java)
        message.type = evaluate(message)
        TODO("Save to DB")
    }

    // Model functions

    fun getModel(version: String) {
        TODO("Get model version from DB")
    }

    fun getModels() {
        TODO("Get all model versions from DB")
    }

    fun retrain() {
        TODO("Run retraining script")
    }

    fun evaluate(message: ChatMessage): Pair<MessageType, Float> {
        TODO("Get result from evaluation script")
    }
}