/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.api.twitter.model.impl;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.mariotaku.twidere.api.twitter.model.IDs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariotaku on 15/5/10.
 */
public class IDsImpl extends TwitterResponseImpl implements IDs {

    public static final TypeConverter<IDs> CONVERTER = new TypeConverter<IDs>() {
        @Override
        public IDs parse(JsonParser jsonParser) throws IOException {
            return MAPPER.parse(jsonParser);
        }

        @Override
        public void serialize(IDs object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) {
            throw new UnsupportedOperationException();
        }
    };

    public static final JsonMapper<IDs> MAPPER = new JsonMapper<IDs>() {
        @SuppressWarnings("TryWithIdenticalCatches")
        @Override
        public IDs parse(JsonParser jsonParser) throws IOException {
            IDsImpl instance = new IDsImpl();
            if (jsonParser.getCurrentToken() == null) {
                jsonParser.nextToken();
            }
            if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                parseIDsArray(instance, jsonParser);
            } else if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = jsonParser.getCurrentName();
                    jsonParser.nextToken();
                    parseField(instance, fieldName, jsonParser);
                    jsonParser.skipChildren();
                }
            } else {
                jsonParser.skipChildren();
                return null;
            }
            return instance;
        }

        @Override
        public void serialize(IDs activity, JsonGenerator jsonGenerator, boolean writeStartAndEnd) {
            throw new UnsupportedOperationException();
        }

        public void parseField(IDsImpl instance, String fieldName, JsonParser jsonParser) throws IOException {
            if ("ids".equals(fieldName)) {
                parseIDsArray(instance, jsonParser);
            } else if ("previous_cursor".equals(fieldName)) {
                instance.previousCursor = jsonParser.getValueAsLong();
            } else if ("next_cursor".equals(fieldName)) {
                instance.nextCursor = jsonParser.getValueAsLong();
            }
        }

        private void parseIDsArray(IDsImpl instance, JsonParser jsonParser) throws IOException {
            List<Long> collection1 = new ArrayList<>();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                collection1.add(jsonParser.getValueAsLong());
            }
            long[] array = new long[collection1.size()];
            int i = 0;
            for (long value : collection1) {
                array[i++] = value;
            }
            instance.ids = array;
        }

    };

    long previousCursor;
    long nextCursor;
    long[] ids;

    @Override
    public long getNextCursor() {
        return nextCursor;
    }

    @Override
    public long getPreviousCursor() {
        return previousCursor;
    }

    @Override
    public boolean hasNext() {
        return nextCursor != 0;
    }

    @Override
    public boolean hasPrevious() {
        return previousCursor != 0;
    }

    @Override
    public long[] getIDs() {
        return ids;
    }
}
