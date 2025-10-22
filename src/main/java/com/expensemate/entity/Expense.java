package com.expensemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Expense entity representing user expenses with categorization
 */
@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    @NotNull(message = "Date is required")
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(length = 500)
    private String notes;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors
    public Expense() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.expenseDate = LocalDate.now();
    }
    
    public Expense(String description, BigDecimal amount, Category category, LocalDate expenseDate, User user) {
        this();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.user = user;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    // Utility methods
    public String getFormattedAmount() {
        return "₹" + amount.toString();
    }
    
    public String getCategoryDisplayName() {
        return category.getDisplayName();
    }
    
    public String getCategoryIcon() {
        return category.getIcon();
    }
    
    public String getCategoryColor() {
        return category.getColor();
    }
    
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category=" + category +
                ", expenseDate=" + expenseDate +
                '}';
    }
    
    // Category enum with display properties
    public enum Category {
        FOOD("Food & Dining", "🍽️", "#FF6B6B"),
        TRAVEL("Travel & Transport", "🚗", "#4ECDC4"),
        RENT("Rent & Housing", "🏠", "#45B7D1"),
        UTILITIES("Utilities", "⚡", "#96CEB4"),
        ENTERTAINMENT("Entertainment", "🎬", "#FFEAA7"),
        HEALTHCARE("Healthcare", "🏥", "#DDA0DD"),
        SHOPPING("Shopping", "🛍️", "#98D8C8"),
        EDUCATION("Education", "📚", "#F7DC6F"),
        GROCERIES("Groceries", "🛒", "#82E0AA"),
        INSURANCE("Insurance", "🛡️", "#AED6F1"),
        INVESTMENT("Investment", "📈", "#F8C471"),
        OTHER("Other", "📝", "#D5DBDB");
        
        private final String displayName;
        private final String icon;
        private final String color;
        
        Category(String displayName, String icon, String color) {
            this.displayName = displayName;
            this.icon = icon;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
        public String getColor() { return color; }
    }
}