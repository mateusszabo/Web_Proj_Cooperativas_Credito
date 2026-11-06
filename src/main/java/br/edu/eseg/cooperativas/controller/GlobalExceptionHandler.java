package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public String tratarRegraNegocio(RegraNegocioException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String tratarAcessoNegado(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro",
                "Acesso Negado (HTTP 403): operação restrita ao perfil ROLE_GESTOR.");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
}