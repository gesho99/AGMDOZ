package com.akarakoutev.agmdoz.db

import com.akarakoutev.agmdoz.core.ChatMessage
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepo : JpaRepository<ChatMessage, Long> {
    @Query(value = "SELECT * FROM chat_message ORDER BY id DESC", nativeQuery = true)
    fun findAllPaginated(pageable: Pageable): List<ChatMessage>
}