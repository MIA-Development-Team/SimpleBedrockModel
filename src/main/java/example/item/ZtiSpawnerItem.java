package example.item;

import example.entity.Zti;
import example.init.ExampleModRegister;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** 右键在玩家前方生成一个实体渲染与动画测试对象。 */
public final class ZtiSpawnerItem extends Item {
    public ZtiSpawnerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            Zti entity = new Zti(ExampleModRegister.ZTI_ENTITY_TYPE, serverLevel);
            Vec3 position = player.position().add(player.getLookAngle().scale(4.0D));
            entity.snapTo(position.x, position.y, position.z, player.getYRot() + 180.0F, 0.0F);
            serverLevel.addFreshEntity(entity);
        }
        return InteractionResult.SUCCESS;
    }
}
