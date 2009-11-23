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
package com.arjuna.webservices.wscoor.policy;

import com.arjuna.webservices.HandlerRegistry;
import com.arjuna.webservices.base.handlers.LoggingFaultHandler;
import com.arjuna.webservices.wscoor.CoordinationConstants;
import com.arjuna.webservices.wscoor.handlers.CreateCoordinationContextHandler;

/**
 * Policy responsible for binding in the WS-Coordination server handlers.
 * @author kevin
 */
public class ActivationCoordinatorPolicy
{
    /**
     * Add this policy to the registry.
     * @param registry The registry containing the policy.
     */
    public static void register(final HandlerRegistry registry)
    {
        registry.registerBodyHandler(CoordinationConstants.WSCOOR_ELEMENT_CREATE_COORDINATION_CONTEXT_QNAME, new CreateCoordinationContextHandler()) ;
        registry.registerFaultHandler(new LoggingFaultHandler(CoordinationConstants.SERVICE_ACTIVATION_COORDINATOR)) ;
    }

    /**
     * Remove this policy from the registry.
     * @param registry The registry containing the policy.
     */
    public static void remove(final HandlerRegistry registry)
    {
        registry.registerFaultHandler(null) ;
        registry.removeBodyHandler(CoordinationConstants.WSCOOR_ELEMENT_CREATE_COORDINATION_CONTEXT_QNAME) ;
    }
}