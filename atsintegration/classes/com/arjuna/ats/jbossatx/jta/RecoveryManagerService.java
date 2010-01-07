/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
 * (C) 2009,
 * @author Red Hat Middleware LLC.
 */
package com.arjuna.ats.jbossatx.jta;

import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.arjuna.recovery.RecoveryModule;
import com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule;
import com.arjuna.ats.internal.jbossatx.jta.XAResourceRecoveryHelperWrapper;
import com.arjuna.ats.jbossatx.logging.jbossatxLogger;
import com.arjuna.common.util.ConfigurationInfo;

import org.jboss.tm.XAResourceRecovery;
import org.jboss.tm.XAResourceRecoveryRegistry;

import java.util.Vector;

/**
 * JBoss Transaction Recovery Service.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com)
 * @version $Id$
 */
public class RecoveryManagerService implements XAResourceRecoveryRegistry
{
    private RecoveryManager _recoveryManager;

    /**
     * @message com.arjuna.ats.jbossatx.jta.RecoveryManagerService.create
     * [com.arjuna.ats.jbossatx.jta.RecoveryManagerService.create] JBossTS Recovery Service (tag: {0}) - JBoss Inc.
     */
    public void create() throws Exception
    {
        String tag = ConfigurationInfo.getSourceId();

        jbossatxLogger.loggerI18N.info("com.arjuna.ats.jbossatx.jta.RecoveryManagerService.create", new Object[] {tag});

        RecoveryManager.delayRecoveryManagerThread();
        // listener (if any) is created here:
        _recoveryManager = RecoveryManager.manager();
    }

    public void destroy()
    {
    }

    /**
     * @message com.arjuna.ats.jbossatx.jta.RecoveryManagerService.start
     * [com.arjuna.ats.jbossatx.jta.RecoveryManagerService.start] Starting transaction recovery manager
     */
    public void start()
    {
        jbossatxLogger.loggerI18N.info("com.arjuna.ats.jbossatx.jta.RecoveryManagerService.start");

        _recoveryManager.initialize();
        _recoveryManager.startRecoveryManagerThread() ;
    }

    /**
     * @message com.arjuna.ats.jbossatx.jta.RecoveryManagerService.stop
     * [com.arjuna.ats.jbossatx.jta.RecoveryManagerService.stop] Stopping transaction recovery manager
     */
    public void stop() throws Exception
    {
        jbossatxLogger.loggerI18N.info("com.arjuna.ats.jbossatx.jta.RecoveryManagerService.stop");

        _recoveryManager.terminate();
    }

    //////////////////////////////

    /**
     * @message com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverysystem
     * [com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverysystem] No recovery system in which to register XAResourceRecovery instance
     * @message com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverymodule
     * [com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverymodule] No suitable recovery module in which to register XAResourceRecovery instance
     */
    public void addXAResourceRecovery(XAResourceRecovery xaResourceRecovery)
    {
        if(_recoveryManager == null) {
            jbossatxLogger.loggerI18N.error("com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverysystem");
            throw new IllegalStateException(jbossatxLogger.loggerI18N.getString("com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverysystem"));
        }

        XARecoveryModule xaRecoveryModule = null;
        for(RecoveryModule recoveryModule : ((Vector<RecoveryModule>)_recoveryManager.getModules())) {
            if(recoveryModule instanceof XARecoveryModule) {
                xaRecoveryModule = (XARecoveryModule)recoveryModule;
                break;
            }
        }

        if(xaRecoveryModule == null) {
            jbossatxLogger.loggerI18N.error("com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverymodule");
            throw new IllegalStateException(jbossatxLogger.loggerI18N.getString("com.arjuna.ats.jbossatx.jta.RecoveryManagerService.norecoverymodule"));
        }

        xaRecoveryModule.addXAResourceRecoveryHelper(new XAResourceRecoveryHelperWrapper(xaResourceRecovery));
    }

    public void removeXAResourceRecovery(XAResourceRecovery xaResourceRecovery)
    {
        if(_recoveryManager == null) {
            jbossatxLogger.loggerI18N.warn("No recovery system from which to remove XAResourceRecovery instance");
            return;
        }

        XARecoveryModule xaRecoveryModule = null;
        for(RecoveryModule recoveryModule : ((Vector <RecoveryModule>)_recoveryManager.getModules())) {
            if(recoveryModule instanceof XARecoveryModule) {
                xaRecoveryModule = (XARecoveryModule)recoveryModule;
                break;
            }
        }

        if(xaRecoveryModule == null) {
            jbossatxLogger.loggerI18N.warn("No suitable recovery module in which to register XAResourceRecovery instance");
            return;
        }

        xaRecoveryModule.removeXAResourceRecoveryHelper(new XAResourceRecoveryHelperWrapper(xaResourceRecovery));
    }
}