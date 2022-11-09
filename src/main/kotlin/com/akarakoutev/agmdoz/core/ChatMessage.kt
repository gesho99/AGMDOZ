package com.akarakoutev.agmdoz.core

import java.time.Instant
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class ChatMessage(
    @GeneratedValue @Id val id: UUID,
    val chatId: UUID?,
    val userId: UUID,
    var modelVersion: String?,
    val ts: Long = Instant.now().epochSecond,
    val text: String,
    var type: Pair<MessageType, Float>?)

enum class MessageType {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
}