
Webadmin.START_USER = SC.Responder.create({

	firstTime : YES,

    didBecomeFirstResponder: function() {		
		Webadmin.makeFirstResponder(Webadmin.READY_USER);		
		var query = Webadmin.store.find(Webadmin.USERS_QUERY);
		if(this.firstTime){
			this.set('firstTime', NO);
		}else{
			query.refresh();
		}
        Webadmin.usersController.set('content', query);
		Webadmin.groupsController.cleanUp();		
	}
});