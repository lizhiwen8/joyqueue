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
package org.joyqueue.server.retry.api;

import org.joyqueue.domain.ConsumeRetry;
import org.joyqueue.exception.JoyQueueException;
import org.joyqueue.model.PageResult;
import org.joyqueue.server.retry.model.RetryQueryCondition;
import org.joyqueue.server.retry.model.RetryStatus;

/**
 * Created by chengzhiliang on 2019/2/20.
 */
public interface ConsoleMessageRetry<T> extends MessageRetry<T> {

    PageResult<ConsumeRetry> queryConsumeRetryList(RetryQueryCondition retryQueryCondition) throws JoyQueueException;

    ConsumeRetry getConsumeRetryById(Long id,String topic) throws JoyQueueException;

    void updateStatus(String topic, String app, T[] messageId, RetryStatus status, long updateTime, int updateBy) throws Exception;

    void batchUpdateStatus(RetryQueryCondition retryQueryCondition, RetryStatus status, long updateTime, int updateBy) throws Exception;
}
