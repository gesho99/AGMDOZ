package com.akarakoutev.agmdoz.controllers.admin

import com.akarakoutev.agmdoz.services.ChatService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class AdminController @Autowired constructor(val chatService: ChatService){
    private final val logger: Logger = LoggerFactory.getLogger(AdminController::class.java)

    @GetMapping("/")
    fun index(model: Model, @RequestParam(required = false, defaultValue = "1") page: Int): String {
        if (page <= 0) return "redirect:/?page=1"
        val pageCount = chatService.getPageCount()
        if (page > pageCount) return "redirect:/?page=${pageCount}"

        return handle(model) {
            model.addAttribute("messages", chatService.getAllPaginated(page))
            model.addAttribute("pageCount", pageCount)
            model.addAttribute("page", page)
            "index"
        }
    }

    @PostMapping("/evaluate")
    fun evaluate(model: Model, redirectAttrs: RedirectAttributes, @RequestParam expr: String): String {
        redirectAttrs.addFlashAttribute("evaluation", chatService.evaluate(expr.trim()))
        return "redirect:/"
    }

    @GetMapping("/delete/{id}")
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

    @PostMapping("/model/retrain")
    fun retrain(model: Model): String {
        return handle(model) {
            chatService.retrain()
            "redirect:/model/all"
        }
    }

    private fun handle(model: Model, action: () -> String): String {
        val errorMessage: String
        val errorCode: Short

        try {
            return action()
        } catch (nsee: NoSuchElementException) {
            logger.error("", nsee)
            errorMessage = nsee.message ?: "Not Found!"
            errorCode = 404
        } catch (e: Exception) {
            logger.error("", e)
            errorMessage = e.message ?: "Internal server error!"
            errorCode = 500
        }

        model.addAttribute("error", errorMessage)
        model.addAttribute("code", errorCode)
        return "error"
    }
}