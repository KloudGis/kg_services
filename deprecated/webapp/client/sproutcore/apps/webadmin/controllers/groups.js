// ==========================================================================
// Project:   Webadmin.groupsController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Controller Here)

  @extends SC.ArrayController
*/

sc_require('data_sources/admin');
Webadmin.groupsController = SC.ArrayController.create(

SC.CollectionViewDelegate,
/** @scope Webadmin.groupsController.prototype */
{
    allowsMultipleSelection: NO,
    deleteButtonEnabled: NO,
    sheetPane: null,
    deleteRecords: null,
    deleteIndexes: null,
    datasource_message: null,
	activeName : null,

    commitChanges: function() {
        Webadmin.store.commitRecords();
		this.doSelectionChanged();	
    },

    rollbackChanges: function() {     
		Webadmin.store.find(Webadmin.GROUPS_QUERY).refresh();
		Webadmin.store.find(Webadmin.USERS_QUERY).refresh();
		Webadmin.store.find(Webadmin.PRIVILEGES_QUERY).refresh();
		this.doSelectionChanged();
    },

    cleanUp: function() {
        this.set('content', null);
        this.set('selection', null);
        Webadmin.activeMembersController.cleanUp();
        Webadmin.activePrivilegesController.cleanUp();
    },

    fetchCompleted: function() {
        this.set('commitCancelButtonEnabled', NO);
    },

    datasourceStatus: function(errCode, errContent) {
        if (errCode < 205) {
            this.set('datasource_message', '');
        } else {
            //console.log(errContent);
            this.set('datasource_message', errContent.message);
        }
    },

    changeMade: function() {
         this.commitChanges();
    },

    selectionDidChange: function() {
        this.commitChanges();      
    }.observes('selection'),

    doSelectionChanged: function() {
        var _group = this.getSelectedGroup();
        var delButton = Webadmin.mainPage.getPath('mainPane.mainView.deleteButton');
        if (_group) {
			this.activeName = _group.get('name');
            this.set('deleteButtonEnabled', YES);
            this.loadMembers(_group);
			this.loadPrivileges(_group);
        } else {
			this.activeName = null;
            this.set('deleteButtonEnabled', NO);
            Webadmin.activeMembersController.set('content', null);
            Webadmin.activePrivilegesController.set('content', null);
        }
    },

    loadMembers: function(_group) {
        if (_group) {
            var _members = _group.get('members');
            if (_members) {
                Webadmin.activeMembersController.set('content', _members);
            } else {
                Webadmin.activeMembersController.set('content', null);
            }
        } else {
            Webadmin.activeMembersController.set('content', null);
        }
    },

	loadPrivileges: function(_group) {
        if (_group) {
            var _privileges = _group.get('privileges');
            if (_privileges) {
                Webadmin.activePrivilegesController.set('content', _privileges);
            } else {
                Webadmin.activePrivilegesController.set('content', null);
            }
        } else {
            Webadmin.activePrivilegesController.set('content', null);
        }
    },

    //to handle delete
    collectionViewDeleteContent: function(view, content, indexes) {
        // destroy the records
        var records = indexes.map(function(idx) {
            return this.objectAt(idx);
        },
        this);
        this.set('deleteRecords', records);
        this.set('deleteIndexes', indexes);
        this.confirmDelete();
    },

    //shown on the bottom gradient bar
    summary: function() {
        var len = this.get('length'),
        ret;
        if (len && len > 0) {
            ret = len === 1 ? "_groups.1group".loc() : "_groups.MultipleGroups".loc(len);
        } else ret = "_groups.NoGroup".loc();
        return ret;
    }.property('length').cacheable(),

    //make sure the active group is visible in the list
    scrollToSelection: function() {
        var _group = this.getSelectedGroup();
        if (_group) {
            this.invokeLater(function() {
                var list = Webadmin.groupsView.getPath('managerView.groupScrollView.contentView');
                list.scrollToContentIndex(this.indexOf(_group));
            });
        }
    },

    //get the selected group in the list
    getSelectedGroup: function() {
        var _sel = this.get('selection');
        if (_sel) {
            var _group = _sel.get('firstObject');
            return _group;
        }
        return NO;
    },

    addGroup: function() {
        Webadmin.makeFirstResponder(Webadmin.NEW_GROUP);
    },

    deleteGroup: function() {
        this.collectionViewDeleteContent(null, this, this.get('selection').indexSetForSource(this));
    },

    //called from the confirm dialog
    performDelete: function() {
        if (this.sheetPane) {
            this.sheetPane.remove();
            this.sheetPane = null;
        }
        if (this.deleteRecords && this.deleteRecords.get('length') > 0) {
            var indexes = this.deleteIndexes;
            this.deleteRecords.invoke('destroy');
            var selIndex = indexes.get('min') - 1;
            if (selIndex < 0) selIndex = 0;
            this.selectObject(this.objectAt(selIndex));
            this.deleteRecords = null;
            this.deleteIndexes = null;
            this.commitChanges();
        }
    },

    confirmDelete: function() {
        if (this.deleteRecords && this.deleteRecords.get('length') > 0) {
            var _mess = this.deleteRecords.get('firstObject').get('name');
            if (this.deleteRecords.get('length') > 1) {
                _mess = "_groups.Multiple (%@)".loc(this.deleteRecords.get('length'));
            }
            var sheet = SC.SheetPane.create({
                layout: {
                    width: 430,
                    height: 150,
                    centerX: 0,
                },
                contentView: SC.View.extend({
                    layout: {
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 0
                    },
                    childViews: 'iconAlertView labelView yesButtonView noButtonView'.w(),

                    iconAlertView: SC.ImageView.extend({
                        layout: {
                            centerY: -10,
                            height: 48,
                            left: 20,
                            width: 48
                        },
                        value: "sc-icon-alert-48",
                    }),

                    labelView: SC.LabelView.extend({
                        layout: {
                            centerY: 0,
                            height: 40,
                            left: 20,
                            right: 0
                        },
                        textAlign: SC.ALIGN_CENTER,
                        classNames: 'messages'.w(),
                        value: "_groups.ConfirmDeleteGroup".loc(_mess),
                    }),

                    yesButtonView: SC.ButtonView.extend({
                        layout: {
                            width: 80,
                            bottom: 20,
                            height: 24,
                            centerX: -50
                        },
                        title: "_Delete".loc(),
                        action: "performDelete",
                        target: "Webadmin.groupsController"
                    }),
                    noButtonView: SC.ButtonView.extend({
                        layout: {
                            width: 80,
                            bottom: 20,
                            height: 24,
                            centerX: 50
                        },
                        title: "_Cancel".loc(),
                        isDefault: YES,
                        isCancel: YES,
                        action: "remove",
                        target: "Webadmin.groupsController.sheetPane"
                    })
                })
            });
            sheet.append();
            this.set('sheetPane', sheet);
            return YES;
        }
        return NO;
    }
});
