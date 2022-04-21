package com.gmc.net;

public class TestMessage extends Message implements FixedLength {
    public TestMessage(TestMessageHead head, TestMessageBody body) {
        super(head, body);
    }

    public TestMessage() {
        super(new TestMessageHead(), new TestMessageBody());
    }
    public TestMessage(TestMessageBody testMessageBody) {
        super(new TestMessageHead(), testMessageBody);
    }

    @Override
    public int size() {
        return ((FixedLength) head).size() + ((FixedLength) body).size();
    }

    static class TestMessageHead extends DefaultMessageHead implements FixedLength {


        public TestMessageHead() {
            super(1, 1);
        }


    }

    static class TestMessageBody implements MessageBody, FixedLength {

        int a;
        short b;
        long c;

        public TestMessageBody(int a, short b, long c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public TestMessageBody() {
        }

        @Override
        public void read(BytesBuffer buffer) {
            a = buffer.readInt();
            b = buffer.readShort();
            c = buffer.readLong();
        }

        @Override
        public void write(BytesBuffer buffer) {
            buffer.writeInt(a);
            buffer.writeShort(b);
            buffer.writeLong(c);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestMessageBody that = (TestMessageBody) o;

            if (a != that.a) return false;
            if (b != that.b) return false;
            return c == that.c;
        }

        @Override
        public int hashCode() {
            int result = a;
            result = 31 * result + (int) b;
            result = 31 * result + (int) (c ^ (c >>> 32));
            return result;
        }

        @Override
        public int size() {
            return 4 + 2 + 8;
        }
    }

}