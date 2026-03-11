package org.example.springmvc.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedActionException.class)
    public String handleUnauthorizedException(UnauthorizedActionException ex,
                                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(InvalidBookingTimeException.class)
    public String handleInvalidBookingException(InvalidBookingTimeException ex,
                                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/bookings";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex,
                                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public String handleDuplicateEntityException(DuplicateEntityException ex,
                                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllOtherExceptions(Exception ex,
                                           RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ErrorMessages.UNEXPECTED_ERROR + ": " + ex.getMessage());
        return "redirect:/error";
    }
}