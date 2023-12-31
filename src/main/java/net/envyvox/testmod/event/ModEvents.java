package net.envyvox.testmod.event;

import net.envyvox.testmod.TestMod;
import net.envyvox.testmod.entity.ModEntityTypes;
import net.envyvox.testmod.entity.custom.ChomperEntity;
import net.envyvox.testmod.networking.ModMessages;
import net.envyvox.testmod.networking.packet.ThirstDataSyncS2CPacket;
import net.envyvox.testmod.thirst.PlayerThirst;
import net.envyvox.testmod.thirst.PlayerThirstProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = TestMod.MOD_ID)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                if (!event.getObject().getCapability(PlayerThirstProvider.PLAYER_THIRST).isPresent()) {
                    event.addCapability(new ResourceLocation(TestMod.MOD_ID, "properties"), new PlayerThirstProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                event.getOriginal().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(oldStore -> {
                    event.getOriginal().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                    });
                });
            }
        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(PlayerThirst.class);
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.side == LogicalSide.SERVER) {
                event.player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                    if (thirst.getThirst() > 0 && event.player.getRandom().nextFloat() < 0.005f) { // Once Every 10
                        // Seconds on Avg
                        thirst.subThirst(1);
                        ModMessages.sendToPlayer(new ThirstDataSyncS2CPacket(thirst.getThirst()),
                                ((ServerPlayer) event.player));
                    }
                });
            }
        }

        @SubscribeEvent
        public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
            if (!event.getLevel().isClientSide()) {
                if (event.getEntity() instanceof ServerPlayer player) {
                    player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                        ModMessages.sendToPlayer(new ThirstDataSyncS2CPacket(thirst.getThirst()), player);
                    });
                }
            }
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (event.getEntity() instanceof Sheep &&
                    event.getSource().getEntity() instanceof Player player) {
                player.sendSystemMessage(Component.literal(player.getName().getString() + "hurt a Sheep!"));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.CHOMPER.get(), ChomperEntity.setAttributes());
        }
    }
}