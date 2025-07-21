# 🏦 Banking Management System

A  backend project simulating a real-world banking system built with Java, Spring Boot, and MySQL. It features user registration and login using JWT authentication, account creation (Savings & Current), secure fund transfer, deposit/withdrawal operations, transaction history, and admin-level functionalities. The project follows best practices with layered architecture, role-based access control, exception handling, and clean RESTful API design.
t 

---

## 🚀 Features

### 🔐 Authentication & Authorization
- ✅ JWT-based authentication system
- ✅ Role-based access: `ADMIN` and `USER`
- ✅ Spring Security integration
- ✅ Secure access to all endpoints

### 👥 User Management
- ✅ Sign up and login with hashed passwords
- ✅ View and update profile
- ✅ User-specific account and transaction data access

### 🧾 Account Management
- ✅ Create new accounts: `SAVINGS` or `CURRENT`
- ✅ Check account balance
- ✅ View account details

### 💸 Transaction Handling
- ✅ Deposit funds into account
- ✅ Withdraw funds with balance validation
- ✅ Transfer money between accounts
- ✅ View transaction history (by account)

### 🛡️ Admin Features
- ✅ View all user accounts
- ✅ Perform deposits/withdrawals on any account
- ✅ View all transactions
- ✅ Transfer money between accounts

---

## 📂 Project Structure

| Module | Description |
|--------|-------------|
| **Auth Module** | Login, signup, JWT generation & validation |
| **User Module** | Profile, account, and transaction access |
| **Account Module** | Create and manage user accounts |
| **Transaction Module** | Deposit, withdraw, transfer, and view transaction logs |
| **Admin Module** | Admin-level access to user and account management |

---

## 🧰 Technology Stack

| Component   | Technology         |
|------------|--------------------|
| Language    | Java               |
| Framework   | Spring Boot        |
| ORM         | JPA (Hibernate)    |
| Database    | MySQL              |
| Security    | Spring Security + JWT |
| Build Tool  | Maven              |

---

## 📝 How It Works

1. **User Signup/Login**
    - JWT token is issued on successful login

2. **Account Creation**
    - User selects account type (Saving or Current)
    - Admin can view all accounts

3. **Transactions**
    - Deposit: Adds money to user’s account
    - Withdraw: Subtracts if sufficient balance
    - Transfer: Moves funds from sender to receiver with transaction record on both sides

4. **Transaction Logs**
    - Every operation is logged in the `Transaction` table
    - Includes type, amount, account numbers, timestamp, and status

---

## 📄 API Overview (Sample)

| Endpoint                     | Method | Access     | Description                    |
|-----------------------------|--------|------------|--------------------------------|
| `/auth/signup`              | POST   | Public     | Register a new user            |
| `/auth/login`               | POST   | Public     | Login and receive JWT token    |
| `/account/create`           | POST   | User       | Create a new account           |
| `/account/balance/{accNo}`  | GET    | User,Admin | View account balance           |
| `/transaction/deposit`      | POST   | Admin      | Deposit funds                  |
| `/transaction/withdraw`     | POST   | Admin      | Withdraw funds                 |
| `/transaction/transfer`     | POST   | Admin      | Transfer money to another acc. |
| `/transaction/history`      | GET    | User,Admin | View account transactions      |
| `/admin/accounts`           | GET    | Admin      | View all accounts              |

---

## 🧑‍💻 Author

**Korat**  
Java Backend Developer | Spring Boot | DSA  
🔗 [GitHub Profile](https://github.com/korat08)


