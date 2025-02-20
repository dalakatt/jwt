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
 * Theme based on the Twitter Bootstrap 2 CSS framework.
 *
 * <p>This theme implements support for building a JWt web application that uses Twitter Bootstrap
 * as a theme for its (layout and) styling. The theme comes with CSS from Bootstrap version 2.2.2.
 * Only the CSS components of twitter bootstrap are used, but not the JavaScript (i.e. the
 * functional parts), since the functionality is already built-in to the widgets from the library.
 *
 * <p>Using this theme, various widgets provided by the library are rendered using markup that is
 * compatible with Twitter Bootstrap. The bootstrap theme is also extended with a proper
 * (compatible) styling of widgets for which bootstrap does not provide styling (table views, tree
 * views, sliders, etc...).
 *
 * <p>By default, the theme will use CSS resources that are shipped together with the JWt
 * distribution, but since the Twitter Bootstrap CSS API is a popular API for custom themes, you can
 * easily replace the CSS with custom-built CSS (by reimplementing {@link
 * WBootstrap2Theme#getStyleSheets() getStyleSheets()}).
 *
 * <p>Although this theme facilitates the use of Twitter Bootstrap with JWt, it is still important
 * to understand how Bootstrap expects markup to be, especially related to layout using its grid
 * system, for which we refer to the official bootstrap documentation, see <a
 * href="http://getbootstrap.com">http://getbootstrap.com</a>
 *
 * <p>
 *
 * @see WApplication#setTheme(WTheme theme)
 */
public class WBootstrap2Theme extends WTheme {
  private static Logger logger = LoggerFactory.getLogger(WBootstrap2Theme.class);

  /** Constructor. */
  public WBootstrap2Theme() {
    super();
    this.responsive_ = false;
  }
  /**
   * Enables responsive features.
   *
   * <p>Responsive features can be enabled only at application startup.
   *
   * <p>Responsive features are disabled by default.
   */
  public void setResponsive(boolean enabled) {
    this.responsive_ = enabled;
  }
  /**
   * Returns whether responsive features are enabled.
   *
   * <p>
   *
   * @see WBootstrap2Theme#setResponsive(boolean enabled)
   */
  public boolean isResponsive() {
    return this.responsive_;
  }

  public String getName() {
    return "bootstrap2";
  }

  public String getResourcesUrl() {
    return WApplication.getRelativeResourcesUrl() + "themes/bootstrap/2/";
  }

  public List<WLinkedCssStyleSheet> getStyleSheets() {
    List<WLinkedCssStyleSheet> result = new ArrayList<WLinkedCssStyleSheet>();
    final String themeDir = this.getResourcesUrl();
    result.add(new WLinkedCssStyleSheet(new WLink(themeDir + "bootstrap.css")));
    if (this.responsive_) {
      result.add(new WLinkedCssStyleSheet(new WLink(themeDir + "bootstrap-responsive.css")));
    }
    result.add(new WLinkedCssStyleSheet(new WLink(themeDir + "wt.css")));
    return result;
  }

  public void init(WApplication app) {
    app.getBuiltinLocalizedStrings().useBuiltin(WtServlet.BootstrapTheme_xml);
  }

  public void apply(WWidget widget, WWidget child, int widgetRole) {
    if (!widget.isThemeStyleEnabled()) {
      return;
    }
    switch (widgetRole) {
      case WidgetThemeRole.MenuItemIcon:
        child.addStyleClass("Wt-icon");
        break;
      case WidgetThemeRole.MenuItemCheckBox:
        child.setStyleClass("Wt-chkbox");
        ((WFormWidget) child).getLabel().addStyleClass("checkbox-inline");
        break;
      case WidgetThemeRole.MenuItemClose:
        {
          child.addStyleClass("close");
          ((WText) child).setText("&times;");
          break;
        }
      case WidgetThemeRole.DialogCoverWidget:
        child.addStyleClass("modal-backdrop Wt-bootstrap2");
        break;
      case WidgetThemeRole.DialogTitleBar:
        child.addStyleClass("modal-header");
        break;
      case WidgetThemeRole.DialogBody:
        child.addStyleClass("modal-body");
        break;
      case WidgetThemeRole.DialogFooter:
        child.addStyleClass("modal-footer");
        break;
      case WidgetThemeRole.DialogCloseIcon:
        {
          child.addStyleClass("close");
          ((WText) child).setText("&times;");
          break;
        }
      case WidgetThemeRole.TableViewRowContainer:
        {
          WAbstractItemView view = (WAbstractItemView) widget;
          child.toggleStyleClass("Wt-striped", view.hasAlternatingRowColors());
          break;
        }
      case WidgetThemeRole.DatePickerPopup:
        child.addStyleClass("Wt-datepicker");
        break;
      case WidgetThemeRole.DatePickerIcon:
        {
          WImage icon = ((child) instanceof WImage ? (WImage) (child) : null);
          icon.setImageLink(new WLink(WApplication.getRelativeResourcesUrl() + "date.gif"));
          icon.setVerticalAlignment(AlignmentFlag.Middle);
          icon.resize(new WLength(16), new WLength(16));
          break;
        }
      case WidgetThemeRole.TimePickerPopup:
        child.addStyleClass("Wt-timepicker");
        break;
      case WidgetThemeRole.PanelTitleBar:
        child.addStyleClass("accordion-heading");
        break;
      case WidgetThemeRole.PanelCollapseButton:
      case WidgetThemeRole.PanelTitle:
        child.addStyleClass("accordion-toggle");
        break;
      case WidgetThemeRole.PanelBody:
        child.addStyleClass("accordion-inner");
        break;
      case WidgetThemeRole.InPlaceEditing:
        child.addStyleClass("input-append");
        break;
      case WidgetThemeRole.NavCollapse:
        child.addStyleClass("nav-collapse");
        break;
      case WidgetThemeRole.NavBrand:
        child.addStyleClass("brand");
        break;
      case WidgetThemeRole.NavbarForm:
        child.addStyleClass("navbar-form");
        break;
      case WidgetThemeRole.NavbarSearchForm:
        child.addStyleClass("navbar-search");
        break;
      case WidgetThemeRole.NavbarSearchInput:
        child.addStyleClass("search-query");
        break;
      case WidgetThemeRole.NavbarAlignLeft:
        child.addStyleClass("pull-left");
        break;
      case WidgetThemeRole.NavbarAlignRight:
        child.addStyleClass("pull-right");
        break;
      case WidgetThemeRole.NavbarMenu:
        child.addStyleClass("navbar-nav");
        break;
      case WidgetThemeRole.NavbarBtn:
        child.addStyleClass("btn-navbar");
        break;
    }
  }

  public void apply(WWidget widget, final DomElement element, int elementRole) {
    final boolean creating = element.getMode() == DomElement.Mode.Create;
    if (!widget.isThemeStyleEnabled()) {
      return;
    }
    {
      WPopupWidget popup = ((widget) instanceof WPopupWidget ? (WPopupWidget) (widget) : null);
      if (popup != null) {
        WDialog dialog = ((widget) instanceof WDialog ? (WDialog) (widget) : null);
        if (!(dialog != null)) {
          element.addPropertyWord(Property.Class, "dropdown-menu");
        }
      }
    }
    switch (element.getType()) {
      case A:
        {
          WPushButton btn = ((widget) instanceof WPushButton ? (WPushButton) (widget) : null);
          if (creating && btn != null) {
            element.addPropertyWord(Property.Class, "btn");
            if (btn.isDefault()) {
              element.addPropertyWord(Property.Class, "btn-primary");
            }
          }
          if (element.getProperty(Property.Class).indexOf("dropdown-toggle") != -1) {
            WMenuItem item =
                ((widget.getParent()) instanceof WMenuItem
                    ? (WMenuItem) (widget.getParent())
                    : null);
            if (!(((item.getParentMenu()) instanceof WPopupMenu
                    ? (WPopupMenu) (item.getParentMenu())
                    : null)
                != null)) {
              DomElement b = DomElement.createNew(DomElementType.B);
              b.setProperty(Property.Class, "caret");
              element.addChild(b);
            }
          }
          break;
        }
      case BUTTON:
        {
          if (creating && !widget.hasStyleClass("list-group-item")) {
            element.addPropertyWord(Property.Class, "btn");
          }
          WPushButton button = ((widget) instanceof WPushButton ? (WPushButton) (widget) : null);
          if (button != null) {
            if (creating && button.isDefault()) {
              element.addPropertyWord(Property.Class, "btn-primary");
            }
            if (button.getMenu() != null
                && element.getProperties().get(Property.InnerHTML) != null) {
              element.addPropertyWord(Property.InnerHTML, "<span class=\"caret\"></span>");
            }
            if (creating && !(button.getText().length() == 0)) {
              element.addPropertyWord(Property.Class, "with-label");
            }
            if (!button.getLink().isNull()) {
              logger.error(
                  new StringWriter()
                      .append(
                          "Cannot use WPushButton::setLink() after the button has been rendered with WBootstrapTheme")
                      .toString());
            }
          }
          break;
        }
      case DIV:
        {
          WDialog dialog = ((widget) instanceof WDialog ? (WDialog) (widget) : null);
          if (dialog != null) {
            element.addPropertyWord(Property.Class, "modal");
            return;
          }
          WPanel panel = ((widget) instanceof WPanel ? (WPanel) (widget) : null);
          if (panel != null) {
            element.addPropertyWord(Property.Class, "accordion-group");
            return;
          }
          WProgressBar bar = ((widget) instanceof WProgressBar ? (WProgressBar) (widget) : null);
          if (bar != null) {
            switch (elementRole) {
              case ElementThemeRole.MainElement:
                element.addPropertyWord(Property.Class, "progress");
                break;
              case ElementThemeRole.ProgressBarBar:
                element.addPropertyWord(Property.Class, "bar");
                break;
              case ElementThemeRole.ProgressBarLabel:
                element.addPropertyWord(Property.Class, "bar-label");
            }
            return;
          }
          WGoogleMap map = ((widget) instanceof WGoogleMap ? (WGoogleMap) (widget) : null);
          if (map != null) {
            element.addPropertyWord(Property.Class, "Wt-googlemap");
            return;
          }
          WAbstractItemView itemView =
              ((widget) instanceof WAbstractItemView ? (WAbstractItemView) (widget) : null);
          if (itemView != null) {
            element.addPropertyWord(Property.Class, "form-inline");
            return;
          }
          WNavigationBar navBar =
              ((widget) instanceof WNavigationBar ? (WNavigationBar) (widget) : null);
          if (navBar != null) {
            element.addPropertyWord(Property.Class, "navbar");
            return;
          }
          break;
        }
      case LABEL:
        {
          if (elementRole == ElementThemeRole.ToggleButtonRole) {
            WCheckBox cb = ((widget) instanceof WCheckBox ? (WCheckBox) (widget) : null);
            WRadioButton rb = null;
            if (cb != null) {
              element.addPropertyWord(Property.Class, "checkbox");
            } else {
              rb = ((widget) instanceof WRadioButton ? (WRadioButton) (widget) : null);
              if (rb != null) {
                element.addPropertyWord(Property.Class, "radio");
              }
            }
            if ((cb != null || rb != null) && widget.isInline()) {
              element.addPropertyWord(Property.Class, "inline");
            }
          }
          break;
        }
      case LI:
        {
          WMenuItem item = ((widget) instanceof WMenuItem ? (WMenuItem) (widget) : null);
          if (item != null) {
            if (item.isSeparator()) {
              element.addPropertyWord(Property.Class, "divider");
            }
            if (item.isSectionHeader()) {
              element.addPropertyWord(Property.Class, "nav-header");
            }
            if (item.getMenu() != null) {
              if (((item.getParentMenu()) instanceof WPopupMenu
                      ? (WPopupMenu) (item.getParentMenu())
                      : null)
                  != null) {
                element.addPropertyWord(Property.Class, "dropdown-submenu");
              } else {
                element.addPropertyWord(Property.Class, "dropdown");
              }
            }
          }
          break;
        }
      case INPUT:
        {
          WAbstractSpinBox spinBox =
              ((widget) instanceof WAbstractSpinBox ? (WAbstractSpinBox) (widget) : null);
          if (spinBox != null) {
            element.addPropertyWord(Property.Class, "Wt-spinbox");
            return;
          }
          WDateEdit dateEdit = ((widget) instanceof WDateEdit ? (WDateEdit) (widget) : null);
          if (dateEdit != null) {
            element.addPropertyWord(Property.Class, "Wt-dateedit");
            return;
          }
          WTimeEdit timeEdit = ((widget) instanceof WTimeEdit ? (WTimeEdit) (widget) : null);
          if (timeEdit != null) {
            element.addPropertyWord(Property.Class, "Wt-timeedit");
            return;
          }
          break;
        }
      case UL:
        {
          WPopupMenu popupMenu = ((widget) instanceof WPopupMenu ? (WPopupMenu) (widget) : null);
          if (popupMenu != null) {
            element.addPropertyWord(Property.Class, "dropdown-menu");
            if (popupMenu.getParentItem() != null
                && ((popupMenu.getParentItem().getParentMenu()) instanceof WPopupMenu
                        ? (WPopupMenu) (popupMenu.getParentItem().getParentMenu())
                        : null)
                    != null) {
              element.addPropertyWord(Property.Class, "submenu");
            }
          } else {
            WMenu menu = ((widget) instanceof WMenu ? (WMenu) (widget) : null);
            if (menu != null) {
              element.addPropertyWord(Property.Class, "nav");
              WTabWidget tabs =
                  ((menu.getParent().getParent()) instanceof WTabWidget
                      ? (WTabWidget) (menu.getParent().getParent())
                      : null);
              if (tabs != null) {
                element.addPropertyWord(Property.Class, "nav-tabs");
              }
            } else {
              WSuggestionPopup suggestions =
                  ((widget) instanceof WSuggestionPopup ? (WSuggestionPopup) (widget) : null);
              if (suggestions != null) {
                element.addPropertyWord(Property.Class, "typeahead");
              }
            }
          }
          break;
        }
      case SPAN:
        {
          WInPlaceEdit inPlaceEdit =
              ((widget) instanceof WInPlaceEdit ? (WInPlaceEdit) (widget) : null);
          if (inPlaceEdit != null) {
            element.addPropertyWord(Property.Class, "Wt-in-place-edit");
          } else {
            WDatePicker picker = ((widget) instanceof WDatePicker ? (WDatePicker) (widget) : null);
            if (picker != null) {
              element.addPropertyWord(Property.Class, "Wt-datepicker");
            }
          }
          break;
        }
      default:
        break;
    }
  }

  public String getDisabledClass() {
    return "disabled";
  }

  public String getActiveClass() {
    return "active";
  }

  public String utilityCssClass(int utilityCssClassRole) {
    switch (utilityCssClassRole) {
      case UtilityCssClassRole.ToolTipInner:
        return "tooltip-inner";
      case UtilityCssClassRole.ToolTipOuter:
        return "tooltip fade top in";
      default:
        return "";
    }
  }

  public boolean isCanStyleAnchorAsButton() {
    return true;
  }

  public void applyValidationStyle(
      WWidget widget, final WValidator.Result validation, EnumSet<ValidationStyleFlag> styles) {
    WApplication app = WApplication.getInstance();
    app.loadJavaScript("js/BootstrapValidate.js", wtjs1());
    app.loadJavaScript("js/BootstrapValidate.js", wtjs2());
    if (app.getEnvironment().hasAjax()) {
      StringBuilder js = new StringBuilder();
      js.append("Wt4_6_1.setValidationState(")
          .append(widget.getJsRef())
          .append(",")
          .append(validation.getState() == ValidationState.Valid ? 1 : 0)
          .append(",")
          .append(WString.toWString(validation.getMessage()).getJsStringLiteral())
          .append(",")
          .append(EnumUtils.valueOf(styles))
          .append(");");
      widget.doJavaScript(js.toString());
    } else {
      boolean validStyle =
          validation.getState() == ValidationState.Valid
              && styles.contains(ValidationStyleFlag.ValidStyle);
      boolean invalidStyle =
          validation.getState() != ValidationState.Valid
              && styles.contains(ValidationStyleFlag.InvalidStyle);
      widget.toggleStyleClass("Wt-valid", validStyle);
      widget.toggleStyleClass("Wt-invalid", invalidStyle);
    }
  }

  public boolean canBorderBoxElement(final DomElement element) {
    return element.getType() != DomElementType.INPUT;
  }

  private boolean responsive_;

  static WJavaScriptPreamble wtjs1() {
    return new WJavaScriptPreamble(
        JavaScriptScope.WtClassScope,
        JavaScriptObjectType.JavaScriptFunction,
        "validate",
        "function(a){var b;b=a.options?a.options.item(a.selectedIndex)==null?\"\":a.options.item(a.selectedIndex).text:a.value;b=a.wtValidate.validate(b);this.setValidationState(a,b.valid,b.message,1)}");
  }

  static WJavaScriptPreamble wtjs2() {
    return new WJavaScriptPreamble(
        JavaScriptScope.WtClassScope,
        JavaScriptObjectType.JavaScriptFunction,
        "setValidationState",
        "function(a,b,i,e){var j=b==1&&(e&2)!=0;e=b!=1&&(e&1)!=0;var d=$(a),c=\"Wt-valid\",k=\"Wt-invalid\",f=this.theme;if(typeof f===\"object\"){c=f.classes.valid;k=f.classes.invalid}d.toggleClass(c,j).toggleClass(k,e);var g,h;c=d.closest(\".control-group\");if(c.length>0){g=\"success\";h=\"error\"}else{c=d.closest(\".form-group\");if(c.length>0){g=\"has-success\";h=\"has-error\"}}if(c.length>0){if(d=c.find(\".Wt-validation-message\"))b?d.text(a.defaultTT):d.text(i); c.toggleClass(g,j).toggleClass(h,e)}if(typeof a.defaultTT===\"undefined\")a.defaultTT=a.getAttribute(\"title\")||\"\";b?a.setAttribute(\"title\",a.defaultTT):a.setAttribute(\"title\",i)}");
  }
}
