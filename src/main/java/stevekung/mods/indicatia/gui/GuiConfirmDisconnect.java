package stevekung.mods.indicatia.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfirmDisconnect extends GuiScreen
{
    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height / 6 + 96, 150, 20, I18n.format("gui.yes")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, I18n.format("gui.no")));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            if (this.mc.isConnectedToRealms())
            {
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                RealmsBridge bridge = new RealmsBridge();
                bridge.switchToRealms(new GuiMainMenu());
            }
            else
            {
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                this.mc.displayGuiScreen(new GuiMultiplayerCustom(new GuiMainMenu()));
            }
        }
        else
        {
            this.mc.displayGuiScreen(new GuiIngameMenu());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Do you want to disconnect?", this.width / 2, 70, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}