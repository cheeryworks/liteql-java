package org.cheeryworks.liteql.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;

import java.io.IOException;

public class LiteQLPrettyPrinter implements PrettyPrinter {

    private transient int nesting;

    @Override
    public void writeRootValueSeparator(JsonGenerator gen) throws IOException {

    }

    @Override
    public void writeStartObject(JsonGenerator gen) throws IOException {
        gen.writeRaw('{');
        nesting++;
    }

    @Override
    public void writeEndObject(JsonGenerator gen, int nrOfEntries) throws IOException {
        gen.writeRaw('\n');
        nesting--;
        gen.writeRaw(String.valueOf(' ').repeat(nesting * 2));
        gen.writeRaw('}');
    }

    @Override
    public void writeObjectEntrySeparator(JsonGenerator gen) throws IOException {
        gen.writeRaw(',');
        gen.writeRaw('\n');
        gen.writeRaw(String.valueOf(' ').repeat(nesting * 2));
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator gen) throws IOException {
        gen.writeRaw(':');
        gen.writeRaw(' ');
    }

    @Override
    public void writeStartArray(JsonGenerator gen) throws IOException {
        gen.writeRaw('[');
        nesting++;
    }

    @Override
    public void writeEndArray(JsonGenerator gen, int nrOfValues) throws IOException {
        gen.writeRaw('\n');
        nesting--;
        gen.writeRaw(String.valueOf(' ').repeat(nesting * 2));
        gen.writeRaw(']');
    }

    @Override
    public void writeArrayValueSeparator(JsonGenerator gen) throws IOException {
        gen.writeRaw(',');
        gen.writeRaw('\n');
        gen.writeRaw(String.valueOf(' ').repeat(nesting * 2));
    }

    @Override
    public void beforeArrayValues(JsonGenerator gen) throws IOException {
        gen.writeRaw('\n');
        gen.writeRaw(String.valueOf(' ').repeat(nesting * 2));
    }

    @Override
    public void beforeObjectEntries(JsonGenerator gen) throws IOException {
        gen.writeRaw('\n');
        gen.writeRaw(String.valueOf(' ').repeat(nesting * 2));
    }

}
