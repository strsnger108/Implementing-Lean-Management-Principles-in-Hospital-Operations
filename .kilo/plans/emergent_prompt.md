# Emergent App Build Prompt — Synergy Lean Hospital

## Copy-Paste Prompt for Emergent / AI App Builder

```
Build a complete, production-ready white-label mobile app called "Synergy Lean Hospital" for hospital operations management using Lean principles.

## Tech Stack
- Frontend: Flutter (Android + iOS from single codebase)
- Backend: Supabase (PostgreSQL, Auth, Edge Functions, Storage)
- Offline sync: Flutter drift (SQLite) with Supabase realtime sync
- State management: flutter_bloc + hydrated_bloc
- Charts: fl_chart
- PDF generation: pdf package

## Project Structure
Create a Flutter project named `synergy_lean_hospital` with this exact folder structure:
lib/
├── main.dart
├── app.dart
├── constants/
│   ├── app_constants.dart
│   └── app_theme.dart
├── data/
│   ├── models/ (hospital_profile.dart, profile.dart, admission.dart, consultant.dart, los_record.dart, daily_admission.dart, kaizen_idea.dart)
│   ├── repositories/ (auth_repository.dart, hospital_repository.dart, admission_repository.dart, lean_metrics_repository.dart, sync_repository.dart)
│   ├── datasources/supabase_datasource.dart
│   └── database/app_database.dart (drift)
├── presentation/
│   ├── bloc/ (auth_bloc.dart, hospital_bloc.dart, admission_bloc.dart, dashboard_bloc.dart, settings_bloc.dart + event/state files)
│   ├── pages/
│   │   ├── splash_page.dart
│   │   ├── role_selection_page.dart
│   │   ├── common/ (ai_assistant_page.dart)
│   │   ├── patient/ (patient_login_page.dart, patient_dashboard_page.dart, patient_tracking_page.dart, patient_feedback_page.dart, patient_notifications_page.dart)
│   │   ├── staff/ (staff_login_page.dart, staff_admission_board_page.dart, staff_discharge_tracker_page.dart, staff_consultant_load_page.dart, staff_5s_audit_page.dart, staff_kaizen_board_page.dart)
│   │   └── admin/ (admin_login_page.dart, admin_dashboard_page.dart, admin_hospital_config_page.dart, admin_consultants_page.dart, admin_reports_page.dart, admin_value_stream_page.dart)
│   └── widgets/ (custom_button.dart, custom_textfield.dart, status_badge.dart, consultant_card.dart, lean_metric_card.dart)
├── services/
│   ├── supabase_service.dart
│   ├── notification_service.dart
│   ├── sync_service.dart
│   └── ai_assistant_service.dart (LeanBot)
└── utils/
    ├── validators.dart
    └── formatters.dart

## Database Schema
Create a Supabase PostgreSQL database with these exact tables: hospital_profiles, profiles, departments, consultants, admissions, los_records, daily_admissions, value_stream_stages, patient_stage_progress, audits_5s, kaizen_ideas, patient_feedback, notifications.

Multi-tenancy: Every table has hospital_code TEXT column. Use Row Level Security (RLS) policies so users only see data from their own hospital_code.

Create a helper function get_user_hospital_code() that returns the hospital_code from JWT claims or profiles table.

Enable uuid-ossp extension. Create auto-update triggers for updated_at columns on hospital_profiles, profiles, admissions, kaizen_ideas.

## Authentication
- Patients: Phone number + OTP (Supabase Magic Link / SMS)
- Staff/Admin: Email + password (Supabase Auth)
- Store hospital_code in auth options during signup
- After login, fetch profile from profiles table using user ID

## Role-Based Features

### Patient
- Login with hospital code + phone OTP
- Dashboard showing current admission status, consultant, bed number, expected discharge
- Tracking page showing patient journey stages with progress indicators
- Feedback page with 1-click rating + Lean waste category selection (waiting, motion, overprocessing, defects, inventory, communication, other)
- Notifications page for discharge alerts and updates

### Staff
- Login with email/password
- Admission Board: Kanban-style columns (Admitted, In Ward, Diagnostics, Discharge Ready) with drag-to-update status
- Discharge Tracker: List patients marked discharge_ready, one-tap to complete discharge
- Consultant Load: Bar chart showing active cases per consultant with cumulative percentage
- 5S Audit: Form with area name, score slider, findings, photo upload to Supabase Storage
- Kaizen Board: Submit improvement ideas with title, description, waste category; view list of ideas with status chips

### Admin
- Login with email/password
- Dashboard: KPI cards (Total Admissions, Avg LOS, Same-Day %, Extended Stay %), LOS distribution bar chart, consultant workload bar chart, monthly admissions line chart
- Hospital Config: Edit name, phone, email, address, colors; upload logo to Supabase Storage
- Consultants: CRUD list with name, phone, email, department; CSV import placeholder
- Value Stream: CRUD for patient journey stages with order_index and target_minutes
- Reports: Generate PDF reports for monthly admissions, consultant workload, lean metrics summary using pdf package

## UI/UX Requirements
- Material Design 3
- Primary color: #2b6cb0, Secondary: #1a365d
- Dark blue AppBar with white text
- Responsive layout for phones and tablets
- Loading states, error SnackBars, empty states
- Charts must be interactive and legible

## Supabase Setup
- Enable Email and Phone auth providers
- Create storage buckets: hospital-logos (public), audit-photos (private)
- RLS policies must enforce hospital_code isolation on every table

## Deliverables
1. Complete Flutter project with all files above
2. pubspec.yaml with all dependencies
3. docs/supabase_schema.sql with complete schema + RLS + triggers
4. README.md with setup instructions
5. investor_pitch.html standalone pitch deck with Chart.js, ROI calculator, problem/solution/market sections
6. AI Assistant Bot "LeanBot" with service, chat UI, and role-aware responses

## AI Assistant Bot Feature
Add an AI chatbot assistant called "LeanBot" to help users navigate the app and answer hospital operations questions.

### AI Bot Architecture
- Service: `lib/services/ai_assistant_service.dart`
- UI: Floating action button on all screens that opens chat bottom sheet
- Pages: `lib/presentation/pages/common/ai_assistant_page.dart`

### AI Bot Capabilities
1. **App Navigation Help**: Guide users to correct screens based on their role
   - Example: "How do I mark a patient as discharge ready?" → Navigate to Discharge Tracker
   - Example: "Where can I see my consultation status?" → Navigate to Patient Dashboard

2. **Lean Healthcare Q&A**: Answer questions about Lean principles
   - Pre-loaded knowledge base about 5S, Kaizen, Value Stream Mapping, waste reduction
   - Contextual answers based on hospital data (e.g., "Our hospital's average LOS is 3.29 days")

3. **Hospital Operations Assistance**:
   - Explain admission statuses (admitted, in_ward, in_diagnostics, discharge_ready, discharged)
   - Clarify consultant workload metrics
   - Guide through 5S audit process
   - Help submit Kaizen ideas

4. **Smart Suggestions**:
   - Proactive alerts: "Patient X has been in diagnostics for 2 hours, consider updating status"
   - Workflow tips: "Discharge rounds are scheduled at 10 AM, mark patients ready before then"

### AI Bot UI Design
- Floating chat bubble in bottom-right corner on all screens
- Tap opens full-screen chat interface with message history
- Quick suggestion chips for common queries
- Typing indicator while "thinking"
- Role-aware responses (different answers for patient vs staff vs admin)

### AI Bot Implementation
- Use rule-based response system with keyword matching and templates
- Store chat history in local SQLite via drift
- Context-aware: knows current user role, hospital, and active admission
- Fallback to generic helpful responses when unsure
- Include onboarding tutorial on first launch

### AI Bot Integration Points
- Patient Dashboard: "Track my admission status" button
- Staff Admission Board: "How do I move a patient?" tooltip
- Admin Dashboard: "Explain LOS metrics" info icon
- All forms: "Need help?" link next to submit button
```

## Important
- All code must be complete and runnable, not pseudocode
- Include proper error handling and null safety
- Use SupabaseService singleton for all database calls
- Follow existing Flutter/BLoC patterns in the project
- Do not use deprecated Supabase methods like .filter(); use .neq(), .eq(), etc.
- Ensure all dart:io imports are present where File is used
- Ensure all Bloc event/state files use part directives correctly
```

## How to Use This Prompt
1. Copy the entire prompt above
2. Paste into Emergent or your AI app builder
3. The builder should generate the complete Flutter project with Supabase backend
4. After generation, run `flutter pub get` and update `lib/constants/app_constants.dart` with your Supabase credentials

## Notes
- This prompt is designed to reproduce the existing `synergy_lean_hospital` project structure
- If Emergent supports file uploads, you can also attach the existing `docs/supabase_schema.sql` as a reference
- For investor pitch, open `investor_pitch.html` directly in any browser after project generation
