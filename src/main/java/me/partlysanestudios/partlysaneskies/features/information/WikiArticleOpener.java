//
// Written by Su386.
// See LICENSE for copyright and license notices.
//

package me.partlysanestudios.partlysaneskies.features.information;

import me.partlysanestudios.partlysaneskies.PartlySaneSkies;
import me.partlysanestudios.partlysaneskies.features.economy.auctionhousemenu.AuctionHouseGui;
import me.partlysanestudios.partlysaneskies.utils.HypixelUtils;
import me.partlysanestudios.partlysaneskies.utils.StringUtils;
import me.partlysanestudios.partlysaneskies.utils.SystemUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WikiArticleOpener {
    public static boolean isWaitingForArticle = false;

    public static void getArticle(String id) {
        isWaitingForArticle = true;
        PartlySaneSkies.Companion.getMinecraft().thePlayer.sendChatMessage("/wiki " + id);
    }

    @SubscribeEvent
    public void openArticle(ClientChatReceivedEvent e) {
        if (!isWaitingForArticle) {
            return;
        }
        if (StringUtils.INSTANCE.removeColorCodes(e.message.getFormattedText()).contains("Invalid")) {
            isWaitingForArticle = false;
            return;
        }
        if (!StringUtils.INSTANCE.removeColorCodes(e.message.getFormattedText()).contains("Click HERE")) {
            return;
        }

        isWaitingForArticle = false;
        String wikiLink = e.message.getChatStyle().getChatClickEvent().getValue();
        if (PartlySaneSkies.Companion.getConfig().getOpenWikiAutomatically()) {
            SystemUtils.INSTANCE.openLink(wikiLink);
        }
    }

    public static void keyDown() {
        if (!HypixelUtils.INSTANCE.isSkyblock()) {
            return;
        }
        ItemStack item;
        if (!(PartlySaneSkies.Companion.getMinecraft().currentScreen instanceof GuiContainer)) {
            return;
        }
        if (PartlySaneSkies.Companion.getMinecraft().currentScreen instanceof AuctionHouseGui) {
            return;
        }
        GuiContainer container = (GuiContainer) PartlySaneSkies.Companion.getMinecraft().currentScreen;
        Slot slot = container.getSlotUnderMouse();
        if (slot == null)
            return;
        item = slot.getStack();

        if (item == null) {
            return;
        }

        if (HypixelUtils.INSTANCE.getItemId(item).isEmpty()) {
            return;
        }
        WikiArticleOpener.getArticle(HypixelUtils.INSTANCE.getItemId(item));
    }
}





/*
 * Ca_mo is in the mod now ig
 * https://media.discordapp.net/attachments/1082885746400178286/1093721027273556098/image.png
 * Ca_mo will now be in everyone's Partly Sane Skies
 */

// hi ca_mo - j10a