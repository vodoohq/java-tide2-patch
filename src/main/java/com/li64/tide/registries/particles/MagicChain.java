package com.li64.tide.registries.particles;

import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public class MagicChain extends TideParticles {
    private static final float OFFSET = 1.0f;
    private static final float MOVE_DST = 2.0f;
    private static final float MOVE_SPEED = 6f;

    private final SpriteSet sprites;

    protected MagicChain(ClientLevel level, double x, double y, double z, SpriteSet sprites, float quadSize, float rotation) {
        super(level, x, y, z, sprites,
                (q, cam) -> q.set(cam.rotation()).mul(Axis.ZP.rotationDegrees(rotation)));
        this.lifetime = 18;
        this.sprites = sprites;
        this.quadSize = quadSize;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.alpha = 1f - (float) this.age / this.lifetime;
    }

    @Override
    protected Corner[] corners(float partialTicks) {
        float u0 = this.getU0(), u1 = this.getU1(), v0 = this.getV0(), v1 = this.getV1();
        float moveT = Mth.clamp(((float) this.age + partialTicks) * MOVE_SPEED / this.lifetime, 0f, 1f);
        float vOffset = MOVE_DST * moveT;
        float vMid = Mth.lerp(moveT, v1, v0);
        final float order = 1f;
        return new Corner[] {
                new Corner( 1f * order, 1f + OFFSET - vOffset, u1, v1),
                new Corner( 1f * order, 1f + OFFSET,           u1, vMid),
                new Corner(-1f * order, 1f + OFFSET,           u0, vMid),
                new Corner(-1f * order, 1f + OFFSET - vOffset, u0, v1),
        };
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public @NotNull Particle createParticle(@NotNull SimpleParticleType type,
                                                @NotNull ClientLevel level,
                                                double x, double y, double z,
                                                double vx, double vy, double vz
                                                /*? if >=26.2 {*/, @NotNull RandomSource random/*?}*/) {
                //? if >=26.2 {
                return new MagicChain(level, x, y, z, sprites,
                    1.0f + random.nextFloat() * 0.6f,
                    (random.nextFloat() - 0.5f) * 200f);
                //?} else {
                /*
                return new MagicChain(level, x, y, z, sprites,
                    1.0f + level.getRandom().nextFloat() * 0.6f,
                    (level.getRandom().nextFloat() - 0.5f) * 200f);
                */
                //?}
        }
    }
}