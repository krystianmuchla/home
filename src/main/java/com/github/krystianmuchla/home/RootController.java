package com.github.krystianmuchla.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @GetMapping("/")
    public String getRoot() {
        return "redirect:art-verse/index.html";
    }
}
