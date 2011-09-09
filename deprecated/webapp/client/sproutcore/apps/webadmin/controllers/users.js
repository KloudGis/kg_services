// ==========================================================================
// Project:   Webadmin.usersController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

/** @class

  (Document Your Controller Here)

  @extends SC.ArrayController
*/

sc_require('data_sources/admin');
Webadmin.usersController = SC.ArrayController.create(

SC.CollectionViewDelegate,
/** @scope Webadmin.usersController.prototype */
{
    allowsMultipleSelection: NO,

    deleteButtonEnabled: NO,
    sheetPane: null,
    deleteRecords: null,
    deleteIndexes: null,
    datasource_message: null,
    possibleRoles: null,

    refresh: function() {
        if (this.get('content')) {
            console.log('records refresh');
            this.get('content').refresh();
        }
    },

    cleanUp: function() {
        this.set('content', null);
        this.set('selection', null);
    },

    datasourceStatus: function(errCode, errContent) {
        if (errCode < 205) {
            this.set('datasource_message', '');
        } else {
            //console.log(errContent);
            this.set('datasource_message', errContent.message);
        }
    },

    selectionDidChange: function() {
        var _user = this.getSelectedUser();
        //console.log("Users selection changed:" + _user);
        var delButton = Webadmin.mainPage.getPath('mainPane.mainView.deleteButton');
        if (_user) {
            this.set('deleteButtonEnabled', YES);
        } else {
            this.set('deleteButtonEnabled', NO);
        }
        this.loadRoles(_user);
    }.observes('selection'),

    loadRoles: function(user) {
        if (SC.none(this.possibleRoles)) {
            SC.Request.getUrl(Webadmin.context + '/resources/admin/roles/names').json().notify(this, 'didFetchRoleNames').send();
        }
        if (user) {
            var roles = user.get('roles');
            if (roles) {
                Webadmin.activeRolesController.set('content', roles);
            } else {
                Webadmin.activeRolesController.set('content', null);
            }
        } else {
            Webadmin.activeRolesController.set('content', null);
        }
    },

    didFetchRoleNames: function(response) {
        var roles = [];
        if (SC.ok(response)) {
            var body = response.get('body');
            if (body) {
                roles = body;
            }
        }
        this.set('possibleRoles', roles);
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
            ret = len === 1 ? "_users.1user".loc() : "_users.MultipleUsers".loc(len);
        } else ret = "_users.NoUser".loc();
        return ret;
    }.property('length').cacheable(),

    //make sure the active user is visible in the list
    scrollToSelection: function() {
        var _user = this.getSelectedUser();
        if (_user) {
            this.invokeLater(function() {
                var list = Webadmin.usersView.getPath('managerView.userScrollView.contentView');
                list.scrollToContentIndex(this.indexOf(_user));
            });
        }
    },

    //get the selected user in the list
    getSelectedUser: function() {
        var _sel = this.get('selection');
        if (_sel) {
            var _user = _sel.get('firstObject');
            return _user;
        }
        return NO;
    },

    addUser: function() {
        Webadmin.makeFirstResponder(Webadmin.NEW_USER);
    },

    deleteUser: function() {
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
        }
    },

    confirmDelete: function() {
        if (this.deleteRecords && this.deleteRecords.get('length') > 0) {
            var _mess = this.deleteRecords.get('firstObject').get('name');
            if (this.deleteRecords.get('length') > 1) {
                _mess = "_users.Multiple (%@)".loc(this.deleteRecords.get('length'));
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
                        value: "_users.ConfirmDeleteUser".loc(_mess),
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
                        target: "Webadmin.usersController"
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
                        target: "Webadmin.usersController.sheetPane"
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
