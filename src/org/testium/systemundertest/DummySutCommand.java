/**
 * 
 */
package org.testium.systemundertest;

import java.io.File;
import java.util.ArrayList;

import org.testium.executor.runTimeData;
import org.testtoolinterfaces.utils.Trace;


/**
 * @author arjan.kranenburg
 *
 * Simple class for starting the System Under Test.
 */
public final class DummySutCommand implements SutIfCommand
{
	private String myAction;

	/**
	 * @param sutControl
	 */
	public DummySutCommand( String anAction )
	{
		myAction = anAction;
	}

	public String getName()
	{
		return myAction;
	}

	public boolean doAction(runTimeData aVariables, File aLogDir)
	{
		Trace.println( Trace.EXEC );
		// NOP
		return true;
	}

	public boolean verifyParameters(runTimeData aVariables)
	{
		return true;
	}

	public ArrayList<Parameter> getParameters()
	{
		return new ArrayList<Parameter>();
	}
}
