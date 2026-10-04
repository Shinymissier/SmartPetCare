-- ============================================================================
-- Smart Pet Care and Vaccination Tracker - Demo Seed Data
-- ============================================================================

-- 1. Demo User: admin@petcare.com (Password: admin123, BCrypt hash: $2a$10$wE9O7Y4V7xV9v5O8V4/eE.n3u5XG5GjGZJkYpZl2hF3D3C6R3K6C)
-- We will use a standard demo user ID
INSERT INTO profiles (id, email, password_hash, full_name, phone, avatar_url)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'demo@petcare.com',
    '$2a$10$r9x56o9qF5o3b2fP0l4aQ.2gP3s7Q/sW2h.3u/L5m9a8G4z6q1xae', -- hash for "admin123"
    'Sarah Jenkins',
    '+1 (555) 234-5678',
    'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&q=80'
) ON CONFLICT (id) DO NOTHING;

-- 2. Demo Pets
INSERT INTO pets (id, user_id, name, species, breed, date_of_birth, gender, weight, color, allergies, health_conditions, notes, photo_url)
VALUES 
(
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'Bruno',
    'Dog',
    'Golden Retriever',
    '2023-04-15',
    'Male',
    28.50,
    'Golden Yellow',
    'Chicken protein',
    'None',
    'Friendly, active, loves swimming and playing fetch in the park.',
    'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=400&q=80'
),
(
    'b0000000-0000-0000-0000-000000000002',
    'a0000000-0000-0000-0000-000000000001',
    'Luna',
    'Cat',
    'Siamese',
    '2022-08-20',
    'Female',
    4.20,
    'Seal Point',
    'None known',
    'Mild sensitive stomach',
    'Very affectionate, vocal, prefers quiet indoor environments.',
    'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=400&q=80'
),
(
    'b0000000-0000-0000-0000-000000000003',
    'a0000000-0000-0000-0000-000000000001',
    'Milo',
    'Dog',
    'Beagle',
    '2024-01-10',
    'Male',
    11.20,
    'Tricolor',
    'Grass pollen seasonal allergy',
    'Vaccination booster pending',
    'Curious puppy, energetic scent tracker.',
    'https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?auto=format&fit=crop&w=400&q=80'
) ON CONFLICT (id) DO NOTHING;

-- 3. Demo Vaccinations
INSERT INTO vaccinations (id, pet_id, user_id, vaccine_name, vaccination_date, next_due_date, vet_name, clinic_name, notes, status)
VALUES
(
    'c0000000-0000-0000-0000-000000000001',
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'Rabies (3-Year)',
    '2024-05-10',
    '2027-05-10',
    'Dr. Alex Morales',
    'Valley Animal Hospital',
    'Administered right hind leg. No immediate adverse reaction.',
    'Completed'
),
(
    'c0000000-0000-0000-0000-000000000002',
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'DHPP Core Booster',
    '2025-10-15',
    '2026-10-15',
    'Dr. Alex Morales',
    'Valley Animal Hospital',
    'Annual booster protection.',
    'Upcoming'
),
(
    'c0000000-0000-0000-0000-000000000003',
    'b0000000-0000-0000-0000-000000000002',
    'a0000000-0000-0000-0000-000000000001',
    'FVRCP Feline Core',
    '2025-02-14',
    '2026-02-14',
    'Dr. Emily Chen',
    'Paws & Whiskers Clinic',
    'Due for annual booster.',
    'Overdue'
),
(
    'c0000000-0000-0000-0000-000000000004',
    'b0000000-0000-0000-0000-000000000003',
    'a0000000-0000-0000-0000-000000000001',
    'Bordetella (Kennel Cough)',
    '2026-03-01',
    '2026-11-01',
    'Dr. Alex Morales',
    'Valley Animal Hospital',
    'Oral dose prior to boarding.',
    'Upcoming'
) ON CONFLICT (id) DO NOTHING;

-- 4. Demo Medical Records
INSERT INTO medical_records (id, pet_id, user_id, visit_date, diagnosis, treatment, medication, vet_name, clinic_name, cost, follow_up_date, notes)
VALUES
(
    'd0000000-0000-0000-0000-000000000001',
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    '2026-01-20',
    'Minor ear infection (Otitis Externa)',
    'Ear flush and topical antibacterial drops',
    'Otomax ointment 2x daily for 7 days',
    'Dr. Alex Morales',
    'Valley Animal Hospital',
    85.00,
    '2026-02-05',
    'Infection fully cleared upon follow-up examination.'
),
(
    'd0000000-0000-0000-0000-000000000002',
    'b0000000-0000-0000-0000-000000000002',
    'a0000000-0000-0000-0000-000000000001',
    '2026-02-15',
    'Routine dental cleaning and checkup',
    'Ultrasonic scaling and polish under mild sedation',
    'None',
    'Dr. Emily Chen',
    'Paws & Whiskers Clinic',
    140.00,
    NULL,
    'Teeth in good condition, mild tartar removed.'
) ON CONFLICT (id) DO NOTHING;

-- 5. Demo Feeding Schedules
INSERT INTO feeding_schedules (id, pet_id, user_id, food_type, quantity, feeding_time, frequency, status, notes)
VALUES
(
    'e0000000-0000-0000-0000-000000000001',
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'Salmon & Sweet Potato Kibble',
    '1.5 cups',
    '08:00 AM',
    'Twice Daily',
    'Active',
    'Avoid chicken-based dry food due to allergy.'
),
(
    'e0000000-0000-0000-0000-000000000002',
    'b0000000-0000-0000-0000-000000000002',
    'a0000000-0000-0000-0000-000000000001',
    'Royal Canin Siamese Wet Food',
    '1 pouch (85g)',
    '07:30 AM',
    'Daily',
    'Active',
    'Serve at room temperature.'
) ON CONFLICT (id) DO NOTHING;

-- 6. Demo Appointments
INSERT INTO appointments (id, pet_id, user_id, title, appointment_type, appointment_date, provider_name, location, status, notes)
VALUES
(
    'f0000000-0000-0000-0000-000000000001',
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'Full Bath & De-shedding Grooming',
    'Grooming',
    '2026-10-10 10:00:00+00',
    'Happy Tails Pet Spa',
    '124 Elm Street, Suite 4',
    'Scheduled',
    'Full coat brushing, nail trim, and ear cleaning requested.'
),
(
    'f0000000-0000-0000-0000-000000000002',
    'b0000000-0000-0000-0000-000000000002',
    'a0000000-0000-0000-0000-000000000001',
    'Overdue Vaccination Catchup & Wellness Exam',
    'Veterinary',
    '2026-10-18 14:30:00+00',
    'Dr. Emily Chen',
    'Paws & Whiskers Clinic',
    'Scheduled',
    'FVRCP booster vaccination.'
) ON CONFLICT (id) DO NOTHING;

-- 7. Demo Reminders
INSERT INTO reminders (id, user_id, pet_id, title, reminder_date, reminder_type, is_completed)
VALUES
(
    '90000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'b0000000-0000-0000-0000-000000000001',
    'Monthly Heartworm & Tick Chewable',
    '2026-10-05',
    'Medication',
    FALSE
),
(
    '90000000-0000-0000-0000-000000000002',
    'a0000000-0000-0000-0000-000000000001',
    'b0000000-0000-0000-0000-000000000002',
    'Schedule FVRCP booster vaccination at clinic',
    '2026-10-01',
    'Vaccination',
    FALSE
) ON CONFLICT (id) DO NOTHING;
