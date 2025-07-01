# Implemented Functionalities

This document outlines the implemented functionalities of the Accounting application.

## Authentication

- **Login:** Users can authenticate themselves to obtain a JWT token.
- **Logout:** Users can log out of the system.

## Administration

- **User Creation:** Administrators can create new users.
- **Role Assignment:** Administrators can assign roles to users.
- **View User Roles:** Administrators can view the roles assigned to a specific user.
- **Remove User Roles:** Administrators can remove roles from a user.

## Accounting Period

- **Create Accounting Period:** Create a new accounting period.
- **Lock Accounting Period:** Lock an accounting period to prevent further changes.

## Chart of Accounts

- **Create Account:** Create a new account in the chart of accounts.
- **Update Account:** Update an existing account.
- **Delete Account:** Delete an account.
- **Get All Accounts:** Retrieve a list of all accounts.

## Journal Entry

- **Create Journal Entry:** Create a new journal entry.
- **Post Journal Entry:** Post a journal entry to the general ledger.
- **Validate Journal Entry:** Validate a journal entry.
- **Cancel Journal Entry:** Cancel a journal entry.
- **Reverse Journal Entry:** Reverse a journal entry.
- **Get All Journal Entries:** Retrieve a list of all journal entries, with an option to filter for posted entries only.
- **Get Journal Entry by ID:** Retrieve a specific journal entry by its ID.
- **Get Journal Entry Audit Trail:** Retrieve the audit trail for a specific journal entry.
- **Search Journal Entries:** Search for journal entries based on various criteria such as date range, account type, account name, account number, journal type, and text search (narration, reference numbers).

## General Ledger

- **Get General Ledger:** Retrieve the general ledger with pagination.
- **Search General Ledger:** Search for ledger entries based on date range, account name, account number, journal type, and narration.
