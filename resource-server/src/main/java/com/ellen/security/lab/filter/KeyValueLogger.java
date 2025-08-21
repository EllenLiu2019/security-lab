package com.ellen.security.lab.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeyValueLogger {

    void log(LoggingFilter.Logs logs) {
        StringBuilder builder = new StringBuilder();
        logs.getMap().forEach((key, value) ->
                builder.append('[')
                        .append(key).append('=').append('[').append(value).append(']').append(']').append(' '));

        if (log.isInfoEnabled()) {
            log.info(builder.toString());
        }
    }
}
