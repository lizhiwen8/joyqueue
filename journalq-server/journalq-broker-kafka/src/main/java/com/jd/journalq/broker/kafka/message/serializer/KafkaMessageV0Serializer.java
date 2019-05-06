/**
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
package com.jd.journalq.broker.kafka.message.serializer;

import com.google.common.collect.Lists;
import com.jd.journalq.broker.kafka.message.KafkaBrokerMessage;
import com.jd.journalq.broker.kafka.message.compressor.KafkaCompressionCodec;
import com.jd.journalq.broker.kafka.util.KafkaBufferUtils;
import com.jd.journalq.message.BrokerMessage;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * KafkaMessageV0Serializer
 * author: gaohaoxiang
 * email: gaohaoxiang@jd.com
 * date: 2018/11/11
 */
public class KafkaMessageV0Serializer extends AbstractKafkaMessageSerializer {

    private static final int EXTENSION_V0_LENGTH = 1; // magic
    private static final int EXTENSION_V1_LENGTH = 1 + 8 + 8; // magic + timestamp + attribute
    private static final int CURRENT_EXTENSION_LENGTH = EXTENSION_V1_LENGTH;

    private static final byte CURRENT_MAGIC = MESSAGE_MAGIC_V0;

    public static void writeExtension(BrokerMessage brokerMessage, KafkaBrokerMessage kafkaBrokerMessage) {
        byte[] extension = new byte[CURRENT_EXTENSION_LENGTH];
        writeExtensionMagic(extension, CURRENT_MAGIC);
        writeExtensionTimestamp(extension, 0);
        writeExtensionAttribute(extension, kafkaBrokerMessage.getAttribute());
        brokerMessage.setExtension(extension);
    }

    public static void readExtension(BrokerMessage brokerMessage, KafkaBrokerMessage kafkaBrokerMessage) {
        byte[] extension = brokerMessage.getExtension();
        if (ArrayUtils.isEmpty(extension)) {
            return;
        }

        if (extension.length == EXTENSION_V1_LENGTH) {
            kafkaBrokerMessage.setAttribute(readExtensionAttribute(extension));
        }
    }

    public static void writeMessages(ByteBuf buffer, List<KafkaBrokerMessage> messages) throws Exception {
        for (KafkaBrokerMessage message : messages) {
            writeMessage(buffer, message);
        }
    }

    public static void writeMessage(ByteBuf buffer, KafkaBrokerMessage message) throws Exception {
        buffer.writeLong(message.getOffset());

        int startIndex = buffer.writerIndex();
        buffer.writeInt(0); // length
        buffer.writeInt(0); // crc
        buffer.writeByte(CURRENT_MAGIC);
        buffer.writeByte(message.getAttribute());

        KafkaBufferUtils.writeBytes(message.getKey(), buffer);
        KafkaBufferUtils.writeBytes(message.getValue(), buffer);

        // 计算整个message长度，包括长度本身的字节数
        int length = buffer.writerIndex() - startIndex;
        byte[] bytes = new byte[length];
        buffer.getBytes(startIndex, bytes);

        // 计算crc，不包括长度字节和crc字节，从magic开始
        long crc = KafkaBufferUtils.crc32(bytes, 4 + 4, bytes.length - 4 - 4);

        // 写入长度和crc
        buffer.setInt(startIndex, length - 4);
        buffer.setInt(startIndex + 4, (int) (crc & 0xffffffffL));
    }

    public static List<KafkaBrokerMessage> readMessages(ByteBuffer buffer) throws Exception {
        List<KafkaBrokerMessage> result = Lists.newLinkedList();
        while (buffer.hasRemaining()) {
            KafkaBrokerMessage message = readMessage(buffer);
            result.add(message);
        }
        return result;
    }

    public static KafkaBrokerMessage readMessage(ByteBuffer buffer) throws Exception {
        long offset = buffer.getLong();
        int size = buffer.getInt();
        int crc = buffer.getInt();
        byte magic = buffer.get();
        byte attribute = buffer.get();
        int keyLength = Math.max(buffer.getInt(), 0);
        int valueLength = Math.max(buffer.getInt(), 0);
        KafkaCompressionCodec compressionCodec = KafkaCompressionCodec.valueOf(getCompressionCodecType(attribute));

        byte[] body = new byte[keyLength + valueLength];
        buffer.get(body);

        KafkaBrokerMessage message = new KafkaBrokerMessage();
        message.setOffset(offset);
        message.setSize(size);
        message.setCrc(crc);
        message.setMagic(CURRENT_MAGIC);
        message.setAttribute(attribute);

        if (compressionCodec.equals(KafkaCompressionCodec.NoCompressionCodec)) {
            if (keyLength != 0) {
                byte[] key = new byte[keyLength];
                System.arraycopy(body, 0, key, 0, keyLength);
                message.setKey(key);
            }
            if (valueLength != 0) {
                byte[] value = new byte[valueLength];
                System.arraycopy(body, keyLength, value, 0, valueLength);
                message.setValue(value);
            }
        } else {
            message.setValue(body);
        }
        return message;
    }
}