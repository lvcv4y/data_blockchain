package request;

import com.google.gson.*;
import dataclasses.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

            final JsonObject json = jsonElement.getAsJsonObject();

            if(!json.has(Request.TYPE)) {
                // we cannot deserialize this, because a request without any type makes no sense.
                throw new JsonParseException("Request type is null");
            }

            RequestTypes rType;
            try {
                rType = RequestTypes.valueOf(json.get(Request.TYPE).getAsString());
            } catch (ClassCastException | IllegalStateException | NullPointerException ignored) {
                // we cannot deserialize this, because a request without any type makes no sense.
                throw new JsonParseException("Request type is null");
            }

            ErrorCodes errorCodes = ErrorCodes.NO_ERROR;
            // if no error codes specified, we interpret this as no error
            try {
                errorCodes = ErrorCodes.valueOf(json.get(Request.ERROR_CODE).getAsString());
                // todo use of int code instead of name ?
            } catch (ClassCastException | IllegalStateException | NullPointerException ignored) { }

            ArrayList<Data> data = new ArrayList<>();

            if(json.has(Request.DATA)){

                JsonArray array = json.getAsJsonArray(Request.DATA);

                for(JsonElement el : array){
                    JsonObject objectEl = el.getAsJsonObject();

                    // element is either a Block, an AddressBalance, a Transaction or a FileChunk.
                    // if the fields are not following the given standards,
                    // deserialization will fail, exception will be thrown and
                    // the request will be ignored.

                    if(objectEl.has(FileChunk.FILE_DATA)) // el is a FileChunk
                        data.add(gson.fromJson(objectEl, FileChunk.class));
                    else if(objectEl.has(Transaction.AMOUNT)) // el is a Transaction
                        data.add(gson.fromJson(objectEl, Transaction.class));
                    else if(objectEl.has(AddressBalance.BALANCE)) // el is an AddressBalance
                        data.add(gson.fromJson(objectEl, AddressBalance.class));
                    else { // el is a Block
                        // so it is either a CurrentBlock, or an OldBlock

                        if(objectEl.has(OldBlock.MINER_ADDRESS)) // if mined, it is an OldBlock
                            data.add(gson.fromJson(objectEl, OldBlock.class));
                        else // not mined, so it is a CurrentBlock
                            data.add(gson.fromJson(objectEl, CurrentBlock.class));
                    }
                }
            }

            if(json.has(SignedRequest.SIGNATURE)){
                // if the request has a signature field, it must be a SignedRequest,
                // and not a regular Request.
                // we must however check that every security field
                // (signature, ip and author's public key) exists and has a valid type.

                String signature = null;
                try {
                    signature = json.get(Request.TYPE).getAsString();
                } catch (ClassCastException | IllegalStateException ignored) { }

                String authorPublicKey = null;
                try {
                    authorPublicKey = json.get(Request.TYPE).getAsString();
                } catch (ClassCastException | IllegalStateException ignored) { }

                String authorIp = null;
                try {
                    authorIp = json.get(Request.TYPE).getAsString();
                } catch (ClassCastException | IllegalStateException ignored) { }

                if(signature != null && authorPublicKey != null && authorIp != null){
                    // all fields exist and have been correctly extracted
                    return new SignedRequest(rType, errorCodes, data, signature, authorPublicKey, authorIp);
                }
            }

            // if we fail to convert it to a SignedRequest, we return a regular
            // Request object. It is not our task here to ensure that data
            // transmitted need, or not, the security layer given by
            // SignedRequest fields (see caller's flux for that).

            return new Request(rType, errorCodes, data);
        }
    }
}
