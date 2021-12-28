package org.ethelred.mc;

import java.nio.file.Path;

public interface World {
    public static World load(Path location) {
        return new WorldImpl(location);
    }
}
