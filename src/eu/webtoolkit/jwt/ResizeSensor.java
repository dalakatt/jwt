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

class ResizeSensor {
  private static Logger logger = LoggerFactory.getLogger(ResizeSensor.class);

  public static void applyIfNeeded(WWidget w) {
    if (w.getJavaScriptMember(WWidget.WT_RESIZE_JS).length() != 0) {
      WApplication app = WApplication.getInstance();
      loadJavaScript(app);
      w.setJavaScriptMember(" ResizeSensor", "");
      w.setJavaScriptMember(
          " ResizeSensor", "new Wt4_6_1.ResizeSensor(Wt4_6_1," + w.getJsRef() + ")");
    }
  }

  public static void loadJavaScript(WApplication app) {
    app.loadJavaScript("js/ResizeSensor.js", wtjs1());
  }

  static WJavaScriptPreamble wtjs1() {
    return new WJavaScriptPreamble(
        JavaScriptScope.WtClassScope,
        JavaScriptObjectType.JavaScriptConstructor,
        "ResizeSensor",
        "function(c,a){var f=window.requestAnimationFrame||window.mozRequestAnimationFrame||window.webkitRequestAnimationFrame||function(b){return window.setTimeout(b,20)};a.resizeSensor=document.createElement(\"div\");a.resizeSensor.className=\"resize-sensor\";a.resizeSensor.style.cssText=\"position: absolute; left: 0; top: 0; right: 0; bottom: 0; overflow: hidden; z-index: -1; visibility: hidden;\";a.resizeSensor.innerHTML='<div class=\"resize-sensor-expand\" style=\"position: absolute; left: 0; top: 0; right: 0; bottom: 0; overflow: hidden; z-index: -1; visibility: hidden;\"><div style=\"position: absolute; left: 0; top: 0; transition: 0s;\"></div></div><div class=\"resize-sensor-shrink\" style=\"position: absolute; left: 0; top: 0; right: 0; bottom: 0; overflow: hidden; z-index: -1; visibility: hidden;\"><div style=\"position: absolute; left: 0; top: 0; transition: 0s; width: 200%; height: 200%\"></div></div>'; a.appendChild(a.resizeSensor);if(c.css(a,\"position\")==\"static\")a.style.position=\"relative\";var g=a.resizeSensor.childNodes[0],m=g.childNodes[0],i=a.resizeSensor.childNodes[1],n=true,e=0,h=function(){if(n)if(a.offsetWidth===0&&a.offsetHeight===0)e||(e=f(function(){e=0;h()}));else n=false;m.style.width=\"100000px\";m.style.height=\"100000px\";g.scrollLeft=1E5;g.scrollTop=1E5;i.scrollLeft=1E5;i.scrollTop=1E5};a.resizeSensor.trigger=function(){var b=j,d=k;if(!c.boxSizing(a)){d-=c.px(a,\"borderTopWidth\");d-= c.px(a,\"borderBottomWidth\");d-=c.px(a,\"paddingTop\");d-=c.px(a,\"paddingBottom\");b-=c.px(a,\"borderLeftWidth\");b-=c.px(a,\"borderRightWidth\");b-=c.px(a,\"paddingLeft\");b-=c.px(a,\"paddingRight\")}a.wtResize&&a.wtResize(a,b,d,false)};h();var l=false,j,k,o=function(){if(l){a.resizeSensor.trigger();l=false}f(o)};f(o);var p,q,r=function(){if((p=a.offsetWidth)!=j||(q=a.offsetHeight)!=k){l=true;j=p;k=q}h()},t=function(b,d,s){b.attachEvent?b.attachEvent(\"on\"+d,s):b.addEventListener(d,s)};t(g,\"scroll\",r);t(i,\"scroll\", r);e=f(function(){e=0;h()})}");
  }
}
