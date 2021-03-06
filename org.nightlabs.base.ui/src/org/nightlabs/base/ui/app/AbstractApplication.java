/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.ui.app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.context.DefaultUIContextRunner;
import org.nightlabs.base.ui.context.UIContext;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.exceptionhandler.SaveRunnableRunner;
import org.nightlabs.base.ui.exceptionhandler.SimpleExceptionHandlerRegistry;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.eclipse.extension.RemoveExtensionRegistry;
import org.nightlabs.singleton.SingletonProviderFactory;
import org.nightlabs.util.IOUtil;

/**
 * This is the basis for RCP applications based on the nightlabs base plugin.
 * <p>
 * In order to use this framework you have to do several things.<br>
 * First you'll have to extend this class and register your implementation
 * to the <code>org.eclipse.core.runtime.applications</code> extension-point.
 * Doing so will tell Eclipse to run your application.
 * <p>
 * When implementing your application you will see, that you have to write two
 * methods. One is to provide a name for your application {@link #initApplicationName()},
 * and the other to return an implementation of {@link AbstractWorkbenchAdvisor} in
 * {@link #initWorkbenchAdvisor(Display)}
 * </p>
 * <p>
 * That's basicly all you have to do. For customizations of your application you can use the
 * {@link WorkbenchAdvisor} you provide (You may use {@link AbstractWorkbenchAdvisor} as a basis here)
 * or the {@link WorkbenchWindowAdvisor} that is provided by the workbench advisor.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek - Daniel.Mazurek[AT]NightLabs[DOT]de
 */
public abstract class AbstractApplication
implements IApplication
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractApplication.class);

//	/**
//	 * The system properties hold the name of the application accessible via this key (it's set by {@link #setAppNameSystemProperty()}).
//	 * Use <code>System.getProperty(APPLICATION_SYSTEM_PROPERTY_NAME)</code> to get the application name.
//	 */
//	public static final String APPLICATION_SYSTEM_PROPERTY_NAME = "nightlabs.base.application.name"; //$NON-NLS-1$
//	/**
//	 * This is used to choose the application folder when the application starts.
//	 * After start the system property with this name will point to the applications root folder.
//	 * <p>
//	 * To initialize the application folder the system property can be set before the application
//	 * starts. It might contain references to system environment variables in the following way:
//	 * $ENV_NAME$, where ENV_NAME is the name of the environment variable.
//	 */
//	public static final String APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME = "nightlabs.base.application.folder"; //$NON-NLS-1$

	public static final String LOG_DIR_PROPERTY_KEY = "org.nightlabs.base.ui.log.dir"; //$NON-NLS-1$
//	public static final String LOG_FILE_NAME_PROPERTY_KEY = "org.nightlabs.base.ui.log.filename";
//	public static final String LOG_FILE_WITH_PATH_PROPERTY_KEY = "org.nightlabs.base.ui.log.file";


	/**
	 * Constructs a new Application and
	 * sets the static members {@link #sharedInstance} and {@link #applicationName}.
	 */
	public AbstractApplication()
	{
		super();
//		applicationName = initApplicationName();
	}

//	private static String applicationName = "AbstractApplication"; //$NON-NLS-1$
//	/**
//	 *
//	 * @return the application name set by {@link #initApplicationName()}
//	 */
//	public static String getApplicationName() {
//		return applicationName;
//	}

	private static String rootDir = ""; //$NON-NLS-1$

	private Display display;
	
	private int platformReturnCode = -1;

	/**
	 * returns the root directory, which is the .{applicationName} in the users home directory.
	 * @return the root directory, which is the applicationName with an leading dot in the users home directory.
	 */
	public static String getRootDir() {
		if (rootDir.equals("")) { //$NON-NLS-1$
			String osgiInstanceArea = System.getProperty("osgi.instance.area"); //$NON-NLS-1$
			if (osgiInstanceArea == null) {
				osgiInstanceArea = System.getProperty("osgi.instance.area.default"); //$NON-NLS-1$
				if (osgiInstanceArea == null) {
					System.err.println("Neither the system property \"osgi.instance.area\" nor \"osgi.instance.area.default\" is set!!! You might want to set \"osgi.instance.area.default\" in your config.ini! And you should check your OSGI environment, because even without the default, a concrete value should be set!"); //$NON-NLS-1$
					throw new IllegalStateException("Neither the system property \"osgi.instance.area\" nor \"osgi.instance.area.default\" is set!!! You might want to set \"osgi.instance.area.default\" in your config.ini! And you should check your OSGI environment, because even without the default, a concrete value should be set!"); //$NON-NLS-1$
				}
			}

			String prefix = "file:"; //$NON-NLS-1$
			if (osgiInstanceArea.startsWith(prefix))
				osgiInstanceArea = osgiInstanceArea.substring(prefix.length());

			File f = new File(osgiInstanceArea).getAbsoluteFile();
			f = new File(f, "data"); //$NON-NLS-1$
			f.mkdirs();
			rootDir = f.getAbsolutePath();

//			File rootFile = null;
//			// check system property org.nightlabs.appfolder
//			String initialFolderName = System.getProperty(APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME);
//			if (initialFolderName == null) {
//				// sys property not set, we use the users home dir
//				rootFile = new File(System.getProperty("user.home"), "."+getApplicationName()); //$NON-NLS-1$ //$NON-NLS-2$
//			}
//			else {
//				// the sys property is set, parse it
//				String resolvedFolderName = initialFolderName;
//				Pattern envRefs = Pattern.compile("\\$((.*?))\\$"); //$NON-NLS-1$
//				Matcher matcher = envRefs.matcher(initialFolderName);
//				while (matcher.find()) {
//					String envValue = System.getenv(matcher.group(1));
//					if (envValue == null) {
//						System.err.println("Reference to undefined system environment variable "+matcher.group(1)+" in system property "+APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME); //$NON-NLS-1$ //$NON-NLS-2$
//						envValue = ""; //$NON-NLS-1$
//					}
//					resolvedFolderName = resolvedFolderName.replace(matcher.group(0), envValue);
//				}
//				rootFile = new File(resolvedFolderName, "."+getApplicationName()); //$NON-NLS-1$
//			}
//			if (rootFile.exists() && !rootFile.isDirectory()) {
//				System.err.println("[PANIC] The application's root directory exists, but is NOT a directory: "+rootFile); //$NON-NLS-1$
//				System.err.println("[PANIC] The application will probably not run correctly!!"); //$NON-NLS-1$
//			}
//			if (!rootFile.exists() && !rootFile.mkdirs()) {
//				System.err.println("[PANIC] Could not create the application's root directory: "+rootFile); //$NON-NLS-1$
//				System.err.println("[PANIC] The application will probably not run correctly!!"); //$NON-NLS-1$
//			}
//			rootDir = rootFile.getAbsolutePath();
//			setAppFolderSystemProperty(rootDir);
		}
		return rootDir;
	}

	private static String configDir = ""; //$NON-NLS-1$

	/**
	 * returns the config directory, which is getRootDir()+"/config".
	 * @return the config directory, which is getRootDir()+"/config".
	 */
	public static String getConfigDir() {
		if (configDir.equals("")){ //$NON-NLS-1$
			File configFile = new File(getRootDir(),"config"); //$NON-NLS-1$
			configFile.mkdirs();
			configDir = configFile.getAbsolutePath();
		}
		return configDir;
	}


	private static String logDir = ""; //$NON-NLS-1$

	/**
	 * returns the log directory, which is getRootDir()+"/log"
	 * @return the log directory, which is getRootDir()+"/log"
	 */
	public static String getLogDir() {
		if (logDir.equals("")){ //$NON-NLS-1$
			File logDirF = new File(getRootDir(), "log"); //$NON-NLS-1$
			if (!logDirF.exists()) {
				if (!logDirF.mkdirs())
					System.err.println("Could not create log directory "+logDirF.getAbsolutePath()); //$NON-NLS-1$
			}
			logDir = logDirF.getAbsolutePath();

			// the log4j.xml references the log-directory via the system property - hence we need to set it here.
			System.setProperty(LOG_DIR_PROPERTY_KEY, logDir);
		}
		return logDir;
	}

	protected static final String LOG4J_CONFIG_FILE = "log4j.xml"; //$NON-NLS-1$

	/**
	 * Configures log4j with the file located in {@link #getConfigDir()}+"/log4j.properties"
	 * @throws IOException If copying the config-file fails.
	 */
	public void initializeLogging()
	throws IOException
	{
		File logConfFile = new File(getConfigDir(), LOG4J_CONFIG_FILE);
		if (!logConfFile.exists()){
			// if not there copy
			IOUtil.copyResource(AbstractApplication.class, LOG4J_CONFIG_FILE, logConfFile);
		}
		getLogDir(); // ensure the directory exists and the system-property is set

		// TODO BEGIN temporary cleanup
		// Because there was a log4j.properties file used until we switched to the log4j.xml, we
		// create a backup and delete it, if necessary, in order to make it clear for an administrator
		// that the file is not used anymore.
		{
			File oldConfFile = new File(getConfigDir(), "log4j.properties"); //$NON-NLS-1$
			if (oldConfFile.exists()) {
				File backup = new File(oldConfFile.getAbsolutePath() + ".bak-" + Long.toString(System.currentTimeMillis(), 36)); //$NON-NLS-1$
				oldConfFile.renameTo(backup);
			}
		}
		// END temporary cleanup

//		org.apache.log4j.PropertyConfigurator.configure(logConfFile.getAbsolutePath());
		org.apache.log4j.xml.DOMConfigurator.configure(logConfFile.toURI().toURL());
		logger.info("Logging for \"" + System.getProperty("eclipse.product") + "\" started."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//		logger.info(getApplicationName()+" started."); //$NON-NLS-1$
	}

//	/**
//	 * sets the System Property for the ApplicationName so that the
//	 * log4j.properties can access this systemProperty
//	 *
//	 */
//	protected static void setAppNameSystemProperty()
//	{
//		try {
//			System.setProperty(APPLICATION_SYSTEM_PROPERTY_NAME, getApplicationName());
//		} catch (SecurityException se) {
//			System.out.println("System Property "+APPLICATION_SYSTEM_PROPERTY_NAME+" could not be set, to "+getApplicationName()+" because:"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			System.out.println("You dont have the permission to set a System Property"); //$NON-NLS-1$
//		} catch (NullPointerException npe) {
//			System.out.println("System Property "+APPLICATION_SYSTEM_PROPERTY_NAME+" could not be set, to "+getApplicationName()+" because:");    	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			System.out.println("applicationName == null"); //$NON-NLS-1$
//		}
//	}

//	/**
//	 * Sets the system property for the applications root dir - the application folder.
//	 */
//	protected static void setAppFolderSystemProperty(String appFolder)
//	{
//		try {
//			System.setProperty(APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME, appFolder);
//		} catch (SecurityException se) {
//			System.out.println("System Property "+APPLICATION_FOLDER_SYSTEM_PROPERTY_NAME+" could not be set, to "+appFolder+" because:"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			System.out.println("You dont have the permission to set a System Property"); //$NON-NLS-1$
//		} catch (NullPointerException npe) {
//			System.out.println("System Property "+APPLICATION_SYSTEM_PROPERTY_NAME+" could not be set, to "+appFolder+" because of a NullPointerException"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			npe.printStackTrace();
//		}
//	}
	
	/**
	 * Initializes the Exception Handling by setting the DefaultUncaughtExceptionHandler
	 * to the {@link ExceptionHandlerRegistry} and setting the Platform {@link SafeRunnable}
	 * to {@link SaveRunnableRunner}.
	 * 
	 * @see Thread#setDefaultUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler)
	 */
	public void initExceptionHandling() {
		ExceptionHandlerRegistry.sharedInstance().addProcessListener(SimpleExceptionHandlerRegistry.sharedInstance());

		
		final Thread.UncaughtExceptionHandler oldDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (!ExceptionHandlerRegistry.syncHandleException(t, e)) {
					if (oldDefaultExceptionHandler != null) {
						oldDefaultExceptionHandler.uncaughtException(t, e);
					}
				}
			}
		});
		SafeRunnable.setRunner(new SaveRunnableRunner());
	}

	/**
	 * Is called when the application starts and does all
	 * the necessary initialization for the application
	 * and afterwards creates the Workbench.
	 * 
	 * @see IApplication#start(IApplicationContext)
	 */
	@Override
	public Object start(IApplicationContext context) throws Exception {
		try {
			initExceptionHandling();
			
			NLBasePlugin.setApplicationSingletonProvider(SingletonProviderFactory.createProviderForInstance(this));
			
			this.arguments = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
			
			initializeLogging();
			initConfig();
			
			try {
				// readded old RemoveExtensionRegistry because nearly all extensions are not adopted yet. Daniel 2010-08-30
				org.nightlabs.base.ui.extensionpoint.RemoveExtensionRegistry.sharedInstance().removeRegisteredExtensions();
				RemoveExtensionRegistry.sharedInstance().removeRegisteredExtensions();
			} catch (Throwable t) {
				logger.error("There occured an error while tyring to remove all registered extensions", t); //$NON-NLS-1$
			}

			preCreateWorkbench();
			if (platformReturnCode >= 0) {
				return platformReturnCode;
			}
			display = PlatformUI.createDisplay();
			UIContext.sharedInstance().registerRunner(Thread.currentThread(), new DefaultUIContextRunner());
			try {
				int returnCode = PlatformUI.createAndRunWorkbench(display, initWorkbenchAdvisor(display));
				if (returnCode == PlatformUI.RETURN_RESTART)
					return IApplication.EXIT_RESTART;
				else
					return IApplication.EXIT_OK;
			} finally {
				// When using the Editor2D-viewer (probably because of the SWT-AWT-bridge) the following display.dispose() sometimes hangs forever.
				// Hence, we launch a surveillance-thread that will call a System.exit after a certain timeout.
				// The Editor2D viewer bug is solved, but still it is very useful to ensure shutdown this way.
				new ExitThread();
				display.dispose();
			}
		} finally { // TODO Is this necessary? Marco.
			if (Display.getCurrent() != null)
				Display.getCurrent().dispose();
		}
	}

	private static class ExitThread extends Thread
	{
		private long timeoutMSec = 60 * 1000;
		private Logger logger = Logger.getLogger(ExitThread.class);

		public ExitThread()
		{
			setDaemon(true);
			start();
		}

		@Override
		public void run()
		{
			long start = System.currentTimeMillis();
			logger.info("Starting surveillance of application shutdown. Giving it " + timeoutMSec + " msec to finish the JVM cleanly."); //$NON-NLS-1$ //$NON-NLS-2$

			while (!isInterrupted() && !interruptRequested) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// silently ignore
				}

				long duration = System.currentTimeMillis() - start;
				if (duration > timeoutMSec) {
					logger.error("The application did not finish cleanly within timeout (" + timeoutMSec + " msec)! Will force immediate termination of JVM now!!!"); //$NON-NLS-1$ //$NON-NLS-2$
					System.exit(0);
				}
			}
		}

		private volatile boolean interruptRequested = false;

		@Override
		public void interrupt()
		{
			interruptRequested = true;
			super.interrupt();
		}
	}

	/**
	 * is called when the application is stopped
	 * and by default closes the workbench
	 * 
	 * @see IApplication#stop()
	 */
	@Override
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	protected String[] arguments = new String[0];

	/**
	 * This method returns the program arguments. Note, that they are <code>null</code> until {@link #run(Object)} has been
	 * called!
	 *
	 * @return The program arguments as passed to the application.
	 */
	public String[] getArguments() {
		return arguments;
	}
	
	
	

	/**
	 * 
	 * @return the Implementation of the AbstractWorkbenchAdvisor for the
	 * {@link AbstractApplication}.
	 * 
	 * @see AbstractWorkbenchAdvisor
	 */
	public abstract AbstractWorkbenchAdvisor initWorkbenchAdvisor(Display display);
	
	/**
	 * Initializes the Config in the ConfigDir of the Application
	 * @throws ConfigException If creating the Configs shared instance fails.
	 */
	public void initConfig()
	throws ConfigException
	{
		// initialize the Config
		Config.createSharedInstance(new File(AbstractApplication.getConfigDir(), "config.xml"), true);		 //$NON-NLS-1$
	}

//	/**
//	 * Should return the application name for this application.
//	 * This will be used to choose the application folder.
//	 *
//	 * @return the name of the Application
//	 */
//	protected abstract String initApplicationName();
	
	/**
	 * Is called before the Workbench is created, subclasses may override and
	 * do custom things like initialization before the Workbench is created.
	 * <p>
	 * This implementation does nothing.
	 * </p>
	 */
	public void preCreateWorkbench() {}
	
	/**
	 * Sets the platforms returnCode
	 * 
	 * @param platformReturnCode The return code to set.
	 */
	protected void setPlatformReturnCode(int platformReturnCode) {
		this.platformReturnCode = platformReturnCode;
	}
}
