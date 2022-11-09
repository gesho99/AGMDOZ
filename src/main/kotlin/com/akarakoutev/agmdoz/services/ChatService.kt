package com.akarakoutev.agmdoz.services

import com.akarakoutev.agmdoz.core.ChatMessage
import com.akarakoutev.agmdoz.core.MessageType
import com.akarakoutev.agmdoz.core.Model
import com.akarakoutev.agmdoz.db.MessageRepo
import com.akarakoutev.agmdoz.db.ModelRepo
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.lang.Integer.parseInt
import java.lang.Long.parseLong
import java.time.Instant
import java.util.*

@Service
class ChatService @Autowired constructor (val objectMapper: ObjectMapper, val messageRepo: MessageRepo, val modelRepo: ModelRepo) {
    fun getPageCount(): Long {
        return (messageRepo.count() / MESSAGE_BATCH_SIZE) + 1
    }

    fun getAllPaginated(page: Int): List<ChatMessage> {
        return messageRepo.findAllPaginated(PageRequest.of(page - 1, MESSAGE_BATCH_SIZE))
    }

    fun get(idStr: String): ChatMessage {
        return messageRepo.findById(parseLong(idStr)).or {
            throw NoSuchElementException("This message does not exist")
        }.get()
    }

    fun delete(idStr: String) {
        messageRepo.findById(parseLong(idStr)).ifPresent {
            messageRepo.delete(it)
        }
    }

    fun add(messageJsonString: String) {
        val message = objectMapper.readValue(messageJsonString, ChatMessage::class.java)
        message.type = evaluate(message.text)
        val model = getModel()
        message.modelVersion = model!!.version
        messageRepo.save(message)
    }

    // Model functions

    fun getModel(version: String? = null): Model? {
        return if (version == null) {
            modelRepo.findByOrderByVersionDesc().firstOrNull()
        } else {
            modelRepo.findById(version).or {
                throw NoSuchElementException("This model version does not exist")
            }.get()
        }
    }

    fun getModels(): List<Model> {
        return modelRepo.findAll()
    }

    fun retrain(): String {
        val lastVersion = modelRepo.findByOrderByVersionDesc().ifEmpty {
            listOf(Model("0.1", 1, Instant.now()))
        }.first().version

        val (major, minor) = lastVersion.split(".")
        val newVersion = "$major.${(parseInt(minor) + 1)}"

        val newModel = Model(newVersion, 1, Instant.now())
        modelRepo.save(newModel)
        return newModel.version
        //TODO("Run retraining script")
    }

    fun evaluate(messageStr: String): Pair<MessageType, Float> {
        return Pair(MessageType.values()[(Random().nextInt(MessageType.values().size))], Random().nextFloat())
        // TODO("Get result from evaluation script")
    }

    companion object {
        const val MESSAGE_BATCH_SIZE = 10
    }
}