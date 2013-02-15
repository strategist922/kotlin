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
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryNameAndLevelPanel;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CreateBundledLibraryDialog extends DialogWrapper {
    private final Module module;
    private final BundledLibraryConfiguration configuration;
    private JPanel contentPane;
    private JPanel librarySettingsPanel;
    private final LibraryNameAndLevelPanel myNameAndLevelPanel;

    private TextFieldWithBrowseButton destinationFolder;
    private final FormBuilder myBuilder;

    private Library library;

    public CreateBundledLibraryDialog(Module module, BundledLibraryConfiguration configuration) {
        super(module.getProject());
        this.module = module;
        this.configuration = configuration;

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

        try {
            library = configuration.createLibrary(module, libraryName, libraryLevel, getPath());
            assert library != null : "Unknown error during library creation";
            super.doOKAction();
        }
        catch (BundledLibraryConfiguration.LibraryCreationException e) {
            Messages.showErrorDialog(module.getProject(), e.getMessage(), "Error During Library Creation");
        }
    }

    @Nullable
    public Library getLibrary() {
        return library;
    }
}
