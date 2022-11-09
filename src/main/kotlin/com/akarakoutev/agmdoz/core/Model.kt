package com.akarakoutev.agmdoz.core

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Model(@Id val version: String, val trainingSetSize: Long, val ts: Long)
