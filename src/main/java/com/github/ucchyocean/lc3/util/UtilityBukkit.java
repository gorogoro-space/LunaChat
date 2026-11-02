/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Bukkit関連のユーティリティクラス
 *
 * @author ucchy
 */
public class UtilityBukkit {

    /**
     * 「§」の色コードの文字列と Component を変換する。
     * Paper が Player#getDisplayName などの文字列版 API の中で使っているものと同じ形式
     * (RGB の色は「§x§r§r§g§g§b§b」で表す)。
     */
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /**
     * チャットの文字列用。LEGACY と同じ形式で、さらに URL をクリックで開けるようにする。
     * 以前使っていた BungeeCord の TextComponent#fromLegacy と同じく、
     * 空白で区切った語の全体が URL の形のときだけリンクにする(「http://」の無い「example.com」も対象)。
     */
    private static final LegacyComponentSerializer LEGACY_WITH_URLS = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .extractUrls(Pattern.compile("(?<=^|\\s)(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?(?=\\s|$)"))
            .build();

    /**
     * 「§」の色コードを含む文字列を Component にする
     *
     * @param legacyText 「§」の色コードを含む文字列
     * @return Component
     */
    public static Component fromLegacyText(String legacyText) {
        return LEGACY.deserialize(legacyText);
    }

    /**
     * 「§」の色コードを含むチャットの文字列を Component にする。URL はクリックで開けるようにする。
     *
     * @param legacyText 「§」の色コードを含む文字列
     * @return Component
     */
    public static Component fromLegacyTextWithUrls(String legacyText) {
        return LEGACY_WITH_URLS.deserialize(legacyText);
    }

    /**
     * Component を「§」の色コードを含む文字列にする
     *
     * @param component Component
     * @return 「§」の色コードを含む文字列
     */
    public static String toLegacyText(Component component) {
        return LEGACY.serialize(component);
    }

    /**
     * 現在接続中のプレイヤーを全て取得する
     *
     * @return 接続中の全てのプレイヤー
     */
    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    /**
     * 現在のサーバー接続人数を返します。
     *
     * @return サーバー接続人数
     */
    public static int getOnlinePlayersCount() {
        return getOnlinePlayers().size();
    }

    /**
     * 指定された名前のオフラインプレイヤーを取得する。
     * Mojang API への問い合わせは行わず、オンラインのプレイヤーとサーバーのキャッシュから探す。
     *
     * @param name プレイヤー名
     * @return オフラインプレイヤー、見つからなければ null
     */
    public static OfflinePlayer getOfflinePlayer(String name) {
        if (name == null) return null;
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
        if (player == null) return null;
        if (!player.hasPlayedBefore() && !player.isOnline())
            return null;
        return player;
    }

    /**
     * 指定された名前のプレイヤーを取得する
     *
     * @param name プレイヤー名
     * @return プレイヤー
     */
    public static Player getPlayerExact(String name) {
        return Bukkit.getPlayer(Utility.stripColorCode(name));
    }

    /**
     * 指定された名前のプレイヤーが接続したことがあるかどうかを検索する
     *
     * @param name プレイヤー名
     * @return 接続したことがあるかどうか
     */
    public static boolean existsOfflinePlayer(String name) {
        return (getOfflinePlayer(name) != null);
    }
}
