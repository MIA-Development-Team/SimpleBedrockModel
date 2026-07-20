package example.block.blockentity;

import example.animation.TestBlockAnimationInstance;
import example.init.ExampleModRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** 方块实体动画实例的宿主。 */
public final class TestBlockEntity extends BlockEntity {
    private final TestBlockAnimationInstance animationInstance = new TestBlockAnimationInstance(this);

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(ExampleModRegister.TEST_BLOCK_ENTITY_TYPE, pos, state);
    }

    public TestBlockAnimationInstance getAnimationInstance() {
        return animationInstance;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TestBlockEntity blockEntity) {
        blockEntity.animationInstance.tick();
    }
}
