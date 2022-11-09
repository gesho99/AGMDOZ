package com.akarakoutev.agmdoz.controllers.admin

import com.akarakoutev.agmdoz.services.ChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin")
class AdminController @Autowired constructor(val chatService: ChatService){

    @GetMapping("/")
    fun index(model: Model): String {
        return handle(model) {
            model.addAttribute("messages", chatService.getAll())
            "index"
        }
    }

    @GetMapping("/{id}/view")
    fun get(model: Model, @PathVariable id: String): String {
        return handle(model) {
            model.addAttribute("message", chatService.get(id))
            "message"
        }
    }

    @DeleteMapping("/{id}")
    fun delete(model: Model, @PathVariable id: String): String {
        return handle(model) {
            chatService.delete(id)
            "redirect:/"
        }
    }

    @GetMapping("/model/all")
    fun models(model: Model): String {
        return handle(model) {
            model.addAttribute("models", chatService.getModels())
            "models"
        }
    }

    @GetMapping("/model/{version}/view")
    fun models(model: Model, @PathVariable version: String): String {
        return handle(model) {
            model.addAttribute("models", chatService.getModel(version))
            "model"
        }
    }

    @PostMapping("/retrain")
    fun retrain(model: Model): String {
        return handle(model) {
            val retrainedVersion = chatService.retrain()
            "redirect:/model/${retrainedVersion}/view"
        }
    }

    private fun handle(model: Model, action: () -> String): String {
        val errorMessage: String
        val errorCode: Short

        try {
            return action()
        } catch (nsee: NoSuchElementException) {
            errorMessage = nsee.message ?: "Not Found!"
            errorCode = 404
        } catch (e: Exception) {
            errorMessage = e.message ?: "Internal server error!"
            errorCode = 500
        }

        model.addAttribute("error", errorMessage)
        model.addAttribute("code", errorCode)
        return "error"
    }
}