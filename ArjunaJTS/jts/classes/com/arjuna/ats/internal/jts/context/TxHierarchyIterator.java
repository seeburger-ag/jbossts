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
/*
 * Copyright (C) 2001, 2002,
 *
 * Hewlett-Packard Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.  
 *
 * $Id: TxHierarchyIterator.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.internal.jts.context;

import com.arjuna.ats.internal.arjuna.template.*;

import org.omg.CosTransactions.*;

/**
 * An iterator for the OTS_ActionHierarchy.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: TxHierarchyIterator.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

public class TxHierarchyIterator
{

    /**
     * Create a new instance to iterate across the specified hierarchy.
     */

    public TxHierarchyIterator (TxHierarchy toIter)
    {
	_theIter = new SimpleListIterator(toIter._hier);
    }

    /**
     * Return the next transaction in the hierarchy.
     */

    public final synchronized Control iterate ()
    {
	return (Control) _theIter.iterate();
    }
    
    private SimpleListIterator _theIter;
 
}