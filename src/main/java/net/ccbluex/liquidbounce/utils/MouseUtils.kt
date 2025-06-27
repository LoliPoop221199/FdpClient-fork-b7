/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 *
 *
 *This module createt by lol1k.xyu (discord)
 * or join discord server freeclients.
 */
package net.ccbluex.liquidbounce.utils

import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.lwjgl.input.Mouse
import java.nio.ByteBuffer

object MouseUtils {
    fun setMouseButtonState(mouseButton: Int, held: Boolean) {
        val mouseEvent = MouseEvent()
        // Explicitly specify T as MouseEvent
        ObfuscationReflectionHelper.setPrivateValue(
            MouseEvent::class.java as Class<MouseEvent>,
            mouseEvent,
            mouseButton,
            "button"
        )
        ObfuscationReflectionHelper.setPrivateValue(
            MouseEvent::class.java as Class<MouseEvent>,
            mouseEvent,
            held,
            "buttonstate"
        )
        MinecraftForge.EVENT_BUS.post(mouseEvent)


    }
}