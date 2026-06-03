# ⛽ Smart Petrol Cost Calculator with BUDI MADANI Rebate

An Android mobile application developed for **ICT602 Mobile Technology Assignment** that estimates total petrol costs in Malaysia 
and applies the BUDI95 (BUDI MADANI) fuel subsidy for eligible users.

---

## ✨ Features

- 🛢️ Supports **RON95, RON97 and Diesel** petrol types
- 💰 Auto-fills current **weekly Malaysia fuel price** (updated based on MOF announcement)
- 🧮 Calculates total petrol cost, BUDI95 rebate and final payable amount
- ⚠️ Enforces **200 litre/month cap** on BUDI95 subsidy
- 🔒 BUDI MADANI eligibility question only appears for **RON95 users**
- 🏦 Bank-style price input (always shows 2 decimal places)
- 🧭 Navigation menu with **Home**, **Calculator** and **About** screens

---

## 🧮 How It Works

### Formula

| Step | Calculation |
|------|-------------|
| Total Petrol Cost | Fuel Usage (L) × Price per Litre (RM) |
| BUDI95 Rebate | Subsidised Litres × RM1.99/L _(RON95 eligible only, max 200L/month)_ |
| Final Payable | Total Petrol Cost − BUDI95 Rebate |

### Sample Calculation

| Input | Value |
|-------|-------|
| Petrol Type | RON95 |
| Price per Litre | RM3.92 |
| Fuel Usage | 40 litres |
| BUDI MADANI Eligible | Yes |

- **Total Petrol Cost** = 40 × RM3.92 = **RM156.80**
- **BUDI95 Rebate** = 40 × RM1.99 = **− RM79.60**
- **Final Payable** = RM156.80 − RM79.60 = **RM77.20**

---

## 📋 BUDI95 Rules

- Applies to **RON95 only**
- Limited to **200 litres per month** per eligible user
- Subsidy rate: **RM1.99 per litre**
- If usage exceeds 200L, only the first 200L receives the rebate

---

## 🗂️ Project Structure

PetrolCalculator/
├── app/src/main/
│   ├── java/com/example/petrolcalculator/
│   │   ├── HomeActivity.java       ← Home/landing screen
│   │   ├── MainActivity.java       ← Calculator screen
│   │   └── AboutActivity.java      ← About screen
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_home.xml
│   │   │   ├── activity_main.xml
│   │   │   └── activity_about.xml
│   │   ├── menu/
│   │   │   └── main_menu.xml
│   │   └── values/
│   │       ├── strings.xml
│   │       ├── colors.xml
│   │       └── themes.xml
│   └── AndroidManifest.xml

---

## 🚀 Getting Started

1. Clone this repository
```bash
   git clone https://github.com/eilmie/PetrolCalculator.git
```
2. Open in **Android Studio**
3. Let Gradle sync finish
4. Run on emulator or physical device (Min SDK 24 / Android 7.0)

---

## ⛽ Current Malaysia Fuel Prices (Week of 28 May – 3 Jun 2026)

| Fuel Type | Price |
|-----------|-------|
| RON95 | RM 3.92 / litre |
| RON97 | RM 4.65 / litre |
| Diesel (Peninsular) | RM 4.87 / litre |
| BUDI95 Subsidised Rate | RM 1.99 / litre |

> Prices updated weekly every Wednesday by the Malaysian Ministry of Finance (MOF).

---

## 🛠️ Tech Stack

- **Language:** Java
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **UI:** Material Design Components, CardView

---

## 👤 Author

| | |
|--|--|
| **Name** | MUHAMMAD EILMIE BIN ISMADI |
| **Matric No** | 2025160255 |
| **Course** | ICT602 Mobile Technology |
| **Institution** | UiTM KAMPUS JASIN, MELAKA |

---

## 📄 License

© 2025 MUHAMMAD EILMIE. All rights reserved.  
ICT602 Mobile Technology Assignment — For academic purposes only.
