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

## 13. Investor Pitch Deliverable
**Status:** Completed
- Created `synergy_lean_hospital/investor_pitch.html`
- Standalone browser-ready HTML using Chart.js CDN
- Sections: Problem (35% waste, consultant bottleneck, discharge delays), Solution (Patient/Staff/Admin roles), Market Opportunity (62% private share, ₹50L Cr market, tier-2 gap), Projected Impact (LOS trajectory + admissions charts), ROI Calculator (editable inputs for LOS, admissions, bed cost), Business Model (SaaS + implementation fee + premium modules), Why Now (NHP 2017, Ayushman Bharat, post-COVID shift), Team & Traction, Contact
- No backend required; opens directly in any browser for investor presentations

## 14. Launch & Pitch Execution Guide
**Goal:** Show the app and dashboard to investors even if you cannot run the full Flutter build locally.

### Step 1: Open the Investor Pitch Dashboard (Immediate)
- File path in this workspace: `/workspace/ba446243-7dd0-4603-8145-96022ac9a5e9/sessions/agent_aafa8d1b-721f-4809-a06e-13f63bf7b1ad/synergy_lean_hospital/investor_pitch.html`
- If your environment supports file browsing, navigate to that path and open it in Chrome/Edge/Firefox.
- If you have terminal access: `open synergy_lean_hospital/investor_pitch.html` (macOS) or `xdg-open synergy_lean_hospital/investor_pitch.html` (Linux).
- If none of the above work, copy the full HTML from the file and paste it into a new file named `investor_pitch.html` on your local machine, then double-click it.
- The ROI calculator at the bottom is interactive — edit the numbers live during the pitch.

### Step 2: Run the Flutter App Demo (If Flutter is installed)
```bash
cd /workspace/ba446243-7dd0-4603-8145-96022ac9a5e9/sessions/agent_aafa8d1b-721f-4809-a06e-13f63bf7b1ad/synergy_lean_hospital
flutter pub get
flutter run
```
- If you do not have Flutter installed, use the HTML pitch deck alone for the investor meeting.
- For a quick demo without building: show the investor the `investor_pitch.html` on a laptop, then verbally walk through the Flutter app screens described in the pitch deck.

### Step 3: Prepare Before the Pitch
1. Replace placeholder Supabase credentials in `lib/constants/app_constants.dart` if you want a live demo.
2. Replace placeholder contact info in `investor_pitch.html` with real email/phone.
3. Practice the 5-minute flow: Problem → Solution → Market → Impact → ROI → Business Model → Team.
4. If you cannot open the HTML file, take screenshots of the pitch deck on a machine that can open it, and present those.

### Step 4: If You Only Have This Chat Interface
- Tell me: “I need the investor_pitch.html content saved locally.” I will output the full file again so you can copy-paste it into a local `.html` file and open it immediately.

## 15. Supabase Setup Troubleshooting & Safe Migration Path
**Problem:** The SQL schema failed with “Failed to get project's logs” or similar Supabase infrastructure errors.

### Root Causes
- Supabase SQL Editor cache/lock after a failed migration
- Auth not enabled before running schema that references `auth.users`
- `uuid-ossp` extension not enabled
- Running the full schema in one batch triggers a Postgres timeout/migration lock

### Safe Recovery Steps
1. **Retry the SQL Editor**
   - Refresh Supabase Dashboard
   - Open a new SQL Editor tab
   - Re-run the schema script from `docs/supabase_schema.sql`

2. **If the error persists, run schema in smaller batches**
   - Batch 1: extensions + `hospital_profiles` + `profiles` + `departments` + `consultants`
   - Batch 2: `admissions` + `los_records` + `daily_admissions`
   - Batch 3: `value_stream_stages` + `patient_stage_progress` + `audits_5s` + `kaizen_ideas` + `patient_feedback` + `notifications`
   - Batch 4: RLS policies + triggers

3. **Pre-flight checks before running SQL**
   - Confirm Auth is enabled: Dashboard → Settings → Auth → Email/Phone providers ON
   - Confirm `uuid-ossp` is enabled: Dashboard → Database → Extensions → search `uuid-ossp` → Enable
   - Create storage buckets first: `hospital-logos` (public), `audit-photos` (private)

4. **If SQL Editor still fails**
   - Use Supabase CLI locally: `supabase db reset` after linking project
   - Or use Table Editor to manually create tables via UI
   - As a last resort, delete the project and create a fresh Supabase project, then re-run schema

### Validation After Fix
- Table Editor should show all 13 tables
- Insert test hospital:
  ```sql
  INSERT INTO hospital_profiles (hospital_code, name, primary_color, secondary_color)
  VALUES ('SGH001', 'Synergy Global Hospital', '#2b6cb0', '#1a365d');
  ```
- Auth → Users should allow creating a test admin
- `profiles` table should allow inserting the admin row linked to `SGH001`

### Flutter App Connection
- Update `lib/constants/app_constants.dart` with Supabase URL + anon key
- Run `flutter pub get && flutter run`
