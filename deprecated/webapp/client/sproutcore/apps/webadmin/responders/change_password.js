
/** @namespace

  The active state when the signup dialog is showing.
  
  @extends SC.Responder
*/
Webadmin.CHANGE_PASSWORD = SC.Responder.create({

    // when we become first responder, always show the create panel
    didBecomeFirstResponder: function() {

        console.log("CHANGE_PASSWORD is First responder!");
        var store = this._store = Webadmin.store.chain(); // buffer changes
        var user = store.find(Webadmin.activeUserController.get('content'));//working copy
		user.set('password', "");
        Webadmin.changePasswordController.set('content', user); // for editing
        // then show the dialog
        var pane = Webadmin.getPath('changePasswordPage.mainPane');
        pane.append(); // show on screen
        pane.makeFirstResponder(pane.contentView.name); // focus first field
    },

    // when we lose first responder, always hide the signup panel.
    willLoseFirstResponder: function() {
        console.log("CHANGE_PASSWORD loosing First responder!");
        // if we still have a store, then cancel first.
        if (this._store) {
            this._store.discardChanges();
            this._store = null;
        }
        Webadmin.changePasswordController.set('content', null); // cleanup controller
		Webadmin.changePasswordController.set('confirmPassword', null);
        Webadmin.getPath('changePasswordPage.mainPane').remove();
    },

    // called when the OK button is pressed.
    submitPassword: function() {
        if (Webadmin.changePasswordController.isValidUser()) {
            console.log("CHANGE_PASSWORD submit");
            this._store.commitChanges();
            this._store = null;
            // find user in global store and set as global
            Webadmin.makeFirstResponder(Webadmin.READY_USER);
        }
    },

    // called when the Cancel button is pressed
    cancel: function() {
        console.log("CHANGE_PASSWORD cancel");
        this._store.discardChanges();
        this._store = null;
        // reset app
        Webadmin.makeFirstResponder(Webadmin.READY_USER);
    }

});
