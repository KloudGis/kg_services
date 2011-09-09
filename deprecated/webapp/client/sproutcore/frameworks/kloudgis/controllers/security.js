// ==========================================================================
// Project:   Kloudgis.securityController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Wrap the user currently logged in

  @extends SC.Object
*/
Kloudgis.securityController = SC.ObjectController.create(
/** @scope Kloudgis.securityController.prototype */
{

    logged: NO,
	_picker: null,

    securityCheck: function(response, query) {
        if (SC.kindOf(response.get('body'), SC.Error)) {
            this.gotoWelcome();
            this.set('logged', NO);
            return NO;
        } else {           
            return YES;
        }
    },

    logout: function() {
        SC.Request.getUrl(Kloudgis.context + "/resources/unprotected/login/logout").json().notify(this, this.didLogout).send();
    },

    didLogout: function(response) {
        if (SC.ok(response)) {
            console.log('log out');			
            this.gotoWelcome();
			this.set('content', null);
        } else {
            console.log('error logging out');
        }
    },

    gotoWelcome: function() {
        if (SC.buildMode === 'debug') {
            window.location = 'http://localhost:8080' + Kloudgis.context + '/protected/welcome_dev.html?app=' + Kloudgis.context_client;
        } else {
            window.location.href = Kloudgis.context + '/protected/welcome.html?app=' + Kloudgis.context_client;
        }
    },

	updateLoggedUser: function(){
		SC.Request.getUrl(Kloudgis.context + "/resources/protected/login/user").json().notify(this, this.didUpdateLoggedUser).send();
	},
	
	didUpdateLoggedUser: function(response){
		if (SC.ok(response)) {
			var hash = response.get('body');
			var user = Kloudgis.UserDescriptor.create({});
			user.fullname = hash.fullName;
			user.name = hash.name; 
			this.set('content', user);
			this.set('logged', YES);			
		}else{
			this.set('content', null);
		}
	},

    showUserPicker: function(anchor) {      
        this.buildPicker();
        this._picker.popup(anchor, SC.PICKER_POINTER, [3, 0, 1, 2, 2]);
    },
	
	buildPicker: function() {
        this._picker = SC.PickerPane.create({
            layout: {
                width: 120,
                height: 30
            },
            contentView: SC.View.design({
                childViews: 'logout'.w(),              
				
				logout : SC.ButtonView.design({
					layout: {
                        left: 3,
                        right:3,
                        top: 3,
                        height: 24,
                    },
					title: '_logout'.loc(),
					action: function(){
						Kloudgis.securityController.logout();
					}
				})
            }),

			remove: function(){
				sc_super();
				this.destroy();
				Kloudgis.securityController._picker = null;
			}
        });
    }

});
