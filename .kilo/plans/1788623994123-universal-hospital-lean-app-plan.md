# Universal Hospital Lean Operations App — Implementation Plan

## 1. Objective
Build a **white-label, multi-tenant mobile app** that any hospital can adopt to digitize patient flow, reduce Lean wastes (waiting, over-processing, motion, defects), and give patients transparency into their care journey. The app must be configurable per hospital without code changes.

## 2. Architecture
- **Frontend:** Flutter (Android + iOS from single codebase)
- **Backend:** Supabase (PostgreSQL, Auth, Edge Functions, Storage)
- **Offline sync:** Flutter `drift` (SQLite) with Supabase realtime sync
- **Admin panel:** Supabase-based web dashboard (or lightweight Flutter Web) for hospital configuration
- **Multi-tenancy:** Row-level security via `hospital_code` column on every table; Supabase RLS policies enforce isolation

## 3. Tenant Isolation Model
| Entity | Isolation Mechanism |
|--------|---------------------|
| Patients | Sign up with `hospital_code` + phone/email; scoped to that hospital |
| Staff | Admin creates accounts; `hospital_code` auto-assigned |
| Records | Every table includes `hospital_code`; RLS guarantees no cross-hospital reads |
| Branding | `hospital_profiles` table stores logo, primary color, name |

## 4. Data Model (Core Tables)
```sql
hospital_profiles (id, hospital_code, name, logo_url, primary_color, address, phone, created_at)
profiles (id, hospital_code, role ['patient','staff','admin'], name, phone, email, dob, created_at)
admissions (id, hospital_code, patient_id, consultant_id, admission_date, discharge_date, status, department, diagnosis_code)
consultants (id, hospital_code, name, department, phone, email, color_tag)
daily_admissions (id, hospital_code, date, count)
los_records (id, hospital_code, admission_id, los_days, category ['same_day','short','medium','long'])
```

## 5. Role-Based Feature Set

### 5.1 Patient View
- **Onboarding:** Enter hospital code + phone OTP; auto-link to hospital
- **Dashboard:** Current admission status, expected discharge date, consultant name
- **Tracking:** Real-time bed allocation stage (Admission → Ward → Diagnostics → Discharge)
- **Notifications:** Discharge readiness alerts, appointment reminders, bill summary
- **Feedback:** 1-click satisfaction rating mapped to Lean waste categories (waiting, communication, cleanliness)

### 5.2 Staff View (Nurse / Admin)
- **Admission Board:** Kanban-style view of inpatients by status
- **Discharge Tracker:** Patients flagged for discharge today; one-tap "Discharge Ready"
- **Consultant Load:** Real-time count of active cases per doctor (Pareto view)
- **5S Audit:** Digital checklist for ward/storage organization with photo upload
- **Kaizen Board:** Staff submit improvement ideas; admin assigns PDSA cycles

### 5.3 Admin View (Hospital Management)
- **Hospital Config:** Branding (logo, color), department list, consultant roster upload (CSV), custom fields
- **Lean Metrics:** Avg LOS trend, same-day discharge %, extended stay %, incomplete record %
- **Value Stream:** Configurable patient journey stages (add/remove steps per hospital workflow)
- **Reports:** Export CSV/PDF of monthly admissions, LOS distribution, consultant workload

## 6. Customization Mechanism (No-Code for Hospitals)
Hospitals configure the app via the Admin Panel:
- **Branding:** Upload logo, set primary/secondary colors
- **Departments:** Add/edit/delete departments (maps to admission form dropdown)
- **Consultants:** Bulk import CSV or add individually; assign departments
- **Patient Journey Stages:** Define value-stream steps (e.g., "Triage", "Lab", "Pharmacy", "Billing") and estimated target times
- **Notifications:** Toggle SMS/email/push for discharge alerts, appointment reminders
- **Language:** Select primary language (English + regional language support)

## 7. Offline-First Strategy
- Critical patient lists and admission boards cached locally via `drift`
- Staff can update discharge status offline; syncs when connectivity returns
- Patient app shows last-known status with "Last updated X mins ago" timestamp
- Conflict resolution: Last-write-wins for status changes; server timestamp authoritative

## 8. Authentication & Security
- **Patients:** Phone number + OTP (Supabase Magic Link / SMS)
- **Staff/Admin:** Email + password (Supabase Auth); admin can reset passwords
- **RLS Policies:** Every query filtered by `hospital_code`; staff cannot access other hospitals' data
- **PII:** Patient names/mobiles masked in staff lists where not clinically necessary; audit log for data access

## 9. Implementation Phases

### Phase 1: Foundation (Weeks 1-3)
- Flutter project setup, Supabase project creation, RLS policies
- Auth flows (patient OTP, staff email/password)
- `hospital_profiles` and `profiles` tables with CRUD
- Basic patient onboarding (hospital code → profile creation)

### Phase 2: Core Patient Flow (Weeks 4-7)
- Admission creation by staff
- Patient status tracking dashboard
- Discharge workflow (mark ready → notify patient)
- Offline sync for admission board

### Phase 3: Lean Analytics & Admin (Weeks 8-10)
- Admin panel for hospital configuration
- Metrics calculation engine (LOS, same-day %, consultant Pareto)
- Charts matching existing dashboard (trend, distribution, consultant workload)
- CSV/PDF export

### Phase 4: Polish & White-Label (Weeks 11-12)
- Custom branding per hospital
- Regional language support scaffold
- Push notification setup
- User testing with Synergy Global Hospital data (seed from existing Excel/dashboard)

## 10. Validation Plan
1. **Multi-tenant isolation test:** Create two hospital codes; verify staff from Hospital A cannot see Hospital B data
2. **Offline resilience test:** Disable network; update discharge status; reconnect and confirm sync
3. **Lean metric accuracy test:** Seed 381 patient records from project data; verify dashboard numbers match existing HTML dashboard
4. **Customization test:** Change hospital logo/color via admin panel; confirm app reflects changes without redeploy
5. **Auth test:** Patient signup with invalid hospital code rejected; staff account creation restricted to admin role

## 11. Out of Scope (Explicit)
- Clinical decision support / diagnosis algorithms
- Billing / payment gateway integration
- Integration with existing hospital HIS/EMR systems (future phase)
- Advanced AI predictions (future phase)

## 12. Key Risks & Mitigations
| Risk | Mitigation |
|------|-----------|
| Supabase RLS misconfiguration leaks data | Unit-test RLS policies; code review required before deploy |
| Offline sync conflicts | Last-write-wins + manual reconciliation UI for admins |
| Hospital config drift | Version config snapshots; support rollback |
| Low staff adoption | Mirror existing dashboard UX; train on Lean waste reduction benefits |
