/** @namespace

  The active state when the signup dialog is showing.
  
  @extends SC.Responder
*/
Webadmin.NEW_GROUP = SC.Responder.create({

    // when we become first responder, always show the create panel
    didBecomeFirstResponder: function() {

        console.log("NEW_GROUP is First responder!");
        // Create a new group and set it as the root of the signup controller
        // so that we can edit it.
        var store = this._store = Webadmin.store.chain(); // buffer changes
        var group = store.createRecord(Webadmin.Group, {});
        Webadmin.newGroupController.set('content', group); // for editing
        // then show the dialog
        var pane = Webadmin.getPath('newGroupPage.mainPane');
        pane.append(); // show on screen
        pane.makeFirstResponder(pane.contentView.name); // focus first field
    },

    // when we lose first responder, always hide the signup panel.
    willLoseFirstResponder: function() {
        console.log("NEW_GROUP loosing First responder!");
        // if we still have a store, then cancel first.
        if (this._store) {
            this._store.discardChanges();
            this._store = null;
        }
        Webadmin.newGroupController.set('content', null); // cleanup controller
        Webadmin.getPath('newGroupPage.mainPane').remove();
    },

    // called when the OK button is pressed.
    submitGroup: function() {
        console.log("NEW_GROUP submit");
        this._store.commitChanges();
        this._store = null;		
        // find group in global store and set as global
        var group = Webadmin.store.find(Webadmin.newGroupController);
        Webadmin.groupsController.selectObject(group);
        Webadmin.makeFirstResponder(Webadmin.READY_GROUP);
		Webadmin.store.commitRecords();
    },

    // called when the Cancel button is pressed
    cancelGroup: function() {
        console.log("NEW_GROUP cancel");
        this._store.discardChanges();
        this._store = null;
        // reset app
        Webadmin.makeFirstResponder(Webadmin.READY_GROUP);
    }

});
