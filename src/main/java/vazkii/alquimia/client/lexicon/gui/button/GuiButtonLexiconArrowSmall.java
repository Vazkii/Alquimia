package vazkii.alquimia.client.lexicon.gui.button;

import com.google.common.base.Supplier;

import net.minecraft.util.text.translation.I18n;
import vazkii.alquimia.client.lexicon.gui.GuiLexicon;

public class GuiButtonLexiconArrowSmall extends GuiButtonLexicon {

	public final boolean left;
	
	public GuiButtonLexiconArrowSmall(GuiLexicon parent, int x, int y, boolean left, Supplier<Boolean> displayCondition) {
		super(parent, x, y, 272, left ? 27 : 20, 5, 7, displayCondition,
				I18n.translateToLocal(left ? "alquimia.gui.lexicon.button.prev_page" : "alquimia.gui.lexicon.button.next_page"));
		this.left = left;
	}

}
