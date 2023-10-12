package service;

import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.fileEditor.impl.EditorWithProviderComposite;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.FileColorManager;
import config.TabColorConfig;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;

public class ColorChangeUtils {
    private static volatile ColorChangeUtils colorChangeUtils = null;

    private final Project project;
    private final TabColorConfig tabColorConfig;

    private ColorChangeUtils(Project project) {
        this.project = project;
        tabColorConfig = TabColorConfig.getSettings(project);
    }

    public static ColorChangeUtils getInstance(Project project){
        if (colorChangeUtils == null) {
            synchronized(ColorChangeUtils.class) {
                if (colorChangeUtils == null) {
                    colorChangeUtils = new ColorChangeUtils(project);
                }
            }
        }
        return colorChangeUtils;
    }

    public void changeColor(VirtualFile file, EditorWindow editorWindow) {
        if (file != null && editorWindow.findFileComposite(file) != null) {
            setTabColor(tabColorConfig.getBackgroundColor(), file, editorWindow);
        }
    }

    public void changeActiveColor(VirtualFile file, EditorWindow editorWindow) {
        if (file != null && editorWindow.findFileComposite(file) != null) {
            Color color = tabColorConfig.getBackgroundColor();
            int activeRed = Math.min(color.getRed() + 30, 255);
            int activeGreen = Math.min(color.getGreen() + 30, 255);
            int activeBlue = Math.min(color.getBlue() + 30, 255);
            Color activeColor = new Color(activeRed, activeGreen, activeBlue);
            setTabColor(activeColor, file, editorWindow);
        }
    }

    public void clearColor(@NotNull FileColorManager fileColorManager, VirtualFile oldFile, EditorWindow editorWindow) {
        if (oldFile != null && editorWindow.findFileComposite(oldFile) != null) {
            setTabColor(fileColorManager.getFileColor(oldFile), oldFile, editorWindow);
        }
    }

    public void clearAllColor(@NotNull FileColorManager fileColorManager, Set<VirtualFile> setTabFile, EditorWindow editorWindow){
        for (VirtualFile file : setTabFile){
            if (file != null && editorWindow.findFileComposite(file) != null && !file.equals(TabColorConfig.activeFile)) {
                setTabColor(fileColorManager.getFileColor(file), file, editorWindow);
            }
        }
    }

    public void refreshAllColor(Set<VirtualFile> setTabFile, EditorWindow editorWindow){
        for (VirtualFile file : setTabFile){
            if (file != null && editorWindow.findFileComposite(file) != null) {
                setTabColor(tabColorConfig.getBackgroundColor(), file, editorWindow);
            }
            changeActiveColor(TabColorConfig.activeFile, editorWindow);
        }
    }

    public void setTabColor(Color color, @NotNull VirtualFile file, @NotNull EditorWindow editorWindow) {
        final EditorWithProviderComposite fileComposite = editorWindow.findFileComposite(file);
        final int index = getEditorIndex(editorWindow, fileComposite);
        if (index >= 0) {
            if (editorWindow.getTabbedPane() != null) { //Distraction free mode // Presentation mode
                editorWindow.getTabbedPane().getTabs().getTabAt(index).setTabColor(color);
            }
        }
    }

    public int getEditorIndex(@NotNull EditorWindow editorWindow, EditorWithProviderComposite fileComposite) {
        return Arrays.asList(editorWindow.getEditors()).indexOf(fileComposite);
    }
}
