//
// Written by Su386.
// See LICENSE for copyright and license notices.
//

package me.partlysanestudios.partlysaneskies.events

import me.partlysanestudios.partlysaneskies.events.minecraft.PSSChatEvent
import me.partlysanestudios.partlysaneskies.events.minecraft.TablistUpdateEvent
import me.partlysanestudios.partlysaneskies.events.minecraft.render.RenderWaypointEvent
import me.partlysanestudios.partlysaneskies.events.skyblock.dungeons.DungeonEndEvent
import me.partlysanestudios.partlysaneskies.events.skyblock.dungeons.DungeonStartEvent
import me.partlysanestudios.partlysaneskies.events.skyblock.mining.MinesEvent
import me.partlysanestudios.partlysaneskies.utils.MinecraftUtils
import me.partlysanestudios.partlysaneskies.utils.SystemUtils.log
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.logging.log4j.Level
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

object EventManager {

    private var oldTablist = emptyList<String>()

    internal val registeredFunctions = HashMap<KClass<*>, ArrayList<EventFunction>>()

    fun register(obj: Any) {
        val kClass = obj::class // get the class
        for (function in kClass.memberFunctions) { // for each function in the class
            if (!function.hasAnnotation<SubscribePSSEvent>()) { // if the functions are not annotated, continue
                continue
            }

            val functionParameters = function.parameters
            if (functionParameters.size != 2) { // if there is not only 1 parameter (param 1 is always the instance parameter
                log(
                    Level.WARN,
                    "Unable to add ${function.name} due to incorrect number of function parameters (${functionParameters.size}",
                )
                continue
            }
            val paramClass = functionParameters[1].type.classifier as? KClass<*> ?: continue

            if (!registeredFunctions.containsKey(paramClass)) {
                registeredFunctions[paramClass] = ArrayList()
            }
            registeredFunctions[paramClass]?.add(EventFunction(obj, function)) // adds the function to a list to call
            log(Level.INFO, "Registered ${function.name} from ${obj.javaClass.name} in PSS events")
        }
    }

    fun tick() {
        val tablist = MinecraftUtils.getTabList()

        if (tablist != oldTablist) {
            TablistUpdateEvent.onUpdate(registeredFunctions[TablistUpdateEvent::class] ?: ArrayList(), tablist)
            oldTablist = tablist
        }
    }

    @SubscribeEvent
    fun onScreenRender(event: RenderWorldLastEvent) {
        RenderWaypointEvent.onEventCall(event.partialTicks, registeredFunctions[RenderWaypointEvent::class] ?: ArrayList())
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onChatReceivedEvent(event: ClientChatReceivedEvent) {
        if (event.type.toInt() != 0) return

        PSSChatEvent.onMessageReceived(registeredFunctions[PSSChatEvent::class] ?: ArrayList(), event.message)
    }

    @SubscribePSSEvent
    fun onChat(event: PSSChatEvent) {
        val message = event.message
        DungeonStartEvent.onMessageReceived(registeredFunctions[DungeonStartEvent::class] ?: ArrayList(), message)
        DungeonEndEvent.onMessageReceived(registeredFunctions[DungeonEndEvent::class] ?: ArrayList(), message)
        MinesEvent.onMessageReceived(registeredFunctions[MinesEvent::class] ?: ArrayList(), message)
    }

    internal class EventFunction(val obj: Any, val function: KFunction<*>)
}
