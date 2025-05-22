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
- [ ] Entry status: Draft | Posted | Reversed

---

## 📚 General Ledger

📘 A log of every movement in every account.

- [ ] Display all transactions per account  
- [ ] Filter by date, account type, etc.  
- [ ] Link entries to original journal entries

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