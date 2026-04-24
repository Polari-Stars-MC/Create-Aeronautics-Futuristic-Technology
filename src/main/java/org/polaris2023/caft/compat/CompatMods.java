package org.polaris2023.caft.compat;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.polaris2023.caft.compat.jei.FutureEnergyCoreJeiPlugin;
import org.polaris2023.caft.compat.ponder.ModPonderSetup;

import java.util.Locale;
import java.util.function.Consumer;

import static org.polaris2023.caft.compat.emi.FutureEnergyCoreEmiPlugin.dispatch;

public enum CompatMods {
    PONDER(ModPonderSetup::registerPonderScenes),
    JEI((RecipesUpdatedEvent event) -> FutureEnergyCoreJeiPlugin.syncRuntimeRecipes(event.getRecipeManager())),
    EMI((ScreenEvent.MouseDragged.Pre event) -> {
        if (dispatch(event.getScreen(), (group, widget, interactiveWidget) -> {
            int localMouseX = (int) event.getMouseX() - group.x();
            int localMouseY = (int) event.getMouseY() - group.y();
            if (!interactiveWidget.caft$isInteracting() && !widget.getBounds().contains(localMouseX, localMouseY)) {
                return false;
            }
            return interactiveWidget.caft$mouseDragged(localMouseX, localMouseY, event.getMouseButton(), event.getDragX(), event.getDragY());
        })) {
            event.setCanceled(true);
        }
    }, (ScreenEvent.MouseButtonReleased.Pre event) -> {
        if (dispatch(event.getScreen(), (group, widget, interactiveWidget) ->
                interactiveWidget.caft$mouseReleased((int) event.getMouseX() - group.x(), (int) event.getMouseY() - group.y(), event.getButton()))) {
            event.setCanceled(true);
        }
    }, (ScreenEvent.MouseScrolled.Pre event) -> {
        if (dispatch(event.getScreen(), (group, widget, interactiveWidget) -> {
            int localMouseX = (int) event.getMouseX() - group.x();
            int localMouseY = (int) event.getMouseY() - group.y();
            if (!widget.getBounds().contains(localMouseX, localMouseY)) {
                return false;
            }
            return interactiveWidget.caft$mouseScrolled(localMouseX, localMouseY, event.getScrollDeltaY());
        })) {
            event.setCanceled(true);
        }
    }),
    REI()
    ;
    private final String name;
    private Runnable run;
    private Consumer<? extends Event>[] event;

    CompatMods() {
        this.name = name().toLowerCase(Locale.ROOT);
    }

    @SafeVarargs
    CompatMods(Consumer<? extends Event>... event) {
        this();
        this.event = event;
    }

    CompatMods(Runnable run) {
        this();
        this.run = run;
    }

    public boolean isLoader() {
        return ModList.get().isLoaded(name);
    }

    public void run() {
        run.run();
        run = null;// gc
    }

    public <T extends Event> void listening(int n,T event) {
        ((Consumer<T>) this.event[n]).accept(event);
    }





}
