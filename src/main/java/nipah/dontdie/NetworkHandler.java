package nipah.dontdie;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import nipah.dontdie.client.ToastHelper;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Dontdie.MODID)
public final class NetworkHandler {
    private static final String PROTOCOL = "1";
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Dontdie.MODID, "main");
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar reg = event.registrar(PROTOCOL)
            .versioned("0.0.1")
            .optional();
        reg.playToClient(
            ToastMsg.TYPE,
            ToastMsg.STREAM_CODEC,
            ToastMsg::handle
        );
    }

    public record ToastMsg(Component title, Component subtitle) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ToastMsg> TYPE =
            new CustomPacketPayload.Type<>(ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, ToastMsg> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            ToastMsg::title,
            ComponentSerialization.STREAM_CODEC,
            ToastMsg::subtitle,
            ToastMsg::new
        );

        @Override
        public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        static void handle(ToastMsg m, final IPayloadContext ctx) {
            ctx.enqueueWork(() ->
                ToastHelper.show(m.title, m.subtitle))
            .exceptionally(e -> {
                Dontdie.LOGGER.error("Failed to handle ToastMsg packet", e);
                ctx.disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }
}
