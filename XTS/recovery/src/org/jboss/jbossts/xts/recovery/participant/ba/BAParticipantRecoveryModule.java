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
 * (C) 2007,
 * @author Red Hat Middleware LLC.
 */
package org.jboss.jbossts.xts.recovery.participant.ba;

import com.arjuna.ats.arjuna.objectstore.StateStatus;
import org.jboss.jbossts.xts.logging.XTSLogger;

import com.arjuna.ats.arjuna.recovery.RecoveryModule;
import com.arjuna.ats.arjuna.logging.FacilityCode;
import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.objectstore.ObjectStore;
import com.arjuna.ats.internal.arjuna.common.UidHelper;
import com.arjuna.common.util.logging.DebugLevel;
import com.arjuna.common.util.logging.VisibilityLevel;

import java.util.Vector;
import java.util.Enumeration;
import java.util.HashMap;
import java.io.IOException;

/**
 * This class is a plug-in module for the recovery manager.
 * It is responsible for recovering XTS BA participants
 * (instances of org.jboss.jbossts.xts.recovery.participant.ba.BAParticipantRecoveryRecord)
 *
 * $Id$
 *
 * @message org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_1 [org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_1] - RecoveryManagerStatusModule: Object store exception: {0}
 * @message org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_3 [org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_3] - failed to access transaction store {0}
 * @message org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_4 [org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_4] - unable to load recovery record implementation class {0} for WS-BA participant recovery record {1}
 * @message org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_5 [org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_5] - unable to instantiate recovery record implementation class {0} for WS-BA participant recovery record {1}
 * @message org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_6 [org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_6] - unable to unpack recovery record data for WS-BA participant recovery record {0}
 * @message org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_7 [org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_7] - missing recovery record data for WS-BA participant recovery record {0}
 * @message org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_8 [org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_8] - unable to read recovery record data for WS-BA participant recovery record {0}
 */

public class BAParticipantRecoveryModule implements RecoveryModule
{
    public BAParticipantRecoveryModule()
    {
        if (XTSLogger.arjLogger.isDebugEnabled())
        {
            XTSLogger.arjLogger.debug
                    ( DebugLevel.CONSTRUCTORS,
                            VisibilityLevel.VIS_PUBLIC,
                            FacilityCode.FAC_CRASH_RECOVERY,
                            "BAParticipantRecoveryModule created - default" );
        }

        if (_objectStore == null)
        {
            _objectStore = TxControl.getStore() ;
        }

        _participantType = BAParticipantRecoveryRecord.type();
    }

    /**
     * called by the service startup code before the recovery module is added to the recovery managers
     * module list
     */
    public void install()
    {
        XTSBARecoveryManager.setRecoveryManager(new XTSBARecoveryManagerImple(_objectStore));
        // Subordinate Coordinators register durable participants with their parent transaction so
        // we need to add an XTSBARecoveryModule which knows about the registered participants

        subordinateRecoveryModule = new XTSBASubordinateRecoveryModule();
         XTSBARecoveryManager.getRecoveryManager().registerRecoveryModule(subordinateRecoveryModule);
    }

    /**
     * a recovery module which knows hwo to recover the participants registered by Subordinate BA Coordinators
     */

    private XTSBASubordinateRecoveryModule subordinateRecoveryModule;

    /**
     * called by the service shutdown code after the recovery module is removed from the recovery managers
     * module list
     */
    public void uninstall()
    {
        XTSBARecoveryManager.getRecoveryManager().unregisterRecoveryModule(subordinateRecoveryModule);
    }

    /**
     * This is called periodically by the RecoveryManager
     */
    public void periodicWorkFirstPass()
    {
        // Transaction type
        boolean BAParticipants = false ;

        // uids per transaction type
        InputObjectState acc_uids = new InputObjectState() ;

        try
        {
            if (XTSLogger.arjLogger.isDebugEnabled())
            {
                XTSLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
                            FacilityCode.FAC_CRASH_RECOVERY, "BAParticipantRecoveryModule: first pass");
            }

            BAParticipants = _objectStore.allObjUids(_participantType, acc_uids );

        }
        catch ( ObjectStoreException ex )
        {
            if (XTSLogger.arjLoggerI18N.isWarnEnabled())
            {
                XTSLogger.arjLoggerI18N.warn("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_1",
                        ex);
            }
        }

        if ( BAParticipants )
        {
            _participantUidVector = processParticipants( acc_uids ) ;
        }
    }

    public void periodicWorkSecondPass()
    {
        if (XTSLogger.arjLogger.isDebugEnabled())
        {
            XTSLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
                            FacilityCode.FAC_CRASH_RECOVERY, "BAParticipantRecoveryModule: Second pass");
        }

        processParticipantsStatus() ;
    }

    private void doRecoverParticipant( Uid recoverUid )
    {
        // Retrieve the participant from its original process.

        if (XTSLogger.arjLogger.isDebugEnabled())
        {
            XTSLogger.arjLogger.debug
                    ( DebugLevel.FUNCTIONS,
                            VisibilityLevel.VIS_PUBLIC,
                            FacilityCode.FAC_CRASH_RECOVERY,
                            "participant type is "+ _participantType + " uid is " +
                                    recoverUid.toString()) ;
        }

        // we don't need to use a lock here because we only attempt the read
        // when the uid is inactive which means it cannto be pulled out form under our
        // feet at commit. uniqueness of uids also means we can't be foiled by a reused
        // uid.

        XTSBARecoveryManager recoveryManager = XTSBARecoveryManager.getRecoveryManager();

        if (!recoveryManager.isParticipantPresent(recoverUid)) {
            // ok, the participant can neither be active nor loaded awaiting recreation by
            // an application recovery module so we need to load it
            try {
                // retrieve the data for the participant
                InputObjectState inputState = _objectStore.read_committed(recoverUid, _participantType);

                if (inputState != null) {
                    try {
                        String participantRecordClazzName = inputState.unpackString();
                        try {
                            // create a participant engine instance and tell it to recover itself
                            Class participantRecordClazz = Class.forName(participantRecordClazzName);
                            BAParticipantRecoveryRecord participantRecord = (BAParticipantRecoveryRecord)participantRecordClazz.newInstance();
                            participantRecord.restoreState(inputState);
                            // ok, now insert into recovery map if needed
                            XTSBARecoveryManager.getRecoveryManager().addParticipantRecoveryRecord(recoverUid, participantRecord);
                        } catch (ClassNotFoundException cnfe) {
                            // oh boy, not supposed to happen -- n.b. either the user deployed 1.0
                            // last time and 1.1 this time or vice versa or something is rotten in
                            // the state of Danmark
                            if (XTSLogger.arjLoggerI18N.isErrorEnabled())
                            {
                                XTSLogger.arjLoggerI18N.error("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_4",
                                        new Object[]{participantRecordClazzName, recoverUid.toString()}, cnfe);
                            }
                        } catch (InstantiationException ie) {
                            // this is also worrying, log an error
                            if (XTSLogger.arjLoggerI18N.isErrorEnabled())
                            {
                                XTSLogger.arjLoggerI18N.error("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_5",
                                        new Object[]{participantRecordClazzName, recoverUid.toString()}, ie);
                            }
                        } catch (IllegalAccessException iae) {
                            // this is another configuration problem, log an error
                            if (XTSLogger.arjLoggerI18N.isErrorEnabled())
                            {
                                XTSLogger.arjLoggerI18N.error("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_5",
                                        new Object[]{participantRecordClazzName, recoverUid.toString()}, iae);
                            }
                        }
                    } catch (IOException ioe) {
                        // hmm, record corrupted? log this as a warning
                        if (XTSLogger.arjLoggerI18N.isWarnEnabled())
                        {
                            XTSLogger.arjLoggerI18N.warn("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_6",
                                    new Object[]{recoverUid.toString()}, ioe);
                        }
                    }
                } else {
                    // hmm, it ought not to be able to disappear unless the recovery manager knows about it
                    // this is an error!
                    if (XTSLogger.arjLoggerI18N.isErrorEnabled())
                    {
                        XTSLogger.arjLoggerI18N.error("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_7",
                                new Object[]{recoverUid.toString()});
                    }
                }
            } catch (ObjectStoreException ose) {
                // if the object store is not working this is serious
                if (XTSLogger.arjLoggerI18N.isErrorEnabled())
                {
                    XTSLogger.arjLoggerI18N.error("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_8",
                            new Object[]{recoverUid.toString()}, ose);
                }
            }
        }
    }

    private Vector processParticipants( InputObjectState uids )
    {
        Vector uidVector = new Vector() ;

        if (XTSLogger.arjLogger.isDebugEnabled())
        {
            XTSLogger.arjLogger.debug( DebugLevel.FUNCTIONS,
                    VisibilityLevel.VIS_PUBLIC,
                    FacilityCode.FAC_CRASH_RECOVERY,
                    "processing " + _participantType
                            + " WS-BA participants" ) ;
        }

        Uid NULL_UID = Uid.nullUid();
        Uid theUid = null;

        while (true)
        {
            try
            {
                theUid = UidHelper.unpackFrom( uids ) ;
            }
            catch ( Exception ex )
            {
                break;
            }

            if (theUid.equals( NULL_UID ))
            {
                break;
            }

            if (XTSLogger.arjLogger.isDebugEnabled())
            {
                XTSLogger.arjLogger.debug
                        ( DebugLevel.FUNCTIONS,
                                VisibilityLevel.VIS_PUBLIC,
                                FacilityCode.FAC_CRASH_RECOVERY,
                                "found WS-BA participant "+ theUid ) ;
            }

            uidVector.addElement( theUid ) ;
        }

        return uidVector ;
    }

    private void processParticipantsStatus()
    {
        if (_participantUidVector != null) {
        // Process the Vector of transaction Uids
        Enumeration participantUidEnum = _participantUidVector.elements() ;

        while ( participantUidEnum.hasMoreElements() )
        {
            Uid currentUid = (Uid) participantUidEnum.nextElement();

            try
            {
                if ( _objectStore.currentState( currentUid, _participantType) != StateStatus.OS_UNKNOWN )
                {
                    doRecoverParticipant( currentUid ) ;
                }
            }
            catch ( ObjectStoreException ex )
            {
                if (XTSLogger.arjLogger.isWarnEnabled())
                {
                    XTSLogger.arjLoggerI18N.warn("org.jboss.transactions.xts.recovery.participant.ba.BAParticipantRecoveryModule_3",
                            new Object[]{currentUid.toString()}, ex);
                }
            }
        }
        }

        // now get the BA recovery manager to try to activate recovered participants

        XTSBARecoveryManager.getRecoveryManager().recoverParticipants();
    }

    // 'type' within the Object Store for BAParticipant record.
    private String _participantType = BAParticipantRecoveryRecord.type() ;

    // Array of transactions found in the object store of the
    // ACCoordinator type.
    private Vector _participantUidVector = null ;

    // Reference to the Object Store.
    private static ObjectStore _objectStore = null ;

    // This object provides information about whether or not a participant is currently active.

    private HashMap<String, BAParticipantRecoveryRecord> _recoveredParticipantMap ;
}