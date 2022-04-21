package com.gmc.config;

import java.io.IOException;

public interface ChangeNotifyResource<T extends ChangeNotifyResource<T>> extends Resource {

    void addChangeNotifyListener(ResourceChangeListener<T> resourceChangeListener);

    interface ResourceChangeListener<T extends ChangeNotifyResource<T>> {
        void changeNotify(ChangeNotifyContext<T> context) throws IOException;

    }

    class ChangeNotifyContext<T extends ChangeNotifyResource<T>> {
        private T resource;
        public ChangeNotifyContext(T resource) {
            this.resource = resource;
        }


        public T getResource() {
            return resource;
        }
    }

}
