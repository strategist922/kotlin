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

package org.jetbrains.jet.plugin.framework;

import com.google.common.collect.Sets;
import com.intellij.framework.library.LibraryVersionProperties;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration;
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.plugin.framework.ui.FileUIUtils;
import org.jetbrains.jet.plugin.framework.ui.FrameworkSourcePanel;
import org.jetbrains.jet.utils.PathUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class JetJavaScriptLibraryDescription extends CustomLibraryDescription {
    public static final LibraryKind KOTLIN_JAVASCRIPT_HEADERS_KIND = LibraryKind.create("JetJavaScriptLibraryDescription");

    private static final String JAVA_SCRIPT_LIBRARY_CREATION = "JavaScript Library Creation";

    @Nullable
    private FrameworkSourcePanel configurationPanel;

    public void setConfigurationPanel(@Nullable FrameworkSourcePanel configurationPanel) {
        this.configurationPanel = configurationPanel;
    }

    @NotNull
    @Override
    public Set<? extends LibraryKind> getSuitableLibraryKinds() {
        return Sets.newHashSet(KOTLIN_JAVASCRIPT_HEADERS_KIND);
    }

    @Nullable
    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent, @Nullable VirtualFile contextDirectory) {
        if (configurationPanel == null || configurationPanel.isConfigureFromBundled()) {
            return createFromPlugin(parentComponent, contextDirectory);
        }
        else {
            throw new IllegalStateException("Feature isn't ready yet");
        }
   }

    private NewLibraryConfiguration createFromPlugin(JComponent parentComponent, VirtualFile contextDirectory) {
        File runtimePath = PathUtil.getKotlinPathsForIdeaPlugin().getJsLibJarPath();

        if (!runtimePath.exists()) {
            Messages.showErrorDialog("JavaScript standard library was not found. Make sure plugin is installed properly.",
                                     JAVA_SCRIPT_LIBRARY_CREATION);
            return null;
        }

        final VirtualFile directory = FileUIUtils.selectCopyToDirectory(
                "Select folder where Kotlin JavaScript header should be copied",
                parentComponent, contextDirectory);

        if (directory == null) {
            return null;
        }

        final File targetFile;
        try {
            targetFile = FileUIUtils.copyWithOverwriteDialog(directory, runtimePath);
        }
        catch (IOException e) {
            Messages.showErrorDialog("Error during file copy", JAVA_SCRIPT_LIBRARY_CREATION);
            return null;
        }

        return new NewLibraryConfiguration(PathUtil.JS_LIB_JAR_NAME, getDownloadableLibraryType(), new LibraryVersionProperties()) {
            @Override
            public void addRoots(@NotNull LibraryEditor editor) {
                editor.addRoot(VfsUtil.getUrlForLibraryRoot(targetFile), OrderRootType.SOURCES);
            }
        };
    }
}
