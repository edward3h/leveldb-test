package org.ethelred.mc;

import java.nio.file.Path;

class WorldImpl implements World {

    WorldImpl(Path path) {
        /*
        Is it a world? Should handle these cases for path:
        * zip file with 'zip' or 'mcworld' extension
        * directory containing a single 'db' directory nested at some level
        * the 'db' directory itself
        */
    }
}
