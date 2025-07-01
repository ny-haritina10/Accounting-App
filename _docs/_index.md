# 📘 Accounting Application - TODO List

This document outlines the core features of an accounting system and the business logic behind each. The goal is to help understand how accounting works before diving into code.

---

## 🧠 Basic Accounting Concepts (Understand First)

- [ ] Learn the **Double-Entry Bookkeeping** system  
  📘 Every transaction affects **two accounts**: one is **debited**, one is **credited**  
  🔁 Total **debits = credits**

- [ ] Understand **Account Types**  
  - 📦 Assets (e.g. Cash, Inventory, Equipment)  
  - 💳 Liabilities (e.g. Loans, Accounts Payable)  
  - 👤 Equity (Owner's capital or retained earnings)  
  - 💰 Revenue (Sales income, service fees)  
  - 📉 Expenses (Rent, utilities, salaries)

- [ ] Learn how **Journal Entries** record financial activity  
  - One entry per transaction  
  - Each entry has **at least one debit and one credit**

- [ ] Understand **Ledgers and Financial Statements**  
  - The **General Ledger** is a book of all account activities  
  - Financial Reports (Balance Sheet, Income Statement) are derived from the ledger

---

## 📋 User Authentication & Roles

- [ ] User Registration / Login  
- [ ] Roles: Admin, Accountant, Viewer  
- [ ] Permissions to restrict actions (e.g., only admin can delete)

---

## 🧾 Chart of Accounts (COA)

📘 A structured list of all accounts used in the system.

- [ ] Create default Chart of Accounts at setup  
- [ ] Allow users to add/edit/delete custom accounts  
- [ ] Classify accounts by type: Asset, Liability, Equity, Revenue, Expense  
- [ ] Assign account codes for sorting (e.g., 1000 for Assets, 2000 for Liabilities)

---

## 🧮 Journal Entries

📘 Manual input of transactions based on real-world events.

- [ ] Form to create new journal entries  
- [ ] Require at least one debit and one credit  
- [ ] Prevent submission if debit ≠ credit  
- [ ] Track who posted the entry and when  
- [ ] Entry status: Created | Validated | Canceled | Reversed

1. Retrieve & Search Entries

Implement GET endpoints to:
    Retrieve a list of all journal entries
    Retrieve a journal entry by ID
    Search/filter journal entries by:
        Date range
        Status
        User
        Account involved

2. Posting & Period Locking
    Implement "posting" of journal entries — make them immutable after validation.
    Add logic to lock accounting periods, disallowing new entries or edits to existing ones in closed periods.
    Scenario: End of Month Accounting
      Let's say your company has the following process:
          Accountants create and edit journal entries during the month of April 2025.
          On May 5, 2025, the financial controller reviews and closes April to finalize books.
          After April is closed:
              No new entries can be created for April.
              Existing entries for April cannot be modified or deleted.
              Only posted entries are used in reports (like balance sheet or income statement).

3. Reversing Entries
    Provide an endpoint to auto-generate reversing journal entries, useful for accruals and temporary adjustments.

4. Journal Entry Numbering (Sequence)
    Assign unique sequential journal entry numbers, optionally per period (e.g., 2025-0001).
    Suggest a well formatted and coherent unique sequential entry numbers 

5. Audit Trail [[NEXT]]
    Record:
      Who created/updated/reversed a Journal Entry
      Timestamps for each action
  
---

## 📚 General Ledger

📘 A log of every movement in every account.

- [ ] Display all transactions per account  
- [ ] Filter by date, account type, etc.  
- [ ] Link entries to original journal entries

🔧 Core Functionalities (General Ledger Module)

1. View General Ledger
    ✅ Display all ledger entries per account
    ✅ Show running balances (debit/credit, net)
    ✅ Group by account (e.g., cash, receivables, etc.)
    ✅ Paginated 

**CURRENT**
2. Filter & Search 
    ✅ Filter by:
        Date range
        Account type (Asset, Liability, Equity, Income, Expense)
        Account name or number
        Journal type (Sales, Purchase, Cash, etc.)
    ✅ Text search (narration, reference numbers)

3. Ledger Entry Details
    ✅ Clickable row to view:
        Original journal entry
        Linked documents (invoice, payment, etc.)
        Narration / Description
        Debit / Credit amounts
        Reference ID / Transaction ID

4. Link to Journal Entries
    ✅ Navigate to full journal entry details
    ✅ Show source transaction (invoice, bill, etc.)
    ✅ Display related accounts involved in the journal

5. Account Balances Summary
    ✅ Opening balance
    ✅ Period movements (debit and credit totals)
    ✅ Closing balance
    ✅ Trial balance-compatible view

6. Export & Reports
    ✅ Export ledger to:
        PDF
        Excel
        CSV
    ✅ Generate printable reports for:
        Specific accounts
        Specific date ranges

7. Audit & Security
    ✅ View who added/edited an entry
    ✅ Role-based access (e.g., read-only for auditors)
    ✅ Entry locking after period closure

8. Period Closing and Archiving
    ✅ Lock periods to prevent post-close modifications
    ✅ Archiving old data
    ✅ Reconcile balances with trial balance / balance sheet

9. Multi-Currency Support (Optional)
    ✅ Record entries in base + foreign currency
    ✅ Auto-calculate exchange differences
    ✅ Show gains/losses in ledgers

10. Reconciliation Support
    ✅ Mark entries as reconciled/unreconciled
    ✅ Bank and account reconciliation tools
    ✅ Generate discrepancy reports

---

## 📊 Financial Reports

📘 Automatically generated based on data in the ledger.

- [ ] **Trial Balance**  
  - Lists all accounts with debit/credit totals  
  - Used to confirm accounts are balanced

- [ ] **Income Statement (Profit & Loss)**  
  - Revenue - Expenses = Net Income

- [ ] **Balance Sheet**  
  - Assets = Liabilities + Equity  
  - Snapshot of financial position at a given date

- [ ] **Cash Flow Statement** (optional)  
  - Tracks inflow/outflow of cash (operating, investing, financing)

---

## 💸 Invoicing and Payments (Optional for MVP)

📘 Allows billing clients and recording payments.

- [ ] Create/send invoices to customers  
- [ ] Record partial or full payments  
- [ ] Automatically update journal entries

---

## 🏦 Bank Reconciliation (Optional for MVP)

📘 Match bank statement records with system transactions.

- [ ] Import bank statements  
- [ ] Match them with journal entries  
- [ ] Flag unmatched items for review

---

## 🕵️‍♀️ Audit Logs

📘 Keep a record of who did what and when.

- [ ] Log all changes to journal entries and accounts  
- [ ] Include timestamp and user info  
- [ ] Make logs read-only

---

## 🔐 Security & Backup

- [ ] Secure user data (hash passwords, SSL)  
- [ ] Regular database backups  
- [ ] Access control by user role

---

## ✅ MVP Checklist Summary

- [ ] Auth system with user roles  
- [ ] Chart of Accounts (with account types)  
- [ ] Journal Entry creation + validation  
- [ ] General Ledger display  
- [ ] Trial Balance, Income Statement, Balance Sheet