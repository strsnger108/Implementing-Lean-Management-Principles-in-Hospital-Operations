# Lean Hospital - Operations & Quality Management (Android)

An Android application implementing **Lean Management Principles in Hospital Operations**, based on the empirical study conducted at **Synergy Global Hospital, Ranchi, Jharkhand** by **Gunjan Prakash** (MBA in Hospital Administration and Healthcare Management, Dr. D. Y. Patil Vidyapeeth Pune).

---

## 🏥 Key Features Ported & Built

### 1. Executive Lean Operations Dashboard
- **Monthly Cohort Filtering**: Analyze admissions and LOS for All Months, November 2025, December 2025, January 2026, and February 2026.
- **Dynamic Key Performance Indicators (KPIs)**:
  - Total Admissions & Average Admissions/Day
  - Average Length of Stay (LOS) vs Lean Target (≤ 2.8 days)
  - Same-Day Discharge Rate (0 days)
  - Extended Stay (6+ days)
  - Documentation Gap (Incomplete Records %) vs Target (< 2.0%)
- **Interactive Jetpack Compose Visual Charts**:
  - **Monthly Admission & Avg LOS Trend**: Dual-axis bar and line chart displaying admission volume alongside average length of stay.
  - **LOS Distribution Histogram**: Patient frequency across 0 to 18 stay days, color-coded by clinical category (Short, Medium, Extended).
  - **Consultant Workload Pareto Chart**: Distribution of cases across doctors (e.g., Dr. Rahul Sinha managing 40.1% of patients) with cumulative percentage curve demonstrating 80/20 bottleneck dynamics.
  - **LOS Category Doughnut Chart**: Interactive breakdown of same-day, short-stay, medium-stay, and extended-stay patients.
- **Lean Improvement Action Items**: Data-driven recommendations covering discharge rounds, referral leveling, and documentation protocols.

### 2. Inpatient Patient Flow Registry
- Real-time patient census tracking active inpatients and discharged records.
- Search and filter by patient name, IPD number, consultant, and department.
- **Lean Discharge Processing**: Record discharge date, actual LOS, and tag root-cause delay reasons (TPA Insurance reconciliation, doctor signature delays, pharmacy returns).

### 3. Value Stream Mapping (VSM) & 7 Wastes (Muda)
- **Patient Journey VSM**: Quantifies Current State (78.5 hrs lead time, 21.5% value-added time, 78.5% non-value waiting) versus Future Lean Target (42.0 hrs lead time, 48.0% value-added time).
- **Stage Bottleneck Breakdown**: Detailed cycle and waiting times from admission to bed turnover.
- **7 Wastes of Healthcare Logger**: Frontline incident logging for Waiting, Motion, Overprocessing, Defects, Inventory, Transportation, Overproduction, and Underutilized Talent.

### 4. Lean Operations & Quality Tools
- **5S Workplace Organization**: Department audit scoring (Sort, Set in order, Shine, Standardize, Sustain) for OPD, Emergency, OT, Pharmacy, and Wards.
- **Kaizen Continuous Improvement Tracker**: Status tracking for initiatives such as 10:00 AM Standardized Discharge Rounds, Inpatient Discharge Lounge, and Kanban Bed Boards.
- **Bed Capacity & Financial Simulator**: Interactive tool calculating annual bed-days freed, additional patient capacity accommodated, and financial value released.

### 5. MBA Project Research Study Viewer
- Complete structured browser for the academic report: Executive Summary, Hospital Infrastructure Profile, 5 Core Research Objectives, Paired t-Test Statistical Analysis (*t = 4.82, p < 0.001*), Key Findings, and Strategic Roadmap.

---

## 🛠 Technology Stack

- **Platform**: Android (minSdk 24, targetSdk 36, compileSdk 36)
- **Language**: Kotlin 2.2+
- **UI Toolkit**: Jetpack Compose with Material 3 (M3)
- **Persistence**: Room Database with SQLite and Kotlin Coroutines/Flow
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Asset Integration**: Custom adaptive launcher icon and clinical hero imagery
