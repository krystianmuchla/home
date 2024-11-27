package com.github.krystianmuchla.home.domain.id.api;

import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static com.github.krystianmuchla.home.application.util.Resource.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Attribute.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tag.*;

public class SignInController extends Controller {
    public static final SignInController INSTANCE = new SignInController();

    public SignInController() {
        super("/id/sign_in");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        ResponseWriter.writeHtml(exchange, 200, response());
    }

    private String response() {
        return "<!DOCTYPE html>" + html(attrs(lang("en")),
            head(
                title("Home"),
                meta(attrs(name("viewport"), content("width=device-width, initial-scale=1.0"))),
                link(attrs(rel("stylesheet"), href(COMMON_STYLE.urlPath))),
                link(attrs(rel("stylesheet"), href(SIGN_IN_FORM_STYLE.urlPath))),
                script(attrs(type("module"), src(SIGN_IN_FORM_SCRIPT.urlPath), defer()))
            ),
            body(
                div(attrs(clazz("background")),
                    div(attrs(clazz("modal")),
                        div(attrs(clazz("on-top sign-in-form")),
                            labeledTextInput("Login", "login", "text"),
                            labeledTextInput("Password", "password", "password"),
                            button(attrs(id("sign-in"), clazz("main-button")),
                                "Sign in"
                            ),
                            a(attrs(href(SignUpController.INSTANCE.getPath())),
                                "No account?"
                            )
                        )
                    )
                )
            )
        );
    }
}
