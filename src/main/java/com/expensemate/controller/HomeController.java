package com.expensemate.controller;

import com.expensemate.entity.User;
import com.expensemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controller for public pages and authentication
 */
@Controller
public class HomeController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Home page
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    /**
     * About page
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    /**
     * Contact page
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    /**
     * Login page
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "expired", required = false) String expired,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        
        if (expired != null) {
            model.addAttribute("error", "Your session has expired. Please login again.");
        }
        
        return "auth/login";
    }
    
    /**
     * Registration page
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    /**
     * Process registration
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        
        // Check for validation errors
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Check if username already exists
        if (!userService.isUsernameAvailable(user.getUsername())) {
            model.addAttribute("error", "Username already exists!");
            return "auth/register";
        }
        
        // Check if email already exists
        if (!userService.isEmailAvailable(user.getEmail())) {
            model.addAttribute("error", "Email already exists!");
            return "auth/register";
        }
        
        // Validate password strength
        if (!userService.isPasswordValid(user.getPassword())) {
            model.addAttribute("error", "Password must be at least 6 characters long!");
            return "auth/register";
        }
        
        try {
            // Set default role
            user.setRole(User.Role.USER);
            
            // Register user
            userService.registerUser(user);
            
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please login with your credentials.");
            
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
    
    /**
     * Access denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
    
    /**
     * Error page
     */
    @GetMapping("/error")
    public String error() {
        return "error/error";
    }
    
    /**
     * Check username availability (AJAX)
     */
    @GetMapping("/api/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return userService.isUsernameAvailable(username);
    }
    
    /**
     * Check email availability (AJAX)
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userService.isEmailAvailable(email);
    }
}