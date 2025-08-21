package com.ellen.security.lab.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class KeyValueLogger {

    void log(Map<String, Object> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((key,value) ->
                builder.append('[')
                        .append(key).append('=').append('[').append(value).append(']').append(']').append(' '));

        if (log.isInfoEnabled()) {
            log.info(builder.toString());
        }
    }
}
