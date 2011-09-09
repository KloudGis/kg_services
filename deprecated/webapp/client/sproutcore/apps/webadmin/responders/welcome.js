

Webadmin.WELCOME = SC.Responder.create({

    // when we become first responder, always show the create panel
    didBecomeFirstResponder: function() {
        console.log("WELCOME is First responder!");
        Webadmin.activeAppController.set('activePage', "Webadmin.welcomeView");
		Webadmin.activeAppController.set('activeTitle', "Welcome");
    },

    // when we lose first responder, always hide the signup panel.
    willLoseFirstResponder: function() {
        console.log("WELCOME loosing First responder!");
    },
});