/*
 * Copyright 2013, Manuel Carrasco Moñino (manolo@apache.org, @dodotis).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package gwtquery.plugins.gwtcaptcha.client;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A Client side Captcha widget for GWT.
 * 
 * It depends on the gwtQuery library.
 *
 * Code Example:
 * <pre>
    HorizontalPanel panel = new HorizontalPanel();
    RootPanel.get().add(panel);
    
    final Captcha captcha = new Captcha("mycaptcha");
    Button button = new Button("Validate");
    
    panel.add(captcha);
    panel.add(button);
    
    button.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        Window.alert(captcha.validate() ? "Valid captcha" : "Invalid captcha, try again");
      }
    }); 
 * </pre>
 * 
 * @author Manuel Carrasco Moñino.
 *
 */
public class Captcha extends FlowPanel {
  
  private String RELOAD_ICON = "\u267B", value, fontfamily = "arial";;
  private int width, height, fontsize = 24, vLength = 5;
  private CssColor bg, fg;
  private TextBox textbox;
  private Context2d g;
  
  private void computeBgFgColors() {
    int r = rnd(255);
    int g = rnd(255);
    int b = rnd(255);
    bg = CssColor.make(r, g, b);
    double i = (r << 16 | g << 8 | b);
    fg = CssColor.make((0xffffff / 2) < i ? "#111111" : "#ffffff");
  }

  public boolean validate() {
    String u = textbox.getValue().replaceAll("[Oo]+", "0");
    String v = value.replaceAll("[Oo]+", "0");
    boolean r = v.equalsIgnoreCase(u);
    if (!r) {
      setFocus(true);
    }
    return r;
  }
  
  private boolean rnd() {
    return Random.nextBoolean();
  }

  private int rnd(int n) {
    return n > 0 ? Random.nextInt(n) : Random.nextInt();
  }

  private void computeValue() {
    byte[] b = Long.toString(rnd(0), 36).toLowerCase().replace("-", "").substring(0, vLength).getBytes();
    for (int i = 0; i < b.length; i++) {
      byte x = b[i];
      if (x > 96 && x < 123 && rnd()){
        b[i] -= 32;
      }
    }
    value = new String(b);
    fontsize = width / (vLength - 1);
  }

  private void rectangle(double x, double y, double w, double h) {
    g.setFillStyle(bg);
    g.fillRect(0, 0, w, h);
  }

  private void line(double x1, double y1, double x2, double y2) {
    g.setStrokeStyle(getRandomColor());
    g.beginPath();
    g.moveTo(x1, y1);
    g.lineTo(x2, y2);
    g.stroke();
    g.closePath();
  }
  
  private void text() {
    g.setFillStyle(fg);
    int w = (int) g.measureText(value).getWidth();
    int a = fontsize;
    int x = width / 2 - w / 2;
    int y = height / 2 + a / 2 - 2;
    int l = value.length();
    int sp = 3;
    int xp = Math.max(sp, x - sp * (l - 1));
    for (int c = 0; c < l; c++) {
      double rotate = rnd(6) * (rnd() ? -1 : 1);
      double r = Math.toRadians(rotate); 
      g.rotate(r);
      String ch = String.valueOf(value.charAt(c));
      int ht = rnd(10);
      g.fillText(ch, xp, y + (rnd(0) % 2 == 0 ? -ht : ht));
      g.rotate(-r);
      xp += g.measureText(ch).getWidth() + sp;
    }
    g.fill();
  }

  private CssColor getRandomColor() {
    return CssColor.make(rnd(255), rnd(255), rnd(255));
  }

  public Captcha(String id, int width, int height) {
    this.width = width;
    this.height = height;
    setStyleName("gwt-Captcha");
    if (id != null) {
      Panel p = RootPanel.get(id);
      if (p != null) {
        p.add(this);
      }
    }
    initCanvas();
    draw();
  }

  public Captcha(String id) {
    this(id, 100, 50);
  }
  
  public Captcha() {
    this(null);
  }
  
  public void reset() {
    draw();
  }

  private void draw() {
    computeBgFgColors();
    computeValue();
    
    rectangle(0, 0, width, height);

    g.setFont(fontsize + "px " + fontfamily);
    text();
    
    int d = rnd(20);
    int w = (int) g.measureText(value).getWidth();

    for (int i = 0; i < height; i += rnd(10)) {
      line(-w,  d - i, width + w, 2 * (height + d - i));
      line(0, i, width, i);
    }
    
  }

  public void initCanvas() {
    Canvas canvas = Canvas.createIfSupported();
    assert canvas != null : "browser doesn't support canvas";
    
    canvas.setStyleName("gwt-captcha");
    canvas.setWidth(width + "px");
    canvas.setCoordinateSpaceWidth(width);
    canvas.setHeight(height + "px");
    canvas.setCoordinateSpaceHeight(height);
    
    InlineHTML button = new InlineHTML(RELOAD_ICON);
    textbox = new TextBox();
    textbox.setMaxLength(vLength);
    textbox.setName("gwt-captcha");
    
    button.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        draw();
        textbox.setValue("");
        setFocus(true);
      }
    });
    
    add(textbox);
    add(button);
    add(canvas);
    
    $(canvas).css($$("box-shadow: -1px 1px 8px brown; border-radius: 7px; border: 1px solid white; float: right; min-height:" + height + "px; min-width:" + width + "px" ));
    $(button).css($$("cursor: pointer; color: brown; font-size: 25px; float: right; padding: " + (height/2) + "px 10px 1px 10px"));
    int h = $(textbox).height() + 4;
    $(textbox).css($$("box-shadow: -1px 1px 8px brown; margin-right: 10px; width: 5em; margin-top: " + (height-h) + "px"));
    g =  canvas.getContext2d();
  }

  public void setFocus(boolean b) {
    getTextBox().setFocus(true);
  }
  
  public TextBox getTextBox() {
    return textbox;
  }
}
