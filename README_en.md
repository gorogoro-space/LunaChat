# LunaChat
[![Paper 1.21.11](https://img.shields.io/badge/Paper-1.21.11-brightgreen.svg)](https://fill-ui.papermc.io/projects/paper/version/1.21.11)
[![GitHub release](https://img.shields.io/github/release/gorogoro-space/LunaChat.svg)](https://github.com/gorogoro-space/LunaChat/releases)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/gorogoro-space/LunaChat/issues)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://github.com/gorogoro-space/LunaChat/blob/main/LICENSE)

README - [日本語](README.md) / English

A Paper plugin that provides channel chat and romaji-to-Japanese (kana/kanji) conversion (Japanize).

**This repository is a fork.** The original source code is [ucchyocean/LunaChat](https://github.com/ucchyocean/LunaChat) by [ucchy](https://github.com/ucchyocean). This fork is based on [f1w3/LunaChat](https://github.com/f1w3/LunaChat) (LunaChat Remastered), which continued that project.
Main changes from f1w3/LunaChat v3.0.20:

- Paper 1.21.11 / Java 21 only. The build tool was changed from Maven to Gradle (the plugin is not loaded on servers older than 1.21.11. It uses the Paper API, so it does not work on Spigot)
- Removed BungeeCord support (this is now a plugin for Paper servers)
- Removed bStats statistics reporting (`metricsDisabled` in `config.yml` is no longer used)
- Fixed a bug where the server froze for a few seconds when `/tell` etc. was used with a name that does not exist or has never joined the server
- Looking up players by name (`/ch kick`, `/ch ban`, `/ch mute`, `/ch hide`, etc.) no longer queries Mojang's servers. Only online players and players in the server's cache (`usercache.json`) are found
- Replaced APIs deprecated by Paper (fixed build warnings)
- The `config.yml` in the jar is now the Japanese version, and the English version is extracted as `config_en.yml` (the server always reads `config.yml`, regardless of the language. An existing `config.yml` is kept as is)
- The default of `playerChatEventListenerPriority` in `config.yml` is now `HIGHEST` (also when the line is missing)

## Features

- Channel chat. Each channel can have its own color, format, password, visibility in the list, chat range, and more
- Channel moderators. Moderators can change channel settings and kick or ban players (for a newly created channel, the first player who joins becomes the moderator)
- Converts romaji typed in chat into kana and kanji (Japanize)
- Messages in channel chat are clickable
- Replaces `/tell` with LunaChat's version. `/tell` messages are also converted, and you can reply with `/r`
- API and events for other plugins

### Supported plugins (all optional)

- Vault (for prefix/suffix plugins)
- dynmap
- Multiverse-Core
- mcMMO

# Requirements
- Paper 1.21.11
- Java 21

# Installation method
Please place the .jar file in the Paper plugins folder and restart the server.
When updating, delete the old jar (such as `LunaChat-3.0.20.jar`) first. If the old jar remains, you can't tell which one will be loaded.

# Usage
`/ch` shows the list of commands (help).

```
/ch join <channel>      Join a channel (depending on config.yml, it is created if it does not exist)
/ch leave               Leave the channel you are in
/ch list                Show the list of channels
/tell <name> <message>  Send a message to an online player (/msg /m /t also work)
/r <message>            Reply to the last private chat partner
/jp on|off              Turn Japanize conversion of your chat on / off
```

# Data
The following are saved in `plugins/LunaChat/`.

- `config.yml` — settings
- `config_en.yml` — English descriptions of the settings (not loaded; write your settings in `config.yml`)
- `messages/` — message texts
- `channels/` — settings and members of each channel
- `defaults.yml`, `templates.yml`, `japanize.yml`, `dictionary.yml`, `hidelist.yml` — default channels, format templates, Japanize on / off, conversion dictionary, hidden players
- `uuidcache.yml` — record of player UUIDs and names
- `logs/` — chat logs (when logging is enabled in config.yml)

# Disclaimer
Do not assume any responsibility by use. Please use it at your own risk.

## Build

JDK 21 is required. Gradle is downloaded automatically by the wrapper (`gradlew`), so you don't need to install it.

```
gradlew.bat clean build
```

(`./gradlew clean build` on macOS / Linux)

The jar is created at `build/libs/LunaChat-<version>.jar` (for example `LunaChat-4.0.0.jar`; the version is `version` in `gradle.properties`).
In IntelliJ IDEA, run `clean` and then `build` from the Gradle tool window (`Tasks`). Do not use "Build → Build Artifacts", because the class files may not be included.

## License

Like the original project, this plugin is licensed under the **GNU Lesser General Public License v3.0 (LGPL v3)**. See [LICENSE](LICENSE) for the full text. Since the LGPL v3 is the GPL v3 with additional permissions, the GPL v3 text is also included in [COPYING](COPYING).
The original copyright belongs to ucchy and the contributors of the original projects.
