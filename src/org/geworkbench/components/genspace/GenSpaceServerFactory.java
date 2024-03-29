package org.geworkbench.components.genspace;
//package genspace.ui;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.geworkbench.components.genspace.server.stubs.FriendFacade;
import org.geworkbench.components.genspace.server.stubs.FriendFacadeService;
import org.geworkbench.components.genspace.server.stubs.NetworkFacade;
import org.geworkbench.components.genspace.server.stubs.NetworkFacadeService;
import org.geworkbench.components.genspace.server.stubs.PublicFacade;
import org.geworkbench.components.genspace.server.stubs.PublicFacadeService;
import org.geworkbench.components.genspace.server.stubs.Tool;
import org.geworkbench.components.genspace.server.stubs.ToolUsageInformation;
import org.geworkbench.components.genspace.server.stubs.ToolUsageInformationService;
import org.geworkbench.components.genspace.server.stubs.UsageInformation;
import org.geworkbench.components.genspace.server.stubs.UsageInformationService;
import org.geworkbench.components.genspace.server.stubs.User;
import org.geworkbench.components.genspace.server.stubs.UserFacade;
import org.geworkbench.components.genspace.server.stubs.UserFacadeService;
import org.geworkbench.components.genspace.server.stubs.UserNetwork;
import org.geworkbench.components.genspace.server.stubs.WorkflowFolder;
import org.geworkbench.components.genspace.server.stubs.WorkflowRepository;
import org.geworkbench.components.genspace.server.stubs.WorkflowRepositoryService;
import org.geworkbench.components.genspace.server.wrapper.UserWrapper;



public class GenSpaceServerFactory {

	private static User user;
	private static String username = null;
	private static String password = null;
	private static String SERVER_ADDR = "http://"+RuntimeEnvironmentSettings.PROD_HOST+":8080";
	
//	public static Logger logger = Logger.getLogger(GenSpaceServerFactory.class);
//	private static UserFacade userFacade;
//	private static UsageInformation usageFacade;
//	private static FriendFacade friendFacade;
//	private static NetworkFacade networkFacade;
//	private static PublicFacade publicFacade;
//	private static WorkflowRepository workflowFacade;
//	private static ToolUsageInformation toolUsageFacade;
//	
	private static Object lock = new Object();

	public static void init()
	{
		loadTools();
	}
	
	public static Object readObject(byte[] data)
	{
		ObjectInputStream is;
		try {
			is = new ObjectInputStream(new ByteArrayInputStream(data));
			return is.readObject(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static byte[] writeObject(Object o)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	private static void loadTools()
	{
		if(RuntimeEnvironmentSettings.tools == null)
		{
			RuntimeEnvironmentSettings.tools = new HashMap<Integer, Tool>();
				for(Tool t : getUsageOps().getAllTools())
				{
					RuntimeEnvironmentSettings.tools.put(t.getId(), t);
				}
		}
	}
	
	public static void handleExecutionException(String msg, Exception e)
	{
//		e.printStackTrace();
		GenSpaceServerFactory.clearCache();
		JOptionPane.showMessageDialog(null, "There was an error communicating with the genSpace server:\n"+msg, "Error communicating with server", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void handleExecutionException(Exception e)
	{
//		e.printStackTrace();
		GenSpaceServerFactory.clearCache();
		JOptionPane.showMessageDialog(null, "There was an error communicating with the genSpace server.\n Please try your request again", "Error communicating with server", JOptionPane.ERROR_MESSAGE);
	}
	public static void clearCache()
	{
//		userFacade = null;
//		usageFacade = null;
//		friendFacade = null;
//		networkFacade = null;
//		publicFacade = null;
//		workflowFacade = null;	
	}
	public static void handleException(Exception e)
	{
//		e.printStackTrace();
	}
	private static void addCredentials(BindingProvider svc)
	{
		if(username != null && password != null)
		{
			((BindingProvider)svc).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			((BindingProvider)svc).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
	}
	public synchronized static WorkflowRepository getWorkflowOps()
	{
		WorkflowRepository workflowFacade;
			loadTools();
			try {
				workflowFacade = (new WorkflowRepositoryService(new URL(SERVER_ADDR+"/WorkflowRepositoryService/WorkflowRepository?wsdl"),  new QName("http://server.genspace.components.geworkbench.org/", "WorkflowRepositoryService"))).getWorkflowRepositoryPort();
				addCredentials((BindingProvider) workflowFacade);
				return workflowFacade;

			} catch (MalformedURLException e) {
				return null;
			}
	}
	public synchronized static ToolUsageInformation getUsageOps()
	{
		loadTools();
		ToolUsageInformation toolUsageFacade;
		try {
			toolUsageFacade = (new ToolUsageInformationService(new URL(SERVER_ADDR+"/ToolUsageInformationService/ToolUsageInformation?wsdl"),  new QName("http://server.genspace.components.geworkbench.org/", "ToolUsageInformationService"))).getToolUsageInformationPort();
			return toolUsageFacade;

		} catch (MalformedURLException e) {
			return null;
		}
	}
	public synchronized static UserFacade getUserOps()
	{

		UserFacade userFacade;
		try {
			userFacade = (new UserFacadeService(new URL(SERVER_ADDR+"/UserFacadeService/UserFacade?wsdl"),  new QName("http://server.genspace.components.geworkbench.org/", "UserFacadeService"))).getUserFacadePort();
			addCredentials((BindingProvider) userFacade);
			return userFacade;
		} catch (MalformedURLException e) {
			return null;
		}
		
	}
	
	public synchronized static PublicFacade getPublicFacade()
	{
		loadTools();
		try{
		PublicFacade publicFacade = (new PublicFacadeService(new URL(SERVER_ADDR+"/PublicFacadeService/PublicFacade?wsdl"),  new QName("http://server.genspace.components.geworkbench.org/", "PublicFacadeService"))).getPublicFacadePort();
		addCredentials((BindingProvider) publicFacade);
		return publicFacade;
		} catch(MalformedURLException e)
		{
			return null;
		}
	}
	
	public synchronized static UsageInformation getPrivUsageFacade()
	{
		if(user == null)
			return null;
		loadTools();
		try
		{
		UsageInformation usageFacade = (new UsageInformationService(new URL(SERVER_ADDR+"/UsageInformationService/UsageInformation?wsdl"),  new QName("http://server.genspace.components.geworkbench.org/", "UsageInformationService"))).getUsageInformationPort();
			addCredentials((BindingProvider) usageFacade);
		return usageFacade;
		} catch (MalformedURLException e)
		{
			return null;
		}
	}
	public synchronized static FriendFacade getFriendOps()
	{
try{
		FriendFacade friendFacade = (new FriendFacadeService(new URL(SERVER_ADDR+"/FriendFacadeService/FriendFacade?wsdl"),  new QName("http://server.genspace.components.geworkbench.org/", "FriendFacadeService"))).getFriendFacadePort();
			addCredentials((BindingProvider) friendFacade);
		return friendFacade;
} catch (MalformedURLException e)
{
	return null;
}
	}
	public synchronized static NetworkFacade getNetworkOps()
	{
		try{
		NetworkFacade networkFacade = (new NetworkFacadeService(new URL(SERVER_ADDR+"/NetworkFacadeService/NetworkFacade?wsdl"),  new QName("http://server.genspace.components.geworkbench.org/", "NetworkFacadeService"))).getNetworkFacadePort();
		addCredentials((BindingProvider) networkFacade);
		return networkFacade;
		} catch (MalformedURLException e)
		{
			return null;
		}
	}
	
	
	public GenSpaceServerFactory() {
		super();
	}
	public static WorkflowFolder rootFolder = null;
	
	public static UserWrapper getWrappedUser() {
		return new UserWrapper(user);
	}
	public static User getUser() {
		return user;
	}
	public static boolean userRegister(User u) {
		try {
			user = getPublicFacade().register(u);
		} catch (Exception e) {
			handleExecutionException(e);
		}

		if(user != null)
			return true;
		return false;
	}
	public static String getObjectSize(Serializable s)
	{
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(s);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		 
		 return " " + ((double) baos.size())/(1024) + " KB";
	}

	public static boolean userLogin(String username, String password) {
		synchronized(lock)
		{
			System.setProperty("java.security.auth.login.config", "components/genspace/classes/org/geworkbench/components/genspace/login.conf");
			try {
				logout();
				GenSpaceServerFactory.username = username;
				GenSpaceServerFactory.password = UserWrapper.getEncryptedPassword(password.toCharArray());
				
				user = getUserOps().getMe();
				//TODO
					return true;
			} 
			catch (Exception e) {
				handleException(e);
				return false;
			}
//			return true;
		}
	
	}
	public static void updateCachedUser()
	{
		try {
			user = getUserOps().getMe();
		} catch (Exception e) {
			handleException(e);
		}
	}
	public static boolean userUpdate() {

		try {
			getUserOps().updateUser(user);
		} catch (Exception e) {
			handleException(e);
		}
		return true;
	}

	public static List<UserNetwork> getAllNetworks() {
		return getUserOps().getMyNetworks();
	}

	public static String getUsername() {
		if(user == null)
			return null;
		return user.getUsername();
	}

	public static boolean isLoggedIn() {
		return user != null;
	}

	public static void logout() {
		try {
			//TODO
//			pm.logout(true);
		} catch (Exception e) {
		}
		rootFolder = null;
//		userFacade = null;
//		usageFacade = null;
		username = null;
		password = null;
//		friendFacade = null;
//		networkFacade = null;
//		publicFacade = null;
//		workflowFacade = null;
		user = null;
	}

	/**
	 * Returns if the currently logged in user may view the profile of the specified user
	 * @param user2
	 * @return
	 */
	public static boolean isVisible(User user2) {
		if(user2.getUsername().equals(getUser().getUsername()))
			return true;
		return user2.isVisible();
	}

	
}
