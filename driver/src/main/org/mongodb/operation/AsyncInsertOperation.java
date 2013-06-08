/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation;

import org.mongodb.Encoder;
import org.mongodb.MongoNamespace;
import org.mongodb.WriteConcern;
import org.mongodb.connection.BufferProvider;
import org.mongodb.operation.protocol.InsertMessage;
import org.mongodb.operation.protocol.MessageSettings;
import org.mongodb.operation.protocol.RequestMessage;

public class AsyncInsertOperation<T> extends AsyncWriteOperation {
    private final Insert<T> insert;
    private final Encoder<T> encoder;

    public AsyncInsertOperation(final MongoNamespace namespace, final Insert<T> insert,
                                final Encoder<T> encoder, final BufferProvider bufferProvider) {
        super(namespace, bufferProvider);
        this.insert = insert;
        this.encoder = encoder;
    }

    @Override
    protected RequestMessage createRequestMessage(final MessageSettings settings) {
       return new InsertMessage<T>(getNamespace().getFullName(), insert, encoder, settings);
    }

    @Override
    public Insert<T> getWrite() {
        return insert;
    }

    @Override
    public WriteConcern getWriteConcern() {
        return insert.getWriteConcern();
    }
}