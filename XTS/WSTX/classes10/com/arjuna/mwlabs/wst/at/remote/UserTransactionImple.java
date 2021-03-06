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
 * Copyright (C) 2003,
 *
 * Arjuna Technologies Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: UserTransactionImple.java,v 1.21.4.1 2005/11/22 10:36:09 kconner Exp $
 */

package com.arjuna.mwlabs.wst.at.remote;

import java.util.Hashtable;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.mw.wst.TransactionManager;
import com.arjuna.mw.wst.UserTransaction;
import com.arjuna.mw.wst.TxContext;
import com.arjuna.mw.wst.common.Environment;
import com.arjuna.mw.wstx.logging.wstxLogger;
import com.arjuna.mw.wsc.context.Context;
import com.arjuna.mwlabs.wst.at.ContextImple;
import com.arjuna.mwlabs.wst.at.context.TxContextImple;
import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.SoapRegistry;
import com.arjuna.webservices.wsaddr.AttributedURIType;
import com.arjuna.webservices.wsaddr.EndpointReferenceType;
import com.arjuna.webservices.wsarj.InstanceIdentifier;
import com.arjuna.webservices.wsat.AtomicTransactionConstants;
import com.arjuna.webservices.wscoor.CoordinationConstants;
import com.arjuna.webservices.wscoor.CoordinationContextType;
import com.arjuna.wsc.ActivationCoordinator;
import com.arjuna.wsc.InvalidCreateParametersException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;
import com.arjuna.wst.stub.CompletionStub;
import org.jboss.jbossts.xts.environment.XTSPropertyManager;

public class UserTransactionImple extends UserTransaction
{

	public UserTransactionImple ()
	{
		try
		{
            _activationCoordinatorService = XTSPropertyManager.getWSCEnvironmentBean().getCoordinatorURL10();

			/*
			 * If the coordinator URL hasn't been specified via the
			 * configuration file then assume we are using a locally registered
			 * implementation.
			 */

			if (_activationCoordinatorService == null)
			{
                final SoapRegistry soapRegistry = SoapRegistry.getRegistry() ;
                _activationCoordinatorService = soapRegistry.getServiceURI(CoordinationConstants.SERVICE_ACTIVATION_COORDINATOR) ;
			}
		}
		catch (Exception ex)
		{
			// TODO

			ex.printStackTrace();
		}
        _userSubordinateTransaction = new UserSubordinateTransactionImple();
	}

    public UserTransaction getUserSubordinateTransaction() {
        return _userSubordinateTransaction;
    }

    public void begin () throws WrongStateException, SystemException
	{
		begin(0);
	}

	public void begin (int timeout) throws WrongStateException, SystemException
	{
		try
		{
			if (_ctxManager.currentTransaction() != null)
				throw new WrongStateException();

			com.arjuna.mw.wsc.context.Context ctx = startTransaction(timeout, null);

			_ctxManager.resume(new TxContextImple(ctx));

			enlistCompletionParticipants();
		}
		catch (com.arjuna.wsc.InvalidCreateParametersException ex)
		{
			tidyup();

			throw new SystemException(ex.toString());
		}
		catch (com.arjuna.wst.UnknownTransactionException ex)
		{
			tidyup();

			throw new SystemException(ex.toString());
		}
		catch (SystemException ex)
		{
			tidyup();

			throw ex;
		}
	}

	public void commit () throws TransactionRolledBackException,
			UnknownTransactionException, SecurityException, SystemException, WrongStateException
	{
		try
		{
			commitWithoutAck();
		}
		catch (SystemException ex)
		{
			throw ex;
		}
		finally
		{
			tidyup();
		}
	}

	public void rollback () throws UnknownTransactionException, SecurityException, SystemException, WrongStateException
	{
		try
		{
			abortWithoutAck();
		}
		catch (SystemException ex)
		{
			throw ex;
		}
		finally
		{
			tidyup();
		}
	}

	public String transactionIdentifier ()
	{
		try
		{
			return _ctxManager.currentTransaction().toString();
		}
		catch (com.arjuna.wst.SystemException ex)
		{
			return "Unknown";
		}
		catch (NullPointerException ex)
		{
			return "Unknown";
		}
	}

	public String toString ()
	{
		return transactionIdentifier();
	}

    /**
     * method provided for the benefit of UserSubordinateTransactionImple to allow it
     * to begin a subordinate transaction which requires an existing context to be
     * installed on the thread before it will start and instal la new transaction
     *
     * @param timeout
     * @throws WrongStateException
     * @throws SystemException
     */
    public void beginSubordinate(final int timeout)
        throws WrongStateException, SystemException
    {
        try
        {
            TxContext current = _ctxManager.currentTransaction();
            if (current == null || !(current instanceof TxContextImple))
                throw new WrongStateException();
            TxContextImple currentImple = (TxContextImple)current;
            com.arjuna.mw.wsc.context.Context ctx = startTransaction(timeout, currentImple);

            _ctxManager.resume(new TxContextImple(ctx));
            // n.b. we don't enlist the subordinate transaction for completion
            // that ensures that any attempt to commit or rollback will fail
        }
        catch (com.arjuna.wsc.InvalidCreateParametersException ex)
        {
            tidyup();

            throw new SystemException(ex.toString());
        }
        catch (com.arjuna.wst.UnknownTransactionException ex)
        {
            tidyup();

            throw new SystemException(ex.toString());
        }
        catch (SystemException ex)
        {
            tidyup();

            throw ex;
        }
    }

	/*
	 * Not sure if this is right as it doesn't map to registering a participant
	 * with the coordinator.
	 */

	private final void enlistCompletionParticipants ()
			throws WrongStateException, UnknownTransactionException,
			SystemException
	{
		try
		{
			TransactionManagerImple tm = (TransactionManagerImple) TransactionManager.getTransactionManager();

			final String id = ((TxContextImple) tm.currentTransaction()).identifier();
			final EndpointReferenceType completionCoordinator = tm.enlistForCompletion(getCompletionParticipant(id));

			_completionCoordinators.put(id, completionCoordinator);
		}
		catch (com.arjuna.wsc.AlreadyRegisteredException ex)
		{
			throw new SystemException(ex.toString());
		}
	}

    /**
     * fetch the coordination context type stashed in the current AT context implememtation
     * @param current the current AT context implememtation
     * @return the coordination context type stashed in the current AT context implememtation
     */
    private CoordinationContextType getContextType(TxContextImple current)
    {
        Context context = (Context)current.context();
        return context.getCoordinationContext();
    }

    protected final com.arjuna.mw.wsc.context.Context startTransaction(int timeout, TxContextImple current)
			throws com.arjuna.wsc.InvalidCreateParametersException,
			SystemException
	{
		try
		{
            // TODO: tricks for per app _activationCoordinatorService config, perhaps:
            //InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/foo.properties");

            
            final Long expires = (timeout > 0 ? new Long(timeout) : null) ;
            final String messageId = new Uid().stringForm() ;
            final CoordinationContextType currentContext = (current != null ? getContextType(current) : null);
            final CoordinationContextType coordinationContext = ActivationCoordinator.createCoordinationContext(
                    _activationCoordinatorService, messageId, AtomicTransactionConstants.WSAT_PROTOCOL, expires, currentContext) ;
            if (coordinationContext == null)
            {
                throw new SystemException(
                        wstxLogger.i18NLogger.get_mwlabs_wst_at_remote_UserTransactionImple_2());
            }
            return new ContextImple(coordinationContext) ;
		}
        catch (final InvalidCreateParametersException icpe)
        {
            throw icpe ;
        }
		catch (final SoapFault sf)
		{
			throw new SystemException(sf.getMessage()) ;
		}
		catch (final Exception ex)
		{
			throw new SystemException(ex.toString());
		}
	}

	private final void commitWithoutAck ()
			throws TransactionRolledBackException, UnknownTransactionException,
			SecurityException, SystemException, WrongStateException
	{
		TxContextImple ctx = null;
		String id = null;

		try
		{
			ctx = (TxContextImple) _ctxManager.suspend();
            if (ctx == null) {
                throw new WrongStateException();
            }
            id = ctx.identifier();

			/*
			 * By default the completionParticipantURL won't be set for an interposed (imported)
			 * bridged transaction. This is fine, because you shouldn't be able to commit that
			 * transaction from a node in the tree, only from the root. So, we can prevent commit
			 * or rollback at this stage. The alternative would be to setup the completionParticipantURL
			 * and throw the exception from the remote coordinator side (see enlistCompletionParticipants
			 * for how to do this).
			 *
			 * The same applies for an interposed subordinate transaction created via beginSubordinate.
			 */

			final EndpointReferenceType completionCoordinator = (EndpointReferenceType) _completionCoordinators.get(id);

			if (completionCoordinator == null)
				throw new WrongStateException();

			CompletionStub completionStub = new CompletionStub(id, completionCoordinator);

			completionStub.commit();
		}
		catch (SystemException ex)
		{
			throw ex;
		}
		catch (TransactionRolledBackException ex)
		{
			throw ex;
		}
        catch (WrongStateException ex)
        {
            throw ex;
        }
		catch (UnknownTransactionException ex)
		{
			throw ex;
		}
		catch (SecurityException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			throw new SystemException(ex.toString());
		}
		finally
		{
			try
			{
				if (ctx != null)
					_ctxManager.resume(ctx);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			if (id != null)
				_completionCoordinators.remove(id);
		}
	}

	private final void abortWithoutAck () throws UnknownTransactionException, SecurityException,
			SystemException, WrongStateException
	{
		TxContextImple ctx = null;
		String id = null;

		try
		{
			ctx = (TxContextImple) _ctxManager.suspend();
            if (ctx == null) {
                throw new WrongStateException();
            }
			id = ctx.identifier();

			/*
			 * By default the completionParticipantURL won't be set for an interposed (imported)
			 * bridged transaction. This is fine, because you shouldn't be able to commit that
			 * transaction from a node in the tree, only from the root. So, we can prevent commit
			 * or rollback at this stage. The alternative would be to setup the completionParticipantURL
			 * and throw the exception from the remote coordinator side (see enlistCompletionParticipants
			 * for how to do this).
			 *
			 * The same applies for an interposed subordinate transaction created via beginSubordinate.
			 */

			EndpointReferenceType completionCoordinator = (EndpointReferenceType) _completionCoordinators.get(id);

			if (completionCoordinator == null)
				throw new WrongStateException();

			CompletionStub completionStub = new CompletionStub(id, completionCoordinator);

			completionStub.rollback();
		}
		catch (SystemException ex)
		{
			throw ex;
		}
		catch (UnknownTransactionException ex)
		{
			throw ex;
		}
        catch (WrongStateException ex)
        {
            throw ex;
        }
		catch (SecurityException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new SystemException(ex.toString());
		}
		finally
		{
			try
			{
				if (ctx != null)
					_ctxManager.resume(ctx);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			if (id != null)
				_completionCoordinators.remove(id);
		}
	}

    private EndpointReferenceType getCompletionParticipant(final String id)
    {
        final SoapRegistry soapRegistry = SoapRegistry.getRegistry() ;
        final String serviceURI = soapRegistry.getServiceURI(AtomicTransactionConstants.SERVICE_COMPLETION_INITIATOR) ;
        final EndpointReferenceType participant = new EndpointReferenceType(new AttributedURIType(serviceURI)) ;
        InstanceIdentifier.setEndpointInstanceIdentifier(participant, id) ;
        return participant ;
    }

	protected final void tidyup ()
	{
		try
		{
			_ctxManager.suspend();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected ContextManager _ctxManager = new ContextManager();
	protected String _activationCoordinatorService;
	private Hashtable _completionCoordinators = new Hashtable();
    private UserSubordinateTransactionImple _userSubordinateTransaction;
}
