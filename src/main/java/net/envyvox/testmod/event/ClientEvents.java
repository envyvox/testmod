package net.envyvox.testmod.event;

import net.envyvox.testmod.TestMod;
import net.envyvox.testmod.block.entity.ModBlockEntities;
import net.envyvox.testmod.block.entity.renderer.GemInfusingStationBlockRenderer;
import net.envyvox.testmod.client.ThirstHudOverlay;
import net.envyvox.testmod.networking.ModMessages;
import net.envyvox.testmod.networking.packet.DrinkWaterC2SPacket;
import net.envyvox.testmod.util.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (KeyBinding.DRINKING_KEY.consumeClick()) {
                assert Minecraft.getInstance().player != null;
                ModMessages.sendToServer(new DrinkWaterC2SPacket());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(@NotNull RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.DRINKING_KEY);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(@NotNull RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("thirst", ThirstHudOverlay.HUD_THIRST);
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.GEM_INFUSING_STATION.get(),
                    GemInfusingStationBlockRenderer::new);
        }
    }
}
