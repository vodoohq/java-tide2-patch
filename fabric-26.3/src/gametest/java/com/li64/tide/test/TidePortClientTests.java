package com.li64.tide.test;

import com.li64.tide.client.gui.menus.AnglingTableMenu;
import com.li64.tide.client.gui.overlays.CatchMinigameOverlay;
import com.li64.tide.client.gui.screens.AnglingTableScreen;
import com.li64.tide.client.gui.screens.journal.FishingJournal;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TidePortClientTests implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (var world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();
            world.getServer().runOnServer(server -> {
                var player = server.getPlayerList().getPlayers().getFirst();
                var rod = com.li64.tide.registries.TideItems.IRON_FISHING_ROD.getDefaultInstance();
                player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, rod);
                ((com.li64.tide.registries.items.TideFishingRodItem) rod.getItem()).castHook(rod, player, player.level(), 1.0F);
                var fish = com.li64.tide.registries.TideEntityTypes.ENTITY_TYPES.get("rainbow_trout").create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                fish.setPos(player.position().add(player.getLookAngle().scale(3.0)));
                ((net.minecraft.world.entity.Mob) fish).setNoAi(true);
                player.level().addFreshEntity(fish);
            });
            world.getConnection().waitForClientboundPackets();
            context.waitTicks(3);
            context.runOnClient(client -> {
                if (client.player.fishing == null) throw new AssertionError("Cast hook must synchronize to client");
            });
            context.takeScreenshot("tide-casting-and-fish");
            world.getServer().runOnServer(server -> {
                var player = server.getPlayerList().getPlayers().getFirst();
                com.li64.tide.registries.entities.misc.fishing.HookAccessor.getHook(player).discard();
            });
            context.setScreen(() -> new FishingJournal(Minecraft.getInstance().player));
            context.waitTicks(10);
            context.takeScreenshot("tide-journal");
            context.setScreen(() -> {
                var inventory = Minecraft.getInstance().player.getInventory();
                return new AnglingTableScreen(new AnglingTableMenu(0, inventory), inventory, Component.literal("Angling Table"));
            });
            context.waitTicks(5);
            context.takeScreenshot("tide-angling-table");
            context.runOnClient(client -> {
                client.setScreenAndShow(null);
                CatchMinigameOverlay.start((byte) 0, (byte) 0, 0.3F, 1.0F);
            });
            context.waitTicks(8);
            context.takeScreenshot("tide-minigame");
            context.runOnClient(client -> CatchMinigameOverlay.close());
        }
    }
}
