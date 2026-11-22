package nipah.dontdie.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.toasts.SystemToast;

public final class DontdieClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ToastPacket.TYPE, (packet, context) -> {
            var client = context.client();
            client.getToasts().addToast(SystemToast.multiline(
                    client, SystemToast.SystemToastId.PERIODIC_NOTIFICATION, packet.title(), packet.subtitle()));
        });
    }
}
