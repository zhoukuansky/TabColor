package action;

import service.ColorChangeUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import config.TabColorConfig;

public class TabColorAction extends AnAction {
    ColorChangeUtils colorChangeUtils;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        colorChangeUtils = ColorChangeUtils.getInstance(project);

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        FileEditor selectedEditor = fileEditorManager.getSelectedEditor();
        VirtualFile file = selectedEditor.getFile();

        final FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
        for (EditorWindow editorWindow : manager.getWindows()) {
            if (!file.equals(TabColorConfig.activeFile)) {
                colorChangeUtils.changeColor(file, editorWindow);
            } else {
                colorChangeUtils.changeActiveColor(file, editorWindow);
            }
        }

        TabColorConfig.tabCache.add(file);
    }
}

