// ==========================================================================
// Project:   Landanalysis.Road
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document your Model here)

  @CoreChart.PlaceItem
  @version 0.1
*/
sc_require('views/road')
Landanalysis.Road = CoreChart.PlaceItem.extend(
/** @scope Landanalysis.Road.prototype */ {

	featuretypeName: 'Road',
	inspectorViewClass: Landanalysis.RoadView,

	sparsearrayBlock: 100,

}) ;
