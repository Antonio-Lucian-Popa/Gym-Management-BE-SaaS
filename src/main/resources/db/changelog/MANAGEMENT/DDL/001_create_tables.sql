-- Ensure the UUID extension is enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  keycloak_id uuid UNIQUE NOT NULL, -- ID-ul utilizatorului din Keycloak
  first_name text NOT NULL,
  last_name text NOT NULL,
  email text UNIQUE NOT NULL,
  phone text,
  role text NOT NULL, -- 'ADMIN' sau 'USER'
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE TABLE gyms (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name text NOT NULL,
  owner_id UUID REFERENCES users(id) NOT NULL, -- Adminul sălii
  stripe_customer_id text UNIQUE, -- ID-ul Stripe al sălii
  subscription_status text DEFAULT 'inactive', -- Starea abonamentului (e.g., active, inactive)
  subscription_tier text, -- Tipul abonamentului (Basic, Pro, Enterprise)
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE TABLE gym_members (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES users(id) NOT NULL, -- Utilizatorul din tabela `users`
  gym_id UUID REFERENCES gyms(id) NOT NULL, -- Sala la care este membru
  membership_type text NOT NULL, -- Tipul abonamentului membrului
  membership_status text DEFAULT 'active' CHECK (membership_status IN ('active', 'inactive', 'canceled')), -- Starea abonamentului
  start_date date NOT NULL, -- Data începerii abonamentului
  end_date date, -- Data expirării abonamentului
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE TABLE member_payments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  gym_id UUID REFERENCES gyms(id) NOT NULL, -- Sala asociată plății
  member_id UUID REFERENCES gym_members(id) NOT NULL, -- Membrul care a făcut plata
  amount numeric NOT NULL, -- Suma plătită
  payment_date date NOT NULL, -- Data plății
  payment_type text NOT NULL, -- Tipul plății (e.g., Card, Stripe)
  status text DEFAULT 'completed', -- Starea plății (e.g., pending, completed)
  stripe_payment_id text, -- ID-ul unic al plății din Stripe
  stripe_customer_id text, -- ID-ul clientului din Stripe
  receipt_url text, -- URL-ul pentru chitanța Stripe
  created_at timestamptz DEFAULT now() -- Timpul creării plății
);


CREATE TABLE expenses (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  gym_id UUID REFERENCES gyms(id) NOT NULL, -- Sala pentru care s-a efectuat cheltuiala
  category text NOT NULL, -- Categoria cheltuielii (e.g., Rent, Equipment)
  amount numeric NOT NULL, -- Suma cheltuită
  date date NOT NULL, -- Data cheltuielii
  description text, -- Detalii despre cheltuială
  created_at timestamptz DEFAULT now()
);

CREATE TABLE subscriptions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  tier text UNIQUE NOT NULL, -- Tipul abonamentului (Basic, Pro, Enterprise)
  max_members int NOT NULL, -- Numărul maxim de membri permis pentru acest abonament
  max_gyms int NOT NULL, -- Numărul maxim de săli permis pentru acest abonament
  price numeric NOT NULL, -- Prețul abonamentului
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE TABLE gym_subscriptions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  gym_id UUID REFERENCES gyms(id) NOT NULL, -- Sala abonată
  subscription_id UUID REFERENCES subscriptions(id) NOT NULL, -- Abonamentul asociat
  start_date date NOT NULL, -- Data începerii abonamentului
  end_date date, -- Data expirării abonamentului
  status text DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'canceled')), -- Starea abonamentului
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Indexuri pentru optimizare
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_gym_owner ON gyms(owner_id);
