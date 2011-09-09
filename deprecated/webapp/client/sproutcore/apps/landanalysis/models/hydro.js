// ==========================================================================
// Project:   Landanalysis.Hydro
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document your Model here)

  @Kloudgis.CoreChart
  @version 0.1
*/
sc_require('views/hydro')
Landanalysis.Hydro = CoreChart.PlaceItem.extend(
/** @scope Landanalysis.Road.prototype */ {

	featuretypeName: 'Hydro',
	inspectorViewClass: Landanalysis.HydroView,

}) ;
