package com.akarakoutev.agmdoz

import com.akarakoutev.agmdoz.services.ChatService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.util.*

@SpringBootTest
class ChatServiceTest {

    @Autowired
    lateinit var chatService: ChatService

//    @Test
//    fun testAllModelPipeline() {
//        val testAudio = File("src/test/resources/test-audio.mp3")
//
//        val transcribedAudio = chatService.transcribe(testAudio)!!
//        assert(transcribedAudio == "LYDIA WAS LYDIA STILL UNTAMED UNABASHED WILD NOISY AND FEARLESS")
//
//        val punctuatedTranscription = chatService.punctuate(transcribedAudio)
//        assert(punctuatedTranscription == "LYDIA WAS. LYDIA STILL UNTAMED, UNABASHED, WILD, NOISY AND FEARLESS.")
//    }

//    @Test
//    fun buildMoodChartTest() {
//        chatService.buildMoodChart(UUID.randomUUID(), File("src/test/resources/test-audio.mp3"))
//    }

//    @Test
//    fun visualizeTest() {
//        chatService.visualise(listOf(0.848, 0.342, 0.893, 0.124, 0.582))
//    }

}