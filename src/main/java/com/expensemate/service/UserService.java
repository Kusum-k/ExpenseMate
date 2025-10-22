package com.expensemate.service;

import com.expensemate.entity.User;
import com.expensemate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for User entity operations and authentication
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Load user by username for Spring Security authentication
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username or email: " + username);
        }
        return user.get();
    }
    
    /**
     * Register a new user
     */
    public User registerUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        
        // Set timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by username or email
     */
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier);
    }
    
    /**
     * Update user profile
     */
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Update user password
     */
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    /**
     * Enable or disable user account
     */
    public void setUserEnabled(Long userId, boolean enabled) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(enabled);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    /**
     * Change user role
     */
    public void changeUserRole(Long userId, User.Role newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Get enabled users only
     */
    public List<User> getEnabledUsers() {
        return userRepository.findByEnabledTrue();
    }
    
    /**
     * Get users created after specific date
     */
    public List<User> getUsersCreatedAfter(LocalDateTime date) {
        return userRepository.findByCreatedAtAfter(date);
    }
    
    /**
     * Get recent users (last 30 days)
     */
    public List<User> getRecentUsers() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.findRecentUsers(thirtyDaysAgo);
    }
    
    /**
     * Get users with current month expenses
     */
    public List<User> getUsersWithCurrentMonthExpenses() {
        return userRepository.findUsersWithCurrentMonthExpenses();
    }
    
    /**
     * Get users with budgets
     */
    public List<User> getUsersWithBudgets() {
        return userRepository.findUsersWithBudgets();
    }
    
    /**
     * Get users with badges
     */
    public List<User> getUsersWithBadges() {
        return userRepository.findUsersWithBadges();
    }
    
    /**
     * Delete user account
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    /**
     * Check if username is available
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    /**
     * Validate password strength
     */
    public boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Get user statistics for admin dashboard
     */
    public UserStats getUserStats() {
        long totalUsers = userRepository.countTotalUsers();
        long adminUsers = userRepository.countByRole(User.Role.ADMIN);
        long regularUsers = userRepository.countByRole(User.Role.USER);
        long activeUsers = userRepository.countByEnabledTrue();
        
        return new UserStats(totalUsers, adminUsers, regularUsers, activeUsers);
    }
    
    /**
     * Get top users by expense count
     */
    public List<User> getTopUsersByExpenseCount() {
        return userRepository.findTopUsersByExpenseCount();
    }
    
    /**
     * Inner class for user statistics
     */
    public static class UserStats {
        private final long totalUsers;
        private final long adminUsers;
        private final long regularUsers;
        private final long activeUsers;
        
        public UserStats(long totalUsers, long adminUsers, long regularUsers, long activeUsers) {
            this.totalUsers = totalUsers;
            this.adminUsers = adminUsers;
            this.regularUsers = regularUsers;
            this.activeUsers = activeUsers;
        }
        
        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getAdminUsers() { return adminUsers; }
        public long getRegularUsers() { return regularUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getInactiveUsers() { return totalUsers - activeUsers; }
        public double getActiveUserPercentage() { 
            return totalUsers > 0 ? (double) activeUsers / totalUsers * 100 : 0; 
        }
    }
}