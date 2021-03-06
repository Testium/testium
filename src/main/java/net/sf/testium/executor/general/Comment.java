/**
 * 
 */
package net.sf.testium.executor.general;

import java.util.ArrayList;

import net.sf.testium.systemundertest.SutInterface;

import org.testtoolinterfaces.testresult.TestStepCommandResult;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.utils.RunTimeData;

/**
 * Prints a comment to the screen and the log
 * 
 * @author Arjan Kranenburg
 *
 */
public class Comment extends GenericCommandExecutor {
	private static final String COMMAND = "comment";

	private static final String PAR_COMMENT	 = "comment";
	private static final String PAR_TOSCREEN = "toScreen";

	private static final SpecifiedParameter PARSPEC_COMMENT = new SpecifiedParameter( 
			PAR_COMMENT, String.class, "The comment to make",
			false, true, true, false );
	public static final SpecifiedParameter PARSPEC_TOSCREEN = new SpecifiedParameter( 
			PAR_TOSCREEN, Boolean.class, "Flag to indicate if comment must be printed to stdout",
			true, true, false, false ).setDefaultValue(Boolean.TRUE);
	

	public Comment( SutInterface anInterface ) {
		super( COMMAND, "Prints a comment to the logfile and optionally to the screen",
				anInterface, new ArrayList<SpecifiedParameter>() );

		this.addParamSpec( PARSPEC_COMMENT );
		this.addParamSpec( PARSPEC_TOSCREEN );
	}

	@Override
	protected void doExecute(RunTimeData aVariables,
			ParameterArrayList parameters, TestStepCommandResult result)
			throws Exception {

		String comment = (String) this.obtainValue(aVariables, parameters, PARSPEC_COMMENT);
		Boolean toScreen = (Boolean) this.obtainOptionalValue(aVariables, parameters, PARSPEC_TOSCREEN);

		result.addComment(comment);
		if ( toScreen )
		{
			System.out.println( comment );
		}
//		result.setDisplayName( this.toString() + "\"" + comment + "\"" );
	}
}
