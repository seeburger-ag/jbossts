/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors 
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors. 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
/*
 * Copyright (C) 2002,
 *
 * Hewlett-Packard Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.  
 *
 * $Id: XATxConverter.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.jta.xa;

import java.io.UnsupportedEncodingException;

import javax.transaction.xa.Xid;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.internal.jta.xa.XID;

/**
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: XATxConverter.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

public class XATxConverter
{
    public static final int FORMAT_ID = 131076; // different from JTS ones.

    static XID getXid (Uid uid, boolean branch, String eisName) throws IllegalStateException
    {
        if (branch)
            return getXid(uid, new Uid(), FORMAT_ID, eisName);
        else
            return getXid(uid, Uid.nullUid(), FORMAT_ID, eisName);
    }

    public static Xid getXid (Uid uid, boolean branch, int formatId) throws IllegalStateException
    {
        XID xid;
        if (branch)
            xid = getXid(uid, new Uid(), formatId, null);
        else
            xid = getXid(uid, Uid.nullUid(), formatId, null);

        return new XidImple(xid);
    }

    private static XID getXid(Uid uid, Uid branch, int formatId, String eisNameString) throws IllegalStateException
    {
        if (uid == null) {
    	    throw new IllegalStateException();
        }

        XID xid = new XID();
        xid.formatID = formatId;

        // gtrid is uid byte form followed by as many chars of the node name as will fit.
        byte[] gtridUid = uid.getBytes();

        if (gtridUid.length > XID.MAXGTRIDSIZE) {
            throw new IllegalStateException(); // Uid is too long!!!!
        }

        int nodeName = TxControl.getXANodeName();
        int nodeNameLengthToUse =  4;
        xid.gtrid_length = gtridUid.length+nodeNameLengthToUse;

        // src, srcPos, dest, destPos, length
        System.arraycopy(gtridUid, 0, xid.data, 0, gtridUid.length);

    	int offset = gtridUid.length;
    	xid.data[offset + 0] = (byte) (nodeName >>> 24);
    	xid.data[offset + 1] = (byte) (nodeName >>> 16);
    	xid.data[offset + 2] = (byte) (nodeName >>> 8);
    	xid.data[offset + 3] = (byte) (nodeName >>> 0);

        
        if (branch.notEquals(Uid.nullUid()))
		{
            // bqual is uid byte form plus EIS name.
            byte[] bqualUid = branch.getBytes();

            if (bqualUid.length > XID.MAXBQUALSIZE) {
                throw new IllegalStateException(); // Uid is too long!!!!
            }

            int spareBqualBytes = XID.MAXBQUALSIZE - (bqualUid.length + 4 + 4);
            byte[] eisName;
            try {
                // caution: we may truncate the byte[] to fit, so double byte encodings are best avoided.
                eisName = (eisNameString == null ? new byte[0] : eisNameString.getBytes("US-ASCII"));
            } catch(UnsupportedEncodingException e) {
                eisName = new byte[0];
            }
            int eisNameLengthToUse = eisName.length;
            if( eisName.length > spareBqualBytes) {
                eisNameLengthToUse = spareBqualBytes;
            }
            xid.bqual_length = bqualUid.length+4+4+eisNameLengthToUse;

            // src, srcPos, dest, destPos, length
            System.arraycopy (bqualUid, 0, xid.data, xid.gtrid_length, bqualUid.length);
            

        	offset = xid.gtrid_length + bqualUid.length;
        	xid.data[offset + 0] = (byte) (nodeName >>> 24);
        	xid.data[offset + 1] = (byte) (nodeName >>> 16);
        	xid.data[offset + 2] = (byte) (nodeName >>> 8);
        	xid.data[offset + 3] = (byte) (nodeName >>> 0);
            
            // Leave four bytes free to encode the parent node when this XID is inflowed
        	
            System.arraycopy (eisName, 0, xid.data, xid.gtrid_length+bqualUid.length+4+4, eisNameLengthToUse);
        }
		else
		{
		    /*
		     * Note: for some dbs we seem to be able to get
		     * away with setting the size field to the size
		     * of the actual branch. However, for Oracle,
		     * it appears as though it must always be 64.
		     * (At least for zero branches.)
		     */
		    xid.data[xid.gtrid_length] = (byte) 0;
		    xid.bqual_length = 64;
		}

        return xid;
    }

    public static Uid getUid(XID xid)
    {
        if (xid == null || xid.formatID != FORMAT_ID) {
            return Uid.nullUid();
        }

        // The Uid byte are the first in the data array, so we just pass
        // in the whole thing and the additional trailing data is ignored.
        Uid tx = new Uid(xid.data);
        return tx;
    }

	public static int getNodeName(XID xid) {
		// Arjuna.XID()
		// don't check the formatId - it may differ e.g. JTA vs. JTS.
		if (xid.formatID != FORMAT_ID && xid.formatID != 131072
				&& xid.formatID != 131080) {
			return -1;
		}

		// the node name follows the Uid with no separator, so the only
		// way to tell where it starts is to figure out how long the Uid is.
		int offset = Uid.UID_SIZE;

		return (xid.data[offset + 0] << 24)
				+ ((xid.data[offset + 1] & 0xFF) << 16)
				+ ((xid.data[offset + 2] & 0xFF) << 8)
				+ (xid.data[offset + 3] & 0xFF);
	}

	public static int getSubordinateNodeName(XID xid) {
		// Arjuna.XID()
		// don't check the formatId - it may differ e.g. JTA vs. JTS.
		if (xid.formatID != FORMAT_ID && xid.formatID != 131072
				&& xid.formatID != 131080) {
			return -1;
		}

		// the node name follows the Uid with no separator, so the only
		// way to tell where it starts is to figure out how long the Uid is.
		int offset = Uid.UID_SIZE + 4 + Uid.UID_SIZE;

		return (xid.data[offset + 0] << 24)
				+ ((xid.data[offset + 1] & 0xFF) << 16)
				+ ((xid.data[offset + 2] & 0xFF) << 8)
				+ (xid.data[offset + 3] & 0xFF);
	}

	public static int getSubordinateParentNodeName(XID xid) {
		// Arjuna.XID()
		// don't check the formatId - it may differ e.g. JTA vs. JTS.
		if (xid.formatID != FORMAT_ID && xid.formatID != 131072
				&& xid.formatID != 131080) {
			return -1;
		}

		// the node name follows the Uid with no separator, so the only
		// way to tell where it starts is to figure out how long the Uid is.
		int offset = Uid.UID_SIZE + 4 + Uid.UID_SIZE + 4;

		return (xid.data[offset + 0] << 24)
				+ ((xid.data[offset + 1] & 0xFF) << 16)
				+ ((xid.data[offset + 2] & 0xFF) << 8)
				+ (xid.data[offset + 3] & 0xFF);
	}

    public static Uid getBranchUid(XID xid)
    {
        if (xid == null || xid.formatID != FORMAT_ID) {
            return Uid.nullUid();
        }

        byte[] bqual = new byte[xid.bqual_length];
        System.arraycopy(xid.data, xid.gtrid_length, bqual, 0, xid.bqual_length);

        // The Uid byte are the first in the data array, so we just pass
        // in the whole thing and the additional trailing data is ignored.
        Uid tx = new Uid(bqual);
        return tx;
    }

    public static String getEISName(XID xid)
    {
        if(xid == null || xid.formatID != FORMAT_ID) {
            return "unknown eis name";
        }

        Uid uid = getUid(xid);
        int uidLength = uid.getBytes().length;
        int nameLength = xid.bqual_length-(uidLength+4+4);

        if(nameLength == 0) {
            return "unknown eis name";
        }

        byte[] eisName = new byte[nameLength];
        System.arraycopy(xid.data, xid.gtrid_length+uidLength+4+4, eisName, 0, eisName.length);

        try {
            return new String(eisName, "US-ASCII");
        } catch(UnsupportedEncodingException e) {
            // should never happen, we use a required charset.
            return "<failed to get eisName>";
        }
    }

    public static String getXIDString(XID xid)
    {
        // companion method to XID.toString - try to keep them in sync.

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("< formatId=");
        stringBuilder.append(xid.formatID);
        stringBuilder.append(", gtrid_length=");
        stringBuilder.append(xid.gtrid_length);
        stringBuilder.append(", bqual_length=");
        stringBuilder.append(xid.bqual_length);
        stringBuilder.append(", tx_uid=");
        stringBuilder.append(getUid(xid).stringForm());
        stringBuilder.append(", node_name=");
        stringBuilder.append(getNodeName(xid));
        stringBuilder.append(", branch_uid=");
        stringBuilder.append(getBranchUid(xid));
        stringBuilder.append(", eis_name=");
        stringBuilder.append(getEISName(xid));
        stringBuilder.append(" >");

        return stringBuilder.toString();
    }
}
