// ==========================================================================
// Project:   Kloudgis.ToolbarButtonView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
Kloudgis.PickerPane = SC.PickerPane.extend({

    classNames: 'gh-picker'.w(),

    wantsAcceleratedLayer: YES,

    init: function() {
        sc_super();
        Kloudgis.activeRoute.addObserver('currentPagePane', this, this.closePicker);
        if (SC.browser.chrome === 0 && (SC.browser.isSafari)) {
            //safari does not render properly scroll with a lot of items with this to YES.
            console.log('AcceleratedLayer is desactivated!');
            this.set('wantsAcceleratedLayer', NO);
        }
    },

    destroy: function() {
        sc_super();
        Kloudgis.activeRoute.removeObserver('currentPagePane', this, this.closePicker);
    },

    closePicker: function() {
        this.remove();
    },

    modalPaneDidClick: function(evt) {
        if (this.get('isModal')) {
            return sc_super();
        } else {
            return YES;
        }
    },

    render: function(context, firstTime) {
        if (context.needsContent) {
            context.push("<div class='top-header'></div>");
            //   context.push("<div class='bottom-header'></div>");
        }
        sc_super();
    },

});
