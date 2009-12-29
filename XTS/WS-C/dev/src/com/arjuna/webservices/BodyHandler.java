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
package com.arjuna.webservices;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.arjuna.webservices.soap.SoapDetails;

/**
 * Interface for body handlers.
 * @author kevin
 */
public interface BodyHandler
{
    /**
     * Handle the body element.
     * @param soapDetails The SOAP details.
     * @param context The current message context.
     * @param responseContext The response message context.
     * @param action The transport SOAP action.
     * @param in The current stream reader.
     * @throws XMLStreamException for parsing errors.
     * @throws SoapFault for processing errors.
     * @return The response elements or null if one way.
     */
    public SoapBody invoke(final SoapDetails soapDetails,
        final MessageContext context, final MessageContext responseContext,
        final String action, final XMLStreamReader in)
        throws XMLStreamException, SoapFault ;
}