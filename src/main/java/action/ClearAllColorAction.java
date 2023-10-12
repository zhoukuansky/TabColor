package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.ui.FileColorManager;
import config.TabColorConfig;
import service.ColorChangeUtils;

public class ClearAllColorAction extends AnAction {
    ColorChangeUtils colorChangeUtils;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        colorChangeUtils = ColorChangeUtils.getInstance(project);

        final FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
        final FileColorManager fileColorManager = FileColorManager.getInstance(project);
        for (EditorWindow editorWindow : manager.getWindows()) {
            colorChangeUtils.clearAllColor(fileColorManager, TabColorConfig.tabCache, editorWindow);
        }

        TabColorConfig.tabCache.clear();
    }
}
