package org.medcare.igtl.messages;

import Jama.Matrix;
import org.medcare.igtl.util.Header;

public class DataArrayMessage extends OpenIGTMessage {
    /**
     * This is a stub class for how the Array Data message is to be implemented
     *
     * @param head
     * @param body
     * @throws Exception
     */
    public DataArrayMessage(Header head, byte[] body) throws Exception {
        super(head, body);
    }

    @Override
    public boolean unpackBody() throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public byte[] packBody() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    public Matrix getDataMatrix() {
        // TODO Auto-generated method stub
        return null;
    }

}
