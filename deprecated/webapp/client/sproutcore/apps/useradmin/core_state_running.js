Useradmin.runningState = Ki.State.extend({

    initialSubstate: 'noUserSelectedState',

    //running state activated
    enterState: function() {
        Useradmin.getPath('mainPage.mainPane').append();
        Useradmin.loadUsers();
		Useradmin.loadCategories();
    },

    //running state deactivated
    exitState: function() {
        Useradmin.getPath('mainPage.mainPane').remove();
    },

    //no user has been selected
    noUserSelectedState: Ki.State.design({

        enterState: function() {},

        exitState: function() {},

        userSelected: function(sender, user) {
            SC.Logger.warn('User selected! ' + user.get('label'));
            this.gotoState('userSelectedState');
        }

    }),

    //there is a selected user
    userSelectedState: Ki.State.plugin('Useradmin.userSelectedState'),

    //The backend server failed: show a message
    //if the user hit Try Again: come back to app
    //if the user hit Quit: logout the app
    loadErrorState: Useradmin.abstractErrorServerState.design({
	
        tryAgain: function() {
            this.gotoState('reloadUsersState');
        },

        quit: function() {
            Useradmin.logout();
        }

    }),

	reloadUsersState: Ki.State.design({

        enterState: function() {
			Useradmin.loadUsers();
		},

        exitState: function() {},

		//events
        loadCompleted: function(sender) {
			//stop anim
        },

		loadError: function(){
			this.gotoState('runErrorServerState');
		}

    }),
	
	//Events

	//try again event from errorServerState
    serverTryAgain: function(sender) {
        this.gotoState('reloadUsersState');
    },

	flushAndReloadUsers: function(sender) {
        Useradmin.flushUsers();
		this.gotoState('reloadUsersState');
    },

    addUser: function(sender) {
        if (Useradmin.usersController.get('content').get('status') & SC.Record.BUSY) {
            SC.Logger.warn('cannot create user while record array is busy');
        } else {
            var u = Useradmin.store.createRecord(Useradmin.User, {
                email: ''
            });
            Useradmin.store.commitRecords();
            var self = this;
            u.addObserver('status', SC.Object.create({
                state: self,
                userStatusChanged: function(user) {
                    SC.Logger.log('Created user status changed: ' + user);
                    if (user.get('status') & SC.Record.READY) {
                        user.removeObserver('status', this, 'userStatusChanged');
                        Useradmin.statechart.sendEvent('reloadUsers', this, user);
                    } else if (user.get('status') & SC.Record.ERROR) {
                        SC.Logger.error('Create user error...');
                    }
                }
            }), 'userStatusChanged');
            Useradmin.usersController.selectObject(u);			
        }
    },

	toggleActive: function(user){
		if(SC.kindOf(user, Useradmin.User)){
			user.set('isActive', !user.get('isActive'));
			Useradmin.store.commitRecords();
		}
	}

});
