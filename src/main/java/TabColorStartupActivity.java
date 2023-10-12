import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import listener.ActiveTabChangedListener;
import listener.SettingsOfTabColorChangedListener;
import org.jetbrains.annotations.NotNull;


public class TabColorStartupActivity implements StartupActivity, DumbAware {

    private static final Logger logger = Logger.getInstance(TabColorStartupActivity.class);

    private MessageBusConnection connection;

    public void init(Project project) {
        logger.debug("Initializing component");
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        connection = bus.connect();
        ActiveTabChangedListener activeTabChangedListener = new ActiveTabChangedListener(project);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, activeTabChangedListener);
        connection.subscribe(SettingsOfTabColorChangedListener.CHANGE_TABCOLOR_SETTINGS_TOPIC, activeTabChangedListener);
    }

    @Override
    public void runActivity(@NotNull Project project) {
        init(project);
        if(ApplicationManager.getApplication().isUnitTestMode()) {
            // don't create the UI when unit testing
            return;
        }
    }
}
