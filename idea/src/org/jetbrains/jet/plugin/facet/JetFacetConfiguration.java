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

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.plugin.facet.ui.JetFacetEditorTab;

@State(name = JetFacetConfiguration.COMPONENT_NAME, storages = {@Storage(file = "$MODULE_FILE$")})
public class JetFacetConfiguration implements FacetConfiguration, PersistentStateComponent<JetFacetSettings> {
    public static final String COMPONENT_NAME = "JetFacetConfiguration";

    private final JetFacetSettings settingsData = new JetFacetSettings();

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[] { new JetFacetEditorTab(settingsData, editorContext, validatorsManager) };
    }

    @Deprecated
    @Override
    public void readExternal(Element element) throws InvalidDataException {
    }

    @Deprecated
    @Override
    public void writeExternal(Element element) throws WriteExternalException {
    }

    @Nullable
    @Override
    public JetFacetSettings getState() {
        return settingsData;
    }

    @Override
    public void loadState(JetFacetSettings state) {
        XmlSerializerUtil.copyBean(state, settingsData);
    }
}
