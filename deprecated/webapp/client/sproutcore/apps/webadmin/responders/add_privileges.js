/** @namespace

  The active state when the signup dialog is showing.
  
  @extends SC.Responder
*/
Webadmin.ADD_PRIVILEGES = SC.Responder.create({

    // when we become first responder, always show the create panel
    didBecomeFirstResponder: function() {

        console.log("ADD PRIVILEGES is First responder!");
        // Create a new user and set it as the root of the signup controller
        // so that we can edit it.
    	var allPrivileges = Webadmin.store.find(Webadmin.Privilege);
		var privileges = Webadmin.activePrivilegesController;
		var privsNotIn = allPrivileges.filter(function(priv){
			if(SC.isEqual(privileges.indexOf(priv), -1)){
				return YES;
			}
			return NO;
		});
        Webadmin.addPrivilegesController.set('content', privsNotIn); // for editing
		Webadmin.addPrivilegesController.set('selection',null); 
        // then show the dialog
        var pane = Webadmin.getPath('addPrivilegesPage.mainPane');
        pane.append(); // show on screen
        pane.makeFirstResponder(pane.contentView.okbutton);
    },

    // when we lose first responder, always hide the signup panel.
    willLoseFirstResponder: function() {
        console.log("ADD PRIVILEGES loosing First responder!");
        // if we still have a store, then cancel first.
        Webadmin.addPrivilegesController.set('content', null); // cleanup controller
		Webadmin.addPrivilegesController.set('selection',null); 
        Webadmin.getPath('addPrivilegesPage.mainPane').remove();
    },

    // called when the OK button is pressed.
    addPrivileges: function() {
		var selection = Webadmin.addPrivilegesController.get('selection');
		if(selection && selection.get('firstObject')){
			var _group = Webadmin.activeGroupController.get('content');
			selection.forEach(function(priv){
				if(priv){
					console.log("Active group:" + _group);
					console.log("adding privilege:" + priv);
					priv.addGroup(_group);	
					console.log("Active group:" + _group);				
				}
			})
			Webadmin.groupsController.changeMade();
			Webadmin.groupsController.loadPrivileges(_group);
		}
    	Webadmin.makeFirstResponder(Webadmin.READY_GROUP);
    },

    // called when the Cancel button is pressed
    cancelAddPrivileges: function() {
        Webadmin.makeFirstResponder(Webadmin.READY_GROUP);
    }

});
