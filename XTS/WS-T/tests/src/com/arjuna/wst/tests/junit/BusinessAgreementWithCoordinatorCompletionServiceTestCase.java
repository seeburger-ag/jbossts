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
 * Copyright (c) 2004, Arjuna Technologies Limited.
 *
 * $Id: BusinessAgreementWithCoordinatorCompletionServiceTestCase.java,v 1.1.2.2 2004/06/18 15:06:10 nmcl Exp $
 */

package com.arjuna.wst.tests.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.arjuna.webservices.SoapRegistry;
import com.arjuna.webservices.wsaddr.AttributedURIType;
import com.arjuna.webservices.wsaddr.EndpointReferenceType;
import com.arjuna.webservices.wsarj.InstanceIdentifier;
import com.arjuna.webservices.wsba.BusinessActivityConstants;
import com.arjuna.wst.BusinessAgreementWithCoordinatorCompletionParticipant;
import com.arjuna.wst.FaultedException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.WrongStateException;
import com.arjuna.wst.messaging.engines.CoordinatorCompletionCoordinatorEngine;
import com.arjuna.wst.stub.BusinessAgreementWithCoordinatorCompletionStub;
import com.arjuna.wst.tests.TestUtil;

public class BusinessAgreementWithCoordinatorCompletionServiceTestCase
{
    @Before
    public void setUp()
        throws Exception
    {
        final SoapRegistry soapRegistry = SoapRegistry.getRegistry() ;
        final String coordinatorCompletionParticipantServiceURI = soapRegistry.getServiceURI(BusinessActivityConstants.SERVICE_COORDINATOR_COMPLETION_PARTICIPANT) ;

        EndpointReferenceType noExceptionBusinessAgreementWithCoordinatorCompletionCoordinator = new EndpointReferenceType(new AttributedURIType(coordinatorCompletionParticipantServiceURI));
        InstanceIdentifier.setEndpointInstanceIdentifier(noExceptionBusinessAgreementWithCoordinatorCompletionCoordinator, TestUtil.NOEXCEPTION_PARTICIPANT_IDENTIFIER);

        EndpointReferenceType wrongStateExceptionBusinessAgreementWithCoordinatorCompletionCoordinator = new EndpointReferenceType(new AttributedURIType(coordinatorCompletionParticipantServiceURI));
        InstanceIdentifier.setEndpointInstanceIdentifier(wrongStateExceptionBusinessAgreementWithCoordinatorCompletionCoordinator, TestUtil.WRONGSTATEEXCEPTION_PARTICIPANT_IDENTIFIER);

        EndpointReferenceType systemExceptionBusinessAgreementWithCoordinatorCompletionCoordinator = new EndpointReferenceType(new AttributedURIType(coordinatorCompletionParticipantServiceURI));
        InstanceIdentifier.setEndpointInstanceIdentifier(systemExceptionBusinessAgreementWithCoordinatorCompletionCoordinator, TestUtil.SYSTEMEXCEPTION_PARTICIPANT_IDENTIFIER);

        EndpointReferenceType nonexistentBusinessAgreementWithCoordinatorCompletionCoordinator = new EndpointReferenceType(new AttributedURIType(coordinatorCompletionParticipantServiceURI));
        InstanceIdentifier.setEndpointInstanceIdentifier(nonexistentBusinessAgreementWithCoordinatorCompletionCoordinator, TestUtil.NONEXISTENT_PARTICIPANT_IDENTIFIER);

        EndpointReferenceType faultedExceptionBusinessAgreementWithCoordinatorCompletionCoordinator = new EndpointReferenceType(new AttributedURIType(coordinatorCompletionParticipantServiceURI));
        InstanceIdentifier.setEndpointInstanceIdentifier(faultedExceptionBusinessAgreementWithCoordinatorCompletionCoordinator, TestUtil.FAULTEDEXCEPTION_PARTICIPANT_IDENTIFIER);

        CoordinatorCompletionCoordinatorEngine _noExceptionBusinessAgreementWithCoordinatorCompletionEngine
                = new CoordinatorCompletionCoordinatorEngine("businessAgreementWithCoordinatorCompletionCoordinator", noExceptionBusinessAgreementWithCoordinatorCompletionCoordinator);
        CoordinatorCompletionCoordinatorEngine _wrongStateExceptionBusinessAgreementWithCoordinatorCompletionEngine
                = new CoordinatorCompletionCoordinatorEngine("businessAgreementWithCoordinatorCompletionCoordinator", wrongStateExceptionBusinessAgreementWithCoordinatorCompletionCoordinator);
        CoordinatorCompletionCoordinatorEngine _systemExceptionBusinessAgreementWithCoordinatorCompletionEngine
                = new CoordinatorCompletionCoordinatorEngine("businessAgreementWithCoordinatorCompletionCoordinator", systemExceptionBusinessAgreementWithCoordinatorCompletionCoordinator);
        CoordinatorCompletionCoordinatorEngine _faultedExceptionBusinessAgreementWithCoordinatorCompletionEngine
                = new CoordinatorCompletionCoordinatorEngine("businessAgreementWithCoordinatorCompletionCoordinator", faultedExceptionBusinessAgreementWithCoordinatorCompletionCoordinator);
        _noExceptionBusinessAgreementWithCoordinatorCompletionStub
                = new BusinessAgreementWithCoordinatorCompletionStub(_noExceptionBusinessAgreementWithCoordinatorCompletionEngine);
        _wrongStateExceptionBusinessAgreementWithCoordinatorCompletionStub
                = new BusinessAgreementWithCoordinatorCompletionStub(_wrongStateExceptionBusinessAgreementWithCoordinatorCompletionEngine);
        _systemExceptionBusinessAgreementWithCoordinatorCompletionStub
                = new BusinessAgreementWithCoordinatorCompletionStub(_systemExceptionBusinessAgreementWithCoordinatorCompletionEngine);
        _faultedExceptionBusinessAgreementWithCoordinatorCompletionStub
                = new BusinessAgreementWithCoordinatorCompletionStub(_faultedExceptionBusinessAgreementWithCoordinatorCompletionEngine);
    }

    @Test
    public void testCloseWithNoException()
        throws Exception
    {
        _noExceptionBusinessAgreementWithCoordinatorCompletionStub.close();
    }

    @Test
    public void testCancelWithNoException()
        throws Exception
    {
        _noExceptionBusinessAgreementWithCoordinatorCompletionStub.cancel();
    }

    @Test
    public void testCompensateWithNoException()
        throws Exception
    {
        _noExceptionBusinessAgreementWithCoordinatorCompletionStub.compensate();
    }

    @Test
    public void testCloseWithWrongStateException()
        throws Exception
    {
        try
        {
            _wrongStateExceptionBusinessAgreementWithCoordinatorCompletionStub.close();
            fail("Expected exception \"WrongStateException\"");
        }
        catch (WrongStateException wrongStateException)
        {
        }
    }

    @Test
    public void testCancelWithWrongStateException()
        throws Exception
    {
        try
        {
            _wrongStateExceptionBusinessAgreementWithCoordinatorCompletionStub.cancel();
            fail("Expected exception \"WrongStateException\"");
        }
        catch (WrongStateException wrongStateException)
        {
        }
    }

    @Test
    public void testCompensateWithWrongStateException()
        throws Exception
    {
        try
        {
            _wrongStateExceptionBusinessAgreementWithCoordinatorCompletionStub.compensate();
            fail("Expected exception \"WrongStateException\"");
        }
        catch (WrongStateException wrongStateException)
        {
        }
    }

    @Test
    public void testCloseWithSystemException()
        throws Exception
    {
        try
        {
            _systemExceptionBusinessAgreementWithCoordinatorCompletionStub.close();
            fail("Expected exception \"SystemException\"");
        }
        catch (SystemException systemException)
        {
        }
    }

    @Test
    public void testCancelWithSystemException()
        throws Exception
    {
        try
        {
            _systemExceptionBusinessAgreementWithCoordinatorCompletionStub.cancel();
            fail("Expected exception \"SystemException\"");
        }
        catch (SystemException systemException)
        {
        }
    }

    @Test
    public void testCompensateWithSystemException()
        throws Exception
    {
        try
        {
            _systemExceptionBusinessAgreementWithCoordinatorCompletionStub.compensate();
            fail("Expected exception \"SystemException\"");
        }
        catch (SystemException systemException)
        {
        }
    }

    @Test
    public void testCompensateWithFaultedException ()
        throws Exception
    {
        try
        {
            _faultedExceptionBusinessAgreementWithCoordinatorCompletionStub.compensate();
            fail("Expected exception \"FaultedException\"");
        }
        catch (FaultedException faultedException)
        {
        }
    }

    @After
    public void tearDown()
        throws Exception
    {
        _noExceptionBusinessAgreementWithCoordinatorCompletionStub                    = null;
        _wrongStateExceptionBusinessAgreementWithCoordinatorCompletionStub            = null;
        _systemExceptionBusinessAgreementWithCoordinatorCompletionStub                = null;
    }

    private BusinessAgreementWithCoordinatorCompletionParticipant _noExceptionBusinessAgreementWithCoordinatorCompletionStub                    = null;
    private BusinessAgreementWithCoordinatorCompletionParticipant _wrongStateExceptionBusinessAgreementWithCoordinatorCompletionStub            = null;
    private BusinessAgreementWithCoordinatorCompletionParticipant _systemExceptionBusinessAgreementWithCoordinatorCompletionStub                = null;
    private BusinessAgreementWithCoordinatorCompletionParticipant _faultedExceptionBusinessAgreementWithCoordinatorCompletionStub               = null;

}
