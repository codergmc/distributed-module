package com.gmc.net;

public class DefaultMessageHead implements MessageHead, FixedLength {
    private short type;
    private short version;
    private int autoIncrId;

    public DefaultMessageHead(short type, short version) {
        this.type = type;
        this.version = version;
    }

    public DefaultMessageHead(int type, int version) {
        this((short) type, (short) version);
    }

    @Override
    public void read(BytesBuffer buffer) {
        this.type = buffer.readShort();
        this.version = buffer.readShort();
        this.autoIncrId = buffer.readInt();
    }

    @Override
    public void write(BytesBuffer buffer) {
        buffer.writeShort(type);
        buffer.writeShort(version);
        buffer.writeInt(autoIncrId);
    }

    public DefaultMessageHead setAutoIncrId(int autoIncrId) {
        this.autoIncrId = autoIncrId;
        return this;
    }

    public short getType() {
        return type;
    }

    public short getVersion() {
        return version;
    }

    public int getAutoIncrId() {
        return autoIncrId;
    }

    @Override
    public int size() {
        return 2 + 2 + 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultMessageHead that = (DefaultMessageHead) o;

        if (type != that.type) return false;
        if (version != that.version) return false;
        return autoIncrId == that.autoIncrId;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (int) version;
        result = 31 * result + autoIncrId;
        return result;
    }
}
