package com.alienpants.linker.libraries;

import com.alienpants.linker.application.Linker;

public class Backend {

    private Linker application;

    public Backend(Linker application) {
        this.application = application;
    }
    public Linker getApplication() {
        return application;
    }


}