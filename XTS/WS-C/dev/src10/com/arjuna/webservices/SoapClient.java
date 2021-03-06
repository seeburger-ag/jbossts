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
package com.arjuna.webservices;

import java.io.IOException;


/**
 * Interface for soap client processors.
 * @author kevin
 */
public interface SoapClient
{
    /**
     * Invoke a request on a service.
     * @param request The request object.
     * @param url The destination URL.
     * @return The response object.
     * @throws SoapFault For errors processing the request.
     * @throws IOException for processing errors.
     */
    public SoapMessage invoke(final SoapMessage request, final String url)
        throws SoapFault, IOException ;
    
    /**
     * Invoke a one way request on a service.
     * @param request The request object.
     * @param url The destination URL.
     * @throws SoapFault For errors processing the request.
     * @throws IOException for processing errors.
     */
    public void invokeOneWay(final SoapMessage request, final String url)
        throws SoapFault, IOException ;
}
