# Uybek

Uybek is an Android real-estate app for the Uzbekistan market, built with Jetpack Compose, Firebase, and Supabase. The app already supports browsing listings, search, favorites, seller profiles, posting, chat, authentication, settings, FAQ, and privacy screens.

## Current stack

- Kotlin + Jetpack Compose + Material 3
- Firebase Auth for email, phone OTP, and Google sign-in
- Firebase Realtime Database for chat
- Supabase for property and user data
- MVVM with a single `AppViewModel`

## Main screens

- Home feed with filters and listing cards
- Search with filter bottom sheet
- Property detail
- Seller profile
- Chat list and chat detail
- Post listing flow
- Saved listings
- Profile and settings
- Login and register
- FAQ and privacy policy

## UI direction in this version

This version uses a rounded-card visual system across the app:

- Consistent border radius on cards, fields, buttons, dialogs, and hero sections
- Unified listing cards across home, saved, search, and seller profile
- Cleaner dark-theme color handling for prices and important accents
- Seller profile listings now use the same presentation style as the main feed
- Home no longer exposes the cramped 3-column card layout
- Property detail no longer shows the share icon

## Project structure

```text
app/src/main/java/uz/angrykitten/uybek/
  MainActivity.kt
  data/
    model/
    repository/
  ui/
    components/
    navigation/
    screens/
    theme/
    viewmodel/
```

## Running the app

### Requirements

- Android Studio
- JDK 11+
- Firebase project with Auth and Realtime Database
- Supabase project with the required tables

### Local setup

Add values to `local.properties`:

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_KEY=your-anon-key
GOOGLE_WEB_CLIENT_ID=your-web-client-id.apps.googleusercontent.com
```

Place `google-services.json` inside `app/`.

### Build

```bash
./gradlew assembleDebug
```

## Data notes

- Listings can be loaded from Supabase.
- The repository still falls back to `app/src/main/assets/sample_data.json` when remote loading fails.
- New listings are inserted locally first and then synced to Supabase.

## What still needs to be done for a full production version

This section is based on the current codebase, not generic advice.

### 1. Listing management must be completed

- `MyListingsScreen` still shows a `Tahrirlash` button without an implemented edit flow.
- `PostListingScreen` currently posts a hardcoded placeholder image URL instead of real uploaded media.
- Listing creation needs real image upload, validation feedback, progress states, and edit/update support.

### 2. Backend consistency needs hardening

- The app still relies on `sample_data.json` as a fallback source.
- Property refresh is mostly pull-based and local-state-driven, not fully reactive to backend updates.
- Delete and write flows need clearer success/error reporting and stronger sync conflict handling.

### 3. Account lifecycle is not fully complete yet

- `deleteAccount()` currently signs the user out locally but does not show a full backend cleanup flow.
- User profile editing is limited to name and phone.
- A full version should add avatar updates, account recovery, session management, and server-side delete handling.

### 4. Trust and moderation features are still missing

- No reporting flow for fake or abusive listings
- No listing moderation queue or admin review tools
- No verified-agent or verified-owner states
- No fraud-prevention signals, audit logs, or abuse limits

### 5. Search and discovery can be much stronger

- Search is still mainly local text matching plus basic filters.
- A full version should add district/city drill-down, sorting, map search, nearby search, recent searches, and recommendation logic.
- Home should eventually support promoted inventory, editorial collections, and personalization.

### 6. Real media and listing quality systems are needed

- No gallery upload pipeline
- No image compression/cropping flow
- No cover-photo selection
- No listing completeness score
- No mandatory moderation around photo quality or duplicate detection

### 7. Chat is functional but still basic

- No unread counts
- No delivery/read states
- No attachments, image sharing, or voice notes
- No blocking/reporting tools inside chat
- No push notifications for new messages

### 8. Product polish and reliability need another pass

- Some screens are still feature-complete visually but not functionally complete.
- Empty, error, loading, and offline states should be standardized across every data-driven screen.
- Theme preference is currently local to the running app session and should be persisted explicitly.

### 9. Legal and operational readiness is not finished

- Privacy and FAQ pages exist in-app, but production release needs final legal copy and public URLs.
- Support, contact, and escalation flows should be real and connected to operations.
- Analytics, crash reporting, release monitoring, and backup/recovery processes need to be added.

### 10. Business features for a real marketplace are still missing

- Paid promotions or featured listings
- Agent/business accounts
- Lead tracking and seller analytics
- Saved search alerts
- Notification campaigns
- Admin dashboards and content operations

## Recommended next implementation order

1. Real media upload for listings
2. Edit listing flow
3. Proper backend-backed account deletion
4. Notifications and unread chat states
5. Map search and better discovery
6. Moderation/reporting tools
7. Analytics, crash reporting, and release hardening

## Verification

Latest verified local build:

```bash
./gradlew assembleDebug
```
