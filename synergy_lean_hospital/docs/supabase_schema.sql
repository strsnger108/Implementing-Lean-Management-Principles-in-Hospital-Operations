-- ============================================================
-- Synergy Lean Hospital — Supabase Schema
-- Prerequisites:
--   1. Enable Authentication in Supabase Dashboard
--      (Settings → Auth → Enable Email and/or Phone provider)
--   2. Create storage buckets: hospital-logos (public), audit-photos (private)
--   3. Run this entire script in Supabase SQL Editor
-- ============================================================

-- ------------------------------------------------------------
-- Extensions
-- ------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ------------------------------------------------------------
-- 1. Hospital Profiles (Tenant Root)
-- ------------------------------------------------------------
CREATE TABLE hospital_profiles (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  logo_url TEXT,
  primary_color TEXT DEFAULT '#2b6cb0',
  secondary_color TEXT DEFAULT '#1a365d',
  address TEXT,
  phone TEXT,
  email TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 2. User Profiles (Patients, Staff, Admins)
--    Must be created AFTER auth.users exists (enable Auth first)
-- ------------------------------------------------------------
CREATE TABLE profiles (
  id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  role TEXT NOT NULL CHECK (role IN ('patient', 'staff', 'admin')),
  name TEXT NOT NULL,
  phone TEXT,
  email TEXT,
  dob DATE,
  gender TEXT CHECK (gender IN ('male', 'female', 'other')),
  address TEXT,
  emergency_contact TEXT,
  emergency_phone TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 3. Departments
-- ------------------------------------------------------------
CREATE TABLE departments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  name TEXT NOT NULL,
  description TEXT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 4. Consultants
-- ------------------------------------------------------------
CREATE TABLE consultants (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  name TEXT NOT NULL,
  department_id UUID REFERENCES departments(id),
  phone TEXT,
  email TEXT,
  color_tag TEXT DEFAULT '#2b6cb0',
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 5. Admissions
-- ------------------------------------------------------------
CREATE TABLE admissions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  patient_id UUID NOT NULL REFERENCES profiles(id),
  consultant_id UUID REFERENCES consultants(id),
  department_id UUID REFERENCES departments(id),
  admission_date DATE NOT NULL,
  discharge_date DATE,
  expected_discharge_date DATE,
  status TEXT NOT NULL DEFAULT 'admitted' CHECK (status IN ('admitted', 'in_ward', 'in_diagnostics', 'discharge_ready', 'discharged')),
  diagnosis_code TEXT,
  notes TEXT,
  bed_number TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 6. LOS Records
-- ------------------------------------------------------------
CREATE TABLE los_records (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  admission_id UUID NOT NULL REFERENCES admissions(id),
  los_days INTEGER NOT NULL,
  category TEXT NOT NULL CHECK (category IN ('same_day', 'short', 'medium', 'long')),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 7. Daily Admissions (Aggregated)
-- ------------------------------------------------------------
CREATE TABLE daily_admissions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  date DATE NOT NULL,
  count INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(hospital_code, date)
);

-- ------------------------------------------------------------
-- 8. Value Stream Stages
-- ------------------------------------------------------------
CREATE TABLE value_stream_stages (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  name TEXT NOT NULL,
  order_index INTEGER NOT NULL,
  target_minutes INTEGER NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 9. Patient Stage Progress
-- ------------------------------------------------------------
CREATE TABLE patient_stage_progress (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  admission_id UUID NOT NULL REFERENCES admissions(id),
  stage_id UUID NOT NULL REFERENCES value_stream_stages(id),
  entered_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  completed_at TIMESTAMP WITH TIME ZONE,
  is_completed BOOLEAN DEFAULT FALSE
);

-- ------------------------------------------------------------
-- 10. 5S Audits
-- ------------------------------------------------------------
CREATE TABLE audits_5s (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  area_name TEXT NOT NULL,
  conducted_by UUID REFERENCES profiles(id),
  score INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),
  findings TEXT,
  photos JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 11. Kaizen Ideas
-- ------------------------------------------------------------
CREATE TABLE kaizen_ideas (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  submitted_by UUID REFERENCES profiles(id),
  title TEXT NOT NULL,
  description TEXT,
  category TEXT CHECK (category IN ('waiting', 'motion', 'overprocessing', 'defects', 'inventory', 'other')),
  status TEXT NOT NULL DEFAULT 'submitted' CHECK (status IN ('submitted', 'reviewing', 'implementing', 'completed', 'rejected')),
  assigned_to UUID REFERENCES profiles(id),
  pdsa_notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 12. Patient Feedback
-- ------------------------------------------------------------
CREATE TABLE patient_feedback (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  admission_id UUID REFERENCES admissions(id),
  patient_id UUID REFERENCES profiles(id),
  overall_rating INTEGER CHECK (overall_rating >= 1 AND overall_rating <= 5),
  waste_category TEXT CHECK (waste_category IN ('waiting', 'motion', 'overprocessing', 'defects', 'inventory', 'communication', 'other')),
  comments TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- 13. Notifications
-- ------------------------------------------------------------
CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  hospital_code TEXT NOT NULL REFERENCES hospital_profiles(hospital_code),
  user_id UUID REFERENCES profiles(id),
  title TEXT NOT NULL,
  body TEXT NOT NULL,
  type TEXT CHECK (type IN ('discharge_ready', 'appointment', 'bill', 'general')),
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Enable Row Level Security (RLS)
-- ------------------------------------------------------------
ALTER TABLE hospital_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE departments ENABLE ROW LEVEL SECURITY;
ALTER TABLE consultants ENABLE ROW LEVEL SECURITY;
ALTER TABLE admissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE los_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE daily_admissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE value_stream_stages ENABLE ROW LEVEL SECURITY;
ALTER TABLE patient_stage_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE audits_5s ENABLE ROW LEVEL SECURITY;
ALTER TABLE kaizen_ideas ENABLE ROW LEVEL SECURITY;
ALTER TABLE patient_feedback ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;

-- ------------------------------------------------------------
-- Helper: get hospital_code from current user context
-- ------------------------------------------------------------
CREATE OR REPLACE FUNCTION get_user_hospital_code()
RETURNS TEXT AS $$
BEGIN
  RETURN COALESCE(
    (current_setting('request.jwt.claims', true)::json->>'hospital_code'),
    (SELECT hospital_code FROM profiles WHERE id = auth.uid() LIMIT 1)
  );
END;
$$ LANGUAGE plpgsql STABLE;

-- ------------------------------------------------------------
-- RLS Policies
-- ------------------------------------------------------------
-- Hospital Profiles: public read, admin write
CREATE POLICY "Public read hospital profiles" ON hospital_profiles FOR SELECT USING (true);
CREATE POLICY "Admin insert hospital profiles" ON hospital_profiles FOR INSERT WITH CHECK (true);
CREATE POLICY "Admin update hospital profiles" ON hospital_profiles FOR UPDATE USING (true);

-- Profiles: users see same hospital, update own
CREATE POLICY "Users see same hospital profiles" ON profiles FOR SELECT USING (hospital_code = get_user_hospital_code());
CREATE POLICY "Users update own profile" ON profiles FOR UPDATE USING (id = auth.uid());
CREATE POLICY "Admin insert profiles" ON profiles FOR INSERT WITH CHECK (hospital_code = get_user_hospital_code());

-- Admissions: same hospital only
CREATE POLICY "Same hospital admissions" ON admissions FOR ALL USING (hospital_code = get_user_hospital_code());

-- Consultants: same hospital only
CREATE POLICY "Same hospital consultants" ON consultants FOR ALL USING (hospital_code = get_user_hospital_code());

-- Departments: same hospital only
CREATE POLICY "Same hospital departments" ON departments FOR ALL USING (hospital_code = get_user_hospital_code());

-- LOS Records: same hospital only
CREATE POLICY "Same hospital los_records" ON los_records FOR ALL USING (hospital_code = get_user_hospital_code());

-- Daily Admissions: same hospital only
CREATE POLICY "Same hospital daily_admissions" ON daily_admissions FOR ALL USING (hospital_code = get_user_hospital_code());

-- Value Stream Stages: same hospital only
CREATE POLICY "Same hospital value_stream_stages" ON value_stream_stages FOR ALL USING (hospital_code = get_user_hospital_code());

-- Patient Stage Progress: same hospital only
CREATE POLICY "Same hospital patient_stage_progress" ON patient_stage_progress FOR ALL USING (hospital_code = get_user_hospital_code());

-- 5S Audits: same hospital only
CREATE POLICY "Same hospital audits_5s" ON audits_5s FOR ALL USING (hospital_code = get_user_hospital_code());

-- Kaizen Ideas: same hospital only
CREATE POLICY "Same hospital kaizen_ideas" ON kaizen_ideas FOR ALL USING (hospital_code = get_user_hospital_code());

-- Patient Feedback: same hospital only
CREATE POLICY "Same hospital patient_feedback" ON patient_feedback FOR ALL USING (hospital_code = get_user_hospital_code());

-- Notifications: same hospital only
CREATE POLICY "Same hospital notifications" ON notifications FOR ALL USING (hospital_code = get_user_hospital_code());

-- ------------------------------------------------------------
-- Auto-update updated_at triggers
-- ------------------------------------------------------------
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_hospital_profiles_updated_at BEFORE UPDATE ON hospital_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_profiles_updated_at BEFORE UPDATE ON profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_admissions_updated_at BEFORE UPDATE ON admissions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_kaizen_ideas_updated_at BEFORE UPDATE ON kaizen_ideas FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
