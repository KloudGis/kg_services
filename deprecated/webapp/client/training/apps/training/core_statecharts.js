SC.mixin(Training, {
    statechart: Ki.Statechart.create({
	
		//log trace
        trace: YES,

        initialState: 'state1',

        state1: Ki.State.design({

            initialSubstate: 'state11',

            enterState: function() {
                SC.Logger.debug('State1 ACTIVE');
                Training.set('buttonToOneEnabled', NO);
                Training.set('buttonToTwoEnabled', YES);
            },

            exitState: function() {},

            gotoState2: function(sender) {
                this.gotoState('state2');
            },

            state11: Ki.State.design({

                enterState: function() {
                    SC.Logger.warn('1-1');
                },

                exitState: function() {},

                gotoState12: function(sender) {
                    this.gotoState('state12');
                },

            }),
            state12: Ki.State.design({

                enterState: function() {
                    SC.Logger.warn('1-2');
                    alert('State 12');
                },

                exitState: function() {},

                gotoState2: function(sender) {
                    this.gotoState('state2');
                },

                gotoState11: function(sender) {
                    this.gotoState('state11');
                },
            }),
        }),

        state2: Ki.State.design({

            substatesAreConcurrent: YES,

            enterState: function() {
                Training.set('buttonToOneEnabled', YES);
                Training.set('buttonToTwoEnabled', NO);
            },

            exitState: function() {},

            gotoState1: function(sender) {
                SC.Logger.warn('click button2!!');
                this.gotoState('state1');
            },

            state21: Ki.State.design({

                enterState: function() {
                    SC.Logger.warn('2-1');
                },

                exitState: function() {},

                gotoState1: function(sender) {
                    SC.Logger.warn('2-1 click button2!!');
                },

            }),
            state22: Ki.State.design({

                enterState: function() {
                    SC.Logger.warn('2-2');
                },

                exitState: function() {},

                gotoState2: function(sender) {
                    SC.Logger.warn('2-2 click button2!!');
                },
            }),

            state23: Ki.State.design({

                enterState: function() {
                    SC.Logger.warn('2-3');
                },

                exitState: function() {},

                gotoState3: function() {
                    this.gotoState('state3');
                }

            }),
        }),
        //plugin example
        //define in a separate file. (state_state3.js)
        state3: Ki.State.plugin('Training.state3')
    })
});
