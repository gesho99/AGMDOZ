package com.akarakoutev.agmdoz.controllers.admin

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin")
class AdminController {

    @GetMapping("/")
    fun index(model: Model): String {
        TODO("Not yet implemented")
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String, model: Model): String {
        TODO("Not yet implemented")
    }

    @DeleteMapping("/{id}")
    fun add(@PathVariable id: String, model: Model): String {
        TODO("Not yet implemented")
    }

    @PostMapping("/update")
    fun update(model: Model): String {
        TODO("Not yet implemented")
    }

    @PostMapping("/retrain")
    fun retrain(model: Model): String {
        TODO("Not yet implemented")
    }
}