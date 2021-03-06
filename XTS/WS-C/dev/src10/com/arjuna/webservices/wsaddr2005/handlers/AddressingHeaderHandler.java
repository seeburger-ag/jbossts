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
package com.arjuna.webservices.wsaddr2005.handlers;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.arjuna.webservices.HeaderHandler;
import com.arjuna.webservices.MessageContext;
import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.soap.SoapDetails;

/**
 * Base header handler for WS-Addressing.
 * @author kevin
 */
abstract class AddressingHeaderHandler implements HeaderHandler
{
    /**
     * Write the header element in a response.
     * @param out The output stream.
     * @param headerElementName The name of the header element.
     * @param context The current message context.
     * @param soapDetails The SOAP details.
     * @throws XMLStreamException 
     */
    public void writeContent(final XMLStreamWriter out, final QName headerElementName,
        final MessageContext context, final SoapDetails soapDetails)
        throws XMLStreamException
    {
        // handled by AddressingContextHandler
    }
    
    /**
     * Validate headers after processing.
     * @param context The current message context.
     * @throws SoapFault for validation errors.
     */
    public void headerValidate(final MessageContext context)
        throws SoapFault
    {
        // Handled by AddressingInterceptorHandler
    }
    
    /**
     * Notification of a subsequent header processing fault.
     * @param context The current message context.
     */
    public void headerFaultNotification(final MessageContext context)
    {
    }
}
