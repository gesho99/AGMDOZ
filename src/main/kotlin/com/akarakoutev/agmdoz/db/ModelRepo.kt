package com.akarakoutev.agmdoz.db

import com.akarakoutev.agmdoz.core.Model
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ModelRepo : JpaRepository<Model, String> {
    fun findByOrderByVersion(): List<Model>
}