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
package com.arjuna.wst.messaging.engines;

import java.util.TimerTask;

import javax.xml.namespace.QName;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.util.TransportTimer;
import com.arjuna.webservices.wsaddr.AddressingContext;
import com.arjuna.webservices.wsaddr.EndpointReferenceType;
import com.arjuna.webservices.wsarj.ArjunaContext;
import com.arjuna.webservices.wsarj.InstanceIdentifier;
import com.arjuna.webservices.wsat.NotificationType;
import com.arjuna.webservices.wsat.ParticipantInboundEvents;
import com.arjuna.webservices.wsat.State;
import com.arjuna.webservices.wsat.client.CoordinatorClient;
import com.arjuna.webservices.wsat.processors.ParticipantProcessor;
import com.arjuna.webservices.wscoor.CoordinationConstants;
import com.arjuna.wsc.messaging.MessageId;
import com.arjuna.wst.Aborted;
import com.arjuna.wst.Participant;
import com.arjuna.wst.Prepared;
import com.arjuna.wst.ReadOnly;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.Vote;

/**
 * The participant state engine
 * @author kevin
 */
public class ParticipantEngine implements ParticipantInboundEvents
{
    /**
     * The associated participant
     */
    private final Participant participant ;
    /**
     * The participant id.
     */
    private final String id ;
    /**
     * The coordinator endpoint reference.
     */
    private final EndpointReferenceType coordinator ;
    /**
     * The current state.
     */
    private State state ;
    /**
     * The associated timer task or null.
     */
    private TimerTask timerTask ;
    
    /**
     * Construct the initial engine for the participant.
     * @param participant The participant.
     * @param id The participant id.
     * @param coordinator The coordinator endpoint reference.
     */
    public ParticipantEngine(final Participant participant, final String id, final EndpointReferenceType coordinator)
    {
        this(participant, id, State.STATE_ACTIVE, coordinator) ;
    }
    
    /**
     * Construct the engine for the participant in a specified state.
     * @param participant The participant.
     * @param id The participant id.
     * @param state The initial state.
     * @param coordinator The coordinator endpoint reference.
     */
    public ParticipantEngine(final Participant participant, final String id, final State state, final EndpointReferenceType coordinator)
    {
        this.participant = participant ;
        this.id = id ;
        this.state = state ;
        this.coordinator = coordinator ;
    }
    
    /**
     * Handle the commit event.
     * @param commit The commit notification.
     * @param addressingContext The addressing context.
     * @param arjunaContext The arjuna context.
     * 
     * None -> None (send committed)
     * Active -> Aborting (do nothing)
     * Preparing -> Aborting (do nothing)
     * PreparedSuccess -> Committing (initiate commit)
     * Committing -> Committing (do nothing)
     * Aborting -> Aborting (do nothing)
     */
    public void commit(final NotificationType commit, final AddressingContext addressingContext, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_PREPARED_SUCCESS)
            {
                state = State.STATE_COMMITTING ;
                if (timerTask != null)
                {
                    timerTask.cancel() ;
                }
            }
            else if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING))
            {
                state = State.STATE_ABORTING ;
            }
        }
        
        if (current == State.STATE_PREPARED_SUCCESS)
        {
            executeCommit() ;
        }
        else if (current == null)
        {
            sendCommitted() ;
        }
    }
    
    /**
     * Handle the prepare event.
     * @param prepare The prepare notification.
     * @param addressingContext The addressing context.
     * @param arjunaContext The arjuna context.
     * 
     * None -> None (send aborted)
     * Active -> Preparing (execute prepare)
     * Preparing -> Preparing (do nothing)
     * PreparedSuccess -> PreparedSuccess (resend prepared)
     * Committing -> Committing (ignore)
     * Aborting -> Aborting (send aborted and forget)
     */
    public void prepare(final NotificationType prepare, final AddressingContext addressingContext, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                state = State.STATE_PREPARING ;
            }
        }
        
        if (current == State.STATE_ACTIVE)
        {
            executePrepare() ;
        }
        else if (current == State.STATE_PREPARED_SUCCESS)
        {
            sendPrepared() ;
        }
        else if ((current == State.STATE_ABORTING) || (current == null))
        {
            sendAborted() ;
            forget() ;
        }
    }
    
    /**
     * Handle the rollback event.
     * @param rollback The rollback notification.
     * @param addressingContext The addressing context.
     * @param arjunaContext The arjuna context.
     * 
     * None -> None (send aborted)
     * Active -> Aborting (execute rollback, send aborted and forget)
     * Preparing -> Aborting (execute rollback, send aborted and forget)
     * PreparedSuccess -> Aborting (execute rollback, send aborted and forget)
     * Committing -> Committing (ignore)
     * Aborting -> Aborting (send aborted and forget)
     */
    public void rollback(final NotificationType rollback, final AddressingContext addressingContext, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING) ||
                (current == State.STATE_PREPARED_SUCCESS))
            {
                state = State.STATE_ABORTING ;
            }
        }
        
        if (current != State.STATE_COMMITTING)
        {
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING) ||
                (current == State.STATE_PREPARED_SUCCESS))
            {
                if (!executeRollback())
                {
                    return ;
                }
            }
            
            sendAborted() ;
            
            if (current != null)
            {
                forget() ;
            }
        }
    }
    
    /**
     * Handle the early rollback event.
     * 
     * None -> None
     * Active -> Aborting (execute rollback, send aborted and forget)
     * Preparing -> Aborting (execute rollback, send aborted and forget)
     * PreparedSuccess -> PreparedSuccess
     * Committing -> Committing
     * Aborting -> Aborting
     */
    public void earlyRollback()
    {
        rollbackDecision() ;
    }
    
    /**
     * Handle the early readonly event.
     * 
     * None -> None
     * Active -> None (send ReadOnly)
     * Preparing -> None (send ReadOnly)
     * PreparedSuccess -> PreparedSuccess
     * Committing -> Committing
     * Aborting -> Aborting
     */
    public void earlyReadonly()
    {
        readOnlyDecision() ;
    }
    
    /**
     * Handle the recovery event.
     * 
     * None -> None
     * Active -> Active
     * Preparing -> Preparing
     * PreparedSuccess -> PreparedSuccess
     * Committing -> PreparedSuccess (resend Prepared)
     * Aborting -> Aborting
     */
    public void recovery()
    {
	synchronized(this)
	{
	    if (timerTask != null)
	    {
                timerTask.cancel() ;
	    }
	}
        sendReplay() ;
    }
    
    /**
     * Handle the soap fault event.
     * @param soapFault The soap fault.
     * @param addressingContext The addressing context.
     * @param arjunaContext The arjuna context.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.soapFault_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.soapFault_1] - Unexpected SOAP fault for participant {0}: {1} {2}
     */
    public void soapFault(final SoapFault soapFault, final AddressingContext addressingContext, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.arjLoggerI18N.isDebugEnabled())
        {
            final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
            final SoapFaultType soapFaultType = soapFault.getSoapFaultType() ;
            final QName subCode = soapFault.getSubcode() ;
            WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.soapFault_1", new Object[] {instanceIdentifier, soapFaultType, subCode}) ;
        }
        
        if (CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME.equals(soapFault.getSubcode()))
        {
            forget() ;
        }
    }
    
    /**
     * Handle the commit decision event.
     * 
     * Preparing -> PreparedSuccess (send Prepared)
     * Committing -> Committing (send committed and forget)
     */
    private void commitDecision()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_PREPARING)
            {
                state = State.STATE_PREPARED_SUCCESS ;
            }
        }
        
        if (current == State.STATE_PREPARING)
        {
            sendPrepared() ;
        }
        else if (current == State.STATE_COMMITTING)
        {
            sendCommitted() ;
            forget() ;
        }
    }
    
    /**
     * Handle the readOnly decision event.
     * 
     * Active -> None (send ReadOnly)
     * Preparing -> None (send ReadOnly)
     */
    private void readOnlyDecision()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING))
            {
        	state = null ;
            }
        }
        
        if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING))
        {
            sendReadOnly() ;
            forget() ;
        }
    }
    
    /**
     * Handle the rollback decision event.
     * 
     * Active -> Aborting (send aborted)
     * Preparing -> Aborting (send aborted)
     */
    private void rollbackDecision()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_PREPARING) || (current == State.STATE_ACTIVE))
            {
                state = State.STATE_ABORTING ;
            }
        }
        
        if ((current == State.STATE_PREPARING) || (current == State.STATE_ACTIVE))
        {
            sendAborted() ;
            forget() ;
        }
    }
    
    /**
     * Handle the comms timeout event.
     * 
     * PreparedSuccess -> PreparedSuccess (resend Prepared)
     */
    private void commsTimeout()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
        }
        
        if (current == State.STATE_PREPARED_SUCCESS)
        {
            sendPrepared() ;
        }
    }
    
    /**
     * Execute the commit transition.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.executeCommit_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.executeCommit_1] - Unexpected exception from participant commit
     */
    private void executeCommit()
    {
        try
        {
            participant.commit() ;
            commitDecision() ;
        }
        catch (final Throwable th)
        {
            synchronized(this)
            {
                if (state == State.STATE_COMMITTING)
                {
            	    state = State.STATE_PREPARED_SUCCESS ;
                }
            }
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.executeCommit_1", th) ;
            }
        }
    }
    
    /**
     * Execute the rollback transition.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.executeRollback_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.executeRollback_1] - Unexpected exception from participant rollback
     */
    private boolean executeRollback()
    {
        try
        {
            participant.rollback() ;
        }
        catch (final SystemException se)
        {
            return false ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.executeRollback_1", th) ;
            }
        }
        return true ;
    }
    
    /**
     * Execute the prepare transition.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.executePrepare_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.executePrepare_1] - Unexpected exception from participant prepare
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.executePrepare_2 [com.arjuna.wst.messaging.engines.ParticipantEngine.executePrepare_2] - Unexpected result from participant prepare: {0}
     */
    private void executePrepare()
    {
        final Vote vote ;
        try
        {
            vote = participant.prepare();
        }
        catch (final SystemException se)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.executePrepare_1", se) ;
            }
            return ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.executePrepare_1", th) ;
            }
            rollbackDecision() ;
            return ;
        }
        
        if (vote instanceof Prepared)
        {
            commitDecision() ;
        }
        else if (vote instanceof ReadOnly)
        {
            readOnlyDecision() ;
        }
        else if (vote instanceof Aborted)
        {
            rollbackDecision() ;
        }
        else
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.executePrepare_2", new Object[] {(vote == null ? "null" : vote.getClass().getName())}) ;
            }
            rollbackDecision() ;
        }
    }
    
    /**
     * Forget the current participant.
     */
    private void forget()
    {
        synchronized(this)
        {
            state = null ;
        }
        ParticipantProcessor.getProcessor().deactivateParticipant(this) ;
    }
    
    /**
     * Send the committed message.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.sendCommitted_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.sendCommitted_1] - Unexpected exception while sending Committed
     */
    private void sendCommitted()
    {
        final AddressingContext responseAddressingContext = createContext() ;
        final InstanceIdentifier instanceIdentifier = new InstanceIdentifier(id) ;
        try
        {
            CoordinatorClient.getClient().sendCommitted(responseAddressingContext, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.sendCommitted_1", th) ;
            }
        }
    }
    
    /**
     * Send the prepared message.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.sendPrepared_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.sendPrepared_1] - Unexpected exception while sending Prepared
     */
    private void sendPrepared()
    {
        final AddressingContext responseAddressingContext = createContext() ;
        final InstanceIdentifier instanceIdentifier = new InstanceIdentifier(id) ;
        try
        {
            CoordinatorClient.getClient().sendPrepared(responseAddressingContext, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.sendPrepared_1", th) ;
            }
        }
        
        initiateTimer() ;
    }
    
    /**
     * Send the aborted message.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.sendAborted_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.sendAborted_1] - Unexpected exception while sending Aborted
     */
    private void sendAborted()
    {
        final AddressingContext responseAddressingContext = createContext() ;
        final InstanceIdentifier instanceIdentifier = new InstanceIdentifier(id) ;
        try
        {
            CoordinatorClient.getClient().sendAborted(responseAddressingContext, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.sendAborted_1", th) ;
            }
        }
    }
    
    /**
     * Send the read only message.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.sendReadOnly_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.sendReadOnly_1] - Unexpected exception while sending ReadOnly
     */
    private void sendReadOnly()
    {
        final AddressingContext responseAddressingContext = createContext() ;
        final InstanceIdentifier instanceIdentifier = new InstanceIdentifier(id) ;
        try
        {
            CoordinatorClient.getClient().sendReadOnly(responseAddressingContext, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.sendReadOnly_1", th) ;
            }
        }
    }
    
    /**
     * Send the replay message.
     * 
     * @message com.arjuna.wst.messaging.engines.ParticipantEngine.sendReplay_1 [com.arjuna.wst.messaging.engines.ParticipantEngine.sendReplay_1] - Unexpected exception while sending Replay
     */
    private void sendReplay()
    {
        final AddressingContext responseAddressingContext = createContext() ;
        final InstanceIdentifier instanceIdentifier = new InstanceIdentifier(id) ;
        try
        {
            CoordinatorClient.getClient().sendReplay(responseAddressingContext, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst.messaging.engines.ParticipantEngine.sendReplay_1", th) ;
            }
        }
    }
    
    /**
     * Initiate the timer.
     */
    private synchronized void initiateTimer()
    {
        if (timerTask != null)
        {
            timerTask.cancel() ;
        }
        
        if (state == State.STATE_PREPARED_SUCCESS)
        {
            timerTask = new TimerTask() {
                public void run() {
                    commsTimeout() ;
                }
            } ;
            TransportTimer.getTimer().schedule(timerTask, TransportTimer.getTransportPeriod()) ;
        }
        else
        {
            timerTask = null ;
        }
    }
    
    /**
     * Create a response context from the incoming context.
     * @return The addressing context.
     */
    private AddressingContext createContext()
    {
        final String messageId = MessageId.getMessageId() ;
        return AddressingContext.createRequestContext(coordinator, messageId) ;
    }
}