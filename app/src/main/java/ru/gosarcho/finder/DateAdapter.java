package ru.gosarcho.finder;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateAdapter extends TypeAdapter<Date> {
    @Override
    public void write(final JsonWriter jsonWriter, final Date date ) throws IOException {
        jsonWriter.value(date.toString());
    }

    @Override
    public Date read( final JsonReader jsonReader ) throws IOException {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(jsonReader.nextString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
