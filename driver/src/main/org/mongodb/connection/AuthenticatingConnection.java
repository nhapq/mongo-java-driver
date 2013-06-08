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

package org.mongodb.connection;

import org.mongodb.MongoCredential;

import java.util.List;

import static org.mongodb.assertions.Assertions.isTrue;
import static org.mongodb.assertions.Assertions.notNull;

class AuthenticatingConnection implements Connection {
    private volatile Connection wrapped;
    private final CachingAuthenticator authenticator;

    public AuthenticatingConnection(final Connection wrapped, final List<MongoCredential> credentialList,
                                    final BufferProvider bufferProvider) {
        this.wrapped = notNull("wrapped", wrapped);
        this.authenticator = new CachingAuthenticator(new MongoCredentialsStore(credentialList), wrapped, bufferProvider);
    }

    @Override
    public void close() {
        if (wrapped != null) {
            wrapped.close();
            wrapped = null;
        }
    }

    @Override
    public boolean isClosed() {
        return wrapped.isClosed();
    }

    @Override
    public ServerAddress getServerAddress() {
        isTrue("open", wrapped != null);
        return wrapped.getServerAddress();
    }

    @Override
    public void sendMessage(final ChannelAwareOutputBuffer buffer) {
        isTrue("open", wrapped != null);
        authenticator.authenticateAll();
        wrapped.sendMessage(buffer);
    }

    @Override
    public ResponseBuffers receiveMessage(final ResponseSettings responseSettings) {
        isTrue("open", wrapped != null);
        authenticator.authenticateAll();
        return wrapped.receiveMessage(responseSettings);
    }
}
