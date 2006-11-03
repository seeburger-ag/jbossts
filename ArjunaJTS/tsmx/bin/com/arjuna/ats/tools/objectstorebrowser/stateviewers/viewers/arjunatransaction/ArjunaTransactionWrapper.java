/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
package com.arjuna.ats.tools.objectstorebrowser.stateviewers.viewers.arjunatransaction;

import com.arjuna.ats.arjuna.coordinator.BasicAction;
import com.arjuna.ats.arjuna.coordinator.RecordList;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.gandiva.ObjectName;
import com.arjuna.ats.internal.jts.orbspecific.coordinator.ArjunaTransactionImple;

public class ArjunaTransactionWrapper extends BasicAction
{
    public ArjunaTransactionWrapper(Uid objUid, ObjectName objectName)
    {
        super(objUid, ObjectType.ANDPERSISTENT, objectName);
    }

    /**
     * Overloads StateManager.type()
     */
    public String type()
    {
        return ArjunaTransactionImple.typeName();
    }

    public RecordList getFailedList()
    {
        return failedList;
    }

    public RecordList getHeuristicList()
    {
        return heuristicList;
    }

    public RecordList getPendingList()
    {
        return pendingList;
    }

    public RecordList getPreparedList()
    {
        return preparedList;
    }

    public RecordList getReadOnlyList()
    {
        return readonlyList;
    }
}
