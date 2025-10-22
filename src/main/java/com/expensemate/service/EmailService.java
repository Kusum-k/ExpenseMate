package com.expensemate.service;

import com.expensemate.entity.Badge;
import com.expensemate.entity.Budget;
import com.expensemate.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

/**
 * Service class for sending automated emails and notifications
 */
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    private static final String APP_NAME = "ExpenseMate";
    
    /**
     * Send budget 80% alert email
     */
    public void sendBudgetAlert80(Budget budget) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(budget.getUser().getEmail());
            helper.setSubject("⚠️ Budget Alert: 80% Limit Reached - " + APP_NAME);
            
            String htmlContent = createBudgetAlert80Html(budget);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error and send simple text email as fallback
            sendSimpleBudgetAlert80(budget);
        }
    }
    
    /**
     * Send budget 100% alert email
     */
    public void sendBudgetAlert100(Budget budget) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(budget.getUser().getEmail());
            helper.setSubject("🚨 Budget Alert: Limit Exceeded - " + APP_NAME);
            
            String htmlContent = createBudgetAlert100Html(budget);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error and send simple text email as fallback
            sendSimpleBudgetAlert100(budget);
        }
    }
    
    /**
     * Send badge awarded notification
     */
    public void sendBadgeAwardedNotification(User user, Badge badge) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("🏆 Congratulations! New Badge Earned - " + APP_NAME);
            
            String htmlContent = createBadgeAwardedHtml(user, badge);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error and send simple text email as fallback
            sendSimpleBadgeNotification(user, badge);
        }
    }
    
    /**
     * Send welcome email to new user
     */
    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("🎉 Welcome to " + APP_NAME + " - Start Your Financial Journey!");
            
            String htmlContent = createWelcomeEmailHtml(user);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error and send simple text email as fallback
            sendSimpleWelcomeEmail(user);
        }
    }
    
    /**
     * Send monthly report email
     */
    public void sendMonthlyReport(User user, String reportContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("📊 Your Monthly Expense Report - " + APP_NAME);
            
            String htmlContent = createMonthlyReportHtml(user, reportContent);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error
            System.err.println("Failed to send monthly report email to: " + user.getEmail());
        }
    }
    
    // HTML Email Templates
    
    private String createBudgetAlert80Html(Budget budget) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .alert-box { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .progress-bar { background: #e9ecef; height: 20px; border-radius: 10px; overflow: hidden; margin: 10px 0; }
                    .progress-fill { background: #ffc107; height: 100%%; transition: width 0.3s ease; }
                    .btn { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⚠️ Budget Alert</h1>
                        <p>You've reached 80%% of your monthly budget</p>
                    </div>
                    <div class="content">
                        <div class="alert-box">
                            <h3>Hello %s!</h3>
                            <p>Your spending for <strong>%s %d</strong> has reached <strong>%.1f%%</strong> of your budget.</p>
                        </div>
                        
                        <h4>Budget Overview:</h4>
                        <ul>
                            <li><strong>Budget Amount:</strong> %s</li>
                            <li><strong>Spent Amount:</strong> %s</li>
                            <li><strong>Remaining:</strong> %s</li>
                        </ul>
                        
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: %.1f%%;"></div>
                        </div>
                        
                        <p>💡 <strong>Tip:</strong> Consider reviewing your recent expenses and planning your remaining budget carefully to stay within limits.</p>
                        
                        <a href="http://localhost:8080/user/dashboard" class="btn">View Dashboard</a>
                    </div>
                    <div class="footer">
                        <p>This is an automated message from %s. Stay on track with your financial goals!</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            budget.getUser().getFullName(),
            budget.getMonthName(),
            budget.getBudgetYear(),
            budget.getSpentPercentage(),
            budget.getFormattedBudgetAmount(),
            budget.getFormattedSpentAmount(),
            budget.getFormattedRemainingAmount(),
            budget.getSpentPercentage(),
            APP_NAME
        );
    }
    
    private String createBudgetAlert100Html(Budget budget) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #ff6b6b 0%%, #ee5a24 100%%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .alert-box { background: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .progress-bar { background: #e9ecef; height: 20px; border-radius: 10px; overflow: hidden; margin: 10px 0; }
                    .progress-fill { background: #dc3545; height: 100%%; transition: width 0.3s ease; }
                    .btn { display: inline-block; background: #dc3545; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚨 Budget Exceeded!</h1>
                        <p>You've exceeded your monthly budget limit</p>
                    </div>
                    <div class="content">
                        <div class="alert-box">
                            <h3>Hello %s!</h3>
                            <p>Your spending for <strong>%s %d</strong> has reached <strong>%.1f%%</strong> of your budget.</p>
                            <p><strong>You have exceeded your budget by %s</strong></p>
                        </div>
                        
                        <h4>Budget Overview:</h4>
                        <ul>
                            <li><strong>Budget Amount:</strong> %s</li>
                            <li><strong>Spent Amount:</strong> %s</li>
                            <li><strong>Over Budget:</strong> %s</li>
                        </ul>
                        
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: 100%%;"></div>
                        </div>
                        
                        <p>💡 <strong>Recommendation:</strong> Review your expenses and consider adjusting your spending for the remainder of the month.</p>
                        
                        <a href="http://localhost:8080/user/dashboard" class="btn">Review Expenses</a>
                    </div>
                    <div class="footer">
                        <p>This is an automated message from %s. Take control of your finances!</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            budget.getUser().getFullName(),
            budget.getMonthName(),
            budget.getBudgetYear(),
            budget.getSpentPercentage(),
            budget.getFormattedRemainingAmount().replace("-", ""),
            budget.getFormattedBudgetAmount(),
            budget.getFormattedSpentAmount(),
            budget.getFormattedRemainingAmount().replace("-", ""),
            APP_NAME
        );
    }
    
    private String createBadgeAwardedHtml(User user, Badge badge) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #ffd700 0%%, #ffb347 100%%); color: #333; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .badge-box { background: white; border: 2px solid %s; padding: 20px; border-radius: 10px; text-align: center; margin: 20px 0; }
                    .badge-icon { font-size: 48px; margin: 10px 0; }
                    .btn { display: inline-block; background: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🏆 Congratulations!</h1>
                        <p>You've earned a new badge!</p>
                    </div>
                    <div class="content">
                        <div class="badge-box">
                            <div class="badge-icon">%s</div>
                            <h2>%s</h2>
                            <p><strong>%s Level</strong> • %d Points</p>
                            <p>%s</p>
                        </div>
                        
                        <p>Hello <strong>%s</strong>!</p>
                        <p>Great job on your financial management! You've successfully earned the <strong>%s</strong> badge.</p>
                        <p>Keep up the excellent work and continue building healthy financial habits!</p>
                        
                        <a href="http://localhost:8080/user/badges" class="btn">View All Badges</a>
                    </div>
                    <div class="footer">
                        <p>This is an automated message from %s. Keep achieving your financial goals!</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            badge.getBadgeColor(),
            badge.getBadgeIcon(),
            badge.getBadgeName(),
            badge.getBadgeLevel(),
            badge.getBadgePoints(),
            badge.getBadgeDescription(),
            user.getFullName(),
            badge.getBadgeName(),
            APP_NAME
        );
    }
    
    private String createWelcomeEmailHtml(User user) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .feature-box { background: white; padding: 15px; margin: 10px 0; border-radius: 5px; border-left: 4px solid #667eea; }
                    .btn { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to %s!</h1>
                        <p>Your smart expense tracking journey begins now</p>
                    </div>
                    <div class="content">
                        <h3>Hello %s!</h3>
                        <p>Welcome to ExpenseMate! We're excited to help you take control of your finances with our smart expense tracking and gamified progress system.</p>
                        
                        <h4>🚀 Get Started:</h4>
                        <div class="feature-box">
                            <strong>📊 Track Expenses:</strong> Add your daily expenses with easy categorization
                        </div>
                        <div class="feature-box">
                            <strong>💰 Set Budgets:</strong> Create monthly budgets and get automatic alerts
                        </div>
                        <div class="feature-box">
                            <strong>🏆 Earn Badges:</strong> Achieve financial goals and unlock achievements
                        </div>
                        <div class="feature-box">
                            <strong>📈 View Analytics:</strong> Get insights with interactive charts and reports
                        </div>
                        
                        <p>💡 <strong>Pro Tip:</strong> Start by adding your first expense and setting up a monthly budget to unlock the full potential of ExpenseMate!</p>
                        
                        <a href="http://localhost:8080/user/dashboard" class="btn">Go to Dashboard</a>
                    </div>
                    <div class="footer">
                        <p>This is an automated welcome message from %s. Happy expense tracking!</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            APP_NAME,
            user.getFullName(),
            APP_NAME
        );
    }
    
    private String createMonthlyReportHtml(User user, String reportContent) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #4ecdc4 0%%, #44a08d 100%%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .btn { display: inline-block; background: #4ecdc4; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📊 Monthly Report</h1>
                        <p>Your expense summary and insights</p>
                    </div>
                    <div class="content">
                        <h3>Hello %s!</h3>
                        <p>Here's your monthly expense report with detailed insights and recommendations.</p>
                        
                        %s
                        
                        <a href="http://localhost:8080/user/reports" class="btn">View Detailed Reports</a>
                    </div>
                    <div class="footer">
                        <p>This is an automated monthly report from %s.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            user.getFullName(),
            reportContent,
            APP_NAME
        );
    }
    
    // Fallback simple text email methods
    
    private void sendSimpleBudgetAlert80(Budget budget) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(budget.getUser().getEmail());
        message.setSubject("Budget Alert: 80% Limit Reached - " + APP_NAME);
        message.setText(String.format(
            "Hello %s,\n\n" +
            "Your spending for %s %d has reached %.1f%% of your budget.\n\n" +
            "Budget: %s\n" +
            "Spent: %s\n" +
            "Remaining: %s\n\n" +
            "Please review your expenses to stay within budget.\n\n" +
            "Best regards,\n%s Team",
            budget.getUser().getFullName(),
            budget.getMonthName(),
            budget.getBudgetYear(),
            budget.getSpentPercentage(),
            budget.getFormattedBudgetAmount(),
            budget.getFormattedSpentAmount(),
            budget.getFormattedRemainingAmount(),
            APP_NAME
        ));
        
        mailSender.send(message);
    }
    
    private void sendSimpleBudgetAlert100(Budget budget) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(budget.getUser().getEmail());
        message.setSubject("Budget Alert: Limit Exceeded - " + APP_NAME);
        message.setText(String.format(
            "Hello %s,\n\n" +
            "Your spending for %s %d has exceeded your budget (%.1f%%).\n\n" +
            "Budget: %s\n" +
            "Spent: %s\n" +
            "Over Budget: %s\n\n" +
            "Please review your expenses immediately.\n\n" +
            "Best regards,\n%s Team",
            budget.getUser().getFullName(),
            budget.getMonthName(),
            budget.getBudgetYear(),
            budget.getSpentPercentage(),
            budget.getFormattedBudgetAmount(),
            budget.getFormattedSpentAmount(),
            budget.getFormattedRemainingAmount().replace("-", ""),
            APP_NAME
        ));
        
        mailSender.send(message);
    }
    
    private void sendSimpleBadgeNotification(User user, Badge badge) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Congratulations! New Badge Earned - " + APP_NAME);
        message.setText(String.format(
            "Hello %s,\n\n" +
            "Congratulations! You've earned a new badge:\n\n" +
            "%s %s\n" +
            "Level: %s\n" +
            "Points: %d\n" +
            "Description: %s\n\n" +
            "Keep up the great work with your financial management!\n\n" +
            "Best regards,\n%s Team",
            user.getFullName(),
            badge.getBadgeIcon(),
            badge.getBadgeName(),
            badge.getBadgeLevel(),
            badge.getBadgePoints(),
            badge.getBadgeDescription(),
            APP_NAME
        ));
        
        mailSender.send(message);
    }
    
    private void sendSimpleWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Welcome to " + APP_NAME + "!");
        message.setText(String.format(
            "Hello %s,\n\n" +
            "Welcome to ExpenseMate! We're excited to help you manage your finances.\n\n" +
            "Get started by:\n" +
            "1. Adding your first expense\n" +
            "2. Setting up a monthly budget\n" +
            "3. Exploring the dashboard\n\n" +
            "Visit: http://localhost:8080/user/dashboard\n\n" +
            "Best regards,\n%s Team",
            user.getFullName(),
            APP_NAME
        ));
        
        mailSender.send(message);
    }
}