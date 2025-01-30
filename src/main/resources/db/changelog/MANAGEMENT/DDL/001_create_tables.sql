-- Ensure the UUID extension is enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabela pentru utilizatori
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  keycloak_id uuid UNIQUE NOT NULL, -- ID-ul utilizatorului din Keycloak
  first_name text NOT NULL,
  last_name text NOT NULL,
  email text UNIQUE NOT NULL,
  phone text,
  role text NOT NULL, -- 'ADMIN' sau 'USER'
  stripe_customer_id text UNIQUE, -- ID-ul Stripe al utilizatorului
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Tabela pentru săli
CREATE TABLE gyms (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name text NOT NULL,
  owner_id UUID REFERENCES users(id) NOT NULL, -- Adminul sălii
  subscription_status text DEFAULT 'INACTIVE', -- Starea abonamentului (e.g., active, inactive)
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Tabela pentru membri ai sălilor
CREATE TABLE gym_members (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES users(id) NOT NULL, -- Utilizatorul din tabela `users`
  gym_id UUID REFERENCES gyms(id) NOT NULL, -- Sala la care este membru
  membership_type text NOT NULL, -- Tipul abonamentului membrului
  membership_status text DEFAULT 'ACTIVE' CHECK (membership_status IN ('ACTIVE', 'INACTIVE', 'CANCELED')), -- Starea abonamentului
  start_date date NOT NULL, -- Data începerii abonamentului
  end_date date, -- Data expirării abonamentului
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Tabela pentru plăți ale membrilor
CREATE TABLE member_payments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  gym_id UUID REFERENCES gyms(id) NOT NULL, -- Sala asociată plății
  member_id UUID REFERENCES gym_members(id) NOT NULL, -- Membrul care a făcut plata
  amount numeric NOT NULL, -- Suma plătită
  description text, -- Detalii despre plată
  payment_date date NOT NULL, -- Data plății
  payment_type text NOT NULL, -- Tipul plății (e.g., Card, Stripe)
  status text DEFAULT 'completed', -- Starea plății (e.g., pending, completed)
  stripe_payment_id text, -- ID-ul unic al plății din Stripe
  stripe_customer_id text, -- ID-ul clientului din Stripe
  receipt_url text, -- URL-ul pentru chitanța Stripe
  created_at timestamptz DEFAULT now() -- Timpul creării plății
);

-- Tabela pentru cheltuieli
CREATE TABLE expenses (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  gym_id UUID REFERENCES gyms(id) NOT NULL, -- Sala pentru care s-a efectuat cheltuiala
  category text NOT NULL, -- Categoria cheltuielii (e.g., Rent, Equipment)
  amount numeric NOT NULL, -- Suma cheltuită
  date date NOT NULL, -- Data cheltuielii
  description text, -- Detalii despre cheltuială
  created_at timestamptz DEFAULT now()
);

-- Tabela pentru abonamente
CREATE TABLE subscriptions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  tier text UNIQUE NOT NULL, -- Tipul abonamentului (Basic, Pro, Enterprise)
  max_members int NOT NULL, -- Numărul maxim de membri permis pentru acest abonament
  max_gyms int NOT NULL, -- Numărul maxim de săli permis pentru acest abonament
  price numeric NOT NULL, -- Prețul abonamentului
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Inserează abonamentele
INSERT INTO subscriptions (tier, max_members, max_gyms, price)
VALUES
    ('BASIC', 100, 1, 0.00),
    ('PRO', 500, 5, 29.99),
    ('ENTERPRISE', 1000, 10, 99.99)
ON CONFLICT (tier) DO NOTHING; -- Evită duplicarea în caz de re-rulare

-- Tabela pentru relația dintre utilizatori și abonamente
CREATE TABLE user_subscriptions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES users(id) NOT NULL, -- Utilizatorul abonat
  subscription_id UUID REFERENCES subscriptions(id) NOT NULL, -- Abonamentul asociat
  start_date DATE NOT NULL, -- Data începerii abonamentului
  end_date DATE, -- Data expirării abonamentului
  status TEXT DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'CANCELED')), -- Starea abonamentului
  created_at TIMESTAMP DEFAULT now(), -- Data creării înregistrării
  updated_at TIMESTAMP DEFAULT now() -- Data ultimei actualizări
);

-- Indexuri pentru optimizare
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_subscription_user_id ON user_subscriptions(user_id);
CREATE INDEX idx_user_subscription_status ON user_subscriptions(status);

CREATE INDEX idx_member_payments_gym_id ON member_payments(gym_id);
CREATE INDEX idx_member_payments_member_id ON member_payments(member_id);
CREATE INDEX idx_member_payments_payment_date ON member_payments(payment_date);
