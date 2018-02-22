package vazkii.alquimia.client.lexicon.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.translation.I18n;
import vazkii.alquimia.client.lexicon.gui.button.GuiButtonLexiconResize;

public class GuiLexiconWriter extends GuiLexicon {

	LexiconTextRenderer text, editableText;
	GuiTextField textfield;
	
	private static String savedText = "";
	private static boolean drawHeader;

	@Override
	public void initGui() {
		super.initGui();
		
		text = new LexiconTextRenderer(this, I18n.translateToLocal("alquimia.gui.lexicon.editor.info"), LEFT_PAGE_X, TOP_PADDING + 20);
		textfield = new GuiTextField(0, fontRenderer, 10, FULL_HEIGHT - 40, PAGE_WIDTH, 20);
		textfield.setMaxStringLength(Integer.MAX_VALUE);
		textfield.setText(savedText);
		
		buttonList.add(new GuiButtonLexiconResize(this, bookLeft + 115, bookTop + PAGE_HEIGHT - 36, false));
		
		Keyboard.enableRepeatEvents(true);
		refreshText();
	}
	
	@Override
	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(mouseX, mouseY, partialTicks);
		
		drawCenteredStringNoShadow(I18n.translateToLocal("alquimia.gui.lexicon.editor"), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, 0x333333);
		drawSeparator(LEFT_PAGE_X, TOP_PADDING + 12);

		if(drawHeader) {
			drawCenteredStringNoShadow(I18n.translateToLocal("alquimia.gui.lexicon.editor.mock_header"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, 0x333333);
			drawSeparator(RIGHT_PAGE_X, TOP_PADDING + 12);
		}
		
		textfield.drawTextBox();
		text.render(mouseX, mouseY);
		editableText.render(mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		textfield.mouseClicked(mouseX - bookLeft, mouseY - bookTop, mouseButton);
		
		text.click(mouseX, mouseY, mouseButton);
		editableText.click(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		textfield.textboxKeyTyped(typedChar, keyCode);
		refreshText();
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		if(button instanceof GuiButtonLexiconResize) {
			drawHeader = !drawHeader;
			refreshText();
		}
	}
	
	public void refreshText() {
		int yPos = TOP_PADDING + (drawHeader ? 22 : 0);
		
		savedText = textfield.getText();
		try {
			editableText = new LexiconTextRenderer(this, savedText, RIGHT_PAGE_X, yPos);
		} catch(Throwable e) {
			editableText = new LexiconTextRenderer(this, "[ERROR]", RIGHT_PAGE_X, yPos);
		}
	}
}
