// BEGIN LICENSE BLOCK
// Version: CMPL 1.1
//
// The contents of this file are subject to the Cisco-style Mozilla Public
// License Version 1.1 (the "License"); you may not use this file except
// in compliance with the License.  You may obtain a copy of the License
// at www.eclipse-clp.org/license.
// 
// Software distributed under the License is distributed on an "AS IS"
// basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See
// the License for the specific language governing rights and limitations
// under the License. 
// 
// The Original Code is  The ECLiPSe Constraint Logic Programming System. 
// The Initial Developer of the Original Code is  Cisco Systems, Inc. 
// Portions created by the Initial Developer are
// Copyright (C) 2000 - 2006 Cisco Systems, Inc.  All Rights Reserved.
// 
// Contributor(s): Stefano Novello / Josh Singer, Parc Technologies
// 
// END LICENSE BLOCK

//Title:        Java/ECLiPSe interface
//Version:      $Id: EclipseTerminatedException.java,v 1.1.1.1 2006/09/23 01:54:09 snovello Exp $
//Author:       Josh Singer / Stefano Novello
//Company:      Parc Technologies
//Description:  Exception thrown when access to a terminated ECLiPSe is requested.
package com.parctechnologies.eclipse;
import java.io.*;

/**
 * Exception thrown when access to a terminated ECLiPSe is requested.
 * This IOException indicates that a method invocation which required
 * communication with
 * an ECLiPSe engine was not possible because the ECLiPSe (or the connection to
 * it) has been terminated.
 */
public class EclipseTerminatedException extends IOException
{
}
