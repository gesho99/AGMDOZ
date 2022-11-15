package com.akarakoutev.agmdoz.services

import com.akarakoutev.agmdoz.core.ChatMessage
import com.akarakoutev.agmdoz.core.MessageType
import com.akarakoutev.agmdoz.db.MessageRepo
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Long.parseLong
import java.nio.file.Files
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


@Service
class ChatService @Autowired constructor (val objectMapper: ObjectMapper, val messageRepo: MessageRepo) {

    companion object {
        private val logger = LoggerFactory.getLogger(ChatService::class.java)

        const val MESSAGE_BATCH_SIZE = 10
        private const val MODEL_DIRECTORY = "src/main/resources/ml"
        private const val MODEL_SCRIPT_ENGINE = "python"
        private const val EVAL_MODEL_FILE_NAME = "evaluate.py"
        private const val TRANSCRIBE_MODEL_FILE_NAME = "transcribe.py"
        private const val PUNCTUATE_MODEL_FILE_NAME = "punctuate.py"
        private const val VISUALIZE_SCRIPT_FILE_NAME = "visualize.py"
    }

    fun getPageCount(): Long {
        return (messageRepo.count() / MESSAGE_BATCH_SIZE) + 1
    }

    fun getAllPaginated(page: Int): List<ChatMessage> {
        return messageRepo.findAllPaginated(PageRequest.of(page - 1, MESSAGE_BATCH_SIZE))
    }

    fun getByChatId(chatId: String): List<ChatMessage> {
        return messageRepo.findByChatId(chatId)
    }

    fun delete(idStr: String) {
        messageRepo.findById(parseLong(idStr)).ifPresent {
            messageRepo.delete(it)
        }
    }

    fun generateChatMoodChart(idStr: String){
        val messages = getByChatId(idStr)
        visualise(UUID.fromString(idStr), messages.map {
            val evaluationsMap = objectMapper.readTree(it.evaluationsJson!!)
            listOf(
                evaluationsMap[MessageType.POSITIVE.name].asDouble(),
                evaluationsMap[MessageType.NEUTRAL.name].asDouble(),
                evaluationsMap[MessageType.NEGATIVE.name].asDouble()
            )
        }.flatten())
    }

    fun add(messageJsonString: String) {
        val message = objectMapper.readValue(messageJsonString, ChatMessage::class.java)
        message.modelVersion = "1"
        message.ts = Instant.now()
        thread {
            message.text.split(".").filter { it.isNotEmpty() }.forEach {
                messageRepo.save(ChatMessage(null, message.chatId, message.userId, message.modelVersion, message.ts, it, objectMapper.writeValueAsString(evaluateAll(it))))
            }
        }
    }

    //=================//
    // Model functions //
    //=================//

    fun buildMoodChart(reqId: UUID, multipartSoundFile: MultipartFile): File {
        val soundFile = File("src/main/resources/$reqId-tmp.${multipartSoundFile.originalFilename!!.substring(multipartSoundFile.originalFilename!!.lastIndexOf(".") + 1)}")
        FileOutputStream(soundFile).use { os -> os.write(multipartSoundFile.bytes) }

        val transcribedAudio = transcribe(soundFile)!!
        val punctuatedTranscription = punctuate(transcribedAudio)!!
        val evaluated = punctuatedTranscription.split(".").filter { it.isNotEmpty() }.map {
            val evaluatedValues = evaluateAll(it)
            listOf(evaluatedValues[MessageType.POSITIVE]!!, evaluatedValues[MessageType.NEUTRAL]!!, evaluatedValues[MessageType.NEGATIVE]!!)
        }.flatten()
        val result = visualise(reqId, evaluated)
        Files.delete(soundFile.toPath())
        return result
    }

    /**
     * Python Dependencies:
     *  - sentencepiece
     *  - deepmultilingualpunctuation
     *  - transformers
     *  - scipy
     *  - numpy
     *  - matplotlib
     *  - pandas
     */
    private fun runScript(workingDir: File, vararg command: String): String? {
        return try {
            val proc = ProcessBuilder(*command)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(60, TimeUnit.MINUTES)
            //logger.error(proc.errorStream.bufferedReader().readText())
            proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun visualise(reqId: UUID, values: List<Double>): File {
        logger.info("Running visualization script")
        val valuesStr = values.map { it.toString() }.toTypedArray()
        runScript(File(MODEL_DIRECTORY), MODEL_SCRIPT_ENGINE, VISUALIZE_SCRIPT_FILE_NAME, reqId.toString(), *valuesStr)
        while (true) {
            var resultPng: File
            try {
                resultPng = File("$MODEL_DIRECTORY/$reqId.png")
                return resultPng
            } catch (e: Exception) {
                // Expected while waiting
                logger.warn("Result image not generated yet, retrying...")
                Thread.sleep(1000)
            }
        }
    }

    fun punctuate(text: String): String? {
        logger.info("Running punctuation model")
        return runScript(File(MODEL_DIRECTORY), MODEL_SCRIPT_ENGINE, PUNCTUATE_MODEL_FILE_NAME, text)?.trim()?.replace("\n","")
    }

    fun transcribe(soundFile: File): String? {
        logger.info("Running transcription model")
        return runScript(File(MODEL_DIRECTORY), MODEL_SCRIPT_ENGINE, TRANSCRIBE_MODEL_FILE_NAME, soundFile.canonicalPath)?.trim()?.replace("\n","")
    }

    fun evaluate(messageStr: String): Pair<MessageType, Double> {
        logger.info("Running evaluation model")
        val resultJsonStr = runScript(File(MODEL_DIRECTORY), MODEL_SCRIPT_ENGINE, EVAL_MODEL_FILE_NAME, messageStr)
        val resultJson = objectMapper.readTree(resultJsonStr)
        val maxValueElement = resultJson.fields().asSequence().reduce {
                a, b -> if (a.value.doubleValue() > b.value.doubleValue()) a else b
        }
        return Pair(MessageType.valueOf(maxValueElement.key.toString().uppercase()), maxValueElement.value.asDouble())
    }

    fun evaluateAll(messageStr: String): Map<MessageType, Double> {
        logger.info("Running evaluation model")
        val resultJsonStr = runScript(File(MODEL_DIRECTORY), MODEL_SCRIPT_ENGINE, EVAL_MODEL_FILE_NAME, messageStr)
        val resultJson = objectMapper.readTree(resultJsonStr)
        return resultJson.fields().asSequence().map {
                MessageType.valueOf(it.key.toString().uppercase()) to it.value.doubleValue()
        }.toMap()
    }
}