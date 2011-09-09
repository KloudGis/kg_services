/** @namespace

  The active state when the signup dialog is showing.
  
  @extends SC.Responder
*/
Webadmin.NEW_USER = SC.Responder.create({

    // when we become first responder, always show the create panel
    didBecomeFirstResponder: function() {

        console.log("NEW USER is First responder!");
        // Create a new user and set it as the root of the signup controller
        // so that we can edit it.
        var store = this._store = Webadmin.store.chain(); // buffer changes
        var user = store.createRecord(Webadmin.User, {});
        Webadmin.newUserController.set('content', user); // for editing
        // then show the dialog
        var pane = Webadmin.getPath('newUserPage.mainPane');
        pane.append(); // show on screen
        pane.makeFirstResponder(pane.contentView.name); // focus first field
    },

    // when we lose first responder, always hide the signup panel.
    willLoseFirstResponder: function() {
        console.log("NEW USER loosing First responder!");
        // if we still have a store, then cancel first.
        if (this._store) {
            this._store.discardChanges();
            this._store = null;
        }
        Webadmin.newUserController.set('content', null); // cleanup controller
        Webadmin.newUserController.set('confirmPassword', null);
        Webadmin.getPath('newUserPage.mainPane').remove();
    },

    // called when the OK button is pressed.
    submitUser: function() {
        if (Webadmin.newUserController.isValidUser()) {
            console.log("NEW USER submit");
            this._store.commitChanges();
            this._store = null;
            // find user in global store and set as global
            var user = Webadmin.store.find(Webadmin.newUserController);
            Webadmin.usersController.selectObject(user);
            Webadmin.makeFirstResponder(Webadmin.READY_USER);
			Webadmin.store.commitRecords();
        }
    },

    // called when the Cancel button is pressed
    cancel: function() {
        console.log("NEW USER cancel");
        this._store.discardChanges();
        this._store = null;
        // reset app
        Webadmin.makeFirstResponder(Webadmin.READY_USER);
    }

});
