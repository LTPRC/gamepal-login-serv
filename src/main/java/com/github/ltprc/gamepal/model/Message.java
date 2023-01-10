package com.github.ltprc.gamepal.model;

public abstract class Message {
    protected String fromUuid;
    protected String toUuid;
    protected Integer type;
    protected String content;
    public String getFromUuid() {
        return fromUuid;
    }
    public void setFromUuid(String fromUuid) {
        this.fromUuid = fromUuid;
    }
    public String getToUuid() {
        return toUuid;
    }
    public void setToUuid(String toUuid) {
        this.toUuid = toUuid;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
