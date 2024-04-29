package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import com.github.krystianmuchla.home.ui.Html;
import com.github.krystianmuchla.home.ui.Script;
import com.github.krystianmuchla.home.ui.Style;
import com.github.krystianmuchla.home.ui.element.LabeledTextInput;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static j2html.TagCreator.*;

public class SignUpController extends Controller {
    public static final String PATH = "/id/sign_up";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            session(request);
            response.sendRedirect("/todo");
        } catch (final AuthenticationException exception) {
            ResponseWriter.writeHtml(response, html());
        }
    }

    private String html() {
        return Html.dom(
            List.of(
                Style.BACKGROUND,
                Style.BOX,
                Style.LABELED_TEXT_INPUT,
                Style.MAIN_BUTTON,
                Style.MODAL,
                Style.SIGN_UP_FORM,
                Style.TEXT_INPUT
            ),
            List.of(Script.SIGN_UP_FORM),
            div(attrs(".background"),
                div(attrs(".box.modal.sign-up-form"),
                    LabeledTextInput.html("Login", "login", "text"),
                    LabeledTextInput.html("Password", "password", "password"),
                    button(attrs("#sign-up.main-button"), "Sign up"),
                    a("Already have an account?").withHref("sign_in")
                )
            )
        );
    }
}
