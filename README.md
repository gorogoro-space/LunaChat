# LunaChat
[![Paper 1.21.11](https://img.shields.io/badge/Paper-1.21.11-brightgreen.svg)](https://fill-ui.papermc.io/projects/paper/version/1.21.11)
[![GitHub release](https://img.shields.io/github/release/gorogoro-space/LunaChat.svg)](https://github.com/gorogoro-space/LunaChat/releases)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/gorogoro-space/LunaChat/issues)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://github.com/gorogoro-space/LunaChat/blob/main/LICENSE)

README - 日本語 / [English](README_en.md)

チャンネルチャットと、ローマ字入力の漢字変換(Japanize)を提供する Paper 用プラグインです。

**このリポジトリはフォークです。** 元のソースコードは [ucchy](https://github.com/ucchyocean) さんの [ucchyocean/LunaChat](https://github.com/ucchyocean/LunaChat) で、それを引き継いだ [f1w3/LunaChat](https://github.com/f1w3/LunaChat)(LunaChat Remastered)から、さらにフォークしています。
f1w3/LunaChat v3.0.20 からの主な変更点は次のとおりです。

- Paper 1.21.11 / Java 21 専用にし、ビルドを Maven から Gradle に変更(1.21.11 未満のサーバーでは読み込まれません。Paper の API を使うので、Spigot では動きません)
- BungeeCord への対応を削除(Paper のサーバーに入れて使うプラグインになりました)
- bStats による統計情報の送信を削除(`config.yml` の `metricsDisabled` は使われなくなりました)
- `/tell` などで、存在しない名前やサーバーに来たことのない名前を指定すると、サーバーが数秒止まる不具合を修正
- プレイヤーを名前で探すとき(`/ch kick` `/ch ban` `/ch mute` `/ch hide` など)、Mojang のサーバーに問い合わせないように変更。オンラインの人と、サーバーのキャッシュ(`usercache.json`)にいる人だけが見つかります
- Paper で非推奨になっている API の置き換え(ビルド時の警告の修正)
- jar に入っている `config.yml` を日本語版にし、英語版を `config_en.yml` として展開するように変更(サーバーでは言語に関係なく `config.yml` を読みます。すでにある `config.yml` はそのまま使われます)
- `config.yml` の `playerChatEventListenerPriority` の既定値を `HIGHEST` に変更(設定に行が無いときも `HIGHEST` です)

## 主な機能

- チャンネルチャット。チャンネルごとに、色・書式・パスワード・リストからの非表示・発言範囲などを設定できます
- チャンネルのモデレーター。モデレーターは、チャンネルの設定の変更や、キック・BAN ができます(作ったばかりのチャンネルでは、最初に入った人がモデレーターになります)
- チャットのローマ字入力を、かなや漢字に変換します(Japanize)
- チャンネルチャットの発言はクリックできます
- `/tell` を LunaChat のものに置き換えます。`/tell` のメッセージも漢字に変換され、`/r` で返信できます
- ほかのプラグインから使える API とイベント

### 連携できるプラグイン(どれも任意)

- Vault(接頭辞・接尾辞を設定するプラグインと連携)
- dynmap
- Multiverse-Core
- mcMMO

# Requirements
- Paper 1.21.11
- Java 21

# Installation method
Please place the .jar file in the Paper plugins folder.

.jar ファイルを Paper の plugins フォルダに置き、サーバーを再起動してください。
更新するときは、古い jar(`LunaChat-3.0.20.jar` など)を先に消してください。古い jar が残っていると、どちらが読み込まれるかわかりません。

# Usage
`/ch` でコマンドの一覧(ヘルプ)が表示されます。

```
/ch join <チャンネル名>   チャンネルに参加します(config.yml の設定によっては、無ければ作ります)
/ch leave              参加しているチャンネルから退出します
/ch list               チャンネルの一覧を表示します
/tell <名前> <メッセージ>  オンラインの人にメッセージを送ります(/msg /m /t も同じ)
/r <メッセージ>           直前の個人チャットの相手に返信します
/jp on|off             自分のチャットのローマ字変換を ON / OFF にします
```

# Data
`plugins/LunaChat/` に次のものを保存します。

- `config.yml` — 設定
- `config_en.yml` — 設定の英語の説明(読み込まれません。設定は `config.yml` に書きます)
- `messages/` — メッセージの文面
- `channels/` — チャンネルごとの設定とメンバー
- `defaults.yml`・`templates.yml`・`japanize.yml`・`dictionary.yml`・`hidelist.yml` — 発言先のチャンネル、書式のテンプレート、ローマ字変換の ON / OFF、変換の辞書、非表示にした相手
- `uuidcache.yml` — プレイヤーの UUID と名前の記録
- `logs/` — チャットのログ(config.yml で記録する設定のとき)

# Disclaimer
Do not assume any responsibility by use. Please use it at your own risk.

## ビルド

JDK 21 が必要です。Gradle はラッパー(`gradlew`)が自動でダウンロードするので、別途インストールする必要はありません。

```
gradlew.bat clean build
```

(macOS / Linux では `./gradlew clean build`)

## IntelliJ IDEA でのビルド手順

本プロジェクトはビルドツールに Gradle を使用しています。
IntelliJ IDEA 上で正しくプラグイン（JARファイル）を生成するには、以下の手順を実行してください。
「ビルド → アーティファクトのビルド」はクラスファイルが入らないことがあるので使わないでください。

### 🛠️ ビルド手順

1. IntelliJ IDEA の画面右端にある **「Gradle」タブ** をクリックして開きます。
2. プロジェクト名（LunaChat）を展開し、 **`Tasks`** ツリーを開きます。
3. リスト内にある **`clean`** をダブルクリックして実行します（古いビルドキャッシュを削除します）。
4. 続けてリスト内にある **`build`** をダブルクリックして実行します。

### 📦 生成されたファイルの場所
ビルドが成功すると、プロジェクトのルート直下に `build/libs` フォルダが作成（または更新）され、その中に中身の詰まった正しい JAR ファイルが生成されます。

* **生成先:** `build/libs/LunaChat-<version>.jar`(例: `LunaChat-4.0.0.jar`。バージョンは `gradle.properties` の `version`)

この JAR ファイルを Minecraft サーバーの `plugins` フォルダに配置してください。

## 開発(IntelliJ IDEA で Claude Code を使う)

このリポジトリには、AI コーディングツール [Claude Code](https://docs.claude.com/ja/docs/claude-code/overview) 向けの作業ルールを書いた `CLAUDE.md` があります。IntelliJ IDEA で使う手順は次のとおりです。

1. Claude の有料プラン(Pro / Max)か、Anthropic API のアカウントを用意します。
2. Windows では、先に [Git for Windows](https://git-scm.com/downloads/win) をインストールします。
3. Claude Code 本体をインストールします。Windows では PowerShell で次を実行します。
   ```
   irm https://claude.ai/install.ps1 | iex
   ```
4. IntelliJ IDEA の「設定 → プラグイン → Marketplace」で「Claude Code」を検索してインストールし、IDE を再起動します。検索結果には Anthropic 以外が作った似た名前のプラグインも表示されます。プラグイン名の下に書かれた提供元が「Anthropic」になっているものを選んでください。
5. プロジェクトを開いて、ターミナルで `claude` を実行します(`Ctrl+Esc` でも起動できます)。初回はブラウザでログインします。

起動すると `CLAUDE.md` が自動で読み込まれ、このプロジェクトの設計方針に沿って作業します。

## ライセンス

本プラグインのライセンスは、フォーク元と同じく **GNU Lesser General Public License v3.0(LGPL v3)** です。条文は [LICENSE](LICENSE) を参照してください。LGPL v3 は GPL v3 に追加の許可を加えたものなので、GPL v3 の条文も [COPYING](COPYING) に置いています。
元の著作権は ucchy さんと、フォーク元の貢献者の皆さんにあります。

## kubotan へのメモ

リポジトリを新規作成したら、**必ず Watch を設定すること**(忘れない!)。
GitHub の自動 Watch 機能は 2025 年 5 月に廃止されたため、設定しないと他の人が立てた issue や PR の通知が届かない。

1. リポジトリのページ右上の **「Watch」** を押す
2. **「Custom」** を選び、**Issues** と **Pull requests** にチェックを入れる
