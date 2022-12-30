package com.uroria.kebab.utils;

public class MojangAPI {
    public static class SkinResponse {
        final String skin;
        final String signature;
        public SkinResponse(String skin, String signature) {
            this.skin = skin;
            this.signature = signature;
        }

        public String getSkin() {
            return skin;
        }

        public String getSignature() {
            return signature;
        }
    }


}
