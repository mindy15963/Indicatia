package stevekung.mods.indicatia.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import stevekung.mods.indicatia.command.*;
import stevekung.mods.indicatia.config.ConfigManager;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.handler.*;
import stevekung.mods.indicatia.profile.RenderProfileConfig;
import stevekung.mods.indicatia.renderer.ColoredFontRenderer;
import stevekung.mods.indicatia.renderer.RenderFishNew;
import stevekung.mods.indicatia.util.*;

@Mod(modid = IndicatiaMod.MOD_ID, name = IndicatiaMod.NAME, version = IndicatiaMod.VERSION, dependencies = IndicatiaMod.FORGE_VERSION, clientSideOnly = true, guiFactory = IndicatiaMod.GUI_FACTORY, acceptedMinecraftVersions = "[1.12,1.12.1]", certificateFingerprint = IndicatiaMod.CERTIFICATE)
public class IndicatiaMod
{
    public static final String NAME = "Indicatia";
    public static final String MOD_ID = "indicatia";
    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 1;
    public static final int BUILD_VERSION = 9;
    public static final String VERSION = IndicatiaMod.MAJOR_VERSION + "." + IndicatiaMod.MINOR_VERSION + "." + IndicatiaMod.BUILD_VERSION;
    public static final String MC_VERSION = String.valueOf(FMLInjectionData.data()[4]);
    public static final String GUI_FACTORY = "stevekung.mods.indicatia.config.ConfigGuiFactory";
    public static final String FORGE_VERSION = "after:forge@[14.23.2.2611,);";
    public static final String URL = "https://minecraft.curseforge.com/projects/indicatia";
    public static final String CERTIFICATE = "@FINGERPRINT@";
    private static boolean DEOBFUSCATED;
    public static Minecraft MC;
    public static boolean CHECK_NO_CONNECTION;
    public static boolean SHOW_ANNOUNCE_MESSAGE;
    public static boolean FOUND_LATEST;
    public static ColoredFontRenderer coloredFontRenderer;
    public static JsonUtil json;
    private static final List<String> allowedUUID = new ArrayList<>();
    public static String allowedUserUUID = "";

    static
    {
        try
        {
            IndicatiaMod.DEOBFUSCATED = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        }
        catch (Exception e) {}

        IndicatiaMod.MC = Minecraft.getMinecraft();
        IndicatiaMod.json = new JsonUtil();
        IndicatiaMod.allowedUUID.add("7d06c93d-736c-4d63-a683-c7583f6763e7");
        IndicatiaMod.allowedUUID.add("dbd9f8ed-0101-4cd3-8300-782a775c0225");
        IndicatiaMod.allowedUUID.add("2cd88ad0-89b1-4ca7-907e-78066fe36b08");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        for (String uuid : IndicatiaMod.allowedUUID)
        {
            if (GameProfileUtil.getUUID().toString().contains(uuid))
            {
                IndicatiaMod.allowedUserUUID = uuid;
                ModLogger.info("Found user {} that can be use all features in the mod!", uuid);
            }
        }

        IndicatiaMod.init(event.getModMetadata());
        ConfigManager.init(new File(event.getModConfigurationDirectory(), "indicatia.cfg"));
        KeyBindingHandler.init();
        ExtendedConfig.load();
        RenderProfileConfig.load();
        MinecraftForge.EVENT_BUS.register(new HUDRenderHandler(IndicatiaMod.MC));
        MinecraftForge.EVENT_BUS.register(new CommonHandler(IndicatiaMod.MC));
        MinecraftForge.EVENT_BUS.register(new BlockhitAnimationHandler(IndicatiaMod.MC));
        MinecraftForge.EVENT_BUS.register(new PlayerChatHandler(IndicatiaMod.MC));

        if (IndicatiaMod.isSteveKunG())
        {
            try
            {
                Class<?> clazz = Class.forName("stevekung.mods.indicatia.internal.InternalEventHandler");
                clazz.getMethod("init").invoke(null);
            }
            catch (Exception e) {}
        }

        if (ConfigManager.enableFishingRodOldRender)
        {
            ModelLoader.setCustomModelResourceLocation(Items.FISHING_ROD, 0, new ModelResourceLocation("indicatia:fishing_rod", "inventory"));
            ModLogger.info("Successfully replacing vanilla Fishing Rod item model");
        }

        ClientCommandHandler.instance.registerCommand(new CommandMojangStatusCheck());
        ClientCommandHandler.instance.registerCommand(new CommandChangeLog());
        ClientCommandHandler.instance.registerCommand(new CommandAutoLogin());
        ClientCommandHandler.instance.registerCommand(new CommandSlimeChunkSeed());
        ClientCommandHandler.instance.registerCommand(new CommandAFK());
        ClientCommandHandler.instance.registerCommand(new CommandIndicatia());
        ClientCommandHandler.instance.registerCommand(new CommandProfile());
        ClientCommandHandler.instance.registerCommand(new CommandAutoRealms());
        ClientCommandHandler.instance.registerCommand(new CommandHideName());
        ClientCommandHandler.instance.registerCommand(new CommandPingAll());

        if (IndicatiaMod.isSteveKunG())
        {
            ClientCommandHandler.instance.registerCommand(new CommandEntityDetector());
            ClientCommandHandler.instance.registerCommand(new CommandAutoClick());
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ConfigManager.enableFishingRodOldRender)
        {
            IndicatiaMod.MC.getRenderManager().entityRenderMap.entrySet().removeIf(entry -> entry.getKey().equals(EntityFishHook.class));
            IndicatiaMod.MC.getRenderManager().entityRenderMap.put(EntityFishHook.class, new RenderFishNew(IndicatiaMod.MC.getRenderManager()));
            ModLogger.info("Successfully replacing {}", EntityFishHook.class.getName());
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (ConfigManager.enableVersionChecker)
        {
            VersionChecker.startCheck();
        }
        CapeUtil.loadCapeTextureAtStartup();
        IndicatiaMod.coloredFontRenderer = new ColoredFontRenderer(IndicatiaMod.MC.gameSettings, new ResourceLocation("textures/font/ascii.png"), IndicatiaMod.MC.renderEngine, false);
        ((IReloadableResourceManager)IndicatiaMod.MC.getResourceManager()).registerReloadListener(IndicatiaMod.coloredFontRenderer);
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        if (IndicatiaMod.isObfuscatedEnvironment())
        {
            ModLogger.info("Development environment detected! Ignore certificate check.");
        }
        else
        {
            throw new RuntimeException("Invalid fingerprint detected! This version will NOT be supported by the author!");
        }
    }

    public static boolean isObfuscatedEnvironment()
    {
        return IndicatiaMod.DEOBFUSCATED;
    }

    public static boolean isSteveKunG()
    {
        return GameProfileUtil.getUsername().equals("SteveKunG") && GameProfileUtil.getUUID().equals(UUID.fromString("eef3a603-1c1b-4c98-8264-d2f04b231ef4")) || IndicatiaMod.isObfuscatedEnvironment() || GameProfileUtil.getUUID().toString().contains(IndicatiaMod.allowedUserUUID);
    }

    private static void init(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = IndicatiaMod.MOD_ID;
        info.name = IndicatiaMod.NAME;
        info.version = IndicatiaMod.VERSION;
        info.description = "Simple in-game info and utility!";
        info.url = IndicatiaMod.URL;
        info.authorList = Arrays.asList("SteveKunG");
    }
}