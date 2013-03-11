/**
 * Copyright (c) 2000-2006, Serhiy Yevtushenko
 * All rights reserved.
 * Please read license.txt for licensing issues.
 **/



package conexp.util.exceptions;


public class ConfigFatalError extends Error {

    public ConfigFatalError(String s) {
        super(s);
    }

    public ConfigFatalError(String message, Throwable cause) {
        super(message, cause);
    }
}
