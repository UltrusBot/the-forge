package com.sussysyrup.theforge.blocks.alloysmeltery.entity;

import com.sussysyrup.theforge.api.block.ForgeAlloySmelteryRegistry;
import com.sussysyrup.theforge.api.fluid.FluidProperties;
import com.sussysyrup.theforge.api.fluid.ForgeMoltenFluidRegistry;
import com.sussysyrup.theforge.api.fluid.ForgeSmelteryResourceRegistry;
import com.sussysyrup.theforge.api.fluid.SmelteryResource;
import com.sussysyrup.theforge.api.transfer.MultiVariantStorage;
import com.sussysyrup.theforge.blocks.alloysmeltery.AlloySmelteryControllerBlock;
import com.sussysyrup.theforge.registry.BlocksRegistry;
import com.sussysyrup.theforge.screen.AlloySmelteryInvScreenHandler;
import com.sussysyrup.theforge.util.FluidUtil;
import com.sussysyrup.theforge.util.InventoryUtil;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AlloySmelteryControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    boolean valid = false;
    int count20 = 0;
    int count5 = 0;

    int oldHeight = 0;
    int oldWidth = 0;
    int oldLength = 0;

    public Inventory itemInventory = new SimpleInventory(1);
    Inventory oldItemInventory;
    public List<Integer> smeltTicks = new ArrayList<>();
    public int itemPageShift = 0;

    public final MultiVariantStorage<FluidVariant> fluidStorage = new MultiVariantStorage<>(){
        @Override
        public void onFinalCommit() {
            markDirty();
        }
    };

    public AlloySmelteryControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlocksRegistry.ALLOY_SMELTERY_CONTROLLER_BLOCK_ENTITY, blockPos, blockState);

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("INVSIZE_KEY", itemInventory.size());
        InventoryUtil.writeNbt(nbt, itemInventory, true);
        nbt.putIntArray("SMELTTICKSLIST", smeltTicks);
        nbt.putInt("PAGESHIFTITEM", itemPageShift);

        nbt.putInt("HEIGHT", oldHeight);
        nbt.putInt("WIDTH", oldWidth);
        nbt.putInt("LENGTH", oldLength);

        FluidUtil.writeNbt(nbt, fluidStorage);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        int size = nbt.getInt("INVSIZE_KEY");
        itemInventory = new SimpleInventory(size);
        InventoryUtil.readNbt(nbt, itemInventory);
        oldItemInventory = itemInventory;
        smeltTicks = new ArrayList<>(Arrays.stream(nbt.getIntArray("SMELTTICKSLIST")).boxed().toList());
        itemPageShift = nbt.getInt("PAGESHIFTITEM");

        oldHeight = nbt.getInt("HEIGHT");
        oldWidth = nbt.getInt("WIDTH");
        oldLength = nbt.getInt("LENGTH");

        FluidUtil.readNbt(nbt, fluidStorage);
    }

    public static <E extends AlloySmelteryControllerBlockEntity> void clientTicker(World world, BlockPos blockPos, BlockState blockState, E e) {
    }

    public static <E extends AlloySmelteryControllerBlockEntity> void serverTicker(World world, BlockPos blockPos, BlockState blockState, E e) {
        e.count20++;

        if(e.count20 >= 20)
        {
            e.count20 = 0;
            e.structCheck(world, blockPos);
            e.markDirty();
        }
        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_LISTENERS);

        if(e.isValid())
        {
            if(e.count5 >= 5) {
                blockState = blockState.with(AlloySmelteryControllerBlock.LIT, true);
                world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
                e.markDirty();
            }

            e.itemSmeltTick();
        }
        else
        {
            if(e.count5 >= 5) {
                blockState = blockState.with(AlloySmelteryControllerBlock.LIT, false);
                world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
                e.markDirty();
            }
        }

        e.count5++;
    }

    @javax.annotation.Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    protected void structCheck(World world, BlockPos pos) {

        BlockPos scanPos = pos;

        Direction direction = world.getBlockState(pos).get(AlloySmelteryControllerBlock.FACING);
        Direction relativeRight = direction.rotateYClockwise();
        Direction relativeLeft = direction.rotateYCounterclockwise();

        Direction directionOpposite = direction.getOpposite();

        int distanceL = 0;
        int distanceR = 0;

        int remaining = 6;

        int sideLength1 = 0;
        int sideLength2 = 0;
        int sideLength3 = 0;
        int sideLength4 = 0;

        boolean continueScan;

        MAINLOOP : for (int height = 0; height < 16; height++)
        {
            if(height == 0)
            {
                //scan left of controller
                continueScan = true;
                while(continueScan)
                {
                    scanPos = scanPos.add(relativeLeft.getVector());

                    if(remaining == 1)
                    {
                        continueScan = false;
                        continue;
                    }

                    if(!checkBlockProper(world, scanPos))
                    {
                        continueScan = false;
                        continue;
                    }
                    distanceL++;
                    remaining--;
                }
                
                //scan right of controller
                continueScan = true;
                scanPos = pos;
                while(continueScan)
                {
                    scanPos = scanPos.add(relativeRight.getVector());

                    if(remaining == 0)
                    {
                        continueScan = false;
                        continue;
                    }

                    if(!checkBlockProper(world, scanPos))
                    {
                        continueScan = false;
                        continue;
                    }
                    distanceR++;
                    remaining--;
                }
                sideLength1 = distanceL + distanceR + 1;

                //scan relative right side
                continueScan = true;
                while (continueScan)
                {
                    scanPos = scanPos.add(direction.getVector());

                    if(sideLength2 == 7)
                    {
                        continueScan = false;
                        continue;
                    }

                    if(!checkBlockProper(world, scanPos))
                    {
                        continueScan = false;
                        continue;
                    }
                    sideLength2++;
                }

                //scan opposite side
                continueScan = true;
                while (continueScan)
                {
                    scanPos = scanPos.add(relativeLeft.getVector());

                    if(sideLength3 == 7)
                    {
                        continueScan = false;
                        continue;
                    }

                    if(!checkBlockProper(world, scanPos))
                    {
                        continueScan = false;
                        continue;
                    }
                    sideLength3++;
                }

                if(sideLength1 != sideLength3)
                {
                    failedScan();
                    return;
                }

                //prepare pos for continued scan
                scanPos = pos;
                for(int i = 0; i < distanceL + 1; i++)
                {
                    scanPos = scanPos.add(relativeLeft.getVector());
                }

                //scan relative left side
                continueScan = true;
                while (continueScan)
                {
                    scanPos = scanPos.add(direction.getVector());

                    if(sideLength4 == 7)
                    {
                        continueScan = false;
                        continue;
                    }

                    if(!checkBlockProper(world, scanPos))
                    {
                        continueScan = false;
                        continue;
                    }
                    sideLength4++;
                }

                if(sideLength2 != sideLength4)
                {
                    failedScan();
                    return;
                }

                //Floor scan
                for(int length = 0; length < sideLength2; length++)
                {
                    scanPos = pos.add(0, -1, 0);
                    for(int i = 0; i < distanceL + 1; i++)
                    {
                        scanPos = scanPos.add(relativeLeft.getVector());
                    }
                    for(int i = 0; i < length + 1; i++) {
                        scanPos = scanPos.add(direction.getVector());
                    }

                    for(int width = 0; width < sideLength1; width++)
                    {
                        scanPos = scanPos.add(relativeRight.getVector());
                        if(!checkBlockFloor(world, scanPos))
                        {
                            failedScan();
                            return;
                        }
                    }
                }
            }
            else
            {
                for(int iter = 0; iter < 2; iter++) {
                    //prepare
                    scanPos = pos.add(0, height, 0);
                    for (int i = 0; i < distanceL + 1; i++) {
                        scanPos = scanPos.add(relativeLeft.getVector());
                    }

                    if(iter == 0)
                    {
                        for(int i = 0; i < sideLength1; i++)
                        {
                            scanPos = scanPos.add(relativeRight.getVector());

                            if(!checkBlock(world, scanPos))
                            {
                                successfulScan(height + 1, sideLength1, sideLength2);
                                return;
                            }
                        }
                        scanPos = scanPos.add(relativeRight.getVector());
                        for(int i = 0; i < sideLength2; i++)
                        {
                            scanPos = scanPos.add(direction.getVector());

                            if(!checkBlock(world, scanPos))
                            {
                                successfulScan(height + 1, sideLength1, sideLength2);
                                return;
                            }
                        }
                        scanPos = scanPos.add(direction.getVector());
                        for(int i = 0; i < sideLength3; i++)
                        {
                            scanPos = scanPos.add(relativeLeft.getVector());

                            if(!checkBlock(world, scanPos))
                            {
                                successfulScan(height + 1, sideLength1, sideLength2);
                                return;
                            }
                        }
                        scanPos = scanPos.add(relativeLeft.getVector());
                        for(int i = 0; i < sideLength4; i++)
                        {
                            scanPos = scanPos.add(directionOpposite.getVector());

                            if(!checkBlock(world, scanPos))
                            {
                                successfulScan(height + 1, sideLength1, sideLength2);
                                return;
                            }
                        }
                    }
                    if(iter == 1)
                    {
                        for(int i = 0; i < sideLength1; i++)
                        {
                            scanPos = scanPos.add(relativeRight.getVector());

                            if(!checkBlockProper(world, scanPos))
                            {
                                failedScan();
                                break MAINLOOP;
                            }
                        }
                        scanPos = scanPos.add(relativeRight.getVector());
                        for(int i = 0; i < sideLength2; i++)
                        {
                            scanPos = scanPos.add(direction.getVector());

                            if(!checkBlockProper(world, scanPos))
                            {
                                failedScan();
                                break MAINLOOP;
                            }
                        }
                        scanPos = scanPos.add(direction.getVector());
                        for(int i = 0; i < sideLength3; i++)
                        {
                            scanPos = scanPos.add(relativeLeft.getVector());

                            if(!checkBlockProper(world, scanPos))
                            {
                                failedScan();
                                break MAINLOOP;
                            }
                        }
                        scanPos = scanPos.add(relativeLeft.getVector());
                        for(int i = 0; i < sideLength4; i++)
                        {
                            scanPos = scanPos.add(directionOpposite.getVector());

                            if(!checkBlockProper(world, scanPos))
                            {
                                failedScan();
                                break MAINLOOP;
                            }
                        }
                    }
                }
            }
        }
        successfulScan(16, sideLength1, sideLength2);
    }

    private boolean checkBlockProper(World world, BlockPos pos)
    {
        if(ForgeAlloySmelteryRegistry.getStructureBlocks().contains(world.getBlockState(pos).getBlock())) {
            return true;
        }
        return false;
    }

    private boolean checkBlock(World world, BlockPos pos)
    {
        if(ForgeAlloySmelteryRegistry.getStructureBlocks().contains(world.getBlockState(pos).getBlock())) {
            return true;
        }
        return false;
    }

    private boolean checkBlockFloor(World world, BlockPos pos)
    {
        if(ForgeAlloySmelteryRegistry.getStructureBlocks().contains(world.getBlockState(pos).getBlock())) {
            return true;
        }
        return false;
    }

    private void successfulScan(int heightIn, int length, int width)
    {
        valid = true;

        int height = heightIn - 1;

        boolean recalculateInventories = oldHeight != height || oldLength != length || oldWidth != width;

        //Item Inventory
        if(recalculateInventories)
        {
            SimpleInventory newInv = new SimpleInventory(height * length * width);
            int newSize = newInv.size();
            int oldSize = itemInventory.size();

            if(oldSize > newSize) {
                for (int i = 0; i < newSize; i++) {
                    newInv.setStack(i, itemInventory.getStack(i));
                }

                BlockPos dropPos = pos.add(world.getBlockState(pos).get(AlloySmelteryControllerBlock.FACING).getOpposite().getVector());
                Inventory dropInv = new SimpleInventory(oldSize - newSize);

                int counter = 0;

                for (int i = newSize; i < oldSize; i++) {
                    dropInv.setStack(counter, itemInventory.getStack(i));
                    counter++;
                }

                ItemScatterer.spawn(world, dropPos, dropInv);
            }
            else
            {
                for(int i = 0; i < oldSize; i++)
                {
                    newInv.setStack(i, itemInventory.getStack(i));
                }
            }

            itemInventory = newInv;
        }

        oldHeight = height;
        oldLength = length;
        oldWidth = width;
    }

    private void failedScan()
    {
        valid = false;
    }

    public boolean isValid()
    {
        return valid;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if(valid)
        {
            return new AlloySmelteryInvScreenHandler(syncId, inv, itemInventory, this);
        }
        return null;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("theforge.container.alloysmeltery");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(itemInventory.size());
        buf.writeBlockPos(getPos());
        buf.writeBlockPos(getPos());
    }

    void itemSmeltTick()
    {
        //PrepareSmeltList
        if(smeltTicks == null)
        {
            smeltTicks = new ArrayList<>();
        }
        if(smeltTicks.isEmpty())
        {
            for(int i = 0; i < itemInventory.size(); i++)
            {
                smeltTicks.add(0);
            }
        }
        else
        {
            if(smeltTicks.size() > itemInventory.size())
            {
                smeltTicks = smeltTicks.subList(0, itemInventory.size() - 1);
            }
            if(smeltTicks.size() < itemInventory.size())
            {
                for(int i = smeltTicks.size(); i < itemInventory.size(); i++)
                {
                    smeltTicks.add(0);
                }
            }
        }

        if(oldItemInventory == null)
        {
            oldItemInventory = itemInventory;
        }

        ItemStack stack;
        Item item;
        SmelteryResource smelteryResource;
        FluidProperties fluidProperties;
        int cookTime;

        int smeltTick;

        //Actually tick the smelting
        for(int i = 0; i < itemInventory.size(); i++)
        {
            stack = itemInventory.getStack(i);
            if(stack.isEmpty())
            {
                smeltTicks.set(i, 0);
                continue;
            }
            item = stack.getItem();

            if(!ForgeSmelteryResourceRegistry.getSmelteryResourceMap().containsKey(item))
            {
                smeltTicks.set(i, 0);
                continue;
            }

            if(!oldItemInventory.getStack(i).getItem().equals(itemInventory.getStack(i).getItem()))
            {
                smeltTicks.set(i, 0);
            }

            smelteryResource = ForgeSmelteryResourceRegistry.getSmelteryResource(item);
            fluidProperties = ForgeMoltenFluidRegistry.getFluidProperties(smelteryResource.fluidID());
            cookTime = (int) (fluidProperties.getCookTime() * (smelteryResource.fluidValue() / FluidConstants.INGOT));

            smeltTicks.set(i, smeltTicks.get(i) + 1);

            smeltTick = smeltTicks.get(i);

            if(smeltTick >= cookTime)
            {
                smeltTicks.set(i, 0);
                itemInventory.setStack(i, ItemStack.EMPTY);
            }
        }
    }
}