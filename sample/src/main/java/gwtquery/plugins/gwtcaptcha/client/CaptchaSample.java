package gwtquery.plugins.gwtcaptcha.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Example code for the GwtQuery Captcha plugin.
 */
public class CaptchaSample implements EntryPoint {

  public void onModuleLoad() {
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
    
  }
}
