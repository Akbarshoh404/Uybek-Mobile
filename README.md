# 🏠 Uybek — O'zbekiston Ko'chmas Mulk Ilovasi

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" width="120" alt="Uybek Logo"/>
</p>

<p align="center">
  <strong>Jetpack Compose • Firebase • Supabase • MVVM</strong>
</p>

---

## 📌 Loyiha haqida

**Uybek** — O'zbekiston ko'chmas mulk bozori uchun zamonaviy Android ilovasi. Foydalanuvchilar kvartiralar, uylar, tijorat va yer maydonlarini sotish yoki ijaraga berish uchun e'lon joylashtirishlari va real vaqt rejimida sotuvchilar bilan muloqot qilishlari mumkin.

---

## ✨ Asosiy imkoniyatlar

| Funksiya | Tavsif |
|----------|--------|
| 🔐 **Autentifikatsiya** | Email/parol, telefon OTP, Google Sign-In (Firebase Auth) |
| 🏘️ **E'lonlar** | Ko'chmas mulk e'lonlarini ko'rish, qidirish va filtrlash |
| ➕ **E'lon berish** | 5 bosqichli shakl: muomala → tafsilotlar → joylashuv → narx/aloqa → ko'rib chiqish |
| 💬 **Chat** | Firebase Realtime Database orqali foydalanuvchilar o'rtasida real vaqt chat |
| 👤 **Sotuvchi profili** | Sotuvchining barcha e'lonlari va aloqa ma'lumotlari |
| ❤️ **Sevimlilar** | E'lonlarni saqlash va boshqarish |
| 🌙 **Dark/Light rejim** | Tizim rejimiga moslashuv + qo'lda almashtirish |
| ⚙️ **Sozlamalar** | Ism, telefon o'zgartirish; akkauntni o'chirish |
| 📖 **FAQ** | Ko'p so'raladigan savollar (akkordeon UI) |
| 🔒 **Maxfiylik** | Maxfiylik siyosati ekrani |
| 📱 **Telegram** | Sotuvchi bilan Telegram orqali bog'lanish |

---

## 🏗️ Arxitektura

```
uz.angrykitten.uybek/
├── data/
│   ├── model/          # Property, City, District, AppData
│   └── repository/     # PropertyRepository, UserRepository, AuthRepository, SupabaseRepository
├── ui/
│   ├── components/     # Reusable Compose components
│   ├── navigation/     # AppNavGraph, Screen, BottomNavItem
│   ├── screens/        # All screen composables
│   ├── theme/          # Color, Typography, Theme (dark/light)
│   └── viewmodel/      # AppViewModel (MVVM)
└── MainActivity.kt     # Entry point, theme state management
```

### Navigatsiya oqimi
```
Splash → Home (default)
       ↘ Login / Register
Home → PropertyDetail → SellerProfile
                      → ChatDetail
Bottom Nav: Home | Chat | Post | Saved | Profile
Profile → Settings → (edit name, phone, delete account)
        → FAQ
        → Privacy Policy
```

---

## 🔧 O'rnatish

### 1. Talablar
- Android Studio Hedgehog+
- JDK 11+
- Firebase loyihasi (Authentication + Realtime Database)
- Supabase loyihasi (PostgreSQL)

### 2. Klonlash
```bash
git clone https://github.com/yourusername/Uybek.git
cd Uybek
```

### 3. Firebase sozlash
1. [Firebase Console](https://console.firebase.google.com) → yangi loyiha yarating
2. Android ilovasi qo'shing: `uz.angrykitten.uybek`
3. `google-services.json` ni `app/` papkasiga nusxalang
4. Authentication → Email/Password, Phone, Google → yoqing
5. **Realtime Database** → yarating, qoidalar:
```json
{
  "rules": {
    "chats": {
      "$chatId": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    }
  }
}
```

### 4. Supabase sozlash
1. [supabase.com](https://supabase.com) → yangi loyiha
2. `local.properties` ga qo'shing:
```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_KEY=your-anon-key
GOOGLE_WEB_CLIENT_ID=your-web-client-id.apps.googleusercontent.com
```
3. SQL schema (Supabase SQL Editor):
```sql
create table properties (
  id uuid primary key,
  user_id text not null,
  title text not null,
  description text,
  deal_type text not null,         -- 'sale' | 'rent'
  property_type text not null,     -- 'apartment' | 'house' | 'commercial' | 'land'
  city_id int, city_name text,
  district_id int, district_name text,
  address text,
  latitude float8, longitude float8,
  price float8 not null,
  currency text default 'USD',
  price_period text,               -- 'month' | 'year' | null
  area_m2 float8,
  bedrooms int, bathrooms int,
  floor int, total_floors int,
  year_built int,
  is_new_build boolean default false,
  is_active boolean default true,
  views_count int default 0,
  images text[] default '{}',
  seller_name text,
  seller_phone text,
  seller_whatsapp text,            -- used for Telegram username
  seller_avatar text,
  created_at timestamptz default now()
);

-- Enable RLS
alter table properties enable row level security;
create policy "Public read" on properties for select using (true);
create policy "Auth insert" on properties for insert with check (auth.role() = 'authenticated');
create policy "Owner update" on properties for update using (auth.uid()::text = user_id);
```

### 5. Ishga tushirish
```bash
./gradlew assembleDebug
# yoki Android Studio → Run
```

---

## 📦 Asosiy bog'liqliklar

| Kutubxona | Versiya | Maqsad |
|-----------|---------|--------|
| Jetpack Compose BOM | 2024.09.00 | UI framework |
| Navigation Compose | 2.9.0 | Ekranlar orasida navigatsiya |
| Firebase Auth | 24.0.1 | Autentifikatsiya |
| Firebase Database | 21.0.0 | Real vaqt chat |
| Supabase BOM | 3.0.3 | Ko'chmas mulk ma'lumotlari |
| Ktor Client | 3.0.3 | HTTP (Kotlin 2.0.21 mos) |
| Coil Compose | 2.6.0 | Rasm yuklash |
| DataStore | 1.1.4 | Lokal foydalanuvchi ma'lumotlari |
| Material Icons Extended | — | Ikonkalar |

---

## 💬 Chat arxitekturasi

Chat funksiyasi **Firebase Realtime Database** orqali ishlaydi:

```
/chats
  /{userId1}_{userId2}          ← sorted, deterministic chat ID
    /participants
      /{userId1}/name: "Ali"
      /{userId2}/name: "Bobur"
    /messages
      /{msgId}
        senderId: "uid1"
        senderName: "Ali"
        text: "Salom!"
        timestamp: 1714900000000
```

Chat ID = ikki user ID ni saralangan holda `_` bilan birlashtirish:
```kotlin
val chatId = listOf(myId, otherId).sorted().joinToString("_")
```

---

## 🎨 Dizayn tizimi

| Token | Qiymat |
|-------|--------|
| Brand (asosiy rang) | `#7B61FF` |
| Gradient Start | `#7B61FF` |
| Gradient End | `#5E3FC8` |
| Dark background | `#0A0A0F` |
| Light background | System default |
| Typography | Material 3 defaults |

Dark/Light rejim `isSystemInDarkTheme()` orqali tizim sozlamasiga moslashadi. Foydalanuvchi profil yoki sozlamalar ekranidan qo'lda almashtirishiga ham imkon beradi.

---

## 📂 Screens

| Ekran | Fayl | Tavsif |
|-------|------|--------|
| Splash | `SplashScreen.kt` | Kirish animatsiyasi |
| Home | `HomeScreen.kt` | E'lonlar lenti + filtrlar |
| Property Detail | `PropertyDetailScreen.kt` | E'lon tafsilotlari |
| Seller Profile | `SellerProfileScreen.kt` | Sotuvchi profili va e'lonlari |
| Chat List | `ChatScreens.kt` | Barcha suhbatlar |
| Chat Detail | `ChatScreens.kt` | Real vaqt xabarlar |
| Post Listing | `PostListingScreen.kt` | 5 bosqichli e'lon berish |
| Saved | `SavedScreen.kt` | Sevimli e'lonlar |
| Profile | `ProfileScreen.kt` | Foydalanuvchi profili |
| Settings | `SettingsScreen.kt` | Sozlamalar |
| Login/Register | `AuthScreens.kt` | Firebase autentifikatsiya |
| FAQ | `InfoScreens.kt` | Ko'p so'raladigan savollar |
| Privacy Policy | `InfoScreens.kt` | Maxfiylik siyosati |

---

## 🪪 Litsenziya

MIT License — bepul foydalanishingiz mumkin.

---

<p align="center">Made with ❤️ in Uzbekistan</p>
