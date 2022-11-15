package com.akarakoutev.agmdoz.core

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class ChatMessage(
    @GeneratedValue @Id val id: Long? = null,
    val chatId: String,
    val userId: Long,
    var modelVersion: String?,
    var ts: Instant,
    val text: String,
    var evaluationsJson: String?)

enum class MessageType {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
}