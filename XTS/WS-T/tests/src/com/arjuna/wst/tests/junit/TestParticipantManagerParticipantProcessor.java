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
package com.arjuna.wst.tests.junit;

import java.util.HashMap;
import java.util.Map;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.wsaddr.AddressingContext;
import com.arjuna.webservices.wsarj.ArjunaContext;
import com.arjuna.webservices.wsarjtx.processors.ParticipantManagerParticipantProcessor;
import com.arjuna.webservices.wsat.NotificationType;
import com.arjuna.wst.BAParticipantManager;

public class TestParticipantManagerParticipantProcessor extends
        ParticipantManagerParticipantProcessor
{
    private Map messageIdMap = new HashMap() ;
    
    public ParticipantManagerParticipantDetails getParticipantManagerParticipantDetails(final String messageId, final long timeout)
    {
        final long endTime = System.currentTimeMillis() + timeout ;
        synchronized(messageIdMap)
        {
            long now = System.currentTimeMillis() ;
            while(now < endTime)
            {
                final ParticipantManagerParticipantDetails details = (ParticipantManagerParticipantDetails)messageIdMap.remove(messageId) ;
                if (details != null)
                {
                    return details ;
                }
                try
                {
                    messageIdMap.wait(endTime - now) ;
                }
                catch (final InterruptedException ie) {} // ignore
                now = System.currentTimeMillis() ;
            }
            final ParticipantManagerParticipantDetails details = (ParticipantManagerParticipantDetails)messageIdMap.remove(messageId) ;
            if (details != null)
            {
                return details ;
            }
        }
        throw new NullPointerException("Timeout occurred waiting for id: " + messageId) ;
    }

    public void completed(NotificationType completed,
            AddressingContext addressingContext, ArjunaContext arjunaContext)
    {
        final String messageId = addressingContext.getMessageID().getValue() ;
        final ParticipantManagerParticipantDetails details = new ParticipantManagerParticipantDetails(addressingContext, arjunaContext) ;
        details.setCompleted(true) ;
        
        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public void exit(NotificationType exit,
            AddressingContext addressingContext, ArjunaContext arjunaContext)
    {
        final String messageId = addressingContext.getMessageID().getValue() ;
        final ParticipantManagerParticipantDetails details = new ParticipantManagerParticipantDetails(addressingContext, arjunaContext) ;
        details.setExit(true) ;
        
        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public void fault(NotificationType fault,
            AddressingContext addressingContext, ArjunaContext arjunaContext)
    {
        final String messageId = addressingContext.getMessageID().getValue() ;
        final ParticipantManagerParticipantDetails details = new ParticipantManagerParticipantDetails(addressingContext, arjunaContext) ;
        details.setFault(true) ;
        
        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public void soapFault(SoapFault soapFault, AddressingContext addressingContext,
            ArjunaContext arjunaContext)
    {
        final String messageId = addressingContext.getMessageID().getValue() ;
        final ParticipantManagerParticipantDetails details = new ParticipantManagerParticipantDetails(addressingContext, arjunaContext) ;
        details.setSoapFault(soapFault) ;
        
        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }
    
    public void activateParticipant(BAParticipantManager participant,
            String identifier)
    {
    }

    public void deactivateParticipant(BAParticipantManager participant)
    {
    }
    
    public static class ParticipantManagerParticipantDetails
    {
        private final AddressingContext addressingContext ;
        private final ArjunaContext arjunaContext ;
        private boolean completed ;
        private boolean exit ;
        private boolean fault ;
        private SoapFault soapFault ;
        
        ParticipantManagerParticipantDetails(final AddressingContext addressingContext, final ArjunaContext arjunaContext)
        {
            this.addressingContext = addressingContext ;
            this.arjunaContext = arjunaContext ;
        }
        
        public AddressingContext getAddressingContext()
        {
            return addressingContext ;
        }
        
        public ArjunaContext getArjunaContext()
        {
            return arjunaContext ;
        }
        
        public boolean hasCompleted()
        {
            return completed ;
        }
        
        void setCompleted(final boolean completed)
        {
            this.completed = completed ;
        }
        
        public boolean hasExit()
        {
            return exit ;
        }
        
        void setExit(final boolean exit)
        {
            this.exit = exit ;
        }
        
        public boolean hasFault()
        {
            return fault ;
        }
        
        void setFault(final boolean fault)
        {
            this.fault = fault ;
        }
        
        public SoapFault getSoapFault()
        {
            return soapFault ;
        }
        
        void setSoapFault(final SoapFault soapFault)
        {
            this.soapFault = soapFault ;
        }
    }
}