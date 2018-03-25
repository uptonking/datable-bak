package com.vaadin.addon.spreadsheet.action;


import java.io.Serializable;

public class Action implements Serializable {
    private String caption;
    private Resource icon = null;

    public Action(String caption) {
        this.caption = caption;
    }

    public Action(String caption, Resource icon) {
        this.caption = caption;
        this.icon = icon;
    }

    public String getCaption() {
        return this.caption;
    }

    public Resource getIcon() {
        return this.icon;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setIcon(Resource icon) {
        this.icon = icon;
    }

    public interface Container extends Serializable {
        void addActionHandler(Action.Handler var1);

        void removeActionHandler(Action.Handler var1);
    }

    public interface Handler extends Serializable {
        Action[] getActions(Object var1, Object var2);

        void handleAction(Action var1, Object var2, Object var3);
    }

    public interface ShortcutNotifier extends Serializable {
        Registration addShortcutListener(ShortcutListener var1);

        /** @deprecated */
        @Deprecated
        void removeShortcutListener(ShortcutListener var1);
    }

    public interface Notifier extends Action.Container {
        <T extends Action & Action.Listener> void addAction(T var1);

        <T extends Action & Action.Listener> void removeAction(T var1);
    }

    @FunctionalInterface
    public interface Listener extends Serializable {
        void handleAction(Object var1, Object var2);
    }
}
