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
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import portablejim.veinminer.api.VeinminerCancelHarvest;

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

    @Instance(ModInfo.MOD_ID)
    public static VeinMinerModSupport instance;

    @Init
    public void init(@SuppressWarnings("UnusedParameters") FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @ForgeSubscribe
    public void makeToolsWork(VeinminerCancelHarvest event) {
        Item currentEquippedItem = event.player.getCurrentEquippedItem().getItem();
        if(Loader.isModLoaded("DartCraft")) {
            if(currentEquippedItem instanceof IBreakable) {
                event.setCanceled(true);
            }
        }
        if(Loader.isModLoaded("TConstruct")) {
            tinkersConstructToolEvent(event);
        }
    }

    private void tinkersConstructToolEvent(VeinminerCancelHarvest event) {
        ItemStack currentItem = event.player.getCurrentEquippedItem();

        if(currentItem == null) {
            return;
        }

        if(!currentItem.hasTagCompound()) {
            return;
        }
        NBTTagCompound toolTags = currentItem.getTagCompound().getCompoundTag("InfiTool");
        if(toolTags == null) {
            return;
        }

        boolean hasLava = toolTags.getBoolean("Lava");
        if(!hasLava) {
            return;
        }

        Random r = event.player.worldObj.rand;
        Block block = Block.blocksList[event.blockId];
        if(block == null || event.blockId < 1 || event.blockId > 4095) {
            return;
        }

        ItemStack smeltStack = new ItemStack(
                block.idDropped(event.blockMetadata, r, 0),
                block.quantityDropped(event.blockMetadata, 0, r),
                block.damageDropped(event.blockMetadata));
        ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(smeltStack);
        if(smeltResult == null) {
            return;
        }

        event.setCanceled(true);
    }
}
