package vazkii.alquimia.common.block.tile;

import java.util.function.BiConsumer;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import vazkii.alquimia.common.block.ModBlocks;
import vazkii.alquimia.common.block.interf.IAutomaton;
import vazkii.alquimia.common.block.interf.IAutomatonHead;
import vazkii.alquimia.common.item.interf.IAutomatonHeadItem;
import vazkii.alquimia.common.item.interf.IAutomatonInstruction;
import vazkii.arl.block.tile.TileMod;
import vazkii.arl.block.tile.TileSimpleInventory;

public class TileAutomaton extends TileSimpleInventory implements IAutomaton, ITickable {

	// TODO increase to 12
	public static final int INSTRUCTION_SLOTS = 6;
	public static final int INSTRUCTION_TIME = 10;
	
	private static final String TAG_HEAD_DATA = "headData";
	private static final String TAG_FACING = "facing";
	private static final String TAG_PREV_FACING = "prevFacing";
	private static final String TAG_UP = "up";
	private static final String TAG_PREV_UP = "prevUp";
	private static final String TAG_ROTATION = "rotation";
	private static final String TAG_EXECUTING = "executing";
	private static final String TAG_CLOCK = "clock";
	private static final String TAG_SELECTION = "selection";

	protected IAutomatonHead head = null;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected EnumFacing prevFacing = EnumFacing.NORTH;
	protected boolean up = false;
	protected boolean prevUp = false;
	protected Rotation rotation = Rotation.NONE;
	protected boolean executing = false;
	protected int clock = 0;
	protected int selection = 1;

	@Override
	public void update() {
		ItemStack stack = getStackInSlot(0);
		if(stack.isEmpty()) {
			runInHead(IAutomatonHead::onRemoved);
			head = null;
		} else if(head == null && stack.getItem() instanceof IAutomatonHeadItem) {
			head = ((IAutomatonHeadItem) stack.getItem()).provideHead(stack);
			runInHead(IAutomatonHead::onAttached);
		}

		runInHead(IAutomatonHead::onTicked);
		
		if(!isEnabled() || getHead() == null) {
			clock = INSTRUCTION_TIME;
			selection = 1;
		} else {
			if(clock >= INSTRUCTION_TIME - 1)
				executeCurrentInstruction();
			else clock++;
		}
	}

	protected void startExecuting() {
		executing = true;
		clock = 0;
		
		ItemStack stack;
		do {
			selection++;
			if(selection >= getSizeInventory()) {
				selection = 1;
				break;
			}
			
			stack = getStackInSlot(selection);
		} while(!(stack.getItem() instanceof IAutomatonInstruction));
	}

	// TODO handle case where power goes out halfway through
	protected void executeCurrentInstruction() {
		if(prevUp != up) {
			prevUp = up;
			runInHead(IAutomatonHead::onEngageStatusEnd);
		}
		
		if(prevFacing != facing) {
			prevFacing = facing;
			runInHead(IAutomatonHead::onRotateEnd);
		}
			
		executing = false;
		boolean executed = false;
		
		ItemStack stack = getStackInSlot(selection);
		if(stack.getItem() instanceof IAutomatonInstruction) {
			((IAutomatonInstruction) stack.getItem()).run(stack, this);
			executed = true;
		}
		
		if(executed)
			startExecuting();
		else selection = 1;
	}
	
	@Override
	public void writeSharedNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeSharedNBT(par1nbtTagCompound);
		
		NBTTagCompound cmp = new NBTTagCompound();
		runInHead((h, a) -> h.writeToNBT(a, cmp));
		par1nbtTagCompound.setTag(TAG_HEAD_DATA, cmp);
		par1nbtTagCompound.setInteger(TAG_FACING, facing.ordinal());
		par1nbtTagCompound.setInteger(TAG_PREV_FACING, prevFacing.ordinal());
		par1nbtTagCompound.setBoolean(TAG_UP, up);
		par1nbtTagCompound.setBoolean(TAG_PREV_UP, prevUp);
		par1nbtTagCompound.setInteger(TAG_ROTATION, rotation.ordinal());
		par1nbtTagCompound.setBoolean(TAG_EXECUTING, executing);
		par1nbtTagCompound.setInteger(TAG_CLOCK, clock);
		par1nbtTagCompound.setInteger(TAG_SELECTION, selection);
	}
	
	@Override
	public void readSharedNBT(NBTTagCompound par1nbtTagCompound) {
		super.readSharedNBT(par1nbtTagCompound);
		
		getHead(); // cause the head object to instantiate 
		
		NBTTagCompound cmp = par1nbtTagCompound.getCompoundTag(TAG_HEAD_DATA);
		runInHead((h, a) -> h.readFromNBT(a, cmp));
		facing = EnumFacing.values()[par1nbtTagCompound.getInteger(TAG_FACING)];
		prevFacing = EnumFacing.values()[par1nbtTagCompound.getInteger(TAG_PREV_FACING)];
		up = par1nbtTagCompound.getBoolean(TAG_UP);
		prevUp = par1nbtTagCompound.getBoolean(TAG_PREV_UP);
		rotation = Rotation.values()[par1nbtTagCompound.getInteger(TAG_ROTATION)];
		executing = par1nbtTagCompound.getBoolean(TAG_EXECUTING);
		clock = par1nbtTagCompound.getInteger(TAG_CLOCK);
		selection = par1nbtTagCompound.getInteger(TAG_SELECTION);
	}

	@Override
	public IAutomatonHead getHead() {
		return head;
	}

	@Override
	public EnumFacing getCurrentFacing() {
		return facing;
	}

	@Override
	public EnumFacing getPreviousFacing() {
		return prevFacing;
	}

	@Override
	public Rotation getCurrentRotation() {
		return rotation;
	}

	@Override
	public void rotate(Rotation rotation) {
		if(!isExecuting()) {
			this.rotation = rotation;
			prevFacing = facing;
			EnumFacing newFacing = rotation.rotate(facing);
			if(prevFacing != newFacing) 
				runInHead(IAutomatonHead::onRotateStart);
			
			facing = newFacing;
		}
	}

	@Override
	public boolean isUp() {
		return up;
	}

	@Override
	public boolean wasUp() {
		return prevUp;
	}

	@Override
	public void setUp(boolean up) {
		if(!isExecuting()) {
			prevUp = this.up;
			if(prevUp != up)
				runInHead(IAutomatonHead::onEngageStatusStart);
			this.up = up;
		}
	}
	
	public void runInHead(BiConsumer<IAutomatonHead, IAutomaton> func) {
		if(head != null)
			func.accept(head, this);
	}

	@Override
	public boolean isEnabled() {
		return getWorld().getBlockState(getPos().down()).getBlock() == Blocks.GOLD_BLOCK; // TODO redstone
	}

	@Override
	public boolean isExecuting() {
		return executing;
	}

	@Override
	public int getInstructionTime() {
		return clock;
	}
	
	public int getSelection() {
		return selection;
	}

	@Override
	public int getSizeInventory() {
		return INSTRUCTION_SLOTS + 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isAutomationEnabled() {
		return false;
	}

}