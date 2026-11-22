package nipah.dontdie.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.toasts.SystemToast;

public final class DontdieClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ToastPacket.ID, (client, handler, buffer, responseSender) -> {
            var title = buffer.readComponent();
            var subtitle = buffer.readComponent();
            client.execute(() -> client.getToasts().addToast(SystemToast.multiline(
                    client, SystemToast.SystemToastIds.TUTORIAL_HINT, title, subtitle)));
        });
    }
}
