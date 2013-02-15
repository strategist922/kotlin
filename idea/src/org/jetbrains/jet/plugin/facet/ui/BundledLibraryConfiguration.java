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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.NewLibraryEditor;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.utils.KotlinPaths;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public interface BundledLibraryConfiguration {
    String getTitle();
    String getDefaultLibraryName();
    LibrariesContainer.LibraryLevel getDefaultLevel();
    String getDefaultCopyPath(@NotNull Module module);

    Library createLibrary(
            @NotNull Module module,
            @NotNull String libraryName,
            @NotNull LibrariesContainer.LibraryLevel level,
            @NotNull String destinationFolder) throws LibraryCreationException;

    class LibraryCreationException extends Exception {
        public LibraryCreationException(String message) {
            super(message);
        }
    }

    BundledLibraryConfiguration JAVA_RUNTIME_CONFIGURATION = new BundledLibraryConfiguration() {
        @Override
        public String getTitle() {
            return "Kotlin Java Runtime";
        }

        @Override
        public String getDefaultLibraryName() {
            return "KotlinJavaRuntime";
        }

        @Override
        public LibrariesContainer.LibraryLevel getDefaultLevel() {
            return LibrariesContainer.LibraryLevel.PROJECT;
        }

        @Override
        public String getDefaultCopyPath(@NotNull Module module) {
            return new File(PathUtil.getLocalPath(module.getProject().getBaseDir()), "lib").getAbsolutePath();
        }

        @Override
        public Library createLibrary(
                @NotNull Module module,
                @NotNull String libraryName,
                @NotNull final LibrariesContainer.LibraryLevel level,
                @NotNull final String destinationFolder
        ) throws LibraryCreationException {
            File runtimePath = org.jetbrains.jet.utils.PathUtil.getKotlinPathsForIdeaPlugin().getRuntimePath();
            if (!runtimePath.exists()) {
                throw new LibraryCreationException("Java Runtime library was not found. Make sure plugin is installed properly.");
            }

            File folder = new File(destinationFolder);
            File targetFile = new File(folder, runtimePath.getName());

            try {
                if (!targetFile.exists()) {
                    FileUtil.copy(runtimePath, targetFile);
                }
                else {
                    int replaceIfExist = Messages.showYesNoCancelDialog(
                            String.format("File \"%s\" already exist in %s. Do you want to rewrite it?", targetFile.getName(),
                                          folder.getAbsolutePath()),
                            "Replace File", Messages.getWarningIcon());

                    if (replaceIfExist == JOptionPane.YES_OPTION) {
                        FileUtil.copy(runtimePath, targetFile);
                    }
                }
            }
            catch (IOException e) {
                throw new LibraryCreationException("Failed to copy file: " + e.getMessage());
            }

            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);

            final NewLibraryEditor editor = new NewLibraryEditor();
            editor.setName(libraryName);
            editor.addRoot(VfsUtil.getUrlForLibraryRoot(targetFile), OrderRootType.CLASSES);
            editor.addRoot(VfsUtil.getUrlForLibraryRoot(targetFile) + "src", OrderRootType.SOURCES);

            final LibrariesContainer container = LibrariesContainerFactory.createContainer(module);

            return ApplicationManager.getApplication().runWriteAction(new Computable<Library>() {
                @Override
                public Library compute() {
                    return container.createLibrary(editor, level);
                }
            });
        }
    };

    BundledLibraryConfiguration JAVASCRIPT_STDLIB_CONFIGURATION = new BundledLibraryConfiguration() {
        @Override
        public String getTitle() {
            return "Kotlin JavaScript Standard Library";
        }

        @Override
        public String getDefaultLibraryName() {
            return "kotlin-jslib";
        }

        @Override
        public LibrariesContainer.LibraryLevel getDefaultLevel() {
            return LibrariesContainer.LibraryLevel.PROJECT;
        }

        @Override
        public String getDefaultCopyPath(@NotNull Module module) {
            return new File(PathUtil.getLocalPath(module.getProject().getBaseDir()), "lib").getAbsolutePath();
        }

        @Override
        public Library createLibrary(@NotNull Module module, @NotNull String libraryName, @NotNull final LibrariesContainer.LibraryLevel level, @NotNull String destinationFolder)
                throws LibraryCreationException {

            KotlinPaths paths = org.jetbrains.jet.utils.PathUtil.getKotlinPathsForIdeaPlugin();
            File jsLibJarPath = paths.getJsLibJarPath();

            if (!jsLibJarPath.exists()) {
                throw new LibraryCreationException("JavaScript library was not found. Make sure plugin is installed properly.");
            }

            File folder = new File(destinationFolder);
            File targetFile = new File(folder, jsLibJarPath.getName());

            // TODO: create folder if it is not present yet
            assert folder.exists();

            try {
                if (!targetFile.exists()) {
                    FileUtil.copy(jsLibJarPath, targetFile);
                }
                else {
                    int replaceIfExist = Messages.showYesNoCancelDialog(
                            String.format("File \"%s\" already exist in %s. Do you want to rewrite it?", targetFile.getName(),
                                          folder.getAbsolutePath()),
                            "Replace File", Messages.getWarningIcon());

                    if (replaceIfExist == JOptionPane.YES_OPTION) {
                        FileUtil.copy(jsLibJarPath, targetFile);
                    }
                }
            }
            catch (IOException e) {
                throw new LibraryCreationException("Failed to copy file: " + e.getMessage());
            }

            final VirtualFile libFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);

            final NewLibraryEditor editor = new NewLibraryEditor();
            editor.setName(libraryName);
            editor.addRoot(libFile, OrderRootType.SOURCES);

            final LibrariesContainer container = LibrariesContainerFactory.createContainer(module);

            return ApplicationManager.getApplication().runWriteAction(new Computable<Library>() {
                @Override
                public Library compute() {
                    return container.createLibrary(editor, level);
                }
            });
        }
    };
}
