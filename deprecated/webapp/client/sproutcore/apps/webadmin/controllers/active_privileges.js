// ==========================================================================
// Project:   Webadmin.activePrivilegesController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Webadmin.activePrivilegesController = SC.ArrayController.create(

SC.CollectionViewDelegate,
/** @scope Webadmin.usersController.prototype */
{
	allowsMultipleSelection: YES,
	
	deleteRecords: null,
    deleteIndexes: null,
	removePrivilegesEnabled: NO,

	addPrivileges : function(){
		 Webadmin.makeFirstResponder(Webadmin.ADD_PRIVILEGES);
	},
	
	removePrivileges: function(){
		this.collectionViewDeleteContent(null, this, this.get('selection').indexSetForSource(this));
	},	                
	
	cleanUp: function() {
        this.set('content', null);
		this.set('selection', null);
    },	
	
	selectionChanged : function(){
		if(this.get('selection') && this.get('firstObject')){
			this.set('removePrivilegesEnabled', YES);
		}else{
			this.set('removePrivilegesEnabled', NO);
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
		this.deleteRecords.forEach(function(privilege) {
			Webadmin.activePrivilegesController.removeObject(privilege);
			privilege.removeGroup(Webadmin.activeGroupController.get('content'));
			Webadmin.groupsController.changeMade();
		});
        if (selIndex < 0) selIndex = 0;
        this.selectObject(this.objectAt(selIndex));
        this.deleteRecords = null;
        this.deleteIndexes = null;
	},
	
	summary: function() {
        return '_groups.Privileges'.loc(this.get('length'));
    }.property('length').cacheable(),
});