# Software Requirements Specification (SRS) & Implementation Blueprint
**Target System:** Java-Based Banking Application (Desktop/CLI or Monolithic Backend)
**Document Purpose:** Functional specification and structured feature breakdown for autonomous LLM coding agents.

---

## 1. System Architecture & Role-Based Access Control (RBAC)

The implementation uses a small inheritance hierarchy for authentication and role checks. A base `User` class is extended by two concrete types: `Administrator` and `Customer`. Role-based access control is implemented via the `isAdmin` field on `User`: when `isAdmin` is true the account is treated as an administrator, otherwise it is a customer account.

Authentication still routes to the same high-level control flow (login → role check → admin or customer menu). The logical diagram remains the same:

```
                  +--------------------------------+
                  |          Login Screen          |
                  +--------------------------------+
                                  |
                   Validate Account ID & Password
                                  |
                  +---------------+---------------+
                  |                               |
       [isAdmin == true]                 [isAdmin == false]
                  |                               |
                  v                               v
       +--------------------+          +--------------------+
       |     Admin Menu     |          |   Customer Menu    |
       +--------------------+          +--------------------+
       | - Account Manager  |          | - Deposit/Withdraw |
       | - Loan Manager     |          | - Balance Inquiry  |
       | - System Overrides |          | - Fund Transfers   |
       | - Audit Controls   |          | - Loan Management  |
       +--------------------+          | - Analytics Engine |
                                       +--------------------+
```

---

## 2. Global Authentication Module

### 2.1 Login Engine
* **Source Integration:** Credentials (`Account ID` and `Password`) are derived from the legacy data structure ("old banking app"). (Refer to the old ```bankingapp``` project and its classes ```com.gabriel.bankingapp.models.Account``` and ```com.gabriel.bankingapp.models.Bank```, and even older implementations ```com.gabriel.twoforms.Account``` and ```com.gabriel.twoforms.Bank```)
* **Behavior:** Processes entry strings, verifies existence, checks account status (active vs. frozen), and routes to the appropriate subsystem block.

---

## 3. Admin Subsystem ("Admin Menu")

### 3.1 Account Manager Module
* **Create Account Sequence:**
  * Auto-generates a unique, non-colliding `Account ID`.
  * Captures the structural `Name of user`.
  * Assigns a global `Default PIN` initialized strictly to `1234` (implemented as the `pin` field with default value 1234 on the `Customer` class).
  * Captures a temporary or permanent user-defined `Password`.
  * Assigns system permissions via the `Role` attribute.
* **Modify Account State:**
  * **Freeze/Unfreeze Flag:** Toggles the `isAccountFrozen` boolean on the `Customer` class. If `isAccountFrozen` is true, customer-facing operational logic must throw an access-denied exception and block transactional routing until the flag is reverted.
  * **Detail Mutations:** Explicit workflows to rewrite existing core variables: `Name`, `PIN`, and `Password`.
  * **Balance Overrides:** System-level entry bypass allowing arbitrary data correction by manually overriding the balance values for debugging or ledger balancing.
* **Delete Account Sequence:**
  * Purges the target `Account ID` and all nested dependencies completely from active operational states.
* **View & Search Engine:**
  * Exposes individual status records containing `Account ID`, `Name of User`, and operational status (e.g., `Active` or `Frozen`).
  * Implements multi-parameter searching optimized for lookup via `Account ID` strings or `Name` fragments.

### 3.2 Loan Manager Module
* **Loan data model (existing class):** There is a `Loan` class in the codebase (work in progress). The `Loan` class currently exposes the following fields which should be used as the canonical loan schema while the module is completed:
  * `id` (int)
  * `userID` (int) — the account owner
  * `loanMoney` (double) — principal amount
  * `moneyLeftToRepay` (double) — outstanding liability
  * `duration` (int) — term (e.g., months)
  * `interestRate` (double)
  * `installmentRate` (double)

* **Loan Parameter Configuration:**
  * Controls system-wide tier definitions (e.g., Cap matrix: 6-month repayment ceiling capped at an upper bound of `$1,200` carrying a `4.5%` nominal interest rate). Use the `Loan` fields above when mapping GUI fields to model properties.
* **Portfolio Supervision:**
  * Aggregates a dynamic system view displaying all accounts currently maintaining active debt lines using `Loan.moneyLeftToRepay` and `Loan.userID` to join with customer records.
  * Provides a precise search interface to crawl accounts with active loans.

---

## 4. Customer Subsystem ("Customer Menu")

Upon authentication, the interface renders a personalized greeting header (`Welcome User`) and exposes the financial processing pipeline.

### 4.1 Security Guardrail (PIN Verification Pattern)
> **Agent Implementation Note:** Every transactional routine within this block (*Deposit, Withdraw, Balance Inquiry, Send Money, Transaction History*) must explicitly trigger a secondary gate requiring the user to re-verify their `PIN code` prior to updating the data layers.

### 4.2 Core Transaction Engine
* **Deposit Routine:**
  * Prompts for security `PIN`.
  * Accepts credit payload, mutates core balance tracking variables, and logs ledger updates.
  * Returns a structural transaction receipt frame (`Success Message`) or enables a `Go Back` exit trap.
* **Withdraw Routine:**
  * Prompts for security `PIN`.
  * Checks available liquidity limits; rejects if overdrawn. Mutates balance down.
  * Emits structural success indicators or routes to standard `Go Back` steps.
* **Balance Inquiry:**
  * Gatekeeps through a `PIN` prompt.
  * Queries and prints the exact current ledger balance. Exits smoothly back to menu boundaries.
* **Send Money (Peer-to-Peer Transfer):**
  * Gatekeeps through a `PIN` prompt.
  * Requires valid destination token (`Account Number to Send To`).
  * Evaluates sender balance bounds, executes atomic double-entry bookkeeping transactions (debit sender, credit receiver), and routes exceptions on routing failures.

### 4.3 Advanced Loan Processing (“Loan Money”)
* **Loan Dashboard Interface:**
  * This UI viewport is structured to dominate the container layout space, presenting a tabular data matrix tracking:
    1. Index sequence tracking ID (`No.`)
    2. Principal balance loaned (`Loan Money`)
    3. Residual liability outstanding (`Left to Repay`)
    4. Duration lifecycle metrics (`Duration`)
    5. Active yield percentage (`Interest Rate`)
    6. Amortized liabilities calculated linearly (`Installment per month`)
  * **Aggregations Line:** Computes running totals across three explicit pillars: *Total Loaned Money*, *Total Amount to be Repaid*, and *Total Installments per month*.
* **Payment Module:**
  * Triggers a dedicated billing script (`Pay Existing loan`) to amortize active debts against the liquid balance.
* **Origination Module:**
  * Exposes a creation interface (`Create New Loan`) enabling the consumer to select an admin-defined loan plan and declare an entry amount within limits.

### 4.4 Analytics & Transaction History
* **Time-Series Analytics Engine:**
  * Gatekeeps through a `PIN` prompt.
  * Aggregates historical ledger values into distinct chronological buckets: `Daily`, `Weekly`, `Monthly`, or `Yearly`.
  * Maps data objects to an Area Graph interface layout to trace data flow shifts (visual target benchmark matches the `PHP`-denominated standard currency `Account Balance` visualization model).
* **Tabular Audit Trail:**
  * Renders historical logs as sequentially dense text lines mapping operational actions (e.g., Formatted template: `[Timestamp] Sent PHP [Amount] to Account Number [Masked/Full Account ID]`).

---

## 5. Security & Account Maintenance Configuration (“Settings”)

Provides granular client-side modifications and profile visualization.

### 5.1 Profile Inspection (`View Details`)
* Renders metadata bindings: `Account Number`, `Name of the Holder`, `Validity of the Card`, and `Card Type`.

### 5.2 Credential Rotation Blocks
* **PIN Modification Flow:**
  * Captures `Old PIN code`.
  * Requests `New PIN code` duplicated exactly twice (`2x`) to evaluate input matches prior to running DB mutations.
* **Password Modification Flow:**
  * Captures `Old password`.
  * Requests `New Password` duplicated exactly twice (`2x`) to ensure semantic equality before saving changes.

### 5.3 System Lifecycle Controls
* **Account Deletion Request:** Trigger path allowing the customer to register a destruction vector for their system profile.
* **Session Termination (`Logout`):** Invalidates access tokens, breaks the system runtime loop, and gracefully shifts context execution frames back to the primary `Login Screen`.

---

## 6. Real-Time Telemetry & Async Messaging

### 6.1 Inbound Transfer Notifications
* **Notification Indicator Widget:** An interactive UI anchor displaying pending state updates.
* **Event Loop Handler:** Monitors asynchronous events; pushes UI alerts when balance increments are received from external peers via the `Send Money` pipeline.

---

## 7. Implementation Blueprint Checklist for LLM Agents

- [ ] **Data Model Layer:** Formulate `Account`, `Transaction`, `Loan`, and `Notification` schemas with strong validation rule sets.
- [ ] **State Machine Engine:** Code validation boundaries preventing frozen accounts from running consumer processes.
- [ ] **Transactional Security Aspect:** Implement programmatic wrappers or interceptors enforcing `PIN` input confirmation ahead of execution targets across protected financial tasks.
- [ ] **Math Layer Calibration:** Implement precision data tracking (e.g., `BigDecimal` in Java) to manage compound calculations across multi-tier interest plans and debt totals without rounding loss.
- [ ] **UI Rendering Blueprint:** Coordinate visual layout properties for the Loan Dashboard and Area Graph components using structural console buffers or target Java UI framework nodes (Swing/JavaFX).
