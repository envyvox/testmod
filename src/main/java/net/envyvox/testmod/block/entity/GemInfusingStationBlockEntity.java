package net.envyvox.testmod.block.entity;

import net.envyvox.testmod.block.custom.GemInfusingStationBlock;
import net.envyvox.testmod.fluid.ModFluids;
import net.envyvox.testmod.item.ModItems;
import net.envyvox.testmod.networking.ModMessages;
import net.envyvox.testmod.networking.packet.EnergySyncS2CPacket;
import net.envyvox.testmod.networking.packet.FluidSyncS2CPacket;
import net.envyvox.testmod.recipe.GemInfusingStationRecipe;
import net.envyvox.testmod.screen.GemInfusingStationMenu;
import net.envyvox.testmod.util.ModEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class GemInfusingStationBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
                case 1 -> true;
                case 2 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final ModEnergyStorage ENERGY_STORAGE = new ModEnergyStorage(60000, 256) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            ModMessages.sendToClients(new EnergySyncS2CPacket(this.energy, getBlockPos()));
        }
    };

    private static final int ENERGY_REQ = 32;

    private final FluidTank FLUID_TANK = new FluidTank(64000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (!level.isClientSide()) {
                ModMessages.sendToClients(new FluidSyncS2CPacket(this.fluid, worldPosition));
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER ||
                    stack.getFluid() == ModFluids.SOURCE_SOAP_WATER.get();
        }
    };

    public void setFluid(FluidStack stack) {
        this.FLUID_TANK.setFluid(stack);
    }

    public FluidStack getFluidStack() {
        return this.FLUID_TANK.getFluid();
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap = Map.of(
            Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                    (i) -> i == 2,
                    (i, s) -> false)),
            Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                    (index) -> index == 1,
                    (index, stack) -> itemHandler.isItemValid(1, stack))),
            Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                    (i) -> i == 2,
                    (i, s) -> false)),
            Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                    (i) -> i == 1,
                    (index, stack) -> itemHandler.isItemValid(1, stack))),
            Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                    (index) -> index == 0 || index == 1,
                    (index, stack) -> itemHandler.isItemValid(0, stack) || itemHandler.isItemValid(1, stack))));

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public GemInfusingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEM_INFUSING_STATION.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GemInfusingStationBlockEntity.this.progress;
                    case 1 -> GemInfusingStationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GemInfusingStationBlockEntity.this.progress = value;
                    case 1 -> GemInfusingStationBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Gem Infusing Station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        ModMessages.sendToClients(new EnergySyncS2CPacket(this.ENERGY_STORAGE.getEnergyStored(), worldPosition));
        ModMessages.sendToClients(new FluidSyncS2CPacket(this.getFluidStack(), worldPosition));
        return new GemInfusingStationMenu(id, inventory, this, this.data);
    }

    public IEnergyStorage getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyItemHandler.cast();
            }

            if (directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(GemInfusingStationBlock.FACING);

                if (side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("gem_infusing_station.progress", this.progress);
        nbt.putInt("gem_infusing_station.energy", ENERGY_STORAGE.getEnergyStored());
        nbt = FLUID_TANK.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("gem_infusing_station.progress");
        ENERGY_STORAGE.setEnergy(nbt.getInt("gem_infusing_station.energy"));
        FLUID_TANK.readFromNBT(nbt);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GemInfusingStationBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        if (hasGemInFirstSlot(entity)) {
            entity.ENERGY_STORAGE.receiveEnergy(64, false);
        }

        if (hasRecipe(entity) && hasEnoughEnergy(entity)) {
            entity.progress++;
            extractEnergy(entity);
            setChanged(level, pos, state);

            if (entity.progress >= entity.maxProgress) {
                craftItem(entity);
            }
        } else {
            entity.resetProgress();
            setChanged(level, pos, state);
        }

        if (hasFluidItemInSourceSlot(entity)) {
            transferItemFluidToFluidTank(entity);
        }
    }

    private static void transferItemFluidToFluidTank(GemInfusingStationBlockEntity pEntity) {
        pEntity.itemHandler.getStackInSlot(0).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            int drainAmount = Math.min(pEntity.FLUID_TANK.getSpace(), 1000);

            FluidStack stack = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
            if (pEntity.FLUID_TANK.isFluidValid(stack)) {
                stack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                fillTankWithFluid(pEntity, stack, handler.getContainer());
            }
        });
    }

    private static void fillTankWithFluid(GemInfusingStationBlockEntity pEntity, FluidStack stack, ItemStack container) {
        pEntity.FLUID_TANK.fill(stack, IFluidHandler.FluidAction.EXECUTE);

        pEntity.itemHandler.extractItem(0, 1, false);
        pEntity.itemHandler.insertItem(0, container, false);
    }

    private static boolean hasFluidItemInSourceSlot(GemInfusingStationBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(0).getCount() > 0;
    }

    private static void extractEnergy(GemInfusingStationBlockEntity entity) {
        entity.ENERGY_STORAGE.extractEnergy(ENERGY_REQ, false);
    }

    private static boolean hasEnoughEnergy(GemInfusingStationBlockEntity entity) {
        return entity.ENERGY_STORAGE.getEnergyStored() >= ENERGY_REQ * entity.maxProgress;
    }

    private static boolean hasGemInFirstSlot(GemInfusingStationBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(0).getItem() == ModItems.ZIRCON.get();
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(GemInfusingStationBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<GemInfusingStationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(GemInfusingStationRecipe.Type.INSTANCE, inventory, level);

        if (hasRecipe(entity)) {
            entity.FLUID_TANK.drain(recipe.get().getFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
            entity.itemHandler.extractItem(1, 1, false);
            entity.itemHandler.setStackInSlot(2, new ItemStack(recipe.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(2).getCount() + 1));
            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(GemInfusingStationBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<GemInfusingStationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(GemInfusingStationRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent() &&
                canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, recipe.get().getResultItem()) &&
                hasCorrectFluidInTank(entity, recipe) &&
                hasCorrectFluidAmountInTank(entity, recipe);
    }

    private static boolean hasCorrectFluidAmountInTank(GemInfusingStationBlockEntity entity,
                                                       Optional<GemInfusingStationRecipe> recipe) {
        return entity.FLUID_TANK.getFluidAmount() >= recipe.get().getFluid().getAmount();
    }

    private static boolean hasCorrectFluidInTank(GemInfusingStationBlockEntity entity,
                                                 Optional<GemInfusingStationRecipe> recipe) {
        return recipe.get().getFluid().equals(entity.FLUID_TANK.getFluid());
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(2).getItem() == itemStack.getItem() || inventory.getItem(2).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }
}
