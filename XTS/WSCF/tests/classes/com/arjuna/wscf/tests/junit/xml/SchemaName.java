/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
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
 * Arjuna Technologies Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: SchemaName.java,v 1.2 2003/01/17 15:39:35 nmcl Exp $
 */

package com.arjuna.wscf.tests.junit.xml;

import com.arjuna.mw.wscf.UserCoordinator;
import com.arjuna.mw.wscf.UserCoordinatorFactory;

import com.arjuna.mw.wscf.exceptions.ProtocolNotRegisteredException;

import java.net.URL;

import javax.xml.parsers.*;

/**
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: SchemaName.java,v 1.2 2003/01/17 15:39:35 nmcl Exp $
 * @since 1.0.
 */

public class SchemaName
{

    public static void main (String[] args)
    {
	boolean passed = false;
	
	try
	{
	    URL url = ClassLoader.getSystemClassLoader().getResource("com/arjuna/mwtests/wscf/xml/example.xml");

	    if (url == null)
	    {
		System.err.println("No example.xml found!");
	    }
	    else
	    {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		org.w3c.dom.Document doc = builder.parse(url.toExternalForm());
	    
		if (doc == null)
		{
		    System.err.println("Failed to create example doc!");
		}

		UserCoordinator ua = UserCoordinatorFactory.userCoordinator(doc);
	
		ua.start();
		
		ua.end();
	    }
	}
	catch (ProtocolNotRegisteredException ex)
	{
	    passed = true;
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	}
	
	if (passed)
	    System.out.println("\nPassed.");
	else
	    System.out.println("\nFailed.");
    }

}