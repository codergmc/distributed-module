package com.gmc.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface Resource {
     InputStream getInputStream() throws IOException;
     String getNameSpace();
     String getResourceName();
     String getProfile();


}
