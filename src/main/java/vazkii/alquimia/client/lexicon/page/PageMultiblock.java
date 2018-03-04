package vazkii.alquimia.client.lexicon.page;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import vazkii.alquimia.client.lexicon.LexiconEntry;
import vazkii.alquimia.client.lexicon.LexiconPage;
import vazkii.alquimia.client.lexicon.gui.GuiLexicon;
import vazkii.alquimia.client.lexicon.gui.GuiLexiconEntry;
import vazkii.alquimia.client.lexicon.gui.LexiconTextRenderer;
import vazkii.alquimia.common.multiblock.ModMultiblocks;
import vazkii.alquimia.common.multiblock.Multiblock;
import vazkii.alquimia.common.multiblock.Multiblock.StateMatcher;
import vazkii.arl.util.ClientTicker;

public class PageMultiblock extends LexiconPage {

	String name;
	String text;
	String multiblock;

	transient LexiconTextRenderer textRender;
	transient Multiblock multiblockObj;

	@Override
	public void build(LexiconEntry entry, int pageNum) {
		multiblockObj = ModMultiblocks.MULTIBLOCKS.get(new ResourceLocation(multiblock));
	}

	@Override
	public void onDisplayed(GuiLexiconEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		textRender = new LexiconTextRenderer(parent, text, 0, 115);
	}

	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		int x = GuiLexicon.PAGE_WIDTH / 2 - 53;
		int y = 7;
		GlStateManager.enableBlend();
		GuiLexicon.drawFromTexture(x, y, 405, 149, 106, 106);
		
		textRender.render(mouseX, mouseY);
		parent.drawCenteredStringNoShadow(name, GuiLexicon.PAGE_WIDTH / 2, 0, 0x333333);

		if(multiblockObj != null)
			renderMultiblock();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		textRender.click(mouseX, mouseY, mouseButton);
	}

	private void renderMultiblock() {
		float maxX = 90;
		float maxY = 90;
		float diag = (float) Math.sqrt(multiblockObj.sizeX * multiblockObj.sizeX + multiblockObj.sizeZ * multiblockObj.sizeZ);
		float height = multiblockObj.sizeY;
		float scaleX = maxX / diag;
		float scaleY = maxY / height;
		float scale = -Math.min(scaleX, scaleY);
		
		int xPos = GuiLexicon.PAGE_WIDTH / 2;
		int yPos = 60;
		GlStateManager.pushMatrix();
		GlStateManager.translate(xPos, yPos, 100);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(-(float) multiblockObj.sizeX / 2, -(float) multiblockObj.sizeY / 2, 0);

		GlStateManager.rotate(-30F, 1F, 0F, 0F);
		
		float offX = (float) -multiblockObj.sizeX / 2;
		float offZ = (float) -multiblockObj.sizeZ / 2 + 1;

		float time = parent.ticksInBook * 0.5F;
		if(!GuiScreen.isShiftKeyDown())
			time += ClientTicker.partialTicks;
		GlStateManager.translate(-offX, 0, -offZ);
		GlStateManager.rotate(time, 0F, 1F, 0F);
		GlStateManager.rotate(45F, 0F, 1F, 0F);
		GlStateManager.translate(offX, 0, offZ);
		
		for(int x = 0; x < multiblockObj.sizeX; x++)
			for(int y = 0; y < multiblockObj.sizeY; y++)
				for(int z = 0; z < multiblockObj.sizeZ; z++)
					renderElement(multiblockObj, x, y, z);
		GlStateManager.popMatrix();
	}

	private void renderElement(Multiblock mb, int x, int y, int z) {
		StateMatcher matcher = mb.stateTargets[x][y][z];
		IBlockState state = matcher.displayState;
		if(state == null)
			return;

//		if(x != 0 || y != 0 || z != 0)
//			return;

		BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		brd.renderBlockBrightness(state, 1.0F);
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

}
