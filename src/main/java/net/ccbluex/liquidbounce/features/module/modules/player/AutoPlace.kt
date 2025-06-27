
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition
import org.lwjgl.input.Mouse

object AutoPlace : Module("AutoPlace", Category.PLAYER, subjective = true, gameDetecting = false, hideModule = false) {
    private val delayValue = FloatValue("Delay", 0f, 0f..4f)
    private val mouseDownValue = BoolValue("MouseDown", false)
    private val clickTimer = MSTimer()
    private var clickCount = 0
    private var lastMovingObject: MovingObjectPosition? = null
    private var lastBlockPos: BlockPos? = null

    @EventTarget
    fun onRender(event: Render3DEvent) {
        val thePlayer = mc.thePlayer ?: return
        val theWorld = mc.theWorld ?: return

        if (mc.currentScreen != null || thePlayer.capabilities.isFlying) return

        val itemStack = thePlayer.heldItem
        if (itemStack?.item !is ItemBlock) return

        val movingObject = mc.objectMouseOver ?: return
        if (movingObject.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK ||
            movingObject.sideHit == EnumFacing.UP || movingObject.sideHit == EnumFacing.DOWN ||
            movingObject.sideHit !in listOf(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST)
        ) return

        if (lastMovingObject != null && clickCount < delayValue.get()) {
            clickCount++
            return
        }

        lastMovingObject = movingObject
        val blockPos = movingObject.blockPos

        if (lastBlockPos?.let { pos ->
                pos.x == blockPos.x && pos.y == blockPos.y && pos.z == blockPos.z
            } == true) return

        val block = theWorld.getBlockState(blockPos).block
        if (block == null || block == Blocks.air || block is BlockLiquid || (mouseDownValue.get() && !Mouse.isButtonDown(1))) return

        if (clickTimer.hasTimePassed(25)) {
            if (mc.playerController.onPlayerRightClick(
                    thePlayer,
                    theWorld,
                    itemStack as ItemStack?,
                    blockPos,
                    movingObject.sideHit,
                    movingObject.hitVec
                )
            ) {
                MouseUtils.setMouseButtonState(1, true)
                thePlayer.swingItem()
                mc.effectRenderer.addBlockHitEffects(blockPos, movingObject.sideHit)
                MouseUtils.setMouseButtonState(1, false)
                lastBlockPos = blockPos
                clickCount = 0
                clickTimer.reset()
            }
        }
    }
}