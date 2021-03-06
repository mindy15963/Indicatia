package stevekung.mods.indicatia.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import stevekung.mods.indicatia.config.ConfigManager;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.core.IndicatiaMod;
import stevekung.mods.indicatia.handler.CommonHandler;
import stevekung.mods.indicatia.util.HideNameData;
import stevekung.mods.indicatia.util.InfoUtil;

public class GuiPlayerTabOverlayNew extends GuiPlayerTabOverlay
{
    private static String murderAssassinsNick = "";

    public GuiPlayerTabOverlayNew()
    {
        super(IndicatiaMod.MC, IndicatiaMod.MC.ingameGUI);
    }

    @Override
    public void renderPlayerlist(int width, Scoreboard scoreboard, @Nullable ScoreObjective scoreObjective)
    {
        List<NetworkPlayerInfo> list = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(this.mc.player.connection.getPlayerInfoMap());
        int listWidth = 0;
        int j = 0;

        for (NetworkPlayerInfo info : list)
        {
            int pingWidth = ConfigManager.enableCustomPlayerList ? this.mc.fontRenderer.getStringWidth(String.valueOf(info.getResponseTime())) : 0;
            int stringWidth = this.mc.fontRenderer.getStringWidth(this.getPlayerName(info) + pingWidth);
            listWidth = Math.max(listWidth, stringWidth);

            if (scoreObjective != null && scoreObjective.getRenderType() != IScoreCriteria.EnumRenderType.HEARTS)
            {
                stringWidth = this.mc.fontRenderer.getStringWidth(" " + scoreboard.getOrCreateScore(info.getGameProfile().getName(), scoreObjective).getScorePoints());
                j = Math.max(j, stringWidth);
            }
        }

        list = list.subList(0, Math.min(list.size(), 80));
        int playerListSize = list.size();
        int playerCount = playerListSize;
        int columnSize;

        for (columnSize = 1; playerCount > 20; playerCount = (playerListSize + columnSize - 1) / columnSize)
        {
            ++columnSize;
        }

        boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getConnection().getNetworkManager().isEncrypted();
        int l;

        if (scoreObjective != null)
        {
            if (scoreObjective.getRenderType() == IScoreCriteria.EnumRenderType.HEARTS)
            {
                l = 90;
            }
            else
            {
                l = j;
            }
        }
        else
        {
            l = 0;
        }

        int i1 = Math.min(columnSize * ((flag ? 9 : 0) + listWidth + l + 13), width - 50) / columnSize;
        int j1 = width / 2 - (i1 * columnSize + (columnSize - 1) * 5) / 2;
        int yOffset = 10;
        int l1 = i1 * columnSize + (columnSize - 1) * 5;
        List<String> list1 = null;
        ITextComponent header = this.mc.ingameGUI.getTabList().header;
        ITextComponent footer = this.mc.ingameGUI.getTabList().footer;

        if (header != null)
        {
            list1 = this.mc.fontRenderer.listFormattedStringToWidth(header.getFormattedText(), width - 50);

            for (String s : list1)
            {
                l1 = Math.max(l1, this.mc.fontRenderer.getStringWidth(s));
            }
        }

        List<String> list2 = null;

        if (footer != null)
        {
            list2 = this.mc.fontRenderer.listFormattedStringToWidth(footer.getFormattedText(), width - 50);

            for (String s1 : list2)
            {
                l1 = Math.max(l1, this.mc.fontRenderer.getStringWidth(s1));
            }
        }

        if (list1 != null)
        {
            Gui.drawRect(width / 2 - l1 / 2 - 1, yOffset - 1, width / 2 + l1 / 2 + 1, yOffset + list1.size() * this.mc.fontRenderer.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s2 : list1)
            {
                int i2 = this.mc.fontRenderer.getStringWidth(s2);
                this.mc.fontRenderer.drawStringWithShadow(s2, width / 2 - i2 / 2, yOffset, -1);
                yOffset += this.mc.fontRenderer.FONT_HEIGHT;
            }
            ++yOffset;
        }

        Gui.drawRect(width / 2 - l1 / 2 - 1, yOffset - 1, width / 2 + l1 / 2 + 1, yOffset + playerCount * 9, Integer.MIN_VALUE);

        for (int k4 = 0; k4 < playerListSize; ++k4)
        {
            int l4 = k4 / playerCount;
            int i5 = k4 % playerCount;
            int j2 = j1 + l4 * i1 + l4 * 5;
            int k2 = yOffset + i5 * 9;
            Gui.drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            if (k4 < list.size())
            {
                NetworkPlayerInfo networkplayerinfo1 = list.get(k4);
                GameProfile gameprofile = networkplayerinfo1.getGameProfile();

                if (flag)
                {
                    EntityPlayer entityplayer = this.mc.world.getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && ("Dinnerbone".equals(gameprofile.getName()) || "Grumm".equals(gameprofile.getName()));
                    this.mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    int l2 = 8 + (flag1 ? 8 : 0);
                    int i3 = 8 * (flag1 ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(j2, k2, 8.0F, l2, 8, i3, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT))
                    {
                        int j3 = 8 + (flag1 ? 8 : 0);
                        int k3 = 8 * (flag1 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(j2, k2, 40.0F, j3, 8, k3, 8, 8, 64.0F, 64.0F);
                    }
                    j2 += 9;
                }

                String s4 = this.getPlayerName(networkplayerinfo1);

                if (networkplayerinfo1.getGameType() == GameType.SPECTATOR)
                {
                    this.mc.fontRenderer.drawStringWithShadow(TextFormatting.ITALIC + s4, j2, k2, -1862270977);
                }
                else
                {
                    for (String hide : HideNameData.getHideNameList())
                    {
                        if (s4.contains(hide))
                        {
                            s4 = s4.replace(hide, TextFormatting.OBFUSCATED + hide + TextFormatting.RESET);
                        }
                    }
                    if (s4.contains("YOU"))
                    {
                        GuiPlayerTabOverlayNew.murderAssassinsNick = TextFormatting.getTextWithoutFormattingCodes(s4).replace("YOU ", "");
                    }
                    this.mc.fontRenderer.drawStringWithShadow(s4, j2, k2, -1);
                }

                if (scoreObjective != null && networkplayerinfo1.getGameType() != GameType.SPECTATOR)
                {
                    int k5 = j2 + listWidth + 1;
                    int l5 = k5 + l;

                    if (l5 - k5 > 5)
                    {
                        this.drawScoreboardValues(scoreObjective, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
                    }
                }
                this.drawPing(i1, j2 - (flag ? 9 : 0), k2, networkplayerinfo1);
            }
        }

        if (list2 != null)
        {
            yOffset = yOffset + playerCount * 9 + 1;
            Gui.drawRect(width / 2 - l1 / 2 - 1, yOffset - 1, width / 2 + l1 / 2 + 1, yOffset + list2.size() * this.mc.fontRenderer.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s3 : list2)
            {
                int j5 = this.mc.fontRenderer.getStringWidth(s3);
                this.mc.fontRenderer.drawStringWithShadow(s3, width / 2 - j5 / 2, yOffset, -1);
                yOffset += this.mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }

    @Override
    protected void drawPing(int x1, int x2, int y, NetworkPlayerInfo info)
    {
        int ping = info.getResponseTime();

        if (InfoUtil.INSTANCE.isHypixel() && (info.getGameProfile().getName().equals(ExtendedConfig.HYPIXEL_NICK_NAME) || info.getGameProfile().getName().equals(GuiPlayerTabOverlayNew.murderAssassinsNick)))
        {
            ping = CommonHandler.currentServerPing;
        }

        if (ConfigManager.enableCustomPlayerList)
        {
            TextFormatting color = TextFormatting.GREEN;

            if (ping >= 200 && ping < 300)
            {
                color = TextFormatting.YELLOW;
            }
            else if (ping >= 300 && ping < 500)
            {
                color = TextFormatting.RED;
            }
            else if (ping >= 500)
            {
                color = TextFormatting.DARK_RED;
            }
            String pingText = String.valueOf(ping);
            this.mc.fontRenderer.drawString(color + pingText, x1 + x2 - this.mc.fontRenderer.getStringWidth(pingText), y + 0.5F, 0, true);
        }
        else
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(ICONS);
            int state;

            if (ping < 0)
            {
                state = 5;
            }
            else if (ping < 150)
            {
                state = 0;
            }
            else if (ping < 300)
            {
                state = 1;
            }
            else if (ping < 600)
            {
                state = 2;
            }
            else if (ping < 1000)
            {
                state = 3;
            }
            else
            {
                state = 4;
            }
            this.zLevel += 100.0F;
            this.drawTexturedModalRect(x2 + x1 - 11, y, 0, 176 + state * 8, 10, 8);
            this.zLevel -= 100.0F;
        }
    }
}