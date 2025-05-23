# API Response Format Documentation

## Overview

The `ApiResponse` class provides a standardized JSON response format for all API endpoints in the accounting module. It ensures consistent response structure across the application.

---

## Response Structure

Every API response follows this JSON format:

```json
{
  "success": boolean,
  "data": object | null,
  "message": string,
  "errorCode": string | null
}
````

### Fields

* **`success`**: Indicates if the request was successful (`true`) or failed (`false`)
* **`data`**: Contains the response data (can be any type or `null` for errors)
* **`message`**: Human-readable message describing the result
* **`errorCode`**: Unique code for error identification (`null` for successful responses)

---

## Usage

### Success Response

```java
ApiResponse<User> response = ApiResponse.success(user, "User retrieved successfully");
```

**Example JSON output:**

```json
{
  "success": true,
  "data": { /* user object */ },
  "message": "User retrieved successfully",
  "errorCode": null
}
```

### Error Response

```java
ApiResponse<User> response = ApiResponse.error("User not found", "USER_NOT_FOUND");
```

**Example JSON output:**

```json
{
  "success": false,
  "data": null,
  "message": "User not found",
  "errorCode": "USER_NOT_FOUND"
}
```

---

## Implementation Notes

* The `ApiResponse` class is **generic** to support different data types.
* Use the **static factory methods** `success()` and `error()` for creating responses.
* Place in the `mg.module.accounting.common` package for shared use.
* Ensure all controllers return `ApiResponse` for consistency.
