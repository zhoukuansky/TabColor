package listener;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.FileColorManager;
import config.TabColorConfig;
import org.jetbrains.annotations.NotNull;
import service.ColorChangeUtils;

import javax.swing.*;

public class ActiveTabChangedListener implements FileEditorManagerListener, SettingsOfTabColorChangedListener {
    private static final Logger LOGGER = Logger.getInstance(ActiveTabChangedListener.class);
    private ColorChangeUtils colorChangeUtils;
    private final TabColorConfig tabColorConfig;
    private final Project project;

    public ActiveTabChangedListener(Project project) {
        this.project = project;
        colorChangeUtils = ColorChangeUtils.getInstance(project);
        tabColorConfig = TabColorConfig.getSettings(project);
        init();
    }

    private void init() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        FileEditor selectedEditor = fileEditorManager.getSelectedEditor();
        if (selectedEditor != null) {
            VirtualFile file = selectedEditor.getFile();
            SwingUtilities.invokeLater(() -> handleActiveTabChange(null, file));
        }
    }

    private void handleActiveTabChange(VirtualFile oldFile, VirtualFile newFile) {
        final FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
        final FileColorManager fileColorManager = FileColorManager.getInstance(project);

        for (EditorWindow editorWindow : manager.getWindows()) {
            if (TabColorConfig.tabCache.contains(oldFile)) {
                colorChangeUtils.changeColor(oldFile, editorWindow);
            } else {
                colorChangeUtils.clearColor(fileColorManager, oldFile, editorWindow);
            }
            colorChangeUtils.changeActiveColor(newFile, editorWindow);
        }

        TabColorConfig.activeFile = newFile;
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
        LOGGER.info(String.format("fileOpen %s", virtualFile.getUrl()));
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
        if (fileEditorManagerEvent.getManager().getProject().equals(project)) {
            handleActiveTabChange(fileEditorManagerEvent.getOldFile(), fileEditorManagerEvent.getNewFile());
        }
    }

    @Override
    public void settingsChanged(SettingsChangedEvent context) {
        if (ProjectManager.getInstance().getOpenProjects() != null) {
            for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                final FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
                for (EditorWindow editorWindow : manager.getWindows()) {
                    colorChangeUtils.refreshAllColor(TabColorConfig.tabCache, editorWindow);
                }
            }
        }
    }
}
