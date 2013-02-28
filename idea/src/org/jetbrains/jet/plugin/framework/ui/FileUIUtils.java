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

package org.jetbrains.jet.plugin.framework.ui;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.intellij.ide.util.projectWizard.ProjectWizardUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUIUtils {
    private FileUIUtils() {
    }

    @Nullable
    public static List<File> copyWithOverwriteDialog(
            @NotNull Component parent,
            @NotNull String messagesTitle,
            @NotNull String destinationFolder,
            @NotNull File... files
    ) {
        if (!ProjectWizardUtil.createDirectoryIfNotExists("Destination folder", destinationFolder, false)) {
            Messages.showErrorDialog(String.format("Error during folder creating '%s'", destinationFolder), messagesTitle + ". Error");
            return null;
        }
        
        File folder = new File(destinationFolder);

        List<File> targetFiles = new ArrayList<File>(files.length);
        for (File targetFile : files) {
            targetFiles.add(new File(folder, targetFile.getName()));
        }

        Collection<File> existentFiles = Collections2.filter(targetFiles, new Predicate<File>() {
            @Override
            public boolean apply(@Nullable File file) {
                assert file != null;
                return file.exists();
            }
        });

        if (!existentFiles.isEmpty()) {
            String message = existentFiles.size() == 1 ?
                String.format("File \"%s\" is already exist in %s.\nDo you want to overwrite it?", existentFiles.iterator().next().getName(), folder.getAbsolutePath()) :
                String.format("Several files are already exist in %s:\n%s\nDo you want to overwrite them?", folder.getAbsolutePath(), StringUtil.join(existentFiles, "\n"));

            int replaceIfExist = Messages.showYesNoDialog(
                    null,
                    message,
                    messagesTitle + ". Replace File",
                    "Overwrite",
                    "Cancel",
                    Messages.getWarningIcon());

            if (replaceIfExist != JOptionPane.YES_OPTION) {
                return null;
            }
        }


        assert folder.exists();

        try {
            if (!targetFile.exists()) {
                FileUtil.copy(file, targetFile);
            }
            else {
                int replaceIfExist = Messages.showYesNoDialog(
                        parent,
                        String.format("File \"%s\" is already exist in %s.\nDo you want to rewrite it?", targetFile.getName(), folder.getAbsolutePath()),
                        messagesTitle + ". Replace File",
                        Messages.getWarningIcon());

                if (replaceIfExist == JOptionPane.YES_OPTION) {
                    FileUtil.copy(file, targetFile);
                }
            }

            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);

            return targetFile;
        }
        catch (IOException e) {
            Messages.showErrorDialog("Error during file copy", messagesTitle + ". Error");
            return null;
        }
    }

    @NotNull
    public static String getDefaultLibraryFolder(@Nullable Project project, @Nullable VirtualFile contextDirectory) {
        String path = null;
        if (contextDirectory != null) {
            path = PathUtil.getLocalPath(contextDirectory);

        }
        else if (project != null) {
            path = PathUtil.getLocalPath(project.getBaseDir());
        }

        if (path != null) {
            path = new File(path, "lib").getAbsolutePath();
        }
        else {
            path = "";
        }
        return path;
    }
}
