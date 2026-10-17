package com.dominowww.gametestfabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameTestFabric implements ModInitializer, PreLaunchEntrypoint {
	public static final String MOD_ID = "gametest-fabric";
	private static final String BUNDLER_MAIN_CLASS = "bundlerMainClass";
	private static final String GAME_TEST_MAIN_CLASS = "net.minecraft.gametest.Main";
	private static final String FABRIC_GAME_TEST_PROPERTY = "fabric-api.gametest";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("GameTest-Fabric initialized");
	}

	@Override
	public void onPreLaunch() {
		if (GAME_TEST_MAIN_CLASS.equals(System.getProperty(BUNDLER_MAIN_CLASS))) {
			System.setProperty(FABRIC_GAME_TEST_PROPERTY, "true");
			LOGGER.info("Enabled Fabric GameTest mode for the vanilla GameTest launcher");
		}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
