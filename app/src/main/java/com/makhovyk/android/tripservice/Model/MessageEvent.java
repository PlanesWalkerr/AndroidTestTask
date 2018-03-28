package com.makhovyk.android.tripservice.Model;

/**
 * Created by misha on 3/28/18.
 */

public class MessageEvent {

    public final String message;
    public final String errorMessage;

    public MessageEvent(String message, String errorMessage) {
        this.message = message;
        this.errorMessage = errorMessage;
    }
}
