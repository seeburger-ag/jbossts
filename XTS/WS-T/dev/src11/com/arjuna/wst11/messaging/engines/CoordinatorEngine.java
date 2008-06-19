package com.arjuna.wst11.messaging.engines;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.util.TransportTimer;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsat.CoordinatorInboundEvents;
import com.arjuna.webservices11.wsat.State;
import com.arjuna.webservices11.wsat.client.ParticipantClient;
import com.arjuna.webservices11.wsat.processors.CoordinatorProcessor;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wsc11.messaging.MessageId;
import org.oasis_open.docs.ws_tx.wsat._2006._06.Notification;

import javax.xml.namespace.QName;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.TimerTask;

/**
 * The coordinator state engine
 * @author kevin
 */
public class CoordinatorEngine implements CoordinatorInboundEvents
{
    /**
     * Flag indicating this is a coordinator for a durable participant.
     */
    private final boolean durable ;
    /**
     * The coordinator id.
     */
    private final String id ;
    /**
     * The instance identifier.
     */
    private final InstanceIdentifier instanceIdentifier ;
    /**
     * The participant endpoint reference.
     */
    private final W3CEndpointReference participant ;
    /**
     * The current state.
     */
    private State state ;
    /**
     * The flag indicating that this coordinator has been recovered from the log.
     */
    private boolean recovered ;
    /**
     * The flag indicating a read only response.
     */
    private boolean readOnly ;
    /**
     * The associated timer task or null.
     */
    private TimerTask timerTask ;

    /**
     * Construct the initial engine for the coordinator.
     * @param id The coordinator id.
     * @param durable true if the participant is durable, false if volatile.
     * @param participant The participant endpoint reference.
     */
    public CoordinatorEngine(final String id, final boolean durable, final W3CEndpointReference participant)
    {
        this(id, durable, participant, false, State.STATE_ACTIVE) ;
    }

    /**
     * Construct the engine for the coordinator in a specified state.
     * @param id The coordinator id.
     * @param durable true if the participant is durable, false if volatile.
     * @param participant The participant endpoint reference.
     * @param state The initial state.
     */
    public CoordinatorEngine(final String id, final boolean durable, final W3CEndpointReference participant, boolean recovered, final State state)
    {
        this.id = id ;
        this.instanceIdentifier = new InstanceIdentifier(id) ;
        this.durable = durable ;
        this.participant = participant ;
        this.state = state ;
        this.recovered = recovered;

        CoordinatorProcessor.getProcessor().activateCoordinator(this, id) ;
    }

    /**
     * Handle the aborted event.
     * @param aborted The aborted notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * None -> None (ignore)
     * Active -> Aborting (forget)
     * Preparing -> Aborting (forget)
     * PreparedSuccess -> PreparedSuccess (invalid state)
     * Committing -> Committing (invalid state)
     * Aborting -> Aborting (forget)
     */
    public synchronized void aborted(final Notification aborted, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current = state ;
        if (current == State.STATE_ACTIVE)
        {
            changeState(State.STATE_ABORTING) ;
        }
        else if ((current == State.STATE_PREPARING) || (current == State.STATE_ABORTING))
        {
            forget() ;
        }
    }

    /**
     * Handle the committed event.
     * @param committed The committed notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * None -> None (ignore)
     * Active -> Aborting (invalid state)
     * Preparing -> Aborting (invalid state)
     * PreparedSuccess -> PreparedSuccess (invalid state)
     * Committing -> Committing (forget)
     * Aborting -> Aborting (invalid state)
     */
    public synchronized void committed(final Notification committed, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current = state ;
        if (current == State.STATE_ACTIVE)
        {
            changeState(State.STATE_ABORTING) ;
        }
        else if ((current == State.STATE_PREPARING) || (current == State.STATE_COMMITTING))
        {
            forget() ;
        }
    }

    /**
     * Handle the prepared event.
     * @param prepared The prepared notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * None -> Durable: (send rollback), Volatile: Invalid state: none
     * Active -> Aborting (invalid state)
     * Preparing -> PreparedSuccess (Record Vote)
     * PreparedSuccess -> PreparedSuccess (ignore)
     * Committing -> Committing (resend Commit)
     * Aborting -> Aborting (resend Rollback and forget)
     */
    public void prepared(final Notification prepared, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_ABORTING) ;
            }
            else if (current == State.STATE_PREPARING)
            {
                changeState(State.STATE_PREPARED_SUCCESS) ;
            }
        }
        if (current == State.STATE_COMMITTING)
        {
            sendCommit() ;
        }
        else if ((current == State.STATE_ABORTING) || ((current == null) && !readOnly))
        {
            if (durable)
            {
                sendRollback() ;
            }
            else
            {
        	    sendInvalidState(addressingProperties, arjunaContext) ;
            }
            if (current != null)
            {
        	forget() ;
            }
        }
    }

    /**
     * Handle the readOnly event.
     * @param readOnly The readOnly notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * None -> None (ignore)
     * Active -> Active (forget)
     * Preparing -> Preparing (forget)
     * PreparedSuccess -> PreparedSuccess (invalid state)
     * Committing -> Committing (invalid state)
     * Aborting -> Aborting (forget)
     */
    public synchronized void readOnly(final Notification readOnly, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current = state ;
        if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING) ||
            (current == State.STATE_ABORTING))
        {
            if (current != State.STATE_ABORTING)
            {
                this.readOnly = true ;
            }
            forget() ;
        }
    }

    /**
     * Handle the soap fault event.
     * @param soapFault The soap fault.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorEngine.soapFault_1 [com.arjuna.wst11.messaging.engines.CoordinatorEngine.soapFault_1] - Unexpected SOAP fault for coordinator {0}: {1} {2}
     */
    public void soapFault(final SoapFault soapFault, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.arjLoggerI18N.isDebugEnabled())
        {
            final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
            final SoapFaultType soapFaultType = soapFault.getSoapFaultType() ;
            final QName subCode = soapFault.getSubcode() ;
            WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorEngine.soapFault_1", new Object[] {instanceIdentifier, soapFaultType, subCode}) ;
        }
    }

    /**
     * Handle the prepare event.
     *
     * None -> None (invalid state)
     * Active -> Preparing (send prepare)
     * Preparing -> Preparing (resend prepare)
     * PreparedSuccess -> PreparedSuccess (do nothing)
     * Committing -> Committing (invalid state)
     * Aborting -> Aborting (invalid state)
     */
    public State prepare()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_PREPARING) ;
            }
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING))
        {
            sendPrepare() ;
        }

        waitForState(State.STATE_PREPARING, TransportTimer.getTransportTimeout()) ;

        synchronized(this)
        {
            if (state != State.STATE_PREPARING)
            {
                return state ;
            }

            if (timerTask != null)
            {
        	timerTask.cancel() ;

                timerTask = null;
            }
            return state ;
        }
    }

    /**
     * Handle the commit event.
     *
     * None -> None (invalid state)
     * Active -> Active (invalid state)
     * Preparing -> Preparing (invalid state)
     * PreparedSuccess -> Committing (send commit)
     * Committing -> Committing (resend commit)
     * Aborting -> Aborting (invalid state)
     */
    public State commit()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_PREPARED_SUCCESS)
            {
                changeState(State.STATE_COMMITTING) ;
            }
        }

        if ((current == State.STATE_PREPARED_SUCCESS) || (current == State.STATE_COMMITTING))
        {
            sendCommit() ;
        }

        waitForState(State.STATE_COMMITTING, TransportTimer.getTransportTimeout()) ;

        synchronized(this)
        {
            if (state != State.STATE_COMMITTING)
            {
                return state ;
            }

            if (timerTask != null)
            {
                timerTask.cancel() ;

                // this deals with a race to kill the timer task before it runs
                // it may actually have gone off but not been able to call
                // the method to clear this field before we entered this
                // synchronized block. setting the field ot null here notifies
                // the called method that it has been cancelled.

                timerTask = null;
            }

            // no answer means this entry will be saved in the log and the commit retried
            // we remove this engine but leave a ghost to make sure we drop incoming
            // prepared or completed messages from the client until we reinsert a new engine
            // when recovery kicks in. we leave this engine in state COMMITTING so we resend
            // the commit at the next recovery stage

            CoordinatorProcessor.getProcessor().deactivateCoordinator(this, true) ;

            return State.STATE_COMMITTING;
        }
    }

    /**
     * Handle the rollback event.
     *
     * None -> None (invalid state)
     * Active -> Aborting (send rollback)
     * Preparing -> Aborting (send rollback)
     * PreparedSuccess -> Aborting (send rollback)
     * Committing -> Committing (invalid state)
     * Aborting -> Aborting (do nothing)
     */
    public State rollback()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING) ||
                (current == State.STATE_PREPARED_SUCCESS))
            {
                changeState(State.STATE_ABORTING) ;
            }
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_PREPARING) ||
            (current == State.STATE_PREPARED_SUCCESS))
        {
            sendRollback() ;
        }
        else if (current == State.STATE_ABORTING)
        {
            forget() ;
        }

        return waitForState(State.STATE_ABORTING, TransportTimer.getTransportTimeout()) ;
    }

    /**
     * Handle the comms timeout event.
     *
     * Preparing -> Preparing (resend Prepare)
     * Committing -> Committing (resend Commit)
     */
    private void commsTimeout(TimerTask caller)
    {
        final State current ;
        synchronized(this)
        {
            if (timerTask != caller) {
                // the timer was cancelled but it went off before it could be cancelled
                
                return;
            }

            current = state ;
        }

        if (current == State.STATE_PREPARING)
        {
            sendPrepare() ;
        }
        else if (current == State.STATE_COMMITTING)
        {
            sendCommit() ;
        }
    }

    /**
     * Get the coordinator id.
     * @return The coordinator id.
     */
    public String getId()
    {
        return id ;
    }

    /**
     * Get the participant endpoint reference
     * @return The participant endpoint reference
     */
    public W3CEndpointReference getParticipant()
    {
        return participant ;
    }

    /**
     * Is the participant durable?
     * @return true if durable, false otherwise.
     */
    public boolean isDurable()
    {
        return durable ;
    }

    /**
     * Was this a read only response?
     * @return true if a read only response, false otherwise.
     */
    public synchronized boolean isReadOnly()
    {
        return readOnly ;
    }

    /**
     * Change the state and notify any listeners.
     * @param state The new state.
     */
    private synchronized void changeState(final State state)
    {
        if (this.state != state)
        {
            this.state = state ;
            notifyAll() ;
        }
    }

    /**
     * Wait for the state to change from the specified state.
     * @param origState The original state.
     * @param delay The maximum time to wait for (in milliseconds).
     * @return The current state.
     */
    private State waitForState(final State origState, final long delay)
    {
        final long end = System.currentTimeMillis() + delay ;
        synchronized(this)
        {
            while(state == origState)
            {
                final long remaining = end - System.currentTimeMillis() ;
                if (remaining <= 0)
                {
                    break ;
                }
                try
                {
                    wait(remaining) ;
                }
                catch (final InterruptedException ie) {} // ignore
            }
            return state ;
        }
    }

    /**
     * Forget the current coordinator.
     */
    private void forget()
    {
        // we don't leave a ghost entry here
        CoordinatorProcessor.getProcessor().deactivateCoordinator(this, false) ;

        changeState(null) ;
    }

    /**
     * Send the prepare message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendPrepare_1 [com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendPrepare_1] - Unexpecting exception while sending Prepare
     */
    private void sendPrepare()
    {
        TimerTask newTimerTask = createTimerTask();
        synchronized (this) {
            // cancel any existing timer task

            if (timerTask != null) {
                timerTask.cancel();
            }

            // install the new timer task. this signals our intention to post a prepare which may need
            // rescheduling later but allows us to drop the lock on this while we are in the comms layer.
            // our intention can be revised by another thread by reassigning the field to a new task
            // or null

            timerTask = newTimerTask;
        }

        // ok now try the prepare

        try
        {
            ParticipantClient.getClient().sendPrepare(participant, createContext(), instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendPrepare_1", th) ;
            }
        }

        // reobtain the lock before deciding whether to schedule the timer

        synchronized (this) {
            if (timerTask == newTimerTask) {
                // the timer task has not been cancelled so schedule it if appropriate
                if (state == State.STATE_PREPARING) {
                    scheduleTimer(newTimerTask);
                } else {
                    // no need to schedule it so get rid of it
                    timerTask = null;
                }
            }
        }
    }

    /**
     * Send the commit message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendCommit_1 [com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendCommit_1] - Unexpecting exception while sending Commit
     */
    private void sendCommit()
    {
        TimerTask newTimerTask = createTimerTask();
        synchronized (this) {
            // cancel any existing timer task

            if (timerTask != null) {
                timerTask.cancel();
            }

            // install the new timer task. this signals our intention to post a commit which may need
            // rescheduling later but allows us to drop the lock on this while we are in the comms layer.
            // our intention can be revised by another thread by reassigning the field to a new task
            // or null

            timerTask = newTimerTask;
        }

        // ok now try the commit

        try
        {
            ParticipantClient.getClient().sendCommit(participant, createContext(), instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendCommit_1", th) ;
            }
        }

        // reobtain the lock before deciding whether to schedule the timer

        synchronized (this) {
            if (timerTask == newTimerTask) {
                // the timer task has not been cancelled so schedule it if appropriate
                if (state == State.STATE_COMMITTING) {
                    scheduleTimer(newTimerTask);
                } else {
                    // no need to schedule it so get rid of it
                    timerTask = null;
                }
            }
        }
    }

    /**
     * Send the rollback message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendRollback_1 [com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendRollback_1] - Unexpecting exception while sending Rollback
     */
    private void sendRollback()
    {
        try
        {
            ParticipantClient.getClient().sendRollback(participant, createContext(), instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendRollback_1", th) ;
            }
        }
    }

    /**
     * Send the InvalidStateWS message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendInvalidState_1 [com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendInvalidState_1] - Inconsistent internal state.
     * @message com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendInvalidState_2 [com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendInvalidState_2] - Unexpecting exception while sending InvalidState
     */
    private void sendInvalidState(final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        try
        {
            final AddressingProperties faultAddressingProperties = AddressingHelper.createFaultContext(addressingProperties, MessageId.getMessageId()) ;
            final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;

            final String message = WSTLogger.log_mesg.getString("com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendInvalidState_1") ;
            final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME, message) ;
            ParticipantClient.getClient().sendSoapFault(faultAddressingProperties, soapFault, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorEngine.sendInvalidState_2", th) ;
            }
        }
    }

    /**
     * create a timer task to handle a comms timeout
     *
     * @return the timer task
     */
    private TimerTask createTimerTask()
    {
        return new TimerTask() {
            public void run() {
                commsTimeout(this) ;
            }
        } ;
    }

    /**
     * schedule a timer task to handle a commms timeout
     * @param timerTask the timer task to be scheduled
     */

    private void scheduleTimer(TimerTask timerTask)
    {
        TransportTimer.getTimer().schedule(timerTask, TransportTimer.getTransportPeriod()) ;
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
        if ((state == State.STATE_PREPARING) || (state == State.STATE_COMMITTING))
        {
            timerTask = new TimerTask() {
                public void run() {
                    commsTimeout(this) ;
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
     * Create a context for the outgoing message.
     * @return The addressing context.
     */
    private AddressingProperties createContext()
    {
        final String messageId = MessageId.getMessageId() ;

        return AddressingHelper.createNotificationContext(messageId) ;
    }
}
