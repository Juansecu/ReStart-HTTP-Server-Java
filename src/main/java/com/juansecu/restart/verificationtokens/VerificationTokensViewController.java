package com.juansecu.restart.verificationtokens;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.juansecu.restart.verificationtokens.enums.EVerificationTokenType;

@Controller
@RequestMapping("/verification-tokens")
@RequiredArgsConstructor
public class VerificationTokensViewController {
    private final VerificationTokensViewService verificationTokensViewService;

    @GetMapping("/verify")
    public String verify(
        @RequestParam("token") final String token,
        @RequestParam("type") final EVerificationTokenType verificationTokenType,
        @RequestParam("userId") final Integer userId,
        final Model model,
        final HttpServletRequest request
    ) {
        return this.verificationTokensViewService.verify(
            verificationTokenType,
            model,
            request
        );
    }
}
