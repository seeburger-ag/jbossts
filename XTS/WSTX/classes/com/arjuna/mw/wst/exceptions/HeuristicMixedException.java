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
/*
 * Copyright (C) 2003,
 *
 * Arjuna Technologies Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: HeuristicMixedException.java,v 1.1 2003/02/03 11:01:26 nmcl Exp $
 */

package com.arjuna.mw.wst.exceptions;

/**
 * Thrown if some of the participants commit while others rollback.
 * Can only happen for a participant if it has received a
 * previous prepare message.
 *
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: HeuristicMixedException.java,v 1.1 2003/02/03 11:01:26 nmcl Exp $
 * @since 1.0.
 */

public class HeuristicMixedException extends Exception
{

    public HeuristicMixedException ()
    {
	super();
    }

    public HeuristicMixedException (String s)
    {
	super(s);
    }

}


