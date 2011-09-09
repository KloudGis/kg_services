Webadmin.activeMembersController = SC.ArrayController.create(

SC.CollectionViewDelegate,
/** @scope Webadmin.usersController.prototype */
{
	allowsMultipleSelection: YES,
	
	deleteRecords: null,
    deleteIndexes: null,
	removeMembersEnabled: NO,

	addMembers : function(){
		 Webadmin.makeFirstResponder(Webadmin.ADD_MEMBERS);
	},
	
	removeMembers: function(){
		this.collectionViewDeleteContent(null, this, this.get('selection').indexSetForSource(this));
	},
	
	cleanUp: function() {
        this.set('content', null);
		this.set('selection', null);
    },
	
	
	selectionChanged : function(){
		if(this.get('selection') && this.get('firstObject')){
			this.set('removeMembersEnabled', YES);
		}else{
			this.set('removeMembersEnabled', NO);
		}
	}.observes('selection'),

	//to handle remove
    collectionViewDeleteContent: function(view, content, indexes) {
        var records = indexes.map(function(idx) {
            return this.objectAt(idx);
        },
        this);
        this.set('deleteRecords', records);
        this.set('deleteIndexes', indexes);
        this.confirmDelete();
    },

	
	confirmDelete: function(){		
		var selIndex = this.deleteIndexes.get('min') - 1;
		this.deleteRecords.forEach(function(user) {
			console.log("Removing from group:" + user.get('group'));
			console.log("member:" + user);
			Webadmin.activeMembersController.removeObject(user);
			user.setGroup(null);
			Webadmin.groupsController.changeMade();
		});
        if (selIndex < 0) selIndex = 0;
        this.selectObject(this.objectAt(selIndex));
        this.deleteRecords = null;
        this.deleteIndexes = null;
	},
	
	summary: function() {
        return '_groups.Members'.loc(this.get('length'));
    }.property('length').cacheable(),
	
});