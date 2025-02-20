/*
 * Copyright (C) 2020 Emweb bv, Herent, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.servlet.*;
import eu.webtoolkit.jwt.utils.*;
import java.io.*;
import java.lang.ref.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A rich-text XHTML editor.
 *
 * <p>The editor provides interactive editing of XHTML text. By default it provides basic mark-up
 * (font, formatting, color, links, and lists), but additional buttons may be added to the tool bars
 * that add additional formatting options.
 *
 * <p>The implementation is based on <a href="http://tinymce.moxiecode.com/">TinyMCE</a>. The widget
 * may be configured and tailored using the {@link WTextEdit#setConfigurationSetting(String name,
 * Object value) setConfigurationSetting()} and related methods that provide direct access to the
 * underlying TinyMCE component.
 *
 * <p>You can use this widget with TinyMCE version 3 or version 4.
 *
 * <p>The choice is global and set using Configuration#setTinyMCEVersion(int).
 *
 * <p>
 *
 * <h3>CSS</h3>
 *
 * <p>Styling through CSS is not applicable.
 *
 * <p><div align="center"> <img src="doc-files/WTextEdit-1.png">
 *
 * <p><strong>Default configuration of a WTextEdit</strong> </div>
 */
public class WTextEdit extends WTextArea {
  private static Logger logger = LoggerFactory.getLogger(WTextEdit.class);

  /** Creates a new text editor. */
  public WTextEdit(WContainerWidget parentContainer) {
    super();
    this.onChange_ = new JSignal(this, "change");
    this.onRender_ = new JSignal(this, "render");
    this.contentChanged_ = false;
    this.configurationSettings_ = new HashMap<String, Object>();
    this.init();
    if (parentContainer != null) parentContainer.addWidget(this);
  }
  /**
   * Creates a new text editor.
   *
   * <p>Calls {@link #WTextEdit(WContainerWidget parentContainer) this((WContainerWidget)null)}
   */
  public WTextEdit() {
    this((WContainerWidget) null);
  }
  /**
   * Creates a new text editor and initialize with given text.
   *
   * <p>The <code>text</code> should be valid XHTML.
   */
  public WTextEdit(final String text, WContainerWidget parentContainer) {
    super(text, (WContainerWidget) null);
    this.onChange_ = new JSignal(this, "change");
    this.onRender_ = new JSignal(this, "render");
    this.contentChanged_ = false;
    this.configurationSettings_ = new HashMap<String, Object>();
    this.init();
    if (parentContainer != null) parentContainer.addWidget(this);
  }
  /**
   * Creates a new text editor and initialize with given text.
   *
   * <p>Calls {@link #WTextEdit(String text, WContainerWidget parentContainer) this(text,
   * (WContainerWidget)null)}
   */
  public WTextEdit(final String text) {
    this(text, (WContainerWidget) null);
  }
  /** Destructor. */
  public void remove() {
    super.remove();
  }
  /**
   * Returns the TinyMCE version.
   *
   * <p>This returns the configured version of TinyMCE (currently 3 or 4).
   */
  public int getVersion() {
    return this.version_;
  }
  /**
   * Sets the content.
   *
   * <p>The <code>text</code> should be valid XHTML.
   *
   * <p>The default value is &quot;&quot;.
   */
  public void setText(final String text) {
    super.setText(text);
    this.contentChanged_ = true;
  }
  /**
   * Sets the stylesheet for displaying the content.
   *
   * <p>The content is rendered using the rules defined in this stylesheet. The stylesheet is also
   * used to derive additional styles that are available in the text editor, for example in the
   * &quot;styleselect&quot; button.
   *
   * <p>Multiple stylesheets may be specified as a comma separated list.
   */
  public void setStyleSheet(final String uri) {
    this.setConfigurationSetting("content_css", uri);
  }
  /**
   * Returns the content stylesheet.
   *
   * <p>
   *
   * @see WTextEdit#setStyleSheet(String uri)
   */
  public String getStyleSheet() {
    return StringUtils.asString(this.getConfigurationSetting("content_css")).toString();
  }
  /**
   * Loads additional TinyMCE plugins.
   *
   * <p>Wt loads by default only the plugin &apos;safari&apos; (which adds support for the Safari
   * web browser). Use this method to load additional plugins. Multiple plugins may be specified as
   * a comma separated list.
   *
   * <p>The various plugins are described in the <a
   * href="http://www.tinymce.com/wiki.php/Plugins">TinyMCE documentation</a>.
   *
   * <p>
   *
   * <p><i><b>Note: </b>Plugins can only be loaded before the initial display of the widget. </i>
   */
  public void setExtraPlugins(final String plugins) {
    this.setConfigurationSetting("plugins", plugins);
  }
  /**
   * Returns the extra plugins.
   *
   * <p>
   *
   * @see WTextEdit#setExtraPlugins(String plugins)
   */
  public String getExtraPlugins() {
    return StringUtils.asString(this.getConfigurationSetting("plugins")).toString();
  }
  /**
   * Configures a tool bar.
   *
   * <p>This configures the buttons for the <code>i&apos;th</code> tool bar (with 0 &lt;= <code>i
   * </code> &lt;= 3).
   *
   * <p>
   *
   * <h3>TinyMCE 3</h3>
   *
   * <p>The syntax and available buttons is documented in the <a
   * href="http://www.tinymce.com/wiki.php/Configuration3x:theme_advanced_buttons_1_n">TinyMCE
   * documentation</a>.
   *
   * <p>The default <i>config</i> for the first tool bar (<code>i</code> = 0) is: "fontselect, |,
   * bold, italic, underline, |, fontsizeselect, |, forecolor, backcolor, |, justifyleft,
   * justifycenter, justifyright, justifyfull, |, anchor, |, numlist, bullist".
   *
   * <p>By default, the other three tool bars are disabled (<code>config</code> = &quot;&quot;).
   *
   * <p>
   *
   * <h3>TinyMCE 4</h3>
   *
   * <p>The syntax and available buttons is documented in the <a
   * href="http://www.tinymce.com/wiki.php/Configuration:toolbar%3CN%3E">TinyMCEdocumentation</a>.
   *
   * <p>The default <i>config</i> for the first tool bar (<code>i</code> = 0) is: &quot;undo redo |
   * styleselect | bold italic | link&quot;.
   *
   * <p>Some buttons are only available after loading extra plugins using {@link
   * WTextEdit#setExtraPlugins(String plugins) setExtraPlugins()}.
   *
   * <p>
   *
   * <p><i><b>Note: </b>The tool bar configuration can only be set before the initial display of the
   * widget. </i>
   */
  public void setToolBar(int i, final String config) {
    String setting = "";
    if (this.version_ < 4) {
      setting = "theme_advanced_buttons";
    } else {
      setting = "toolbar";
    }
    this.setConfigurationSetting(setting + String.valueOf(i + 1), config);
  }
  /**
   * Returns a tool bar configuration.
   *
   * <p>
   *
   * @see WTextEdit#setToolBar(int i, String config)
   */
  public String getToolBar(int i) {
    String setting = "";
    if (this.version_ < 4) {
      setting = "theme_advanced_buttons";
    } else {
      setting = "toolbar";
    }
    return StringUtils.asString(this.getConfigurationSetting(setting + String.valueOf(i + 1)))
        .toString();
  }
  /**
   * Configure a TinyMCE setting.
   *
   * <p>A list of possible settings can be found at: <a
   * href="http://tinymce.moxiecode.com/wiki.php/Configuration">http://tinymce.moxiecode.com/wiki.php/Configuration</a>
   *
   * <p>The widget itself will also define a number of configuration settings and these may be
   * overridden using this method.
   */
  public void setConfigurationSetting(final String name, final Object value) {
    if ((value != null)) {
      this.configurationSettings_.put(name, value);
    } else {
      this.configurationSettings_.remove(name);
    }
  }
  /**
   * Returns a TinyMCE configuration setting&apos;s value.
   *
   * <p>An empty Any is returned when no value could be found for the provided argument.
   */
  public Object getConfigurationSetting(final String name) {
    Object it = this.configurationSettings_.get(name);
    if (it != null) {
      return it;
    } else {
      return null;
    }
  }
  /**
   * Sets the placeholder text.
   *
   * <p>This method is not supported on {@link WTextEdit} and will thrown an exception instead.
   */
  public void setPlaceholderText(final CharSequence placeholder) {
    throw new WException("WTextEdit::setPlaceholderText() is not implemented.");
  }

  public void setReadOnly(boolean readOnly) {
    super.setReadOnly(readOnly);
    if (readOnly) {
      this.setConfigurationSetting("readonly", "1");
    } else {
      this.setConfigurationSetting("readonly", null);
    }
  }

  public void propagateSetEnabled(boolean enabled) {
    super.propagateSetEnabled(enabled);
    this.setReadOnly(!enabled);
  }

  public void resize(final WLength width, final WLength height) {
    super.resize(width, height);
  }
  /**
   * Signal emitted when rendered.
   *
   * <p>A text edit is instantiated asynchronously as it depends on additional JavaScript libraries
   * and initialization. This signal is emitted when the component is initialized. The underlying
   * TinyMCE editor component is accessible as {@link WWidget#getJsRef()} + &quot;.ed&quot;.
   */
  public JSignal rendered() {
    return this.onRender_;
  }

  String renderRemoveJs(boolean recursive) {
    if (this.isRendered()) {
      String result = this.getJsRef() + ".ed.remove();";
      if (!recursive) {
        result += "Wt4_6_1.remove('" + this.getId() + "');";
      }
      return result;
    } else {
      return super.renderRemoveJs(recursive);
    }
  }

  void updateDom(final DomElement element, boolean all) {
    super.updateDom(element, all);
    if (element.getType() == DomElementType.TEXTAREA) {
      element.removeProperty(Property.StyleDisplay);
    }
    if (all && element.getType() == DomElementType.TEXTAREA) {
      StringWriter config = new StringWriter();
      config.append("{");
      boolean first = true;
      for (Iterator<Map.Entry<String, Object>> it_it =
              this.configurationSettings_.entrySet().iterator();
          it_it.hasNext(); ) {
        Map.Entry<String, Object> it = it_it.next();
        if (it.getKey().equals("plugins")) {
          continue;
        }
        if (!first) {
          config.append(',');
        }
        first = false;
        config
            .append(it.getKey())
            .append(": ")
            .append(StringUtils.asJSLiteral(it.getValue(), TextFormat.UnsafeXHTML));
      }
      if (!first) {
        config.append(',');
      }
      config.append("plugins: '").append(this.getPlugins()).append("'");
      config.append(",init_instance_callback: obj.init}");
      DomElement dummy = new DomElement(DomElement.Mode.Update, DomElementType.TABLE);
      this.updateDom(dummy, true);
      element.callJavaScript(
          "(function() { var obj = "
              + this.getJsRef()
              + ".wtObj;obj.render("
              + config.toString()
              + ","
              + jsStringLiteral(dummy.getCssStyle())
              + ","
              + (this.changed().isConnected() ? "true" : "false")
              + ");})();");
      this.contentChanged_ = false;
    }
    if (!all && this.contentChanged_) {
      element.callJavaScript(this.getJsRef() + ".ed.load();");
      this.contentChanged_ = false;
    }
  }

  protected void getDomChanges(final List<DomElement> result, WApplication app) {
    DomElement e = DomElement.getForUpdate(this.getFormName(), DomElementType.TABLE);
    this.updateDom(e, false);
    super.getDomChanges(result, app);
    result.add(e);
  }

  boolean domCanBeSaved() {
    return false;
  }

  protected int boxPadding(Orientation orientation) {
    return 0;
  }

  protected int boxBorder(Orientation orientation) {
    return 0;
  }

  private JSignal onChange_;
  private JSignal onRender_;
  private int version_;
  private boolean contentChanged_;
  private Map<String, Object> configurationSettings_;

  private String getPlugins() {
    String plugins = this.getExtraPlugins();
    if (this.version_ == 3) {
      if (plugins.length() != 0) {
        plugins += ",";
      }
      plugins += "safari";
    }
    return plugins;
  }

  private void init() {
    WApplication app = WApplication.getInstance();
    this.setInline(false);
    initTinyMCE();
    this.version_ = getTinyMCEVersion();
    this.setJavaScriptMember(
        " WTextEdit",
        "new Wt4_6_1.WTextEdit(" + app.getJavaScriptClass() + "," + this.getJsRef() + ");");
    this.setJavaScriptMember(
        WT_RESIZE_JS,
        "function(e, w, h, s) { var obj = "
            + this.getJsRef()
            + ".wtObj; obj.wtResize(e, w, h, s); };");
    String direction = app.getLayoutDirection() == LayoutDirection.LeftToRight ? "ltr" : "rtl";
    this.setConfigurationSetting("directionality", direction);
    String toolbar = "";
    if (this.version_ < 4) {
      toolbar =
          "fontselect,|,bold,italic,underline,|,fontsizeselect,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,anchor,|,numlist,bullist";
    } else {
      toolbar = "undo redo | styleselect | bold italic | link";
    }
    this.setToolBar(0, toolbar);
    for (int i = 1; i <= 3; i++) {
      this.setToolBar(i, "");
    }
    this.setConfigurationSetting("doctype", WApplication.getInstance().getDocType());
    this.setConfigurationSetting("relative_urls", true);
    if (this.version_ < 4) {
      this.setConfigurationSetting("button_tile_map", true);
      this.setConfigurationSetting("theme", "advanced");
      this.setConfigurationSetting("theme_advanced_toolbar_location", "top");
      this.setConfigurationSetting("theme_advanced_toolbar_align", "left");
    }
    this.onChange_.addListener(
        this,
        () -> {
          WTextEdit.this.propagateOnChange();
        });
  }

  private void propagateOnChange() {
    this.changed().trigger();
  }

  private static void initTinyMCE() {
    String THIS_JS = "js/WTextEdit.js";
    WApplication app = WApplication.getInstance();
    if (!app.isJavaScriptLoaded(THIS_JS)) {
      if (app.getEnvironment().hasAjax()) {
        app.doJavaScript("window.tinyMCE_GZ = { loaded: true };", false);
      }
      String tinyMCEURL = "";
      tinyMCEURL = WApplication.readConfigurationProperty("tinyMCEURL", tinyMCEURL);
      if (tinyMCEURL.length() == 0) {
        int version = getTinyMCEVersion();
        String folder = "";
        String jsFile = "";
        if (version < 3) {
          folder = "tinymce/";
          jsFile = "tinymce.js";
        } else {
          if (version == 3) {
            folder = "tiny_mce/";
            jsFile = "tiny_mce.js";
          } else {
            folder = "tinymce/";
            jsFile = "tinymce.min.js";
          }
        }
        String tinyMCEBaseURL = WApplication.getRelativeResourcesUrl() + folder;
        tinyMCEBaseURL = WApplication.readConfigurationProperty("tinyMCEBaseURL", tinyMCEBaseURL);
        if (tinyMCEBaseURL.length() != 0
            && tinyMCEBaseURL.charAt(tinyMCEBaseURL.length() - 1) != '/') {
          tinyMCEBaseURL += '/';
        }
        tinyMCEURL = tinyMCEBaseURL + jsFile;
      }
      app.require(tinyMCEURL, "window['tinyMCE']");
      app.getStyleSheet().addRule(".mceEditor", "display: block; position: absolute;");
      app.loadJavaScript(THIS_JS, wtjs1());
    }
  }

  private static int getTinyMCEVersion() {
    String version = "3";
    version = WApplication.readConfigurationProperty("tinyMCEVersion", version);
    return Integer.parseInt(version);
  }

  static WJavaScriptPreamble wtjs1() {
    return new WJavaScriptPreamble(
        JavaScriptScope.WtClassScope,
        JavaScriptObjectType.JavaScriptConstructor,
        "WTextEdit",
        "function(m,b){b.wtObj=this;var o,p,q=0,r=this,c=m.WT,s;if(!tinymce.dom.Event.domLoaded)tinymce.dom.Event.domLoaded=true;tinyMCE.init({mode:\"none\"});this.render=function(a,g,d){s=g;b.ed=new tinymce.Editor(b.id,a,tinymce.EditorManager);b.ed.render();if(d)tinymce.EditorManager.majorVersion<4?b.ed.onChange.add(function(){m.emit(b,\"change\")}):b.ed.on(\"change\",function(){m.emit(b,\"change\")});setTimeout(function(){m.emit(b,\"render\")},0)};this.init= function(){var a=c.getElement(b.id+\"_ifr\"),g,d;if(tinymce.EditorManager.majorVersion<4){d=g=a.parentNode.parentNode.parentNode.parentNode;g=g.parentNode}else g=a.parentNode.parentNode.parentNode;if(d){d.style.cssText=\"width:100%;\"+s;b.style.height=d.offsetHeight+\"px\"}g.wtResize=b.wtResize;c.isGecko?setTimeout(function(){r.wtResize(b,o,p,true)},100):r.wtResize(b,o,p,true);$((c.isIE?document.frames[a.id].document:a.contentDocument).body).bind(\"paste\",function(k){function e(n){return n.indexOf(\"image/\")== 0}var f=k.clipboardData||k.originalEvent.clipboardData,i,l;if(f&&f.types){i=0;for(l=f.types.length;i<l;++i)if(e(f.types[i])||e(f.items[i].type)){var j=f.items[i].getAsFile(),h=new FileReader;h.onload=function(){b.ed.insertContent('<img src=\"'+this.result+'\"></img>')};h.readAsDataURL(j);c.cancelEvent(k)}}})};this.wtResize=function(a,g,d){if(!(d<0)){var k=c.getElement(a.id+\"_ifr\");if(k){var e=0,f=0;e=c.px(a,\"marginLeft\")+c.px(a,\"marginRight\");f=c.px(a,\"marginTop\")+c.px(a,\"marginBottom\");if(!c.boxSizing(a)){e+= c.px(a,\"borderLeftWidth\")+c.px(a,\"borderRightWidth\")+c.px(a,\"paddingLeft\")+c.px(a,\"paddingRight\");f+=c.px(a,\"borderTopWidth\")+c.px(a,\"borderBottomWidth\")+c.px(a,\"paddingTop\")+c.px(a,\"paddingBottom\")}a.style.height=d-f+\"px\";var i;f=b.style.position!==\"absolute\";if(tinymce.EditorManager.majorVersion<4){var l=k.parentNode.parentNode,j=l.parentNode.parentNode,h,n;i=j;e=j.parentNode;if(!f&&typeof g!==\"undefined\")e.style.width=g-2+\"px\";h=0;for(n=j.rows.length;h<n;h++)if(j.rows[h]!=l)d-=j.rows[h].offsetHeight}else{l= k.parentNode;j=l.parentNode;e=j.parentNode;if(!f&&typeof g!==\"undefined\")e.style.width=g-2+\"px\";h=0;for(n=j.childNodes.length;h<n;h++)if(j.childNodes[h]!=l)d-=j.childNodes[h].offsetHeight+1;d-=1}if(d<0){if(q<10){a=Math.pow(2,q)*100;setTimeout(function(){r.wtResize(b,o,p,true)},a)}q+=1}else{d+=\"px\";if(f){e.style.position=\"static\";e.style.display=\"block\"}else{e.style.position=a.style.position;e.style.left=a.style.left;e.style.top=a.style.top;if(!f&&i)i.style.width=g+\"px\";if(i){i.style.height=d+\"px\"; e.style.height=a.style.height}}if(k.style.height!=d){q=0;k.style.height=d;m.layouts2&&m.layouts2.setElementDirty(b)}}}else{o=g;p=d}}};p=b.offsetHeight;o=b.offsetWidth}");
  }
}
