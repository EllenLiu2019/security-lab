package com.ellen.security.lab.detector;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FilePropertyDetector implements EncryptablePropertyDetector {

    private final String fieldPrefix = "ENC(";

    private final String filePrefix = "FILE(";

    private final String suffix = ")";

    @Override
    public boolean isEncrypted(String property) {
        if (property == null) {
            return false;
        } else {
            String trimmedValue = property.trim();
            return (trimmedValue.startsWith(this.fieldPrefix) || trimmedValue.startsWith(this.filePrefix))
                    && trimmedValue.endsWith(this.suffix);
        }
    }


    @Override
    public String unwrapEncryptedValue(String property) {
        String prefix = property.startsWith(this.fieldPrefix) ? this.fieldPrefix : this.filePrefix;
        String content = property.substring(prefix.length(), property.length() - this.suffix.length());
        if (prefix.startsWith(this.fieldPrefix)) {
            return content;
        }
        String fileContent;
        try (InputStream is = new FileInputStream(content)) {
            fileContent = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (this.isEncrypted(fileContent)) {
            return unwrapEncryptedValue(fileContent);
        } else {
            return fileContent;
        }
    }
}
