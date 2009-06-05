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
 * Copyright (C) 2001, 2002,
 *
 * Hewlett-Packard Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: JTATest.java 2342 2006-03-30 13:06:17Z  $
 */

package com.hp.mwtests.ts.jta.basic;

import com.hp.mwtests.ts.jta.common.*;

import com.arjuna.ats.jta.*;

import com.arjuna.ats.arjuna.common.*;
import org.jboss.dtf.testframework.unittest.Test;
import org.jboss.dtf.testframework.unittest.LocalHarness;

import javax.transaction.*;
import javax.transaction.xa.*;

import java.lang.IllegalAccessException;

public class SuspendResume extends Test
{
    public void run (String[] args)
    {
        try
        {
            javax.transaction.TransactionManager tm = com.arjuna.ats.jta.TransactionManager
                    .transactionManager();

            if (tm != null)
            {
                System.out.println("Starting top-level transaction.");

                tm.begin();

                javax.transaction.Transaction theTransaction = tm.getTransaction();

                if (theTransaction != null)
                {
                    tm.commit();

                    tm.resume(theTransaction);
                }
                else
                {
                    System.err.println("Error - could not get transaction!");
                    tm.rollback();
                    assertFailure();
                }

                System.out.println("\nTest completed successfully.");
                assertSuccess();
            }
            else
            {
                System.err
                        .println("Error - could not get transaction manager!");
                assertFailure();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertFailure();
        }
    }

    public static void main (String[] args)
    {
        SuspendResume test = new SuspendResume();
        test.initialise(null, null, args, new LocalHarness());
        test.runTest();
    }

}