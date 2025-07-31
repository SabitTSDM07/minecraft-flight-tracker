# ✈️ Minecraft Flight Tracker

A real-time aircraft tracking mod for Minecraft (Forge 1.20.1) powered by OpenSky Network.



## 📦 Features
- Live tracking of real-world aircraft
- Accurate heading, location, and altitude
- Color-coded altitude and direction arrows
- Works in singleplayer or multiplayer (experimental)
- Powered by OpenSky API (no account required)

## 🔧 Commands
- `/ft location <lat> <lon> <radius>` – Set the reference point for tracking
- `/ft fetchplanes` – Fetch and render visible aircraft
- `/ft toggle` – Start/stop auto-tracking
- `/ft erase` – Remove all visible aircraft

## 🧭 Dependencies
- Minecraft Forge 1.20.1
- Java 17+

## 🛫 Data Source
Aircraft data provided by [OpenSky Network](https://opensky-network.org).

## 📁 Installation
Drop the `.jar` file into your Minecraft `mods/` folder and launch with Forge.

## 🌍 License
MIT License – see `LICENSE`.

---

### 🛠 Building from Source
```bash
./gradlew build
