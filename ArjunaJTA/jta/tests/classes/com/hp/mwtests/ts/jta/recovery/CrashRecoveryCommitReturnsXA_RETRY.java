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
 * Copyright (C) 2004,
 *
 * Arjuna Technologies Ltd,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: CrashRecovery.java 2342 2006-03-30 13:06:17Z  $
 */

package com.hp.mwtests.ts.jta.recovery;

import java.util.Vector;

import javax.transaction.xa.XAResource;

import junit.framework.TestCase;

import com.arjuna.ats.arjuna.recovery.RecoveryEnvironment;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.arjuna.recovery.RecoveryModule;
import com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule;
import com.arjuna.ats.jta.recovery.XAResourceRecoveryHelper;

public class CrashRecoveryCommitReturnsXA_RETRY extends TestCase {
	public void test() throws Exception {
		// this test is supposed to leave a record around in the log store
		// during a commit long enough
		// that the periodic recovery thread runs and detects it.

		System.setProperty(
				com.arjuna.ats.arjuna.common.Environment.RECOVERY_BACKOFF_PERIOD,
				"1");
		System.setProperty(
				com.arjuna.ats.arjuna.common.Environment.PERIODIC_RECOVERY_PERIOD,
				"1");

		System.setProperty(RecoveryEnvironment.MODULE_PROPERTY_PREFIX + ".1",
				"com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule");
		System.setProperty(RecoveryEnvironment.MODULE_PROPERTY_PREFIX + ".2",
				"com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule");
		RecoveryManager.manager().initialize();

		XARecoveryModule xaRecoveryModule = null;
		for (RecoveryModule recoveryModule : ((Vector<RecoveryModule>) RecoveryManager
				.manager().getModules())) {
			if (recoveryModule instanceof XARecoveryModule) {
				xaRecoveryModule = (XARecoveryModule) recoveryModule;
				break;
			}
		}

		if (xaRecoveryModule == null) {
			throw new Exception("No XARM");
		}

		XAResource firstResource = new SimpleResource();
		Object toWakeUp = new Object();
		final SimpleResourceXA_RETRY secondResource = new SimpleResourceXA_RETRY(
				toWakeUp);

		xaRecoveryModule
				.addXAResourceRecoveryHelper(new XAResourceRecoveryHelper() {

					@Override
					public boolean initialise(String p) throws Exception {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public XAResource[] getXAResources() throws Exception {
						// TODO Auto-generated method stub
						return new XAResource[] { secondResource };
					}
				});

		// ok, now drive a TX to completion. the script should ensure that the
		// recovery

		javax.transaction.TransactionManager tm = com.arjuna.ats.jta.TransactionManager
				.transactionManager();

		tm.begin();

		javax.transaction.Transaction theTransaction = tm.getTransaction();

		theTransaction.enlistResource(firstResource);
		theTransaction.enlistResource(secondResource);

		assertFalse(secondResource.wasCommitted());

		tm.commit();

		synchronized (toWakeUp) {
			toWakeUp.wait();
		}
		assertTrue(secondResource.wasCommitted());
	}
}