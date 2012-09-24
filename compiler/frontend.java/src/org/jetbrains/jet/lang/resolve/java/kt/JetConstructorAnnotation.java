/*
 * Copyright 2010-2012 JetBrains s.r.o.
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

package org.jetbrains.jet.lang.resolve.java.kt;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.resolve.java.JavaDescriptorResolver;
import org.jetbrains.jet.lang.resolve.java.JvmStdlibNames;

/**
 * @author Stepan Koltsov
 */
public class JetConstructorAnnotation extends PsiAnnotationWithFlags {

    public static final JetConstructorAnnotation NULL_ANNOTATION = new JetConstructorAnnotation(null);

    public JetConstructorAnnotation(@Nullable PsiAnnotation psiAnnotation) {
        super(psiAnnotation);
    }

    @Override
    protected void initialize() {
        super.initialize();
        hidden = getBooleanAttribute(JvmStdlibNames.JET_CONSTRUCTOR_HIDDEN_FIELD, false);
    }

    private boolean hidden;

    /** @deprecated */
    public boolean hidden() {
        checkInitialized();
        return hidden;
    }

    public static JetConstructorAnnotation get(PsiMethod constructor, boolean kotlin) {
        final PsiAnnotation annotation =
                JavaDescriptorResolver.findAnnotation(constructor, JvmStdlibNames.JET_CONSTRUCTOR.getFqName().getFqName(), kotlin);
        return annotation != null ? new JetConstructorAnnotation(annotation) : NULL_ANNOTATION;
    }
}
