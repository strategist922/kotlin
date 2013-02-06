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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JetFacetSettings {
    private final static String DEFAULT_RUNTIME_LIBRARY_NAME = "kotlin-runtime";
    private final static String DEFAULT_JAVASCRIPT_FOLDER_NAME = "lib";

    private boolean isJavaModule = true;
    private String runtimeLibraryName = DEFAULT_RUNTIME_LIBRARY_NAME;
    private String jsLibraryFolder = DEFAULT_JAVASCRIPT_FOLDER_NAME;

    public boolean isJavaModule() {
        return isJavaModule;
    }

    public void setJavaModule(boolean javaModule) {
        isJavaModule = javaModule;
    }

    @NotNull
    public String getRuntimeLibraryName() {
        return runtimeLibraryName;
    }

    public void setRuntimeLibraryName(@Nullable String runtimeLibraryName) {
        this.runtimeLibraryName = runtimeLibraryName != null ? runtimeLibraryName : DEFAULT_RUNTIME_LIBRARY_NAME;
    }

    @NotNull
    public String getJsLibraryFolder() {
        return jsLibraryFolder;
    }

    public void setJsLibraryFolder(@Nullable String jsLibraryFolder) {
        this.jsLibraryFolder = jsLibraryFolder != null ? jsLibraryFolder : DEFAULT_JAVASCRIPT_FOLDER_NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JetFacetSettings settings = (JetFacetSettings) o;

        if (isJavaModule != settings.isJavaModule) return false;
        if (!jsLibraryFolder.equals(settings.jsLibraryFolder)) return false;
        if (!runtimeLibraryName.equals(settings.runtimeLibraryName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isJavaModule ? 1 : 0);
        result = 31 * result + runtimeLibraryName.hashCode();
        result = 31 * result + jsLibraryFolder.hashCode();
        return result;
    }
}
