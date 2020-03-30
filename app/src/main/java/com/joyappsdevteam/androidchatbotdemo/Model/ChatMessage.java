package com.joyappsdevteam.androidchatbotdemo.Model;

public class ChatMessage {

    private boolean isImage, isMine;
    private String context;

    public ChatMessage(boolean isImage, boolean isMine, String context) {
        this.isImage = isImage;
        this.isMine = isMine;
        this.context = context;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
