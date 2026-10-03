package nipah.dontdie.fabric;

import java.util.ArrayList;
import java.util.List;

/** Used only on the server thread, and cleared at server lifecycle boundaries. */
final class TickScheduler {
    private final List<Task> tasks = new ArrayList<>();

    void schedule(int ticks, Runnable action) {
        tasks.add(new Task(Math.max(0, ticks), action));
    }

    void tick() {
        // Remove before invoking callbacks so callbacks may safely schedule new work.
        List<Runnable> ready = new ArrayList<>();
        tasks.removeIf(task -> {
            if (task.ticksLeft-- <= 1) {
                ready.add(task.action);
                return true;
            }
            return false;
        });
        ready.forEach(Runnable::run);
    }

    void clear() {
        tasks.clear();
    }

    private static final class Task {
        private int ticksLeft;
        private final Runnable action;

        private Task(int ticksLeft, Runnable action) {
            this.ticksLeft = ticksLeft;
            this.action = action;
        }
    }
}
