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
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.util.PathUtil;

import java.io.File;

public interface BundledLibraryConfiguration {
    String getTitle();
    String getDefaultLibraryName();
    LibrariesContainer.LibraryLevel getDefaultLevel();
    String getDefaultCopyPath(Module module);

    Library createLibrary(String libraryName, LibrariesContainer.LibraryLevel level, String filePath);

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
        public String getDefaultCopyPath(Module module) {
            return new File(PathUtil.getLocalPath(module.getProject().getBaseDir()), "lib").getAbsolutePath();
        }

        @Override
        public Library createLibrary(String libraryName, LibrariesContainer.LibraryLevel level, String filePath) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        public String getDefaultCopyPath(Module module) {
            return new File(PathUtil.getLocalPath(module.getProject().getBaseDir()), "lib").getAbsolutePath();
        }

        @Override
        public Library createLibrary(String libraryName, LibrariesContainer.LibraryLevel level, String filePath) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };
}
