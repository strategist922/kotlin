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

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.plugin.JetIcons;

import javax.swing.*;

public class JetFacetType extends FacetType<JetFacet, JetFacetConfiguration> {
    @NonNls
    private static final String ID = "Kotlin";


    public JetFacetType() {
        super(JetFacet.JET_FACET_TYPE_ID, ID, "Kotlin", null);
    }

    @Override
    public JetFacetConfiguration createDefaultConfiguration() {
        return new JetFacetConfiguration();
    }

    @Override
    public JetFacet createFacet(
            @NotNull Module module, String name,
            @NotNull JetFacetConfiguration configuration, @Nullable Facet underlyingFacet
    ) {
        return new JetFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return moduleType instanceof JavaModuleType;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return JetIcons.SMALL_LOGO;
    }
}
