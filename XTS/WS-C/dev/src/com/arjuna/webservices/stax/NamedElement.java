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
package com.arjuna.webservices.stax;

import javax.xml.namespace.QName;

/**
 * Utility class to wrap an element and its name.
 * @author kevin
 */
public class NamedElement
{
    /**
     * The element name of the any element.
     */
    private final QName name ;
    /**
     * The any element content.
     */
    private final ElementContent elementContent ;
    
    /**
     * Construct the any element utility class.
     * @param name The name of the element.
     * @param elementContent The contents of the element.
     */
    public NamedElement(final QName name, final ElementContent elementContent)
    {
        this.name = name ;
        this.elementContent = elementContent ;
    }
    
    /**
     * Get the element name.
     * @return The element name.
     */
    public QName getName()
    {
        return name ;
    }
    
    /**
     * Get the element content.
     * @return The element content.
     */
    public ElementContent getElementContent()
    {
        return elementContent ;
    }
}