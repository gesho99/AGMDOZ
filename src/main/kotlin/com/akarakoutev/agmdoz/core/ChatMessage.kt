package com.akarakoutev.agmdoz.core

import java.time.Instant
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class ChatMessage(
    @GeneratedValue @Id val id: Long? = null,
    val chatId: Int?,
    val userId: Long,
    var modelVersion: String?,
    val ts: Instant = Instant.now(),
    val text: String,
    var type: Pair<MessageType, Float>?)

enum class MessageType {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
}