package example.client.event;

import com.github.mcmodderanchor.simplebedrockmodel.SimpleBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.particle.resource.ParticleDefinitionLoader;
import com.github.mcmodderanchor.simplebedrockmodel.v2.particle.world.WorldEmitterManager;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/** 在玩家视线前方生成世界粒子发射器的开发测试命令。 */
@EventBusSubscriber(modid = SimpleBedrockModel.MOD_ID, value = Dist.CLIENT)
public final class ParticleExampleCommand {
    private ParticleExampleCommand() {
    }

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("sbm_example_particle")
                .then(argument("effect", IdentifierArgument.id())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                                ParticleDefinitionLoader.getInstance().getAllDefinitions().keySet(), builder))
                        .executes(ParticleExampleCommand::spawn)));
    }

    private static int spawn(CommandContext<CommandSourceStack> context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return 0;
        }
        Identifier id = IdentifierArgument.getId(context, "effect");
        var definition = ParticleDefinitionLoader.getInstance().getDefinition(id);
        if (definition == null) {
            minecraft.gui.setOverlayMessage(Component.literal("Unknown particle: " + id), false);
            return 0;
        }
        Vec3 position = minecraft.player.getEyePosition().add(minecraft.player.getLookAngle().scale(3.0D));
        WorldEmitterManager.getInstance().addEmitter(minecraft.level, position, Vec3.ZERO, definition);
        minecraft.gui.getChat().addClientSystemMessage(Component.literal("Spawned SBM particle: " + id));
        return 1;
    }
}
