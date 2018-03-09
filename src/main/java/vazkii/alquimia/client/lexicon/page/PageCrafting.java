package vazkii.alquimia.client.lexicon.page;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.crafting.IShapedRecipe;
import vazkii.alquimia.client.lexicon.LexiconEntry;
import vazkii.alquimia.client.lexicon.gui.GuiLexicon;
import vazkii.alquimia.client.lexicon.page.abstr.PageDoubleRecipe;

public class PageCrafting extends PageDoubleRecipe<IRecipe> {
	
	@Override
	protected void drawRecipe(IRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.renderEngine.bindTexture(GuiLexicon.CRAFTING_TEXTURE);
		GlStateManager.enableBlend();
		parent.drawModalRectWithCustomSizedTexture(recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 128);
		
		boolean shaped = recipe instanceof IShapedRecipe;
		if(!shaped) {
			int iconX = recipeX + 62;
			int iconY = recipeY + 2;
			parent.drawModalRectWithCustomSizedTexture(iconX, iconY, 0, 64, 11, 11, 128, 128);
			if(parent.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11))
				parent.setTooltip(I18n.translateToLocal("alquimia.gui.lexicon.shapeless"));
		}

		parent.drawCenteredStringNoShadow(getTitle(second), GuiLexicon.PAGE_WIDTH / 2, recipeY - 10, 0x333333);
		
		renderItem(recipeX + 79, recipeY + 22, mouseX, mouseY, recipe.getRecipeOutput());
		
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		int wrap = 3;
		if(shaped)
			wrap = ((IShapedRecipe) recipe).getRecipeWidth();
		
		for(int i = 0; i < ingredients.size(); i++)
			renderIngredient(recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
	}
	
	protected IRecipe loadRecipe(LexiconEntry entry, String loc) {
		if(loc == null)
			return null;
		
		IRecipe tempRecipe = CraftingManager.getRecipe(new ResourceLocation(loc));
		if(tempRecipe != null)
			entry.addRelevantStack(tempRecipe.getRecipeOutput(), pageNum);
		return tempRecipe;
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

	@Override
	protected ItemStack getRecipeOutput(IRecipe recipe) {
		return recipe.getRecipeOutput();
	}

}
