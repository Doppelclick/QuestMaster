package com.QuestMaster.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

//from SBA under then GNU 3.0 license
public class GsonInitialisableTypeAdapter implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                T object = delegate.read(in);
                if (object instanceof GsonInitializable) {
                    ((GsonInitializable) object).gsonInit();
                }
                return object;
            }
        };
    }

    public interface GsonInitializable {
        void gsonInit();
    }
}
