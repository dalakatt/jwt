/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.webtoolkit.jwt.servlet.WebRequest;
import eu.webtoolkit.jwt.servlet.WebResponse;
import eu.webtoolkit.jwt.servlet.WebRequest.ProgressListener;
import eu.webtoolkit.jwt.utils.JarUtils;
import eu.webtoolkit.jwt.utils.MathUtils;
import eu.webtoolkit.jwt.utils.StreamUtils;

/**
 * The abstract JWt servlet class.
 * <p>
 * This servlet processes all requests for a JWt application. You need to specialize this class to provide an entry point for
 * your web application.
 * <p>
 * For each new session {@link #createApplication(WEnvironment)} is called to create a new {@link WApplication} object for that session.
 * The web controller that is implemented by this servlet validates each incoming request, and takes the appropriate action by either
 * notifying the application of an event, or serving a {@link WResource}.
 */
public abstract class WtServlet extends HttpServlet {
	private static class BoundSession implements HttpSessionBindingListener {
		private WebSession session;

		BoundSession(WebSession session) {
			this.session = session;
		}

		WebSession getSession() {
			return session;
		}

		@Override
		public void valueBound(HttpSessionBindingEvent arg0) { }

		@Override
		public void valueUnbound(HttpSessionBindingEvent arg0) {
			WApplication app = session.getApp();
			if (app != null)
				app.destroy();
			session.destruct();
		}
	}
	
	private static Logger logger = LoggerFactory.getLogger(WtServlet.class);
	
	private static final long serialVersionUID = 1L;

	private static ServletApi servletApi = null;

	private Configuration configuration;
	private ProgressListener progressListener;
	private Set<String> uploadProgressUrls_ = new HashSet<String>();
	private int ajaxSessions = 0;
	private int sessions = 0;

	private String redirectSecret_;

	private static final String WT_WEBSESSION_ID = "wt-websession";
	private static final Map<String, String> mimeTypes = new HashMap<String, String>();
	
	private List<WResource> staticResources = new ArrayList<WResource>();

	static final String Boot_html;
	static final String Plain_html;
	static final String Wt_js;
	static final String Boot_js;
	static final String Hybrid_html;
	static final String JQuery_js;
	static final String Wt_xml = "/eu/webtoolkit/jwt/wt";
	public static final String Auth_xml = "/eu/webtoolkit/jwt/auth/auth";
	
	private static WtServlet instance;

	static {
		Boot_html = readFile("/eu/webtoolkit/jwt/skeletons/Boot.html");
		Plain_html = readFile("/eu/webtoolkit/jwt/skeletons/Plain.html");
		Hybrid_html = readFile("/eu/webtoolkit/jwt/skeletons/Hybrid.html");
		Wt_js = readFile("/eu/webtoolkit/jwt/skeletons/Wt.min.js");
		Boot_js = readFile("/eu/webtoolkit/jwt/skeletons/Boot.min.js");
		JQuery_js = readFile("/eu/webtoolkit/jwt/skeletons/jquery.min.js");
		
		String[][] mimeTypes = {
				{ "css", "text/css" },
				{ "gif", "image/gif" },
				{ "htm", "text/html" },
				{ "html", "text/html" },
				{ "jpg", "image/jpeg" },
				{ "png", "image/png" },
				{ "js", "text/javascript" } 
			};

		for (String[] s : mimeTypes)
			WtServlet.mimeTypes.put(s[0], s[1]);
		
		WObject.seedId(MathUtils.randomInt());
	}

	/**
	 * This function is only to be used by JWt internals.
	 * 
	 * @return the servlet API interface
	 */
	public static ServletApi getServletApi() {
		return servletApi;
	}

	/**
	 * Constructor.
	 * <p>
	 * Instantiates the servlet using the default configuration.
	 * 
	 * @see #getConfiguration()
	 */
	public WtServlet() {
		this.progressListener = new ProgressListener() {
			public void update(WebRequest request, long pBytesRead, long pContentLength) {
				requestDataReceived(request, pBytesRead, pContentLength);
			}
		};
		
		this.configuration = new Configuration();
		
		redirectSecret_ = MathUtils.randomId(32);
		
		instance = this;
	}
	
	/**
	 * Initiate the internal servlet api.
	 * 
	 * If you want to override this function, make sure to call the super function,
	 * to ensure the initialization of the servlet api.
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		String configFile = this.getInitParameter("jwt-config-file");
		if (configFile != null)
			this.configuration = new Configuration(new File(configFile));
		
		servletApi = ServletInit.getInstance(config.getServletContext()).getServletApi();
	}

	void handleRequest(final HttpServletRequest request, final HttpServletResponse response) {
		String pathInfo = WebRequest.computePathInfo(request);
		String resourcePath = configuration.getProperty(WApplication.RESOURCES_URL);
		
		if (pathInfo !=null) {
			String scriptName = WebRequest.computeScriptName(request);
			
			for (WResource staticResource : staticResources) {
				String staticResourcePath = null;
				if (!staticResource.getInternalPath().startsWith("/"))
					staticResourcePath = scriptName + staticResource.getInternalPath();
				else
					staticResourcePath = staticResource.getInternalPath();
				
				if ((scriptName + pathInfo).equals(staticResourcePath)) {
					try {
						WebRequest webRequest = new WebRequest(request, progressListener);
						WebResponse webResponse = new WebResponse(response, webRequest);
						staticResource.handle(webRequest, webResponse);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		}

		if (pathInfo != null && (pathInfo.startsWith(resourcePath) || pathInfo.equals(configuration.getFavicon()))) {
			logger.debug("serving static file: " + pathInfo);

			String fileName = "wt-resources/";

			if (pathInfo.startsWith(resourcePath))
				pathInfo = pathInfo.substring(resourcePath.length());

			while (pathInfo.charAt(0) == '/')
				pathInfo = pathInfo.substring(1);

			fileName += pathInfo;
			try {
				InputStream s = getResourceStream(fileName);
				if (s != null) {
					String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
					String mimeType = mimeTypes.get(suffix);
					if (mimeType != null)
						response.setContentType(mimeType);
					else
						response.setContentType("application/octet-stream");
					StreamUtils.copy(s, response.getOutputStream());
					response.getOutputStream().flush();
				} else {
					response.setStatus(404);
				}
			} catch (FileNotFoundException e) {
				response.setStatus(404);
				e.printStackTrace();
			} catch (IOException e) {
				response.setStatus(500);
				e.printStackTrace();
			}

			return;
		}

		WebRequest webRequest = new WebRequest(request, progressListener);
		WebResponse webResponse = new WebResponse(response, webRequest);

		servletApi.doHandleRequest(this, webRequest, webResponse);
	}

	/**
	 * Implement the GET request.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	/**
	 * Implement the POST request.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	/**
	 * Returns the JWt configuration.
	 * <p>
	 * The Configuration is only definitively constructed after WtServlet#init() is invoked.
	 * You should only modify the configuration from this method.
	 * 
	 * @return the configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the JWt configuration.
	 * <p>
	 * You should only set the configuration from the servlet constructor.
	 * 
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	WApplication doCreateApplication(WebSession session) {
		return createApplication(session.getEnv());
	}

	/**
	 * Creates a new application for a new session.
	 * 
	 * @param env the environment that describes the new user (agent) and initial parameters
	 * @return a new application object.
	 */
	public abstract WApplication createApplication(WEnvironment env);

	synchronized int addSession() {
		++sessions;
		return sessions;
	}

	synchronized void newAjaxSession() {
		++ajaxSessions;
	}

	synchronized int removeSession(WebSession session) {
		if (session.getEnv().hasAjax())
			--ajaxSessions;
		--sessions;
		return sessions;
	}

	/*
	 * Actual request handling, may be within an async call depending on the servlet API.
	 */
	void doHandleRequest(WebRequest request, WebResponse response) {		
		HttpSession jsession = request.getSession();
		BoundSession bsession = (BoundSession) jsession.getAttribute(WtServlet.WT_WEBSESSION_ID);
		WebSession wsession = null;

		if (bsession != null)
			wsession = bsession.getSession();

		getConfiguration().setSessionTimeout(jsession.getMaxInactiveInterval());

		try {
			if (wsession == null) {
				String applicationTypeS = getServletConfig().getInitParameter("ApplicationType");
				
				EntryPointType applicationType;
				if (applicationTypeS == null || applicationTypeS.equals("") || applicationTypeS.equals("Application")) {
					applicationType = EntryPointType.Application; 
				} else if (applicationTypeS.equals("WidgetSet")) {
					applicationType = EntryPointType.WidgetSet; 
				} else {
					throw new WtException("Illegal application type: " + applicationTypeS);
				}
				
				wsession = new WebSession(this, jsession.getId(), applicationType, getConfiguration().getFavicon(), request);
				logger.info("Session created: " + jsession.getId() + " (#sessions = " + addSession() + ")");
				jsession.setAttribute(WtServlet.WT_WEBSESSION_ID, new BoundSession(wsession));
			}
	
			logger.debug("Handling: (" + jsession.getId() + "): " + request.getRequestURI() + " " + request.getMethod() + " " + request.getScriptName() + " " + request.getPathInfo() + " " + request.getQueryString());
			
			WebSession.Handler handler = null;
			try {
				handler = new WebSession.Handler(wsession, request, response);
				wsession.handleRequest(handler);
			} finally {
				handler.release();
			}

			if (handler != null && handler.getSession().isDead()) {
				try {
					jsession.setAttribute(WtServlet.WT_WEBSESSION_ID, null);
					jsession.invalidate();
					logger.info("Session exiting: " + jsession.getId() + " (#sessions = " + removeSession(handler.getSession()) + ")");
				} catch (IllegalStateException e) {
					// If session was invalidated by another request...
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void addUploadProgressUrl(String url) {
		synchronized (uploadProgressUrls_) {
			uploadProgressUrls_.add(url.substring(url.indexOf('?') + 1));
		}
	}

	void removeUploadProgressUrl(String url) {
		synchronized (uploadProgressUrls_) {
			uploadProgressUrls_.remove(url.substring(url.indexOf('?') + 1));
		}
	}
	
	boolean requestDataReceived(WebRequest request, long current, long total) {
		boolean found = false;

		synchronized (uploadProgressUrls_) {
			for (String url : uploadProgressUrls_) {
				if (url.equals(request.getQueryString())) {
					found = true;
					break;
				}
			}
		}

		if (found) {
			HttpSession jsession = request.getSession();
			BoundSession bsession = (BoundSession) jsession.getAttribute(WtServlet.WT_WEBSESSION_ID);
			WebSession wsession = null;

			if (bsession != null)
				wsession = bsession.getSession();

			if (wsession != null) {
				WebSession.Handler handler = null;
				
				try {
					handler = new WebSession.Handler(wsession,request, null);

					if (!wsession.isDead() && wsession.getApp() != null) {
						String requestE = request.getParameter("request");

						WResource resource = null;
						if (requestE == null && request.getPathInfo().length() != 0)
							resource = wsession.getApp().
								decodeExposedResource("/path/" + request.getPathInfo());

						if (resource == null) {
							String resourceE = request.getParameter("resource");
							resource = wsession.getApp().
								decodeExposedResource(resourceE);
						}

						if (resource != null) {
							// FIXME, we should do this within app.notify()
							resource.dataReceived().trigger(current, total);
						}
					}
					
				} finally {
					handler.release();
				}
			}
		}

		return true;
	}

	/**
	 * Returns whether asynchronous I/O is supported.
	 * 
	 * This is only the case when the servlet container implements the Servlet 3.0 API,
	 * and when this application is configured to support asynchronous processing.
	 * 
	 * Asynchronous I/O is required for recursive event loops, and encouraged for
	 * scalable server push (although JWt doesn't strictly require it).
	 * 
	 * @return whether asynchronous I/O is supported.
	 */
	public static boolean isAsyncSupported() {
		WebSession.Handler handler = WebSession.Handler.getInstance();

		return servletApi.isAsyncSupported(handler != null ? handler.getRequest() : null);
	}

    public boolean limitPlainHtmlSessions() {
    	return false; // FIXME
	}
	
	public String readConfigurationProperty(String name, String value) {
		String result = configuration.getProperty(name);
		if (result == null)
			return value;
		else
			return result;
	}
	
	private InputStream getResourceStream(final String fileName) throws FileNotFoundException {
		return this.getClass().getResourceAsStream("/eu/webtoolkit/jwt/" + fileName);
	}

	private static String readFile(final String fileName) {
		return JarUtils.getInstance().readTextFromJar(fileName);
	}

	String computeRedirectHash(String url) {
		try {
			MessageDigest d = MessageDigest.getInstance("MD5");
			d.update(redirectSecret_.getBytes("UTF-8"));
			d.update(url.getBytes("UTF-8"));
			return StringUtils.encodeBase64(d.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Binds a resource to a fixed path.
	 *
	 * Resources may either be private to a single session or public. Use this method to add a public resource with a fixed path.
	 * When the path contains the application context's path, the path should start with a '/',
	 * if not the '/' should be omitted.
	 */
	public void addResource(WResource staticResource, String path) {
		for (WResource sr : staticResources) {
			if (sr.getInternalPath() != null && sr.getInternalPath().equals(path)) {
				WString error = new WString(
						"WtServlet#addResource() error: a static resource was already deployed on path '{1}'");
				throw new RuntimeException(error.arg(path).toString());
			}
		}
		
		staticResource.setInternalPath(path);
		staticResources.add(staticResource);
	}
	
	public static WtServlet getInstance() {
		return instance;
	}
}