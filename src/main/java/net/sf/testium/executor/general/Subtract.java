/**
 * 
 */
package net.sf.testium.executor.general;

import java.util.ArrayList;

import net.sf.testium.executor.DefaultInterface;

import org.testtoolinterfaces.testresult.TestStepCommandResult;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.utils.RunTimeData;
import org.testtoolinterfaces.utils.RunTimeVariable;

/**
 * 
 * @author Arjan Kranenburg
 *
 */
public class Subtract extends GenericCommandExecutor {
	private static final String COMMAND = "subtract";

	private static final String PAR_INT1 = "int1";
	private static final String PAR_INT2 = "int2";
	private static final String PAR_RESULT = "result";

	public static final SpecifiedParameter PARSPEC_INT1 = new SpecifiedParameter( 
			PAR_INT1, Integer.class, "Minuend", false, true, true, false );
	public static final SpecifiedParameter PARSPEC_INT2 = new SpecifiedParameter( 
			PAR_INT2, Integer.class, "Subtrahend", false, true, true, false );

	private static final SpecifiedParameter PARSPEC_RESULT = new SpecifiedParameter( 
			PAR_RESULT, String.class, "Name of the variable to store the difference",
			false, true, false, false );

	public Subtract( DefaultInterface defInterface )
	{
		super( COMMAND, "Subtracts two integer values", defInterface, new ArrayList<SpecifiedParameter>() );

		this.addParamSpec( PARSPEC_INT1 );
		this.addParamSpec( PARSPEC_INT2 );
		this.addParamSpec( PARSPEC_RESULT );
	}

	@Override
	protected void doExecute(RunTimeData aVariables,
			ParameterArrayList parameters, TestStepCommandResult result)
			throws Exception
	{
		Integer int1 = (Integer) obtainValue( aVariables, parameters, PARSPEC_INT1 );
		Integer int2 = (Integer) obtainValue( aVariables, parameters, PARSPEC_INT2 );

		String varName = (String) obtainValue(aVariables, parameters, PARSPEC_RESULT);
		int sum = int1 - int2;
		result.setDisplayName( this.toString() + " " +  varName + " = " + int1 + " - " + int2 );
		
		RunTimeVariable rtVariable = new RunTimeVariable( varName, sum );
		aVariables.add(rtVariable);
	}
}
