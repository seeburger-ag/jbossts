/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.arjuna.jta.distributed.example.server.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.InvalidTransactionException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.arjuna.ats.arjuna.common.CoordinatorEnvironmentBean;
import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.RecoveryEnvironmentBean;
import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.arjuna.tools.osb.mbean.ObjStoreBrowser;
import com.arjuna.ats.internal.jbossatx.jta.XAResourceRecordWrappingPluginImpl;
import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.SubordinateTransaction;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.SubordinateXidImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.SubordinationManager;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.XATerminatorImple;
import com.arjuna.ats.jbossatx.jta.RecoveryManagerService;
import com.arjuna.ats.jbossatx.jta.TransactionManagerService;
import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.jta.distributed.example.TestResourceRecovery;
import com.arjuna.jta.distributed.example.server.CompletionCounter;
import com.arjuna.jta.distributed.example.server.DummyRemoteException;
import com.arjuna.jta.distributed.example.server.LocalServer;
import com.arjuna.jta.distributed.example.server.LookupProvider;
import com.arjuna.jta.distributed.example.server.RemoteServer;

public class ServerImpl implements LocalServer, RemoteServer {

	private int nodeName;
	private RecoveryManagerService recoveryManagerService;
	private TransactionManagerService transactionManagerService;
	private Map<SubordinateXidImple, TransactionImple> transactions = new HashMap<SubordinateXidImple, TransactionImple>();
	private RecoveryManager _recoveryManager;
	private CompletionCounter counter;

	public void initialise(LookupProvider lookupProvider, Integer nodeName) throws CoreEnvironmentBeanException, IOException, SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		this.nodeName = nodeName;

		RecoveryEnvironmentBean recoveryEnvironmentBean = com.arjuna.ats.arjuna.common.recoveryPropertyManager.getRecoveryEnvironmentBean();
		recoveryEnvironmentBean.setRecoveryBackoffPeriod(1);

		recoveryEnvironmentBean.setRecoveryInetAddress(InetAddress.getByName("localhost"));
		recoveryEnvironmentBean.setRecoveryPort(4712 + nodeName);
		recoveryEnvironmentBean.setTransactionStatusManagerInetAddress(InetAddress.getByName("localhost"));
		recoveryEnvironmentBean.setTransactionStatusManagerPort(4713 + nodeName);
		List<String> recoveryModuleClassNames = new ArrayList<String>();

		recoveryModuleClassNames.add("com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule");
		recoveryModuleClassNames.add("com.arjuna.ats.internal.txoj.recovery.TORecoveryModule");
		recoveryModuleClassNames.add("com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule");
		recoveryEnvironmentBean.setRecoveryModuleClassNames(recoveryModuleClassNames);
		List<String> expiryScannerClassNames = new ArrayList<String>();
		expiryScannerClassNames.add("com.arjuna.ats.internal.arjuna.recovery.ExpiredTransactionStatusManagerScanner");
		recoveryEnvironmentBean.setExpiryScannerClassNames(expiryScannerClassNames);
		recoveryEnvironmentBean.setRecoveryActivators(null);

		CoreEnvironmentBean coreEnvironmentBean = com.arjuna.ats.arjuna.common.arjPropertyManager.getCoreEnvironmentBean();
		coreEnvironmentBean.setSocketProcessIdPort(4714 + nodeName);
		coreEnvironmentBean.setNodeIdentifier(nodeName);
		coreEnvironmentBean.setSocketProcessIdMaxPorts(1);

		CoordinatorEnvironmentBean coordinatorEnvironmentBean = com.arjuna.ats.arjuna.common.arjPropertyManager.getCoordinatorEnvironmentBean();
		coordinatorEnvironmentBean.setEnableStatistics(false);
		coordinatorEnvironmentBean.setDefaultTimeout(300);
		coordinatorEnvironmentBean.setTransactionStatusManagerEnable(false);
		coordinatorEnvironmentBean.setDefaultTimeout(0);

		ObjectStoreEnvironmentBean actionStoreObjectStoreEnvironmentBean = com.arjuna.common.internal.util.propertyservice.BeanPopulator.getNamedInstance(
				com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean.class, "default");
		actionStoreObjectStoreEnvironmentBean.setObjectStoreDir(System.getProperty("user.dir") + "/distributedjta-example/tx-object-store/" + nodeName);

		ObjectStoreEnvironmentBean stateStoreObjectStoreEnvironmentBean = com.arjuna.common.internal.util.propertyservice.BeanPopulator.getNamedInstance(
				com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean.class, "stateStore");
		stateStoreObjectStoreEnvironmentBean.setObjectStoreDir(System.getProperty("user.dir") + "/distributedjta-example/tx-object-store/" + nodeName);

		ObjectStoreEnvironmentBean communicationStoreObjectStoreEnvironmentBean = com.arjuna.common.internal.util.propertyservice.BeanPopulator
				.getNamedInstance(com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean.class, "communicationStore");
		communicationStoreObjectStoreEnvironmentBean.setObjectStoreDir(System.getProperty("user.dir") + "/distributedjta-example/tx-object-store/" + nodeName);

		ObjStoreBrowser objStoreBrowser = new ObjStoreBrowser();
		Map<String, String> types = new HashMap<String, String>();
		types.put("StateManager/BasicAction/TwoPhaseCoordinator/AtomicAction", "com.arjuna.ats.internal.jta.tools.osb.mbean.jta.JTAActionBean");
		objStoreBrowser.setTypes(types);

		JTAEnvironmentBean jTAEnvironmentBean = com.arjuna.ats.jta.common.jtaPropertyManager.getJTAEnvironmentBean();
		jTAEnvironmentBean.setLastResourceOptimisationInterface(org.jboss.tm.LastResource.class);
		jTAEnvironmentBean.setTransactionManagerClassName("com.arjuna.ats.jbossatx.jta.TransactionManagerDelegate");
		jTAEnvironmentBean.setUserTransactionClassName("com.arjuna.ats.internal.jta.transaction.arjunacore.UserTransactionImple");
		jTAEnvironmentBean
				.setTransactionSynchronizationRegistryClassName("com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple");
		List<Integer> xaRecoveryNodes = new ArrayList<Integer>();
		xaRecoveryNodes.add(nodeName);
		jTAEnvironmentBean.setXaRecoveryNodes(xaRecoveryNodes);

		List<String> xaResourceOrphanFilterClassNames = new ArrayList<String>();

		xaResourceOrphanFilterClassNames.add("com.arjuna.ats.internal.jta.recovery.arjunacore.JTATransactionLogXAResourceOrphanFilter");
		xaResourceOrphanFilterClassNames.add("com.arjuna.ats.internal.jta.recovery.arjunacore.JTANodeNameXAResourceOrphanFilter");
		xaResourceOrphanFilterClassNames.add("com.arjuna.ats.internal.jta.recovery.arjunacore.SubordinateJTAXAResourceOrphanFilter");
		jTAEnvironmentBean.setXaResourceOrphanFilterClassNames(xaResourceOrphanFilterClassNames);
		jTAEnvironmentBean.setXAResourceRecordWrappingPlugin(new XAResourceRecordWrappingPluginImpl());

		recoveryManagerService = new RecoveryManagerService();
		recoveryManagerService.create();
		recoveryManagerService.addXAResourceRecovery(new ProxyXAResourceRecovery(lookupProvider, nodeName));
		recoveryManagerService.addXAResourceRecovery(new TestResourceRecovery(counter, nodeName));

		recoveryManagerService.start();

		transactionManagerService = new TransactionManagerService();
		TxControl txControl = new com.arjuna.ats.arjuna.coordinator.TxControl();
		transactionManagerService.setJbossXATerminator(new com.arjuna.ats.internal.jbossatx.jta.jca.XATerminator());
		transactionManagerService
				.setTransactionSynchronizationRegistry(new com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple());
		transactionManagerService.create();
	}

	@Override
	public TransactionManager getTransactionManager() {
		return transactionManagerService.getTransactionManager();
	}

	@Override
	public boolean getAndResumeTransaction(int remainingTimeout, Xid toResume) throws XAException, InvalidTransactionException, IllegalStateException,
			SystemException {
		boolean existed = true;
		Transaction transaction = transactions.get(new SubordinateXidImple(toResume));
		if (transaction == null) {
			transaction = SubordinationManager.getTransactionImporter().getImportedTransaction(toResume);
			if (transaction == null) {
				transaction = SubordinationManager.getTransactionImporter().importTransaction(toResume, remainingTimeout);
				existed = false;
			}
		}
		transactionManagerService.getTransactionManager().resume(transaction);
		return existed;
	}

	@Override
	public Integer getNodeName() {
		return nodeName;
	}

	@Override
	public void storeRootTransaction() throws SystemException {
		TransactionImple transaction = ((TransactionImple) transactionManagerService.getTransactionManager().getTransaction());
		Xid txId = transaction.getTxId();
		transactions.put(new SubordinateXidImple(txId), transaction);
	}

	@Override
	public Xid getCurrentXid() throws SystemException {
		TransactionImple transaction = ((TransactionImple) transactionManagerService.getTransactionManager().getTransaction());
		return transaction.getTxId();
	}

	@Override
	public void removeRootTransaction(Xid toMigrate) {
		transactions.remove(new SubordinateXidImple(toMigrate));
	}

	@Override
	public ProxyXAResource generateProxyXAResource(LookupProvider lookupProvider, Integer localServerName, Integer remoteServerName) {
		return new ProxyXAResource(lookupProvider, localServerName, remoteServerName);
	}

	@Override
	public Synchronization generateProxySynchronization(LookupProvider lookupProvider, Integer localServerName, Integer remoteServerName, Xid toRegisterAgainst) {
		return new ProxySynchronization(lookupProvider, localServerName, remoteServerName, toRegisterAgainst);
	}

	@Override
	public RemoteServer connectTo() {
		return this;
	}

	@Override
	public int propagatePrepare(Xid xid) throws XAException, DummyRemoteException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);			
		}
		SubordinateTransaction tx = SubordinationManager.getTransactionImporter().getImportedTransaction(xid);
		return SubordinationManager.getXATerminator().prepare(xid);
	}

	@Override
	public void propagateCommit(Xid xid) throws XAException, DummyRemoteException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			SubordinationManager.getXATerminator().commit(xid, false);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);			
		}
	}

	@Override
	public void propagateRollback(Xid xid) throws XAException, DummyRemoteException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			SubordinationManager.getXATerminator().rollback(xid);			
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);			
		}
	}

	@Override
	public Xid[] propagateRecover(int formatId, byte[] gtrid, int flag) throws XAException, DummyRemoteException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			List<Xid> toReturn = new ArrayList<Xid>();
			Xid[] recovered = SubordinationManager.getXATerminator().recover(flag);
			if (recovered != null) {
				for (int i = 0; i < recovered.length; i++) {
					// Filter out the transactions that are not owned by this parent
					if (recovered[i].getFormatId() == formatId && Arrays.equals(gtrid, recovered[i].getGlobalTransactionId())) {
						toReturn.add(recovered[i]);
					}
				}
			}
			return toReturn.toArray(new Xid[0]);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);			
		}
	}

	@Override
	public void propagateForget(Xid xid) throws XAException, DummyRemoteException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			SubordinationManager.getXATerminator().forget(xid);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);			
		}

	}

	@Override
	public void propagateBeforeCompletion(Xid xid) throws XAException, SystemException, DummyRemoteException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			((XATerminatorImple) SubordinationManager.getXATerminator()).beforeCompletion(xid);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);			
		}
	}

	@Override
	public Xid extractXid(XAResource xaResource) {
		ProxyXAResource proxyXAResource = (ProxyXAResource) xaResource;
		return proxyXAResource.getXid();
	}
}