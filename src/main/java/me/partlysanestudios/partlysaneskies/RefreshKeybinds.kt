package me.partlysanestudios.partlysaneskies

import com.sun.org.apache.xpath.internal.operations.Bool
import me.partlysanestudios.partlysaneskies.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

class RefreshKeybinds {
    private val operatingSystem: String = System.getProperty("os.name").lowercase()
    private var lastClick = -1L //credit to NEU's WardrobeMouseButtons.kt for this framework

    @SubscribeEvent
    fun onGuiKeyboardInput(event: GuiScreenEvent.KeyboardInputEvent.Pre) {
        if (!PartlySaneSkies.config.refreshKeybind) return
        checkKeybinds(event)
    }

    private fun checkKeybinds(event: GuiScreenEvent) {
        if (!PartlySaneSkies.config.refreshKeybind) return
        val gui: GuiChest = event.gui as? GuiChest ?: return
        val keyRDown: Boolean = Keyboard.isKeyDown(Keyboard.KEY_R)
        val refreshKeyDownWindowsLinux: Boolean = ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) xor Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) && keyRDown)
        val refreshKeyDownMacOS: Boolean = ((Keyboard.isKeyDown(Keyboard.KEY_LMETA) xor Keyboard.isKeyDown(Keyboard.KEY_RMETA)) && keyRDown)
        if ((refreshKeyDownWindowsLinux && !operatingSystem.contains("mac")) || (refreshKeyDownMacOS && operatingSystem.contains("mac"))) {
            val container: ContainerChest = (gui).inventorySlots as ContainerChest
            for (i: Int in 0..53) {
                val stackItem: ItemStack = container.getSlot(i).stack ?: continue
                if (stackItem.displayName.contains("Refresh") && (System.currentTimeMillis() - lastClick > 300)) {
                    Utils.clickOnSlot(i)
                    lastClick = System.currentTimeMillis()
                    break
                }
            }
        }
    }
}