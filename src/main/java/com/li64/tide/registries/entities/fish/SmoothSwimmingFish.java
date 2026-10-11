package com.li64.tide.registries.entities.fish;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;

import net.minecraft.world.level.Level;

public class SmoothSwimmingFish extends AbstractTideFish {
    public SmoothSwimmingFish(EntityType<? extends SmoothSwimmingFish> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 45, 5,
                0.8f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FindWaterGoal(this));
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0f, 10));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }
    private static final class FindWaterGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final SmoothSwimmingFish fish;
        private net.minecraft.core.BlockPos destination;

        private FindWaterGoal(SmoothSwimmingFish fish) {
            this.fish = fish;
            setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (fish.isInWater() || !fish.onGround()) return false;
            for (var pos : net.minecraft.core.BlockPos.withinManhattan(fish.blockPosition(), 2)) {
                if (fish.level().getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                    destination = pos.immutable();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            fish.getMoveControl().setWantedPosition(destination.getX() + 0.5, destination.getY() + 0.5, destination.getZ() + 0.5, 1.0);
        }

        @Override
        public boolean canContinueToUse() { return false; }
    }
}
