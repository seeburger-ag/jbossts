package org.jboss.jbossts.xts.wsaddr.map;

import javax.xml.namespace.QName;

/**
 * MAPRelationship is a wrapper class which works with class MAP. This is the JBossWS CXF implementation.
 */
public class MAPRelatesTo
{
    MAPRelatesTo(String relatesTo, QName type)
    {
        this.relatesTo = relatesTo;
        this.type = type;
    }

    public String getRelatesTo()
    {
        return relatesTo;
    }

    public QName getType()
    {
        return type;
    }

    public void setType(QName type)
    {
        this.type = type;
    }

    private String relatesTo;
    private QName type;
}