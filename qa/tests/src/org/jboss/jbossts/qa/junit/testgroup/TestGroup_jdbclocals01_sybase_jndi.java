/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
 * (C) 2009,
 * @author JBoss Inc.
 */
package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

// Automatically generated by XML2JUnit
public class TestGroup_jdbclocals01_sybase_jndi extends TestGroupBase
{
	public String getTestGroupName()
	{
		return "jdbclocals01_sybase_jndi";
	}

	protected Task server0 = null;

	@Before public void setUp()
	{
		super.setUp();
		Task task0 = createTask("task0", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		task0.perform("DB_SYBASE_JNDI");
		Task setup = createTask("setup", org.jboss.jbossts.qa.JDBCLocals01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup.perform("DB_SYBASE_JNDI");
		server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
	}

	@After public void tearDown()
	{
		try {
			server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCLocals01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		cleanup.perform("DB_SYBASE_JNDI");
		} finally {
			super.tearDown();
		}
	}

	@Test public void JDBCLocals01_sybase_JNDI_Test001()
	{
		setTestName("Test001");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCLocals01.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("DB_SYBASE_JNDI");
		client0.waitFor();
	}

	@Test public void JDBCLocals01_sybase_JNDI_Test002()
	{
		setTestName("Test002");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCLocals01.Client02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("DB_SYBASE_JNDI");
		client0.waitFor();
	}

	@Test public void JDBCLocals01_sybase_JNDI_Test003()
	{
		setTestName("Test003");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCLocals01.Client03.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("DB_SYBASE_JNDI");
		client0.waitFor();
	}

	@Test public void JDBCLocals01_sybase_JNDI_Test004()
	{
		setTestName("Test004");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCLocals01.Client04.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("DB_SYBASE_JNDI");
		client0.waitFor();
	}

	@Test public void JDBCLocals01_sybase_JNDI_Test005()
	{
		setTestName("Test005");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCLocals01.Client05.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("DB_SYBASE_JNDI");
		client0.waitFor();
	}

	@Test public void JDBCLocals01_sybase_JNDI_Test006()
	{
		setTestName("Test006");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCLocals01.Client06.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("DB_SYBASE_JNDI");
		client0.waitFor();
	}

}