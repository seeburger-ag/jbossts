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
package com.arjuna.xts.nightout.clients.weblogic.proxies;

/**
 * Generated class, do not edit.
 *
 * This service class was generated by weblogic
 * webservice stub gen on Wed Jul 21 12:42:39 BST 2004 */

public class TheatreServiceAT_Impl 
     extends weblogic.webservice.core.rpc.ServiceImpl 
     implements com.arjuna.xts.nightout.clients.weblogic.proxies.TheatreServiceAT{

  public TheatreServiceAT_Impl() 
       throws java.io.IOException{

    this( "com/arjuna/xts/nightout/clients/weblogic/proxies/TheatreServiceAT.wsdl" );
  }

  public TheatreServiceAT_Impl( String wsdlurl ) 
       throws java.io.IOException{

    super( wsdlurl, "com.arjuna.xts.nightout.clients.weblogic.proxies.TheatreServiceAT" );
  }
    //***********************************
  // Port Name: TheatreServiceATPort 
  // Port Type: TheatreServiceATPort  //***********************************

  com.arjuna.xts.nightout.clients.weblogic.proxies.TheatreServiceATPort mvar_TheatreServiceATPort;


  /**
   * returns a port in this service 
   *
   */

  public com.arjuna.xts.nightout.clients.weblogic.proxies.TheatreServiceATPort getTheatreServiceATPort(){

    if( mvar_TheatreServiceATPort == null ){
      mvar_TheatreServiceATPort = 
        new com.arjuna.xts.nightout.clients.weblogic.proxies.TheatreServiceATPort_Stub( _getPort( "TheatreServiceATPort" ) );
    }

    return mvar_TheatreServiceATPort;

  }

  public com.arjuna.xts.nightout.clients.weblogic.proxies.TheatreServiceATPort getTheatreServiceATPort(String username, String password){

    _setUser( username, password, getTheatreServiceATPort() );

    return getTheatreServiceATPort();
  }
}