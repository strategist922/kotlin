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

package org.jetbrains.jet.plugin.facet;

import com.intellij.facet.FacetManager;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.NewLibraryEditor;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.util.Computable;
import org.jetbrains.jet.plugin.facet.ui.BundledLibraryConfiguration;
import org.jetbrains.jet.plugin.facet.ui.CreateBundledLibraryDialog;

public class JetFacetInEditorConfigurator {
    private JetFacetInEditorConfigurator() {}

    public static boolean configureAsJavaModule(Module module) {
        return addFacet(module, getSettingsForJavaConfiguration(module));
    }

    public static boolean configureAsJavaScriptModule(Module module) {
        return addFacet(module, getSettingsForJavaScriptConfiguration(module));
    }

    private static boolean addFacet(Module module, JetFacetSettings settings) {
        if (settings == null) {
            // Configuring was canceled
            return false;
        }

        final JetFacet facet = JetFacetType.getInstance().createFacet(module, settings);

        final ModifiableFacetModel model = FacetManager.getInstance(module).createModifiableModel();
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                model.addFacet(facet);
                model.commit();
            }
        });

        return true;
    }

    private static JetFacetSettings getSettingsForJavaConfiguration(final Module module) {
        CreateBundledLibraryDialog libraryDialog = new CreateBundledLibraryDialog(module, BundledLibraryConfiguration.JAVA_RUNTIME_CONFIGURATION);
        libraryDialog.show();

        if (libraryDialog.isOK()) {
            final LibrariesContainer.LibraryLevel level = libraryDialog.getLibraryLevel();

            final NewLibraryEditor editor = libraryDialog.getLibraryEditor();
            assert editor != null : "Library editor should have been created";

            Library library = ApplicationManager.getApplication().runWriteAction(new Computable<Library>() {
                @Override
                public Library compute() {
                    return LibrariesContainerFactory.createContainer(module).createLibrary(editor, level);
                }
            });

            return new JetFacetSettings(true, library.getName(), level, null, null, null);
        }

        // Configuration was canceled
        return null;
    }

    private static JetFacetSettings getSettingsForJavaScriptConfiguration(final Module module) {
        CreateBundledLibraryDialog libraryDialog = new CreateBundledLibraryDialog(module, BundledLibraryConfiguration.JAVASCRIPT_STDLIB_CONFIGURATION);
        libraryDialog.show();

        if (libraryDialog.isOK()) {
            final LibrariesContainer.LibraryLevel level = libraryDialog.getLibraryLevel();

            final NewLibraryEditor editor = libraryDialog.getLibraryEditor();
            assert editor != null : "Library editor should have been created";

            Library library = ApplicationManager.getApplication().runWriteAction(new Computable<Library>() {
                @Override
                public Library compute() {
                    return LibrariesContainerFactory.createContainer(module).createLibrary(editor, level);
                }
            });

            return new JetFacetSettings(false, null, null, library.getName(), level, null);
        }

        // Configuration was canceled
        return null;
    }
}
