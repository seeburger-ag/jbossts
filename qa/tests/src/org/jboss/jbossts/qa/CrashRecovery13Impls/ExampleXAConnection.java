/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors
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
 * Copyright (C) 2004,
 *
 * Arjuna Technologies Ltd,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: ExampleXAConnection.java,v 1.1 2004/10/13 15:45:47 nmcl Exp $
 */

package org.jboss.jbossts.qa.CrashRecovery13Impls;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.SQLException;

public class ExampleXAConnection implements XAConnection
{
	public XAResource getXAResource() throws SQLException
	{
		return new ExampleXAResource();
	}

	public void addConnectionEventListener(ConnectionEventListener l)
	{
	}

	public void close() throws SQLException
	{
	}

	public Connection getConnection() throws SQLException
	{
		return null;
	}

	public void removeConnectionEventListener(ConnectionEventListener l)
	{
	}

	public void addStatementEventListener(StatementEventListener l)
	{
	}

    public void removeStatementEventListener(StatementEventListener l) 
    {
    }
}

