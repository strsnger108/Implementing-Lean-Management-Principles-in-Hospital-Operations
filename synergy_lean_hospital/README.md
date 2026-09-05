# Synergy Lean Hospital

A white-label, multi-tenant mobile app for hospital operations built with Flutter and Supabase. Designed to implement Lean management principles, reduce operational waste, and improve patient experience.

## Features

### For Patients
- Hospital signup via unique hospital code + phone OTP
- Real-time admission status tracking (Admitted → Ward → Diagnostics → Discharge)
- Discharge readiness notifications
- 1-click feedback mapped to Lean waste categories
- Contact hospital directly from app

### For Staff (Nurses / Front Desk)
- Kanban-style admission board with drag-and-drop status updates
- Discharge tracker with one-tap "Discharge Ready" marking
- Real-time consultant workload (Pareto view)
- Digital 5S audit with photo upload
- Kaizen board for submitting and tracking improvement ideas

### For Admins
- Hospital configuration (branding, departments, consultants)
- Lean metrics dashboard (LOS trends, same-day %, extended stay %)
- Consultant workload analysis
- Value stream stage configuration
- CSV/PDF report export

## Architecture

- **Frontend:** Flutter (Android + iOS)
- **Backend:** Supabase (PostgreSQL, Auth, Edge Functions, Storage)
- **State Management:** Flutter BLoC + Hydrated BLoC
- **Offline Sync:** Drift (SQLite) with Supabase realtime sync
- **Multi-tenancy:** Row-level security via `hospital_code`

## Project Structure

```
lib/
├── main.dart                 # App entry point
├── app.dart                  # Main app widget with routes
├── constants/
│   ├── app_constants.dart    # App-wide constants
│   └── app_theme.dart        # Theme configuration
├── data/
│   ├── models/               # Data models
│   │   ├── hospital_profile.dart
│   │   ├── profile.dart
│   │   ├── admission.dart
│   │   ├── consultant.dart
│   │   ├── los_record.dart
│   │   ├── daily_admission.dart
│   │   └── kaizen_idea.dart
│   ├── repositories/         # Data repositories
│   │   ├── auth_repository.dart
│   │   ├── hospital_repository.dart
│   │   ├── admission_repository.dart
│   │   ├── lean_metrics_repository.dart
│   │   └── sync_repository.dart
│   ├── datasources/
│   │   └── supabase_datasource.dart
│   └── database/
│       └── app_database.dart # Drift local database
├── presentation/
│   ├── bloc/                 # BLoC state management
│   │   ├── auth_bloc.dart
│   │   ├── hospital_bloc.dart
│   │   ├── admission_bloc.dart
│   │   ├── dashboard_bloc.dart
│   │   └── settings_bloc.dart
│   ├── pages/                # UI screens
│   │   ├── splash_page.dart
│   │   ├── role_selection_page.dart
│   │   ├── patient/
│   │   ├── staff/
│   │   └── admin/
│   └── widgets/              # Reusable widgets
├── services/
│   ├── supabase_service.dart
│   ├── notification_service.dart
│   └── sync_service.dart
└── utils/
    ├── validators.dart
    └── formatters.dart
```

## Setup Instructions

### Prerequisites
- Flutter SDK >= 3.0.0
- Dart SDK >= 3.0.0
- Supabase account

### 1. Clone the Repository
```bash
git clone <repository-url>
cd synergy_lean_hospital
```

### 2. Install Dependencies
```bash
flutter pub get
```

### 3. Supabase Setup
1. Create a new project at [supabase.com](https://supabase.com)
2. Go to SQL Editor and run the SQL schema from `docs/supabase_schema.sql`
3. Enable **Phone** and **Email** authentication providers
4. Create storage buckets: `hospital-logos` (public) and `audit-photos` (private)
5. Copy your Project URL and anon public key

### 4. Configure App Constants
Update `lib/constants/app_constants.dart` with your Supabase credentials:
```dart
static const String supabaseUrl = 'https://YOUR_PROJECT_ID.supabase.co';
static const String supabaseAnonKey = 'YOUR_ANON_KEY';
```

### 5. Run the App
```bash
flutter run
```

## Multi-Tenant Setup

Each hospital is identified by a unique `hospital_code` (e.g., `SGH001`).

### Creating a New Hospital
1. Insert into `hospital_profiles`:
   ```sql
   INSERT INTO hospital_profiles (hospital_code, name, primary_color, secondary_color)
   VALUES ('HOSP001', 'Your Hospital Name', '#2b6cb0', '#1a365d');
   ```

2. Create admin user via Supabase Auth (email/password)
3. Insert admin profile:
   ```sql
   INSERT INTO profiles (id, hospital_code, role, name, email)
   VALUES ('user-uuid-here', 'HOSP001', 'admin', 'Admin Name', 'admin@hospital.com');
   ```

4. Hospital staff and patients sign up using the hospital code `HOSP001`

## Database Schema

See `docs/supabase_schema.sql` for complete schema with RLS policies.

### Core Tables
- `hospital_profiles` - Tenant configuration and branding
- `profiles` - Users (patients, staff, admins)
- `departments` - Hospital departments
- `consultants` - Doctor/consultant roster
- `admissions` - Patient admissions with status tracking
- `los_records` - Length of stay records
- `daily_admissions` - Daily admission counts
- `value_stream_stages` - Configurable patient journey stages
- `patient_stage_progress` - Patient journey tracking
- `audits_5s` - 5S audit records
- `kaizen_ideas` - Staff improvement ideas
- `patient_feedback` - Patient feedback mapped to Lean wastes
- `notifications` - Push notifications

## Deployment

### Build APK (Android)
```bash
flutter build apk --release
```

### Build App Bundle (Google Play)
```bash
flutter build appbundle --release
```

### Build iOS (requires Mac)
```bash
flutter build ios --release
```

## Lean Integration

This app directly supports Lean healthcare principles:
- **Waiting waste reduction:** Real-time status updates reduce patient waiting anxiety
- **Motion waste reduction:** Digital forms and workflows reduce physical movement
- **Over-processing waste reduction:** Standardized digital workflows eliminate redundant paperwork
- **Defects waste reduction:** Audit trails and standardized checklists reduce errors
- **Inventory waste reduction:** Digital tracking of bed/consultant availability

## Future Enhancements
- Integration with existing Hospital Information Systems (HIS)
- Billing and payment gateway
- AI-powered discharge prediction
- Advanced analytics and predictive insights
- Multi-language support (regional languages)

## License

MIT License

## Contact

For support or customization inquiries, contact the development team.
