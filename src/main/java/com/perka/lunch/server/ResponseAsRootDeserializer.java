package com.perka.lunch.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class ResponseAsRootDeserializer implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> adapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                final JsonElement rootElement = adapter.read(in);
                if (!rootElement.isJsonObject()) {
                    return delegate.fromJsonTree(rootElement);
                }
                final JsonObject rootJsonObject = rootElement.getAsJsonObject();
                final JsonElement response = rootJsonObject.get("response");
                return delegate.fromJsonTree(response);
            }
        }.nullSafe();
    }
}
