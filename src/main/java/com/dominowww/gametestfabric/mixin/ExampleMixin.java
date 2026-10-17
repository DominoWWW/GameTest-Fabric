package com.dominowww.gametestfabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.impl.gametest.FabricGameTestRunner;
import net.minecraft.server.Main;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Mixin(Main.class)
public class ExampleMixin {
	@Redirect(
			method = "main",
			at = @At(
					value = "INVOKE",
					target = "Ljoptsimple/OptionParser;parse([Ljava/lang/String;)Ljoptsimple/OptionSet;",
					remap = false
			)
	)
	private static OptionSet filterGameTestArguments(OptionParser parser, String[] args) {
		if (!"net.minecraft.gametest.Main".equals(System.getProperty("bundlerMainClass"))) {
			return parser.parse(args);
		}

		List<String> filtered = new ArrayList<>(args.length);
		for (int i = 0; i < args.length; i++) {
			if ("--packs".equals(args[i]) && i + 1 < args.length) {
				copyPacks(Path.of(args[++i]));
			} else {
				filtered.add(args[i]);
			}
		}
		return parser.parse(filtered.toArray(String[]::new));
	}

	@Inject(
			method = "main",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/server/packs/repository/ServerPacksSource;createPackRepository(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Lnet/minecraft/server/packs/repository/PackRepository;"
			),
			cancellable = true
	)
	private static void launchGameTestServer(
			String[] args,
			CallbackInfo info,
			@Local(name = "access") LevelStorageSource.LevelStorageAccess storageAccess,
			@Local(name = "packRepository") PackRepository packRepository
	) {
		if ("net.minecraft.gametest.Main".equals(System.getProperty("bundlerMainClass"))) {
			FabricGameTestRunner.runHeadlessServer(storageAccess, packRepository);
			info.cancel();
		}
	}

	private static void copyPacks(Path source) {
			Path destination = Path.of("world", "datapacks");
			try {
				Files.createDirectories(destination);
				try (var paths = Files.list(source)) {
					for (Path pack : paths.toList()) {
						copy(pack, destination.resolve(pack.getFileName()));
					}
				}
			} catch (IOException exception) {
				throw new IllegalStateException("Failed to copy GameTest datapacks from " + source, exception);
			}
		}

	private static void copy(Path source, Path destination) throws IOException {
			if (Files.isDirectory(source)) {
				Files.createDirectories(destination);
				try (var paths = Files.list(source)) {
					for (Path child : paths.toList()) {
						copy(child, destination.resolve(child.getFileName()));
					}
				}
			} else {
				Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
