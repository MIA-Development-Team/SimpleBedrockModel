package com.github.mcmodderanchor.simplebedrockmodel.v2.resource;

import java.io.InputStream;

public interface RawResourceLoader {
    <T> T load(InputStream inputStream, Class<T> clazz);
}
