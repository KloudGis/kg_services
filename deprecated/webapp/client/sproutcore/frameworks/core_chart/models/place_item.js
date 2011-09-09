// ==========================================================================
// Project:   CoreChart.PlaceItem
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  Super class for PlaceItem features.

  @extends Kloudgis.Feature
  @version 0.1
*/
CoreChart.PlaceItem = Kloudgis.Feature.extend(
/** @scope Kloudgis.PlaceItem.prototype */
 {

    title: SC.Record.attr(String),
    description: SC.Record.attr(String),
    category: SC.Record.attr(String),
    subcategory: SC.Record.attr(String),

	controllerClassName: 'CoreChart.placeItemController',

    label: function() {
        var lbl = this.get('title');
		if(SC.empty(lbl)){
			return '_unknown'.loc();
		}
		return lbl;
    }.property('title'),

    labelInfo: function() {
        var desc = this.get('description');
		if(SC.empty(desc)){
			return '_unknown'.loc();
		}
    }.property('description'),

    labelInspector: function() {
		return this.get('label');
    }.property('label'),

    getChartInfo: function(chartName) {
        if (this.get('chartNames') && this.get('chartNames').indexOf(chartName) !== -1) {
            return CoreChart.ChartInfo.create({
                feature: this,
                chartName: chartName,
                chartRecordType: this.getChartRecordTypeFor(chartName)
            });
        }
    },

    //to override
    getChartRecordTypeFor: function(chartName) {
        return NO;
    },

    fetchChart: function(feature, chartName, callback, callbackMethod) {
        var ftName = feature.get('featuretypeName');
        if (ftName) {           
            var url = '%@/resources/protected/features/%@/%@/%@?locale=%@'.fmt(Kloudgis.get('context'), ftName, feature.get('id'), chartName, SC.Locale.currentLocale.get('language'));
            return SC.Request.getUrl(url).json().notify(this, 'didFetchChart', callback, callbackMethod).send();
        }
    },

    didFetchChart: function(response, callback, callbackMethod) {
        if (callback && callbackMethod) {
            if (SC.ok(response)) {
                if (Kloudgis.securityController.securityCheck(response)) {
                    var body = response.get('body');
                    if (body) {
						if(!SC.none(body.data)){
							callbackMethod.call(callback, YES, body.data, body.title);
						}else if(!SC.none(body.count)){
							callbackMethod.call(callback, YES, body.count);
						}
                    }
                }
            }else{
				callbackMethod.call(callback, NO);
			}
        }
    },

    //stream CHART features
    fetchChartQuery: function(sparseArray, range, requestLength) {
        var query = sparseArray.get('query');
        var count = requestLength === YES;
        var item = query.get('item');
        var type = query.get('chartType');
        var id = query.get('id');
        if (!SC.none(id)) {
            var url = '%@/resources/protected/features/%@/%@/%@?item=%@&start=%@&length=%@&count=%@'.fmt(Kloudgis.context, this.get('featuretypeName'), id, type, item, range.start, range.length, count);
            SC.Request.getUrl(url).json().notify(this, this.didFetchFeatures, sparseArray.get('store'), {
                array: sparseArray,
                start: range.start,
                length: range.length
            }).send();
            return YES;
        }
    }
});
