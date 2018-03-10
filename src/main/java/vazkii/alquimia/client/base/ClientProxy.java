package vazkii.alquimia.client.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.alquimia.client.handler.LexiconRightClickHandler;
import vazkii.alquimia.client.handler.MultiblockVisualizationHandler;
import vazkii.alquimia.client.lexicon.LexiconRegistry;
import vazkii.alquimia.common.base.CommonProxy;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		LexiconRegistry.INSTANCE.init();
		PersistentData.setup(event.getSuggestedConfigurationFile().getParentFile().getParentFile());
		
		MinecraftForge.EVENT_BUS.register(ClientAdvancements.class);
		MinecraftForge.EVENT_BUS.register(MultiblockVisualizationHandler.class);
		MinecraftForge.EVENT_BUS.register(LexiconRightClickHandler.class);
	}
	
	@Override
	public void onConfigChanged(boolean firstChange) {
		if(!firstChange)
			LexiconRegistry.INSTANCE.reloadLexiconRegistry();
	}
	
}
