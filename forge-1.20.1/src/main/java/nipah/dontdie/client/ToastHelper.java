package nipah.dontdie.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;

public final class ToastHelper {
    public static void show(Component title, Component subtitle) {
        Minecraft mc = Minecraft.getInstance();
        Toast toast = SystemToast.multiline(mc,
            SystemToast.SystemToastIds.TUTORIAL_HINT, title, subtitle);
        mc.getToasts().addToast(toast);
    }
}
