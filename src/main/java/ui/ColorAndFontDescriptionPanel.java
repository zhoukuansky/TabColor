/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ui;

import com.intellij.application.options.colors.ColorAndFontDescription;
import com.intellij.application.options.colors.OptionsPanelImpl;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorSchemeAttributeDescriptor;
import com.intellij.openapi.editor.colors.EditorSchemeAttributeDescriptorWithPath;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.options.colors.AbstractKeyDescriptor;
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.EventDispatcher;
import com.intellij.util.FontUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author cdr
 */
public class ColorAndFontDescriptionPanel extends JPanel implements OptionsPanelImpl.ColorDescriptionPanel {
    private static final Logger LOGGER = Logger.getInstance(ColorAndFontDescriptionPanel.class);

    private final EventDispatcher<Listener> myDispatcher = EventDispatcher.create(Listener.class);

    private JPanel myPanel;

    private ColorPanel myBackgroundChooser;

    private JBCheckBox myCbBackground;

    private Map<String, EffectType> myEffectsMap;
    private boolean myUiEventsEnabled = true;

    {
        Map<String, EffectType> map = new LinkedHashMap();
        map.put(ApplicationBundle.message("combobox.effect.underscored"), EffectType.LINE_UNDERSCORE);
        map.put(ApplicationBundle.message("combobox.effect.boldunderscored"), EffectType.BOLD_LINE_UNDERSCORE);
        map.put(ApplicationBundle.message("combobox.effect.underwaved"), EffectType.WAVE_UNDERSCORE);
        map.put(ApplicationBundle.message("combobox.effect.bordered"), EffectType.BOXED);
        map.put(ApplicationBundle.message("combobox.effect.strikeout"), EffectType.STRIKEOUT);
        map.put(ApplicationBundle.message("combobox.effect.bold.dottedline"), EffectType.BOLD_DOTTED_LINE);
        myEffectsMap = Collections.unmodifiableMap(map);
    }

    public ColorAndFontDescriptionPanel() {
        super(new BorderLayout());
        add(myPanel, BorderLayout.CENTER);

        setBorder(JBUI.Borders.empty(4, 0, 4, 4));
        //noinspection unchecked
        //noinspection unchecked

        ActionListener actionListener = e -> {
            if (myUiEventsEnabled) {
                myBackgroundChooser.setEnabled(myCbBackground.isSelected());

                myDispatcher.getMulticaster().onSettingsChanged(e);
            }
        };

        for (JBCheckBox c : new JBCheckBox[]{myCbBackground}) {
            c.addActionListener(actionListener);
        }
        for (ColorPanel c : new ColorPanel[]{myBackgroundChooser}) {
            c.addActionListener(actionListener);
        }
    }

    private static void updateColorChooser(JCheckBox checkBox,
                                           ColorPanel colorPanel,
                                           boolean isEnabled,
                                           boolean isChecked,
                                           @Nullable Color color) {
        checkBox.setEnabled(isEnabled);
        checkBox.setSelected(isChecked);
        if (color != null) {
            colorPanel.setSelectedColor(color);
        } else {
            colorPanel.setSelectedColor(JBColor.WHITE);
        }
        colorPanel.setEnabled(isChecked);
    }

    @NotNull
    @Override
    public JComponent getPanel() {
        return this;
    }

    public void resetDefault() {
        LOGGER.debug("resetDefault() called");
        try {
            myUiEventsEnabled = false;
            updateColorChooser(myCbBackground, myBackgroundChooser, false, false, null);
        } finally {
            myUiEventsEnabled = true;
        }
    }

    public void reset(@NotNull EditorSchemeAttributeDescriptor attrDescription) {
        LOGGER.debug("reset(attrDescription) called");
        try {
            myUiEventsEnabled = false;
            if (!(attrDescription instanceof ColorAndFontDescription)) return;
            ColorAndFontDescription description = (ColorAndFontDescription) attrDescription;

            updateColorChooser(myCbBackground, myBackgroundChooser, description.isBackgroundEnabled(),
                    description.isBackgroundChecked(), description.getBackgroundColor());
            setInheritanceInfo(description);
        } finally {
            myUiEventsEnabled = true;
        }
    }


    private void setInheritanceInfo(ColorAndFontDescription description) {
        Pair<ColorAndFontDescriptorsProvider, ? extends AbstractKeyDescriptor> baseDescriptor = description.getFallbackKeyDescriptor();
        if (baseDescriptor != null && baseDescriptor.second.getDisplayName() != null) {
            String attrName = baseDescriptor.second.getDisplayName();
            String attrLabel = attrName.replaceAll(EditorSchemeAttributeDescriptorWithPath.NAME_SEPARATOR, FontUtil.rightArrow(UIUtil.getLabelFont()));
            ColorAndFontDescriptorsProvider settingsPage = baseDescriptor.first;
            String style = "<div style=\"text-align:right\" vertical-align=\"top\">";
            String tooltipText;
            String labelText;
            if (settingsPage != null) {
                String pageName = settingsPage.getDisplayName();
                tooltipText = "Editor | Color Scheme | " + pageName + "<br>" + attrLabel;
                labelText = style + "<a href=\"" + pageName + "\">" + attrLabel + "</a><br>(" + pageName + ")";
            } else {
                tooltipText = attrLabel;
                labelText = style + attrLabel + "<br>&nbsp;";
            }

            setEditEnabled(!description.isInherited() && description.isEditable(), description);
        } else {
            setEditEnabled(description.isEditable(), description);
        }
    }

    private void setEditEnabled(boolean isEditEnabled, ColorAndFontDescription description) {
        myCbBackground.setEnabled(isEditEnabled && description.isBackgroundEnabled());
        myBackgroundChooser.setEditable(isEditEnabled);
    }

    public void apply(@NotNull EditorSchemeAttributeDescriptor attrDescription, EditorColorsScheme scheme) {

        //Propagate event
        LOGGER.debug("apply(attrDescription, scheme) called");
        if (!(attrDescription instanceof ColorAndFontDescription)) return;
        ColorAndFontDescription description = (ColorAndFontDescription) attrDescription;

        if (description.isInherited()) {
            TextAttributes baseAttributes = description.getBaseAttributes();
            if (baseAttributes != null) {
                description.setFontType(baseAttributes.getFontType());
                description.setBackgroundChecked(baseAttributes.getBackgroundColor() != null);
                description.setBackgroundColor(baseAttributes.getBackgroundColor());
            } else {
                description.setInherited(false);
            }
            reset(description);
        } else {
            setInheritanceInfo(description);
            int fontType = Font.PLAIN;
            description.setFontType(fontType);
            description.setBackgroundChecked(myCbBackground.isSelected());
            description.setBackgroundColor(myBackgroundChooser.getSelectedColor());
        }
        description.apply(scheme);
    }

    @Override
    public void addListener(@NotNull Listener listener) {
        myDispatcher.addListener(listener);
    }

    public boolean isBackgroundColorEnabled() {
        return myCbBackground.isSelected();
    }

    public Color getSelectedBackgroundColor() {
        return myCbBackground.isSelected() ? myBackgroundChooser.getSelectedColor() : null;
    }

}
