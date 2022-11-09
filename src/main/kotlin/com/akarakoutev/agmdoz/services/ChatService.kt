package com.akarakoutev.agmdoz.services

import com.akarakoutev.agmdoz.core.ChatMessage
import com.akarakoutev.agmdoz.core.MessageType
import com.akarakoutev.agmdoz.core.Model
import com.akarakoutev.agmdoz.db.MessageRepo
import com.akarakoutev.agmdoz.db.ModelRepo
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatService @Autowired constructor (val objectMapper: ObjectMapper, val messageRepo: MessageRepo, val modelRepo: ModelRepo) {
    fun getAll(): List<ChatMessage> {
        return messageRepo.findAll()
    }

    fun get(idStr: String): ChatMessage {
        val id = UUID.fromString(idStr)
        return messageRepo.findById(id).or {
            throw NoSuchElementException("This message does not exist")
        }.get()
    }

    fun delete(idStr: String) {
        val id = UUID.fromString(idStr)
        messageRepo.deleteAllById(listOf(id))
    }

    fun add(messageJsonString: String) {
        val message = objectMapper.readValue(messageJsonString, ChatMessage::class.java)
        message.type = evaluate(message)
        val model = getModel()
        message.modelVersion = model!!.version
        messageRepo.save(message)
    }

    // Model functions

    fun getModel(version: String? = null): Model? {
        return if (version == null) {
            modelRepo.findByOrderByVersion().firstOrNull()
        } else {
            modelRepo.findById(version).or {
                throw NoSuchElementException("This model version does not exist")
            }.get()
        }
    }

    fun getModels(): List<Model> {
        return modelRepo.findAll()
    }

    fun retrain() {
        TODO("Run retraining script")
    }

    fun evaluate(message: ChatMessage): Pair<MessageType, Float> {
        TODO("Get result from evaluation script")
    }
}