package com.expensemate.repository;

import com.expensemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all users by role
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Find all enabled users
     */
    List<User> findByEnabledTrue();
    
    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users with expenses in current month
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.expenses e WHERE MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE)")
    List<User> findUsersWithCurrentMonthExpenses();
    
    /**
     * Find users with budgets
     */
    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.budgets) > 0")
    List<User> findUsersWithBudgets();
    
    /**
     * Find users with badges
     */
    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.badges) > 0")
    List<User> findUsersWithBadges();
    
    /**
     * Count total users
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();
    
    /**
     * Count users by role
     */
    long countByRole(User.Role role);
    
    /**
     * Count active users
     */
    long countByEnabledTrue();
    
    /**
     * Find top users by expense count
     */
    @Query("SELECT u FROM User u ORDER BY SIZE(u.expenses) DESC")
    List<User> findTopUsersByExpenseCount();
    
    /**
     * Find users registered in last N days
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
}