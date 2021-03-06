package com.arjuna.wscf11.tests.model.sagas;

import com.arjuna.mw.wscf.model.sagas.api.CoordinatorManager;
import com.arjuna.mw.wscf.model.sagas.exceptions.CoordinatorCancelledException;
import com.arjuna.mw.wscf11.model.sagas.CoordinatorManagerFactory;
import com.arjuna.wscf11.tests.SagasParticipant;
import com.arjuna.wscf11.tests.WSCF11TestUtils;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 */

public class ParticipantFaultedClose
{
    @Test
    public void testParticipantFaultedClose()
            throws Exception
    {
        System.out.println("Running test : " + this.getClass().getName());

        CoordinatorManager cm = CoordinatorManagerFactory.coordinatorManager();

	try
	{
        String id = "1236";

	    cm.begin("Sagas11HLS");

        SagasParticipant participant = new SagasParticipant(id);

	    cm.enlistParticipant(participant);

	    cm.participantFaulted(id);

	    System.out.println("Started: "+cm.identifier()+"\n");

	    cm.close();

        fail("Close succeeded after participantFaulted");
	}
    catch (CoordinatorCancelledException ex)
    {
        WSCF11TestUtils.cleanup(cm);
    }
    catch (Exception ex)
    {
        WSCF11TestUtils.cleanup(cm);
        throw ex;
    }
    }
}