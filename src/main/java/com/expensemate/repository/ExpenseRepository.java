package com.expensemate.repository;

import com.expensemate.entity.Expense;
import com.expensemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for Expense entity operations with analytics support
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    /**
     * Find expenses by user
     */
    List<Expense> findByUserOrderByExpenseDateDesc(User user);
    
    /**
     * Find expenses by user and date range
     */
    List<Expense> findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(
        User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find expenses by user and category
     */
    List<Expense> findByUserAndCategoryOrderByExpenseDateDesc(User user, Expense.Category category);
    
    /**
     * Find expenses by user for current month
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE) ORDER BY e.expenseDate DESC")
    List<Expense> findCurrentMonthExpensesByUser(@Param("user") User user);
    
    /**
     * Find expenses by user for specific month and year
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year ORDER BY e.expenseDate DESC")
    List<Expense> findByUserAndMonthYear(@Param("user") User user, @Param("month") int month, @Param("year") int year);
    
    /**
     * Calculate total expenses by user for current month
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE)")
    BigDecimal calculateCurrentMonthTotal(@Param("user") User user);
    
    /**
     * Calculate total expenses by user for specific month and year
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal calculateMonthlyTotal(@Param("user") User user, @Param("month") int month, @Param("year") int year);
    
    /**
     * Get category-wise spending for user in current month
     */
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE) GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryWiseSpendingCurrentMonth(@Param("user") User user);
    
    /**
     * Get category-wise spending for user in specific month
     */
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryWiseSpending(@Param("user") User user, @Param("month") int month, @Param("year") int year);
    
    /**
     * Get monthly spending trend for user (last 12 months)
     */
    @Query("SELECT MONTH(e.expenseDate), YEAR(e.expenseDate), SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseDate >= :startDate GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate) ORDER BY YEAR(e.expenseDate), MONTH(e.expenseDate)")
    List<Object[]> getMonthlySpendingTrend(@Param("user") User user, @Param("startDate") LocalDate startDate);
    
    /**
     * Get daily expenses for current month
     */
    @Query("SELECT DAY(e.expenseDate), SUM(e.amount) FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE) GROUP BY DAY(e.expenseDate) ORDER BY DAY(e.expenseDate)")
    List<Object[]> getDailyExpensesCurrentMonth(@Param("user") User user);
    
    /**
     * Count expenses by user for current month
     */
    @Query("SELECT COUNT(e) FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE)")
    long countCurrentMonthExpenses(@Param("user") User user);
    
    /**
     * Count total expenses by user
     */
    long countByUser(User user);
    
    /**
     * Find recent expenses by user (last N days)
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.expenseDate >= :date ORDER BY e.expenseDate DESC")
    List<Expense> findRecentExpenses(@Param("user") User user, @Param("date") LocalDate date);
    
    /**
     * Find top expenses by user (highest amounts)
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user ORDER BY e.amount DESC")
    List<Expense> findTopExpensesByAmount(@Param("user") User user);
    
    /**
     * Check if user has expenses for consecutive days
     */
    @Query("SELECT COUNT(DISTINCT e.expenseDate) FROM Expense e WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate")
    long countDistinctExpenseDays(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find expenses by description containing keyword
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY e.expenseDate DESC")
    List<Expense> findByUserAndDescriptionContaining(@Param("user") User user, @Param("keyword") String keyword);
    
    /**
     * Get average daily spending for user in current month
     */
    @Query("SELECT AVG(daily.total) FROM (SELECT SUM(e.amount) as total FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE) GROUP BY e.expenseDate) daily")
    BigDecimal getAverageDailySpending(@Param("user") User user);
    
    /**
     * Find highest spending category for user in current month
     */
    @Query("SELECT e.category FROM Expense e WHERE e.user = :user AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE) GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Expense.Category> findTopSpendingCategories(@Param("user") User user);
    
    /**
     * Get total expenses for all users (admin analytics)
     */
    @Query("SELECT SUM(e.amount) FROM Expense e")
    BigDecimal getTotalExpensesAllUsers();
    
    /**
     * Get user-wise total expenses (admin analytics)
     */
    @Query("SELECT e.user, SUM(e.amount) FROM Expense e GROUP BY e.user ORDER BY SUM(e.amount) DESC")
    List<Object[]> getUserWiseTotalExpenses();
}