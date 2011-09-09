// ==========================================================================
// Project:   Landanalysis.Note
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document your Model here)

  @extends Kloudgis.Feature
  @version 0.1
*/
sc_require('views/note');
Landanalysis.Note = Kloudgis.Feature.extend(
/** @scope Landanalysis.Note.prototype */
{

    title: SC.Record.attr(String),
    description: SC.Record.attr(String),

	featuretypeName: 'Note',
	controllerClassName: 'Landanalysis.noteController',
	
	inspectorViewClass: Landanalysis.NoteView,

    label: function() {
		var title = this.get('title');
        if(SC.empty(title)){
			return '_unknown'.loc();
		}
		return title;
    }.property('title').cacheable(),

    labelInfo: function() {
        return this.get('description');
    }.property('description'),

    labelInspector: function() {
        return this.get('label');
    }.property('label').cacheable()

});
