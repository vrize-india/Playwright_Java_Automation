package com.tonic.exceptions;

@SuppressWarnings("serial")
public class PropertyFileHandleException extends FrameworkException {

    /**
     * Pass the message that needs to be appended to the stacktrace
     * @param message Details about the exception or custom message
     */
    public PropertyFileHandleException(String message) {
        super(message);
    }

}
