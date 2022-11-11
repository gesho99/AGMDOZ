package com.akarakoutev.agmdoz.services

import com.akarakoutev.agmdoz.core.ChatMessage
import com.akarakoutev.agmdoz.core.MessageType
import com.akarakoutev.agmdoz.core.Model
import com.akarakoutev.agmdoz.db.MessageRepo
import com.akarakoutev.agmdoz.db.ModelRepo
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.lang.Integer.parseInt
import java.lang.Long.parseLong
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

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

    fun runScript(workingDir: File, vararg command: String): String? {
        return try {
            val proc = ProcessBuilder(*command)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(60, TimeUnit.MINUTES)
            proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun evaluate(messageStr: String): Pair<MessageType, Double> {
        val resultJsonStr = runScript(File(MODEL_DIRECTORY), MODEL_SCRIPT_ENGINE, MODEL_FILE_NAME, messageStr)
        val resultJson = objectMapper.readTree(resultJsonStr)
        val maxValueElement = resultJson.fields().asSequence().reduce {
                a, b -> if (a.value.doubleValue() > b.value.doubleValue()) a else b
        }
        return Pair(MessageType.valueOf(maxValueElement.key.toString().uppercase()), maxValueElement.value.asDouble())
    }

    companion object {
        const val MESSAGE_BATCH_SIZE = 10
        private const val MODEL_DIRECTORY = "src/main/resources/ml"
        private const val MODEL_SCRIPT_ENGINE = "python"
        private const val MODEL_FILE_NAME = "evaluate.py"
    }
}