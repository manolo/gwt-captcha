

This is a captcha widget for GWT which not depends on server side.

Since it is code run in the client side it could have security issues, 
but the agressive obfuscation of GWT makes the javascript code difficult
to hack.

When validation is failed captcha is recomputed so as we protect against
brute force.

The captcha image is shown in a canvas, so it is available in most browsers
but ie6-8.

This widget depends on the gwtQuery library.

Example code:



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

