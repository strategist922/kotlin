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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.intellij.facet.impl.ui.ProjectConfigurableContext;
import com.intellij.facet.ui.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileTypeDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.NewLibraryEditor;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.util.ArrayUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.plugin.JetLanguage;
import org.jetbrains.jet.plugin.facet.JetFacetSettings;
import org.jetbrains.jet.utils.KotlinPaths;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JetFacetEditorTab extends FacetEditorTab {
    private final JetFacetSettings facetSettings;
    private final FacetEditorContext editorContext;
    private final FacetValidatorsManager validatorsManager;
    private JPanel mainPanel;
    private JRadioButton javaModuleRadioButton;
    private JRadioButton javaScriptModuleRadioButton;
    private JComboBox runtimeLibraryComboBox;
    private JButton createJavaRuntimeLibraryButton;
    private JComboBox kotlinJSSourcesComboBox;
    private JButton createJSSourcesLibraryButton;
    private TextFieldWithBrowseButton javaScriptRuntimeFileField;
    private JButton bundledJsButton;

    public JetFacetEditorTab(JetFacetSettings facetSettings, FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        this.facetSettings = facetSettings;
        this.editorContext = editorContext;
        this.validatorsManager = validatorsManager;

        validatorsManager.registerValidator(new FacetEditorValidator() {
            @Override
            public ValidationResult check() {
                return JetFacetEditorTab.this.check();
            }
        }, javaModuleRadioButton, javaScriptModuleRadioButton, runtimeLibraryComboBox, kotlinJSSourcesComboBox, javaScriptRuntimeFileField);

        reset();

        ListCellRendererWrapper<Library> libraryRenderer = new LibraryRenderer();

        runtimeLibraryComboBox.setRenderer(libraryRenderer);
        kotlinJSSourcesComboBox.setRenderer(libraryRenderer);

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

        javaScriptRuntimeFileField.addBrowseFolderListener(
                "Kotlin JavaScript Runtime",
                "Select JavaScript kotlin runtime file",
                editorContext.getProject(), new FileTypeDescriptor("Select JavaScript kotlin runtime file", "js"));

        createJavaRuntimeLibraryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                onCreateJavaRuntimeLibraryButtonClicked();
            }
        });
        createJSSourcesLibraryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                onCreateJSSourcesLibraryButtonClicked();
            }
        });
        bundledJsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                onBundleJsButtonClicked();
            }
        });
    }

    @Override
    public void onTabEntering() {
        reset();
    }

    private void onBundleJsButtonClicked() {
        ChoosePathDialog dialog = new ChoosePathDialog(
                editorContext.getProject(),
                "Copy Bundled JavaScript Runtime",
                PathUtil.getLocalPath(editorContext.getProject().getBaseDir()));
        dialog.show();

        if (dialog.isOK()) {
            String copyToFolder = dialog.getPath();

            KotlinPaths paths = org.jetbrains.jet.utils.PathUtil.getKotlinPathsForIdeaPlugin();
            File jsLibJsPath = paths.getJsLibJsPath();
            if (!jsLibJsPath.exists()) {
                Messages.showErrorDialog("JavaScript library not found. Make sure plugin is installed properly.", "Copy Bundled JavaScript");
            }

            try {
                File resultFile = CopyFileUtil.copyWithOverwriteDialog(copyToFolder, jsLibJsPath);
                LocalFileSystem.getInstance().refreshAndFindFileByIoFile(resultFile);

                javaScriptRuntimeFileField.setText(resultFile.getAbsolutePath());
            }
            catch (IOException e) {
                Messages.showErrorDialog("Failed to copy file.", "Copy Bundled JavaScript");

            }
        }
    }

    private void onCreateJavaRuntimeLibraryButtonClicked() {
        ComboBoxModel model = createLibraryWithModuleUpdate(BundledLibraryConfiguration.JAVA_RUNTIME_CONFIGURATION);

        if (model != null) {
            runtimeLibraryComboBox.setModel(model);
            validatorsManager.validate();
        }
    }

    private void onCreateJSSourcesLibraryButtonClicked() {
        ComboBoxModel model = createLibraryWithModuleUpdate(BundledLibraryConfiguration.JAVASCRIPT_STDLIB_CONFIGURATION);
        if (model != null) {
            kotlinJSSourcesComboBox.setModel(model);
            validatorsManager.validate();
        }
    }

    private ComboBoxModel createLibraryWithModuleUpdate(BundledLibraryConfiguration configuration) {
        final CreateBundledLibraryDialog newLibraryDialog = new CreateBundledLibraryDialog(editorContext.getModule(), configuration);
        newLibraryDialog.show();

        if (newLibraryDialog.isOK()) {
            final NewLibraryEditor libraryEditor = newLibraryDialog.getLibraryEditor();
            assert libraryEditor != null : "Library editor should have been created";

            final LibrariesContainer.LibraryLevel level = newLibraryDialog.getLibraryLevel();

            Library library = ApplicationManager.getApplication().runWriteAction(new Computable<Library>() {
                @Override
                public Library compute() {
                    Library library = ((ProjectConfigurableContext) editorContext).getContainer()
                            .createLibrary(libraryEditor, level);
                    final Library.ModifiableModel model = library.getModifiableModel();
                    libraryEditor.applyTo((LibraryEx.ModifiableModelEx) model);
                    model.commit();
                    return library;
                }
            });

            return getModelForLibraries(library.getName(), level);
        }

        return null;
    }

    private ValidationResult check() {
        if (isJavaModeEnabled()) {
            Library library = getSelectedRuntimeLibrary();
            if (library == null) {
                return new ValidationResult("Java Runtime library isn't set");
            }
        }
        else {
            Library library = getSelectedJSStandardLibrary();
            if (library == null) {
                return new ValidationResult("Java Script Standard library isn't set");
            }

            String jsRuntimePath = getJsRuntimePath();
            if (jsRuntimePath != null) {
                File file = new File(jsRuntimePath);
                if (!(file.exists()) || !file.isFile()) {
                    return new ValidationResult("Java Script runtime file wasn't found");
                }
            }
            else {
                return new ValidationResult("Java Script runtime file isn't set");
            }

        }

        return ValidationResult.OK;
    }



    private void changeModuleTypeControls(boolean isJavaModule) {
        runtimeLibraryComboBox.setEnabled(isJavaModule);
        createJavaRuntimeLibraryButton.setEnabled(isJavaModule);

        kotlinJSSourcesComboBox.setEnabled(!isJavaModule);
        createJSSourcesLibraryButton.setEnabled(!isJavaModule);
        javaScriptRuntimeFileField.setEnabled(!isJavaModule);
        bundledJsButton.setEnabled(!isJavaModule);
    }

    private void update(JetFacetSettings settings) {
        settings.setJavaModule(isJavaModeEnabled());

        Library javaRuntimeLibrary = getSelectedRuntimeLibrary();
        settings.setJavaRuntimeLibraryName(javaRuntimeLibrary != null ? javaRuntimeLibrary.getName() : null);
        settings.setJavaRuntimeLibraryLevel(javaRuntimeLibrary != null ? LibraryUtils.getLibraryLevel(javaRuntimeLibrary) : null);

        Library jsSourcesLibrary = getSelectedJSStandardLibrary();
        settings.setJsStdLibraryName(jsSourcesLibrary != null ? jsSourcesLibrary.getName() : null);
        settings.setJsStdLibraryLevel(jsSourcesLibrary != null ? LibraryUtils.getLibraryLevel(jsSourcesLibrary) : null);

        settings.setJsLibraryFolder(javaScriptRuntimeFileField.getText());
    }

    private boolean isJavaModeEnabled() {
        return javaModuleRadioButton.isSelected();
    }

    private Library getSelectedRuntimeLibrary() {
        return (Library) runtimeLibraryComboBox.getSelectedItem();
    }

    private Library getSelectedJSStandardLibrary() {
        return (Library) kotlinJSSourcesComboBox.getSelectedItem();
    }

    @Nullable
    private String getJsRuntimePath() {
        String jsRuntimePath = javaScriptRuntimeFileField.getText().trim();
        if (jsRuntimePath.length() == 0) {
            jsRuntimePath = null;
        }
        return jsRuntimePath;
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
        updateLibrariesSelectionForExternalChange();
        update(facetSettings);
    }

    @Override
    public boolean isModified() {
        updateLibrariesSelectionForExternalChange();

        JetFacetSettings currentSettings = new JetFacetSettings();
        update(currentSettings);

        return !facetSettings.equals(currentSettings);
    }

    private void resetFromSettings(JetFacetSettings settings) {
        javaModuleRadioButton.setSelected(settings.isJavaModule());
        javaScriptModuleRadioButton.setSelected(!settings.isJavaModule());

        javaScriptRuntimeFileField.setText(settings.getJsLibraryFolder());

        runtimeLibraryComboBox.setModel(
                getModelForLibraries(settings.getJavaRuntimeLibraryName(), settings.getJavaRuntimeLibraryLevel()));
        kotlinJSSourcesComboBox.setModel(
                getModelForLibraries(settings.getJsStdLibraryName(), settings.getJsStdLibraryLevel()));

        changeModuleTypeControls(settings.isJavaModule());
    }

    private void updateLibrariesSelectionForExternalChange() {
        JetFacetSettings currentSettings = new JetFacetSettings();
        update(currentSettings);

        // There could be already deleted libraries
        resetFromSettings(currentSettings);
    }

    @Override
    public void reset() {
        resetFromSettings(facetSettings);
    }

    private DefaultComboBoxModel getModelForLibraries(
            @Nullable final String libName,
            @Nullable LibrariesContainer.LibraryLevel level
    ) {
        List<Library> libraries = Arrays.asList(getLibraries(editorContext));

        DefaultComboBoxModel libraryModel = new DefaultComboBoxModel(ArrayUtil.toObjectArray(libraries, Library.class));
        libraryModel.insertElementAt(null, 0);
        libraryModel.setSelectedItem(null);

        if (libName != null && level != null) {
            final LibraryTable libraryTable = LibraryUtils.getLibraryTable(editorContext.getModule(), level);
            Optional<Library> runtimeLibrarySelected = Iterables.tryFind(libraries, new Predicate<Library>() {
                @Override
                public boolean apply(@Nullable Library lib) {
                    assert lib != null;
                    String name = lib.getName();
                    return name != null && name.equals(libName) && libraryTable.equals(lib.getTable());
                }
            });

            if (runtimeLibrarySelected.isPresent()) {
                libraryModel.setSelectedItem(runtimeLibrarySelected.get());
            }
        }

        return libraryModel;
    }

    private static Library[] getLibraries(FacetEditorContext context) {
        return ((ProjectConfigurableContext) context).getContainer().getAllLibraries();
    }

    @Override
    public void disposeUIResources() {
    }
}
