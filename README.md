# Currency Exchange Project 🚀💳🌍

![Maven](https://img.shields.io/badge/Maven-3.8.7-blue?style=for-the-badge&logo=apache)
![Database](https://img.shields.io/badge/Database-SQLite-brightgreen?style=for-the-badge)
![Apache Tomcat](https://img.shields.io/badge/Server-Apache%20Tomcat-red?style=for-the-badge&logo=apache)
![Jakarta EE](https://img.shields.io/badge/Platform-Jakarta%20EE-orange?style=for-the-badge)
![MVC Pattern](https://img.shields.io/badge/Pattern-MVC-yellow?style=for-the-badge)

![Currency Exchange](.github/pictures/currency_exchange.webp)

# Table of Contents 📚
- [Important Things](#important-things)
- [Objective of the Project 📈](#objective-of-the-project-)
- [Tools Used in the Project 🛠️](#tools-used-in-the-project-)
- [Endpoints 🔑](#endpoints-)
- [Quick Start Guide for Currency Exchange ⚡](#quick-start-guide-)
- [Share Your Thoughts 💬✨](#share-your-thoughts-)
- [🌟 Overview](#overview-)
---

# Important Things

## Objective of the Project 📈
- Get to know the MVC pattern
- Get acquainted with SQL language and learn basic DDL, DML commands
- REST API - correct naming of resources
- Use of HTTP response codes
- Learn to work with Java servlets

## Tools Used in the Project 🛠️
- 🚀 **Servlet API:** A standardized API designed for implementation on the server and working with the client using a request-response scheme.
- 💾 **SQLite:** Utilized for in-memory data storage during runtime in this project.
- 🔌 **Apache Commons DBCP:** Used to connect to the database and get connections from the connection pool.
- 🐱‍👤 **Apache Tomcat:** A Java web application server.
- 📄 **Jackson JSON:** A suite of data-processing tools for Java (and the JVM platform).
- ⚙️ **Maven:** A software project management and comprehension tool.

---

# Endpoints 🔑

## 💵 Currencies

### GET ```/currencies```
*Retrieve a list of currencies.*

```json
[
    {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },   
    {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    }
]
```
### HTTP Response Codes:
- Success - **200**
- Error (e.g., database unavailable) - **500**

### GET ```/currency/EUR```
*Retrieve a specific currency by its code.*

```json
{
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```
### HTTP Response Codes:
- Success - 200
- Currency code missing in the address - 400
- Currency not found - 404
- Error (e.g., database unavailable) - 500

### POST ```/currencies```
*Add a new currency to the database. Data is sent in the request body as form fields (x-www-form-urlencoded). Required fields: name, code, sign.*

```json
{
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```
### HTTP Response Codes:
- Success - 201
- Required form field missing - 400
- Currency with this code already exists - 409
- Error (e.g., database unavailable) - 500

## 💱 Exchange Rates

### GET ```/exchangeRates```
*Retrieve a list of all exchange rates.*

```json
[
    {
        "id": 0,
        "baseCurrency": {
            "id": 0,
            "name": "United States dollar",
            "code": "USD",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 1,
            "name": "Euro",
            "code": "EUR",
            "sign": "€"
        },
        "rate": 0.99
    }
]

```
### HTTP Response Codes:
- Success - 200
- Error (e.g., database unavailable) - 500

### GET ```/exchangeRate/{baseCurrencyCode}{targetCurrencyCode}```
*Retrieve a specific exchange rate.*

```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```
### HTTP Response Codes:
- Success - **200**
- Currency pair codes missing in the address - **400**
- Exchange rate for the pair not found - **404**
- Error (e.g., database unavailable) - **500**

### POST ```/exchangeRates```
Add a new exchange rate to the database. Data is sent in the request body as form fields (x-www-form-urlencoded). Required fields: `baseCurrencyCode`, `targetCurrencyCode`, `rate`. Example form fields:

- `baseCurrencyCode` - USD
- `targetCurrencyCode` - EUR
- `rate` - 0.99

Example response - JSON representation of the inserted record, including its ID:

```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```
### HTTP Response Codes:
- Success - **201**
- Required form field missing - **400**
- Currency pair with this code already exists - **409**
- One (or both) currencies in the pair do not exist in the database - **404**
- Error (e.g., database unavailable) - **500**

### PATCH ```/exchangeRate/{baseCurrencyCode}{targetCurrencyCode}```
*Update an existing exchange rate in the database. The currency pair is specified by the consecutive currency codes in the request address. Data is sent in the request body as form fields (x-www-form-urlencoded). The only form field is rate.*

```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
}
```
### HTTP Response Codes:
- Success - **201**
- Required form field missing - **400**
- Currency pair not found in the database - **404**
- Error (e.g., database unavailable) - **500**

## 💳 Currency Exchange
**GET** ```/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT```
*Calculate the conversion of a specific amount from one currency to another.*
```json
{
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A$"
    },
    "rate": 1.45,
    "amount": 10.00,
    "convertedAmount": 14.50
}
```
# Quick Start Guide for Currency Exchange ⚡

## Installation Instructions

1. Clone the repository:

   ```bash
   https://github.com/tem1dev/currency-exchange.git
   ```  
2. Open IntelliJ IDEA.
3. In the main menu, click on Open and select the folder you cloned in the first step.
4. In IntelliJ IDEA, go to the Run menu, then select Edit -> Configurations
5. In the window that appears, click the + button and add Tomcat Server -> Local.
6. Next to the warning Warning: No artifacts marked for deployment, click the Fix button.
7. In the new window, select currency-exchange.war exploded.
7. Leave the Application context field as just /.
8. Click Apply and start Tomcat.  

# Share Your Thoughts 💬✨
I am always eager to enhance my programming skills and improve my project implementations. Your insights, suggestions, and constructive criticism are invaluable to me! If you have any feedback or would like to discuss any aspect of this project, I would love to hear from you.
Feel free to open an issue and share your thoughts or ideas! Your contributions can help shape the future of this project. Thank you for your support! 🙌
👉 [Share Your Feedback Here](https://github.com/tem1dev/currency-exchange/issues).  

# 🌟 Overview
We would like to extend our heartfelt thanks to **Sergey Zhukov** for his invaluable contributions and support throughout this project. You can check out his work on GitHub: [Sergey Zhukov's GitHub](https://github.com/zhukovsd).
Additionally, for those interested in furthering their Java knowledge, we recommend the Java Roadmap course available here: [Java Backend Learning Course](https://zhukovsd.github.io/java-backend-learning-course/).
