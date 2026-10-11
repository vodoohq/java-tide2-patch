package com.li64.tide.network.messages;

import com.li64.tide.Tide;
import com.li64.tide.data.minigame.FishCatchMinigame;
import net.minecraft.network.FriendlyByteBuf;
//? if >=26.2 {
import net.minecraft.resources.Identifier;
//?} else {
/*
import net.minecraft.resources.ResourceLocation;
*/
//?}
import net.minecraft.world.entity.player.Player;

public record MinigameServerMsg(byte event) implements TidePacketPayload {
        //? if >=26.2 {
        public static final Identifier ID = Tide.resource("minigame_server");
        @Override public Identifier id() { return ID; }
        //?} else {
        /*
        public static final ResourceLocation ID = Tide.resource("minigame_server");
        @Override public ResourceLocation id() { return ID; }
        */
        //?}

    public MinigameServerMsg(FriendlyByteBuf buf) {
        this(buf.readByte());
    }

    public static void encode(MinigameServerMsg message, FriendlyByteBuf buf) {
        buf.writeByte(message.event);
    }

    public static void handle(MinigameServerMsg message, Player player) {
        // The close packet may arrive after the hook was removed or the cast cancelled.
        if (com.li64.tide.registries.entities.misc.fishing.HookAccessor.getHook(player) == null) return;
        FishCatchMinigame minigame = FishCatchMinigame.create(player);
        minigame.handleClientEvent(message.event);
    }
}
