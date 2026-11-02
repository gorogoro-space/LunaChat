# LunaChat

チャンネルチャットと、ローマ字入力の漢字変換(Japanize)を提供する Paper 用プラグイン(Paper 1.21.11 以降・Java 21 必須)。
**フォーク元**: [f1w3/LunaChat](https://github.com/f1w3/LunaChat)(LunaChat Remastered)。その元は [ucchyocean/LunaChat](https://github.com/ucchyocean/LunaChat)(作者 ucchy)
リポジトリ: https://github.com/gorogoro-space/LunaChat (LGPL-3.0、公開リポジトリ)

## 作業の進め方(必ず守ること)

- **設計が確定するまで実装しない。** 機能追加や仕様変更は、まず設計案(何を・なぜ・どう変えるか、影響範囲)を提示し、承認を得てからコードを書く。
- 判断が必要な点は、選択肢を示して質問する。勝手に決めない。
- やり取りは日本語で行う。新しく書くコードのコメントは日本語でよい。
- 変更は必要最小限にする。頼まれていないリファクタリングや機能追加はしない(フォークなので、元のコードとの差分を小さく保つ意味もある)。
- 作業後は、変更・追加・削除したファイルの一覧と変更内容を報告する。
- 仕様を変えたら README.md・README_en.md と CLAUDE.md も合わせて更新する。
- `git push` の前には必ず確認を取る。コミットは意味のある単位で分ける。
- リポジトリへの初回の `git push` の前には、「GitHub で Watch 設定(Custom → Issues と Pull requests)はしましたか?」と日本語で確認する。自動 Watch は廃止されており、設定しないと issue や PR の通知が届かない。
- コミットメッセージや PR に `Co-Authored-By: Claude` などの署名を付けない。`.claude/` は Git に入れない(`.git/info/exclude` で除外済み)。
- 実装中に設計の抜けや穴に気づいたら、黙って対処せず報告して相談する。
- プルリクエストをチェックするときは、次の観点で確認して報告する。
  - 変更概要(何を・なぜ変えているか)
  - 脆弱性(権限チェックの漏れ、入力値の検証、パスの扱いなど)
  - 安全面(データの破損・消失、再読み込み時の後始末、他プラグインの妨げなど)
  - 性能(TPS など)の低下(高頻度イベントでの重い処理、メインスレッドでの同期 I/O、定期タスクの追加など。「最重要の設計方針」に沿っているか)

## フォークとしての注意

- 既存のコメントは日本語が中心(ucchy さんのもの)。既存のコメントは、頼まれない限り書き換えない。
- パッケージ名 `com.github.ucchyocean.lc3`(本体)と `com.github.ucchyocean.lc`(LunaChat 2 系の API との互換用)は変えない。ほかのプラグインが API として使うので、`LunaChatAPI` や `event` のクラスの名前・引数も、頼まれない限り変えない。
- ライセンスは LGPL-3.0。ソースの先頭の著作権表示(`@author ucchy` など)を消さない。`LICENSE` が LGPL v3、`COPYING` がその元になる GPL v3 の条文で、jar には両方を `META-INF/` に入れている。
- `plugin.yml` は `authors: [ ucchy, YukiLeafX, LazyGon, tomo1560, kubotan ]`、`website` はこのリポジトリの URL にしてある。

## 環境

- Paper 1.21.11(`io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT`、`compileOnly`)/ `plugin.yml` の `api-version: '1.21.11'`(1.21.11 未満のサーバーでは読み込まれない)。勝手に変えない
- **Java 21 必須。** `build.gradle.kts` の toolchain で JDK 21 を使い、Java 21 の class ファイルを出力する。勝手に変えない
- ビルド: Gradle(Kotlin DSL、wrapper 9.7.1)。`gradlew.bat clean build`(Windows)。**JDK 21 で実行する**(JDK の場所は `CLAUDE.local.md`)。「ビルドして」と言われたら常にクリーンビルドする。成果物は `build/libs/LunaChat-<version>.jar`。テスト(JUnit 4、`src/test`)も `build` で走る
  - IntelliJ の「アーティファクトのビルド」はクラスファイルが入らないことがある。必ず Gradle でビルドする
- バージョンは `gradle.properties` の `version` だけで管理する。`plugin.yml` などの `${project.version}`・`${project.name}`・`${project.description}` は `processResources` でビルド時に置き換わる
- 連携するプラグイン(どれも softdepend・`compileOnly`): Vault、dynmap、Multiverse-Core、mcMMO。jar に同梱するライブラリはない(gson などは Paper に入っているものを使う)
- commons-lang などの外部ユーティリティは使わず、Java 標準で書く
- メッセージは Adventure(`Component`)で送る。`ChannelMember.sendMessage(Component)`。色コード(`§`)の文字列との変換は `util/UtilityBukkit` の `fromLegacyText`・`fromLegacyTextWithUrls`(URL をクリックで開けるようにする)・`toLegacyText` を使う。BungeeCord の chat API(`TextComponent` など)・`sender.spigot()` は使わない
- 非推奨の API は使わない(`-Xlint:all` で警告 0 件にしてある)。警告を `@SuppressWarnings` で抑えているのは次の場所だけで、増やすときは理由をコメントに書く
  - チャットの受け取りの `AsyncPlayerChatEvent`(`bukkit/BukkitEventListener`・`BukkitNormalChatJapanizeTask`。理由は下の「チャットの受け取り」)
  - 古い API(`com.github.ucchyocean.lc`)との互換のための `deprecation`(`lc/LunaChat`・`lc/LunaChatAPIImpl`・`bukkit/BukkitEventSender`。古いイベントを発火するため)
  - YAML の読み込みの `unchecked`(`channel/Channel`・`util/YamlConfig`・`util/YamlSection`)と、`util/ChatColor` の `unused`(元のコードのまま)
- パッケージ名は**すべて小文字**のままにする。大文字が混ざると plugin.yml の main と一致せず起動しない
- 動作確認はサーバーを再起動して行う。PlugManX での読み込みは権限やコマンドの登録が不完全になることがある。jar を差し替えるときは先に古い jar を消す(同じ名前のプラグインが 2 つあると、どちらが読まれるかわからない)
- サーバーと開発 PC の環境情報は `CLAUDE.local.md`(Git に入れない)を参照する

## リリース

- 既定のブランチは `main`。GitHub Actions などの自動ビルドは無い(手元でビルドした jar を添付する)
- 手順: 「バージョンを X.Y.Z にする」コミットで `gradle.properties` の `version` を上げる → push → クリーンビルド → タグ `vX.Y.Z` で GitHub のリリースを作り、`LunaChat-X.Y.Z.jar` を添付する
- リリースの本文は日本語で、動作環境・主な変更点・更新するときの注意(古い jar を消すなど)・ライセンスを書く。公開前に本文をユーザーに確認してもらう
- 最初のリリースは v4.0.0(f1w3/LunaChat v3.0.20 からのフォーク)

## 最重要の設計方針

### TPS に影響させない
- 高頻度イベント(AsyncPlayerChatEvent、PlayerMoveEvent など)を使うときは、安い判定を先に行い、対象外なら即座に抜ける
- BlockPhysicsEvent、VehicleMoveEvent など発生頻度が極端に高いイベントは使わない
- メインスレッドでファイルや DB の同期 I/O をしない(起動時・リロード時に一度だけ行う小さなファイルの読み込みは除く)
- メインスレッドで外部と通信しない。Mojang のサーバーへの問い合わせ(名前から UUID を引くなど)はしない。プレイヤーを名前で探すときは、オンラインの人とサーバーのキャッシュだけを見る(`UtilityBukkit.getOfflinePlayer` は `Bukkit.getOfflinePlayerIfCached` を使う)
- 定期タスクは最小限にし、追加するときは頻度と理由を設計案に書く(今あるのは、BAN・ミュートの期限を確かめる `ExpireCheckTask` だけ。非同期で 30 秒ごと)

### チャットの受け取り
- 通常のチャットは `AsyncPlayerChatEvent`(Paper では非推奨)で受け取る。Paper の `AsyncChatEvent` には移さない
  - 理由: Paper は 1 回の発言で、先に古い `AsyncPlayerChatEvent` を MONITOR まで処理してから `AsyncChatEvent` を出す。LunaChat が `AsyncChatEvent` で発言を止めても、古いイベントを使う dynmap(MONITOR で Web に送る)・mcMMO・CoreProtect などには止まっていない発言が届き、チャンネルの発言が dynmap の Web マップに出てしまう
  - 実機でも確認済み(2026-11-03): チャットの受け取りだけを `AsyncChatEvent` に変えた版で、`/ch create test` → `/ch join test` のあとに発言すると、その発言が dynmap の Web マップに表示された。`AsyncChatEvent` に移すなら、dynmap などへの転送を止める方法と一緒に設計し、同じ手順で確かめる
- 優先度は `config.yml` の `playerChatEventListenerPriority` で選ぶ(既定は HIGHEST。行が無いとき・読めないときも HIGHEST)。リスナーは優先度ごとにメソッドを用意し、設定と一致するものだけが処理する

### 外部と通信しない
- 統計情報の送信(bStats など)や更新チェックなど、外部への通信は入れない(フォーク元にあった bStats は削除済み)
- 例外は Japanize の漢字変換で、Google の変換 API(`japanize/IMEConverter`)に問い合わせる。これは元からある機能なので残している(非同期のタスクで行う)

### 他プラグインとの関係
- 既存の機能を妨げないこと。イベントをキャンセルする範囲は必要最小限にする

## 機能仕様

(現在の実装の動作の要点。詳しい使い方は README.md)

- チャンネルチャット(`/ch`、別名 `/lunachat` `/lc`)。参加・退出・一覧・招待・キック・BAN・ミュート・非表示・モデレーター・チャンネルの設定など
- `/tell`(`/msg` など)と `/r` を LunaChat のものに置き換える。個人チャットも Japanize で変換する
- `/jp`(`/japanize`)で、自分のチャットのローマ字変換の ON / OFF を切り替える
- チャンネルチャットの発言のチャンネル名・プレイヤー名はクリックできる(参加コマンドの実行・`/tell` の入力)
- 設定: jar の日本語版を `config.yml`、英語版を `config_en.yml` として同梱。サーバーでは常に `config.yml` を読む。`config_en.yml` は説明を読むために展開するだけで、読み込まない
- メッセージ: `messages_ja.yml`・`messages_en.yml`。足りないキーは jar の `messages_en.yml` から補う。`Messages.java` のメソッドは `tool/MessageParser` が `messages_ja.yml` から生成する(手で書き換えず、MessageParser を直して作り直す)

## 過去にハマった点(他プロジェクトでの経験を含む)

- `/tell` などで存在しない名前を指定すると、Mojang への問い合わせでサーバーが数秒止まっていた(4.0.0 で修正。オンラインの人とキャッシュだけを見るようにした)
- `config.getString(path, "")` のように既定値を渡すと、jar 内 config.yml の既定値が参照されない。既定値なしで取得して null を判定すること
- plugin.yml で `default: true` にした権限でも、登録されないと Bukkit は「OP のみ」として扱う。全員向けの機能を権限で縛らない

## ファイル構成

- `src/main/java/com/github/ucchyocean/lc3/LunaChatBukkit.java` — メインクラス(連携プラグインの読み込み、リスナー・コマンドの登録、期限チェックのタスク)
- `lc3/LunaChatConfig.java` — config.yml の読み込み
- `lc3/bukkit/` — チャットのイベントのリスナー(`BukkitEventListener`)、Japanize の 2 行表示のタスク、LunaChat のイベントの発火
- `lc3/channel/` — チャンネル(`Channel`・`BukkitChannel`)とその管理(`ChannelManager`)
- `lc3/command/` — `/ch` のサブコマンド、`/tell`・`/r`・`/jp`
- `lc3/member/` — 発言者(プレイヤー・コンソール・コマンドブロックなど)
- `lc3/japanize/` — ローマ字からかなへの変換と、漢字変換(IME)
- `lc3/bridge/` — Vault・dynmap・Multiverse-Core・mcMMO との連携
- `lc3/util/` — 書式(`ClickableFormat`)、Adventure との変換(`UtilityBukkit`)、YAML など
- `src/main/java/com/github/ucchyocean/lc/` — LunaChat 2 系の API との互換
- `src/main/resources/` — `plugin.yml`、`config.yml`・`config_en.yml`、`messages_ja.yml`・`messages_en.yml`
- `build.gradle.kts` / `settings.gradle.kts` / `gradle.properties` — Gradle の設定(バージョンは `gradle.properties`)
