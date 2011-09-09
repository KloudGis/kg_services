// ==========================================================================
// Project:   Googlevisualization.chartController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Googlevisualization */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Googlevisualization.chartController = SC.ObjectController.create(
/** @scope Googlevisualization.chartController.prototype */
{

    // TODO: Add your own code here.
    loadChart: function() {
		console.log("loadChart");
        SC.Request.getUrl('/webserver/resources/protected/charts/status').json().notify(this, 'didFetchChart').send();
        
    },

	didFetchChart : function(response){
		var data = new google.visualization.DataTable();
		if (SC.ok(response)) {
            var body = response.get('body');
			if(body){
				data = new google.visualization.DataTable(body);
			}
		}
		Googlevisualization.mainPage.mainPane.gvView.drawData(data);
	}

	

});
