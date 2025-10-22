package com.expensemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * Budget entity representing monthly budget limits for users
 */
@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "budget_month", "budget_year"})
})
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    @Column(name = "budget_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal budgetAmount;
    
    @NotNull(message = "Budget month is required")
    @Column(name = "budget_month", nullable = false)
    private Integer budgetMonth;
    
    @NotNull(message = "Budget year is required")
    @Column(name = "budget_year", nullable = false)
    private Integer budgetYear;
    
    @Column(name = "spent_amount", precision = 10, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;
    
    @Column(name = "alert_80_sent")
    private boolean alert80Sent = false;
    
    @Column(name = "alert_100_sent")
    private boolean alert100Sent = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors
    public Budget() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Budget(BigDecimal budgetAmount, Integer budgetMonth, Integer budgetYear, User user) {
        this();
        this.budgetAmount = budgetAmount;
        this.budgetMonth = budgetMonth;
        this.budgetYear = budgetYear;
        this.user = user;
    }
    
    public Budget(BigDecimal budgetAmount, YearMonth yearMonth, User user) {
        this(budgetAmount, yearMonth.getMonthValue(), yearMonth.getYear(), user);
    }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; }
    
    public Integer getBudgetMonth() { return budgetMonth; }
    public void setBudgetMonth(Integer budgetMonth) { this.budgetMonth = budgetMonth; }
    
    public Integer getBudgetYear() { return budgetYear; }
    public void setBudgetYear(Integer budgetYear) { this.budgetYear = budgetYear; }
    
    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }
    
    public boolean isAlert80Sent() { return alert80Sent; }
    public void setAlert80Sent(boolean alert80Sent) { this.alert80Sent = alert80Sent; }
    
    public boolean isAlert100Sent() { return alert100Sent; }
    public void setAlert100Sent(boolean alert100Sent) { this.alert100Sent = alert100Sent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    // Utility methods
    public YearMonth getYearMonth() {
        return YearMonth.of(budgetYear, budgetMonth);
    }
    
    public void setYearMonth(YearMonth yearMonth) {
        this.budgetYear = yearMonth.getYear();
        this.budgetMonth = yearMonth.getMonthValue();
    }
    
    public BigDecimal getRemainingAmount() {
        return budgetAmount.subtract(spentAmount);
    }
    
    public double getSpentPercentage() {
        if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return spentAmount.divide(budgetAmount, 4, BigDecimal.ROUND_HALF_UP)
                         .multiply(BigDecimal.valueOf(100))
                         .doubleValue();
    }
    
    public boolean isOverBudget() {
        return spentAmount.compareTo(budgetAmount) > 0;
    }
    
    public boolean isNearBudgetLimit() {
        return getSpentPercentage() >= 80.0;
    }
    
    public boolean shouldSend80Alert() {
        return !alert80Sent && getSpentPercentage() >= 80.0;
    }
    
    public boolean shouldSend100Alert() {
        return !alert100Sent && getSpentPercentage() >= 100.0;
    }
    
    public String getFormattedBudgetAmount() {
        return "₹" + budgetAmount.toString();
    }
    
    public String getFormattedSpentAmount() {
        return "₹" + spentAmount.toString();
    }
    
    public String getFormattedRemainingAmount() {
        return "₹" + getRemainingAmount().toString();
    }
    
    public String getBudgetStatus() {
        double percentage = getSpentPercentage();
        if (percentage >= 100) {
            return "EXCEEDED";
        } else if (percentage >= 80) {
            return "WARNING";
        } else if (percentage >= 50) {
            return "MODERATE";
        } else {
            return "SAFE";
        }
    }
    
    public String getBudgetStatusColor() {
        switch (getBudgetStatus()) {
            case "EXCEEDED": return "#dc3545";
            case "WARNING": return "#fd7e14";
            case "MODERATE": return "#ffc107";
            case "SAFE": return "#28a745";
            default: return "#6c757d";
        }
    }
    
    public String getMonthName() {
        return getYearMonth().getMonth().name();
    }
    
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", budgetAmount=" + budgetAmount +
                ", budgetMonth=" + budgetMonth +
                ", budgetYear=" + budgetYear +
                ", spentAmount=" + spentAmount +
                ", spentPercentage=" + getSpentPercentage() + "%" +
                '}';
    }
}