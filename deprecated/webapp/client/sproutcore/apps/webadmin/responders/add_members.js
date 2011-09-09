/** @namespace

  The active state when the signup dialog is showing.
  
  @extends SC.Responder
*/
Webadmin.ADD_MEMBERS = SC.Responder.create({

    // when we become first responder, always show the create panel
    didBecomeFirstResponder: function() {

        console.log("ADD MEMBERS is First responder!");
        // Create a new user and set it as the root of the signup controller
        // so that we can edit it.
    	var users = Webadmin.store.find(Webadmin.User);
		var members = Webadmin.activeMembersController;
		var usersNotIn = users.filter(function(user){
			if(SC.isEqual(members.indexOf(user), -1)){
				return YES;
			}
			return NO;
		});
        Webadmin.addMembersController.set('content', usersNotIn); // for editing
		Webadmin.addMembersController.set('selection',null); 
        // then show the dialog
        var pane = Webadmin.getPath('addMembersPage.mainPane');
        pane.append(); // show on screen
        pane.makeFirstResponder(pane.contentView.okbutton);
    },

    // when we lose first responder, always hide the signup panel.
    willLoseFirstResponder: function() {
        console.log("ADD MEMBERS loosing First responder!");
        // if we still have a store, then cancel first.
        Webadmin.addMembersController.set('content', null); // cleanup controller
        Webadmin.getPath('addMembersPage.mainPane').remove();
    },

    // called when the OK button is pressed.
    addMembers: function() {
		var selection = Webadmin.addMembersController.get('selection');
		if(selection && selection.get('firstObject')){
			var _group = Webadmin.activeGroupController.get('content');
			selection.forEach(function(user){
				if(user){
					console.log("Active group:" + _group);
					console.log("adding member:" + user);
					user.setGroup(_group);	
					console.log("Active group:" + _group);				
				}
			})
			Webadmin.groupsController.changeMade();
			Webadmin.groupsController.loadMembers(_group);
		}
    	Webadmin.makeFirstResponder(Webadmin.READY_GROUP);
    },

    // called when the Cancel button is pressed
    cancelAddMembers: function() {
        Webadmin.makeFirstResponder(Webadmin.READY_GROUP);
    }

});
