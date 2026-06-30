# Bank App Feature Specification

> Target stack: **Java, JavaFX (GUI), Spring Boot (database)**

---

## 1. Overview

The application has a single **Login Screen** that authenticates a user and routes them to one of two experiences based on account type:

- **Admin** — staff-facing tools for managing customer accounts and loan plans.
- **Customer** — self-service banking features (deposit, withdraw, transfer, loans, history, settings).

---

## 2. Login Screen

- Single entry point for all users.
- On successful authentication, route based on `Account Type`:
  - `ADMIN` → Admin Menu
  - `CUSTOMER` → Customer Menu (displays "Welcome, {User}")

---

## 3. Admin Flow

### 3.1 Admin Menu
Top-level navigation containing:
- Account Manager
- Loan Manager
- Logout

### 3.2 Account Manager

#### Create Account
- **Auto-generated ID** (system-assigned, not user-entered)
- Name of user
- Default PIN: `1234` (assigned automatically on creation; customer should be prompted to change it)
- Password of user
- Role (e.g., Customer/Admin)

#### Modify Account
- **Freeze / Unfreeze Account**
  - A frozen account cannot be accessed by the customer until unfrozen by an admin.
- **Edit details**: name, PIN, password
- **Balance override**: directly edit account balance (for testing/demo simplicity — not a real-world banking practice, but included for this simulation)

#### Delete Account
- Permanently removes the account and its data from the system.

#### View Accounts
- Display:
  - Account status (Active / Frozen)
  - Account ID
  - Name of user
- **Search** accounts by Account ID or Name.

### 3.3 Loan Manager
- **Set loan plans** — define configurable loan products, e.g.:
  - Duration: 6 months
  - Max amount: $1,200
  - Interest rate: 4.5%
- **View accounts with active loans**
- **Search accounts**

### 3.4 Logout
- Returns to Login Screen.

---

## 4. Customer Flow

### 4.1 Customer Menu
- Header: "Welcome, {User}"
- Persistent elements on this screen:
  - **Notification button** (see §4.7)
- Menu items:
  - Deposit
  - Withdraw
  - Balance Inquiry
  - Send Money
  - Loan Money
  - Transaction History
  - Settings
  - Logout

> **Frozen account behavior:** If the account is frozen, the customer must be informed and blocked from Deposit, Withdraw, and Send Money. Balance Inquiry remains available even while frozen.

### 4.2 Deposit
1. Enter PIN
2. Go Back (cancel option)
3. On success → Success Message

### 4.3 Withdraw
1. Enter PIN
2. Go Back (cancel option)
3. On success → Success Message

### 4.4 Balance Inquiry
1. Enter PIN
2. Display current balance
- Available even when account is frozen.

### 4.5 Send Money
1. Enter PIN
2. Enter destination Account Number
3. Exit (cancel option)
4. On success → Success Message (implied, consistent with Deposit/Withdraw pattern)

### 4.6 Loan Money

#### View Loans (Dashboard)
Primary, full-space view. Table/list columns:
| Field | Description |
|---|---|
| No. | Row/index number |
| Loan Money | Original loaned amount |
| Left to Repay | Remaining balance on the loan |
| Duration | Loan term |
| Interest Rate | Applicable interest rate |
| Installment per month | Monthly payment amount |

Additional dashboard elements:
- **Pay Existing Loan** (button/action)
- **Totals row/summary**:
  - Total of loaned money
  - Total amount to be repaid
  - Total of installments per month

#### Create New Loan
- Select a loan plan (from admin-defined plans) and a loan amount.

#### View Credit Score
- Display the customer's current credit score.
- If credit score is low, alert the user (exact threshold/alert behavior TBD — flagged for design clarification).

#### Manage Credit Score
- Section/screen for credit score-related actions (details TBD — flagged for design clarification).

### 4.7 Transaction History
1. Enter PIN
2. **Balance over time** — Area Graph, filterable by:
   - Daily
   - Weekly
   - Monthly
   - Yearly
   - Sample reference graph: "Account Balance" with currency in PHP (₱), y-axis as amount, x-axis as time.
3. **Transactions table** — chronological list, e.g.:
   - `9:32 AM — Sent PHP 6,000 to Account Number 328*******`
   - Recipient/sender account numbers should be partially masked for privacy (e.g., last digits hidden).
4. Exit (cancel option)

### 4.8 Notification Button
- Visible from the Customer Menu.
- Shows notifications for:
  - Received balance/transfers from other accounts
  - Account frozen status (alerts the customer that their account has been frozen)
- Visual indicator: bell icon with a numbered badge (red circle with unread count), e.g. 🔔¹

### 4.9 Settings

#### View Details
- Account Number
- Name of the Holder
- Validity of the Card (expiration)
- Card Type

#### Edit PIN Code
1. Enter old PIN code
2. Enter new PIN code (entered twice for confirmation)

#### Edit Password
1. Enter old password
2. Enter new password (entered twice for confirmation)

#### Delete Account
- Customer-initiated account deletion.

### 4.10 Logout
- Returns to Login Screen.

---

## 5. Cross-Cutting Concerns

- **PIN verification** gates sensitive actions: Deposit, Withdraw, Balance Inquiry, Send Money, Transaction History.
- **Frozen accounts**: enforce restriction at the service/business-logic layer, not just the UI, so frozen accounts cannot transact via any code path.
- **Password vs. PIN**: Password is used for login/account-level changes; PIN is used for per-transaction confirmation.
- **Confirmation patterns**: Destructive or sensitive changes (new PIN, new password) require double entry to confirm.
- **Masking**: Sensitive identifiers (e.g., account numbers in transaction history/notifications) should be partially masked for display.

---

## 6. Open Questions / Items Needing Clarification

- [ ] Exact credit score thresholds and alert copy for "low credit score" warning.
- [ ] Scope and behavior of "Manage Credit Score" — is this customer-editable or just informational/auto-calculated?
- [ ] Whether "Send Money" shows a success message/confirmation screen (pattern exists elsewhere, not explicitly stated for this flow).
- [ ] Loan repayment flow details when "Pay Existing Loan" is triggered (source of funds, partial vs. full payment).
- [ ] Currency: examples use PHP (₱) — confirm if this is the fixed currency for the app or configurable.

---

## 7. Suggested JavaFX Screen/Controller Mapping

| Screen (FXML/Controller) | Maps to Section |
|---|---|
| `LoginView` | §2 |
| `AdminMenuView` | §3.1 |
| `AccountManagerView` (Create/Modify/Delete/View tabs) | §3.2 |
| `LoanManagerView` | §3.3 |
| `CustomerMenuView` | §4.1 |
| `DepositView` | §4.2 |
| `WithdrawView` | §4.3 |
| `BalanceInquiryView` | §4.4 |
| `SendMoneyView` | §4.5 |
| `LoanDashboardView` / `CreateLoanView` / `CreditScoreView` | §4.6 |
| `TransactionHistoryView` | §4.7 |
| `NotificationPanel` (shared component) | §4.8 |
| `SettingsView` | §4.9 |

This mapping is a suggestion for organizing FXML files and controllers; implementers may adjust as needed to fit MVC structure.
