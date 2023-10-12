package listener;

import java.util.EventObject;

// Scalability
public class SettingsChangedEvent extends EventObject {
    public SettingsChangedEvent(Object source) {
        super(source);
    }
}
