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
import com.google.common.collect.Lists;
import com.intellij.facet.ui.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.impl.libraries.ApplicationLibraryTable;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.libraries.AddCustomLibraryDialog;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.plugin.JetLanguage;
import org.jetbrains.jet.plugin.facet.JetFacetSettings;
import org.jetbrains.jet.plugin.facet.RuntimeLibraryDescription;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class JetFacetEditorTab extends FacetEditorTab {
    private final JetFacetSettings facetSettings;
    private final FacetEditorContext editorContext;
    private JPanel mainPanel;
    private JRadioButton javaModuleRadioButton;
    private JRadioButton javaScriptModuleRadioButton;
    private JComboBox runtimeLibraryComboBox;
    private JButton createJavaRuntimeLibraryButton;
    private JComboBox kotlinJSSourcesComboBox;
    private JButton createJSSourcesLibraryButton;
    private TextFieldWithBrowseButton javaScriptRuntimeFileField;

    public JetFacetEditorTab(JetFacetSettings facetSettings, FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        this.facetSettings = facetSettings;
        this.editorContext = editorContext;

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
        createJavaRuntimeLibraryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                onCreateJavaRuntimeLibrary();
            }
        });
        createJSSourcesLibraryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(@NotNull ActionEvent e) {
                onCreateJavaScriptSourceLibrary();
            }
        });
    }

    private void onCreateJavaRuntimeLibrary() {
        AddCustomLibraryDialog libraryDialog =
                AddCustomLibraryDialog.createDialog(new RuntimeLibraryDescription(), editorContext.getModule(), null);
        libraryDialog.show();
    }

    private void onCreateJavaScriptSourceLibrary() {
        AddCustomLibraryDialog libraryDialog =
                AddCustomLibraryDialog.createDialog(new RuntimeLibraryDescription(), editorContext.getModule(), null);
        libraryDialog.show();
    }

    private ValidationResult check() {
        if (isJavaModeEnabled()) {
            Library library = getSelectedRuntimeLibrary();
            if (library == null) {
                return new ValidationResult("Java Runtime library isn't set");
            }
        }
        else {
            Library library = getSelectedJSSourcesLibrary();
            if (library == null) {
                return new ValidationResult("Java Script Standard library isn't set");
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
    }

    private void update(JetFacetSettings settings) {
        settings.setJavaModule(isJavaModeEnabled());

        Library javaRuntimeLibrary = getSelectedRuntimeLibrary();
        settings.setJavaRuntimeLibraryName(javaRuntimeLibrary != null ? javaRuntimeLibrary.getName() : null);

        Library jsSourcesLibrary = getSelectedJSSourcesLibrary();
        settings.setJsSourcesLibraryName(jsSourcesLibrary != null ? jsSourcesLibrary.getName() : null);

        settings.setJsLibraryFolder(javaScriptRuntimeFileField.getText());
    }

    private boolean isJavaModeEnabled() {
        return javaModuleRadioButton.isSelected();
    }

    private Library getSelectedRuntimeLibrary() {
        return (Library) runtimeLibraryComboBox.getSelectedItem();
    }

    private Library getSelectedJSSourcesLibrary() {
        return (Library) kotlinJSSourcesComboBox.getSelectedItem();
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

        javaScriptRuntimeFileField.setText(facetSettings.getJsLibraryFolder());

        Collection<Library> libraries = getLibraries(editorContext.getProject   ());

        DefaultComboBoxModel javaRuntimeLibrariesModel = new DefaultComboBoxModel(ArrayUtil.toObjectArray(libraries, Library.class));
        javaRuntimeLibrariesModel.insertElementAt(null, 0);

        Optional<Library> runtimeLibrarySelected = Iterables.tryFind(libraries, new Predicate<Library>() {
            @Override
            public boolean apply(@Nullable Library lib) {
                assert lib != null;
                return facetSettings.getJavaRuntimeLibraryName().equals(lib.getName());
            }
        });

        if (runtimeLibrarySelected.isPresent()) {
            javaRuntimeLibrariesModel.setSelectedItem(runtimeLibrarySelected.get());
        }
        runtimeLibraryComboBox.setModel(javaRuntimeLibrariesModel);

        DefaultComboBoxModel jsSourceLibrariesModel = new DefaultComboBoxModel(ArrayUtil.toObjectArray(libraries, Library.class));
        jsSourceLibrariesModel.insertElementAt(null, 0);

        Optional<Library> jsSourceLibrarySelected = Iterables.tryFind(libraries, new Predicate<Library>() {
            @Override
            public boolean apply(@Nullable Library lib) {
                assert lib != null;
                return facetSettings.getJsSourcesLibraryName().equals(lib.getName());
            }
        });

        if (jsSourceLibrarySelected.isPresent()) {
            jsSourceLibrariesModel.setSelectedItem(jsSourceLibrarySelected.get());
        }
        kotlinJSSourcesComboBox.setModel(jsSourceLibrariesModel);

        changeModuleTypeControls(facetSettings.isJavaModule());
    }

    // TODO: Move to utility methods
    protected static Collection<Library> getLibraries(Project project) {
        Library[] projectLibraries = ProjectLibraryTable.getInstance(project).getLibraries();
        Library[] globalLibraries = ApplicationLibraryTable.getApplicationTable().getLibraries();

        ArrayList<Library> libraries = Lists.newArrayList();
        libraries.addAll(Arrays.asList(projectLibraries));
        libraries.addAll(Arrays.asList(globalLibraries));

        return libraries;

        //def list(level: LibraryLevel) = Libraries.findBy(level, project).map { library =>
        //    LibraryDescriptor(LibraryId(library.getName, level), Some(new CompilerLibraryData(library)))
        //}
        //val all = list(LibraryLevel.Global) ++ list(LibraryLevel.Project)
        //val (suitable, remaining) = all.partition(_.data.get.problem.isEmpty)
        //suitable.sortBy(_.data.get.version.getOrElse("Unknown")).reverse ++ remaining.sortBy(_.id.name.toLowerCase)
    }

    @Override
    public void disposeUIResources() {
    }
}
