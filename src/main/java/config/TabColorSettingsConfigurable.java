package config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import listener.SettingsChangedEvent;
import listener.SettingsOfTabColorChangedListener;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ui.ColorAndFontDescriptionPanel;

import javax.swing.*;

public class TabColorSettingsConfigurable implements SearchableConfigurable {
    private static final Logger LOGGER = Logger.getInstance(TabColorSettingsConfigurable.class);

    public static final String PREFERENCE_HIGHLIGHTER_SETTINGS_CONFIGURABLE = "preference.HighlighterSettingsConfigurable";
    public static final String ACTIVE_TAB_HIGHLIGHTER_PLUGIN_DISPLAY_NAME = "Active Tab Highlighter Plugin";
    private final TabColorConfig config;
    private final EditorColorsScheme editorColorsScheme;
    private final MessageBus bus;
    private final Project myProject;

    private ColorAndFontDescriptionPanel colorAndFontDescriptionPanel;

    public TabColorSettingsConfigurable(Project project) {
        this.myProject = project;
        this.config = TabColorConfig.getSettings(project);
        this.editorColorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
        bus = ApplicationManager.getApplication().getMessageBus();
    }

    @NotNull
    @Override
    public String getId() {
        return PREFERENCE_HIGHLIGHTER_SETTINGS_CONFIGURABLE;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return ACTIVE_TAB_HIGHLIGHTER_PLUGIN_DISPLAY_NAME;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return PREFERENCE_HIGHLIGHTER_SETTINGS_CONFIGURABLE;
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        colorAndFontDescriptionPanel = new ColorAndFontDescriptionPanel();
        return colorAndFontDescriptionPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        TabColorTextAttributesDescription attributesDescription = config.getAttributesDescription();
        return true;
    }

    @Override
    public void apply() {

        bus.syncPublisher(SettingsOfTabColorChangedListener.CHANGE_TABCOLOR_SETTINGS_TOPIC).beforeSettingsChanged(new SettingsChangedEvent(this));

        config.storeBackgroundColorInformation(colorAndFontDescriptionPanel.isBackgroundColorEnabled(), colorAndFontDescriptionPanel.getSelectedBackgroundColor());
        TabColorTextAttributesDescription attributesDescription = config.getAttributesDescription();
        colorAndFontDescriptionPanel.apply(attributesDescription, editorColorsScheme);

        bus.syncPublisher(SettingsOfTabColorChangedListener.CHANGE_TABCOLOR_SETTINGS_TOPIC).settingsChanged(new SettingsChangedEvent(this));
    }

    @Override
    public void reset() {
        colorAndFontDescriptionPanel.reset(config.getAttributesDescription());
    }

}
