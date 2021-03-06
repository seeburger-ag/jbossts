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
 * $Id: WscNestedContext.java,v 1.3.22.1 2005/11/22 10:34:07 kconner Exp $
 */

package com.arjuna.wscf.tests.junit.model.twophase;

import com.arjuna.mw.wsas.context.Context;
import com.arjuna.mw.wsas.context.ContextManager;
import com.arjuna.mw.wsas.context.soap.SOAPContext;
import com.arjuna.mw.wscf.model.twophase.UserCoordinatorFactory;
import com.arjuna.mw.wscf.model.twophase.api.UserCoordinator;
import com.arjuna.wscf.tests.WSCFTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: WscNestedContext.java,v 1.3.22.1 2005/11/22 10:34:07 kconner Exp $
 * @since 1.0.
 */

public class WscNestedContext
{

    @Test
    public void testWscNestedContext()
            throws Exception
    {
        System.out.println("Running test : " + this.getClass().getName());

        UserCoordinator ua = UserCoordinatorFactory.userCoordinator();

	try
	{
	    ua.begin("TwoPhaseHLS");

	    System.out.println("Started: "+ua.identifier()+"\n");

	    ua.begin("TwoPhaseHLS");

	    System.out.println("Started: "+ua.identifier()+"\n");

	    ua.begin("TwoPhaseHLS");

	    System.out.println("Started: "+ua.identifier()+"\n");

        ContextManager cxman = new ContextManager();
        Context context = cxman.context("TwoPhaseHLS");

        SOAPContext theContext = (SOAPContext)context;

            // this fails because the context toString method gets a NPE -- need a better test
        System.out.println("Context: " + theContext);

	    ua.cancel();

        System.out.println("Cancelled");

        context = cxman.context("TwoPhaseHLS");

        theContext = (SOAPContext)context;

        System.out.println("Context: " + theContext);

	    ua.cancel();

        System.out.println("Cancelled");

        context = cxman.context("TwoPhaseHLS");

        theContext = (SOAPContext)context;

        System.out.println("Context: " + theContext);

	    ua.cancel();

        System.out.println("Cancelled");
    }
	catch (Exception ex)
	{
        WSCFTestUtils.cleanup(ua);

        throw ex;
    }
    }
}
