/** @namespace

  The default responder state.  This is the normal state while we wait for the
  user to click on the signup button.
  
  @extends SC.Responder
*/
Webadmin.READY_USER = SC.Responder.create({

    didBecomeFirstResponder: function() {
		console.log('READY USER Responder active!');
        Webadmin.activeAppController.set('activePage', "Webadmin.usersView");
		Webadmin.activeAppController.set('activeTitle', "_users.Title".loc());
	}
});
