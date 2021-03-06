/**
 * Copyright 2019 The JoyQueue Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joyqueue.nsr.composition.config;

import org.joyqueue.toolkit.config.PropertySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CompositionConfig
 * author: gaohaoxiang
 * date: 2019/8/12
 */
public class CompositionConfig {

    protected static final Logger logger = LoggerFactory.getLogger(CompositionConfig.class);

    private PropertySupplier propertySupplier;

    public CompositionConfig(PropertySupplier propertySupplier) {
        this.propertySupplier = propertySupplier;
    }

    public String getReadSource() {
        return PropertySupplier.getValue(propertySupplier, CompositionConfigKey.READ_SOURCE);
    }

    public String getWriteSource() {
        return PropertySupplier.getValue(propertySupplier, CompositionConfigKey.WRITE_SOURCE);
    }

    public boolean isWriteAll() {
        return getWriteSource().equalsIgnoreCase("all");
    }

    public boolean isReadIgnite() {
        return getReadSource().equalsIgnoreCase("ignite");
    }

    public boolean isWriteIgnite() {
        return isWriteAll() || getWriteSource().equalsIgnoreCase("ignite");
    }

    public boolean isReadJournalkeeper() {
        return getReadSource().equalsIgnoreCase("journalkeeper");
    }

    public boolean isWriteJournalkeeper() {
        return isWriteAll() || getReadSource().equalsIgnoreCase("journalkeeper");
    }
}