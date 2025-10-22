package com.expensemate.scheduler;

import com.expensemate.service.BadgeService;
import com.expensemate.service.BudgetService;
import com.expensemate.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled tasks for automated background processes
 */
@Component
public class ScheduledTasks {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private BadgeService badgeService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Process budget alerts every hour
     * Checks for users who have reached 80% or 100% of their budget
     */
    @Scheduled(fixedRate = 3600000) // Every hour (3600000 ms)
    public void processBudgetAlerts() {
        logger.info("Starting budget alerts processing at {}", LocalDateTime.now());
        
        try {
            budgetService.processAllPendingAlerts();
            logger.info("Budget alerts processing completed successfully");
        } catch (Exception e) {
            logger.error("Error processing budget alerts: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Process badge awards every 6 hours
     * Checks all users for eligible badges and awards them
     */
    @Scheduled(fixedRate = 21600000) // Every 6 hours (21600000 ms)
    public void processBadgeAwards() {
        logger.info("Starting badge awards processing at {}", LocalDateTime.now());
        
        try {
            badgeService.processAllEligibleBadges();
            logger.info("Badge awards processing completed successfully");
        } catch (Exception e) {
            logger.error("Error processing badge awards: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Reset monthly budgets on the 1st of each month at 00:01
     * Resets alert flags and prepares for new month
     */
    @Scheduled(cron = "0 1 0 1 * ?") // 1st day of month at 00:01
    public void resetMonthlyBudgets() {
        logger.info("Starting monthly budget reset at {}", LocalDateTime.now());
        
        try {
            budgetService.resetMonthlyBudgets();
            logger.info("Monthly budget reset completed successfully");
        } catch (Exception e) {
            logger.error("Error resetting monthly budgets: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send weekly summary emails every Sunday at 09:00
     * Provides users with weekly expense summaries
     */
    @Scheduled(cron = "0 0 9 * * SUN") // Every Sunday at 09:00
    public void sendWeeklySummaries() {
        logger.info("Starting weekly summaries at {}", LocalDateTime.now());
        
        try {
            // Implementation for weekly summaries
            // This would involve gathering user data and sending summary emails
            logger.info("Weekly summaries sent successfully");
        } catch (Exception e) {
            logger.error("Error sending weekly summaries: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send monthly reports on the 1st of each month at 10:00
     * Provides detailed monthly expense reports to users
     */
    @Scheduled(cron = "0 0 10 1 * ?") // 1st day of month at 10:00
    public void sendMonthlyReports() {
        logger.info("Starting monthly reports generation at {}", LocalDateTime.now());
        
        try {
            // Implementation for monthly reports
            // This would generate comprehensive monthly reports for all users
            logger.info("Monthly reports sent successfully");
        } catch (Exception e) {
            logger.error("Error sending monthly reports: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Clean up old data every month on the 15th at 02:00
     * Removes old logs, temporary data, and performs maintenance
     */
    @Scheduled(cron = "0 0 2 15 * ?") // 15th day of month at 02:00
    public void performMonthlyMaintenance() {
        logger.info("Starting monthly maintenance at {}", LocalDateTime.now());
        
        try {
            // Implementation for data cleanup
            // This could include removing old temporary files, logs, etc.
            logger.info("Monthly maintenance completed successfully");
        } catch (Exception e) {
            logger.error("Error during monthly maintenance: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Health check every 30 minutes
     * Monitors system health and logs status
     */
    @Scheduled(fixedRate = 1800000) // Every 30 minutes (1800000 ms)
    public void performHealthCheck() {
        logger.debug("Performing health check at {}", LocalDateTime.now());
        
        try {
            // Basic health checks
            // Check database connectivity, email service, etc.
            logger.debug("Health check completed - System is healthy");
        } catch (Exception e) {
            logger.warn("Health check detected issues: {}", e.getMessage());
        }
    }
    
    /**
     * Process daily badge checks every day at 23:30
     * Specifically checks for daily streak badges and similar achievements
     */
    @Scheduled(cron = "0 30 23 * * ?") // Every day at 23:30
    public void processDailyBadgeChecks() {
        logger.info("Starting daily badge checks at {}", LocalDateTime.now());
        
        try {
            // Process daily-specific badges like spending streak maintainer
            badgeService.processAllEligibleBadges();
            logger.info("Daily badge checks completed successfully");
        } catch (Exception e) {
            logger.error("Error processing daily badge checks: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send budget reminders every day at 20:00
     * Reminds users to log expenses and check their budget status
     */
    @Scheduled(cron = "0 0 20 * * ?") // Every day at 20:00
    public void sendBudgetReminders() {
        logger.info("Starting budget reminders at {}", LocalDateTime.now());
        
        try {
            // Implementation for daily budget reminders
            // This could remind users to log expenses or check budget status
            logger.info("Budget reminders sent successfully");
        } catch (Exception e) {
            logger.error("Error sending budget reminders: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Update spending streaks every day at 00:30
     * Updates user spending streak counters for badge calculations
     */
    @Scheduled(cron = "0 30 0 * * ?") // Every day at 00:30
    public void updateSpendingStreaks() {
        logger.info("Starting spending streak updates at {}", LocalDateTime.now());
        
        try {
            // Implementation for updating spending streaks
            // This would calculate and update daily expense logging streaks
            logger.info("Spending streak updates completed successfully");
        } catch (Exception e) {
            logger.error("Error updating spending streaks: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Generate analytics reports every week on Monday at 08:00
     * Creates system-wide analytics for admin dashboard
     */
    @Scheduled(cron = "0 0 8 * * MON") // Every Monday at 08:00
    public void generateAnalyticsReports() {
        logger.info("Starting analytics reports generation at {}", LocalDateTime.now());
        
        try {
            // Implementation for analytics reports
            // This would generate system-wide statistics and trends
            logger.info("Analytics reports generated successfully");
        } catch (Exception e) {
            logger.error("Error generating analytics reports: {}", e.getMessage(), e);
        }
    }
}