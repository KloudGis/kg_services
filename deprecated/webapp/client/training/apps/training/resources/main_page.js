// ==========================================================================
// Project:   Training - mainPage
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Training */

// This page describes the main user interface for your application.  
Training.mainPage = SC.Page.design({

    // The main pane is made visible on screen as soon as your app is loaded.
    // Add childViews to this pane for views to display immediately on page 
    // load.
    mainPane: SC.MainPane.design({
        defaultResponder: Training.statechart,
        childViews: 'buttunView buttunView2 custom labelCurrentState'.w(),

        labelCurrentState: SC.LabelView.design({
            layout: {
                centerX: 0,
                top: 20,
                width: 800,
                height: 24
            },
            textAlign: SC.ALIGN_CENTER,
            tagName: "h1",
            value: '',
            currentStatesDidCreate: function(statechart) {
                this.set('value', Training.statechart.get('currentStates').toArray());
                //needed because the array is the same instance but contains other items.
                this.displayDidChange();
            }.observes('Training.statechart.currentStates')

        }),

        buttunView: SC.ButtonView.design({
            layout: {
                centerX: 0,
                centerY: 0,
                width: 200,
                height: 24
            },
            textAlign: SC.ALIGN_CENTER,
            tagName: "h1",
            title: "Button to 2",
            action: 'gotoState2',
            isEnabledBinding: 'Training.buttonToTwoEnabled'
        }),

        buttunView2: SC.ButtonView.design({
            layout: {
                centerX: 0,
                centerY: 100,
                width: 200,
                height: 24
            },
            textAlign: SC.ALIGN_CENTER,
            tagName: "h1",
            title: "Button to 1",
            action: 'gotoState1',
            isEnabledBinding: 'Training.buttonToOneEnabled'
        }),

        custom: SC.View.design({
            layout: {
                top: 0,
                left: 100,
                width: 500,
                height: 200
            },

            acceptsFirstResponder: YES,

            render: function(context, firstTime) {
                if (firstTime) {
                    context.push('<div id="myButton" role="button" class="appNavButton" style="width:200px; left 40px; height:30px; top:0px">', '<span id="myButtonSpan" class="inner-span">', '<label id="myButtonLabel" class="sc-button-label">Go to state3</label>', '</span>', '</div>', '<ul>', '<li class="search"><input id="s-id" type="text" value="" placeholder="Test" ></li>', '<li><label>type "home" to go to state1</label></li>', '<li><label>type "cc" to go to change align</label></li>', '<li>&nbsp;</li>', '<li id="log-in" class="login">Login</li>', '<li class="state11">Go to State11</li>', '<li class="state12">Go to State12</li>', '</ul>');
                }
                sc_super();
            },

            mouseDown: function(evt) {
                //console.log('MOUSE D');
                //console.log(evt);
                var target = evt.target;
                var id = target.id;
                //Core Query ?
                if (id === 's-id') {
                    this.becomeFirstResponder();
                } else if (id === 'myButton' || id === 'myButtonSpan' || id === 'myButtonLabel') {
                    console.log('Button pressed!');
                    Training.statechart.sendEvent('gotoState3');
                }
                var tarQ = SC.CoreQuery(evt.target);
                if (tarQ.attr('class') === 'state11') {
                    Training.statechart.sendEvent('gotoState11');
                } else if (tarQ.attr('class') === 'state12') {
                    Training.statechart.sendEvent('gotoState12');
                }
                return NO;
            },

            keyDown: function(evt) {
                //console.log('KEY');
                // console.log(evt);
                var target = SC.CoreQuery(evt.target);
                //  console.log(target);				
                if (target.attr('id') === 's-id' && evt.charCode == 13) {
                    //change the element text with class 'new' to toto
                    this.$('.new').text('toto');
                    //change input attribute to toto
                    this.$('input').attr('value', 'toto');
                    //change element with id log-in to toto2
                    this.$('#log-in').text('toto2');
                }
                return NO;
            },

            keyUp: function(evt) {
                var actualText = this.$('#s-id').attr('value');
                console.log('textfield value =' + actualText);
                if (actualText === 'css') {
                    this.$('.login').css('color', 'red');
                } else if (actualText === 'cc') {
                    this.$('.login').setClass('centered', YES); //add
                    this.$('.appNavButton .inner-span').setClass('move-right', YES);

                } else if (actualText === '') {
                    this.$('.login').setClass('centered', NO); //remove
                    this.$('.appNavButton .inner-span').setClass('move-right', NO);
                    //this.displayDidChange();
                } else if (actualText === 'home') {
                    Training.statechart.sendEvent('gotoState1');
                }
            }

        })
    })

});
