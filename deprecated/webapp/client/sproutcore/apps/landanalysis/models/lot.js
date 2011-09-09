// ==========================================================================
// Project:   Landanalysis.Lot
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document your Model here)

  @CoreChart.PlaceItem
  @version 0.1
*/
sc_require('views/lot')
Landanalysis.Lot = CoreChart.PlaceItem.extend(
/** @scope Landanalysis.Lot.prototype */ {

	featuretypeName: 'Lot',
	inspectorViewClass: Landanalysis.LotView,

}) ;