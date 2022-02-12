package request;

import com.google.gson.*;

import java.lang.reflect.Type;

public class JsonConverter {

    public static final Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new Serializer()).create();

    public static Request deserialize(String json){
        return gson.fromJson(json, Request.class);
    }

    public static String serialize(Request request){
        return gson.toJson(request);
    }


    public static class Serializer implements JsonDeserializer<Request> {


        @Override
        public Request deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {

            return null;
        }
    }
}
