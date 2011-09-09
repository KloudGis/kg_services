/** @namespace

  The default responder state.  This is the normal state while we wait for the
  user to click on the signup button.
  
  @extends SC.Responder
*/
Webadmin.READY_GROUP = SC.Responder.create({

    didBecomeFirstResponder: function() {
		console.log('READY GROUP Responder active!');
        Webadmin.activeAppController.set('activePage', "Webadmin.groupsView");
		Webadmin.activeAppController.set('activeTitle', "_groups.Title".loc());		
	}

});
