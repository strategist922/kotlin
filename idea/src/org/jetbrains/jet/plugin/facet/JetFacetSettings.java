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
    private final static String DEFAULT_JS_SOURCES_LIBRARY_NAME = "kotlin-jslib";

    private boolean isJavaModule = true;
    private String javaRuntimeLibraryName = DEFAULT_RUNTIME_LIBRARY_NAME;
    private String jsSourcesLibraryName = DEFAULT_JS_SOURCES_LIBRARY_NAME;
    private String jsLibraryFolder = DEFAULT_JAVASCRIPT_FOLDER_NAME;

    public boolean isJavaModule() {
        return isJavaModule;
    }

    public void setJavaModule(boolean javaModule) {
        isJavaModule = javaModule;
    }

    @NotNull
    public String getJavaRuntimeLibraryName() {
        return javaRuntimeLibraryName;
    }

    public void setJavaRuntimeLibraryName(@Nullable String javaRuntimeLibraryName) {
        this.javaRuntimeLibraryName = javaRuntimeLibraryName != null ? javaRuntimeLibraryName : DEFAULT_RUNTIME_LIBRARY_NAME;
    }

    public String getJsSourcesLibraryName() {
        return jsSourcesLibraryName;
    }

    public void setJsSourcesLibraryName(@Nullable String jsSourcesLibraryName) {
        this.jsSourcesLibraryName = jsSourcesLibraryName != null ? jsSourcesLibraryName : DEFAULT_JS_SOURCES_LIBRARY_NAME;
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
        if (!javaRuntimeLibraryName.equals(settings.javaRuntimeLibraryName)) return false;
        if (!jsLibraryFolder.equals(settings.jsLibraryFolder)) return false;
        if (!jsSourcesLibraryName.equals(settings.jsSourcesLibraryName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isJavaModule ? 1 : 0);
        result = 31 * result + javaRuntimeLibraryName.hashCode();
        result = 31 * result + jsSourcesLibraryName.hashCode();
        result = 31 * result + jsLibraryFolder.hashCode();
        return result;
    }
}
