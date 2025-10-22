package com.expensemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Badge entity representing gamification achievements for users
 */
@Entity
@Table(name = "badges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "badge_type"})
})
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Badge type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private BadgeType badgeType;
    
    @Column(name = "earned_at")
    private LocalDateTime earnedAt;
    
    @Column(name = "is_active")
    private boolean active = true;
    
    @Column(name = "streak_count")
    private Integer streakCount = 0;
    
    @Column(name = "achievement_date")
    private LocalDateTime achievementDate;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors
    public Badge() {
        this.earnedAt = LocalDateTime.now();
        this.achievementDate = LocalDateTime.now();
    }
    
    public Badge(BadgeType badgeType, User user) {
        this();
        this.badgeType = badgeType;
        this.user = user;
    }
    
    public Badge(BadgeType badgeType, User user, Integer streakCount) {
        this(badgeType, user);
        this.streakCount = streakCount;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BadgeType getBadgeType() { return badgeType; }
    public void setBadgeType(BadgeType badgeType) { this.badgeType = badgeType; }
    
    public LocalDateTime getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public Integer getStreakCount() { return streakCount; }
    public void setStreakCount(Integer streakCount) { this.streakCount = streakCount; }
    
    public LocalDateTime getAchievementDate() { return achievementDate; }
    public void setAchievementDate(LocalDateTime achievementDate) { this.achievementDate = achievementDate; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    // Utility methods
    public String getBadgeName() {
        return badgeType.getName();
    }
    
    public String getBadgeDescription() {
        return badgeType.getDescription();
    }
    
    public String getBadgeIcon() {
        return badgeType.getIcon();
    }
    
    public String getBadgeColor() {
        return badgeType.getColor();
    }
    
    public String getBadgeLevel() {
        return badgeType.getLevel();
    }
    
    public int getBadgePoints() {
        return badgeType.getPoints();
    }
    
    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", badgeType=" + badgeType +
                ", earnedAt=" + earnedAt +
                ", active=" + active +
                ", streakCount=" + streakCount +
                '}';
    }
    
    // BadgeType enum with detailed properties
    public enum BadgeType {
        BUDGET_HERO(
            "Budget Hero", 
            "Spent less than 80% of monthly budget", 
            "🏆", 
            "#FFD700", 
            "Gold", 
            100
        ),
        CONSISTENT_SAVER(
            "Consistent Saver", 
            "Stayed within budget for 3 consecutive months", 
            "💰", 
            "#32CD32", 
            "Platinum", 
            200
        ),
        SPENDING_STREAK_MAINTAINER(
            "Spending Streak Maintainer", 
            "Logged expenses daily for 7+ days", 
            "📊", 
            "#4169E1", 
            "Silver", 
            75
        ),
        EXPENSE_TRACKER(
            "Expense Tracker", 
            "Added 50+ expenses", 
            "📝", 
            "#FF6347", 
            "Bronze", 
            50
        ),
        CATEGORY_MASTER(
            "Category Master", 
            "Used all expense categories", 
            "🎯", 
            "#9370DB", 
            "Silver", 
            80
        ),
        MONTHLY_PLANNER(
            "Monthly Planner", 
            "Set budget for 6 consecutive months", 
            "📅", 
            "#20B2AA", 
            "Gold", 
            120
        ),
        SAVINGS_CHAMPION(
            "Savings Champion", 
            "Saved 50% or more of budget for a month", 
            "🏅", 
            "#FF1493", 
            "Diamond", 
            300
        ),
        EARLY_BIRD(
            "Early Bird", 
            "First expense logged within first week of joining", 
            "🌅", 
            "#FFA500", 
            "Bronze", 
            25
        );
        
        private final String name;
        private final String description;
        private final String icon;
        private final String color;
        private final String level;
        private final int points;
        
        BadgeType(String name, String description, String icon, String color, String level, int points) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.color = color;
            this.level = level;
            this.points = points;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public String getColor() { return color; }
        public String getLevel() { return level; }
        public int getPoints() { return points; }
        
        public String getDisplayText() {
            return icon + " " + name;
        }
        
        public String getFullDescription() {
            return name + " (" + level + " - " + points + " points): " + description;
        }
    }
}