package com.arjuna.webservices11.wsaddr;

import com.arjuna.wsc11.messaging.MessageId;

import javax.xml.ws.addressing.*;
import java.net.URISyntaxException;

/**
 * The complete addressing context.
 * @author kevin
 */
public class AddressingHelper
{
    protected AddressingHelper()
    {
    }

    public static AddressingProperties createOneWayResponseContext(final AddressingProperties addressingProperties, final String messageID)
    {
        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        final AddressingProperties responseProperties = builder.newAddressingProperties();
        responseProperties.initializeAsReply(addressingProperties, false) ;
        responseProperties.setMessageID(makeURI(builder, messageID)) ;

        return responseProperties ;
    }

    /**
     * Create an addressing context that represents an inline reply to the specified addressing context.
     * @param addressingProperties The addressing context being replied to.
     * @param messageID The message id of the new message.
     * @return The reply addressing context.
     */
    public static AddressingProperties createResponseContext(final AddressingProperties addressingProperties, final String messageID)
    {
        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        final AddressingProperties responseProperties = builder.newAddressingProperties();
        responseProperties.initializeAsReply(addressingProperties, false) ;
        responseProperties.setMessageID(makeURI(builder, messageID)) ;

        return responseProperties ;
    }

    /**
     * Create an addressing context that represents a fault to the specified addressing context.
     * @param addressingProperties The addressing context being replied to.
     * @param messageID The message id of the new message.
     * @return The fault addressing context.
     *
     * N.B. Still need to do From, Action, ReplyTo, FaultTo if needed.
     */
    public static AddressingProperties createFaultContext(final AddressingProperties addressingProperties, final String messageID)
    {
        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        final AddressingProperties faultProperties = builder.newAddressingProperties();
        faultProperties.initializeAsReply(addressingProperties, true) ;
        faultProperties.setMessageID(makeURI(builder, messageID)) ;

        return faultProperties ;
    }

    /**
     * Create an addressing context that represents a request to the specified address.
     * @param address TheendpointReference target address.
     * @param messageID The message id of the new message.
     * @return The addressing context.
     *
     * N.B. Still need to do From, Action, ReplyTo, FaultTo if needed.
     */
    public static AddressingProperties createRequestContext(final String address, final String messageID)
    {
        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        final AddressingProperties requestProperties = builder.newAddressingProperties();
        requestProperties.setTo(makeURI(builder, address));
        if (messageID != null) {
            requestProperties.setMessageID(makeURI(builder, messageID));
        } else {
            // client does not care about id but we have to set some id or WSA will complain

            final String dummyID = MessageId.getMessageId();

            requestProperties.setMessageID(makeURI(builder, dummyID));
        }
        return requestProperties;
    }

    /**
     * Create an addressing context that represents a request to the specified address.
     * @param address TheendpointReference target address.
     * @param messageID The message id of the new message.
     * @param action The action of the new message.
     * @return The addressing context.
     *
     * N.B. Still need to do From, Action, ReplyTo, FaultTo if needed.
     */
    public static AddressingProperties createRequestContext(final String address, final String action, final String messageID)
    {
        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        final AddressingProperties requestProperties = builder.newAddressingProperties();
        requestProperties.setTo(makeURI(builder, address));
        if (messageID != null) {
            requestProperties.setMessageID(makeURI(builder, messageID));
        } else {
            // client does not care about id but we have to set some id or WSA will complain

            final String dummyID = MessageId.getMessageId();

            requestProperties.setMessageID(makeURI(builder, dummyID));
        }
        requestProperties.setAction(makeURI(builder, action));

        return requestProperties;
    }

    /**
     * Create an addressing context that represents a notification to the specified context.
     * @param addressingProperties The addressing properties used to derive the notification addressing context.
     * @param messageID The message id of the new message.
     * @return The notification addressing context.
     *
     * N.B. Still need to do From, Action, ReplyTo, FaultTo if needed.
     */
    public static AddressingProperties createRequestContext(final AddressingProperties addressingProperties, final String messageID)
    {
        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        final AddressingProperties requestProperties = builder.newAddressingProperties();
        requestProperties.initializeAsReply(addressingProperties, false) ;

        if (messageID != null)
        {
            requestProperties.setMessageID (makeURI(builder, messageID));
        }

        return requestProperties;
    }

    /**
     * Create an addressing context specifying only the message id for a notification.
     * @param messageID The message id of the new message.
     * @return The notification addressing context.
     */
    public static AddressingProperties createNotificationContext(final String messageID)
    {
        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        final AddressingProperties requestProperties = builder.newAddressingProperties();
        requestProperties.setMessageID (makeURI(builder, messageID));
        requestProperties.setAction(makeURI(builder, ""));

        return requestProperties;
    }


    public static void installActionMessageID(AddressingProperties addressingProperties, final String action, final String messageID)
    {
        // requestProperties has been derived from an endpoint so To and ReferenceParameters will be set. we
        // need to install the action and messageID

        // create this each time so it uses the current thread classloader
        // this allows the builder class to be redefined via a property
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        addressingProperties.setMessageID(makeURI(builder, messageID));
        addressingProperties.setAction(makeURI(builder, action));
    }


    public static void installCallerProperties(AddressingProperties addressingProperties, AddressingProperties requestProperties)
    {
        // requestProperties has been derived from an endpoint so To and ReferenceParameters will be set. we
        // need to install alll the other fields supplied in addressingProperties

        AttributedURI uri;
        Relationship[] relatesTo;
        EndpointReference epr;
        uri = addressingProperties.getAction();
        if (uri != null) {
            requestProperties.setAction(uri);
        }
        uri = addressingProperties.getMessageID();
        if (uri != null) {
            requestProperties.setMessageID(uri);
        }
        epr = addressingProperties.getFrom();
        if (epr != null) {
            requestProperties.setFrom(epr);
        }
        epr = addressingProperties.getFaultTo();
        if (epr != null) {
            requestProperties.setFaultTo(epr);
        }
        epr = addressingProperties.getReplyTo();
        if (epr != null) {
            requestProperties.setReplyTo(epr);
        }
        relatesTo = addressingProperties.getRelatesTo();
        if (relatesTo != null) {
            requestProperties.setRelatesTo(relatesTo);
        }
    }
    
    public static javax.xml.ws.addressing.AttributedURI makeURI(AddressingBuilder builder, String messageID)
    {
        try {
            return builder.newURI(messageID);
        } catch (URISyntaxException use) {
            return null;
        }
    }
}
