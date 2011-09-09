/** @namespace

  The active state when the add role dialog is showing.
  
  @extends SC.Responder
*/
Webadmin.ADD_ROLES = SC.Responder.create({

    _lastR: null,
    // when we become first responder, always show the create panel
    didBecomeFirstResponder: function() {

        console.log("ADD ROLES is First responder!");
        var active_roles = Webadmin.activeRolesController.get('content');
        var availableRoles = Webadmin.usersController.get('possibleRoles');
        var len;
        if (SC.none(active_roles)) {
            len = 0;
        } else {
            len = active_roles.get('length');
        }
        var notIn = [];
        availableRoles.forEach(function(role) {
            var i;
            for (i = 0; i < len; i++) {
                if (active_roles.objectAt(i).get('role_name') === role) {
                    return NO;
                }
            }
            notIn.pushObject(SC.Object.create({
                name: role
            }));
        });
        Webadmin.addRolesController.set('content', notIn); // for editing
        Webadmin.addRolesController.set('selection', null);
        // then show the dialog
        var pane = Webadmin.getPath('addRolesPage.mainPane');
        pane.append(); // show on screen
        pane.makeFirstResponder(pane.contentView.okbutton);
    },

    // when we lose first responder, always hide the signup panel.
    willLoseFirstResponder: function() {
        console.log("ADD ROLES loosing First responder!");
        // if we still have a store, then cancel first.
        Webadmin.addRolesController.set('content', null); // cleanup controller
        Webadmin.addRolesController.set('selection', null);
        Webadmin.getPath('addRolesPage.mainPane').remove();
    },

    // called when the OK button is pressed.
    addRoles: function() {
        var selection = Webadmin.addRolesController.get('selection');
        if (selection && selection.get('firstObject')) {
            var user = Webadmin.activeUserController.get('content');
            this._lastR = null;
			var aThis = this;
            selection.forEach(function(role) {
                aThis._lastR = Webadmin.store.createRecord(Webadmin.Role, {
                    role_name: role.name,
                    user_name: user.get('name')
                });
            });
            if (this._lastR) {
                this._lastR.addObserver('status', this, this.refreshUser);
                Webadmin.store.commitRecords();
            }
        }
        Webadmin.makeFirstResponder(Webadmin.READY_USER);
    },

    refreshUser: function() {
        if (this._lastR) {
			this._lastR.removeObserver("status", this, this.refreshUser);
			this._lastR = null;
            var user = Webadmin.activeUserController.get('content');
            if (user) {
                user.refresh();
                Webadmin.usersController.loadRoles(user);
            }
        }
    },

    // called when the Cancel button is pressed
    cancelAddRoles: function() {
        Webadmin.makeFirstResponder(Webadmin.READY_USER);
    }

});
