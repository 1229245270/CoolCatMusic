package com.hzc.coolcatmusic.entity;

import java.util.List;

public class ChatGPTRequest {
    private String model;
    private List<Messages> messages;
    private String temperature;
    private String top_p;
    private String n;
    private String stream;
    private String stop;
    private String max_tokens;
    private String presence_penalty;
    private String frequency_penalty;
    private String logit_bias;
    private String user;
    public static class Messages{
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Messages> getMessages() {
        return messages;
    }

    public void setMessages(List<Messages> messages) {
        this.messages = messages;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTop_p() {
        return top_p;
    }

    public void setTop_p(String top_p) {
        this.top_p = top_p;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(String max_tokens) {
        this.max_tokens = max_tokens;
    }

    public String getPresence_penalty() {
        return presence_penalty;
    }

    public void setPresence_penalty(String presence_penalty) {
        this.presence_penalty = presence_penalty;
    }

    public String getFrequency_penalty() {
        return frequency_penalty;
    }

    public void setFrequency_penalty(String frequency_penalty) {
        this.frequency_penalty = frequency_penalty;
    }

    public String getLogit_bias() {
        return logit_bias;
    }

    public void setLogit_bias(String logit_bias) {
        this.logit_bias = logit_bias;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
