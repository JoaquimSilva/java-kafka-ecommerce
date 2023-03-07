package br.com.alura.ecommerce;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    private final String TYPE = "type";
    private final String PAYLOAD = "payload";
    private final String CORRELATION = "correlationId";

    @Override
    public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        object.addProperty(TYPE, message.getPayLoad().getClass().getName());
        object.add(PAYLOAD, context.serialize(message.getPayLoad()));
        object.add(CORRELATION, context.serialize(message.getCorrelationId()));

        return object;
    }

    @Override
    public Message deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            var object = json.getAsJsonObject();
            var payloadType = object.get(TYPE).getAsString();
            var correlationId = (CorrelationId) context.deserialize(object.get(CORRELATION), CorrelationId.class);
            var payload = context.deserialize(object.get(PAYLOAD), payloadType.getClass());
            return new Message(correlationId, payload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
