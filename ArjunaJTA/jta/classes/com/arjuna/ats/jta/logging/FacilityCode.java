/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors 
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors. 
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
 * Copyright (C) 2001, 2002,
 *
 * Hewlett-Packard Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: FacilityCode.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.jta.logging;

public class FacilityCode extends com.arjuna.common.util.logging.FacilityCode
{
    public static final long FAC_JTA = 0x00000001;

    public long getLevel (String level)
    {
	if (level.equals("FAC_JTA"))
	    return FAC_JTA;

	return FacilityCode.FAC_NONE;
    }

        /**
         * @return the string representation of the facility level. Note, this
         * string is intended only for debugging purposes, and cannot be fed
         * back into the debug system to obtain the facility level that it
         * represents.
         *
         * @since JTS 2.1.2.
         */

    public String printString (long level)
    {
	if (level == FacilityCode.FAC_ALL)
	    return "FAC_ALL";

	if (level == FacilityCode.FAC_NONE)
	    return "FAC_NONE";

	String sLevel = null;

	if ((level & FAC_JTA) != 0)
	    sLevel = ((sLevel == null) ? "FAC_JTA" : " & FAC_JTA");
	
	return ((sLevel == null) ? "FAC_NONE" : sLevel);
    }
}