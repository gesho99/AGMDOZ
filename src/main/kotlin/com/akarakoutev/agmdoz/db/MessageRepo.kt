package com.akarakoutev.agmdoz.db

import com.akarakoutev.agmdoz.core.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageRepo : JpaRepository<ChatMessage, UUID>