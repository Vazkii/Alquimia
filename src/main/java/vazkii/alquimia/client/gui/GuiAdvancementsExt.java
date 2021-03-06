package vazkii.alquimia.client.gui;

import java.io.IOException;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.util.ResourceLocation;
import vazkii.alquimia.client.base.ClientAdvancements;
import vazkii.alquimia.common.lib.LibMisc;

public class GuiAdvancementsExt extends GuiScreenAdvancements {

	GuiScreen parent;
	
	public GuiAdvancementsExt(ClientAdvancementManager manager, GuiScreen parent) {
		super(manager);
		this.parent = parent;
		
		Advancement start = manager.getAdvancementList().getAdvancement(new ResourceLocation(LibMisc.MOD_ID, "root"));
		if(start != null && ClientAdvancements.hasDone(start.getId().toString()))
			manager.setSelectedTab(start, false);
	}

	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == mc.gameSettings.keyBindAdvancements.getKeyCode() || keyCode == 1)
            mc.displayGuiScreen(parent);
        else super.keyTyped(typedChar, keyCode);
    }
	
}
