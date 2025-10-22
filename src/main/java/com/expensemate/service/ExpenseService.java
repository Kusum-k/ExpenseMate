package com.expensemate.service;

import com.expensemate.entity.Expense;
import com.expensemate.entity.User;
import com.expensemate.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for Expense entity operations with analytics support
 */
@Service
@Transactional
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private BadgeService badgeService;
    
    /**
     * Save or update expense
     */
    public Expense saveExpense(Expense expense) {
        Expense savedExpense = expenseRepository.save(expense);
        
        // Update budget spent amount
        budgetService.updateBudgetSpentAmount(expense.getUser(), 
            YearMonth.from(expense.getExpenseDate()));
        
        // Check and award badges
        badgeService.checkAndAwardBadges(expense.getUser());
        
        return savedExpense;
    }
    
    /**
     * Find expense by ID
     */
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }
    
    /**
     * Get all expenses for user
     */
    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUserOrderByExpenseDateDesc(user);
    }
    
    /**
     * Get expenses by user with pagination
     */
    public Page<Expense> getExpensesByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseDate").descending());
        return expenseRepository.findAll(pageable);
    }
    
    /**
     * Get expenses by user and date range
     */
    public List<Expense> getExpensesByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(user, startDate, endDate);
    }
    
    /**
     * Get expenses by user and category
     */
    public List<Expense> getExpensesByUserAndCategory(User user, Expense.Category category) {
        return expenseRepository.findByUserAndCategoryOrderByExpenseDateDesc(user, category);
    }
    
    /**
     * Get current month expenses for user
     */
    public List<Expense> getCurrentMonthExpenses(User user) {
        return expenseRepository.findCurrentMonthExpensesByUser(user);
    }
    
    /**
     * Get expenses for specific month and year
     */
    public List<Expense> getExpensesByMonthYear(User user, int month, int year) {
        return expenseRepository.findByUserAndMonthYear(user, month, year);
    }
    
    /**
     * Calculate total expenses for current month
     */
    public BigDecimal getCurrentMonthTotal(User user) {
        return expenseRepository.calculateCurrentMonthTotal(user);
    }
    
    /**
     * Calculate total expenses for specific month
     */
    public BigDecimal getMonthlyTotal(User user, int month, int year) {
        return expenseRepository.calculateMonthlyTotal(user, month, year);
    }
    
    /**
     * Get category-wise spending for current month
     */
    public Map<Expense.Category, BigDecimal> getCategoryWiseSpendingCurrentMonth(User user) {
        List<Object[]> results = expenseRepository.getCategoryWiseSpendingCurrentMonth(user);
        Map<Expense.Category, BigDecimal> categorySpending = new HashMap<>();
        
        for (Object[] result : results) {
            Expense.Category category = (Expense.Category) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            categorySpending.put(category, amount);
        }
        
        return categorySpending;
    }
    
    /**
     * Get category-wise spending for specific month
     */
    public Map<Expense.Category, BigDecimal> getCategoryWiseSpending(User user, int month, int year) {
        List<Object[]> results = expenseRepository.getCategoryWiseSpending(user, month, year);
        Map<Expense.Category, BigDecimal> categorySpending = new HashMap<>();
        
        for (Object[] result : results) {
            Expense.Category category = (Expense.Category) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            categorySpending.put(category, amount);
        }
        
        return categorySpending;
    }
    
    /**
     * Get monthly spending trend (last 12 months)
     */
    public Map<String, BigDecimal> getMonthlySpendingTrend(User user) {
        LocalDate startDate = LocalDate.now().minusMonths(11).withDayOfMonth(1);
        List<Object[]> results = expenseRepository.getMonthlySpendingTrend(user, startDate);
        
        Map<String, BigDecimal> trendData = new LinkedHashMap<>();
        
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Integer year = (Integer) result[1];
            BigDecimal amount = (BigDecimal) result[2];
            
            String monthYear = String.format("%04d-%02d", year, month);
            trendData.put(monthYear, amount);
        }
        
        return trendData;
    }
    
    /**
     * Get daily expenses for current month
     */
    public Map<Integer, BigDecimal> getDailyExpensesCurrentMonth(User user) {
        List<Object[]> results = expenseRepository.getDailyExpensesCurrentMonth(user);
        Map<Integer, BigDecimal> dailyExpenses = new HashMap<>();
        
        for (Object[] result : results) {
            Integer day = (Integer) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            dailyExpenses.put(day, amount);
        }
        
        return dailyExpenses;
    }
    
    /**
     * Count current month expenses
     */
    public long getCurrentMonthExpenseCount(User user) {
        return expenseRepository.countCurrentMonthExpenses(user);
    }
    
    /**
     * Count total expenses by user
     */
    public long getTotalExpenseCount(User user) {
        return expenseRepository.countByUser(user);
    }
    
    /**
     * Get recent expenses (last N days)
     */
    public List<Expense> getRecentExpenses(User user, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return expenseRepository.findRecentExpenses(user, startDate);
    }
    
    /**
     * Get top expenses by amount
     */
    public List<Expense> getTopExpensesByAmount(User user, int limit) {
        List<Expense> topExpenses = expenseRepository.findTopExpensesByAmount(user);
        return topExpenses.stream().limit(limit).collect(Collectors.toList());
    }
    
    /**
     * Search expenses by description
     */
    public List<Expense> searchExpensesByDescription(User user, String keyword) {
        return expenseRepository.findByUserAndDescriptionContaining(user, keyword);
    }
    
    /**
     * Check if user has consecutive daily expenses
     */
    public boolean hasConsecutiveDailyExpenses(User user, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        long distinctDays = expenseRepository.countDistinctExpenseDays(user, startDate, endDate);
        return distinctDays >= days;
    }
    
    /**
     * Get average daily spending for current month
     */
    public BigDecimal getAverageDailySpending(User user) {
        BigDecimal average = expenseRepository.getAverageDailySpending(user);
        return average != null ? average : BigDecimal.ZERO;
    }
    
    /**
     * Get top spending categories for user
     */
    public List<Expense.Category> getTopSpendingCategories(User user) {
        return expenseRepository.findTopSpendingCategories(user);
    }
    
    /**
     * Get expense statistics for user
     */
    public ExpenseStats getExpenseStats(User user) {
        long totalExpenses = getTotalExpenseCount(user);
        long currentMonthExpenses = getCurrentMonthExpenseCount(user);
        BigDecimal totalAmount = getCurrentMonthTotal(user);
        BigDecimal averageDaily = getAverageDailySpending(user);
        
        List<Expense.Category> topCategories = getTopSpendingCategories(user);
        Expense.Category topCategory = topCategories.isEmpty() ? null : topCategories.get(0);
        
        return new ExpenseStats(totalExpenses, currentMonthExpenses, totalAmount, averageDaily, topCategory);
    }
    
    /**
     * Delete expense
     */
    public void deleteExpense(Long expenseId, User user) {
        Optional<Expense> expenseOpt = expenseRepository.findById(expenseId);
        if (expenseOpt.isPresent()) {
            Expense expense = expenseOpt.get();
            
            // Verify ownership
            if (!expense.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to delete this expense");
            }
            
            expenseRepository.delete(expense);
            
            // Update budget spent amount
            budgetService.updateBudgetSpentAmount(user, YearMonth.from(expense.getExpenseDate()));
        }
    }
    
    /**
     * Get all categories with their usage count for user
     */
    public Map<Expense.Category, Long> getCategoryUsageStats(User user) {
        List<Expense> expenses = getExpensesByUser(user);
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.counting()));
    }
    
    /**
     * Check if user has used all expense categories
     */
    public boolean hasUsedAllCategories(User user) {
        Map<Expense.Category, Long> usage = getCategoryUsageStats(user);
        return usage.size() >= Expense.Category.values().length;
    }
    
    /**
     * Get total expenses for all users (admin function)
     */
    public BigDecimal getTotalExpensesAllUsers() {
        BigDecimal total = expenseRepository.getTotalExpensesAllUsers();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get user-wise total expenses (admin function)
     */
    public Map<User, BigDecimal> getUserWiseTotalExpenses() {
        List<Object[]> results = expenseRepository.getUserWiseTotalExpenses();
        Map<User, BigDecimal> userExpenses = new HashMap<>();
        
        for (Object[] result : results) {
            User user = (User) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            userExpenses.put(user, amount);
        }
        
        return userExpenses;
    }
    
    /**
     * Inner class for expense statistics
     */
    public static class ExpenseStats {
        private final long totalExpenses;
        private final long currentMonthExpenses;
        private final BigDecimal totalAmount;
        private final BigDecimal averageDaily;
        private final Expense.Category topCategory;
        
        public ExpenseStats(long totalExpenses, long currentMonthExpenses, BigDecimal totalAmount, 
                           BigDecimal averageDaily, Expense.Category topCategory) {
            this.totalExpenses = totalExpenses;
            this.currentMonthExpenses = currentMonthExpenses;
            this.totalAmount = totalAmount;
            this.averageDaily = averageDaily;
            this.topCategory = topCategory;
        }
        
        // Getters
        public long getTotalExpenses() { return totalExpenses; }
        public long getCurrentMonthExpenses() { return currentMonthExpenses; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public BigDecimal getAverageDaily() { return averageDaily; }
        public Expense.Category getTopCategory() { return topCategory; }
    }
}