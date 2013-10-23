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
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import portablejim.veinminer.api.VeinminerCancelHarvest;

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
        if(Loader.isModLoaded("DartCraft")) {
            Item currentEquippedItem = event.player.getCurrentEquippedItem().getItem();
            if(currentEquippedItem instanceof IBreakable) {
                event.setCanceled(true);
            }
        }
    }
}
