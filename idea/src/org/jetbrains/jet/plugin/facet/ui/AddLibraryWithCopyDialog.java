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

import com.intellij.facet.impl.ui.libraries.LibraryCompositionSettings;
import com.intellij.facet.impl.ui.libraries.LibraryOptionsPanel;
import com.intellij.framework.library.FrameworkLibraryVersionFilter;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PairConsumer;
import com.intellij.util.ParameterizedRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class AddLibraryWithCopyDialog extends DialogWrapper {
    public interface LibraryCreateContext {
        ModifiableRootModel getRootModule();
        LibraryCompositionSettings getLibrarySettings();
    }

    private static class LibraryCreateContextImpl implements LibraryCreateContext {
        private final ModifiableRootModel model;
        private final LibraryCompositionSettings settings;

        public LibraryCreateContextImpl(ModifiableRootModel model, LibraryCompositionSettings settings) {
            this.model = model;
            this.settings = settings;
        }

        @Override
        public ModifiableRootModel getRootModule() {
            return model;
        }

        @Override
        public LibraryCompositionSettings getLibrarySettings() {
            return settings;
        }
    }

    private final CreateLibraryFromBundledPanel myPanel;
    private final LibrariesContainer myLibrariesContainer;
    private final Module myModule;
    private final ModifiableRootModel myModifiableRootModel;
    private final @Nullable ParameterizedRunnable<LibraryCreateContext> myBeforeLibraryAdded;
    private final List<Library> myAddedLibraries = new ArrayList<Library>();

    private AddLibraryWithCopyDialog(
            @NotNull CustomLibraryDescription description,
            @NotNull LibrariesContainer librariesContainer,
            @NotNull Module module,
            @Nullable ModifiableRootModel modifiableRootModel,
            @Nullable ParameterizedRunnable<LibraryCreateContext> beforeLibraryAdded) {
        super(module.getProject(), true);

        myLibrariesContainer = librariesContainer;
        myModule = module;
        myModifiableRootModel = modifiableRootModel;
        myBeforeLibraryAdded = beforeLibraryAdded;

        setTitle(IdeBundle.message("setup.library.dialog.title"));

        VirtualFile baseDir = myModule.getProject().getBaseDir();
        final String baseDirPath = baseDir != null ? baseDir.getPath() : "";
        myPanel = new CreateLibraryFromBundledPanel(description, baseDirPath, FrameworkLibraryVersionFilter.ALL, myLibrariesContainer, false);


        Disposer.register(myDisposable, myPanel);

        init();
    }

    public static AddLibraryWithCopyDialog createDialog(
            @NotNull CustomLibraryDescription description,
            @NotNull Module module,
            @Nullable ParameterizedRunnable<LibraryCreateContext> beforeLibraryAdded) {
        return createDialog(description, LibrariesContainerFactory.createContainer(module), module, null, beforeLibraryAdded);
    }

    public static AddLibraryWithCopyDialog createDialog(
            @NotNull CustomLibraryDescription description,
            @NotNull LibrariesContainer librariesContainer,
            @NotNull Module module,
            @Nullable ModifiableRootModel modifiableRootModel,
            @Nullable ParameterizedRunnable<LibraryCreateContext> beforeLibraryAdded) {
        return new AddLibraryWithCopyDialog(description, librariesContainer, module, modifiableRootModel, beforeLibraryAdded);
    }

    @Override
    protected JComponent createCenterPanel() {
        return myPanel.getMainPanel();
    }

    @Override
    protected void doOKAction() {
        final LibraryCompositionSettings settings = myPanel.apply();
        if (settings != null && settings.downloadFiles(myPanel.getMainPanel())) {
            if (myModifiableRootModel == null) {
                final ModifiableRootModel model = ModuleRootManager.getInstance(myModule).getModifiableModel();
                new WriteAction() {
                    @Override
                    protected void run(final Result result) {
                        addLibraries(model, settings);
                        model.commit();
                    }
                }.execute();
            }
            else {
                addLibraries(myModifiableRootModel, settings);
            }
            super.doOKAction();
        }
    }

    private void addLibraries(ModifiableRootModel model, final LibraryCompositionSettings settings) {
        if (myBeforeLibraryAdded != null) {
            myBeforeLibraryAdded.run(new LibraryCreateContextImpl(model, settings));
        }
        settings.addLibraries(model, myAddedLibraries, myLibrariesContainer);
    }

    public List<Library> getAddedLibraries() {
        return myAddedLibraries;
    }
}
