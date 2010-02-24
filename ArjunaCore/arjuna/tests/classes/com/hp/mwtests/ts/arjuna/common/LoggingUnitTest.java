/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors
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
 * (C) 2007,
 * @author JBoss, a division of Red Hat.
 */
package com.hp.mwtests.ts.arjuna.common;

import com.arjuna.ats.arjuna.logging.FacilityCode;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.common.util.logging.DebugLevel;
import com.arjuna.common.util.logging.VisibilityLevel;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoggingUnitTest
{
    @Test
    public void testFacilityCode()
    {
        tsLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "hello world");
        tsLogger.arjLogger.debug(DebugLevel.ALL_NON_TRIVIAL, VisibilityLevel.VIS_PACKAGE, FacilityCode.FAC_ABSTRACT_REC, "another test");
        
        FacilityCode fc = new FacilityCode();
        
        assertEquals(fc.getLevel("FAC_CRASH_RECOVERY"), FacilityCode.FAC_CRASH_RECOVERY);
        assertEquals(fc.printString(FacilityCode.FAC_JDBC), "FAC_JDBC");
    }
}