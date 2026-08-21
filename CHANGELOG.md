# Changelog

## v0.7.0 — Library/Queue sub-tabs

- Radarr and Sonarr now split into a **Library** / **Queue** sub-tab switcher instead of
  one long scrolling list with both sections stacked
- Search and status filters live on the Library sub-tab only; Queue is its own plain list
  with an empty state when there's nothing downloading

## v0.6.0 — Search and status filters

- Radarr and Sonarr library lists now have a search field (filters by title as you type)
  and a row of status filter chips: **All / Downloaded / Monitored / Unmonitored** for
  Radarr, **All / Complete / Monitored / Unmonitored** for Sonarr
- The Queue section is hidden while a search or filter is active, showing only Library
  results that match

## v0.5.0 — Home tab, floating nav, and a poster-loading fix

- **Poster fix**: posters now load correctly. The previous version tried to fetch them
  through Radarr/Sonarr's own image proxy using the API key, but that proxy route is
  gated by a separate cookie-based login system the app never established, so images
  silently failed. Posters are now pulled from the original TMDB/TVDB/Fanart URL that
  Radarr/Sonarr already records for each title (publicly reachable, no auth needed),
  falling back to the server's own copy only when no such URL exists.
- **Home tab**: new dashboard tab (and bottom-nav entry) summarizing all three services
  at a glance — library/queue counts for Radarr and Sonarr, pending count for Overseerr —
  tapping a card jumps straight to that service's tab.
- **Floating nav bar**: the bottom navigation is now a raised, fully-rounded "island"
  with margin on all sides instead of a bar flush against the screen edge, so it no
  longer sits under the phone's gesture/button navigation area.

## v0.4.0 — Real poster artwork

- Radarr and Sonarr library rows, and both detail screens, now show the actual poster
  fetched from the configured server (same source Radarr/Sonarr's own web UI uses),
  authenticated with the same per-service API key — no separate image API key needed
- Falls back to the striped placeholder when a title has no poster yet or the image
  fails to load

## v0.3.0 — Imported design implementation

Replaced the crystal-bubble theme with a design imported from Claude Design
(`ArrTerm Mobile.dc.html`): a dark, refined UI using Sora (headings/body) and
JetBrains Mono (labels/data) typography, a muted-green accent, and pill-shaped
buttons throughout.

- New color system: dark app background (#17191A), card surfaces (#1E2123) with
  subtle borders, accent green (#2E9C6F), and dedicated success/warning/error tones
- Bottom navigation redrawn as pure-shape icons (diamond, rounded rect, circle, ring)
  instead of Material icons, matching the design's icon-free visual language
- Poster placeholders (diagonal-stripe pattern) on library rows and the detail banner
- App-wide floating toast (Saved / Search started / Approved / Declined / etc.) and a
  full-screen delete-confirmation dialog that dims the whole app including the nav bar
- Settings, Radarr, Sonarr, Overseerr, and both detail screens restyled to match the
  spec's card layout, badge colors, and button treatments exactly

## v0.2.0 — Crystal UI overhaul + item detail screens

- **Complete visual redesign**: replaced the dark terminal theme with a light, airy,
  light-blue-and-white "crystal bubble" look. Every interactive element (buttons, status
  badges, list cards, bottom navigation) now renders as a translucent glass bubble with a
  soft tinted shadow, glossy gradient fill, and a pale rim-light border. New reusable
  `GlassSurface` / `GlassCard` / `GlassButton` components back this throughout the app.
- **Movie/series detail screens**: tapping any item in the Radarr or Sonarr library now
  opens a detail page showing overview, status, and monitored/downloaded badges, with:
  - **Search for release** — triggers the server's automatic search for that title
  - **Monitor / Unmonitor** toggle
  - **Delete**, with a confirmation dialog offering to also delete the files on disk
- Launcher icon recolored to match the new palette.

## v0.1.0 — Initial MVP

First working build. Dark, monospace, phosphor-green "terminal" theme throughout.

- Settings screen: per-service (Radarr / Sonarr / Overseerr) server URL + API key entry,
  encrypted on-device storage, and a "Test Connection" button
- Radarr: movie library list with monitored/downloaded status badges, plus the download
  queue
- Sonarr: series library list (episode file counts) plus the download queue, mirroring
  Radarr's layout
- Overseerr: pending requests list with Approve / Decline actions
- Any unconfigured service shows a "not configured, go to Settings" placeholder instead
  of erroring
- Cleartext HTTP allowed for user-entered hosts, since self-hosted *arr servers are
  commonly plain HTTP on a LAN
