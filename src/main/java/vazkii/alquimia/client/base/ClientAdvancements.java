package vazkii.alquimia.client.base;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import scala.actors.threadpool.Arrays;
import vazkii.alquimia.client.lexicon.LexiconRegistry;
import vazkii.alquimia.common.base.AlquimiaConfig;
import vazkii.alquimia.common.item.ModItems;

public class ClientAdvancements {

	static List<String> doneAdvancements;

	public static void setDoneAdvancements(String[] done, boolean showToast) {
		showToast &= !AlquimiaConfig.disableAdvancementLocking;
		int doneCount = showToast ? (int) LexiconRegistry.INSTANCE.entries.values().stream().filter((e) -> !e.isLocked()).count() : 0;

		doneAdvancements = Arrays.asList(done);
		updateLockStatus();

		int doneCount2 = showToast ? (int) LexiconRegistry.INSTANCE.entries.values().stream().filter((e) -> !e.isLocked()).count() : 0;

		if(doneCount2 > doneCount )
			Minecraft.getMinecraft().getToastGui().add(new LexiconToast());
	}

	public static void updateLockStatus() {
		if(doneAdvancements != null) {
			LexiconRegistry.INSTANCE.entries.values().forEach((e) -> e.updateLockStatus());
			LexiconRegistry.INSTANCE.categories.values().forEach((c) -> c.updateLockStatus(true));
		}
	}

	public static void resetIfNeeded() {
		if(doneAdvancements != null && doneAdvancements.size() > 0)
			setDoneAdvancements(new String[0], false);
	}

	public static boolean hasDone(String advancement) {
		return doneAdvancements != null && doneAdvancements.contains(advancement);
	}

	@SubscribeEvent
	public static void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END && Minecraft.getMinecraft().player == null)
			resetIfNeeded();
	}

	public static class LexiconToast implements IToast {

		@Override
		public Visibility draw(GuiToast toastGui, long delta) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.getTextureManager().bindTexture(TEXTURE_TOASTS);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
			
            toastGui.getMinecraft().fontRenderer.drawString(I18n.format("alquimia.gui.lexicon.toast"), 30, 7, -11534256);
            toastGui.getMinecraft().fontRenderer.drawString(I18n.format("alquimia.gui.lexicon.toast.info"), 30, 17, -16777216);

            RenderHelper.enableGUIStandardItemLighting();
            toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(null, new ItemStack(ModItems.lexicon), 8, 8);
			
			return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
		}

	}

}
