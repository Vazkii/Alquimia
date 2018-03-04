package vazkii.alquimia.client.lexicon.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import vazkii.alquimia.client.lexicon.LexiconEntry;
import vazkii.alquimia.client.lexicon.LexiconPage;
import vazkii.alquimia.client.lexicon.LexiconRegistry;
import vazkii.alquimia.client.lexicon.gui.GuiLexicon;
import vazkii.alquimia.client.lexicon.gui.GuiLexiconEntry;
import vazkii.alquimia.client.lexicon.gui.LexiconTextRenderer;
import vazkii.alquimia.client.lexicon.gui.button.GuiButtonEntry;
import vazkii.alquimia.common.lib.LibMisc;

public class PageRelations extends LexiconPage {

	List<String> entries;
	String text;
	String title;

	transient List<LexiconEntry> entryObjs;
	transient LexiconTextRenderer textRender;

	@Override
	public void build(LexiconEntry entry, int pageNum) {
		super.build(entry, pageNum);

		entryObjs = entries.stream()
				.map((s) -> s.contains(":") ? new ResourceLocation(s) : new ResourceLocation(LibMisc.MOD_ID, s))
				.map((res) -> LexiconRegistry.INSTANCE.entries.get(res))
				.map((e) -> e == null ? entry : e) // map missing entries to ourselves to prevent crashes
				.collect(Collectors.toList());
	}
	
	@Override
	public void onDisplayed(GuiLexiconEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		textRender = new LexiconTextRenderer(parent, text, 0, 22 + entryObjs.size() * 11);
		
		List<LexiconEntry> displayedEntries = new ArrayList(entryObjs);
		Collections.sort(displayedEntries);
		for(int i = 0; i < displayedEntries.size(); i++) {
			GuiButton button = new GuiButtonEntry(parent, 0, 20 + i * 11, displayedEntries.get(i), i);
			adddButton(button);
		}
	}
	
	@Override
	protected void onButtonClicked(GuiButton button) {
		if(button instanceof GuiButtonEntry)
			GuiLexicon.displayLexiconGui(new GuiLexiconEntry(((GuiButtonEntry) button).getEntry()), true);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		parent.drawCenteredStringNoShadow(title.isEmpty() ? I18n.translateToLocal("alquimia.gui.lexicon.relations") : title, GuiLexicon.PAGE_WIDTH / 2, 0, 0x333333);
		parent.drawSeparator(0, 12);
		
		textRender.render(mouseX, mouseY);
	}

}
