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

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryNameAndLevelPanel;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.plugin.versions.KotlinRuntimeLibraryUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class CreateBundledLibraryDialog extends DialogWrapper {
    private final Module module;
    private JPanel contentPane;
    private JPanel librarySettingsPanel;
    private final LibraryNameAndLevelPanel myNameAndLevelPanel;

    private TextFieldWithBrowseButton destinationFolder;
    private final FormBuilder myBuilder;

    private Library library;

    public CreateBundledLibraryDialog(Module module, BundledLibraryConfiguration configuration) {
        super(module.getProject());
        this.module = module;

        setTitle("Create Bundled Library: " + configuration.getTitle());

        myBuilder = LibraryNameAndLevelPanel.createFormBuilder();
        myNameAndLevelPanel = new LibraryNameAndLevelPanel(
                myBuilder,
                configuration.getDefaultLibraryName(),
                configuration.getDefaultLevel());

        destinationFolder.getTextField().setText(configuration.getDefaultCopyPath(module));
        destinationFolder.getTextField().setColumns(30);
        destinationFolder.addBrowseFolderListener(
                "Choose Destination Folder", "Choose folder for file", module.getProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        librarySettingsPanel.add(myBuilder.getPanel());
        return contentPane;
    }

    @NotNull
    public String getPath() {
        return destinationFolder.getText();
    }

    @Override
    protected void doOKAction() {
        LibrariesContainer.LibraryLevel libraryLevel = myNameAndLevelPanel.getLibraryLevel();
        String libraryName = myNameAndLevelPanel.getLibraryName();

        library = KotlinRuntimeLibraryUtil.createRuntimeLibrary(
                getLibraryTable(module, libraryLevel),
                libraryName,
                new KotlinRuntimeLibraryUtil.FindRuntimeLibraryHandler() {
                    @Nullable
                    @Override
                    public File getRuntimeJarPath() {
                        return new File(getPath(), KotlinRuntimeLibraryUtil.KOTLIN_RUNTIME_JAR);
                    }

                    @Override
                    public void runtimePathDoesNotExist(@NotNull File path) {
                        super.runtimePathDoesNotExist(path);
                    }

                    @Override
                    public void ioExceptionOnCopyingJar(@NotNull IOException e) {
                        super.ioExceptionOnCopyingJar(e);
                    }
                });

        if (library != null) {
            // Exit only if library was created successfully
            super.doOKAction();
        }
    }

    @Nullable
    public Library getLibrary() {
        return library;
    }

    private static LibraryTable getLibraryTable(Module module, @NotNull LibrariesContainer.LibraryLevel level) {
        switch (level) {
            case MODULE:
                return ModuleRootManager.getInstance(module).getModifiableModel().getModuleLibraryTable();
            case PROJECT:
                return LibraryTablesRegistrar.getInstance().getLibraryTable(module.getProject());
            case GLOBAL:
                return LibraryTablesRegistrar.getInstance().getLibraryTable();
            default:
                throw new InvalidParameterException("Unexpceted library level");
        }
    }
}
