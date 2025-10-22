package com.expensemate.service;

import com.expensemate.entity.Badge;
import com.expensemate.entity.User;
import com.expensemate.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Badge entity operations and gamification logic
 */
@Service
@Transactional
public class BadgeService {
    
    @Autowired
    private BadgeRepository badgeRepository;
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Award badge to user
     */
    public Badge awardBadge(User user, Badge.BadgeType badgeType) {
        // Check if user already has this badge
        if (badgeRepository.existsByUserAndBadgeTypeAndActive(user, badgeType, true)) {
            return null; // Badge already exists
        }
        
        Badge badge = new Badge(badgeType, user);
        Badge savedBadge = badgeRepository.save(badge);
        
        // Send notification email
        emailService.sendBadgeAwardedNotification(user, badge);
        
        return savedBadge;
    }
    
    /**
     * Get all badges for user
     */
    public List<Badge> getBadgesByUser(User user) {
        return badgeRepository.findByUserOrderByEarnedAtDesc(user);
    }
    
    /**
     * Get active badges for user
     */
    public List<Badge> getActiveBadgesByUser(User user) {
        return badgeRepository.findByUserAndActiveOrderByEarnedAtDesc(user, true);
    }
    
    /**
     * Check if user has specific badge
     */
    public boolean hasBadge(User user, Badge.BadgeType badgeType) {
        return badgeRepository.existsByUserAndBadgeTypeAndActive(user, badgeType, true);
    }
    
    /**
     * Get badge by user and type
     */
    public Optional<Badge> getBadgeByUserAndType(User user, Badge.BadgeType badgeType) {
        return badgeRepository.findByUserAndBadgeType(user, badgeType);
    }
    
    /**
     * Count total badges for user
     */
    public long getBadgeCount(User user) {
        return badgeRepository.countByUserAndActive(user, true);
    }
    
    /**
     * Calculate total points for user
     */
    public Long getTotalPoints(User user) {
        Long points = badgeRepository.calculateTotalPointsByUser(user);
        return points != null ? points : 0L;
    }
    
    /**
     * Get user's rank by points
     */
    public Long getUserRank(User user) {
        return badgeRepository.getUserRankByPoints(user);
    }
    
    /**
     * Get recently earned badges
     */
    public List<Badge> getRecentlyEarnedBadges(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return badgeRepository.findRecentlyEarnedBadges(date);
    }
    
    /**
     * Get recent badges for user
     */
    public List<Badge> getRecentBadgesByUser(User user, int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return badgeRepository.findRecentBadgesByUser(user, date);
    }
    
    /**
     * Check and award all eligible badges for user
     */
    public void checkAndAwardBadges(User user) {
        checkBudgetHeroBadge(user);
        checkConsistentSaverBadge(user);
        checkSpendingStreakMaintainerBadge(user);
        checkExpenseTrackerBadge(user);
        checkCategoryMasterBadge(user);
        checkMonthlyPlannerBadge(user);
        checkSavingsChampionBadge(user);
        checkEarlyBirdBadge(user);
    }
    
    /**
     * Check and award Budget Hero badge (spent < 80% of budget)
     */
    private void checkBudgetHeroBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.BUDGET_HERO)) {
            return;
        }
        
        Optional<com.expensemate.entity.Budget> currentBudget = budgetService.getCurrentMonthBudget(user);
        if (currentBudget.isPresent()) {
            com.expensemate.entity.Budget budget = currentBudget.get();
            if (budget.getSpentPercentage() < 80.0 && budget.getSpentPercentage() > 0) {
                awardBadge(user, Badge.BadgeType.BUDGET_HERO);
            }
        }
    }
    
    /**
     * Check and award Consistent Saver badge (3+ consecutive months within budget)
     */
    private void checkConsistentSaverBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.CONSISTENT_SAVER)) {
            return;
        }
        
        if (budgetService.hasConsecutiveBudgetSuccess(user, 3)) {
            awardBadge(user, Badge.BadgeType.CONSISTENT_SAVER);
        }
    }
    
    /**
     * Check and award Spending Streak Maintainer badge (7+ consecutive days of expenses)
     */
    private void checkSpendingStreakMaintainerBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.SPENDING_STREAK_MAINTAINER)) {
            return;
        }
        
        if (expenseService.hasConsecutiveDailyExpenses(user, 7)) {
            awardBadge(user, Badge.BadgeType.SPENDING_STREAK_MAINTAINER);
        }
    }
    
    /**
     * Check and award Expense Tracker badge (50+ expenses)
     */
    private void checkExpenseTrackerBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.EXPENSE_TRACKER)) {
            return;
        }
        
        if (expenseService.getTotalExpenseCount(user) >= 50) {
            awardBadge(user, Badge.BadgeType.EXPENSE_TRACKER);
        }
    }
    
    /**
     * Check and award Category Master badge (used all categories)
     */
    private void checkCategoryMasterBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.CATEGORY_MASTER)) {
            return;
        }
        
        if (expenseService.hasUsedAllCategories(user)) {
            awardBadge(user, Badge.BadgeType.CATEGORY_MASTER);
        }
    }
    
    /**
     * Check and award Monthly Planner badge (6+ budgets)
     */
    private void checkMonthlyPlannerBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.MONTHLY_PLANNER)) {
            return;
        }
        
        if (budgetService.getBudgetCount(user) >= 6) {
            awardBadge(user, Badge.BadgeType.MONTHLY_PLANNER);
        }
    }
    
    /**
     * Check and award Savings Champion badge (saved 50%+ of budget)
     */
    private void checkSavingsChampionBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.SAVINGS_CHAMPION)) {
            return;
        }
        
        Optional<com.expensemate.entity.Budget> currentBudget = budgetService.getCurrentMonthBudget(user);
        if (currentBudget.isPresent()) {
            com.expensemate.entity.Budget budget = currentBudget.get();
            if (budget.getSpentPercentage() <= 50.0 && budget.getSpentPercentage() > 0) {
                awardBadge(user, Badge.BadgeType.SAVINGS_CHAMPION);
            }
        }
    }
    
    /**
     * Check and award Early Bird badge (first expense within first week)
     */
    private void checkEarlyBirdBadge(User user) {
        if (hasBadge(user, Badge.BadgeType.EARLY_BIRD)) {
            return;
        }
        
        // Check if user registered within last week and has expenses
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        if (user.getCreatedAt().isAfter(oneWeekAgo) && expenseService.getTotalExpenseCount(user) > 0) {
            awardBadge(user, Badge.BadgeType.EARLY_BIRD);
        }
    }
    
    /**
     * Get badge statistics for user
     */
    public BadgeStats getBadgeStats(User user) {
        long totalBadges = getBadgeCount(user);
        Long totalPoints = getTotalPoints(user);
        Long rank = getUserRank(user);
        
        List<Badge> recentBadges = getRecentBadgesByUser(user, 30);
        
        return new BadgeStats(totalBadges, totalPoints, rank, recentBadges.size());
    }
    
    /**
     * Get top users by badge count
     */
    public List<Object[]> getTopUsersByBadgeCount() {
        return badgeRepository.findTopUsersByBadgeCount();
    }
    
    /**
     * Get top users by points
     */
    public List<Object[]> getTopUsersByPoints() {
        return badgeRepository.findTopUsersByPoints();
    }
    
    /**
     * Get badge distribution statistics
     */
    public List<Object[]> getBadgeDistribution() {
        return badgeRepository.getBadgeDistribution();
    }
    
    /**
     * Process all eligible users for badges (scheduled task)
     */
    public void processAllEligibleBadges() {
        // Budget Hero badges
        List<User> budgetHeroUsers = badgeRepository.findBudgetHeroEligibleUsers();
        for (User user : budgetHeroUsers) {
            awardBadge(user, Badge.BadgeType.BUDGET_HERO);
        }
        
        // Consistent Saver badges
        List<User> consistentSaverUsers = badgeRepository.findConsistentSaverEligibleUsers();
        for (User user : consistentSaverUsers) {
            awardBadge(user, Badge.BadgeType.CONSISTENT_SAVER);
        }
        
        // Expense Tracker badges
        List<User> expenseTrackerUsers = badgeRepository.findExpenseTrackerEligibleUsers();
        for (User user : expenseTrackerUsers) {
            awardBadge(user, Badge.BadgeType.EXPENSE_TRACKER);
        }
        
        // Category Master badges
        List<User> categoryMasterUsers = badgeRepository.findCategoryMasterEligibleUsers();
        for (User user : categoryMasterUsers) {
            awardBadge(user, Badge.BadgeType.CATEGORY_MASTER);
        }
        
        // Monthly Planner badges
        List<User> monthlyPlannerUsers = badgeRepository.findMonthlyPlannerEligibleUsers();
        for (User user : monthlyPlannerUsers) {
            awardBadge(user, Badge.BadgeType.MONTHLY_PLANNER);
        }
        
        // Savings Champion badges
        List<User> savingsChampionUsers = badgeRepository.findSavingsChampionEligibleUsers();
        for (User user : savingsChampionUsers) {
            awardBadge(user, Badge.BadgeType.SAVINGS_CHAMPION);
        }
    }
    
    /**
     * Deactivate badge
     */
    public void deactivateBadge(Long badgeId, User user) {
        Optional<Badge> badgeOpt = badgeRepository.findById(badgeId);
        if (badgeOpt.isPresent()) {
            Badge badge = badgeOpt.get();
            
            // Verify ownership
            if (!badge.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to deactivate this badge");
            }
            
            badge.setActive(false);
            badgeRepository.save(badge);
        }
    }
    
    /**
     * Get user's badge level based on points
     */
    public String getUserBadgeLevel(User user) {
        Long points = getTotalPoints(user);
        
        if (points >= 1000) return "Diamond";
        else if (points >= 500) return "Platinum";
        else if (points >= 250) return "Gold";
        else if (points >= 100) return "Silver";
        else if (points >= 50) return "Bronze";
        else return "Beginner";
    }
    
    /**
     * Inner class for badge statistics
     */
    public static class BadgeStats {
        private final long totalBadges;
        private final Long totalPoints;
        private final Long rank;
        private final long recentBadges;
        
        public BadgeStats(long totalBadges, Long totalPoints, Long rank, long recentBadges) {
            this.totalBadges = totalBadges;
            this.totalPoints = totalPoints;
            this.rank = rank;
            this.recentBadges = recentBadges;
        }
        
        // Getters
        public long getTotalBadges() { return totalBadges; }
        public Long getTotalPoints() { return totalPoints; }
        public Long getRank() { return rank; }
        public long getRecentBadges() { return recentBadges; }
    }
}