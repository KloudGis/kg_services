
sc_require('data_sources/admin');
Webadmin.START_GROUP = SC.Responder.create({

    didBecomeFirstResponder: function() {
		Webadmin.usersController.cleanUp();	
		var records = Webadmin.store.find(Webadmin.GROUPS_QUERY);
        Webadmin.groupsController.set('content', records);
		Webadmin.groupsController.rollbackChanges();			
		Webadmin.makeFirstResponder(Webadmin.READY_GROUP);
	}
});