/*
 * Copyright 2010-2013 JetBrains s.r.o.
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

package org.jetbrains.jet.plugin.facet.ui;

import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.plugin.JetLanguage;
import org.jetbrains.jet.plugin.facet.JetFacetSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JetFacetEditorTab extends FacetEditorTab {
    private final JetFacetSettings facetSettings;
    private JPanel mainPanel;
    private JRadioButton javaModuleRadioButton;
    private JRadioButton javaScriptModuleRadioButton;
    private TextFieldWithBrowseButton runtimeLibraryField;
    private TextFieldWithBrowseButton javascriptLibrariesField;

    public JetFacetEditorTab(JetFacetSettings facetSettings, FacetValidatorsManager validatorsManager) {
        this.facetSettings = facetSettings;

        validatorsManager.registerValidator(new FacetEditorValidator() {
            @Override
            public ValidationResult check() {
                return JetFacetEditorTab.check();
            }
        }, javaModuleRadioButton, javaScriptModuleRadioButton, runtimeLibraryField, javascriptLibrariesField);

        reset();

        javaModuleRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                changeModuleTypeControls(javaModuleRadioButton.isSelected());
            }
        });
        javaScriptModuleRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                changeModuleTypeControls(javaModuleRadioButton.isSelected());
            }
        });
    }

    private static ValidationResult check() {
        // TODO: add checks
        return ValidationResult.OK;
    }

    private void changeModuleTypeControls(boolean javaModule) {
        runtimeLibraryField.setEnabled(javaModule);
        javascriptLibrariesField.setEnabled(!javaModule);
    }

    private void update(JetFacetSettings settings) {
        settings.setJavaModule(javaModuleRadioButton.isSelected());
        settings.setRuntimeLibraryName(runtimeLibraryField.getText());
        settings.setJsLibraryFolder(javaScriptModuleRadioButton.getText());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return JetLanguage.NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public void apply() throws ConfigurationException {
        update(facetSettings);
    }

    @Override
    public boolean isModified() {
        JetFacetSettings currentSettings = new JetFacetSettings();
        update(currentSettings);

        return !facetSettings.equals(currentSettings);
    }

    @Override
    public void reset() {
        javaModuleRadioButton.setSelected(facetSettings.isJavaModule());
        javaScriptModuleRadioButton.setSelected(!facetSettings.isJavaModule());

        runtimeLibraryField.setText(facetSettings.getRuntimeLibraryName());
        javascriptLibrariesField.setText(facetSettings.getJsLibraryFolder());

        changeModuleTypeControls(facetSettings.isJavaModule());
    }

    @Override
    public void disposeUIResources() {
    }
}
