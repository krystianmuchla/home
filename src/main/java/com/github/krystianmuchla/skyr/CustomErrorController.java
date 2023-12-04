package com.github.krystianmuchla.skyr;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public final class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String error() {
        return "error.html";
    }
}
