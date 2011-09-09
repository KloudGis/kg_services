// ==========================================================================
// Project:   Landanalysis.Arpenteur
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document your Model here)

  @extends Kloudgis.Feature
  @version 0.1
*/
sc_require('views/arpenteur');
Landanalysis.Arpenteur = Kloudgis.Feature.extend(
/** @scope Landanalysis.Arpenteur.prototype */
{

    firstname: SC.Record.attr(String),
    lastname: SC.Record.attr(String),
	postalcode: SC.Record.attr(String),

	featuretypeName: 'Arpenteur',
	inspectorViewClass: Landanalysis.ArpenteurView,

    label: function() {
        return "%@ %@".fmt(this.get('firstname'), this.get('lastname')) ;
    }.property('firstname', 'lastname').cacheable(),

    labelInfo: function() {
        return this.get('postalcode');
    }.property('description'),

    labelInspector: function() {
        return this.get('label');
    }.property('label').cacheable()

});
