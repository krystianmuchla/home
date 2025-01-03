package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static com.github.krystianmuchla.home.application.util.Resource.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Attribute.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tag.*;

public class SignUpController extends Controller {
    public static final SignUpController INSTANCE = new SignUpController();

    public SignUpController() {
        super("/id/sign_up");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).html(response()).write();
    }

    private String response() {
        return "<!DOCTYPE html>" + html(attrs(lang("en")),
            head(
                title("Home"),
                meta(attrs(name("viewport"), content("width=device-width, initial-scale=1.0"))),
                link(attrs(rel("stylesheet"), href(COMMON_STYLE.urlPath))),
                link(attrs(rel("stylesheet"), href(SIGN_UP_FORM_STYLE.urlPath))),
                script(attrs(type("module"), src(SIGN_UP_FORM_SCRIPT.urlPath), defer()))
            ),
            body(
                div(attrs(clazz("background")),
                    div(attrs(clazz("modal")),
                        div(attrs(clazz("on-top sign-up-form")),
                            labeledTextInput("Name", "name", "text"),
                            labeledTextInput("Login", "login", "text"),
                            labeledTextInput("Password", "password", "password"),
                            button(attrs(id("sign-up"), clazz("main-button")),
                                "Sign up"
                            ),
                            a(attrs(href(SignInController.INSTANCE.getPath())),
                                "Already have an account?"
                            )
                        )
                    )
                )
            )
        );
    }
}
