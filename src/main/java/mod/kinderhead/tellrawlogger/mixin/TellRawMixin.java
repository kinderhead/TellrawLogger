package mod.kinderhead.tellrawlogger.mixin;

import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.brigadier.CommandDispatcher;

import mod.kinderhead.tellrawlogger.TellrawLogger;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.level.ServerPlayer;

@Mixin(TellRawCommand.class)
public class TellRawMixin {
	@Overwrite
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		dispatcher.register(
			Commands.literal("tellraw")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(
					Commands.argument("targets", EntityArgument.players())
						.then(Commands.argument("message", ComponentArgument.textComponent(context)).executes(c -> {
							TellrawLogger.LOGGER.info(ComponentArgument.getResolvedComponent(c, "message").getString());

							int result = 0;

							for (ServerPlayer player : EntityArgument.getPlayers(c, "targets")) {
								player.sendSystemMessage(ComponentArgument.getResolvedComponent(c, "message", player));
								result++;
							}

							return result;
						}))
					)
		);
	}
}