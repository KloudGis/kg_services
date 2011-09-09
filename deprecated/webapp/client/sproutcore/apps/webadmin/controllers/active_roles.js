// ==========================================================================
// Project:   Webadmin.activeRolesController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Webadmin.activeRolesController = SC.ArrayController.create(

SC.CollectionViewDelegate,
/** @scope Webadmin.activeRolesController.prototype */
{
    allowsMultipleSelection: YES,

    deleteRecords: null,
    deleteIndexes: null,
    removeRoleEnabled: NO,

    addRole: function() {
        Webadmin.makeFirstResponder(Webadmin.ADD_ROLES);
    },

    removeRole: function() {
        this.collectionViewDeleteContent(null, this, this.get('selection').indexSetForSource(this));
    },

    cleanUp: function() {
        this.set('content', null);
        this.set('selection', null);
    },

    selectionChanged: function() {
        if (this.get('selection') && this.get('firstObject')) {
            this.set('removeRoleEnabled', YES);
        } else {
            this.set('removeRoleEnabled', NO);
        }
    }.observes('selection'),

    //to handle remove
    collectionViewDeleteContent: function(view, content, indexes) {
        if (!SC.none(indexes)) {
            var records = indexes.map(function(idx) {
                return this.objectAt(idx);
            },
            this);
            this.set('deleteRecords', records);
            this.set('deleteIndexes', indexes);
            this.confirmDelete();
        }
    },

    confirmDelete: function() {
        var selIndex = this.deleteIndexes.get('min') - 1;
        this.deleteRecords.forEach(function(role) {
            Webadmin.activeRolesController.removeObject(role);
            role.destroy();
        });
        Webadmin.activeUserController.userChanged();
        if (selIndex < 0) selIndex = 0;
        this.selectObject(this.objectAt(selIndex));
        this.deleteRecords = null;
        this.deleteIndexes = null;
    },

    summary: function() {
        return '_users.Roles'.loc(this.get('length'));
    }.property('length').cacheable(),
});
