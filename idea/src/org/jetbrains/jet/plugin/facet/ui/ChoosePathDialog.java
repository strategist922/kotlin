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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileTextField;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

class ChoosePathDialog extends DialogWrapper {
    private final Project myProject;
    private TextFieldWithBrowseButton myPathField;

    protected ChoosePathDialog(Project project) {
        super(project);
        myProject = project;

        setTitle("Local Kotlin Runtime Path");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        FileTextField field = FileChooserFactory.getInstance().createFileTextField(descriptor, myDisposable);
        field.getField().setColumns(25);
        myPathField = new TextFieldWithBrowseButton(field.getField());
        myPathField.addBrowseFolderListener("Choose Destination Folder", "Choose folder for file", myProject, descriptor);

        VirtualFile baseDir = myProject.getBaseDir();
        if (baseDir != null) {
            myPathField.setText(baseDir.getPath().replace('/', File.separatorChar) + File.separatorChar + "lib");
        }

        return myPathField;
    }

    @NotNull
    public String getPath() {
        return myPathField.getText();
    }
}