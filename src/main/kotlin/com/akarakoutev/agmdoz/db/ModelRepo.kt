package com.akarakoutev.agmdoz.db

import com.akarakoutev.agmdoz.core.Model
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ModelRepo : JpaRepository<Model, String> {

    @Query(
        value = "SELECT * FROM model " +
                "ORDER BY " +
                "   CAST(SUBSTRING(version FROM 0 FOR LOCATE('.', version) - 1) AS INT) DESC, " +
                "   CAST(SUBSTRING(version FROM (LOCATE('.', version) + 1)) AS INT) DESC",
        nativeQuery = true)
    fun findByOrderByVersionDesc(): List<Model>
}