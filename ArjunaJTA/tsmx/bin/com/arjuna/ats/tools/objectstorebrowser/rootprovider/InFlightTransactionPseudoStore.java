/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
 * (C) 2008,
 * @author JBoss Inc.
 */
package com.arjuna.ats.tools.objectstorebrowser.rootprovider;

import com.arjuna.ats.arjuna.objectstore.ObjectStoreImple;
import com.arjuna.ats.arjuna.objectstore.ObjectStoreType;
import com.arjuna.ats.arjuna.objectstore.ObjectStore;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.gandiva.inventory.InventoryElement;
import com.arjuna.ats.arjuna.gandiva.ClassName;
import com.arjuna.ats.arjuna.gandiva.ObjectName;
import com.arjuna.ats.tools.objectstorebrowser.TransactionLister;

import java.util.Set;
import java.io.IOException;

/**
 * ObjectStore facade for objtaining a list of live transactions. The facade is required
 * since the browser frame
 * @see com.arjuna.ats.tools.objectstorebrowser.frames.BrowserFrame
 * will only display object store entries.
 */
public class InFlightTransactionPseudoStore extends ObjectStoreImple implements InventoryElement
{
    public static final String STORE_NAME = "InFlightTransactionPseudoStore";
    private static final String TX_TYPE = "Transaction";
    private static final String TX_TYPE_WS = "Transaction/";
    private static TransactionLister transactionLister;

    /**
     * Abstract the mechanism used to discover which transactions are currently in existence
     *
     * @param transactionLister an interface that knows how to discover running transactions
     */
    public static void setTransactionLister(TransactionLister transactionLister)
    {
        InFlightTransactionPseudoStore.transactionLister = transactionLister;
    }

    public int typeIs()
    {
        return ObjectStoreType.USER_DEF_0;
    }

    /*
     * allObjUids - Given a type name, return an ObjectState that contains all
	 * of the uids of objects of that type.
	 */
    public boolean allObjUids(String typeName, InputObjectState buff, int m) throws ObjectStoreException
    {
        OutputObjectState store = new OutputObjectState();

        if (typeName.endsWith(TX_TYPE_WS) || typeName.endsWith(TX_TYPE))
        {
            try
            {
//                Set<Uid> uids = TransactionImple.getTransactions().keySet();
                Set<Uid> uids = transactionLister.getTransactions().keySet();

                for (Uid uid : uids)
                    uid.pack(store);
            }
            catch (IOException e)
            {
                return false;
            }
        }

        try
        {
            Uid.nullUid().pack(store);
        }
        catch (IOException e)
        {
            throw new ObjectStoreException("allObjUids - could not pack end of list Uid.");
        }

        buff.setBuffer(store.buffer());

        return true;
    }

    public boolean allTypes(InputObjectState buff) throws ObjectStoreException
    {
        try
        {
            OutputObjectState store = new OutputObjectState();
            store.packString("Transaction");
            store.packString("");

            buff.setBuffer(store.buffer());

            return true;
        }
        catch (IOException e)
        {
            throw new ObjectStoreException(tsLogger.arjLoggerI18N.getString("com.arjuna.ats.internal.arjuna.objectstore.packProblem"));
        }
    }

    public int currentState(Uid u, String tn) throws ObjectStoreException
    {
        return ObjectStore.OS_UNCOMMITTED;
    }

    public String getStoreName()
    {
        return STORE_NAME;
    }

    public boolean commit_state(Uid u, String tn) throws ObjectStoreException
    {
        return false;
    }

    public boolean hide_state(Uid u, String tn) throws ObjectStoreException
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean reveal_state(Uid u, String tn) throws ObjectStoreException
    {
        return false;
    }

    public InputObjectState read_committed(Uid u, String tn) throws ObjectStoreException
    {
        TxInputObjectState ios = new TxInputObjectState(u, tn, new byte[0]);

        ios.setRealObject(transactionLister.getTransactions().get(u));

        return ios;
    }

    public InputObjectState read_uncommitted(Uid u, String tn) throws ObjectStoreException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean remove_committed(Uid u, String tn) throws ObjectStoreException
    {
        return false;
    }

    public boolean remove_uncommitted(Uid u, String tn) throws ObjectStoreException
    {
        return false;
    }

    public boolean write_committed(Uid u, String tn, OutputObjectState buff) throws ObjectStoreException
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean write_uncommitted(Uid u, String tn, OutputObjectState buff) throws ObjectStoreException
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected boolean supressEntry(String name)
    {
        return false;
    }

    // InventoryElement implementation
    public synchronized Object createVoid ()
    {
        return new InFlightTransactionPseudoStore();
    }

    public synchronized Object createResources (Object[] param)
    {
        return createVoid();
    }

    public synchronized Object createClassName (ClassName className)
    {
        return null;
    }

    public synchronized Object createObjectName (ObjectName objectName)
    {
        return createVoid();
    }

    public synchronized Object createClassNameResources (ClassName className, Object[] resources)
    {
        return null;
    }

    public synchronized Object createObjectNameResources (ObjectName objectName, Object[] resources)
    {
        return null;
    }

    public ClassName className ()
    {
        return className;
    }

    private ClassName className = new ClassName(this.getClass().getSimpleName());
}