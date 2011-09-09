// ==========================================================================
// Project:   Kloudgis.InspectorView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Palette super panel for the inspector.  It float on top of the map.

  @extends SC.View
*/
Kloudgis.InspectorView = SC.PalettePane.extend(SC.Animatable,
/** @scope Kloudgis.InspectorView.prototype */
 {
    transitions: {
		left: {
            duration: .5,
			timing: SC.Animatable.TRANSITION_CSS_EASE_OUT
        },
        top: {
            duration: .5,
			timing: SC.Animatable.TRANSITION_CSS_EASE_OUT
        },
        width: {
            duration: .5,
            timing: SC.Animatable.TRANSITION_CSS_EASE_OUT
        },
        height: {
            duration: .5,
            timing: SC.Animatable.TRANSITION_CSS_EASE_OUT
        }
    },
    classNames: 'gh-picker inspector-view'.w(),
	isModal: NO,
	
	//various bug with google visualization if YES
	wantsAcceleratedLayer: NO,
	
	render: function(context, firstTime) {
		sc_super();
	    if (context.needsContent) {
	       context.push("<div class='top-header'></div>");
	    }
	 },
});

