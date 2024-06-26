/*
 *  Copyright 2023 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.gradle.tasks;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.builder.BuildStrategy;

public abstract class GenerateWasmTask extends TeaVMTask {
    private static final int MB = 1024 * 1024;

    public GenerateWasmTask() {
        getExceptionsUsed().convention(false);
        getMinHeapSize().convention(1);
        getMaxHeapSize().convention(16);
    }

    @Input
    public abstract Property<Boolean> getExceptionsUsed();

    @Input
    public abstract Property<Integer> getMinHeapSize();

    @Input
    public abstract Property<Integer> getMaxHeapSize();

    @Override
    protected void setupBuilder(BuildStrategy builder) {
        builder.setTargetType(TeaVMTargetType.WEBASSEMBLY);
        builder.setWasmExceptionsUsed(getExceptionsUsed().get());
        builder.setMinHeapSize(getMinHeapSize().get() * MB);
        builder.setMaxHeapSize(getMaxHeapSize().get() * MB);
    }
}
