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

import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import org.jetbrains.annotations.Nullable;

public final class JetFacetSettings {
    private boolean isJavaModule = true;

    private String javaRuntimeLibraryName = null;
    private LibrariesContainer.LibraryLevel javaRuntimeLibraryLevel = LibrariesContainer.LibraryLevel.PROJECT;
    private String jsStdLibraryName = null;
    private LibrariesContainer.LibraryLevel jsStdLibraryLevel = LibrariesContainer.LibraryLevel.PROJECT;
    private String jsLibraryFolder = null;

    JetFacetSettings(
            boolean javaModule,
            @Nullable String javaRuntimeLibraryName,
            @Nullable LibrariesContainer.LibraryLevel javaRuntimeLibraryLevel,
            @Nullable String jsStdLibraryName,
            @Nullable LibrariesContainer.LibraryLevel jsStdLibraryLevel,
            @Nullable String jsLibraryFolder
    ) {
        isJavaModule = javaModule;
        this.javaRuntimeLibraryName = javaRuntimeLibraryName;
        this.javaRuntimeLibraryLevel = javaRuntimeLibraryLevel;
        this.jsStdLibraryName = jsStdLibraryName;
        this.jsStdLibraryLevel = jsStdLibraryLevel;
        this.jsLibraryFolder = jsLibraryFolder;
    }

    public JetFacetSettings() {
    }

    public boolean isJavaModule() {
        return isJavaModule;
    }

    public void setJavaModule(boolean javaModule) {
        isJavaModule = javaModule;
    }

    @Nullable
    public String getJavaRuntimeLibraryName() {
        return javaRuntimeLibraryName;
    }

    public void setJavaRuntimeLibraryName(@Nullable String javaRuntimeLibraryName) {
        this.javaRuntimeLibraryName = javaRuntimeLibraryName;
    }

    @Nullable
    public LibrariesContainer.LibraryLevel getJavaRuntimeLibraryLevel() {
        return javaRuntimeLibraryLevel;
    }

    public void setJavaRuntimeLibraryLevel(@Nullable LibrariesContainer.LibraryLevel javaRuntimeLibraryLevel) {
        this.javaRuntimeLibraryLevel = javaRuntimeLibraryLevel;
    }

    @Nullable
    public String getJsStdLibraryName() {
        return jsStdLibraryName;
    }

    public void setJsStdLibraryName(@Nullable String jsStdLibraryName) {
        this.jsStdLibraryName = jsStdLibraryName;
    }

    @Nullable
    public LibrariesContainer.LibraryLevel getJsStdLibraryLevel() {
        return jsStdLibraryLevel;
    }

    public void setJsStdLibraryLevel(@Nullable LibrariesContainer.LibraryLevel jsStdLibraryLevel) {
        this.jsStdLibraryLevel = jsStdLibraryLevel;
    }

    @Nullable
    public String getJsLibraryFolder() {
        return jsLibraryFolder;
    }

    public void setJsLibraryFolder(@Nullable String jsLibraryFolder) {
        this.jsLibraryFolder = jsLibraryFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JetFacetSettings settings = (JetFacetSettings) o;

        if (isJavaModule != settings.isJavaModule) return false;
        if (javaRuntimeLibraryLevel != settings.javaRuntimeLibraryLevel) return false;
        if (javaRuntimeLibraryName != null
            ? !javaRuntimeLibraryName.equals(settings.javaRuntimeLibraryName)
            : settings.javaRuntimeLibraryName != null) {
            return false;
        }
        if (jsLibraryFolder != null ? !jsLibraryFolder.equals(settings.jsLibraryFolder) : settings.jsLibraryFolder != null) return false;
        if (jsStdLibraryLevel != settings.jsStdLibraryLevel) return false;
        if (jsStdLibraryName != null ? !jsStdLibraryName.equals(settings.jsStdLibraryName) : settings.jsStdLibraryName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isJavaModule ? 1 : 0);
        result = 31 * result + (javaRuntimeLibraryName != null ? javaRuntimeLibraryName.hashCode() : 0);
        result = 31 * result + (javaRuntimeLibraryLevel != null ? javaRuntimeLibraryLevel.hashCode() : 0);
        result = 31 * result + (jsStdLibraryName != null ? jsStdLibraryName.hashCode() : 0);
        result = 31 * result + (jsStdLibraryLevel != null ? jsStdLibraryLevel.hashCode() : 0);
        result = 31 * result + (jsLibraryFolder != null ? jsLibraryFolder.hashCode() : 0);
        return result;
    }
}
