package com.expensemate.repository;

import com.expensemate.entity.Budget;
import com.expensemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Budget entity operations
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    /**
     * Find budget by user and month/year
     */
    Optional<Budget> findByUserAndBudgetMonthAndBudgetYear(User user, Integer month, Integer year);
    
    /**
     * Find all budgets by user ordered by year and month
     */
    List<Budget> findByUserOrderByBudgetYearDescBudgetMonthDesc(User user);
    
    /**
     * Find current month budget for user
     */
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.budgetMonth = MONTH(CURRENT_DATE) AND b.budgetYear = YEAR(CURRENT_DATE)")
    Optional<Budget> findCurrentMonthBudget(@Param("user") User user);
    
    /**
     * Find budgets for user in specific year
     */
    List<Budget> findByUserAndBudgetYearOrderByBudgetMonth(User user, Integer year);
    
    /**
     * Find budgets where 80% alert not sent and spending >= 80%
     */
    @Query("SELECT b FROM Budget b WHERE b.alert80Sent = false AND (b.spentAmount / b.budgetAmount) >= 0.8")
    List<Budget> findBudgetsNeedingAlert80();
    
    /**
     * Find budgets where 100% alert not sent and spending >= 100%
     */
    @Query("SELECT b FROM Budget b WHERE b.alert100Sent = false AND (b.spentAmount / b.budgetAmount) >= 1.0")
    List<Budget> findBudgetsNeedingAlert100();
    
    /**
     * Find over-budget budgets
     */
    @Query("SELECT b FROM Budget b WHERE b.spentAmount > b.budgetAmount")
    List<Budget> findOverBudgets();
    
    /**
     * Find budgets by user where spending is under 80%
     */
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND (b.spentAmount / b.budgetAmount) < 0.8")
    List<Budget> findUnderBudgetsByUser(@Param("user") User user);
    
    /**
     * Count consecutive months user stayed within budget
     */
    @Query("SELECT COUNT(b) FROM Budget b WHERE b.user = :user AND b.spentAmount <= b.budgetAmount AND b.budgetYear = :year AND b.budgetMonth <= :month ORDER BY b.budgetYear DESC, b.budgetMonth DESC")
    long countConsecutiveWithinBudgetMonths(@Param("user") User user, @Param("year") Integer year, @Param("month") Integer month);
    
    /**
     * Find users who stayed within budget for 3+ consecutive months
     */
    @Query("SELECT DISTINCT b.user FROM Budget b WHERE b.spentAmount <= b.budgetAmount GROUP BY b.user HAVING COUNT(b) >= 3")
    List<User> findUsersWithConsecutiveBudgetSuccess();
    
    /**
     * Find budgets where user spent less than 80% (Budget Hero candidates)
     */
    @Query("SELECT b FROM Budget b WHERE (b.spentAmount / b.budgetAmount) < 0.8")
    List<Budget> findBudgetHeroCandidates();
    
    /**
     * Get average budget amount for user
     */
    @Query("SELECT AVG(b.budgetAmount) FROM Budget b WHERE b.user = :user")
    Double getAverageBudgetAmount(@Param("user") User user);
    
    /**
     * Get average spending percentage for user
     */
    @Query("SELECT AVG(b.spentAmount / b.budgetAmount * 100) FROM Budget b WHERE b.user = :user AND b.budgetAmount > 0")
    Double getAverageSpendingPercentage(@Param("user") User user);
    
    /**
     * Find recent budgets (last N months)
     */
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND (b.budgetYear * 12 + b.budgetMonth) >= :monthsSinceEpoch ORDER BY b.budgetYear DESC, b.budgetMonth DESC")
    List<Budget> findRecentBudgets(@Param("user") User user, @Param("monthsSinceEpoch") long monthsSinceEpoch);
    
    /**
     * Check if user has budget for specific month
     */
    boolean existsByUserAndBudgetMonthAndBudgetYear(User user, Integer month, Integer year);
    
    /**
     * Count total budgets by user
     */
    long countByUser(User user);
    
    /**
     * Find all budgets for current month (for admin analytics)
     */
    @Query("SELECT b FROM Budget b WHERE b.budgetMonth = MONTH(CURRENT_DATE) AND b.budgetYear = YEAR(CURRENT_DATE)")
    List<Budget> findAllCurrentMonthBudgets();
    
    /**
     * Get budget statistics for admin dashboard
     */
    @Query("SELECT COUNT(b), AVG(b.budgetAmount), AVG(b.spentAmount), AVG(b.spentAmount / b.budgetAmount * 100) FROM Budget b WHERE b.budgetMonth = MONTH(CURRENT_DATE) AND b.budgetYear = YEAR(CURRENT_DATE)")
    Object[] getCurrentMonthBudgetStats();
    
    /**
     * Find users who need budget setup for current month
     */
    @Query("SELECT u FROM User u WHERE u NOT IN (SELECT b.user FROM Budget b WHERE b.budgetMonth = MONTH(CURRENT_DATE) AND b.budgetYear = YEAR(CURRENT_DATE))")
    List<User> findUsersWithoutCurrentMonthBudget();
}