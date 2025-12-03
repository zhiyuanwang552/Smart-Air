📱 Smart-Air Android Application
A child-friendly Android app for learning asthma care, building healthy inhaler habits, and safely sharing progress with healthcare providers.

📌 Purpose
SMART AIR helps children aged 6–16 understand asthma, practice good inhaler technique, log medicines and symptoms, and share parent-approved information with trusted healthcare providers.
Parents stay informed through dashboards, alerts, and exportable reports.

👥 Users & Roles
Child
  - Logs rescue/controller use
  - Records symptoms, triggers, PEF
  - Uses gamified technique helper and earns streaks/badges

Parent
  - Manages one or more children
  - Receives alerts and triage notifications
  - Configures sharing controls

Provider (Read-Only)
  - Accesses only the information the Parent chooses to share
  - Invited via a one-time 7-day code (revocable at any time)

Privacy Defaults:
Children see only their own data
No data is shared with Providers unless explicitly enabled by the Parent

🚀 Features
1. Login
   - Parents, Child, Providers can log in to the account with their username and password
   - Password recovery option for users with registered email
   - Child profiles can be made so children can log into their account without username and password
   - Introduction to the app if it is the first time logging in
     
2. Parents can manage providers permissions
   - Parents can link multiple children accounts and children profiles
   - Parents can toggle in realtime of what information the provider can see
   - Parent can invite providers to view children information via a 7 days expiring link
     
3. Medicines, Techniques and Motivations
   - Separate rescue/controller logging
   - Technique helper with step-by-step guidance
   - Pre/Post check-in (“Better / Same / Worse”)
   - Inventory tracking with alerts (low/expired)
   - Streaks and badges for motivation

4. Safety: PEF, Zones & Triage
   - Manual PEF entry with PB-based zone display
   - One-tap triage with red-flag checks and action steps
   - Parent alerts upon triage start/escalation
   - Incident logging for reports
  
5. Symptoms, Triggers & History
   - Daily check-in for symptoms
   - Multi-tag triggers
   - History browser (3–6 months) with export (PDF/CSV)
     
6. Parent Home, Alerts & Provider Report
   - Dashboard tiles: today’s zone, last rescue use, weekly counts, trend chart
   - Real-time alerts (red-zone, rapid rescue repeats, triage escalation, inventory)
   - Provider report: adherence, rescue frequency, symptoms, zones, incidents, charts

🛠️ Technology used
Android (Java/Kotlin)
Firebase Authentication
Firebase Realtime Database
JUnit Testing (Mockito)

📦 Build Instructions
1. Clone repository
2. Open in Android Studio
3. Sync Gradle
4. Add Firebase configuration files
5. Build & run on device/emulator
