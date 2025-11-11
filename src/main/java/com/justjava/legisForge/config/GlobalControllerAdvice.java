package com.justjava.legisForge.config;



import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {


    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        System.out.println("Current Path: " + request.getRequestURI());
        return request.getRequestURI();
    }

}
