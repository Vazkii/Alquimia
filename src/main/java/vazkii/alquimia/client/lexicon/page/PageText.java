package vazkii.alquimia.client.lexicon.page;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import vazkii.alquimia.client.lexicon.LexiconPage;
import vazkii.alquimia.client.lexicon.gui.GuiLexicon;
import vazkii.alquimia.client.lexicon.gui.GuiLexiconEntry;
import vazkii.alquimia.client.lexicon.gui.LexiconTextRenderer;
import vazkii.alquimia.client.lexicon.page.abstr.PageWithText;

public class PageText extends PageWithText {
	
	@Override
	public int getTextHeight() {
		return pageNum == 0 ? 22 : 0;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		super.render(mouseX, mouseY, pticks);
		
		if(pageNum == 0) {
			boolean adv = mc.gameSettings.advancedItemTooltips;
			parent.drawCenteredStringNoShadow(parent.getEntry().getName(), GuiLexicon.PAGE_WIDTH / 2, adv ? -3 : 0, 0x333333);
			parent.drawSeparator(0, 12);
			
			if(adv) {
				ResourceLocation res = parent.getEntry().getResource();
				GlStateManager.scale(0.5F, 0.5F, 1F);
				parent.drawCenteredStringNoShadow(res.toString(), GuiLexicon.PAGE_WIDTH, 12, 0x333333);
				GlStateManager.scale(2F, 2F, 1F);
			}
		}
	}

}
