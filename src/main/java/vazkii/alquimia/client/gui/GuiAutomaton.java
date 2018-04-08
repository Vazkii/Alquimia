package vazkii.alquimia.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import vazkii.alquimia.common.block.ModBlocks;
import vazkii.alquimia.common.block.tile.TileAutomaton;
import vazkii.alquimia.common.container.ContainerAutomaton;
import vazkii.alquimia.common.lib.LibMisc;

// TODO make generic GuiInventory type
public class GuiAutomaton extends GuiContainer {

    private static final ResourceLocation AUTOMATON_TEXTURE = new ResourceLocation(LibMisc.MOD_ID, "textures/gui/automaton.png");
	
    protected final InventoryPlayer playerInv;
	protected final TileAutomaton automaton;
	
	public GuiAutomaton(InventoryPlayer playerInv, TileAutomaton automaton) {
		super(new ContainerAutomaton(playerInv, automaton));
		this.playerInv = playerInv;
		this.automaton = automaton;
	}

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
	
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = automaton.getDisplayName().getUnformattedText();
        fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
        
        int x = 40 + automaton.getSelection() * 18;
        int y = 54;         
        fontRenderer.drawString("^", x, y, 4210752);
        
        fontRenderer.drawString(automaton.getCurrentFacing().getName() + " (" + automaton.isUp() + ")", 80, 64, 4210752);
    }
    
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(AUTOMATON_TEXTURE);
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
	}

}
