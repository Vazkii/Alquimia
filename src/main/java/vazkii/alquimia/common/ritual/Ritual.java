package vazkii.alquimia.common.ritual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.alquimia.common.lib.LibMisc;
import vazkii.arl.recipe.RecipeHandler;

public abstract class Ritual {

	public final ResourceLocation name;
	public final RitualType type;
	public final List<Ingredient> ingredients;
	
	public Ritual(String name, RitualType type, Collection<Object> ingredients) {
		this(new ResourceLocation(LibMisc.MOD_ID, name), type, ingredients);
	}
	
	public Ritual(ResourceLocation name, RitualType type, Collection<Object> ingredients) {
		this.name = name;
		this.type = type;
		this.ingredients = ingredients.stream().map(RecipeHandler::asIngredient).collect(Collectors.toCollection(ArrayList::new));
	}
	
	public boolean matches(List<ItemStack> stacks) {
		List<ItemStack> copyStacks = new ArrayList(stacks);
		List<Ingredient> copyIngredients = new ArrayList(ingredients);
		
		if(copyStacks.size() != copyIngredients.size())
			return false;
		
		while(!copyStacks.isEmpty()) fulfil: {
			ItemStack stack = copyStacks.get(0);
			for(Ingredient ingr : copyIngredients)
				if(ingr.apply(stack)) {
					copyIngredients.remove(ingr);
					copyStacks.remove(0);
					break fulfil;
				}
			
			return false;
		}
		
		return true;
	}
	
	public boolean canRun(World world, BlockPos center) {
		return true;
	}
	
	public abstract boolean run(World world, BlockPos pos);
	
	public boolean tick(World world, BlockPos pos, int time) {
		return false; 
	}
	
	@SideOnly(Side.CLIENT)
	public void render(World world, BlockPos pos, int time, float pticks) {
		// NO-OP
	}
	
}
