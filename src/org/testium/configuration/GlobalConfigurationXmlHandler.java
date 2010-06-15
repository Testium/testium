package org.testium.configuration;

import java.io.File;
import java.util.ArrayList;

import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.testtoolinterfaces.utils.RunTimeData;
import org.testtoolinterfaces.utils.RunTimeVariable;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * @author Arjan Kranenburg 
 * 
 *  <GlobalConfiguration>
 *    <TestResultOutputBaseDirectory>...</TestResultOutputBaseDirectory>
 *    <DefaultUserConfigurationFile>...</DefaultUserConfigurationFile>
 *    <TraceBaseClass>...</TraceBaseClass>
 *    <PluginLoaders>...</PluginLoaders>
 *    <PluginsDirectory>...</PluginsDirectory>
 *    <TestEnvironment>...</TestEnvironment>
 *    <TestPhase>...</TestPhase>
 *  ...
 *  </GlobalConfiguration>
 * 
 */
public class GlobalConfigurationXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "GlobalConfiguration";

	public static final String CFG_TEST_RESULT_OUTPUT_BASE_DIRECTORY = "TestResultOutputBaseDirectory";
	public static final String CFG_PLUGIN_LOADERS = "PluginLoaders";
	public static final String CFG_PLUGINS_DIRECTORY = "PluginsDirectory";
	public static final String CFG_TESTENVIRONMENT = "TestEnvironment";
	public static final String CFG_TESTPHASE = "TestPhase";
	public static final String CFG_SETTINGS_FILE = "DefaultUserConfigurationFile";

	private static final String CFG_TRACE_BASECLASS = "TraceBaseClass";
	private static final String CFG_TRACE_CLASS = "TraceClass";
	private static final String CFG_TRACE_LEVEL = "TraceLevel";
	private static final String CFG_TRACE_DEPTH = "TraceDepth";

	private RunTimeData myRunTimeData;
	
	public GlobalConfigurationXmlHandler( XMLReader anXmlReader, RunTimeData aRtData )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
		myRunTimeData = aRtData;

	    ArrayList<XmlHandler> xmlHandlers = new ArrayList<XmlHandler>();
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_TEST_RESULT_OUTPUT_BASE_DIRECTORY));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_PLUGIN_LOADERS));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_PLUGINS_DIRECTORY));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_TESTENVIRONMENT));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_TESTPHASE));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_SETTINGS_FILE));

	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_TRACE_BASECLASS));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_TRACE_CLASS));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_TRACE_LEVEL));
	    xmlHandlers.add(new GenericTagAndStringXmlHandler(anXmlReader, CFG_TRACE_DEPTH));

	    for (XmlHandler handler : xmlHandlers)
	    {
			this.addStartElementHandler(handler.getStartElement(), handler);
			handler.addEndElementHandler(handler.getStartElement(), this);
	    }
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
   		//nop;
    }

	@Override
	public void handleCharacters(String aValue)
	{
		//nop
    }
    
	@Override
	public void handleEndElement(String aQualifiedName)
	{
		//nop
    }

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		//nop
    }

	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		//nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.UTIL, "handleReturnFromChildElement( " 
	            + aQualifiedName + " )", true );
		RunTimeVariable rtVar = null;
		if (aQualifiedName.equalsIgnoreCase(CFG_TEST_RESULT_OUTPUT_BASE_DIRECTORY))
    	{
			String resultBaseDirName = aChildXmlHandler.getValue();
			File resultBaseDir = new File( resultBaseDirName );
			rtVar = new RunTimeVariable(KEYS.RESULT_BASE_DIR.toString(), resultBaseDir);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_PLUGIN_LOADERS))
    	{
			String pluginLoaderStr = aChildXmlHandler.getValue();
			ArrayList<String> pluginLoaders = convertStringToPluginLoaders(pluginLoaderStr);
			rtVar = new RunTimeVariable(KEYS.PLUGIN_LOADERS.toString(), pluginLoaders);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_PLUGINS_DIRECTORY))
    	{
			if ( myRunTimeData.containsKey(KEYS.PLUGINSDIRECTORY.toString()) )
			{
				String pluginsDirName = aChildXmlHandler.getValue();
				File pluginsDirectory = new File( pluginsDirName );
				if ( ! pluginsDirectory.isAbsolute() )
				{
					File baseDir = (File) myRunTimeData.getValue( KEYS.BASE_DIR.toString() );
					pluginsDirectory = new File( baseDir, pluginsDirectory.getPath() );
				}

				rtVar = new RunTimeVariable(KEYS.PLUGINSDIRECTORY.toString(), pluginsDirectory);
			}
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_TESTENVIRONMENT))
    	{
			String testEnvironment = aChildXmlHandler.getValue();
			rtVar = new RunTimeVariable(KEYS.TEST_ENVIRONMENT.toString(), testEnvironment);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_TESTPHASE))
    	{
			String testPhase = aChildXmlHandler.getValue();
			rtVar = new RunTimeVariable(KEYS.TEST_PHASE.toString(), testPhase);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_SETTINGS_FILE))
    	{
			String configFileName = aChildXmlHandler.getValue();
			File userHomeDir = (File) myRunTimeData.getValue(KEYS.USER_HOME.toString());
			File configFile = new File( userHomeDir, configFileName );
			rtVar = new RunTimeVariable(KEYS.CONFIGFILENAME.toString(), configFile);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_TRACE_BASECLASS))
    	{
			String traceBaseClass = aChildXmlHandler.getValue();
			Trace.getInstance().addBaseClass(traceBaseClass);
			String pkgBases = (String) myRunTimeData.getValue(KEYS.TRACE_PKG_BASES.toString());
			pkgBases += ";" + traceBaseClass;
			rtVar = new RunTimeVariable(KEYS.TRACE_PKG_BASES.toString(), pkgBases);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_TRACE_CLASS))
    	{
			String traceClass = aChildXmlHandler.getValue();
			Trace.getInstance().setTraceClass(traceClass);
			rtVar = new RunTimeVariable(KEYS.TRACE_CLASS.toString(), traceClass);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_TRACE_LEVEL))
    	{
			Trace.LEVEL traceLevel = Trace.LEVEL.valueOf( aChildXmlHandler.getValue() );
			Trace.getInstance().setTraceLevel(traceLevel);
			rtVar = new RunTimeVariable(KEYS.TRACE_LEVEL.toString(), traceLevel);
    	}
		else if (aQualifiedName.equalsIgnoreCase(CFG_TRACE_DEPTH))
    	{
			int traceDepth = (new Integer(aChildXmlHandler.getValue())).intValue();
			Trace.getInstance().setDepth( traceDepth );
			rtVar = new RunTimeVariable(KEYS.TRACE_DEPTH.toString(), traceDepth);
    	}

		if (rtVar != null)
		{
			myRunTimeData.add( rtVar );
		}

		aChildXmlHandler.reset();
	}
	
	/**
	 * @return the PluginLoaders
	 */
	public ArrayList<String> convertStringToPluginLoaders( String aPluginLoadersString )
	{
		ArrayList<String> pluginLoaders = new ArrayList<String>();
    	if ( ! aPluginLoadersString.isEmpty() )
    	{
        	String[] classNames = aPluginLoadersString.trim().split(";");
        	if ( classNames.length != 0 )
        	{
            	for ( String className : classNames )
            	{
            		className = className.replace('\t', ' ').trim();
            		pluginLoaders.add( className );
            	}
        	}
    	}

		return pluginLoaders;
	}
}
