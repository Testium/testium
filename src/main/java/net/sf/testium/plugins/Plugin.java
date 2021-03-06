package net.sf.testium.plugins;

import net.sf.testium.configuration.ConfigurationException;

import org.testtoolinterfaces.utils.RunTimeData;

/*
 * Interface for plugin classes
 */
public interface Plugin
{
    /**
     * @param aPluginCollection
     * @param aRtData
     * @throws ConfigurationException
     */
    public void loadPlugIn( PluginCollection aPluginCollection, RunTimeData aRtData ) throws ConfigurationException;
}