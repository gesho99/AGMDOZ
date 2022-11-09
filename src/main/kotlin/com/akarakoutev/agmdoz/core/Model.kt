package com.akarakoutev.agmdoz.core

import javax.persistence.Entity

@Entity
data class Model(val version: String, val trainingSetSize: Long, val ts: Long)
