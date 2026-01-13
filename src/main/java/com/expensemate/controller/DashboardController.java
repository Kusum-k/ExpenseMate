package com.expensemate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class DashboardController {

    @GetMapping("/user/dashboard")
    public String dashboard(Model model) {

        // USER NAME
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", "Kusum");
        model.addAttribute("user", user);

        // CURRENT MONTH TOTAL (DUMMY)
        model.addAttribute("currentMonthTotal", 4500);

        // BUDGET DETAILS (DUMMY)
        Map<String, Object> budgetData = new HashMap<>();
        budgetData.put("budgetStatus", "Safe");
        budgetData.put("budgetStatusColor", "#4ecdc4");
        budgetData.put("formattedSpentAmount", "₹4500");
        budgetData.put("formattedBudgetAmount", "₹10000");
        budgetData.put("formattedRemainingAmount", "₹5500");
        budgetData.put("spentPercentage", 45);
        model.addAttribute("currentBudget", budgetData);

        // TOTAL BADGES (DUMMY)
        model.addAttribute("totalBadges", 2);

        // TOTAL POINTS (DUMMY)
        model.addAttribute("totalPoints", 200);

        // CATEGORY SPENDING DATA FOR PIE CHART
        Map<String, Integer> categorySpending = new LinkedHashMap<>();
        categorySpending.put("Food", 1200);
        categorySpending.put("Travel", 800);
        categorySpending.put("Shopping", 2000);
        categorySpending.put("Bills", 500);
        model.addAttribute("categorySpending", categorySpending);

        // MONTHLY TREND GRAPH DATA (LINE CHART)
        Map<String, Integer> monthlyTrend = new LinkedHashMap<>();
        monthlyTrend.put("Jan", 4000);
        monthlyTrend.put("Feb", 4500);
        monthlyTrend.put("Mar", 5000);
        model.addAttribute("monthlyTrend", monthlyTrend);

        // RECENT EXPENSES LIST (DUMMY)
        List<Map<String, Object>> recentExpenses = new ArrayList<>();

        recentExpenses.add(Map.of(
                "expenseDate", new Date(),
                "description", "Groceries",
                "category", Map.of("displayName", "Food"),
                "categoryIcon", "🍔",
                "formattedAmount", "₹1200"
        ));

        recentExpenses.add(Map.of(
                "expenseDate", new Date(),
                "description", "Cab Ride",
                "category", Map.of("displayName", "Travel"),
                "categoryIcon", "🚗",
                "formattedAmount", "₹500"
        ));

        model.addAttribute("recentExpenses", recentExpenses);

        // RECENT BADGES LIST (DUMMY)
        List<Map<String, Object>> recentBadges = new ArrayList<>();
        recentBadges.add(Map.of(
                "badgeIcon", "🏆",
                "badgeName", "Budget Hero",
                "badgeLevel", "Gold",
                "badgePoints", 100
        ));

        model.addAttribute("recentBadges", recentBadges);

        return "dashboard"; // maps to dashboard.html
    }
}
