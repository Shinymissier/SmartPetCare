-- ============================================================================
-- Smart Pet Care and Vaccination Tracker
-- Supabase PostgreSQL Schema with Row Level Security (RLS)
-- ============================================================================

-- Enable pgcrypto / uuid extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- 1. Profiles Table (Users)
-- ============================================================================
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(50),
    avatar_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 2. Pets Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS pets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(20) DEFAULT 'Unknown',
    weight NUMERIC(6, 2) DEFAULT 0.00,
    color VARCHAR(60),
    allergies TEXT,
    health_conditions TEXT,
    notes TEXT,
    photo_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 3. Vaccinations Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS vaccinations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pet_id UUID NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    vaccine_name VARCHAR(120) NOT NULL,
    vaccination_date DATE NOT NULL,
    next_due_date DATE,
    vet_name VARCHAR(120),
    clinic_name VARCHAR(150),
    notes TEXT,
    status VARCHAR(30) DEFAULT 'Completed', -- 'Completed', 'Upcoming', 'Overdue'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 4. Medical Records Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS medical_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pet_id UUID NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    visit_date DATE NOT NULL,
    diagnosis TEXT NOT NULL,
    treatment TEXT,
    medication TEXT,
    vet_name VARCHAR(120),
    clinic_name VARCHAR(150),
    cost NUMERIC(10, 2) DEFAULT 0.00,
    follow_up_date DATE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 5. Feeding Schedules Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS feeding_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pet_id UUID NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    food_type VARCHAR(100) NOT NULL,
    quantity VARCHAR(60) NOT NULL,
    feeding_time VARCHAR(20) NOT NULL, -- e.g. "08:00 AM" or "08:00"
    frequency VARCHAR(50) DEFAULT 'Daily', -- 'Daily', 'Twice Daily', 'Weekly'
    status VARCHAR(30) DEFAULT 'Active',   -- 'Active', 'Paused', 'Completed'
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 6. Feeding Logs Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS feeding_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pet_id UUID NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    schedule_id UUID REFERENCES feeding_schedules(id) ON DELETE SET NULL,
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    fed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) DEFAULT 'Fed', -- 'Fed', 'Skipped', 'Delayed'
    notes TEXT
);

-- ============================================================================
-- 7. Appointments Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pet_id UUID NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    appointment_type VARCHAR(50) NOT NULL, -- 'Veterinary', 'Grooming', 'Vaccination', 'Checkup'
    appointment_date TIMESTAMP WITH TIME ZONE NOT NULL,
    provider_name VARCHAR(150),
    location TEXT,
    status VARCHAR(30) DEFAULT 'Scheduled', -- 'Scheduled', 'Completed', 'Cancelled'
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 8. Reminders Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS reminders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    pet_id UUID REFERENCES pets(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    reminder_date DATE NOT NULL,
    reminder_type VARCHAR(50) DEFAULT 'General', -- 'Vaccination', 'Medication', 'Appointment', 'Deworming'
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Indexes for Performance
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_pets_user_id ON pets(user_id);
CREATE INDEX IF NOT EXISTS idx_vaccinations_pet_id ON vaccinations(pet_id);
CREATE INDEX IF NOT EXISTS idx_vaccinations_user_id ON vaccinations(user_id);
CREATE INDEX IF NOT EXISTS idx_vaccinations_due_date ON vaccinations(next_due_date);
CREATE INDEX IF NOT EXISTS idx_medical_pet_id ON medical_records(pet_id);
CREATE INDEX IF NOT EXISTS idx_medical_user_id ON medical_records(user_id);
CREATE INDEX IF NOT EXISTS idx_feeding_pet_id ON feeding_schedules(pet_id);
CREATE INDEX IF NOT EXISTS idx_appointments_user_date ON appointments(user_id, appointment_date);
CREATE INDEX IF NOT EXISTS idx_reminders_user_date ON reminders(user_id, reminder_date);

-- ============================================================================
-- Row Level Security (RLS) Policies (Supabase)
-- ============================================================================
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE pets ENABLE ROW LEVEL SECURITY;
ALTER TABLE vaccinations ENABLE ROW LEVEL SECURITY;
ALTER TABLE medical_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE feeding_schedules ENABLE ROW LEVEL SECURITY;
ALTER TABLE feeding_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE appointments ENABLE ROW LEVEL SECURITY;
ALTER TABLE reminders ENABLE ROW LEVEL SECURITY;

-- Profiles: users can only see and update their own profile
CREATE POLICY profiles_owner_policy ON profiles
    FOR ALL
    USING (auth.uid() = id)
    WITH CHECK (auth.uid() = id);

-- Pets: users can only see, insert, update, delete their own pets
CREATE POLICY pets_owner_policy ON pets
    FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Vaccinations
CREATE POLICY vaccinations_owner_policy ON vaccinations
    FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Medical Records
CREATE POLICY medical_records_owner_policy ON medical_records
    FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Feeding Schedules
CREATE POLICY feeding_schedules_owner_policy ON feeding_schedules
    FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Feeding Logs
CREATE POLICY feeding_logs_owner_policy ON feeding_logs
    FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Appointments
CREATE POLICY appointments_owner_policy ON appointments
    FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Reminders
CREATE POLICY reminders_owner_policy ON reminders
    FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Trigger to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_timestamp_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_pets_timestamp BEFORE UPDATE ON pets FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();
CREATE TRIGGER update_vaccinations_timestamp BEFORE UPDATE ON vaccinations FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();
CREATE TRIGGER update_medical_timestamp BEFORE UPDATE ON medical_records FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();
CREATE TRIGGER update_feeding_timestamp BEFORE UPDATE ON feeding_schedules FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();
CREATE TRIGGER update_appointments_timestamp BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();
