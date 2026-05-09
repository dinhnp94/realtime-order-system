-- Seed 5 products on startup
-- ON CONFLICT DO NOTHING = safe to run multiple times, won't create duplicates

INSERT INTO products (id, name, price, stock, description, category) VALUES
  ('prod-001', 'MacBook Pro 14"',       1999.99, 50,  'Apple M3 chip, 16GB RAM',          'Electronics'),
  ('prod-002', 'Sony WH-1000XM5',        349.99, 100, 'Industry-leading noise cancelling', 'Electronics'),
  ('prod-003', 'Mechanical Keyboard',    129.99, 200, 'Cherry MX switches, TKL layout',   'Accessories'),
  ('prod-004', 'USB-C Hub 7-in-1',        49.99, 300, '4K HDMI, 100W PD, SD card reader', 'Accessories'),
  ('prod-005', '27" 4K IPS Monitor',     599.99, 75,  '144Hz, USB-C, HDR400',             'Electronics')
ON CONFLICT (id) DO NOTHING;
