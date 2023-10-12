package listener;

import com.intellij.util.messages.Topic;

import java.util.EventListener;

// Scalability
public interface SettingsOfTabColorChangedListener extends EventListener {
    Topic<SettingsOfTabColorChangedListener> CHANGE_TABCOLOR_SETTINGS_TOPIC = Topic.create("Highlighter Topic", SettingsOfTabColorChangedListener.class);

    default void beforeSettingsChanged(SettingsChangedEvent context) {
    }

    void settingsChanged(SettingsChangedEvent context);
}
