/* This file is part of VeinMiner Mod Support.
 *
 *    VeinMiner is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *    VeinMiner is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with VeinMiner.
 *    If not, see <http://www.gnu.org/licenses/>.
 */

package portablejim.veinminermodsupport;

import bluedart.api.IBreakable;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import portablejim.veinminer.api.VeinminerStartCheck;

import java.util.Random;

import static cpw.mods.fml.common.Mod.Init;
import static cpw.mods.fml.common.Mod.Instance;

/**
 * Main mod class to handle events from Veinminer and cancel events when
 * special mod support is wanted.
 */

@Mod(modid = ModInfo.MOD_ID,
        name = ModInfo.MOD_NAME,
        version = ModInfo.VERSION)
public class VeinMinerModSupport {

    private boolean debugMode = false;

    @Instance(ModInfo.MOD_ID)
    public static VeinMinerModSupport instance;

    @Init
    public void init(@SuppressWarnings("UnusedParameters") FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        ModContainer thisMod = Loader.instance().getIndexedModList().get(ModInfo.MOD_ID);
        if(thisMod != null) {
            String fileName = thisMod.getSource().getName();
            if(fileName.contains("-dev") || !fileName.contains(".jar")) {
                debugMode = true;
                devLog("DEV VERSION");
            }
        }
    }

    private void devLog(String string) {
        if(debugMode) {
            FMLLog.getLogger().info("[" + ModInfo.MOD_ID + "] " + string);
        }
    }

    @ForgeSubscribe
    public void makeToolsWork(VeinminerStartCheck event) {
        Item currentEquippedItem = event.player.getCurrentEquippedItem().getItem();
        if(Loader.isModLoaded("DartCraft")) {
            devLog("Dartcraft detected");
            if(currentEquippedItem instanceof IBreakable) {
                devLog("Canceled breaking");
                event.setCanceled(true);
            }
        }
        if(Loader.isModLoaded("TConstruct")) {
            devLog("Tinkers Construct detected");
            tinkersConstructToolEvent(event);
        }
    }

    private void tinkersConstructToolEvent(VeinminerStartCheck event) {
        ItemStack currentItem = event.player.getCurrentEquippedItem();

        if(currentItem == null) {
            devLog("ERROR: Item is null");
            return;
        }

        if(!currentItem.hasTagCompound()) {
            devLog("ERROR: No NBT data");
            return;
        }
        NBTTagCompound toolTags = currentItem.getTagCompound().getCompoundTag("InfiTool");
        if(toolTags == null) {
            devLog("ERROR: Not Dartcraft Tool");
            return;
        }

        boolean hasLava = toolTags.getBoolean("Lava");
        if(!hasLava) {
            devLog("ERROR: Not lava tool");
            return;
        }

        Random r = event.player.worldObj.rand;
        Block block = Block.blocksList[event.blockId];
        if(block == null || event.blockId < 1 || event.blockId > 4095) {
            devLog("ERROR: Block id out of range");
            return;
        }

        ItemStack smeltStack = new ItemStack(
                block.idDropped(event.blockMetadata, r, 0),
                block.quantityDropped(event.blockMetadata, 0, r),
                block.damageDropped(event.blockMetadata));
        ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(smeltStack);
        if(smeltResult == null) {
            devLog("ERROR: No Smelt result");
            return;
        }

        devLog("Canceling event");
        event.setCanceled(true);
    }
}
