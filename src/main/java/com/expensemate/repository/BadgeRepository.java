package com.expensemate.repository;

import com.expensemate.entity.Badge;
import com.expensemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Badge entity operations supporting gamification
 */
@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    /**
     * Find all badges by user
     */
    List<Badge> findByUserOrderByEarnedAtDesc(User user);
    
    /**
     * Find active badges by user
     */
    List<Badge> findByUserAndActiveOrderByEarnedAtDesc(User user, boolean active);
    
    /**
     * Find badge by user and badge type
     */
    Optional<Badge> findByUserAndBadgeType(User user, Badge.BadgeType badgeType);
    
    /**
     * Check if user has specific badge
     */
    boolean existsByUserAndBadgeType(User user, Badge.BadgeType badgeType);
    
    /**
     * Check if user has active badge of specific type
     */
    boolean existsByUserAndBadgeTypeAndActive(User user, Badge.BadgeType badgeType, boolean active);
    
    /**
     * Count total badges by user
     */
    long countByUser(User user);
    
    /**
     * Count active badges by user
     */
    long countByUserAndActive(User user, boolean active);
    
    /**
     * Find users with specific badge type
     */
    @Query("SELECT b.user FROM Badge b WHERE b.badgeType = :badgeType AND b.active = true")
    List<User> findUsersByBadgeType(@Param("badgeType") Badge.BadgeType badgeType);
    
    /**
     * Find recently earned badges (last N days)
     */
    @Query("SELECT b FROM Badge b WHERE b.earnedAt >= :date ORDER BY b.earnedAt DESC")
    List<Badge> findRecentlyEarnedBadges(@Param("date") LocalDateTime date);
    
    /**
     * Find badges earned by user in last N days
     */
    @Query("SELECT b FROM Badge b WHERE b.user = :user AND b.earnedAt >= :date ORDER BY b.earnedAt DESC")
    List<Badge> findRecentBadgesByUser(@Param("user") User user, @Param("date") LocalDateTime date);
    
    /**
     * Get badge statistics for user
     */
    @Query("SELECT b.badgeType, COUNT(b) FROM Badge b WHERE b.user = :user AND b.active = true GROUP BY b.badgeType")
    List<Object[]> getBadgeStatsByUser(@Param("user") User user);
    
    /**
     * Calculate total points earned by user
     */
    @Query("SELECT SUM(CASE " +
           "WHEN b.badgeType = 'BUDGET_HERO' THEN 100 " +
           "WHEN b.badgeType = 'CONSISTENT_SAVER' THEN 200 " +
           "WHEN b.badgeType = 'SPENDING_STREAK_MAINTAINER' THEN 75 " +
           "WHEN b.badgeType = 'EXPENSE_TRACKER' THEN 50 " +
           "WHEN b.badgeType = 'CATEGORY_MASTER' THEN 80 " +
           "WHEN b.badgeType = 'MONTHLY_PLANNER' THEN 120 " +
           "WHEN b.badgeType = 'SAVINGS_CHAMPION' THEN 300 " +
           "WHEN b.badgeType = 'EARLY_BIRD' THEN 25 " +
           "ELSE 0 END) FROM Badge b WHERE b.user = :user AND b.active = true")
    Long calculateTotalPointsByUser(@Param("user") User user);
    
    /**
     * Find top users by badge count
     */
    @Query("SELECT b.user, COUNT(b) as badgeCount FROM Badge b WHERE b.active = true GROUP BY b.user ORDER BY badgeCount DESC")
    List<Object[]> findTopUsersByBadgeCount();
    
    /**
     * Find top users by total points
     */
    @Query("SELECT b.user, SUM(CASE " +
           "WHEN b.badgeType = 'BUDGET_HERO' THEN 100 " +
           "WHEN b.badgeType = 'CONSISTENT_SAVER' THEN 200 " +
           "WHEN b.badgeType = 'SPENDING_STREAK_MAINTAINER' THEN 75 " +
           "WHEN b.badgeType = 'EXPENSE_TRACKER' THEN 50 " +
           "WHEN b.badgeType = 'CATEGORY_MASTER' THEN 80 " +
           "WHEN b.badgeType = 'MONTHLY_PLANNER' THEN 120 " +
           "WHEN b.badgeType = 'SAVINGS_CHAMPION' THEN 300 " +
           "WHEN b.badgeType = 'EARLY_BIRD' THEN 25 " +
           "ELSE 0 END) as totalPoints FROM Badge b WHERE b.active = true GROUP BY b.user ORDER BY totalPoints DESC")
    List<Object[]> findTopUsersByPoints();
    
    /**
     * Get badge distribution statistics
     */
    @Query("SELECT b.badgeType, COUNT(b) FROM Badge b WHERE b.active = true GROUP BY b.badgeType ORDER BY COUNT(b) DESC")
    List<Object[]> getBadgeDistribution();
    
    /**
     * Find users eligible for Budget Hero badge (spent < 80% of budget)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.budgets budget WHERE budget.spentAmount / budget.budgetAmount < 0.8 AND NOT EXISTS (SELECT b FROM Badge b WHERE b.user = u AND b.badgeType = 'BUDGET_HERO' AND b.active = true)")
    List<User> findBudgetHeroEligibleUsers();
    
    /**
     * Find users eligible for Consistent Saver badge
     */
    @Query("SELECT u FROM User u WHERE (SELECT COUNT(budget) FROM Budget budget WHERE budget.user = u AND budget.spentAmount <= budget.budgetAmount) >= 3 AND NOT EXISTS (SELECT b FROM Badge b WHERE b.user = u AND b.badgeType = 'CONSISTENT_SAVER' AND b.active = true)")
    List<User> findConsistentSaverEligibleUsers();
    
    /**
     * Find users eligible for Expense Tracker badge (50+ expenses)
     */
    @Query("SELECT u FROM User u WHERE (SELECT COUNT(e) FROM Expense e WHERE e.user = u) >= 50 AND NOT EXISTS (SELECT b FROM Badge b WHERE b.user = u AND b.badgeType = 'EXPENSE_TRACKER' AND b.active = true)")
    List<User> findExpenseTrackerEligibleUsers();
    
    /**
     * Find users eligible for Category Master badge (used all categories)
     */
    @Query("SELECT u FROM User u WHERE (SELECT COUNT(DISTINCT e.category) FROM Expense e WHERE e.user = u) >= 12 AND NOT EXISTS (SELECT b FROM Badge b WHERE b.user = u AND b.badgeType = 'CATEGORY_MASTER' AND b.active = true)")
    List<User> findCategoryMasterEligibleUsers();
    
    /**
     * Find users eligible for Monthly Planner badge (6+ budgets)
     */
    @Query("SELECT u FROM User u WHERE (SELECT COUNT(budget) FROM Budget budget WHERE budget.user = u) >= 6 AND NOT EXISTS (SELECT b FROM Badge b WHERE b.user = u AND b.badgeType = 'MONTHLY_PLANNER' AND b.active = true)")
    List<User> findMonthlyPlannerEligibleUsers();
    
    /**
     * Find users eligible for Savings Champion badge (saved 50%+ of budget)
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.budgets budget WHERE budget.spentAmount / budget.budgetAmount <= 0.5 AND NOT EXISTS (SELECT b FROM Badge b WHERE b.user = u AND b.badgeType = 'SAVINGS_CHAMPION' AND b.active = true)")
    List<User> findSavingsChampionEligibleUsers();
    
    /**
     * Deactivate all badges for user (for testing or reset)
     */
    @Query("UPDATE Badge b SET b.active = false WHERE b.user = :user")
    void deactivateAllBadgesForUser(@Param("user") User user);
    
    /**
     * Get user's badge level/rank based on total points
     */
    @Query("SELECT COUNT(DISTINCT u2.id) + 1 FROM User u2 WHERE (SELECT COALESCE(SUM(CASE " +
           "WHEN b2.badgeType = 'BUDGET_HERO' THEN 100 " +
           "WHEN b2.badgeType = 'CONSISTENT_SAVER' THEN 200 " +
           "WHEN b2.badgeType = 'SPENDING_STREAK_MAINTAINER' THEN 75 " +
           "WHEN b2.badgeType = 'EXPENSE_TRACKER' THEN 50 " +
           "WHEN b2.badgeType = 'CATEGORY_MASTER' THEN 80 " +
           "WHEN b2.badgeType = 'MONTHLY_PLANNER' THEN 120 " +
           "WHEN b2.badgeType = 'SAVINGS_CHAMPION' THEN 300 " +
           "WHEN b2.badgeType = 'EARLY_BIRD' THEN 25 " +
           "ELSE 0 END), 0) FROM Badge b2 WHERE b2.user = u2 AND b2.active = true) > " +
           "(SELECT COALESCE(SUM(CASE " +
           "WHEN b.badgeType = 'BUDGET_HERO' THEN 100 " +
           "WHEN b.badgeType = 'CONSISTENT_SAVER' THEN 200 " +
           "WHEN b.badgeType = 'SPENDING_STREAK_MAINTAINER' THEN 75 " +
           "WHEN b.badgeType = 'EXPENSE_TRACKER' THEN 50 " +
           "WHEN b.badgeType = 'CATEGORY_MASTER' THEN 80 " +
           "WHEN b.badgeType = 'MONTHLY_PLANNER' THEN 120 " +
           "WHEN b.badgeType = 'SAVINGS_CHAMPION' THEN 300 " +
           "WHEN b.badgeType = 'EARLY_BIRD' THEN 25 " +
           "ELSE 0 END), 0) FROM Badge b WHERE b.user = :user AND b.active = true)")
    Long getUserRankByPoints(@Param("user") User user);
}