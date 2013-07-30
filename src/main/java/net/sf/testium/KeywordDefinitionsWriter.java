package net.sf.testium;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.sf.testium.configuration.KeywordDefinitionsConfiguration;
import net.sf.testium.configuration.KeywordDefinitionsWriterXmlHandler;
import net.sf.testium.executor.SupportedInterfaceList;
import net.sf.testium.executor.TestStepCommandExecutor;
import net.sf.testium.executor.general.SpecifiedParameter;
import net.sf.testium.plugins.PluginCollection;
import net.sf.testium.systemundertest.SutInterface;

import org.apache.commons.io.FileUtils;
import org.testtoolinterfaces.testresultinterface.XmlWriterUtils;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.utils.RunTimeData;
import org.testtoolinterfaces.utils.TTIException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

public class KeywordDefinitionsWriter {

	/*
	 * TODO
	 * - Make a plugin (with what interface?)
	 * - Copy XSL, CSS, etc.
	 * - Add description to parameters
	 * - Add examples (1 minimal, 1 with all optional parameters)
	 */

	private KeywordDefinitionsConfiguration config;

	public KeywordDefinitionsWriter(KeywordDefinitionsConfiguration kdwConfig) {
		config = kdwConfig;
	}

	public void saveKeywordDefs(PluginCollection plugins) {
		File keywordsDir = config.getOutputBaseDir();
		if ( keywordsDir.isDirectory() ) {
			try {
				FileUtils.deleteDirectory(keywordsDir);
			} catch (IOException e) {
				System.out.println("Could not empty keywordsDir. Trying to overwrite...");
				Trace.print(Trace.EXEC, e);
			}
		}
		keywordsDir.mkdir();

		SupportedInterfaceList interfaceList = plugins.getInterfaces();
		KeywordDefinitionsWriter.writeInterfaceKWs(keywordsDir, interfaceList);

		// TODO copy XSL to each new created dir
		
		System.out.println( "See for the keyword definition files:" );
		System.out.println( keywordsDir.getAbsolutePath() );
	}
	
	public static KeywordDefinitionsConfiguration readGlobalInterfaceConfiguration(
			File kdwConfigFile, RunTimeData rtData ) {

		Trace.println(Trace.UTIL, "readInterfaceConfiguration( " + kdwConfigFile.getName() + " )", true );

		KeywordDefinitionsWriterXmlHandler handler = null;
		try {
			XMLReader reader = XmlHandler.getNewXmlReader();
			handler = new KeywordDefinitionsWriterXmlHandler( reader, rtData );
		
			handler.parse(reader, kdwConfigFile);
		} catch (TTIException e) {
System.out.println( "Warning: Configuration of KeywordDefinitionsWriter failed:" );
System.err.print( e );
System.out.println( "Continuing with default configuration" );
			File defOutputBaseDir = KeywordDefinitionsWriterXmlHandler.getDefaultOutputBasedir(rtData);
			return new KeywordDefinitionsConfiguration( defOutputBaseDir, null );
		}

		KeywordDefinitionsConfiguration kdwConfig = handler.getConfiguration( );
		handler.reset();

		return kdwConfig;
	}

	/**
	 * @param keywordsDir
	 * @param interfaceList
	 */
	private static void writeInterfaceKWs(File keywordsDir,
			SupportedInterfaceList interfaceList) {
		Iterator<TestInterface> iFaceItr = interfaceList.iterator();
		while(iFaceItr.hasNext())
		{
			TestInterface iFace = iFaceItr.next();
			if (iFace instanceof SutInterface) {
				File ifTargetDir = new File( keywordsDir, iFace.getInterfaceName() );
				if ( !ifTargetDir.isDirectory() )	{
					ifTargetDir.mkdir();
				}

				ArrayList<String> commandList = iFace.getCommands();
				Collections.sort(commandList);
			    for (String command : commandList)
			    {
					KeywordDefinitionsWriter.writeCommand(ifTargetDir, ((SutInterface) iFace).getCommandExecutor(command));
			    }
			}
		}
	}

	/**
	 * @param ifTargetDir
	 * @param testStepCommandExecutor
	 */
	private static void writeCommand(File ifTargetDir, TestStepCommandExecutor testStepCommandExecutor) {
		String command = testStepCommandExecutor.getCommand();
		File cmdFile = new File( ifTargetDir, command + ".xml" );
		FileWriter cmdFileWriter;
		try
		{
			cmdFileWriter = new FileWriter( cmdFile );

			XmlWriterUtils.printXmlDeclaration(cmdFileWriter, "Keywords.xsl");

//					this.printXml(aTestGroupResult, cmdFileWriter, "", logDir);
			cmdFileWriter.write("<TestStepDefinitions>\n");
			cmdFileWriter.write("  <TestStep command='" + command + "'>\n");
			cmdFileWriter.write("    <Description>");
			cmdFileWriter.write(testStepCommandExecutor.getDescription());
			cmdFileWriter.write("</Description>\n");
			printParamSpecs(cmdFileWriter, testStepCommandExecutor);
			cmdFileWriter.write("  </TestStep>\n");
			cmdFileWriter.write("</TestStepDefinitions>\n");
			cmdFileWriter.flush();
		}
		catch (IOException exception)
		{
			Warning.println("Saving Command File failed: " + exception.getMessage());
			Trace.print(Trace.UTIL, exception);
		}
	}

	private static void printParamSpecs(FileWriter cmdFileWriter,
			TestStepCommandExecutor testStepCommandExecutor) throws IOException {
		ArrayList<SpecifiedParameter> parameterSpecs = testStepCommandExecutor.getParameterSpecs();
		for ( SpecifiedParameter spec : parameterSpecs )
	    {
			cmdFileWriter.write("    <Parameterspec name='" + spec.getName() + "' type='" + spec.getType().getSimpleName() + "'>\n");
			cmdFileWriter.write("      <Optional>" + Boolean.toString(spec.isOptional()) + "</Optional>\n");
			cmdFileWriter.write("      <ValueAllowed>" + Boolean.toString(spec.isValue()) + "</ValueAllowed>\n");
			cmdFileWriter.write("      <VariableAllowed>" + Boolean.toString(spec.isVariable()) + "</VariableAllowed>\n");
			cmdFileWriter.write("      <EmptyAllowed>" + Boolean.toString(spec.isEmpty()) + "</EmptyAllowed>\n");
			if ( spec.isOptional() ) {
				Object defValue = spec.getDefaultValue();
				if (defValue != null) {
					cmdFileWriter.write("      <Default>" + defValue.toString() + "</Default>\n");
				}
			}
			cmdFileWriter.write("    </Parameterspec>\n");
	    }
	}
}
