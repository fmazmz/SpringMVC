package org.example.springmvc.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedActionException.class)
    public String handleUnauthorizedException(UnauthorizedActionException ex,
                                              RedirectAttributes redirectAttributes) {
        log.warn("Unauthorized action: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(InvalidBookingTimeException.class)
    public String handleInvalidBookingException(InvalidBookingTimeException ex,
                                                RedirectAttributes redirectAttributes) {
        log.warn("Invalid booking time: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/bookings";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex,
                                                RedirectAttributes redirectAttributes) {
        log.warn("Entity not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public String handleDuplicateEntityException(DuplicateEntityException ex,
                                                 RedirectAttributes redirectAttributes) {
        log.warn("Duplicate entity: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllOtherExceptions(Exception ex,
                                           RedirectAttributes redirectAttributes) {
        log.error("Unexpected error occurred", ex);
        redirectAttributes.addFlashAttribute("error", ErrorMessages.UNEXPECTED_ERROR + ": " + ex.getMessage());
        return "redirect:/error";
    }
}