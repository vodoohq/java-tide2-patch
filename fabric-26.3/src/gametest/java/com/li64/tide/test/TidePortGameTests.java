package com.li64.tide.test;

import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.TideData;
import com.li64.tide.registries.TideFish;
import com.li64.tide.Tide;
import com.li64.tide.data.TidePlayer;
import com.li64.tide.data.FishLengthHolder;
import com.li64.tide.data.FishyNoteRecipe;
import com.li64.tide.data.item.TideItemData;
import com.li64.tide.data.player.TidePlayerData;
import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.TideItems;
import com.li64.tide.registries.TideBlocks;
import com.li64.tide.registries.TideEntityTypes;
import com.li64.tide.registries.blocks.entities.FishDisplayBlockEntity;
import com.li64.tide.registries.entities.misc.fishing.HookAccessor;
import com.li64.tide.registries.items.TideFishingRodItem;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import java.util.List;

public class TidePortGameTests {
    @GameTest
    public void fishingDataLoads(GameTestHelper helper) {
        helper.assertTrue(FishData.get(TideFish.RAINBOW_TROUT.getDefaultInstance()).isPresent(), Component.literal("Rainbow trout fishing data must load"));
        helper.assertTrue(Items.FISHING_ROD instanceof TideFishingRodItem, Component.literal("Vanilla fishing rod must use Tide fishing mechanics"));
        helper.assertTrue(TideData.CRATES.get().count() == 9, Component.literal("All nine crate fishing definitions must load"));
        helper.succeed();
    }

    @GameTest
    public void castingAndCrateLoot(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var rod = TideItems.IRON_FISHING_ROD.getDefaultInstance();
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, rod);
        ((TideFishingRodItem) rod.getItem()).castHook(rod, player, helper.getLevel(), 1.0F);
        var hook = HookAccessor.getHook(player);
        helper.assertTrue(hook != null, "Casting must create a Tide fishing hook");
        hook.setPos(net.minecraft.world.phys.Vec3.atCenterOf(helper.absolutePos(new BlockPos(1, 2, 1))));
        helper.assertValueEqual(player.fishing.getX(), hook.getX(), "Vanilla hook proxy X");
        helper.assertValueEqual(player.fishing.getY(), hook.getY(), "Vanilla hook proxy Y");
        var context = hook.getContext();
        context.createFishingLootParams();
        for (var crate : TideData.CRATES.get().values()) {
            helper.assertTrue(!crate.getResult(context).items().isEmpty(), "Every crate must generate loot");
        }
        helper.assertTrue(Tide.FISHING_MANAGER.selectCatch(context).isPresent(), "Fishing selector must generate a catch");
        hook.discard();
        helper.assertTrue(player.fishing == null, "Removing hook must clear the vanilla proxy");
        com.li64.tide.network.messages.MinigameServerMsg.handle(new com.li64.tide.network.messages.MinigameServerMsg((byte) 0), player);
        helper.succeed();
    }

    @GameTest
    public void journalAndFishLengthPersist(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var persistent = new CompoundTag();
        persistent.putString("port_test", "journal survives save/load");
        ((TidePlayer) player).tide$setTidePlayerData(persistent);
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        player.saveWithoutId(output);
        ((TidePlayer) player).tide$setTidePlayerData(new CompoundTag());
        player.load(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), output.buildResult()));
        helper.assertValueEqual(((TidePlayer) player).tide$getTidePlayerData().getStringOr("port_test", ""), "journal survives save/load", "Journal save data");

        var fish = TideEntityTypes.ENTITY_TYPES.get("rainbow_trout").create(helper.getLevel(), net.minecraft.world.entity.EntitySpawnReason.LOAD);
        ((FishLengthHolder) fish).tide$setLength(87.25);
        var fishOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        fish.saveWithoutId(fishOutput);
        ((FishLengthHolder) fish).tide$setLength(1.0);
        fish.load(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), fishOutput.buildResult()));
        helper.assertValueEqual(((FishLengthHolder) fish).tide$getLength(), 87.25, "Fish length save data");
        helper.succeed();
    }

    @GameTest
    public void rodAccessoriesAndDisplayPersist(GameTestHelper helper) {
        var rod = TideItems.DIAMOND_FISHING_ROD.getDefaultInstance();
        CustomRodManager.setHook(rod, TideItems.VOID_HOOK.getDefaultInstance());
        CustomRodManager.setLine(rod, TideItems.GOLDEN_LINE.getDefaultInstance());
        CustomRodManager.setBobber(rod, TideItems.PEARL_BOBBER.getDefaultInstance());
        helper.assertTrue(CustomRodManager.getHook(rod).is(TideItems.VOID_HOOK), "Void hook must survive component serialization");
        helper.assertTrue(CustomRodManager.getLine(rod).is(TideItems.GOLDEN_LINE), "Golden line must survive component serialization");
        helper.assertTrue(CustomRodManager.getBobber(rod).is(TideItems.PEARL_BOBBER), "Pearl bobber must survive component serialization");

        var fish = TideFish.RAINBOW_TROUT.getDefaultInstance();
        TideItemData.FISH_LENGTH.set(fish, 93.5);
        var state = TideBlocks.FISH_DISPLAY.defaultBlockState();
        var display = new FishDisplayBlockEntity(BlockPos.ZERO, state);
        helper.assertTrue(display.setDisplayStack(fish), "Rainbow trout must fit in display");
        var saved = display.saveWithoutMetadata(helper.getLevel().registryAccess());
        var restored = new FishDisplayBlockEntity(BlockPos.ZERO, state);
        restored.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), saved));
        helper.assertValueEqual(restored.getFishLength(), 93.5, "Displayed fish length");
        helper.assertTrue(restored.getDisplayStack().is(TideFish.RAINBOW_TROUT), "Display must restore the fish item");
        helper.succeed();
    }

    @GameTest
    public void fishyNoteCrafting(GameTestHelper helper) {
        var input = CraftingInput.of(2, 1, List.of(TideFish.RAINBOW_TROUT.getDefaultInstance(), Items.PAPER.getDefaultInstance()));
        var recipe = new FishyNoteRecipe(CraftingBookCategory.MISC);
        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Fish and paper must craft a fishy note");
        helper.assertTrue(recipe.assemble(input).is(TideItems.FISHY_NOTE), "Recipe must produce a fishy note");
        helper.succeed();
    }
}
