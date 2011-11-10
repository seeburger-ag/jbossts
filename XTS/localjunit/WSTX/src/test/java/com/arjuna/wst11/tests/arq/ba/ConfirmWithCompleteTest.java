package com.arjuna.wst11.tests.arq.ba;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arjuna.wst.tests.common.DemoBusinessParticipant;
import com.arjuna.wst.tests.common.DemoBusinessParticipantWithComplete;
import com.arjuna.wst11.tests.arq.WarDeployment;

@RunWith(Arquillian.class)
public class ConfirmWithCompleteTest {
	@Inject
	ConfirmWithComplete test;
	
	@Deployment
	public static WebArchive createDeployment() {
		return WarDeployment.getDeployment(
				ConfirmWithComplete.class,
				DemoBusinessParticipant.class,
				DemoBusinessParticipantWithComplete.class);
	}
	

	@Test
	public void test() throws Exception {
		test.testConfirmWithComplete();
	}
}