#
SET FOREIGN_KEY_CHECKS = 0;
#
#
DROP TABLE household_invite_member;
#
DROP TABLE household_member;
#
DROP TABLE role_permission;
#
DROP TABLE role;
#
DROP TABLE household;
#
DROP TABLE avatar;
#
DROP TABLE users;
#
#
SET FOREIGN_KEY_CHECKS = 1;
#

INSERT
IGNORE users(id, email, password, receive_updates)
VALUES (1, 'crystal@crystalek.pl', '$2a$10$vjrk34Tmu7PS98XuZm.VDucRT1s73NeLGI.uYHq.Le9lDbsghV6ne', 1),
       (2, 'crystalek.pl@gmail.com', '$2a$10$vjrk34Tmu7PS98XuZm.VDucRT1s73NeLGI.uYHq.Le9lDbsghV6ne', 1),
       (3, 'alice@example.com', 'password123', 1),
       (4, 'bob@example.com', 'password456', 0),
       (5, 'carol@example.com', 'password789', 1);

INSERT INTO user_data (user_id, nickname)
VALUES (3, 'Alice'),
       (4, 'Bob'),
       (5, 'Carol'),
       (1, 'Crystal'),
       (2, 'Crystal-Gmail');

INSERT INTO household (id, creation_time, name, owner_id)
VALUES (1, NOW(), 'Household', 1);

INSERT INTO role (id, name, color, creation_time, household_id)
VALUES (1, 'Admin', '#FF0000', NOW(), 1),
       (2, 'Member', '#00FF00', NOW(), 1),
       (3, 'Owner', '#0000FF', NOW(), 1);

UPDATE household
SET default_role_id = 2,
    owner_role_id   = 1
WHERE id = 1;
UPDATE household
SET default_role_id = 3,
    owner_role_id   = 3
WHERE id = 2;

INSERT INTO household_member (id, join_date, household_id, role_id, user_id)
VALUES (1, NOW(), 1, 3, 1),
       (2, NOW(), 1, 1, 2),
       (3, NOW(), 1, 2, 3),
       (4, NOW(), 1, 2, 4),
       (5, NOW(), 1, 2, 5);

-- Dodaj kategorie
INSERT INTO categories (id, color, creation_time, name, household_id)
VALUES (1, '#FFA500', NOW(), 'Groceries', 1),
       (2, '#0000FF', NOW(), 'Utilities', 1),
       (3, '#008000', NOW(), 'Entertainment', 1),
       (4, '#FF0000', NOW(), 'Transport', 1),
       (5, '#800080', NOW(), 'Health', 1),
       (6, '#FFFF00', NOW(), 'Education', 1),
       (7, '#00FFFF', NOW(), 'Dining Out', 1),
       (8, '#A52A2A', NOW(), 'Clothing', 1),
       (9, '#808080', NOW(), 'Gifts', 1),
       (10, '#FFC0CB', NOW(), 'Pets', 1);


-- Dodaj paragony
INSERT INTO receipts (id, creation_time, settled, shop, shopping_time, household_id, who_paid_id)
VALUES (1, NOW(), 0, 'SuperMart', NOW(), 1, 1),
       (2, NOW(), 1, 'Grocery Store', NOW(), 1, 2),
       (3, NOW(), 0, 'ElectroWorld', NOW(), 1, 2),
       (4, NOW(), 1, 'Pharmacy', NOW(), 1, 2),
       (5, NOW(), 0, 'BookStore', NOW(), 1, 1),
       (6, NOW(), 1, 'PetShop', NOW(), 1, 1),
       (7, NOW(), 0, 'Cinema', NOW(), 1, 1),
       (8, NOW(), 1, 'Restaurant', NOW(), 1, 2),
       (9, NOW(), 0, 'Clothing Store', NOW(), 1, 1),
       (10, NOW(), 1, 'Gift Shop', NOW(), 1, 2);


-- Dodaj elementy paragonów
INSERT INTO receipt_items (id, creation_time, dividing, price, product_name, quantity, category_id, receipt_id,
                           return_user_id)
VALUES (1, NOW(), 1.0, 10.99, 'Milk', 2, 1, 1, 3),
       (2, NOW(), 2.0, 5.49, 'Bread', 1, 1, 1, 3),
       (3, NOW(), 1.0, 20.00, 'Electricity Bill', 1, 2, 2, 1),
       (4, NOW(), 1.5, 299.99, 'Headphones', 1, 3, 3, 2),
       (5, NOW(), 1.0, 12.50, 'Aspirin', 1, 4, 4, 1),
       (6, NOW(), 2.0, 8.90, 'Notebook', 2, 5, 5, 2),
       (7, NOW(), 1.0, 15.00, 'Dog Food', 1, 6, 6, 1),
       (8, NOW(), 1.0, 24.00, 'Movie Ticket', 2, 7, 7, 3),
       (9, NOW(), 2.0, 35.00, 'Dinner', 1, 8, 8, 2),
       (10, NOW(), 1.0, 59.99, 'Jeans', 1, 9, 9, 3),
       (11, NOW(), 1.5, 19.99, 'Gift Box', 1, 10, 10, 1),
       (12, NOW(), 1.0, 2.50, 'Yogurt', 3, 1, 1, 2),
       (13, NOW(), 1.0, 1.20, 'Banana', 6, 1, 1, 3),
       (14, NOW(), 1.0, 0.99, 'Apple', 4, 1, 1, 3),
       (15, NOW(), 1.0, 60.00, 'Water Bill', 1, 2, 2, 2),
       (16, NOW(), 1.0, 45.00, 'Internet', 1, 2, 2, 1),
       (17, NOW(), 1.0, 149.00, 'Monitor', 1, 3, 3, 3),
       (18, NOW(), 1.0, 10.00, 'Painkillers', 1, 4, 4, 2),
       (19, NOW(), 1.0, 18.75, 'Textbook', 1, 5, 5, 3),
       (20, NOW(), 1.0, 22.00, 'Cat Litter', 1, 6, 6, 2),
       (21, NOW(), 1.0, 12.00, 'Popcorn', 1, 7, 7, 3),
       (22, NOW(), 1.0, 50.00, 'Steak Dinner', 1, 8, 8, 1),
       (23, NOW(), 1.0, 79.99, 'Jacket', 1, 9, 9, 2),
       (24, NOW(), 1.0, 34.99, 'Scarf', 1, 9, 9, 3),
       (25, NOW(), 1.0, 14.99, 'Teddy Bear', 1, 10, 10, 1),
       (26, NOW(), 1.0, 1.50, 'Breadsticks', 2, 8, 8, 3),
       (27, NOW(), 1.0, 2.99, 'Juice', 1, 1, 1, 1),
       (28, NOW(), 1.0, 3.49, 'Chocolate', 1, 10, 10, 2),
       (29, NOW(), 1.0, 7.99, 'Shampoo', 1, 4, 4, 3),
       (30, NOW(), 1.0, 6.49, 'Notebook Pen', 1, 5, 5, 1);
