package com.gmc.config;

public interface ConfigInterface {
    public String getValue();
    public boolean getBoolean();
    public byte getByte();
    public short getShort();
    public int getInt();
    public long getLong();
    public float getFloat();
    public double getDouble();
    public <T> T get(Class<T> t);

}
