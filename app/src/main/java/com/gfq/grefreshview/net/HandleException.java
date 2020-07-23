package com.gfq.grefreshview.net;


public class HandleException extends Exception {
    private HandleException() {
    }
    private static class SingletonClassInstance {
        private static final HandleException instance = new HandleException();
    }
    public static HandleException getInstance() {
        return SingletonClassInstance.instance;
    }

    public  void Handle(String message, int code) {

    }

    public void Handle(Throwable e) {
    }
}
