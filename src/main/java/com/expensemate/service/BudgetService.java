package com.expensemate.service;

import com.expensemate.entity.Budget;
import com.expensemate.entity.User;
import com.expensemate.repository.BudgetRepository;
import com.expensemate.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Budget entity operations with alert management
 */
@Service
@Transactional
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Create or update budget
     */
    public Budget saveBudget(Budget budget) {
        // Check if budget already exists for this month/year
        Optional<Budget> existingBudget = budgetRepository.findByUserAndBudgetMonthAndBudgetYear(
            budget.getUser(), budget.getBudgetMonth(), budget.getBudgetYear());
        
        if (existingBudget.isPresent()) {
            // Update existing budget
            Budget existing = existingBudget.get();
            existing.setBudgetAmount(budget.getBudgetAmount());
            existing.preUpdate();
            return budgetRepository.save(existing);
        } else {
            // Create new budget and calculate spent amount
            Budget savedBudget = budgetRepository.save(budget);
            updateBudgetSpentAmount(budget.getUser(), budget.getYearMonth());
            return savedBudget;
        }
    }
    
    /**
     * Find budget by ID
     */
    public Optional<Budget> findById(Long id) {
        return budgetRepository.findById(id);
    }
    
    /**
     * Get budget for user and specific month/year
     */
    public Optional<Budget> getBudgetByUserAndMonth(User user, int month, int year) {
        return budgetRepository.findByUserAndBudgetMonthAndBudgetYear(user, month, year);
    }
    
    /**
     * Get current month budget for user
     */
    public Optional<Budget> getCurrentMonthBudget(User user) {
        return budgetRepository.findCurrentMonthBudget(user);
    }
    
    /**
     * Get all budgets for user
     */
    public List<Budget> getBudgetsByUser(User user) {
        return budgetRepository.findByUserOrderByBudgetYearDescBudgetMonthDesc(user);
    }
    
    /**
     * Get budgets for user in specific year
     */
    public List<Budget> getBudgetsByUserAndYear(User user, int year) {
        return budgetRepository.findByUserAndBudgetYearOrderByBudgetMonth(user, year);
    }
    
    /**
     * Update budget spent amount based on expenses
     */
    public void updateBudgetSpentAmount(User user, YearMonth yearMonth) {
        Optional<Budget> budgetOpt = getBudgetByUserAndMonth(user, 
            yearMonth.getMonthValue(), yearMonth.getYear());
        
        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            BigDecimal spentAmount = expenseRepository.calculateMonthlyTotal(user, 
                yearMonth.getMonthValue(), yearMonth.getYear());
            
            budget.setSpentAmount(spentAmount);
            budgetRepository.save(budget);
            
            // Check for alert conditions
            checkAndSendAlerts(budget);
        }
    }
    
    /**
     * Check and send budget alerts
     */
    private void checkAndSendAlerts(Budget budget) {
        // Check for 80% alert
        if (budget.shouldSend80Alert()) {
            emailService.sendBudgetAlert80(budget);
            budget.setAlert80Sent(true);
            budgetRepository.save(budget);
        }
        
        // Check for 100% alert
        if (budget.shouldSend100Alert()) {
            emailService.sendBudgetAlert100(budget);
            budget.setAlert100Sent(true);
            budgetRepository.save(budget);
        }
    }
    
    /**
     * Get budgets needing 80% alert
     */
    public List<Budget> getBudgetsNeedingAlert80() {
        return budgetRepository.findBudgetsNeedingAlert80();
    }
    
    /**
     * Get budgets needing 100% alert
     */
    public List<Budget> getBudgetsNeedingAlert100() {
        return budgetRepository.findBudgetsNeedingAlert100();
    }
    
    /**
     * Get over-budget budgets
     */
    public List<Budget> getOverBudgets() {
        return budgetRepository.findOverBudgets();
    }
    
    /**
     * Get budgets where user is under 80% spending
     */
    public List<Budget> getUnderBudgetsByUser(User user) {
        return budgetRepository.findUnderBudgetsByUser(user);
    }
    
    /**
     * Count consecutive months user stayed within budget
     */
    public long countConsecutiveWithinBudgetMonths(User user) {
        YearMonth currentMonth = YearMonth.now();
        return budgetRepository.countConsecutiveWithinBudgetMonths(user, 
            currentMonth.getYear(), currentMonth.getMonthValue());
    }
    
    /**
     * Check if user stayed within budget for 3+ consecutive months
     */
    public boolean hasConsecutiveBudgetSuccess(User user, int months) {
        return countConsecutiveWithinBudgetMonths(user) >= months;
    }
    
    /**
     * Get users with consecutive budget success
     */
    public List<User> getUsersWithConsecutiveBudgetSuccess() {
        return budgetRepository.findUsersWithConsecutiveBudgetSuccess();
    }
    
    /**
     * Get Budget Hero candidates (spent < 80%)
     */
    public List<Budget> getBudgetHeroCandidates() {
        return budgetRepository.findBudgetHeroCandidates();
    }
    
    /**
     * Get average budget amount for user
     */
    public BigDecimal getAverageBudgetAmount(User user) {
        Double average = budgetRepository.getAverageBudgetAmount(user);
        return average != null ? BigDecimal.valueOf(average) : BigDecimal.ZERO;
    }
    
    /**
     * Get average spending percentage for user
     */
    public Double getAverageSpendingPercentage(User user) {
        Double average = budgetRepository.getAverageSpendingPercentage(user);
        return average != null ? average : 0.0;
    }
    
    /**
     * Get recent budgets for user
     */
    public List<Budget> getRecentBudgets(User user, int months) {
        YearMonth currentMonth = YearMonth.now();
        long monthsSinceEpoch = (currentMonth.getYear() * 12L) + currentMonth.getMonthValue() - months;
        return budgetRepository.findRecentBudgets(user, monthsSinceEpoch);
    }
    
    /**
     * Check if user has budget for current month
     */
    public boolean hasCurrentMonthBudget(User user) {
        YearMonth currentMonth = YearMonth.now();
        return budgetRepository.existsByUserAndBudgetMonthAndBudgetYear(user, 
            currentMonth.getMonthValue(), currentMonth.getYear());
    }
    
    /**
     * Count total budgets by user
     */
    public long getBudgetCount(User user) {
        return budgetRepository.countByUser(user);
    }
    
    /**
     * Delete budget
     */
    public void deleteBudget(Long budgetId, User user) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            
            // Verify ownership
            if (!budget.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to delete this budget");
            }
            
            budgetRepository.delete(budget);
        }
    }
    
    /**
     * Get budget statistics for user
     */
    public BudgetStats getBudgetStats(User user) {
        long totalBudgets = getBudgetCount(user);
        BigDecimal averageBudget = getAverageBudgetAmount(user);
        Double averageSpending = getAverageSpendingPercentage(user);
        long consecutiveMonths = countConsecutiveWithinBudgetMonths(user);
        
        Optional<Budget> currentBudget = getCurrentMonthBudget(user);
        String currentStatus = currentBudget.map(Budget::getBudgetStatus).orElse("NO_BUDGET");
        
        return new BudgetStats(totalBudgets, averageBudget, averageSpending, 
                              consecutiveMonths, currentStatus);
    }
    
    /**
     * Get all current month budgets (admin function)
     */
    public List<Budget> getAllCurrentMonthBudgets() {
        return budgetRepository.findAllCurrentMonthBudgets();
    }
    
    /**
     * Get current month budget statistics (admin function)
     */
    public Object[] getCurrentMonthBudgetStats() {
        return budgetRepository.getCurrentMonthBudgetStats();
    }
    
    /**
     * Get users without current month budget
     */
    public List<User> getUsersWithoutCurrentMonthBudget() {
        return budgetRepository.findUsersWithoutCurrentMonthBudget();
    }
    
    /**
     * Process all pending budget alerts
     */
    public void processAllPendingAlerts() {
        // Process 80% alerts
        List<Budget> budgets80 = getBudgetsNeedingAlert80();
        for (Budget budget : budgets80) {
            emailService.sendBudgetAlert80(budget);
            budget.setAlert80Sent(true);
            budgetRepository.save(budget);
        }
        
        // Process 100% alerts
        List<Budget> budgets100 = getBudgetsNeedingAlert100();
        for (Budget budget : budgets100) {
            emailService.sendBudgetAlert100(budget);
            budget.setAlert100Sent(true);
            budgetRepository.save(budget);
        }
    }
    
    /**
     * Reset monthly budgets (for scheduler)
     */
    public void resetMonthlyBudgets() {
        // This method can be called by scheduler to reset alert flags
        // and prepare for new month
        List<Budget> allBudgets = budgetRepository.findAll();
        for (Budget budget : allBudgets) {
            budget.setAlert80Sent(false);
            budget.setAlert100Sent(false);
            budgetRepository.save(budget);
        }
    }
    
    /**
     * Inner class for budget statistics
     */
    public static class BudgetStats {
        private final long totalBudgets;
        private final BigDecimal averageBudget;
        private final Double averageSpending;
        private final long consecutiveMonths;
        private final String currentStatus;
        
        public BudgetStats(long totalBudgets, BigDecimal averageBudget, Double averageSpending,
                          long consecutiveMonths, String currentStatus) {
            this.totalBudgets = totalBudgets;
            this.averageBudget = averageBudget;
            this.averageSpending = averageSpending;
            this.consecutiveMonths = consecutiveMonths;
            this.currentStatus = currentStatus;
        }
        
        // Getters
        public long getTotalBudgets() { return totalBudgets; }
        public BigDecimal getAverageBudget() { return averageBudget; }
        public Double getAverageSpending() { return averageSpending; }
        public long getConsecutiveMonths() { return consecutiveMonths; }
        public String getCurrentStatus() { return currentStatus; }
    }
}