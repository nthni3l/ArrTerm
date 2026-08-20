# Changelog

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
