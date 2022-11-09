package com.akarakoutev.agmdoz.core

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Model(@Id val version: String, val trainingSetSize: Long, val ts: Instant)
