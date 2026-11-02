/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import com.github.ucchyocean.lc3.LunaChatStandalone;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.channel.StandaloneChannel;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberDummy;
import junit.framework.TestCase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ucchy
 */
public class ClickableFormatTest extends TestCase {

    // Gradle の出力先(build/)を使う
    private static final String DATA_FOLDER = "build" + File.separator + "LunaChatTest";
    private static final String MESSAGES_FOLDER = "build" + File.separator + "resources" + File.separator + "main";

    private static String plainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Component とその子孫を、すべて並べて返す
     */
    private static List<Component> flatten(Component component) {
        List<Component> result = new ArrayList<>();
        result.add(component);
        for (Component child : component.children()) {
            result.addAll(flatten(child));
        }
        return result;
    }

    /**
     * 指定したクリック動作を持つ部品があるかどうか
     */
    private static boolean hasClickEvent(Component component, ClickEvent.Action action, String value) {
        for (Component c : flatten(component)) {
            ClickEvent event = c.clickEvent();
            if (event != null && event.action() == action
                    && event.payload() instanceof ClickEvent.Payload.Text text
                    && text.value().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void testChannelChatKeyword() throws Exception {

        File dataFolder = new File(DATA_FOLDER);
        System.out.println("dataFolder : " + dataFolder.getAbsolutePath());
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // jar が無いので config.yml を jar からコピーできない。ビルド済みのリソースから用意する
        Files.copy(new File(MESSAGES_FOLDER, "config.yml").toPath(),
                new File(dataFolder, "config.yml").toPath(), StandardCopyOption.REPLACE_EXISTING);

        LunaChatStandalone lunachat = new LunaChatStandalone(dataFolder);
        lunachat.onEnable();

        Messages.initialize(new File(MESSAGES_FOLDER), null, "ja");

        //String format = "&f[%color%ch&f]%prefix%displayname%suffix&a:&f %msg";
        String format = "&7[%color%ch&7]%prefix%username: &f%msg";
        String jpFormat = "%japanize&7 $%msg";

        ChannelMember member = new ChannelMemberDummy();
        Channel channel = new StandaloneChannel("r");
        channel.setColorCode(ChatColor.AQUA.toString());

        ClickableFormat f = ClickableFormat.makeFormat(format, member, channel, true);
        f.replace("%msg", jpFormat);

        String legacy = f.toLegacyText();
        System.out.println("pre = " + f + ", legacy text = " + legacy);

        Component comp = f.makeTextComponent();
        System.out.println("post : " + comp);

        // 表示される文字列は、クリックの部品を文字列にしたものと同じ
        assertEquals(Utility.stripColorCode(Utility.replaceColorCode(legacy)), plainText(comp));

        // チャンネル名をクリックすると参加、名前をクリックすると /tell の入力になる
        assertTrue(hasClickEvent(comp, ClickEvent.Action.RUN_COMMAND, "/lunachat join r"));
        assertTrue(hasClickEvent(comp, ClickEvent.Action.SUGGEST_COMMAND, "/tell " + member.getName()));

        // issue #202: チャンネル名(クリックの部品)には、直前の文字列の色(%color の AQUA)が付く
        boolean found = false;
        for (Component c : flatten(comp)) {
            if (c.clickEvent() != null && c.clickEvent().action() == ClickEvent.Action.RUN_COMMAND) {
                assertEquals(NamedTextColor.AQUA, c.color());
                found = true;
            }
        }
        assertTrue(found);
    }

    public void testEventChatKeyword() {

        String format = "&f%prefix%displayname%suffix&a:&f %msg";
        ChannelMember member = new ChannelMemberDummy();

        Messages.initialize(new File(MESSAGES_FOLDER), null, "ja");

        ClickableFormat f = ClickableFormat.makeFormat(format, member);

        String legacy = f.toLegacyText();
        System.out.println("pre = " + f + ", legacy text = " + legacy);

        Component comp = f.makeTextComponent();
        System.out.println("post : " + comp);

        assertEquals(Utility.stripColorCode(Utility.replaceColorCode(legacy)), plainText(comp));
        assertTrue(hasClickEvent(comp, ClickEvent.Action.SUGGEST_COMMAND, "/tell " + member.getName()));
    }

    public void testUrlIsClickable() {

        ClickableFormat f = ClickableFormat.makeFormat("&f%msg", null);
        f.replace("%msg", "see https://example.com/a?b=1 and example.org now");
        Component comp = f.makeTextComponent();

        assertEquals("see https://example.com/a?b=1 and example.org now", plainText(comp));
        assertTrue(hasClickEvent(comp, ClickEvent.Action.OPEN_URL, "https://example.com/a?b=1"));
        // 「http://」の無い URL も、以前の BungeeCord の部品と同じく http:// を付けて開く
        assertTrue(hasClickEvent(comp, ClickEvent.Action.OPEN_URL, "http://example.org"));
    }

    public void testUrlInsideWordIsNotClickable() {

        ClickableFormat f = ClickableFormat.makeFormat("&f%msg", null);
        f.replace("%msg", "xhttps://example.com, ok");
        Component comp = f.makeTextComponent();

        for (Component c : flatten(comp)) {
            assertNull(c.clickEvent());
        }
    }
}
