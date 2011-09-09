// ==========================================================================
// Project:   Kloudgis.CloseDialogButtonView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
sc_require('views/toolbar_button')
Kloudgis.CloseDialogButtonView = Kloudgis.ToolbarButtonView.extend({

    controlSize: SC.LARGE_CONTROL_SIZE,
    classNames: 'close-button'.w(),
	hasIcon: YES,
    icon: sc_static('images/buttons/closebox.png')
});
