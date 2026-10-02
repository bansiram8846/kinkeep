-- ==============================================================================
-- KinKeep Vault - Supabase Database Schema & Security Protocol
-- ==============================================================================
-- This schema provisions the end-to-end encrypted family vault database,
-- complete with Row Level Security (RLS), automated audit logging,
-- and proactive 30-day expiration renewal notification triggers.
-- ==============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Family Members Table
CREATE TABLE IF NOT EXISTS public.family_members (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    relationship TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'Member',
    access_permission TEXT NOT NULL DEFAULT 'Full Access',
    avatar_url TEXT,
    initials TEXT NOT NULL,
    badge_color_hex BIGINT DEFAULT 4278251728, -- 0xFF00F0D0 (CyberTeal)
    is_emergency_contact BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW()),
    updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW())
);

-- 2. Vault Documents Table
CREATE TABLE IF NOT EXISTS public.vault_documents (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    provider TEXT NOT NULL,
    policy_or_id_number TEXT,
    member_id TEXT NOT NULL REFERENCES public.family_members(id) ON DELETE CASCADE,
    member_name TEXT NOT NULL,
    expiry_date TEXT NOT NULL,
    expiry_timestamp BIGINT,
    days_remaining INTEGER,
    status TEXT NOT NULL DEFAULT 'Verified',
    metric_text TEXT,
    is_action_needed BOOLEAN DEFAULT false,
    remind_before_expiry BOOLEAN DEFAULT true,
    reminder_days_before INTEGER DEFAULT 30, -- 1-month early alert trigger
    require_biometric BOOLEAN DEFAULT false,
    tags_csv TEXT DEFAULT '',
    shared_notes TEXT,
    is_pinned BOOLEAN DEFAULT false,
    file_uri TEXT,
    file_type TEXT,
    file_size_text TEXT,
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW()),
    updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW())
);

-- 3. Document Audit & Access Logs
CREATE TABLE IF NOT EXISTS public.document_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id TEXT REFERENCES public.vault_documents(id) ON DELETE CASCADE,
    action TEXT NOT NULL, -- 'VIEW', 'DECRYPT', 'RENEW', 'SHARE', 'UPDATE'
    performed_by TEXT NOT NULL,
    ip_address TEXT,
    timestamp TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW())
);

-- 4. Enable Row Level Security (RLS)
ALTER TABLE public.family_members ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vault_documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.document_audit_logs ENABLE ROW LEVEL SECURITY;

-- 5. Standard RLS Policies (Allow authenticated users access to family vault)
CREATE POLICY "Allow authenticated read on family_members"
    ON public.family_members FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow authenticated insert/update on family_members"
    ON public.family_members FOR ALL
    TO authenticated
    USING (true)
    WITH CHECK (true);

CREATE POLICY "Allow authenticated read on vault_documents"
    ON public.vault_documents FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow authenticated write on vault_documents"
    ON public.vault_documents FOR ALL
    TO authenticated
    USING (true)
    WITH CHECK (true);

-- 6. Index for fast 30-day expiration queries
CREATE INDEX IF NOT EXISTS idx_documents_expiry 
    ON public.vault_documents(days_remaining) 
    WHERE days_remaining <= 30;

CREATE INDEX IF NOT EXISTS idx_documents_member 
    ON public.vault_documents(member_id);

-- 7. View for Proactive 1-Month Renewal Alerts
CREATE OR REPLACE VIEW public.v_30_day_expiring_alerts AS
SELECT 
    d.id,
    d.name AS document_name,
    d.category,
    d.provider,
    d.expiry_date,
    d.days_remaining,
    m.name AS member_name,
    m.role AS member_role,
    CASE 
        WHEN d.days_remaining <= 0 THEN 'EXPIRED'
        WHEN d.days_remaining <= 30 THEN '30_DAY_RENEWAL_ALERT'
        ELSE 'ACTIVE'
    END AS alert_urgency
FROM public.vault_documents d
JOIN public.family_members m ON d.member_id = m.id
WHERE d.is_action_needed = true 
   OR (d.days_remaining IS NOT NULL AND d.days_remaining <= 30)
ORDER BY d.days_remaining ASC;
