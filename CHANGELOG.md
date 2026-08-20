# Changelog

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
