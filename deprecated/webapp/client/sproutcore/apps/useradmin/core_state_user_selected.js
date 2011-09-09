
Useradmin.userSelectedState = Ki.State.extend({

    //details view and menu can show up at the same time
    substatesAreConcurrent: YES,

    enterState: function() {
		Useradmin.mainPage.get('userSelectedView').set('isVisible', YES);
	},

    exitState: function() {
		Useradmin.mainPage.get('userSelectedView').set('isVisible', NO);
	},

    detailsViewState: Ki.State.design({

        initialSubstate: 'showProfileState',

        enterState: function() {},

        exitState: function() {},

        showProfileState: Ki.State.design({
            enterState: function() {
                SC.Logger.warn('Show Profile!');
				Useradmin.categoryController.selectObject(Useradmin.categoryController.get('firstObject'));			
            },

            exitState: function() {
			},
			
			inputLooseFocus: function(sender, attribute){
				Useradmin.store.commitRecords();
			},
        }),

        showAdminState: Ki.State.design({
            enterState: function() {},

            exitState: function() {},
        }),

		categorySelected: function(sender, category) {
	        Useradmin.mainPage.get('detailsView').set('contentView', Useradmin.mainPage.get(category.get('detailView')));
	    },

	    categoryClearSelection: function(sender) {
	        Useradmin.mainPage.get('detailsView').set('contentView', undefined);
	    }
		
    }),

    optionsMenuStates: Ki.State.design({

        initialSubstate: 'hideMenuState',

        enterState: function() {
            SC.Logger.info('Option Menu Hidden!');
        },

        exitState: function() {},

        hideMenuState: Ki.State.design({
            enterState: function() {},

            exitState: function() {},
        }),

        showMenuState: Ki.State.design({
            enterState: function() {},

            exitState: function() {},
        }),

		tryToHandleEvent: function(eventName){
			if(eventName === 'hideMenuState' || eventName === 'showMenuState'){
				return sc_super();
			}else{
				return YES;//do not propagate the event to parent states.
			}
		}

    }),

    //events
    userSelected: function(sender, user) {
        SC.Logger.warn('An other user selected! ' + user.get('label'));
    },

    userClearSelection: function(sender) {
        this.gotoState('noUserSelectedState');
    },

    deleteUser: function(sender) {
        if (!SC.none(Useradmin.userSelectedController.get('content'))) {
            Useradmin.userSelectedController.get('content').destroy();
            Useradmin.store.commitRecords();
        }
    }
});
