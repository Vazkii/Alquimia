package vazkii.alquimia.client.lexicon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import vazkii.alquimia.client.lexicon.gui.GuiLexiconEntry;
import vazkii.alquimia.common.base.AlquimiaConfig;
import vazkii.alquimia.common.util.SerializationUtil;

public abstract class LexiconPage {

	protected transient Minecraft mc;
	protected transient FontRenderer fontRenderer;
	protected transient GuiLexiconEntry parent;
	protected transient int pageNum;
	private transient List<GuiButton> buttons;
	public transient int left, top;
	
	String type, flag;
	
	public void build(LexiconEntry entry, int pageNum) {
		this.pageNum = pageNum;
	}
	
	public void onDisplayed(GuiLexiconEntry parent, int left, int top) { 
		mc = parent.mc;
		fontRenderer = mc.fontRenderer;
		this.parent = parent;
		this.left = left;
		this.top = top;
		buttons = new ArrayList();
	}
	
	public void onHidden(GuiLexiconEntry parent) {
		parent.getButtonList().removeAll(buttons);
	}
	
	protected void adddButton(GuiButton button) {
		button.x += (parent.bookLeft + left);
		button.y += (parent.bookTop + top);
		buttons.add(button);
		parent.getButtonList().add(button);
	}
	
	public void render(int mouseX, int mouseY, float pticks) { }
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) { }
	protected void onButtonClicked(GuiButton button) { }
	
	public final boolean interceptButton(GuiButton button) {
		if(buttons.contains(button)) {
			onButtonClicked(button);
			return true;
		}
		
		return false;
	}
	
	public void renderItem(int x, int y, int mouseX, int mouseY, ItemStack stack) {
		if(stack == null || stack.isEmpty())
			return;
		
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		mc.getRenderItem().renderItemOverlays(fontRenderer, stack, x, y);
		
		if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16))
			parent.setTooltipStack(stack);
	}
	
	public void renderIngredient(int x, int y, int mouseX, int mouseY, Ingredient ingr) {
		ItemStack[] stacks = ingr.getMatchingStacks();
		if(stacks.length > 0)
			renderItem(x, y, mouseX, mouseY, stacks[(parent.ticksInBook / 20) % stacks.length]);
	}
	
	public boolean canAdd() {
		return flag == null || flag.isEmpty() || AlquimiaConfig.getConfigFlag(flag);
	}
	
	public static class LexiconPageAdapter implements JsonDeserializer<LexiconPage> {
		
		@Override
		public LexiconPage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	        JsonObject obj = json.getAsJsonObject();
	        JsonPrimitive prim = (JsonPrimitive) obj.get("type");
	        String type = prim.getAsString();
	        Class<? extends LexiconPage> clazz = LexiconRegistry.INSTANCE.pageTypes.get(type);
	        if(clazz == null)
	        	return null;
	        
	        return SerializationUtil.RAW_GSON.fromJson(json, clazz);
		}
		
	}
	
}
