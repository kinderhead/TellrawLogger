package mod.kinderhead.tellrawlogger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.brigadier.CommandDispatcher;

import mod.kinderhead.tellrawlogger.TellrawLogger;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TellRawCommand;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(TellRawCommand.class)
public class TellRawMixin {
	@Overwrite
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("tellraw")
				.requires(CommandManager.requirePermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(CommandManager.argument("message", TextArgumentType.text(registryAccess)).executes(context -> {
							TellrawLogger.LOGGER.info(TextArgumentType.parseTextArgument(context,
													"message", null).getString());

							int i = 0;

							for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.getPlayers(context, "targets")) {
								serverPlayerEntity.sendMessageToClient(TextArgumentType.parseTextArgument(context, "message", serverPlayerEntity), false);
								i++;
							}

							return i;
						}))
					)
		);
	}
}