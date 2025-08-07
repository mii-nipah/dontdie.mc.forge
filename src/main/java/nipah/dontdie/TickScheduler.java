package nipah.dontdie;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class TickScheduler {
    private static class Task {
        public int ticksLeft;
        public Consumer<MinecraftServer> action;

        Task(int ticksLeft, Consumer<MinecraftServer> action) {
            this.ticksLeft = ticksLeft;
            this.action = action;
        }
    }
    private static final List<Task> tasks = new CopyOnWriteArrayList<>();

    public static void schedule(int ticks, Consumer<MinecraftServer> action) {
        tasks.add(new Task(ticks, action));
    }

    static final List<Task> toRemove = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post ev) {
        var server = ev.getServer();
        toRemove.clear();
        // decrement and run
        for (Task t : tasks) {
            if (t.ticksLeft <= 1) {
                t.action.accept(server);
                toRemove.add(t);
            } else {
                t.ticksLeft--;
            }
        }
        // remove completed tasks
        for (Task t : toRemove) {
            tasks.remove(t);
        }
    }
}
