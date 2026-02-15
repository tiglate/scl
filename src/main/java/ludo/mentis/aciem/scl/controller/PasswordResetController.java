package ludo.mentis.aciem.scl.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import ludo.mentis.aciem.scl.model.PasswordResetCompleteRequest;
import ludo.mentis.aciem.scl.model.PasswordResetRequest;
import ludo.mentis.aciem.scl.service.PasswordResetService;
import ludo.mentis.aciem.scl.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/passwordReset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(final PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/start")
    public String start(@ModelAttribute final PasswordResetRequest passwordResetRequest) {
        return "passwordReset/start";
    }

    @PostMapping("/start")
    public String start(@ModelAttribute @Valid final PasswordResetRequest passwordResetRequest,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            passwordResetService.startProcess(passwordResetRequest);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Password reset process started. Please check your e-mail.");
            return "redirect:/login";
        }
        return "passwordReset/start";
    }

    @GetMapping("/complete")
    public String complete(@RequestParam("uid") final UUID passwordResetUid,
            @ModelAttribute final PasswordResetCompleteRequest passwordResetCompleteRequest,
            final RedirectAttributes redirectAttributes) {
        passwordResetCompleteRequest.setUid(passwordResetUid);
        if (!passwordResetService.isValidPasswordResetUid(passwordResetUid)) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Invalid or expired password reset request.");
            return "redirect:/login";
        }
        return "passwordReset/complete";
    }

    @PostMapping("/complete")
    public String complete(
            @ModelAttribute @Valid final PasswordResetCompleteRequest passwordResetCompleteRequest,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            passwordResetService.completeProcess(passwordResetCompleteRequest);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Password was updated successfully.");
            return "redirect:/login";
        }
        return "passwordReset/complete";
    }

}
