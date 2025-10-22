package com.expensemate.controller;

import com.expensemate.entity.Badge;
import com.expensemate.entity.Budget;
import com.expensemate.entity.Expense;
import com.expensemate.entity.User;
import com.expensemate.service.BadgeService;
import com.expensemate.service.BudgetService;
import com.expensemate.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for user dashboard and expense management
 */
@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private BadgeService badgeService;
    
    /**
     * User dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        // Get current month data
        List<Expense> recentExpenses = expenseService.getRecentExpenses(user, 7);
        BigDecimal currentMonthTotal = expenseService.getCurrentMonthTotal(user);
        Optional<Budget> currentBudget = budgetService.getCurrentMonthBudget(user);
        
        // Get category-wise spending
        Map<Expense.Category, BigDecimal> categorySpending = 
            expenseService.getCategoryWiseSpendingCurrentMonth(user);
        
        // Get monthly trend (last 6 months)
        Map<String, BigDecimal> monthlyTrend = expenseService.getMonthlySpendingTrend(user);
        
        // Get badges
        List<Badge> recentBadges = badgeService.getRecentBadgesByUser(user, 30);
        long totalBadges = badgeService.getBadgeCount(user);
        Long totalPoints = badgeService.getTotalPoints(user);
        
        // Get statistics
        ExpenseService.ExpenseStats expenseStats = expenseService.getExpenseStats(user);
        BudgetService.BudgetStats budgetStats = budgetService.getBudgetStats(user);
        
        model.addAttribute("user", user);
        model.addAttribute("recentExpenses", recentExpenses);
        model.addAttribute("currentMonthTotal", currentMonthTotal);
        model.addAttribute("currentBudget", currentBudget.orElse(null));
        model.addAttribute("categorySpending", categorySpending);
        model.addAttribute("monthlyTrend", monthlyTrend);
        model.addAttribute("recentBadges", recentBadges);
        model.addAttribute("totalBadges", totalBadges);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("expenseStats", expenseStats);
        model.addAttribute("budgetStats", budgetStats);
        
        return "user/dashboard";
    }
    
    /**
     * Expenses page
     */
    @GetMapping("/expenses")
    public String expenses(@AuthenticationPrincipal User user, Model model) {
        List<Expense> expenses = expenseService.getExpensesByUser(user);
        model.addAttribute("expenses", expenses);
        model.addAttribute("newExpense", new Expense());
        model.addAttribute("categories", Expense.Category.values());
        return "user/expenses";
    }
    
    /**
     * Add expense
     */
    @PostMapping("/expenses/add")
    public String addExpense(@Valid @ModelAttribute("newExpense") Expense expense,
                           BindingResult result,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form.");
            return "redirect:/user/expenses";
        }
        
        try {
            expense.setUser(user);
            expenseService.saveExpense(expense);
            redirectAttributes.addFlashAttribute("success", "Expense added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add expense: " + e.getMessage());
        }
        
        return "redirect:/user/expenses";
    }
    
    /**
     * Edit expense page
     */
    @GetMapping("/expenses/edit/{id}")
    public String editExpense(@PathVariable Long id, 
                            @AuthenticationPrincipal User user, 
                            Model model) {
        
        Optional<Expense> expenseOpt = expenseService.findById(id);
        if (expenseOpt.isEmpty() || !expenseOpt.get().getUser().getId().equals(user.getId())) {
            return "redirect:/user/expenses";
        }
        
        model.addAttribute("expense", expenseOpt.get());
        model.addAttribute("categories", Expense.Category.values());
        return "user/edit-expense";
    }
    
    /**
     * Update expense
     */
    @PostMapping("/expenses/update")
    public String updateExpense(@Valid @ModelAttribute("expense") Expense expense,
                              BindingResult result,
                              @AuthenticationPrincipal User user,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form.");
            return "redirect:/user/expenses/edit/" + expense.getId();
        }
        
        try {
            expense.setUser(user);
            expenseService.saveExpense(expense);
            redirectAttributes.addFlashAttribute("success", "Expense updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update expense: " + e.getMessage());
        }
        
        return "redirect:/user/expenses";
    }
    
    /**
     * Delete expense
     */
    @PostMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id,
                              @AuthenticationPrincipal User user,
                              RedirectAttributes redirectAttributes) {
        
        try {
            expenseService.deleteExpense(id, user);
            redirectAttributes.addFlashAttribute("success", "Expense deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete expense: " + e.getMessage());
        }
        
        return "redirect:/user/expenses";
    }
    
    /**
     * Budgets page
     */
    @GetMapping("/budgets")
    public String budgets(@AuthenticationPrincipal User user, Model model) {
        List<Budget> budgets = budgetService.getBudgetsByUser(user);
        Optional<Budget> currentBudget = budgetService.getCurrentMonthBudget(user);
        
        model.addAttribute("budgets", budgets);
        model.addAttribute("currentBudget", currentBudget.orElse(null));
        model.addAttribute("newBudget", new Budget());
        
        return "user/budgets";
    }
    
    /**
     * Add budget
     */
    @PostMapping("/budgets/add")
    public String addBudget(@Valid @ModelAttribute("newBudget") Budget budget,
                          BindingResult result,
                          @AuthenticationPrincipal User user,
                          RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form.");
            return "redirect:/user/budgets";
        }
        
        try {
            budget.setUser(user);
            budgetService.saveBudget(budget);
            redirectAttributes.addFlashAttribute("success", "Budget set successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to set budget: " + e.getMessage());
        }
        
        return "redirect:/user/budgets";
    }
    
    /**
     * Badges page
     */
    @GetMapping("/badges")
    public String badges(@AuthenticationPrincipal User user, Model model) {
        List<Badge> userBadges = badgeService.getActiveBadgesByUser(user);
        Long totalPoints = badgeService.getTotalPoints(user);
        Long userRank = badgeService.getUserRank(user);
        String badgeLevel = badgeService.getUserBadgeLevel(user);
        
        // All available badges for display
        Badge.BadgeType[] allBadgeTypes = Badge.BadgeType.values();
        
        model.addAttribute("userBadges", userBadges);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("userRank", userRank);
        model.addAttribute("badgeLevel", badgeLevel);
        model.addAttribute("allBadgeTypes", allBadgeTypes);
        
        return "user/badges";
    }
    
    /**
     * Reports page
     */
    @GetMapping("/reports")
    public String reports(@AuthenticationPrincipal User user, Model model) {
        // Get comprehensive data for reports
        Map<Expense.Category, BigDecimal> categorySpending = 
            expenseService.getCategoryWiseSpendingCurrentMonth(user);
        Map<String, BigDecimal> monthlyTrend = expenseService.getMonthlySpendingTrend(user);
        List<Expense> topExpenses = expenseService.getTopExpensesByAmount(user, 10);
        
        ExpenseService.ExpenseStats expenseStats = expenseService.getExpenseStats(user);
        BudgetService.BudgetStats budgetStats = budgetService.getBudgetStats(user);
        
        model.addAttribute("categorySpending", categorySpending);
        model.addAttribute("monthlyTrend", monthlyTrend);
        model.addAttribute("topExpenses", topExpenses);
        model.addAttribute("expenseStats", expenseStats);
        model.addAttribute("budgetStats", budgetStats);
        
        return "user/reports";
    }
    
    /**
     * Profile page
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "user/profile";
    }
    
    /**
     * Settings page
     */
    @GetMapping("/settings")
    public String settings(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "user/settings";
    }
    
    /**
     * API endpoint for chart data
     */
    @GetMapping("/api/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData(@AuthenticationPrincipal User user,
                                          @RequestParam(defaultValue = "category") String type) {
        
        switch (type) {
            case "category":
                return Map.of("data", expenseService.getCategoryWiseSpendingCurrentMonth(user));
            case "monthly":
                return Map.of("data", expenseService.getMonthlySpendingTrend(user));
            case "daily":
                return Map.of("data", expenseService.getDailyExpensesCurrentMonth(user));
            default:
                return Map.of("error", "Invalid chart type");
        }
    }
}