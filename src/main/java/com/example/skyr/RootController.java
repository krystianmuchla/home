package com.example.skyr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public final class RootController {
    @GetMapping("/")
    public String getRoot() {
        return "redirect:art-verse/index.html";
    }
}
