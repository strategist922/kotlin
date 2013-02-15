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

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.impl.libraries.ApplicationLibraryTable;
import com.intellij.openapi.roots.impl.libraries.LibraryTableImplUtil;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class LibraryUtils {
    private LibraryUtils() {
    }

    @NotNull
    public static LibraryTable getLibraryTable(Module module, @NotNull LibrariesContainer.LibraryLevel level) {
        switch (level) {
            case MODULE:
                return ModuleRootManager.getInstance(module).getModifiableModel().getModuleLibraryTable();
            case PROJECT:
                return LibraryTablesRegistrar.getInstance().getLibraryTable(module.getProject());
            case GLOBAL:
                return LibraryTablesRegistrar.getInstance().getLibraryTable();
            default:
                throw new InvalidParameterException("Unexpected library level");
        }
    }

    @NotNull
    public static LibrariesContainer.LibraryLevel getLibraryLevel(@NotNull Library library) {
        String level = library.getTable().getTableLevel();

        if (LibraryTableImplUtil.MODULE_LEVEL.equals(level)) {
            return LibrariesContainer.LibraryLevel.MODULE;
        }
        else if (LibraryTablesRegistrar.PROJECT_LEVEL.equals(level)) {
            return LibrariesContainer.LibraryLevel.PROJECT;
        }
        else if (LibraryTablesRegistrar.APPLICATION_LEVEL.equals(level)) {
            return LibrariesContainer.LibraryLevel.GLOBAL;
        }

        throw new InvalidParameterException("Unexpected library level");
    }

    protected static Collection<Library> getLibraries(Project project) {
        Library[] projectLibraries = ProjectLibraryTable.getInstance(project).getLibraries();
        Library[] globalLibraries = ApplicationLibraryTable.getApplicationTable().getLibraries();

        ArrayList<Library> libraries = Lists.newArrayList();
        libraries.addAll(Arrays.asList(projectLibraries));
        libraries.addAll(Arrays.asList(globalLibraries));

        return libraries;
    }

    @Nullable
    public static Library findLibrary(@NotNull Module module, @NotNull LibrariesContainer.LibraryLevel level, @NotNull String name) {
        Library[] libraries = LibrariesContainerFactory.createContainer(module).getLibraries(level);

        for (Library library : libraries) {
            if (name.equals(name)) {
                return library;
            }
        }

        return null;
    }
}
