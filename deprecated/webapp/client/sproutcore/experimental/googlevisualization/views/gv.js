// ==========================================================================
/** @class

  (Document Your View Here)

  @extends SC.View
*/
Googlevisualization.GVView = SC.View.extend(
{
    didAppendToDocument: function() {
        this.initGV();
    },
    initGV: function() {
        // initialize the map
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Month');
        data.addColumn('number', 'Sales');
        data.addRows([['January', {
            v: 20,
            f: '$20M'
        }], ['February', {
            v: 31,
            f: '$31M'
        }], ['March', {
            v: 61,
            f: '$61M'
        }], ['April', {
            v: 26,
            f: '$26M'
        }]]);
        this.drawData(data);
    },
	drawData: function(data){
		// intelligently get the id of the map container
        var mapCanvasId = this.get("layerId");
		// Create and draw the visualization. 
        new google.visualization.PieChart(
        document.getElementById(mapCanvasId)).
        draw(data, {
            is3D: true
        });
	}
});
