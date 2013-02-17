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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.NewLibraryEditor;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.utils.KotlinPaths;

import java.io.File;
import java.io.IOException;

public interface BundledLibraryConfiguration {
    String getTitle();
    String getDefaultLibraryName();
    LibrariesContainer.LibraryLevel getDefaultLevel();
    String getDefaultCopyPath(@NotNull Module module);

    NewLibraryEditor createNewLibraryEditor(@NotNull String libraryName, @NotNull String destinationFolder) throws LibraryCreationException;

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
        public NewLibraryEditor createNewLibraryEditor(@NotNull String libraryName, @NotNull final String destinationFolder) throws LibraryCreationException {
            File runtimePath = org.jetbrains.jet.utils.PathUtil.getKotlinPathsForIdeaPlugin().getRuntimePath();
            if (!runtimePath.exists()) {
                throw new LibraryCreationException("Java Runtime library was not found. Make sure plugin is installed properly.");
            }

            File targetFile;
            try {
                targetFile = CopyFileUtil.copyWithOverwriteDialog(destinationFolder, runtimePath);
            }
            catch (IOException e) {
                throw new LibraryCreationException("Failed to copy Java Runtime jar file");
            }

            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);

            final NewLibraryEditor editor = new NewLibraryEditor();
            editor.setName(libraryName);
            editor.addRoot(VfsUtil.getUrlForLibraryRoot(targetFile), OrderRootType.CLASSES);
            editor.addRoot(VfsUtil.getUrlForLibraryRoot(targetFile) + "src", OrderRootType.SOURCES);

            return editor;
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
        public NewLibraryEditor createNewLibraryEditor(@NotNull String libraryName, @NotNull String destinationFolder) throws LibraryCreationException {
            KotlinPaths paths = org.jetbrains.jet.utils.PathUtil.getKotlinPathsForIdeaPlugin();
            File jsLibJarPath = paths.getJsLibJarPath();

            if (!jsLibJarPath.exists()) {
                throw new LibraryCreationException("JavaScript library was not found. Make sure plugin is installed properly.");
            }

            File targetFile;
            try {
                targetFile = CopyFileUtil.copyWithOverwriteDialog(destinationFolder, jsLibJarPath);
            }
            catch (IOException e) {
                throw new LibraryCreationException("Failed to copy JavaScript library.");
            }

            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);

            final NewLibraryEditor editor = new NewLibraryEditor();
            editor.setName(libraryName);
            editor.addRoot(VfsUtil.getUrlForLibraryRoot(targetFile), OrderRootType.SOURCES);

            return editor;
        }
    };
}
