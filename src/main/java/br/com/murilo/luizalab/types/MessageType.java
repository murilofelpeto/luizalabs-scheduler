package br.com.murilo.luizalab.types;

public enum MessageType {

    EMAIL("e-mail"),
    SMS("sms"),
    PUSH("push"),
    WHATSAPP("whatsapp");

    private final String messageType;

    MessageType(final String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
