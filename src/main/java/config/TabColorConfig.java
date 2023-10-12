package config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


@State(name = "ColorConfiguration",
        storages = {
                @Storage("tab-highlighter.xml")
        })
public class TabColorConfig implements PersistentStateComponent<TabColorConfig.CacheColor>{
    public static final String GROUP = "TabColor";
    public static final String EXTERNAL_ID = "TabColorConfig";
    private static final Logger LOGGER = Logger.getInstance(TabColorConfig.class);
    public TabColorTextAttributesDescription attributesDescription;
    CacheColor cacheColor;
    private Color backgroundColor;

    public static Set<VirtualFile> tabCache = new HashSet<>();
    public static VirtualFile activeFile = null;

    public TabColorConfig(Project project) {
        setDefaults();
    }

    private void setDefaults() {
        LOGGER.info("*****setDefaults() ");
        cacheColor = new CacheColor();
        cacheColor.background.enabled = true;
        cacheColor.background.red = 242;
        cacheColor.background.green = 168;
        cacheColor.background.blue = 198;
        backgroundColor = cacheColor.getBackgroundColor();
        TextAttributes attributes = new TextAttributes();
        attributes.setBackgroundColor(backgroundColor);
        TextAttributesKey textAttributesKey = TextAttributesKey.createTextAttributesKey(EXTERNAL_ID);
        attributesDescription = new TabColorTextAttributesDescription(GROUP, GROUP, attributes, textAttributesKey, EditorColorsManager.getInstance().getGlobalScheme());
    }

    // 单例模式
    @Nullable
    public static TabColorConfig getSettings(Project project) {
        return project.getService(TabColorConfig.class);
    }

    @Override
    @Nullable
    public TabColorConfig.CacheColor getState() {
        return cacheColor;
    }

    @Override
    public void loadState(@NotNull CacheColor cacheColor) {
//        LOGGER.info("*****LOADING " + cacheColor);
        XmlSerializerUtil.copyBean(cacheColor, this.cacheColor);
        backgroundColor = cacheColor.getBackgroundColor();
        updateAttributes(cacheColor);
    }

    private void updateAttributes(CacheColor cacheColor) {
//        LOGGER.info("*****updateAttributes(" + cacheColor + ")");
        attributesDescription.setBackgroundColor(cacheColor.getBackgroundColor());
        attributesDescription.setBackgroundChecked(cacheColor.isBackgroundColorUsed());
    }

    public Color getBackgroundColor() {
//        LOGGER.info("*****getBackgroundColor  " + backgroundColor);
        rebuildHighlightColorIfNecessary();
        return backgroundColor;
    }

    private void rebuildHighlightColorIfNecessary() {
        if (backgroundColor != null) {
//            LOGGER.info("*****REBUILDING COLOUR  " + attributesDescription.getBackgroundColor());
            if (cacheColor.isBackgroundColorDifferentThan(backgroundColor)) {
//                LOGGER.info("Rebuilding highlight color");
//                LOGGER.debug("Color changed from  " + backgroundColor + " to " + cacheColor);
                backgroundColor = cacheColor.getBackgroundColor();
                updateAttributes(cacheColor);
            }
        }
    }

    public TabColorTextAttributesDescription getAttributesDescription() {
        return attributesDescription;
    }

    public boolean isBackgroundColorUsed() {
        return cacheColor.isBackgroundColorUsed();
    }

    public void storeBackgroundColorInformation(boolean enabled, Color color) {
        LOGGER.info("*****SAVE " + enabled + " " + color);
        this.cacheColor.storeBackgroundColorInformation(enabled, color);
        updateAttributesBackgroundColor(enabled, color);
    }

    private void updateAttributesBackgroundColor(boolean enabled, Color color) {
//        LOGGER.info("*****UPDATE BG COLOR " + enabled + "" + color);
        attributesDescription.setBackgroundColor(color);
        attributesDescription.setBackgroundChecked(enabled);
    }



    static class CacheColor {

        public ColorContent background;
        public ColorContent foreground;

        public CacheColor() {
            background = new ColorContent();
            foreground = new ColorContent();
        }

        public Color getBackgroundColor() {
            return background.getColor();
        }

        public Color getForegroundColor() {
            return foreground.getColor();
        }

        public void storeBackgroundColorInformation(boolean enabled, Color color) {
            background.enabled = enabled;
            if (enabled) {
                if (color == null) {
                    throw new NullPointerException("Color cannot be null when enabled");
                } else {
                    background.red = color.getRed();
                    background.green = color.getGreen();
                    background.blue = color.getBlue();
                }
            }
        }

        public boolean isBackgroundColorDifferentThan(Color color) {
            return !background.red.equals(color.getRed()) || !background.green.equals(color.getGreen()) || !background.blue.equals(color.getBlue());
        }

        @Override
        public String toString() {
            return "PersistentState{" +
                    "background=" + background +
                    ", foreground=" + foreground +
                    '}';
        }

        public boolean isBackgroundColorUsed() {
            return background.enabled;
        }

        static class ColorContent {
            public boolean enabled = false;
            public Integer red;
            public Integer green;
            public Integer blue;

            public Color getColor() {
                if (!enabled) {
                    return null;
                } else {
                    return new Color(red, green, blue);
                }
            }

            @Override
            public String toString() {
                return "ColorContent{" +
                        (enabled ? "enabled" : "disabled") +
                        ", red=" + red +
                        ", green=" + green +
                        ", blue=" + blue +
                        '}';
            }
        }
    }
}
