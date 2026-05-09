-- Generated from app/src/main/assets/sample_data.json
-- Run this in Supabase SQL Editor

create table if not exists public.cities (
  id integer primary key,
  name text not null,
  slug text not null unique
);

create table if not exists public.districts (
  id integer primary key,
  city_id integer not null references public.cities(id) on delete cascade,
  name text not null,
  slug text not null unique
);

create table if not exists public.properties (
  id text primary key,
  user_id text not null,
  title text not null,
  description text not null,
  deal_type text not null,
  property_type text not null,
  city_id integer not null references public.cities(id),
  city_name text not null,
  district_id integer not null references public.districts(id),
  district_name text not null,
  address text not null,
  latitude double precision not null,
  longitude double precision not null,
  price double precision not null,
  currency text not null,
  price_period text null,
  area_m2 double precision not null,
  bedrooms integer not null,
  bathrooms integer not null,
  floor integer not null,
  total_floors integer not null,
  year_built integer not null,
  is_new_build boolean not null default false,
  is_active boolean not null default true,
  views_count integer not null default 0,
  images jsonb not null default '[]'::jsonb,
  seller_name text not null,
  seller_phone text not null,
  seller_whatsapp text not null,
  seller_avatar text not null
);

-- Demo policies for direct mobile access with the publishable key.
-- Tighten these later or move writes behind your own backend if needed.
alter table public.cities enable row level security;
alter table public.districts enable row level security;
alter table public.properties enable row level security;

drop policy if exists "Public read cities" on public.cities;
create policy "Public read cities" on public.cities for select using (true);
drop policy if exists "Public read districts" on public.districts;
create policy "Public read districts" on public.districts for select using (true);
drop policy if exists "Public read properties" on public.properties;
create policy "Public read properties" on public.properties for select using (true);
drop policy if exists "Public write properties" on public.properties;
create policy "Public write properties" on public.properties for all using (true) with check (true);

insert into public.cities (id, name, slug) values
  (1, 'Toshkent', 'tashkent'),
  (2, 'Samarqand', 'samarkand'),
  (3, 'Buxoro', 'bukhara'),
  (4, 'Namangan', 'namangan'),
  (5, 'Andijon', 'andijan'),
  (6, 'Farg''ona', 'fergana'),
  (7, 'Nukus', 'nukus'),
  (8, 'Qarshi', 'karshi')
on conflict (id) do update set name = excluded.name, slug = excluded.slug;

insert into public.districts (id, city_id, name, slug) values
  (1, 1, 'Yunusobod', 'yunusobod'),
  (2, 1, 'Mirzo Ulug''bek', 'mirzo-ulugbek'),
  (3, 1, 'Chilonzor', 'chilonzor'),
  (4, 1, 'Shayxontohur', 'shayxontohur'),
  (5, 1, 'Uchtepa', 'uchtepa'),
  (6, 1, 'Yakkasaroy', 'yakkasaroy'),
  (7, 1, 'Olmazor', 'olmazor'),
  (8, 1, 'Bektemir', 'bektemir'),
  (9, 2, 'Markaziy', 'samarkand-center'),
  (10, 2, 'Registon', 'registon'),
  (11, 3, 'Markaziy', 'bukhara-center'),
  (12, 4, 'Markaziy', 'namangan-center')
on conflict (id) do update set city_id = excluded.city_id, name = excluded.name, slug = excluded.slug;

insert into public.properties (id, user_id, title, description, deal_type, property_type, city_id, city_name, district_id, district_name, address, latitude, longitude, price, currency, price_period, area_m2, bedrooms, bathrooms, floor, total_floors, year_built, is_new_build, is_active, views_count, images, seller_name, seller_phone, seller_whatsapp, seller_avatar) values
  ('p001', 'u001', 'Zamonaviy 3 xonali kvartira Yunusobodda', 'Yunusobod tumanida, 19-mavzeda joylashgan chiroyli 3 xonali kvartira. To''liq ta''mirlangan, yangi mebel va texnika bilan. Uyda konditsioner, internet va barcha kommunal xizmatlar mavjud. Atrofida bog'', maktab va savdo markazlari bor.', 'rent', 'apartment', 1, 'Toshkent', 1, 'Yunusobod', '19-mavze, 45-uy', 41.3392, 69.3561, 800, 'USD', 'month', 95.0, 3, 2, 7, 14, 2019, false, true, 142, '["https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800", "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800", "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800"]'::jsonb, 'Bobur Toshmatov', '+998901234567', '+998901234567', 'https://i.pravatar.cc/150?img=11'),
  ('p002', 'u002', 'Sotiladi: Chilonzorda 2 xonali kvartira', 'Chilonzor tumanida qulay joylashgan 2 xonali kvartira. Metro bekatiga 5 daqiqa yurish. Yangi ta''mirlangan, parket pollari, plastik derazalar. Podval va yerosti garaji bor.', 'sale', 'apartment', 1, 'Toshkent', 3, 'Chilonzor', 'Yangi Sergeli ko''chasi, 12', 41.2765, 69.2104, 75000, 'USD', null, 62.0, 2, 1, 3, 9, 2005, false, true, 89, '["https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800", "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800", "https://images.unsplash.com/photo-1556912173-3bb406ef726f?w=800"]'::jsonb, 'Dilnoza Xasanova', '+998931112233', '+998931112233', 'https://i.pravatar.cc/150?img=5'),
  ('p003', 'u003', 'Mirzo Ulug''bekda yangi 4 xonali uy', 'Mirzo Ulug''bek tumanida qurilgan zamonaviy 4 xonali uy. 2023-yilda qurilgan, premium ta''mirlangan. Katta hovli, garaj va yashil bog'' mavjud. Xavfsiz mahalla, qorovulxona bor.', 'sale', 'house', 1, 'Toshkent', 2, 'Mirzo Ulug''bek', 'Akademgorodok ko''chasi, 7', 41.351, 69.2856, 280000, 'USD', null, 220.0, 4, 3, 1, 2, 2023, true, true, 310, '["https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800", "https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800", "https://images.unsplash.com/photo-1565182999561-18d7dc61c393?w=800"]'::jsonb, 'Jasur Rahimov', '+998971234567', '+998971234567', 'https://i.pravatar.cc/150?img=15'),
  ('p004', 'u004', 'Yakkasaroy tumanida ofis ijarasi', 'Yakkasaroy tumanida, Amir Temur shohko''chasida zamonaviy ofis. Ochiq makonda ishlash uchun ideal. Konditsioner, internet, lift va avtoturargoh bor.', 'rent', 'commercial', 1, 'Toshkent', 6, 'Yakkasaroy', 'Amir Temur shoh ko''chasi, 108', 41.3045, 69.2715, 2500, 'USD', 'month', 150.0, 0, 2, 4, 12, 2018, false, true, 55, '["https://images.unsplash.com/photo-1497366216548-37526070297c?w=800", "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=800"]'::jsonb, 'Kamola Ergasheva', '+998911234567', '+998911234567', 'https://i.pravatar.cc/150?img=20'),
  ('p005', 'u005', 'Samarqandda 3 xonali zamonaviy kvartira', 'Samarqand shahrining markazida, Registon yaqinida ajoyib 3 xonali kvartira. To''liq jihozlangan, panoramik oynalar. Shahar manzarasi ko''rinadi.', 'rent', 'apartment', 2, 'Samarqand', 9, 'Markaziy', 'Registon ko''chasi, 23', 39.654, 66.9597, 600, 'USD', 'month', 88.0, 3, 2, 5, 10, 2020, false, true, 201, '["https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800", "https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=800"]'::jsonb, 'Sherzod Mamatov', '+998901112233', '+998901112233', 'https://i.pravatar.cc/150?img=33'),
  ('p006', 'u006', 'Olmazor tumanida 1 xonali kvartira ijarasi', 'Olmazor tumanida qulay 1 xonali kvartira. Metro va avtobuslarga yaqin. Barcha kommunal xizmatlar ulangan.', 'rent', 'apartment', 1, 'Toshkent', 7, 'Olmazor', 'Bog''ishamol ko''chasi, 14', 41.322, 69.231, 450, 'USD', 'month', 42.0, 1, 1, 2, 5, 2000, false, true, 76, '["https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800", "https://images.unsplash.com/photo-1556020685-ae41abfc9365?w=800"]'::jsonb, 'Malika Yusupova', '+998907654321', '+998907654321', 'https://i.pravatar.cc/150?img=7'),
  ('p007', 'u007', 'Buxoroda arzon yer uchastkasi sotiladi', 'Buxoro shahrida 6 sotix yer uchastkasi. Qurilish uchun ruxsatnomalari tayyor. Gaz, elektr va suv ulangan.', 'sale', 'land', 3, 'Buxoro', 11, 'Markaziy', 'Bahouddin Naqshband ko''chasi', 39.7747, 64.4286, 25000, 'USD', null, 600.0, 0, 0, 0, 0, 0, false, true, 44, '["https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800"]'::jsonb, 'Otabek Qodirov', '+998944321234', '+998944321234', 'https://i.pravatar.cc/150?img=25'),
  ('p008', 'u008', 'Chilonzorda yangi 5 xonali uy sotiladi', 'Chilonzor tumanida 2024-yilda qurilgan premium 5 xonali uy. Uchta qavatli, katta hovlisi va suzish havzasi mavjud. Premium darajada jihozlangan.', 'sale', 'house', 1, 'Toshkent', 3, 'Chilonzor', 'Navruz ko''chasi, 3', 41.2845, 69.2055, 450000, 'USD', null, 380.0, 5, 4, 1, 3, 2024, true, true, 428, '["https://images.unsplash.com/photo-1613977257363-707ba9348227?w=800", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800", "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800"]'::jsonb, 'Nodir Alimov', '+998998765432', '+998998765432', 'https://i.pravatar.cc/150?img=18')
on conflict (id) do update set user_id = excluded.user_id, title = excluded.title, description = excluded.description, deal_type = excluded.deal_type, property_type = excluded.property_type, city_id = excluded.city_id, city_name = excluded.city_name, district_id = excluded.district_id, district_name = excluded.district_name, address = excluded.address, latitude = excluded.latitude, longitude = excluded.longitude, price = excluded.price, currency = excluded.currency, price_period = excluded.price_period, area_m2 = excluded.area_m2, bedrooms = excluded.bedrooms, bathrooms = excluded.bathrooms, floor = excluded.floor, total_floors = excluded.total_floors, year_built = excluded.year_built, is_new_build = excluded.is_new_build, is_active = excluded.is_active, views_count = excluded.views_count, images = excluded.images, seller_name = excluded.seller_name, seller_phone = excluded.seller_phone, seller_whatsapp = excluded.seller_whatsapp, seller_avatar = excluded.seller_avatar;

-- STORAGE BUCKET SETUP
insert into storage.buckets (id, name, public) 
values ('property_images', 'property_images', true)
on conflict (id) do nothing;

drop policy if exists "Public Access" on storage.objects;
create policy "Public Access" on storage.objects for select using ( bucket_id = 'property_images' );

drop policy if exists "Any user can upload" on storage.objects;
create policy "Any user can upload" on storage.objects for insert with check ( bucket_id = 'property_images' );
