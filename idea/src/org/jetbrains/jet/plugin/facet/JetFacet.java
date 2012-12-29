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
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public class JetFacet extends Facet<JetFacetConfiguration> {
    public static final FacetTypeId<JetFacet> JET_FACET_TYPE_ID = new FacetTypeId<JetFacet>("Kotlin");

    public JetFacet(
            @NotNull FacetType<JetFacet, JetFacetConfiguration> facetType,
            @NotNull Module module,
            @NotNull String name,
            @NotNull JetFacetConfiguration configuration,
            Facet underlyingFacet
    ) {
        super(facetType, module, name, configuration, underlyingFacet);
    }
}
