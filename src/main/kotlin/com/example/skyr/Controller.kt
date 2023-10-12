package com.example.skyr

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView

@Controller
class Controller {

    @GetMapping("/")
    fun root(): RedirectView {
        return RedirectView("art-verse/index.html")
    }
}
