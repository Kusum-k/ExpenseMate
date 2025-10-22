
INSERT INTO users (username, email, password, full_name, role, created_at, updated_at, is_enabled) VALUES
('admin', 'admin@expensemate.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin User', 'ADMIN', NOW(), NOW(), true),
('john_doe', 'john@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John Doe', 'USER', NOW(), NOW(), true),
('jane_smith', 'jane@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Jane Smith', 'USER', NOW(), NOW(), true),
('demo_user', 'demo@expensemate.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Demo User', 'USER', NOW(), NOW(), true);

-- Insert sample budgets for current month
INSERT INTO budgets (user_id, budget_amount, budget_month, budget_year, spent_amount, alert_80_sent, alert_100_sent, created_at, updated_at) VALUES
(2, 50000.00, MONTH(NOW()), YEAR(NOW()), 35000.00, false, false, NOW(), NOW()),
(3, 40000.00, MONTH(NOW()), YEAR(NOW()), 25000.00, false, false, NOW(), NOW()),
(4, 30000.00, MONTH(NOW()), YEAR(NOW()), 15000.00, false, false, NOW(), NOW());

-- Insert sample budgets for previous months
INSERT INTO budgets (user_id, budget_amount, budget_month, budget_year, spent_amount, alert_80_sent, alert_100_sent, created_at, updated_at) VALUES
(2, 45000.00, MONTH(DATE_SUB(NOW(), INTERVAL 1 MONTH)), YEAR(DATE_SUB(NOW(), INTERVAL 1 MONTH)), 42000.00, true, false, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(3, 38000.00, MONTH(DATE_SUB(NOW(), INTERVAL 1 MONTH)), YEAR(DATE_SUB(NOW(), INTERVAL 1 MONTH)), 30000.00, false, false, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(4, 32000.00, MONTH(DATE_SUB(NOW(), INTERVAL 1 MONTH)), YEAR(DATE_SUB(NOW(), INTERVAL 1 MONTH)), 28000.00, false, false, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

-- Insert sample expenses for current month
INSERT INTO expenses (user_id, description, amount, category, expense_date, notes, created_at, updated_at) VALUES
-- John Doe's expenses
(2, 'Grocery shopping at Walmart', 2500.00, 'GROCERIES', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Weekly groceries', NOW(), NOW()),
(2, 'Lunch at restaurant', 800.00, 'FOOD', DATE_SUB(NOW(), INTERVAL 2 DAYS), 'Business lunch', NOW(), NOW()),
(2, 'Uber ride to office', 300.00, 'TRAVEL', DATE_SUB(NOW(), INTERVAL 3 DAYS), 'Daily commute', NOW(), NOW()),
(2, 'Electricity bill', 3500.00, 'UTILITIES', DATE_SUB(NOW(), INTERVAL 5 DAYS), 'Monthly electricity', NOW(), NOW()),
(2, 'Movie tickets', 1200.00, 'ENTERTAINMENT', DATE_SUB(NOW(), INTERVAL 7 DAYS), 'Weekend movie', NOW(), NOW()),
(2, 'New shirt', 1500.00, 'SHOPPING', DATE_SUB(NOW(), INTERVAL 10 DAYS), 'Office wear', NOW(), NOW()),
(2, 'Doctor consultation', 1000.00, 'HEALTHCARE', DATE_SUB(NOW(), INTERVAL 12 DAYS), 'Regular checkup', NOW(), NOW()),
(2, 'Coffee and snacks', 450.00, 'FOOD', DATE_SUB(NOW(), INTERVAL 15 DAYS), 'Office break', NOW(), NOW()),

-- Jane Smith's expenses
(3, 'Rent payment', 15000.00, 'RENT', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Monthly rent', NOW(), NOW()),
(3, 'Grocery shopping', 3200.00, 'GROCERIES', DATE_SUB(NOW(), INTERVAL 2 DAYS), 'Monthly groceries', NOW(), NOW()),
(3, 'Gas bill', 1800.00, 'UTILITIES', DATE_SUB(NOW(), INTERVAL 4 DAYS), 'Monthly gas', NOW(), NOW()),
(3, 'Dinner with friends', 2500.00, 'FOOD', DATE_SUB(NOW(), INTERVAL 6 DAYS), 'Social dinner', NOW(), NOW()),
(3, 'Online course', 5000.00, 'EDUCATION', DATE_SUB(NOW(), INTERVAL 8 DAYS), 'Skill development', NOW(), NOW()),
(3, 'Gym membership', 2000.00, 'HEALTHCARE', DATE_SUB(NOW(), INTERVAL 11 DAYS), 'Monthly gym fee', NOW(), NOW()),
(3, 'Books purchase', 1200.00, 'EDUCATION', DATE_SUB(NOW(), INTERVAL 14 DAYS), 'Technical books', NOW(), NOW()),

-- Demo User's expenses
(4, 'Coffee shop visit', 350.00, 'FOOD', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Morning coffee', NOW(), NOW()),
(4, 'Bus fare', 50.00, 'TRAVEL', DATE_SUB(NOW(), INTERVAL 2 DAYS), 'Public transport', NOW(), NOW()),
(4, 'Lunch', 400.00, 'FOOD', DATE_SUB(NOW(), INTERVAL 3 DAYS), 'Office lunch', NOW(), NOW()),
(4, 'Mobile recharge', 500.00, 'UTILITIES', DATE_SUB(NOW(), INTERVAL 5 DAYS), 'Monthly mobile plan', NOW(), NOW()),
(4, 'Stationery', 250.00, 'SHOPPING', DATE_SUB(NOW(), INTERVAL 7 DAYS), 'Office supplies', NOW(), NOW()),
(4, 'Internet bill', 1500.00, 'UTILITIES', DATE_SUB(NOW(), INTERVAL 10 DAYS), 'Broadband connection', NOW(), NOW());

-- Insert sample expenses for previous months
INSERT INTO expenses (user_id, description, amount, category, expense_date, notes, created_at, updated_at) VALUES
-- Previous month expenses for John Doe
(2, 'Monthly rent', 20000.00, 'RENT', DATE_SUB(NOW(), INTERVAL 1 MONTH), 'Previous month rent', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(2, 'Grocery shopping', 8000.00, 'GROCERIES', DATE_SUB(NOW(), INTERVAL 1 MONTH), 'Monthly groceries', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(2, 'Fuel expenses', 5000.00, 'TRAVEL', DATE_SUB(NOW(), INTERVAL 1 MONTH), 'Car fuel', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(2, 'Utility bills', 4000.00, 'UTILITIES', DATE_SUB(NOW(), INTERVAL 1 MONTH), 'Electricity + Water', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(2, 'Entertainment', 3000.00, 'ENTERTAINMENT', DATE_SUB(NOW(), INTERVAL 1 MONTH), 'Movies + Games', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(2, 'Shopping', 2000.00, 'SHOPPING', DATE_SUB(NOW(), INTERVAL 1 MONTH), 'Clothes + Accessories', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH));

-- Insert sample badges
INSERT INTO badges (user_id, badge_type, earned_at, is_active, streak_count, achievement_date) VALUES
(2, 'BUDGET_HERO', DATE_SUB(NOW(), INTERVAL 5 DAYS), true, 0, DATE_SUB(NOW(), INTERVAL 5 DAYS)),
(2, 'EXPENSE_TRACKER', DATE_SUB(NOW(), INTERVAL 10 DAYS), true, 0, DATE_SUB(NOW(), INTERVAL 10 DAYS)),
(2, 'SPENDING_STREAK_MAINTAINER', DATE_SUB(NOW(), INTERVAL 15 DAYS), true, 7, DATE_SUB(NOW(), INTERVAL 15 DAYS)),
(3, 'CONSISTENT_SAVER', DATE_SUB(NOW(), INTERVAL 3 DAYS), true, 0, DATE_SUB(NOW(), INTERVAL 3 DAYS)),
(3, 'MONTHLY_PLANNER', DATE_SUB(NOW(), INTERVAL 7 DAYS), true, 0, DATE_SUB(NOW(), INTERVAL 7 DAYS)),
(4, 'EARLY_BIRD', DATE_SUB(NOW(), INTERVAL 2 DAYS), true, 0, DATE_SUB(NOW(), INTERVAL 2 DAYS));

-- Update budget spent amounts based on expenses
UPDATE budgets b SET spent_amount = (
    SELECT COALESCE(SUM(e.amount), 0) 
    FROM expenses e 
    WHERE e.user_id = b.user_id 
    AND MONTH(e.expense_date) = b.budget_month 
    AND YEAR(e.expense_date) = b.budget_year
) WHERE b.budget_month = MONTH(NOW()) AND b.budget_year = YEAR(NOW());

-- Insert additional historical data for better analytics
INSERT INTO expenses (user_id, description, amount, category, expense_date, notes, created_at, updated_at) VALUES
-- 2 months ago data
(2, 'Monthly expenses', 35000.00, 'RENT', DATE_SUB(NOW(), INTERVAL 2 MONTH), 'Historical data', DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(NOW(), INTERVAL 2 MONTH)),
(3, 'Monthly expenses', 28000.00, 'RENT', DATE_SUB(NOW(), INTERVAL 2 MONTH), 'Historical data', DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(NOW(), INTERVAL 2 MONTH)),
(4, 'Monthly expenses', 20000.00, 'FOOD', DATE_SUB(NOW(), INTERVAL 2 MONTH), 'Historical data', DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(NOW(), INTERVAL 2 MONTH)),

-- 3 months ago data
(2, 'Monthly expenses', 40000.00, 'SHOPPING', DATE_SUB(NOW(), INTERVAL 3 MONTH), 'Historical data', DATE_SUB(NOW(), INTERVAL 3 MONTH), DATE_SUB(NOW(), INTERVAL 3 MONTH)),
(3, 'Monthly expenses', 32000.00, 'UTILITIES', DATE_SUB(NOW(), INTERVAL 3 MONTH), 'Historical data', DATE_SUB(NOW(), INTERVAL 3 MONTH), DATE_SUB(NOW(), INTERVAL 3 MONTH)),
(4, 'Monthly expenses', 25000.00, 'TRAVEL', DATE_SUB(NOW(), INTERVAL 3 MONTH), 'Historical data', DATE_SUB(NOW(), INTERVAL 3 MONTH), DATE_SUB(NOW(), INTERVAL 3 MONTH));
