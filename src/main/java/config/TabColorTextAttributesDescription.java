package config;

import com.intellij.application.options.colors.TextAttributesDescription;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;

public class TabColorTextAttributesDescription extends TextAttributesDescription {

    public TabColorTextAttributesDescription(@NotNull final String name,
                                             final String group,
                                             final TextAttributes attributes,
                                             final TextAttributesKey type,
                                             final EditorColorsScheme scheme) {
        super(name, group, attributes, type, scheme, null, null);
    }

    @Override
    public boolean isErrorStripeEnabled() {
        return true;
    }

    @Override
    public TextAttributes getTextAttributes() {
        return super.getTextAttributes();
    }
}
