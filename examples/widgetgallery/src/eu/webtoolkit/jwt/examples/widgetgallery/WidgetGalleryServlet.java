package eu.webtoolkit.jwt.examples.widgetgallery;

import eu.webtoolkit.jwt.BootstrapVersion;
import eu.webtoolkit.jwt.Configuration;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WBootstrap5Theme;
import eu.webtoolkit.jwt.WBootstrapTheme;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WHBoxLayout;
import eu.webtoolkit.jwt.WLink;
import eu.webtoolkit.jwt.WXmlLocalizedStrings;
import eu.webtoolkit.jwt.WtServlet;

public class WidgetGalleryServlet extends WtServlet {
	public WidgetGalleryServlet() {
		setConfiguration(new Configuration() {
			{
				setErrorReporting(ErrorReporting.NoErrors);
				setUaCompatible("IE8=IE7");
				setTinyMCEVersion(4);
				getProperties().put("leafletJSURL", "https://unpkg.com/leaflet@1.5.1/dist/leaflet.js");
				getProperties().put("leafletCSSURL", "https://unpkg.com/leaflet@1.5.1/dist/leaflet.css");
			}

			@Override
			public boolean progressiveBootstrap(String internalPath) {
				if (internalPath.equals("/trees-tables/mvc-table-views") ||
					internalPath.equals("/trees-tables/mvc-tree-views") ||
					internalPath.equals("/trees-tables/mvc-item-models") ||
					internalPath.equals("/layout/layout-managers") ||
					internalPath.equals("/forms/line_text-editor") ||
					internalPath.startsWith("/graphics-charts"))
					return false;
				else
					return true;
			}
		});
		
	}
	
	@Override
	public WApplication createApplication(WEnvironment env) {
		WApplication app = new WApplication(env);

		WBootstrap5Theme theme = new WBootstrap5Theme();

		app.setTheme(theme);

		WXmlLocalizedStrings resourceBundle = new WXmlLocalizedStrings();
		resourceBundle.use("/eu/webtoolkit/jwt/examples/widgetgallery/text");
		resourceBundle.use("/eu/webtoolkit/jwt/examples/widgetgallery/report");
		resourceBundle.use("/eu/webtoolkit/jwt/examples/widgetgallery/tpl");
		resourceBundle.use("/eu/webtoolkit/jwt/examples/widgetgallery/src");
		app.setLocalizedStrings(resourceBundle);

		app.getRoot().addWidget(new WidgetGallery());
		
		app.setTitle("JWt Widget Gallery");
		
		app.useStyleSheet(new WLink("style/widgetgallery.css"));
		app.useStyleSheet(new WLink("style/everywidget.css"));
		app.useStyleSheet(new WLink("style/dragdrop.css"));
		app.useStyleSheet(new WLink("style/combostyle.css"));
		app.useStyleSheet(new WLink("style/pygments.css"));
		app.useStyleSheet(new WLink("style/layout.css"));
		app.useStyleSheet(new WLink("style/filedrop.css"));

		return app;
	}
}
